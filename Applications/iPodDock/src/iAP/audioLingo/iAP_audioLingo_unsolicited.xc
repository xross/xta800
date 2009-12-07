#include "iAP.h"


// --------------------------- GetAccSampleRateCaps -------------------------------

int iAP_audioLingo_GetAccSampleRateCaps_flag = 0;
int iAP_audioLingo_GetAccSampleRateCaps_transid;
int iAP_audioLingo_GetAccSampleRateCaps_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_audioLingo_GetAccSampleRateCaps_flag = 1;
  iAP_audioLingo_GetAccSampleRateCaps_transid = transid;
  return 1;
}

int iAP_audioLingo_GetAccSampleRateCaps_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_audioLingo_GetAccSampleRateCaps_flag)
  {
    iAP_audioLingo_GetAccSampleRateCaps_flag = 0;
    cCtrl <: CMD_audioLingo_GetAccSampleRateCaps;
    cCtrl <: iAP_audioLingo_GetAccSampleRateCaps_transid;
    return 1;
  }
  return 0;
}

// --------------------------- NewiPodTrackInfo -------------------------------

int iAP_audioLingo_NewiPodTrackInfo_flag = 0;
int iAP_audioLingo_NewiPodTrackInfo_newSampleRate;
int iAP_audioLingo_NewiPodTrackInfo_newSoundCheck;
int iAP_audioLingo_NewiPodTrackInfo_newTrackVolumeAdj;
int iAP_audioLingo_NewiPodTrackInfo_transid;


int iAP_audioLingo_NewiPodTrackInfo_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_audioLingo_NewiPodTrackInfo_flag = 1;
  iAP_audioLingo_NewiPodTrackInfo_transid = transid;
  iAP_audioLingo_NewiPodTrackInfo_newSampleRate = CharArrayToInt(pkt, 0);
  iAP_audioLingo_NewiPodTrackInfo_newSoundCheck = CharArrayToInt(pkt, 4);
  iAP_audioLingo_NewiPodTrackInfo_newTrackVolumeAdj = CharArrayToInt(pkt, 8);  
  return 1;
}

int iAP_audioLingo_NewiPodTrackInfo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_audioLingo_NewiPodTrackInfo_flag)
  {
    iAP_audioLingo_NewiPodTrackInfo_flag = 0;
    cCtrl <: CMD_audioLingo_NewiPodTrackInfo;
    cCtrl <: iAP_audioLingo_NewiPodTrackInfo_transid;
    cCtrl <: iAP_audioLingo_NewiPodTrackInfo_newSampleRate;
    cCtrl <: iAP_audioLingo_NewiPodTrackInfo_newSoundCheck;
    cCtrl <: iAP_audioLingo_NewiPodTrackInfo_newTrackVolumeAdj;
    return 1;
  }
  return 0;
}

// --------------------------- Unsolicited checker ----------------------------------

int iAP_audioLingo_unsolicitedrx (streaming_chanend cCtrl, e_audioLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x2):
      return iAP_audioLingo_GetAccSampleRateCaps_rx (cCtrl, pktlen, pkt, transid);
    case (0x4):
      return iAP_audioLingo_NewiPodTrackInfo_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }
}

int iAP_audioLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_audioLingo_GetAccSampleRateCaps_unsolicited(cCtrl))
    return 1;
  if (iAP_audioLingo_NewiPodTrackInfo_unsolicited(cCtrl))
    return 1;
  return 0;
}

