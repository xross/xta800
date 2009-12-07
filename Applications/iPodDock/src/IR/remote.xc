
#include <xs1.h>
#include "global.h"
#include "remote_cmds.h"
#include "remote.h"

int waitForPort(in port p, int bit, int targetval)
{
  for (int i=0; i<4; i++)
  {
    int portval;
    int retrycount = 1 << 20;
    do
    {
      p :> portval;
      portval = (portval >> bit) & 1;
      retrycount--;
    } while (targetval != portval && retrycount);
    if (retrycount == 0)
      return 1;
  }
  return 0;
}


// Define the times for the different pulses
#define ERROR_LEN		50000		// timing inaccuracy		= 0.50ms
#define START_LEN		900000		// start pulse 			= 9 ms
#define START_PAUSE		450000		// start pause 			= 4.5 ms
#define REPEAT_PAUSE 		225000		// repeat pause 		= 2.25 ms
#define REPEAT_PERIOD_TIMEOUT	15000000	// timeout of repeat pulses	= 150 ms
#define ZERO_LEN  		112000		// length of entire pulse for 0 = 1.12 ms
#define ONE_LEN			225000		// length of entire pulse for 1 = 2.25 ms

int ir_receive(streaming chanend cOut, in port pIR, int bitoffset, timer t)
{
  unsigned  t0, ts, ts1, val, i; // times[32];
  
  if (waitForPort(pIR, bitoffset, 0))
    return -2;
  t :> t0;
  if (waitForPort(pIR, bitoffset, 1))
    return -3;
  t :> ts;

  // Check for start of message
  if ( ( ts - t0 > (START_LEN - ERROR_LEN) ) && ( ts - t0 < (START_LEN + ERROR_LEN) ) )
  {   
    if (waitForPort(pIR, bitoffset, 0))
      return -4;
    t :> ts1;

    // Check for repeat code
    if (((ts1 - ts > (REPEAT_PAUSE - ERROR_LEN)) && (ts1 - ts < (REPEAT_PAUSE + ERROR_LEN))))
    {
      return -1;
    }
    else if ((ts1 - ts > (START_PAUSE - ERROR_LEN)) && (ts1 - ts < (START_PAUSE + ERROR_LEN)) )
    {
      // Reset loop variables code
      val = 0;

      // Get all 32 bit from the IR receiver    
      for ( i = 0; i < 32; i++ )
      {
        if (waitForPort(pIR, bitoffset, 1))
          return -5;
        if (waitForPort(pIR, bitoffset, 0))
          return -6;
        t :> ts;

        // Test whether it fits the timing of a valid bit
        if ((ts - ts1 > (ZERO_LEN - ERROR_LEN)) && (ts - ts1 < (ZERO_LEN + ERROR_LEN)) )
        {
          // It's a zero.
          val <<= 1;
        }
        else if ((ts - ts1 > (ONE_LEN - ERROR_LEN)) && (ts - ts1 < (ONE_LEN + ERROR_LEN)) )
        {
          // It's a one.
          val <<= 1;
          val += 1;
        }
        else
        {                       
          vprintstrln("IR timing error");
          vprintintln(ts-ts1);    
          vprintintln(i);
          return -7;
        }

        // For debug - store the times for each bit
        // times[i] = ts - ts1;

        // Copy over the timing value for the next loop
        ts1 = ts;
      }        

      // If it was a valid IR command
      {
        // For debug, print the times of each bit.
        //printhexln(val);
        //for ( i = 0; i < 32; i++ )
        //{
        //  printuintln( times[i] );
        //} 


        if ( ( val & 0xFFFF0000 ) == 0x77E10000 )
        {
          int remotecmd = (val >> 8) & 0xFF;
          
          switch (remotecmd)
          {
          case 0x20:
          case 0xA0:
            return remotePlayPause;
          case 0x10:
          case 0x90:
            return remoteTrackPrev;
          case 0xE0:
          case 0x60:
            return remoteTrackNext;
          case 0xD0:
          case 0x50:
            return remoteVolumeUp;
          case 0xB0:
          case 0x30:
            return remoteVolumeDown;
          case 0x40:
          case 0xC0:
            return remoteVolumeDown;
          default:
            return -10;            
          }
        }
        else if ( ( val & 0xFFFF0000 ) == 0x00FF0000 )
        {
          // NEC Protocol

          // Make sure the checksum is ok
          if ( ( ~val & 0x000000FF ) == ( ( val & 0x0000FF00 ) >> 8 ) )
          {
            // Old iPod Dock Remote
            if ( ( val & 0x000FF00 ) == 0x0008800 )
            {
              return remotePlayPause;
            }
            else if ( ( val & 0x000FF00 ) == 0x0000000 )
            {
              return remoteVolumeUp;
            }
            else if ( ( val & 0x000FF00 ) == 0x0001000 )
            {
              return remoteVolumeDown;
            }
            else if ( ( val & 0x000FF00 ) == 0x0004000 )
            {
              return remoteTrackNext;
            }
            else if ( ( val & 0x000FF00 ) == 0x0005000 )
            {
              return remoteTrackPrev;
            }
            else
            {
              vprintstrln("NEC remote command fail");
            }
          }
        }
        else
        {
          vprintstrln("Protocol error");
        }

        return -8;
      }
    }
  }
  else
  {
  }
  return -9;
}
