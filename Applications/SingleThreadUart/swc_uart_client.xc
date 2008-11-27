 /****************************************************************
 *
 * swc_uart_client.xc
 *
 * The single thread uart software component
 * This client code drives the single thread server through a channel
 *
 */


#include "swc_uart_common.h"
#include <print.h>
#include "sw_comps_common.h"


//
// Send block of tx data to the uart
//
// The number of bytes accepted into the Tx fifo is returned in numBytes
// 
XMOS_RTN_t uart_Clnt_txd_block(chanend server, uchar data[], uint &numBytes) 
{
  uchar bytesAccepted;
  uint  byteToSend;
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
  
  if (numBytes <= FIFO_DEPTH)
  {
    master
    {
      server <: (unsigned char )REQUEST_TX_BLOCK;
      server <: (unsigned char )numBytes;  
      for (byteToSend = 0; byteToSend < numBytes ; byteToSend++)
      {
        server <: (unsigned char ) data [byteToSend];
      }
      server :> bytesAccepted;
      
    }
  } else
  {
    // invalid number of bytes requested
    bytesAccepted = 0;
    returnCode = XMOS_FAIL;
  }
  
  numBytes = (uint) bytesAccepted;
  return ( returnCode );
}


//
// Receive a block of rx data from the uart
//
// The number of bytes received from the Rx fifo is returned in numBytes
// 
XMOS_RTN_t uart_Clnt_rxd_block(chanend server, uchar data[], uint &numBytes) 
{
  uchar numRxBytes;
  uint  rxByteCount;
  XMOS_RTN_t returnCode = XMOS_SUCCESS;

  master
  {
    server <: (uchar) REQUEST_RX_ALL;
    server :> numRxBytes;
    numBytes = (uint) numRxBytes;  // convert from uchar

    // Check for error
    if (numBytes != RX_ERROR)
    {

      for (rxByteCount= 0; rxByteCount< numBytes ; rxByteCount++)
      {
        server :> data [rxByteCount];
      }
    } else // Error
    {
      // load error code into the data array
      server :> data [0];
      returnCode = XMOS_FAIL;
    }
  }
  return ( returnCode);
}

//
// Enable flow control on the uart
//
// Forward the return code from the server
// 
XMOS_RTN_t uart_Clnt_rts_cts_enable(chanend server)
{
  uchar returnCode;
  master
  {
    server <: (unsigned char )REQUEST_CONFIG;
    server <: (unsigned char )CONFIG_ENABLE_RTS_CTS;
    server :> returnCode;
  }
  return ( (XMOS_RTN_t) returnCode);
}

//
// Disable flow control on the uart
//
// Forward the return code from the server
// 
XMOS_RTN_t uart_Clnt_rts_cts_disable(chanend server)
{
  uchar returnCode;
  master
  {
    server <: (unsigned char )REQUEST_CONFIG;
    server <: (unsigned char )CONFIG_DISABLE_RTS_CTS;
    server :> returnCode;
  }
  return ( (XMOS_RTN_t) returnCode);
}

//
// Close down the uart
// Forward the return code from the server
//
// This frees up the channels and ports for reuse
// 
XMOS_RTN_t uart_Clnt_finish(chanend server) 
{
  uchar returnCode;
  master
  {
    server <: (unsigned char )REQUEST_CONFIG;
    server <: (unsigned char )CONFIG_FINISH;
    server :> returnCode;
  }
    
  return ( (XMOS_RTN_t) returnCode);
}

