#ifndef __IAP_extendedLingo_DEFINES_H__
#define __IAP_extendedLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  extendedLingo_ACK = 1,
  extendedLingo_GetCurrentPlayingTrackChapterInfo = 2,
    extendedLingo_ReturnCurrentPlayingTrackChapterInfo = 3,
  extendedLingo_SetCurrentPlayingTrackChapter = 4,
  extendedLingo_GetCurrentPlayingTrackChapterPlayStatus = 5,
  extendedLingo_GetCurrentPlayingTrackChapterName = 7,
  extendedLingo_GetAudiobookSpeed = 0x9,
  extendedLingo_SetAudiobookSpeed = 0xB,
  extendedLingo_GetIndexedPlayingTrackInfo = 0xc,
    extendedLingo_ReturnIndexedPlayingTrackInfo = 0xd,
  extendedLingo_GetArtworkFormats = 0xe,
    extendedLingo_RetArtworkFormats = 0xf,
  extendedLingo_ResetDBSelection = 0x16,
  extendedLingo_GetPlayStatus = 0x1c,
    extendedLingo_ReturnPlayStatus = 0x1d,
  extendedLingo_GetCurrentPlayingTrackIndex = 0x1e,
    extendedLingo_ReturnCurrentPlayingTrackIndex = 0x1f,
  extendedLingo_GetIndexedPlayingTrackTitle = 0x20,
    extendedLingo_ReturnIndexedPlayingTrackTitle = 0x21,
  extendedLingo_GetIndexedPlayingTrackArtistName = 0x22,
    extendedLingo_ReturnIndexedPlayingTrackArtistName = 0x23,
  extendedLingo_GetIndexedPlayingTrackAlbumName = 0x24,
    extendedLingo_ReturnIndexedPlayingTrackAlbumName = 0x25,
  extendedLingo_SetPlayStatusChangeNotification = 0x26,
  extendedLingo_PlayCurrentSelection = 0x28,
  extendedLingo_PlayControl = 0x29,
  extendedLingo_GetTrackArtworkTimes = 0x2a,
    extendedLingo_RetTrackArtworkTimes = 0x2b,
  extendedLingo_GetShuffle = 0x2c,
    extendedLingo_ReturnShuffle = 0x2d,
  extendedLingo_SetShuffle = 0x2e,
  extendedLingo_GetRepeat = 0x2f,
    extendedLingo_ReturnRepeat = 0x30,
  extendedLingo_SetRepeat = 0x31,
  extendedLingo_SetDisplayImage = 0x32,
  extendedLingo_SetDisplayImage_data = 0x132,
  extendedLingo_GetNumPlayingTracks = 0x35,
    extendedLingo_ReturnNumPlayingTracks = 0x36,
  extendedLingo_SetCurrentPlayingTrack = 0x37,
  

} e_extendedLingo_commands;


#endif //__IAP_extendedLingo_DEFINES_H__
