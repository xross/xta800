#ifndef __IAP_HAL_SENDPACKET_H__

#define __IAP_HAL_SENDPACKET_H__

// Allow use of 'chanend' in STDC
#include <xccompat.h>

#include "iAP_defines.h"

int iAP_sendpacket ( unsigned char lingoID, unsigned char commandID, unsigned datalength, unsigned char commandData[], int transid);

#endif  //__IAP_HAL_SENDPACKET_H__
