#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterInfo_client:
 * Client command for GetCurrentPlayingTrackChapterInfo
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterInfo_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, chapterindex), REFERENCE_PARAM(int, chaptercount))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 2);
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
        *chapterindex = (int)pkt[0]; *chaptercount = (int)pkt[1];
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
 * iAP_extendedLingo_SetCurrentPlayingTrackChapter_client:
 * Client command for SetCurrentPlayingTrackChapter
 */
int iAP_extendedLingo_SetCurrentPlayingTrackChapter_client(streaming_chanend cCtrl, int transid, int chapterindex, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 4);
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
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterPlayStatus_client:
 * Client command for GetCurrentPlayingTrackChapterPlayStatus
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterPlayStatus_client(streaming_chanend cCtrl, int transid, int chapterindex, REFERENCE_PARAM(int, chaplen), REFERENCE_PARAM(int, chapelapsed) )
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 5);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, chapterindex); pktlen = 4;
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
        *chaplen = pkt[0]; *chapelapsed = pkt[1];
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
 * iAP_extendedLingo_GetCurrentPlayingTrackChapterName_client:
 * Client command for GetCurrentPlayingTrackChapterName
 */
int iAP_extendedLingo_GetCurrentPlayingTrackChapterName_client(streaming_chanend cCtrl, int transid, int chapindex, REFERENCE_PARAM(int, retlen), unsigned char ret[])
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 7);
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
        *retlen = pktlen; smemcpy(ret, 0, pkt, 0, *retlen);
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
 * iAP_extendedLingo_GetAudiobookSpeed_client:
 * Client command for GetAudiobookSpeed
 */
int iAP_extendedLingo_GetAudiobookSpeed_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, speedcode))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x9);
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
        *speedcode = (int)pkt[0];
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
 * iAP_extendedLingo_SetAudiobookSpeed_client:
 * Client command for SetAudiobookSpeed
 */
int iAP_extendedLingo_SetAudiobookSpeed_client(streaming_chanend cCtrl, int transid, int speedcode, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0xB);
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
 * iAP_extendedLingo_GetIndexedPlayingTrackInfo_client:
 * Client command for GetIndexedPlayingTrackInfo
 */
int iAP_extendedLingo_GetIndexedPlayingTrackInfo_client(streaming_chanend cCtrl, int transid, char trackinfotype, int trackindex, int chapterindex, REFERENCE_PARAM(int, rettrackinfotype), REFERENCE_PARAM(int, trackinfolen), unsigned char trackinfo[])
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0xc);
  out(cCtrl,0xd & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = trackinfotype; IntToCharArray(pkt, 4, trackindex); ShortToCharArray(pkt, 0x14, chapterindex);   pktlen=12;     
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
        *rettrackinfotype = pkt[0]; smemcpy(trackinfo, 0, pkt, 4, pktlen - 4);
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
 * iAP_extendedLingo_GetArtworkFormats_client:
 * Client command for GetArtworkFormats
 */
int iAP_extendedLingo_GetArtworkFormats_client(streaming_chanend cCtrl, int transid, int cmdid, REFERENCE_PARAM(int, retcmdid), REFERENCE_PARAM(int, formatid), REFERENCE_PARAM(unsigned char, pixelformat), REFERENCE_PARAM(int, imagewidth), REFERENCE_PARAM(int, imageheight))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0xe);
  out(cCtrl,0xf & 0xFF);
    out(cCtrl, transid);
    
    pktlen=0;
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
        smemcpy(formatid, 0, pkt, 8, 2); pixelformat=pkt[2]; smemcpy(imagewidth, 0, pkt, 3, 2); smemcpy(imageheight, 0, pkt, 5, 2);
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
 * iAP_extendedLingo_ResetDBSelection_client:
 * Client command for ResetDBSelection
 */
int iAP_extendedLingo_ResetDBSelection_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x16);
    out(cCtrl, transid);
    
    pktlen=0;
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
 * iAP_extendedLingo_GetPlayStatus_client:
 * Client command for GetPlayStatus
 */
int iAP_extendedLingo_GetPlayStatus_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, tracklen), REFERENCE_PARAM(int, trackpos), REFERENCE_PARAM(int, playerstate))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x1c);
  out(cCtrl,0x1d & 0xFF);
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
        smemcpy(tracklen, 0, pkt, 0, 4); smemcpy(trackpos, 0, pkt, 4, 4); playerstate=(int)pkt[9];
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
 * iAP_extendedLingo_GetCurrentPlayingTrackIndex_client:
 * Client command for GetCurrentPlayingTrackIndex
 */
int iAP_extendedLingo_GetCurrentPlayingTrackIndex_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, trackindex))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x1e);
  out(cCtrl,0x1f & 0xFF);
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
        smemcpy(trackindex,0,pkt,0,4);
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
 * iAP_extendedLingo_GetIndexedPlayingTrackTitle_client:
 * Client command for GetIndexedPlayingTrackTitle
 */
int iAP_extendedLingo_GetIndexedPlayingTrackTitle_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[])
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x20);
  out(cCtrl,0x21 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackindex);
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
        smemcpy(title, 0, pkt, 0, retlen)
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
 * iAP_extendedLingo_GetIndexedPlayingTrackArtistName_client:
 * Client command for GetIndexedPlayingTrackArtistName
 */
int iAP_extendedLingo_GetIndexedPlayingTrackArtistName_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[])
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x22);
  out(cCtrl,0x23 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackindex);
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
        smemcpy(title, 0, pkt, 0, retlen)
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
 * iAP_extendedLingo_GetIndexedPlayingTrackAlbumName_client:
 * Client command for GetIndexedPlayingTrackAlbumName
 */
int iAP_extendedLingo_GetIndexedPlayingTrackAlbumName_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retlen), unsigned char title[])
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x24);
  out(cCtrl,0x25 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackindex);
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
        smemcpy(title, 0, pkt, 0, retlen)
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
 * iAP_extendedLingo_SetPlayStatusChangeNotification_client:
 * Client command for SetPlayStatusChangeNotification
 */
int iAP_extendedLingo_SetPlayStatusChangeNotification_client(streaming_chanend cCtrl, int transid, int notificationeventmask, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x26);
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
 * iAP_extendedLingo_PlayCurrentSelection_client:
 * Client command for PlayCurrentSelection
 */
int iAP_extendedLingo_PlayCurrentSelection_client(streaming_chanend cCtrl, int transid, int trackrecordindex, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x28);
  out(cCtrl,0x28 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackrecordindex); pktlen = 4;
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
        *retval = (int) pkt[0];
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
 * iAP_extendedLingo_PlayControl_client:
 * Client command for PlayControl
 */
int iAP_extendedLingo_PlayControl_client(streaming_chanend cCtrl, int transid, char commandcode, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x29);
  out(cCtrl,0x29 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = commandcode; pktlen = 1;
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
        *retval = (int) pkt[0];
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
 * iAP_extendedLingo_GetTrackArtworkTimes_client:
 * Client command for GetTrackArtworkTimes
 */
int iAP_extendedLingo_GetTrackArtworkTimes_client(streaming_chanend cCtrl, int transid, int trackindex, short formatid, short artworkindex, short artworkcount, REFERENCE_PARAM(int, timeoffset))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x2a);
  out(cCtrl,0x2b & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackindex); ShortToCharArray(pkt, 4, formatid);  ShortToCharArray(pkt, 6, artworkindex); ShortToCharArray(pkt, 8, artworkcount); pktlen = 10;
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
        *timeoffset = CharArrayToInt(pkt, 0);
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
 * iAP_extendedLingo_GetShuffle_client:
 * Client command for GetShuffle
 */
int iAP_extendedLingo_GetShuffle_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, mode))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x2c);
  out(cCtrl,0x2d & 0xFF);
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
        *mode = pkt[0];
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
 * iAP_extendedLingo_SetShuffle_client:
 * Client command for SetShuffle
 */
int iAP_extendedLingo_SetShuffle_client(streaming_chanend cCtrl, int transid, char mode, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x2e);
  out(cCtrl,0x2e & 0xFF);
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
        *retval = pkt[0];
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
 * iAP_extendedLingo_GetRepeat_client:
 * Client command for GetRepeat
 */
int iAP_extendedLingo_GetRepeat_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(char, mode))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x2f);
  out(cCtrl,0x30 & 0xFF);
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
        *mode = pkt[0];
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
 * iAP_extendedLingo_SetRepeat_client:
 * Client command for SetRepeat
 */
int iAP_extendedLingo_SetRepeat_client(streaming_chanend cCtrl, int transid, char mode, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x31);
  out(cCtrl,0x31 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = mode; pktlen = 1;
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
        *retval = pkt[0];
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
 * iAP_extendedLingo_SetDisplayImage_client:
 * Client command for SetDisplayImage
 */
int iAP_extendedLingo_SetDisplayImage_client(streaming_chanend cCtrl, int transid, short telegramindex, char pixelfmt, short imagewidth, short imageheight, int rowsize, char data[], int datalen, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x32);
  out(cCtrl,0x32 & 0xFF);
    out(cCtrl, transid);
    
    ShortToCharArray(pkt, 0, telegramindex); pkt[2] = pixelfmt; ShortToCharArray(pkt, 3, imagewidth); ShortToCharArray(pkt, 5, imageheight); IntToCharArray(pkt, 7, rowsize); smemcpy (pkt, 11, data, 0, datalen); pktlen = 11 + datalen;
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
        *retval = pkt[0];
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
 * iAP_extendedLingo_SetDisplayImage_data_client:
 * Client command for SetDisplayImage_data
 */
int iAP_extendedLingo_SetDisplayImage_data_client(streaming_chanend cCtrl, int transid, short telegramindex, char data[], int datalen, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x132);
  out(cCtrl,0x132 & 0xFF);
    out(cCtrl, transid);
    
    ShortToCharArray(pkt, 0, telegramindex); smemcpy (pkt, 2, data, 0, datalen); pktlen = 2 + datalen;
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
        *retval = pkt[0];
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
 * iAP_extendedLingo_GetNumPlayingTracks_client:
 * Client command for GetNumPlayingTracks
 */
int iAP_extendedLingo_GetNumPlayingTracks_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, numtracks))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x35);
  out(cCtrl,0x36 & 0xFF);
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
        *numtracks = CharArrayToInt(pkt,0);
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
 * iAP_extendedLingo_SetCurrentPlayingTrack_client:
 * Client command for SetCurrentPlayingTrack
 */
int iAP_extendedLingo_SetCurrentPlayingTrack_client(streaming_chanend cCtrl, int transid, int trackindex, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, extendedLingo);
    out(cCtrl, 0x37);
  out(cCtrl,0x37 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, trackindex); pktlen = 4;
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
        *retval = pkt[0];
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



