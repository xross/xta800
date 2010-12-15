/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>

#define BUFFERBITS 9
#define BUFFERSIZE (1 << BUFFERBITS)
#define BUFFERMASK (BUFFERSIZE - 1)

void pktbuffer(chanend cIn, chanend cOut)
{
  unsigned int temp;
  unsigned int buffer[BUFFERSIZE];
  unsigned int rptr = 0;
  unsigned int wptr = 0;
  set_thread_fast_mode_on();
  start_streaming_master(cOut);
  
  // Buffer commands for the driver to interpret
  while (1)
  {
#pragma ordered
    select
    {
      // New packet from source
      case slave {
        cIn :> temp;
        {
    	  // Test if FIFO has enough space to include this packet
          unsigned int tempwptr = wptr + temp + 2;
          if (tempwptr > BUFFERSIZE && tempwptr - BUFFERSIZE >= rptr)
          {
        	  // Nack
            cIn <: (int)1;
          }
          else if (wptr < rptr && tempwptr >= rptr)
          {
            // Nack
            cIn <: (int)1;
          }
          else
          {
        	  // Ack
            cIn <: (int)0;
            // Store packet size in FIFO
            buffer[wptr++] = temp;
            wptr &= BUFFERMASK;
            while (temp)
            {
              temp--;
              cIn :> buffer[wptr++];
              wptr &= BUFFERMASK;
            }
          }
        }
      }:
      break;
      
      // Packet request from sink
      case inuint_byref(cOut, temp):
    	  // Check if FIFO is empty
        if (wptr == rptr)
        {
          // Nack
          outuint(cOut, 1);
        }
        else
        {
          // Retrieve packet size from buffer
          unsigned int num = buffer[rptr++];
          rptr &= BUFFERMASK;
          // Ack
          outuint(cOut, 0);
          // Send packet size
          outuint(cOut, num);
          while (num)
          {
          	num--;
            outuint(cOut, buffer[rptr++]);
            rptr &= BUFFERMASK;
          }
          outct(cOut, XS1_CT_PAUSE);
        }
      break;
    }
  }
}