#ifndef __IAP_extendedLingo_CLIENT_H__

#define __IAP_extendedLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterInfo_client:
 * Client command for GetCurrentPlayingTrackChapterInfo
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterInfo_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, chapterindex), REFERENCE_PARAM(int, chaptercount));

/*
 * iAP_extendedLingo_SetCurrentPlayingTrackChapter_client:
 * Client command for SetCurrentPlayingTrackChapter
 */
int iAP_extendedLingo_SetCurrentPlayingTrackChapter_client(streaming_chanend cCtrl, int transid, int chapterindex, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterPlayStatus_client:
 * Client command for GetCurrentPlayingTrackChapterPlayStatus
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterPlayStatus_client(streaming_chanend cCtrl, int transid, int chapterindex, REFERENCE_PARAM(int, chaplen), REFERENCE_PARAM(int, chapelapsed) );

/*
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterName_client:
 * Client command for GetCurrentPlayingTrackChapterName
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterName_client(streaming_chanend cCtrl, int transid, int chapindex, REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_extendedLingo_GetAudiobookSpeed_client:
 * Client command for GetAudiobookSpeed
 */
int iAP_extendedLingo_GetAudiobookSpeed_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, speedcode));

/*
 * iAP_extendedLingo_SetAudiobookSpeed_client:
 * Client command for SetAudiobookSpeed
 */
int iAP_extendedLingo_SetAudiobookSpeed_client(streaming_chanend cCtrl, int transid, int speedcode, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetIndexedPlayingTrackInfo_client:
 * Client command for GetIndexedPlayingTrackInfo
 */
int iAP_extendedLingo_GetIndexedPlayingTrackInfo_client(streaming_chanend cCtrl, int transid, char trackinfotype, int trackindex, int chapterindex, REFERENCE_PARAM(int, rettrackinfotype), REFERENCE_PARAM(int, trackinfolen), unsigned char trackinfo[]);

/*
 * iAP_extendedLingo_GetArtworkFormats_client:
 * Client command for GetArtworkFormats
 */
int iAP_extendedLingo_GetArtworkFormats_client(streaming_chanend cCtrl, int transid, int cmdid, REFERENCE_PARAM(int, retcmdid), REFERENCE_PARAM(int, formatid), REFERENCE_PARAM(unsigned char, pixelformat), REFERENCE_PARAM(int, imagewidth), REFERENCE_PARAM(int, imageheight));

/*
 * iAP_extendedLingo_ResetDBSelection_client:
 * Client command for ResetDBSelection
 */
int iAP_extendedLingo_ResetDBSelection_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetPlayStatus_client:
 * Client command for GetPlayStatus
 */
int iAP_extendedLingo_GetPlayStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, tracklen), REFERENCE_PARAM(int, trackpos), REFERENCE_PARAM(int, playerstate));

/*
 * iAP_extendedLingo_GetCurrentPlayingTrackIndex_client:
 * Client command for GetCurrentPlayingTrackIndex
 */
int iAP_extendedLingo_GetCurrentPlayingTrackIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, trackindex));

/*
 * iAP_extendedLingo_GetIndexedPlayingTrackTitle_client:
 * Client command for GetIndexedPlayingTrackTitle
 */
int iAP_extendedLingo_GetIndexedPlayingTrackTitle_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[]);

/*
 * iAP_extendedLingo_GetIndexedPlayingTrackArtistName_client:
 * Client command for GetIndexedPlayingTrackArtistName
 */
int iAP_extendedLingo_GetIndexedPlayingTrackArtistName_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[]);

/*
 * iAP_extendedLingo_GetIndexedPlayingTrackAlbumName_client:
 * Client command for GetIndexedPlayingTrackAlbumName
 */
int iAP_extendedLingo_GetIndexedPlayingTrackAlbumName_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[]);

/*
 * iAP_extendedLingo_SetPlayStatusChangeNotification_client:
 * Client command for SetPlayStatusChangeNotification
 */
int iAP_extendedLingo_SetPlayStatusChangeNotification_client(streaming_chanend cCtrl, int transid, int notificationeventmask, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_PlayCurrentSelection_client:
 * Client command for PlayCurrentSelection
 */
int iAP_extendedLingo_PlayCurrentSelection_client(streaming_chanend cCtrl, int transid, int trackrecordindex, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_PlayControl_client:
 * Client command for PlayControl
 */
int iAP_extendedLingo_PlayControl_client(streaming_chanend cCtrl, int transid, char commandcode, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetTrackArtworkTimes_client:
 * Client command for GetTrackArtworkTimes
 */
int iAP_extendedLingo_GetTrackArtworkTimes_client(streaming_chanend cCtrl, int transid, int trackindex, short formatid, short artworkindex, short artworkcount, REFERENCE_PARAM(int, timeoffset));

/*
 * iAP_extendedLingo_GetShuffle_client:
 * Client command for GetShuffle
 */
int iAP_extendedLingo_GetShuffle_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, mode));

/*
 * iAP_extendedLingo_SetShuffle_client:
 * Client command for SetShuffle
 */
int iAP_extendedLingo_SetShuffle_client(streaming_chanend cCtrl, int transid, char mode, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetRepeat_client:
 * Client command for GetRepeat
 */
int iAP_extendedLingo_GetRepeat_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, mode));

/*
 * iAP_extendedLingo_SetRepeat_client:
 * Client command for SetRepeat
 */
int iAP_extendedLingo_SetRepeat_client(streaming_chanend cCtrl, int transid, char mode, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_SetDisplayImage_client:
 * Client command for SetDisplayImage
 */
int iAP_extendedLingo_SetDisplayImage_client(streaming_chanend cCtrl, int transid, short telegramindex, char pixelfmt, short imagewidth, short imageheight, int rowsize, char data[], int datalen, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_SetDisplayImage_data_client:
 * Client command for SetDisplayImage_data
 */
int iAP_extendedLingo_SetDisplayImage_data_client(streaming_chanend cCtrl, int transid, short telegramindex, char data[], int datalen, REFERENCE_PARAM(int, retval));

/*
 * iAP_extendedLingo_GetNumPlayingTracks_client:
 * Client command for GetNumPlayingTracks
 */
int iAP_extendedLingo_GetNumPlayingTracks_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, numtracks));

/*
 * iAP_extendedLingo_SetCurrentPlayingTrack_client:
 * Client command for SetCurrentPlayingTrack
 */
int iAP_extendedLingo_SetCurrentPlayingTrack_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retval));


#endif //__IAP_extendedLingo_CLIENT_H__

