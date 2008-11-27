/****************************************************************
 *
 * uart_example.xc
 *
 * Instantiates and uses a uart
 *
 */


#include <print.h>
#include <xs1_reva.h>
#include "swc_uart_server.h"
#include "swc_uart_client.h"
#include "swc_uart_common.h"
#include "sw_comps_common.h"

#define TEST_CHARS 4

// Define the ports to be used
out port txdPort    = XS1_PORT_1B;
out port rtsPort    = XS1_PORT_1D;
in  port rxdCtsPort = XS1_PORT_4A;

void uartTest (chanend uartChan)
{
  timer tmr; 
  int   time;
  int   i;
  uchar data[FIFO_DEPTH];
  uint  numBytes;
  
  // First disable flow control (this is the default state)
  uart_Clnt_rts_cts_disable(uartChan);
  
  // Put tx data into array
  for (i=0; i < TEST_CHARS; i++)
  {
    data[i] = i;
  }
  
  // Number of chars to Tx
  numBytes = TEST_CHARS;
  
  // send Tx Data
  uart_Clnt_txd_block(uartChan, data, numBytes);
  // assume that all tx chars were accepted
  
  // wait until after the tx data has been sent
  tmr :> time;
  time += ((100000000 / BAUD_RATE) * TEST_CHARS * 10) + 1000;
  tmr when timerafter (time) :> uint ignore;
  
  // An external loopback should have caused all the tx data to have been received now
  uart_Clnt_rxd_block(uartChan, data, numBytes);
  
  printstr("Number of Recieved Chars: ");
  printintln(numBytes);

  for (i=0; i < numBytes; i++)
  {
    printintln(data[i]);
  }
  
  // Close uart
  uart_Clnt_finish(uartChan);
    
}


int main()
{
  // Use a channel to communicate with the UART
  chan uartChan;    // Connects to the uart which is being tested

  // Drive stop bit level onto txd to indicate that it is idle
  txdPort       <: STOP_BIT;

  par
  {
    // Create a uart which should be looped back in hardware
    swc_uart_server(txdPort, rtsPort, rxdCtsPort, uartChan);
    // send and receive data using the uart
    uartTest (uartChan);
  }

  return 0;
}


