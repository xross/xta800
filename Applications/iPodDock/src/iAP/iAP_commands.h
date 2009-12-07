#ifndef __IAP_COMMANDS_H__

#define __IAP_COMMANDS_H__

#include "iAP.h"

/*
 * Enum of iAP commands used for communicating between EP Manager and iPodMain layer
 * This includes unsolicited commands manually inserted in this file
 */
typedef enum
{
  CMD_checkunsolicited = 0x100,
  CMD_none,
  CMD_generalLingo_GetDevAuthenticationInfo,
  CMD_generalLingo_GetDevAuthenticationSignature,
  CMD_generalLingo_NotifyiPodStateChange,
  CMD_generalLingo_GetAccessoryInfo,
  CMD_audioLingo_GetAccSampleRateCaps,
  CMD_audioLingo_NewiPodTrackInfo,
  CMD_equalizerLingo_GetCurrentEQIndex,
  CMD_equalizerLingo_SetCurrentEQIndex,
  CMD_equalizerLingo_GetEQSettingCount,
  CMD_equalizerLingo_GetEQIndexName,
  CMD_storageLingo_GetDeviceCaps,
  CMD_storageLingo_RetiPodFileHandle,
  CMD_displayLingo_RemoteEventNotification,
  CMD_microphoneLingo_GetDevAck,
  CMD_microphoneLingo_iPodModeChange,
  CMD_microphoneLingo_GetDevCaps,
  CMD_microphoneLingo_GetDevCtrl,
  CMD_microphoneLingo_SetDevCtrl,
  CMD_powerLingo_BeginHighPower,
  CMD_powerLingo_EndHighPower,

#if GENERAL_LINGO != 0
#include "iAP_generalLingo_cmds.h"
#endif
#if MICROPHONE_LINGO != 0
#include "iAP_microphoneLingo_cmds.h"
#endif
#if SIMPLE_REMOTE_LINGO != 0
#include "iAP_simpleLingo_cmds.h"
#endif
#if DISPLAY_REMOTE_LINGO != 0
#include "iAP_displayLingo_cmds.h"
#endif
#if EXT_REMOTE_LINGO != 0
#include "iAP_extendedLingo_cmds.h"
#endif
#if POWER_LINGO != 0
#include "iAP_powerLingo_cmds.h"
#endif
#if SPORTS_LINGO != 0
#include "iAP_sportsLingo_cmds.h"
#endif
#if AUDIO_LINGO != 0
#include "iAP_audioLingo_cmds.h"
#endif
#if RT_TUNER_LINGO != 0
#include "iAP_rftunerLingo_cmds.h"
#endif
#if STORAGE_LINGO != 0
#include "iAP_storageLingo_cmds.h"
#endif
#if EQUALIZER_LINGO != 0
#include "iAP_equalizerLingo_cmds.h"
#endif
#if LOCATION_LINGO != 0
#include "iAP_locationLingo_cmds.h"
#endif
} e_cmds;


#endif //__IAP_COMMANDS_H__


