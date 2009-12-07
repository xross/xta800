#ifndef __IAP_simpleLingo_DEFINES_H__
#define __IAP_simpleLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  simpleLingo_ACK = 1,
  simpleLingo_CONTEXTBUTTONSTATUS = 0,
  simpleLingo_IMAGEBUTTONSTATUS = 2,
  simpleLingo_VIDEOBUTTONSTATUS = 3,
  simpleLingo_AUDIOBUTTONSTATUS = 4,
  

} e_simpleLingo_commands;


#endif //__IAP_simpleLingo_DEFINES_H__
