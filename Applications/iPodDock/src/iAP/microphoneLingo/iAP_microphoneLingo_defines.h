#ifndef __IAP_microphoneLingo_DEFINES_H__
#define __IAP_microphoneLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  microphoneLingo_ACK = 4,
  microphoneLingo_RetDevCaps = 0x08,
  microphoneLingo_RetDevCtrl = 0x0A,
  

} e_microphoneLingo_commands;


#endif //__IAP_microphoneLingo_DEFINES_H__
