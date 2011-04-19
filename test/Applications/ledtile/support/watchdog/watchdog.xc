/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include "xs1.h"
#include "watchdog.h"
#include "reset.h"
#include "ctrl.h"
#include "led.h"

#define CORECLOCKDIV 32


void watchDog(chanend c1, chanend c2, chanend c3)
{
  timer t;
  int time;
  int dog_kicked;
  
  // Clock down this core
  pswrite(6, CORECLOCKDIV);
  // Clock down switch
  sswrite(7, 3);
  // Reference clock is still running at 100Mhz, so we cannot sample it reliably
  // Set reference to XCore clock
  psset(2, 1);
  
  // Init dog
  dog_kicked = 0;
  
  // Wait on start
  t :> time;
  t when timerafter(time + WDOG_INITIAL_WAIT/CORECLOCKDIV) :> time;
  
  while (1)
  {
    select
    {
      case t when timerafter (time + WDOG_WAIT/CORECLOCKDIV) :> time:
        // Check dog    
        if (dog_kicked != 7)
        {
#ifdef RELEASE
          chipReset();
#else
          while (1);
#endif
        }
        dog_kicked = 0;
        break;
      case c1 :> int _:
        dog_kicked |= 1;
        break;
      case c2 :> int _:
        dog_kicked |= 2;
        break;
      case c3 :> int _:
        dog_kicked |= 4;
        break;
    }
  }
}