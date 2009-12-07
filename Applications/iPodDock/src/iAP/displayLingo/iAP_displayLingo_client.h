#ifndef __IAP_displayLingo_CLIENT_H__

#define __IAP_displayLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_displayLingo_GetCurrentEQProfileIndex_client:
 * Client command for GetCurrentEQProfileIndex
 */
int iAP_displayLingo_GetCurrentEQProfileIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_SetCurrentEQProfileIndex_client:
 * Client command for SetCurrentEQProfileIndex
 */
int iAP_displayLingo_SetCurrentEQProfileIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetNumEQProfiles_client:
 * Client command for GetNumEQProfiles
 */
int iAP_displayLingo_GetNumEQProfiles_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetIndexedEQProfileName_client:
 * Client command for GetIndexedEQProfileName
 */
int iAP_displayLingo_GetIndexedEQProfileName_client(streaming_chanend cCtrl, int transid, unsigned profileindex,REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_displayLingo_SetRemoteEventNotification_client:
 * Client command for SetRemoteEventNotification
 */
int iAP_displayLingo_SetRemoteEventNotification_client(streaming_chanend cCtrl, int transid, unsigned remEventMask, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetRemoteEventStatus_client:
 * Client command for GetRemoteEventStatus
 */
int iAP_displayLingo_GetRemoteEventStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(unsigned, remEventStatus));

/*
 * iAP_displayLingo_GetiPodStateInfo_client:
 * Client command for GetiPodStateInfo
 */
int iAP_displayLingo_GetiPodStateInfo_client(streaming_chanend cCtrl, int transid, unsigned infoType,  REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_displayLingo_SetiPodStateInfo_client:
 * Client command for SetiPodStateInfo
 */
int iAP_displayLingo_SetiPodStateInfo_client(streaming_chanend cCtrl, int transid, unsigned infoType,  int len, unsigned char s[], REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetPlayStatus_client:
 * Client command for GetPlayStatus
 */
int iAP_displayLingo_GetPlayStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, playState), REFERENCE_PARAM(int, trackIndex),REFERENCE_PARAM(int, trackTotMs), REFERENCE_PARAM(int, trackPosMs));

/*
 * iAP_displayLingo_SetCurrentPlayingTrack_client:
 * Client command for SetCurrentPlayingTrack
 */
int iAP_displayLingo_SetCurrentPlayingTrack_client(streaming_chanend cCtrl, int transid, unsigned trackIndex, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetIndexedPlayingTrackInfo_client:
 * Client command for GetIndexedPlayingTrackInfo
 */
int iAP_displayLingo_GetIndexedPlayingTrackInfo_client(streaming_chanend cCtrl, int transid, int infoType, int trackIndex, int chapIndex,  REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_displayLingo_GetNumPlayingTracks_client:
 * Client command for GetNumPlayingTracks
 */
int iAP_displayLingo_GetNumPlayingTracks_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, numPlayingTracks));

/*
 * iAP_displayLingo_GetArtworkFormats_client:
 * Client command for GetArtworkFormats
 */
int iAP_displayLingo_GetArtworkFormats_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, nformats), s_artwork_format artwork[]);

/*
 * iAP_displayLingo_GetTrackArtworkData_client:
 * Client command for GetTrackArtworkData
 */
int iAP_displayLingo_GetTrackArtworkData_client(streaming_chanend cCtrl, int transid, int trackIndex, int formatID, int timeOffset, REFERENCE_PARAM(s_artwork_data, adata), REFERENCE_PARAM(int, ndata), unsigned char spkt[]);

/*
 * iAP_displayLingo_GetPowerBatteryState_client:
 * Client command for GetPowerBatteryState
 */
int iAP_displayLingo_GetPowerBatteryState_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, powerState), REFERENCE_PARAM(int, battLevel));

/*
 * iAP_displayLingo_GetSoundCheckState_client:
 * Client command for GetSoundCheckState
 */
int iAP_displayLingo_GetSoundCheckState_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, sndChkState));

/*
 * iAP_displayLingo_SetSoundCheckState_client:
 * Client command for SetSoundCheckState
 */
int iAP_displayLingo_SetSoundCheckState_client(streaming_chanend cCtrl, int transid, int sndChkState, int bRestoreOnExit, REFERENCE_PARAM(int, retval));

/*
 * iAP_displayLingo_GetTrackArtworkTimes_client:
 * Client command for GetTrackArtworkTimes
 */
int iAP_displayLingo_GetTrackArtworkTimes_client(streaming_chanend cCtrl, int transid, int trackIndex, int formatID, int artworkIndex, int artworkCount, REFERENCE_PARAM(int, ntimes), int arttimes[]);


#endif //__IAP_displayLingo_CLIENT_H__

