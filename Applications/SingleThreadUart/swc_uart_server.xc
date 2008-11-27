/****************************************************************
 *
 * swc_uart_server.xc
 *
 * The single thread uart software component
 * This code is the code that is run on the single thread server
 *
 */

//

#include <xs1_reva.h>
#include <print.h>
#include "swc_uart_common.h"
#include "sw_comps_common.h"

#define CLOCK_BASE_FREQ 100000000                        // 100MHz
#define BIT_DELAY       CLOCK_BASE_FREQ /BAUD_RATE       // The TXD bit delay
#define X8_TIMER_DELAY  CLOCK_BASE_FREQ /(8* BAUD_RATE ) // Sample RXD at this rate

#define MAX_CLIENT_REQUEST_TIME   80      // max time to service a client request
                                          // This is in 100MHz units
                                          // Note x8 clock @38400 is 325
#define MAX_CLIENT_REQUEST_TIME_MARGIN 20 // Do not give the client a chance to be serviced,
                                          // unless it has this much margin

#define RXD_PORT_BIT_MASK 0x1  // rxd and cts share a port.
#define CTS_PORT_BIT_MASK 0x2  // these are masks for these inputs.

struct txdState_t
{
  uint shiftData ;      // Contains the tx data to shift out including start and stop bits
  uint x8ShiftCount;    // set to (1+8+1)*8 +1 =81 to initiate transfer
                        // Decremented every x8 clock
                        // when x8ShiftCount /8 == 0 shift another bit out
                        // when == 1, the next char can be loaded.
  uint fifoSize;
  uint fifoHead;
  uint fifoTail;
  uint fifoData[FIFO_DEPTH];
};

// Shift out TxData onto port
void x8TxdService (  struct txdState_t &txdState,
                     out port           txdPort)
// This function is called every x8 clock, but the output is only updated every 8 x8 clocks
// And only then when there is valid tx data to shift out                     
{
  if (txdState.x8ShiftCount != 0)
  {
    txdState.x8ShiftCount --;
    if ((txdState.x8ShiftCount & 0x7) == 0) // shift next bit out
                                            // Note this will shift out when x8ShiftCount == 0
                                            // So 2 stop bits are needed in shiftData
    {
      txdPort <: txdState.shiftData;
      txdState.shiftData = txdState.shiftData >> 1;
      // txdPort <: >> txdState.shiftData; 
    }
  }
}

//////////////////////////////////////////////////////////////////////////////

struct rxdState_t
{
  uint  x8ShiftCount;     // 0 implies waiting for a start bit edge
                          // incremented on each x8 clock
                          // %8 = 3,4,5 Sample here
                          // = 5 start bit final sample
                          // = 77 stop bit final sample
                          // = 199 waiting for stop bit before we can detect the start edge
  uint  sample3;          // x8 clock 3 sample
  uint  sample4;          // x8 clock 4 sample

  uint  rxShiftData ;     // Contains the rx data shifted along as it is Rxed
                          // Pass the rxd to the RxFifo
  uint missedClientCount; // Number of consecutive x8 clocks that client
                          // requests have not been serviced.
                          // This indicates that the bit rate is too high

  // Temp store rxd char in rxBufData before it is placed in FIFO.
  // This allows allow next char to be received, and fifo to be serviced during non critical time
  uint rxBufValid;         // Indicate that rxBufData & rxBufFramingError are valid
  uint rxBufData ;         // Contains the tx data to shift out including start and stop bits
  uint rxBufFramingError;  // Stop bit detected as a '0'

  uint fifoData[FIFO_DEPTH];
  uint fifoSize;
  uint fifoHead;
  uint fifoTail;
  uint fifoErrorCode;     // 0 is no error

};

// Txd is serviced each x8 clock to see if the next bit can be scheduled
void x8RxdService (  struct rxdState_t   &rxdState,
                     uint rxdSample)   // sample of the rxd Port
{

  switch (rxdState.x8ShiftCount)
  {
    case 0:  // Waiting for a start bit
      if (rxdSample == START_BIT )
      {
        rxdState.x8ShiftCount++;
        rxdState.rxShiftData = 0;
      }
      break;
    case X8_WAITING_FOR_STOP :
      if (rxdSample == STOP_BIT )
        rxdState.x8ShiftCount = 0; // Stop bit detected, now wait for start bit
      break;

    case 5 :       // Final sample of start bit
      rxdState.x8ShiftCount ++;    // move onto next sample if start bit is valid
      // Check start bit is valid
      if ((rxdState.sample3 + rxdState.sample4 + rxdSample) > 1) // Vote on sample
      {
        // Look for a start bit again because this one wasn't valid
        // Final sample may have been 0, so wait for a stop first
        rxdState.x8ShiftCount = X8_WAITING_FOR_STOP;
      }
      break;

    case (9*8)+5 : // Final sample of stop bit
      if ((rxdState.sample3 + rxdState.sample4 + rxdSample) > 1)  // Vote on sample
      // stop bit valid
      {
        rxdState.rxBufValid = TRUE;
        rxdState.rxBufData = rxdState.rxShiftData;
        rxdState.rxBufFramingError = FALSE;
        rxdState.x8ShiftCount = 0 ; // look for the next start bit
      } else
      {
        rxdState.rxBufValid = TRUE;
        rxdState.rxBufFramingError = TRUE;
        rxdState.x8ShiftCount = X8_WAITING_FOR_STOP ; // wait for a stop bit      }
      }
      break;

    default: //
      switch (rxdState.x8ShiftCount & 0x07) // split up by sample number
      {
        case 3:
          rxdState.sample3 = rxdSample;
          break;
        case 4:
          rxdState.sample4 = rxdSample;
          break;
        case 5:
          // shift rx bit
          rxdState.rxShiftData = (rxdState.rxShiftData >> 1);
          if ((rxdState.sample3 + rxdState.sample4 + rxdSample) > 1)  //Shift in a '1' for 2 or 3 high samples
            rxdState.rxShiftData = rxdState.rxShiftData + 0x80;
          break;
        default:
          break;
      }
      rxdState.x8ShiftCount ++;
      break;
  }
}



//////////////////////////////////////////////////////////////////////////////
transaction clientRequest( chanend client,
                           struct txdState_t   &txdState,
                           struct rxdState_t   &rxdState,
                           uint &rtsCtsFlowControlEnabled,
                           uint &active)
{

  uchar  request;
  uchar  numBytes;
  uint   byteCount;
  uchar  txData;   // temp store for data before placing in fifo
  uint   txBytesAccepted; // The client can send more bytes than the FIFO can accept.
                         // Record the number accepted, and return to the client.
  XMOS_RTN_t returnCode;
  uchar      configCode;

  client :> request;
  switch (request)
  {
    case REQUEST_TX_BLOCK:
      client :> numBytes;
      txBytesAccepted = 0;
      for (byteCount = 0; byteCount < numBytes ; byteCount++)
      {
        client :> txData;  // Always input the data from the channel even when there is no room in the fifo
        if (txdState.fifoSize != FIFO_DEPTH)  // Still space in the FIFO
        {
          txdState.fifoData[txdState.fifoHead] = (uint) txData;
          txdState.fifoHead = (txdState.fifoHead +1) & FIFO_DEPTH_MASK; // i.e. % FIFO_DEPTH;
          txdState.fifoSize++;
          txBytesAccepted ++;
        }
      }
      client <: (uchar)txBytesAccepted ;
      break;

    case REQUEST_RX_ALL:
      if (rxdState.fifoErrorCode == NO_ERROR) // Check for rx errors in the fifo
      {
        // Send the number of rx chars first
        client <: (uchar) rxdState.fifoSize ;

        // Send whole rx fifo to client
        for (byteCount = 0; byteCount < rxdState.fifoSize; byteCount++)
        {
          client <: (uchar) rxdState.fifoData [rxdState.fifoTail];
          rxdState.fifoTail = (rxdState.fifoTail +1) & FIFO_DEPTH_MASK; // i.e. % FIFO_DEPTH;
        }
        rxdState.fifoSize = 0;
      }
      else  // Return an error code instead
      {
        // indicate error and give error code
        client <: (uchar) RX_ERROR;
        client <: (uchar) rxdState.fifoErrorCode ;

        // Clear fifo
        rxdState.fifoHead = 0;
        rxdState.fifoTail = 0;
        rxdState.fifoSize = 0;
        rxdState.fifoErrorCode = NO_ERROR;
      }

      break;

    case REQUEST_CONFIG:
      returnCode = XMOS_SUCCESS;
      client :> configCode;
      switch (configCode)
      {
        case CONFIG_ENABLE_RTS_CTS:
          rtsCtsFlowControlEnabled = TRUE;
          break;

        case CONFIG_DISABLE_RTS_CTS:
          rtsCtsFlowControlEnabled = FALSE;
          break;

        case CONFIG_FINISH:
          active = FALSE;
          break;

        default:
          // unrecognised config
          returnCode = XMOS_FAIL;
          break;

      }
      client <: (uchar) returnCode;
      break;

    default:
      // error condition
      break;
  }
}

//////////////////////////////////////////////////////////////////////////////

// Take tx data from the fifos and initiate tx
// Take rxd and put into rx fifo
void serviceFifos(  struct txdState_t   &txdState,
                    struct rxdState_t   &rxdState,
                    uint                &rtsCtsFlowControlEnabled,
                    out port             rtsPort,
                    uint                 ctsSample)
{
  //////////////////
  // Service tx fifo
  if (
       ( txdState.fifoSize != 0) &&  // There is something in the tx fifo
       ( txdState.x8ShiftCount < 2) &&    // previous tx is either just finishing (1) or already finished (0)
       (
         ( rtsCtsFlowControlEnabled == FALSE) || // Not checking flow control
         ( ctsSample == 0 )                      // cts active (low)
       )
     )
  {
    // Initiate next tx char
    txdState.x8ShiftCount = ((1+8+1)*8)+1;  // Start 8 Stop, +1 so start bit emerges next x8 clock
    // need a second stop bit because it will be shifted out when x8ShiftCount == 0
    txdState.shiftData = START_BIT + ( txdState.fifoData[txdState.fifoTail] << 1) + (STOP_BIT << 9) + (STOP_BIT << 10) ;

    // update the fifo
    txdState.fifoTail = (txdState.fifoTail +1) & FIFO_DEPTH_MASK; // i.e. % FIFO_DEPTH;
    txdState.fifoSize--;
  }

  //////////////////
  // Service rx fifo
  if (rxdState.rxBufValid)
  {
    if ( rxdState.fifoSize == FIFO_DEPTH) // fifo is full
    {
      // Underflow, only record first error though
      if (rxdState.fifoErrorCode == NO_ERROR)
        rxdState.fifoErrorCode = UNDERFLOW_ERROR;
    } 
    else
    {
      // There is space in the fifo
      if (rxdState.rxBufFramingError == TRUE)
      {
        // Framing error, only record first error though
        if (rxdState.fifoErrorCode == NO_ERROR)  
          rxdState.fifoErrorCode = FRAMING_ERROR;
      } else
      {
        // No underflow and no framing error so save rx data in the fifo
        rxdState.fifoData[rxdState.fifoHead] = rxdState.rxBufData;
        rxdState.fifoHead = (rxdState.fifoHead +1) & FIFO_DEPTH_MASK; // i.e. % FIFO_DEPTH;
        rxdState.fifoSize++;
      }
    }
    rxdState.rxBufValid = FALSE;  // Record that this rx char has been put into the fifo
  }

  //////////////////
  // control RTS (RTR)
  if (rtsCtsFlowControlEnabled == TRUE)
  {
    if (rxdState.fifoSize >= FIFO_DEPTH - FIFO_NEGATE_RTS_SPACE)
    {
      rtsPort <: 1;   // Negate rts (rtr) - active low
    }
    else
    {
      rtsPort <: 0;   // Assert rts (rtr) - active low
    }
  }

}

//////////////////////////////////////////////////////////////////////////////


void swc_uart_server( out port txdPort,    // 1 bit output port
                      out port rtsPort,    // 1 bit output port
                      in  port rxdCtsPort, // 4 bit input port rxd = bit 0, cts = bit 1
                      chanend  client
                    )
{
  timer tmr;

  struct txdState_t txdState;
  struct rxdState_t rxdState;

  unsigned int  nextX8Time;            // Time to take next x8 sample
  unsigned int  actualX8Time;          // Used during debug to detect timing failures
  uint active = TRUE;                  // Set to FALSE to close uart
  uint rtsCtsFlowControlEnabled = FALSE;

  uint rxdSample;  // Sample of the rxd port pin == 0 for rxd==0, !=0 for rxd==1
  uint ctsSample;  // Sample of the cts port pin == 0 for cts==0, !=0 for cts==1

  uint now;        // timer sample of current time

  // Initialise the txd_state
  txdState.x8ShiftCount = 0; // show there is no tx data
  txdState.fifoHead = 0;     // empty fifo
  txdState.fifoTail = 0;
  txdState.fifoSize = 0;

  rxdState.x8ShiftCount = X8_WAITING_FOR_STOP; // wait for stop bit first
  rxdState.rxBufValid = FALSE;         // no rx data yet
  rxdState.fifoHead = 0;               // empty fifo
  rxdState.fifoTail = 0;
  rxdState.fifoSize = 0;
  rxdState.fifoErrorCode = NO_ERROR;   // with no errors
  rxdState.missedClientCount = 0;      // No missed client services

  // Drive outputs with default values
  txdPort       <: STOP_BIT;
  rtsPort       <: 0;        // rts (actually RTR - ready to recieve), active low

  // Read the current time to act as a baseline for the x8 timer
  tmr :> nextX8Time;
  // Compute the next
  nextX8Time = nextX8Time + X8_TIMER_DELAY;
  
  while (active)    // active is made FALSE by a finish request
  {
    // wait for the x8 timer
    tmr when timerafter(nextX8Time) :> actualX8Time;
    
    rxdCtsPort :> ctsSample; // sample rxd immediately but process after x8TxdService
                             // This avoids jitter on both txd and rxd 
    rxdSample = ctsSample & RXD_PORT_BIT_MASK; // for rxd mask off the cts signal
    ctsSample = ctsSample & CTS_PORT_BIT_MASK; // for cts mask off the rxd signal

    x8TxdService ( txdState, txdPort);   // Process Tx short function
    x8RxdService ( rxdState, rxdSample);
    
    // Allow further processing only when not the fifth RXD sample
    // This is because this is the cycle with most rxd processing

    if ((rxdState.x8ShiftCount & 0x07) != 0x06 ) // i.e. %8 == 5+1 (fifth sample, but x8ShiftCount has been incremented
    {
     serviceFifos (txdState, rxdState, rtsCtsFlowControlEnabled, rtsPort, ctsSample);
    }

    // allow the client opportunity to request service if there is enough time
    nextX8Time += X8_TIMER_DELAY;
    tmr :> now;
    if ((now - (nextX8Time - MAX_CLIENT_REQUEST_TIME - MAX_CLIENT_REQUEST_TIME_MARGIN)) & 0x80000000)
    {
      // We have enough time to service the client
      rxdState.missedClientCount = 0;   // reset timing error check counter
      select
      {
        case clientRequest (client, txdState, rxdState, rtsCtsFlowControlEnabled, active):
          break;
        case tmr when timerafter (nextX8Time - MAX_CLIENT_REQUEST_TIME ) :> now : // int temp :
          // timeout when there isn't enough time to service a client request before the next x8 tick
          break;
      }
    }
    else
    {
      // not enough time to service client
      rxdState.missedClientCount++;
      if (rxdState.missedClientCount == MISSED_CLIENT_THRESHOLD)
        // This error always takes priority
        rxdState.fifoErrorCode = TIMING_ERROR;
    }

  } // while(active)
}


