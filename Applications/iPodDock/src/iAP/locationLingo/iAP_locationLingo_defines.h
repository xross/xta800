#ifndef __IAP_locationLingo_DEFINES_H__
#define __IAP_locationLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  locationLingo_ACK = 0,
  locationLingo_RetDevCaps_System = 0x02,
  locationLingo_RetDevCaps_NMEA = 0x102,
  locationLingo_RetDevCaps_LocAss = 0x202,
  

} e_locationLingo_commands;


#endif //__IAP_locationLingo_DEFINES_H__
