#ifndef IPODIDPS_H_
#define IPODIDPS_H_

#include "global.h"
#include "iPod.h"

extern int transid;

/*
 * gettransid:
 * Call when requesting to send a new command
 * and a new transaction ID is needed
 */
int gettransid();

/*
 * iPodTryIDPS:
 * Call when attempting to authenticate using IDPS
 * Returns 1 is NACKd
 */
int iPodTryIDPS(streaming_chanend ciPod);

#endif /*IPODIDPS_H_*/
