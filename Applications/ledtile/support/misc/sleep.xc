/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>

void sleep(int timerticks)
{
  timer t;
  int now;
  
  t :> now;
  t when timerafter(now + timerticks) :> now;
  
}

