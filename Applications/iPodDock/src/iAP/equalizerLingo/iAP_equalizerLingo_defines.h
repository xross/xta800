#ifndef __IAP_equalizerLingo_DEFINES_H__
#define __IAP_equalizerLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  equalizerLingo_ACK = 99,
  equalizerLingo_Ack = 0x00,
  equalizerLingo_RetCurrentEQIndex = 0x02,
  equalizerLingo_RetEQSettingCount = 0x05,
  equalizerLingo_RetEQIndexName = 0x07,
  

} e_equalizerLingo_commands;


#endif //__IAP_equalizerLingo_DEFINES_H__
