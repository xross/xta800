#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_displayLingo_GetCurrentEQProfileIndex_client:
 * Client command for GetCurrentEQProfileIndex
 */
int iAP_displayLingo_GetCurrentEQProfileIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 1);
  out(cCtrl,2 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = ((int*)pkt)[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_SetCurrentEQProfileIndex_client:
 * Client command for SetCurrentEQProfileIndex
 */
int iAP_displayLingo_SetCurrentEQProfileIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 3);
  out(cCtrl,3 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (int)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetNumEQProfiles_client:
 * Client command for GetNumEQProfiles
 */
int iAP_displayLingo_GetNumEQProfiles_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 4);
  out(cCtrl,5 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (int)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetIndexedEQProfileName_client:
 * Client command for GetIndexedEQProfileName
 */
int iAP_displayLingo_GetIndexedEQProfileName_client(streaming_chanend cCtrl, int transid, unsigned profileindex,REFERENCE_PARAM(int, retlen), unsigned char ret[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 6);
  out(cCtrl,7 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, profileindex);
  pktlen = 4;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        smemcpy(ret, 0, pkt, 0, pktlen);
  *retlen = pktlen;
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_SetRemoteEventNotification_client:
 * Client command for SetRemoteEventNotification
 */
int iAP_displayLingo_SetRemoteEventNotification_client(streaming_chanend cCtrl, int transid, unsigned remEventMask, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 8);
  out(cCtrl,8 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, remEventMask);
  pktlen = 4;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (int)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetRemoteEventStatus_client:
 * Client command for GetRemoteEventStatus
 */
int iAP_displayLingo_GetRemoteEventStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(unsigned, remEventStatus))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x0A);
  out(cCtrl,0x0B & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *remEventStatus = ((int*)pkt)[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetiPodStateInfo_client:
 * Client command for GetiPodStateInfo
 */
int iAP_displayLingo_GetiPodStateInfo_client(streaming_chanend cCtrl, int transid, unsigned infoType,  REFERENCE_PARAM(int, retlen), unsigned char ret[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x0C);
  out(cCtrl,0x0D & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)infoType;
  pktlen = 1;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        smemcpy(ret, 0, pkt, 1, pktlen-1);
  *retlen = pktlen - 1;
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_SetiPodStateInfo_client:
 * Client command for SetiPodStateInfo
 */
int iAP_displayLingo_SetiPodStateInfo_client(streaming_chanend cCtrl, int transid, unsigned infoType,  int len, unsigned char s[], REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x0E);
  out(cCtrl,0x0E & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)infoType;
  smemcpy(pkt, 1, s, 0, len);
  pktlen = len + 1;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (int)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetPlayStatus_client:
 * Client command for GetPlayStatus
 */
int iAP_displayLingo_GetPlayStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, playState), REFERENCE_PARAM(int, trackIndex),REFERENCE_PARAM(int, trackTotMs), REFERENCE_PARAM(int, trackPosMs))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x0F);
  out(cCtrl,0x10 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *playState = (int)pkt[0];
  *trackIndex = CharArrayToInt(pkt, 1);
  *trackTotMs = CharArrayToInt(pkt, 5);
  *trackPosMs = CharArrayToInt(pkt, 9);
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_SetCurrentPlayingTrack_client:
 * Client command for SetCurrentPlayingTrack
 */
int iAP_displayLingo_SetCurrentPlayingTrack_client(streaming_chanend cCtrl, int transid, unsigned trackIndex, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x11);
  out(cCtrl,0x11 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackIndex);
  pktlen = 4;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (unsigned)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetIndexedPlayingTrackInfo_client:
 * Client command for GetIndexedPlayingTrackInfo
 */
int iAP_displayLingo_GetIndexedPlayingTrackInfo_client(streaming_chanend cCtrl, int transid, int infoType, int trackIndex, int chapIndex,  REFERENCE_PARAM(int, retlen), unsigned char ret[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x12);
  out(cCtrl,0x13 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)infoType;
  IntToCharArray(pkt, 1, trackIndex);
  ShortToCharArray(pkt, 5, chapIndex);
  pktlen=7;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        smemcpy(ret, 0, pkt, 1, pktlen-1);
  *retlen = pktlen - 1;
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetNumPlayingTracks_client:
 * Client command for GetNumPlayingTracks
 */
int iAP_displayLingo_GetNumPlayingTracks_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, numPlayingTracks))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x14);
  out(cCtrl,0x15 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *numPlayingTracks = CharArrayToInt(pkt, 0);
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetArtworkFormats_client:
 * Client command for GetArtworkFormats
 */
int iAP_displayLingo_GetArtworkFormats_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, nformats), s_artwork_format artwork[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x16);
  out(cCtrl,0x17 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *nformats = pktlen / sizeof(s_artwork_format);
  for (i=0; i<pktlen; i++)
  ((char*)artwork)[i] = pkt[i];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetTrackArtworkData_client:
 * Client command for GetTrackArtworkData
 */
int iAP_displayLingo_GetTrackArtworkData_client(streaming_chanend cCtrl, int transid, int trackIndex, int formatID, int timeOffset, REFERENCE_PARAM(s_artwork_data, adata), REFERENCE_PARAM(int, ndata), unsigned char spkt[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x18);
  out(cCtrl,0x19 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackIndex);
  ShortToCharArray(pkt, 4, formatID);
  IntToCharArray(pkt, 6, timeOffset);
  pktlen = 10;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        for (i=0; i<sizeof(s_artwork_data); i++)
  ((char*)adata)[i] = pkt[i];
  *ndata = pktlen - sizeof(s_artwork_data);
  for (i=0; i<*ndata; i++)
  spkt[i] = pkt[i + sizeof(s_artwork_data)];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetPowerBatteryState_client:
 * Client command for GetPowerBatteryState
 */
int iAP_displayLingo_GetPowerBatteryState_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, powerState), REFERENCE_PARAM(int, battLevel))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x1A);
  out(cCtrl,0x1B & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *powerState = CharArrayToInt(pkt, 0);
  *battLevel = (int)pkt[4];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetSoundCheckState_client:
 * Client command for GetSoundCheckState
 */
int iAP_displayLingo_GetSoundCheckState_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, sndChkState))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x1C);
  out(cCtrl,0x1D & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *sndChkState = CharArrayToInt(pkt, 0);
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_SetSoundCheckState_client:
 * Client command for SetSoundCheckState
 */
int iAP_displayLingo_SetSoundCheckState_client(streaming_chanend cCtrl, int transid, int sndChkState, int bRestoreOnExit, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x1E);
  out(cCtrl,0x1E & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)sndChkState;
  pkt[1] = (char)bRestoreOnExit;
  pktlen = 2;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = CharArrayToInt(pkt, 0);
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_displayLingo_GetTrackArtworkTimes_client:
 * Client command for GetTrackArtworkTimes
 */
int iAP_displayLingo_GetTrackArtworkTimes_client(streaming_chanend cCtrl, int transid, int trackIndex, int formatID, int artworkIndex, int artworkCount, REFERENCE_PARAM(int, ntimes), int arttimes[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, displayLingo);
    out(cCtrl, 0x1F);
  out(cCtrl,0x20 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackIndex);
  ShortToCharArray(pkt, 4, formatID);
  ShortToCharArray(pkt, 6, artworkIndex);
  ShortToCharArray(pkt, 8, artworkCount);
  pktlen = 10;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *ntimes = pktlen/4;
  for (i=0; i<pktlen; i++)
  ((char*)arttimes)[i] = pkt[i];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}



