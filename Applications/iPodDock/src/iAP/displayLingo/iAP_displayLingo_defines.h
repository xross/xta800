#ifndef __IAP_displayLingo_DEFINES_H__
#define __IAP_displayLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  displayLingo_ACK = 0,
  displayLingo_GetCurrentEQProfileIndex = 1,
    displayLingo_RetCurrentEQProfileIndex = 2,
  displayLingo_SetCurrentEQProfileIndex = 3,
  displayLingo_GetNumEQProfiles = 4,
    displayLingo_RetNumEQProfiles = 5,
  displayLingo_GetIndexedEQProfileName = 6,
    displayLingo_RetIndexedEQProfileName = 7,
  displayLingo_SetRemoteEventNotification = 8,
  displayLingo_GetRemoteEventStatus = 0x0A,
    displayLingo_RetRemoteEventStatus = 0x0B,
  displayLingo_GetiPodStateInfo = 0x0C,
    displayLingo_RetiPodStateInfo = 0x0D,
  displayLingo_SetiPodStateInfo = 0x0E,
  displayLingo_GetPlayStatus = 0x0F,
    displayLingo_RetPlayStatus = 0x10,
  displayLingo_SetCurrentPlayingTrack = 0x11,
  displayLingo_GetIndexedPlayingTrackInfo = 0x12,
    displayLingo_RetIndexedPlayingTrackInfo = 0x13,
  displayLingo_GetNumPlayingTracks = 0x14,
    displayLingo_RetNumPlayingTracks = 0x15,
  displayLingo_GetArtworkFormats = 0x16,
    displayLingo_RetArtworkFormats = 0x17,
  displayLingo_GetTrackArtworkData = 0x18,
    displayLingo_RetTrackArtworkData = 0x19,
  displayLingo_GetPowerBatteryState = 0x1A,
    displayLingo_RetPowerBatteryState = 0x1B,
  displayLingo_GetSoundCheckState = 0x1C,
    displayLingo_RetSoundCheckState = 0x1D,
  displayLingo_SetSoundCheckState = 0x1E,
  displayLingo_GetTrackArtworkTimes = 0x1F,
    displayLingo_RetTrackArtworkTimes = 0x20,
  

} e_displayLingo_commands;


#endif //__IAP_displayLingo_DEFINES_H__
