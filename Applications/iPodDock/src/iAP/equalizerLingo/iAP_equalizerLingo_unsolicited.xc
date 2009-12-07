#include "iAP.h"

// --------------------------- iAP_equalizerLingo_GetCurrentEQIndex_rx -------------------------------

int iAP_equalizerLingo_GetCurrentEQIndex_rx_flag = 0;
int iAP_equalizerLingo_GetCurrentEQIndex_transid = 0;
int iAP_equalizerLingo_GetCurrentEQIndex_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_equalizerLingo_GetCurrentEQIndex_rx_flag = 1;
  iAP_equalizerLingo_GetCurrentEQIndex_transid = transid;
  return 1;
}

int iAP_equalizerLingo_GetCurrentEQIndex_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_equalizerLingo_GetCurrentEQIndex_rx_flag)
  {
    iAP_equalizerLingo_GetCurrentEQIndex_rx_flag = 0;
    cCtrl <: CMD_equalizerLingo_GetCurrentEQIndex;
    cCtrl <: iAP_equalizerLingo_GetCurrentEQIndex_transid;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_equalizerLingo_SetCurrentEQIndex_rx -------------------------------

int iAP_equalizerLingo_SetCurrentEQIndex_rx_flag = 0;
int iAP_equalizerLingo_SetCurrentEQIndex_transid = 0;
char iAP_equalizerLingo_SetCurrentEQIndex_eqIndex;
int iAP_equalizerLingo_SetCurrentEQIndex_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_equalizerLingo_SetCurrentEQIndex_rx_flag = 1;
  iAP_equalizerLingo_SetCurrentEQIndex_eqIndex = pkt[0];
  iAP_equalizerLingo_SetCurrentEQIndex_transid = transid;
  return 1;
}

int iAP_equalizerLingo_SetCurrentEQIndex_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_equalizerLingo_SetCurrentEQIndex_rx_flag)
  {
    iAP_equalizerLingo_SetCurrentEQIndex_rx_flag = 0;
    cCtrl <: CMD_equalizerLingo_SetCurrentEQIndex;
    cCtrl <: iAP_equalizerLingo_SetCurrentEQIndex_transid;
    cCtrl <: iAP_equalizerLingo_SetCurrentEQIndex_eqIndex;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_equalizerLingo_GetEQSettingCount_rx -------------------------------

int iAP_equalizerLingo_GetEQSettingCount_rx_flag = 0;
int iAP_equalizerLingo_GetEQSettingCount_transid = 0;
int iAP_equalizerLingo_GetEQSettingCount_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_equalizerLingo_GetEQSettingCount_rx_flag = 1;
  iAP_equalizerLingo_GetEQSettingCount_transid = transid;
  return 1;
}

int iAP_equalizerLingo_GetEQSettingCount_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_equalizerLingo_GetEQSettingCount_rx_flag)
  {
    iAP_equalizerLingo_GetEQSettingCount_rx_flag = 0;
    cCtrl <: CMD_equalizerLingo_GetEQSettingCount;
    cCtrl <: iAP_equalizerLingo_GetEQSettingCount_transid;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_equalizerLingo_GetEQIndexName_rx -------------------------------

int iAP_equalizerLingo_GetEQIndexName_rx_flag = 0;
int iAP_equalizerLingo_GetEQIndexName_transid = 0;
char iAP_equalizerLingo_GetEQIndexName_eqIndex;
int iAP_equalizerLingo_GetEQIndexName_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_equalizerLingo_GetEQIndexName_rx_flag = 1;
  iAP_equalizerLingo_GetEQIndexName_eqIndex = pkt[0];
  iAP_equalizerLingo_GetEQIndexName_transid = transid;
  return 1;
}

int iAP_equalizerLingo_GetEQIndexName_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_equalizerLingo_GetEQIndexName_rx_flag)
  {
    iAP_equalizerLingo_GetEQIndexName_rx_flag = 0;
    cCtrl <: CMD_equalizerLingo_GetEQIndexName;
    cCtrl <: iAP_equalizerLingo_GetEQIndexName_transid;
    cCtrl <: iAP_equalizerLingo_GetEQIndexName_eqIndex;
    return 1;
  }
  return 0;
}

// --------------------------- Unsolicited checker ----------------------------------

int iAP_equalizerLingo_unsolicitedrx (streaming_chanend cCtrl, e_equalizerLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x01):
      return iAP_equalizerLingo_GetCurrentEQIndex_rx (cCtrl, pktlen, pkt, transid);
    case (0x03):
      return iAP_equalizerLingo_SetCurrentEQIndex_rx (cCtrl, pktlen, pkt, transid);
    case (0x04):
      return iAP_equalizerLingo_GetEQSettingCount_rx (cCtrl, pktlen, pkt, transid);
    case (0x06):
      return iAP_equalizerLingo_GetEQIndexName_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }
  return 0;
}

int iAP_equalizerLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_equalizerLingo_GetCurrentEQIndex_unsolicited(cCtrl))
    return 1;
  if (iAP_equalizerLingo_SetCurrentEQIndex_unsolicited(cCtrl))
    return 1;
  if (iAP_equalizerLingo_GetEQSettingCount_unsolicited(cCtrl))
    return 1;
  if (iAP_equalizerLingo_GetEQIndexName_unsolicited(cCtrl))
    return 1;
  return 0;
}
