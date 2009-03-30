/****************************************************************
 *
 * UartTransmit.xc
 *
 * Uart transmit
 *
 */

#include <xs1_reva.h>
#include "Uart.h"

/* This function waits for the specified time.
 * 
 * delay - the number of clock ticks to delay
 * 
 */
static void wait(unsigned delay)
{
  unsigned s;
  timer tmr;

  // Input the current time from the timer
  tmr :> s;

  // Input from the timer with a condition on the timing to only
  // do so after the specified delay
  tmr when timerafter(s + delay) :> unsigned t ;
}

/* This function transmits a number of bytes using the Uart protocol 
 * 
 * txd - the port to transmit on
 * bytes - the array of bytes to transmit
 * numBytes - the number of bytes to transmit 
 * 
 */
void uart_transmit(out port txd, char bytes[], int numBytes)
{
  unsigned time;

  for (int i = 0; i < numBytes; i += 1)
  {
    int byte = bytes[i];
    
    // Start bit
    #pragma xta endpoint "transmit_start"
    txd <: 0 @ time;

    // Data bits
    for (int j = 0; j < 8; j += 1)
    {
      time += BIT_LENGTH;
      #pragma xta endpoint "transmit_data"
      txd @ time <: >> byte;
    }

    // Stop bit
    time += BIT_LENGTH;
    #pragma xta endpoint "transmit_stop1"
    txd @ time <: 1;

    time += STOP_BIT_LENGTH;  
    #pragma xta endpoint "transmit_stop2"
    txd @ time <: 1;   
  }
}

void uart_transmit_init(out port txd) 
{
  /* Set pin output to 1 initially to indicate no transmission */
  txd <: 1;
}

