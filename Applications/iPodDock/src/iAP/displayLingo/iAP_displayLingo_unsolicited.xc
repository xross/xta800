#include "iAP.h"


// --------------------------- GetDevAuthenticationInfo -------------------------------

int iAP_displayLingo_RemoteEventNotification_flag = 0;
int iAP_displayLingo_RemoteEventNotification_datalen;
int iAP_displayLingo_RemoteEventNotification_transid;
char iAP_displayLingo_RemoteEventNotification_data[8];
int iAP_displayLingo_RemoteEventNotification_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_displayLingo_RemoteEventNotification_flag = 1;
  for (int i=0; i<pktlen; i++)
    iAP_displayLingo_RemoteEventNotification_data[i] = pkt[i];
  iAP_displayLingo_RemoteEventNotification_datalen = pktlen;
  iAP_displayLingo_RemoteEventNotification_transid = transid;
  return 1;
}

int iAP_displayLingo_RemoteEventNotification_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_displayLingo_RemoteEventNotification_flag)
  {
    iAP_displayLingo_RemoteEventNotification_flag = 0;
    cCtrl <: CMD_displayLingo_RemoteEventNotification;
    cCtrl <: iAP_displayLingo_RemoteEventNotification_transid;
    cCtrl <: iAP_displayLingo_RemoteEventNotification_datalen;
    for (int i=0; i<iAP_displayLingo_RemoteEventNotification_datalen; i++)
    {
      cCtrl <: (char)iAP_displayLingo_RemoteEventNotification_data[i];
    }
    return 1;
  }
  return 0;
}

// --------------------------- Unsolicited checker ----------------------------------

int iAP_displayLingo_unsolicitedrx (streaming_chanend cCtrl, e_displayLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x09):
      return iAP_displayLingo_RemoteEventNotification_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }
}

int iAP_displayLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_displayLingo_RemoteEventNotification_unsolicited(cCtrl))
    return 1;
  return 0;
}
