#ifndef DEVICE_H_
#define DEVICE_H_

#include "usb_descriptors.h"

/* 
 * This header describes the expected device Interface/Endpoint
 * arrangement and the functions for dealing with receiving/sending 
 * packets to/from the various endpoints
 */

extern int dummyfn(int datalen, unsigned char data[]);

#if defined IPOD
#include "device_iPod.h"
#else
#include "device_test.h"
#endif

#endif /*DEVICE_H_*/

