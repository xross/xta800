#ifndef __IAP_audioLingo_DEFINES_H__
#define __IAP_audioLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  audioLingo_ACK = 0x01,
  audioLingo_RetAccSampleRateCaps = 0x03,
  audioLingo_SetVideoDelay = 0x05,
  audioLingo_AccAck = 0x00,
  

} e_audioLingo_commands;


#endif //__IAP_audioLingo_DEFINES_H__
