#ifndef __IAP_storageLingo_DEFINES_H__
#define __IAP_storageLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  storageLingo_ACK = 0x00,
  storageLingo_GetiPodCaps = 0x01,
    storageLingo_RetiPodCaps = 0x02,
  storageLingo_OpeniPodFeatureFile = 0x12,
  storageLingo_WriteiPodFileData = 0x07,
  storageLingo_CloseiPodFile = 0x08,
  storageLingo_GetiPodFreeSpace = 0x10,
    storageLingo_RetiPodFreeSpace = 0x11,
  storageLingo_RetDeviceCaps = 0x12,
  

} e_storageLingo_commands;


#endif //__IAP_storageLingo_DEFINES_H__
