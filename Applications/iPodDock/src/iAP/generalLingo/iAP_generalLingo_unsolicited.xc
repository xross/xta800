#include "iAP.h"


// --------------------------- GetDevAuthenticationInfo -------------------------------

int iAP_generalLingo_GetDevAuthenticationInfo_flag = 0;
int iAP_generalLingo_GetDevAuthenticationInfo_transid;
int iAP_generalLingo_GetDevAuthenticationInfo_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_generalLingo_GetDevAuthenticationInfo_flag = 1;
  iAP_generalLingo_GetDevAuthenticationInfo_transid = transid;
  return 1;
}

int iAP_generalLingo_GetDevAuthenticationInfo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_generalLingo_GetDevAuthenticationInfo_flag)
  {
    iAP_generalLingo_GetDevAuthenticationInfo_flag = 0;
    cCtrl <: CMD_generalLingo_GetDevAuthenticationInfo;
    cCtrl <: iAP_generalLingo_GetDevAuthenticationInfo_transid;
    return 1;
  }
  return 0;
}

// --------------------------- GetDevAuthenticationSignature -------------------------------

int iAP_generalLingo_GetDevAuthenticationSignature_flag = 0;
int iAP_generalLingo_GetDevAuthenticationSignature_pktlen = 0;
unsigned char iAP_generalLingo_GetDevAuthenticationSignature_pkt[24];
int iAP_generalLingo_GetDevAuthenticationSignature_transid;
int iAP_generalLingo_GetDevAuthenticationSignature_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_generalLingo_GetDevAuthenticationSignature_flag = 1;
  iAP_generalLingo_GetDevAuthenticationSignature_transid = transid;
  iAP_generalLingo_GetDevAuthenticationSignature_pktlen = pktlen;
  for (int i=0; i<pktlen; i++)
    iAP_generalLingo_GetDevAuthenticationSignature_pkt[i] = pkt[i];
  return 1;
}

int iAP_generalLingo_GetDevAuthenticationSignature_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_generalLingo_GetDevAuthenticationSignature_flag)
  {
    iAP_generalLingo_GetDevAuthenticationSignature_flag = 0;
    cCtrl <: CMD_generalLingo_GetDevAuthenticationSignature;
    cCtrl <: iAP_generalLingo_GetDevAuthenticationSignature_transid;
    cCtrl <: iAP_generalLingo_GetDevAuthenticationSignature_pktlen-1;
    for (int i=0; i<iAP_generalLingo_GetDevAuthenticationSignature_pktlen-1; i++)
      cCtrl <: (char)iAP_generalLingo_GetDevAuthenticationSignature_pkt[i];
    cCtrl <: (unsigned)iAP_generalLingo_GetDevAuthenticationSignature_pkt[iAP_generalLingo_GetDevAuthenticationSignature_pktlen-1];
    return 1;
  }
  return 0;
}

// --------------------------- NotifyiPodStateChange -------------------------------

int iAP_generalLingo_NotifyiPodStateChange_flag = 0;
int iAP_generalLingo_NotifyiPodStateChange_schange = 0;
int iAP_generalLingo_NotifyiPodStateChange_transid;
int iAP_generalLingo_NotifyiPodStateChange_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_generalLingo_NotifyiPodStateChange_flag = 1;
  iAP_generalLingo_NotifyiPodStateChange_transid = transid;
  iAP_generalLingo_NotifyiPodStateChange_schange = (unsigned)pkt[0];
  return 1;
}

int iAP_generalLingo_NotifyiPodStateChange_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_generalLingo_NotifyiPodStateChange_flag)
  {
    iAP_generalLingo_NotifyiPodStateChange_flag = 0;
    cCtrl <: CMD_generalLingo_NotifyiPodStateChange;
    cCtrl <: iAP_generalLingo_NotifyiPodStateChange_transid;
    cCtrl <: iAP_generalLingo_NotifyiPodStateChange_schange;
    return 1;
  }
  return 0;
}

// --------------------------- GetAccessoryInfo -------------------------------

int iAP_generalLingo_GetAccessoryInfo_flag = 0;
int iAP_generalLingo_GetAccessoryInfo_infoType;
int iAP_generalLingo_GetAccessoryInfo_len;
int iAP_generalLingo_GetAccessoryInfo_transid;
unsigned char iAP_generalLingo_GetAccessoryInfo_parameters[7];
int iAP_generalLingo_GetAccessoryInfo_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_generalLingo_GetAccessoryInfo_flag = 1;
  iAP_generalLingo_GetAccessoryInfo_infoType = (unsigned)pkt[0];
  iAP_generalLingo_GetAccessoryInfo_len = pktlen - 1;
  iAP_generalLingo_GetDevAuthenticationSignature_transid = transid;
  for (int i = 0; i < iAP_generalLingo_GetAccessoryInfo_len; i++)
    iAP_generalLingo_GetAccessoryInfo_parameters[i] = pkt[i + 1];
  iAP_generalLingo_GetAccessoryInfo_transid = transid;
  return 1;
}

int iAP_generalLingo_GetAccessoryInfo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_generalLingo_GetAccessoryInfo_flag)
  {
    iAP_generalLingo_GetAccessoryInfo_flag = 0;
    cCtrl <: CMD_generalLingo_GetAccessoryInfo;
    cCtrl <: iAP_generalLingo_GetAccessoryInfo_transid;
    cCtrl <: iAP_generalLingo_GetAccessoryInfo_infoType;
    cCtrl <: iAP_generalLingo_GetAccessoryInfo_len;
    for (int i = 0; i < iAP_generalLingo_GetAccessoryInfo_len; i++)
      cCtrl <: (char)iAP_generalLingo_GetAccessoryInfo_parameters[i];
    return 1;
  }
  return 0;
}

// --------------------------- Unsolicited checker ----------------------------------

int iAP_generalLingo_unsolicitedrx (streaming_chanend cCtrl, e_generalLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x14):
      return iAP_generalLingo_GetDevAuthenticationInfo_rx (cCtrl, pktlen, pkt, transid);
    case (0x17):
      return iAP_generalLingo_GetDevAuthenticationSignature_rx (cCtrl, pktlen, pkt, transid);
    case (0x23):
      return iAP_generalLingo_NotifyiPodStateChange_rx (cCtrl, pktlen, pkt, transid);
    case (0x27):
      return iAP_generalLingo_GetAccessoryInfo_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }
}

int iAP_generalLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_generalLingo_GetDevAuthenticationInfo_unsolicited(cCtrl))
    return 1;
  if (iAP_generalLingo_GetDevAuthenticationSignature_unsolicited(cCtrl))
    return 1;
  if (iAP_generalLingo_NotifyiPodStateChange_unsolicited(cCtrl))
    return 1;
  if (iAP_generalLingo_GetAccessoryInfo_unsolicited (cCtrl))
    return 1;
  return 0;
}

