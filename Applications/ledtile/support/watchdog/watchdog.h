/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#define SWITCH_WAIT             100000
#define SERV_WAIT               100000
#define LED_WAIT                100000
#define WDOG_WAIT           4000000000 // 10 second timeout
#define WDOG_INITIAL_WAIT    100000000

#ifndef WATCHDOG_H_
#define WATCHDOG_H_

void watchDog(chanend c1, chanend c2, chanend c3);

#endif /*WATCHDOG_H_*/
