#ifndef IPODAUTH_H_
#define IPODAUTH_H_

#include "global.h"
#include "iPod.h"

// Device ID as retrieved from the coProcessor
extern int deviceID;

/*
 * iPodDevAuthInit:
 * Call at start of code - retrieves device ID, version info
 * from coProcessor
 */
int iPodDevAuthInit();

/*
 * iPodDevAuth:
 * Call on receipt of a GetDevAuthenticationInfo
 */
void iPodDevAuth(streaming_chanend ciPod, int rettransid);

/*
 * iPodStartAuth:
 * Call when wanting to start accessory authentication process
 */
int iPodStartAuth(streaming_chanend ciPod);

/*
 * iPodAuthGetDevAuthenticationSignature:
 * Call on receipt of GetDevAuthenticationSignature
 */
int iPodAuthGetDevAuthenticationSignature(streaming_chanend ciPodHID, int challen, char chal[], int rettransid);

#endif /*IPODAUTH_H_*/
