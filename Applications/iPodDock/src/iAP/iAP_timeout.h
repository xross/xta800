#ifndef IAP_TIMEOUT_H_
#define IAP_TIMEOUT_H_

#include "global.h"
#include "iAP.h"

/*
 * Timeout value for iAP commands
 * Currently 1000mS
 */
#define IAP_TIMEOUT (1500 * XS1_TIMER_KHZ)

/*
 * iAP_timeout:
 * Run every time interval by USB stack
 * Checks for any timeouts on the iAP protocol
 */
int iAP_timeout(streaming_chanend cCtrl, int timenow);

#endif /*IAP_TIMEOUT_H_*/
