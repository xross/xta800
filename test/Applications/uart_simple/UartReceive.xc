#include <xs1.h>
#include "Uart.h"

/* This function receives a number of bytes using the Uart protocol 
 * 
 * txr - the port to receive on
 * bytes - the array of bytes to put the received bytes in
 * numBytes - the number of bytes to receive
 * 
 */
void uart_receive(in port txr, char bytes[], int numBytes)
{
  for (int i = 0; i < numBytes; i += 1)
  {
    unsigned time;             // Keeps track of the previous port event time
    unsigned startBitTime;     // The time at the beginning of the start bit
    int input_val = 0;         // The current value being inputted.
    int got_start_bit = FALSE; // Whether the start bit has been received

    // Wait for start bit of the required length
    #pragma xta label "wait_for_start_bit"
    while (!got_start_bit) {
      
      #pragma xta label "start_bit_input"
      txr when pinseq(0) :> int x @ startBitTime;

      /* Now wait until we get 0 for half of BIT_TIME or we revert to a 1 on the port */

      got_start_bit = TRUE;
      while (got_start_bit && time < (startBitTime + BIT_LENGTH/2)) {
        int x;
       
        #pragma xta endpoint "start_bit_sample"
        txr :> x @ time;

        if (x==1) {
          #pragma xta label "start_bit_error"
          got_start_bit = FALSE;
        }
      }
    }
      
    // Data bits
    for (int j = 0; j < 8; j += 1)
    {
      time += BIT_LENGTH;
        
      #pragma xta endpoint "recieve_data"
      txr @ time :> >> input_val;
    }

    // The input will by in the high byte of input_val 
    // so we need to shift it down into the low byte
    #pragma xta label "recieve_complete" 
    bytes[i] = (unsigned char) (input_val >> 24);
  }
}
