#include "iAP.h"

// --------------------------- iAP_microphoneLingo_GetDevAck_rx -------------------------------

int iAP_microphoneLingo_GetDevAck_flag = 0;
int iAP_microphoneLingo_GetDevAck_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_microphoneLingo_GetDevAck_flag = 1;
  return 1;
}

int iAP_microphoneLingo_GetDevAck_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_GetDevAck_flag)
  {
    iAP_microphoneLingo_GetDevAck_flag = 0;
    cCtrl <: CMD_microphoneLingo_GetDevAck;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_microphoneLingo_iPodModeChange_rx -------------------------------

int iAP_microphoneLingo_iPodModeChange_flag = 0;
char iAP_microphoneLingo_iPodModeChange_mode = 0;
int iAP_microphoneLingo_iPodModeChange_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_microphoneLingo_iPodModeChange_flag = 1;
  iAP_microphoneLingo_iPodModeChange_mode = pkt[0];
  return 1;
}

int iAP_microphoneLingo_iPodModeChange_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_iPodModeChange_flag)
  {
    iAP_microphoneLingo_iPodModeChange_flag = 0;
    cCtrl <: CMD_microphoneLingo_iPodModeChange;
    cCtrl <: iAP_microphoneLingo_iPodModeChange_mode;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_microphoneLingo_GetDevCaps_rx -------------------------------

int iAP_microphoneLingo_GetDevCaps_flag = 0;
int iAP_microphoneLingo_GetDevCaps_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_microphoneLingo_GetDevCaps_flag = 1;
  return 1;
}

int iAP_microphoneLingo_GetDevCaps_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_iPodModeChange_flag)
  {
    iAP_microphoneLingo_GetDevCaps_flag = 0;
    cCtrl <: CMD_microphoneLingo_GetDevCaps;
    return 1;
  }
  return 0;
}

// --------------------------- iAP_microphoneLingo_GetDevCtrl_rx -------------------------------

int iAP_microphoneLingo_GetDevCtrl_flag = 0;
char iAP_microphoneLingo_GetDevCtrl_numcontroltype;
char iAP_microphoneLingo_GetDevCtrl_controltype[8];
int iAP_microphoneLingo_GetDevCtrl_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_microphoneLingo_GetDevCtrl_flag = 1;
  iAP_microphoneLingo_GetDevCtrl_numcontroltype = pktlen;
  for (int i=0; i<pktlen; i++)
    iAP_microphoneLingo_GetDevCtrl_controltype[i] = pkt[i];
  return 1;
}

int iAP_microphoneLingo_GetDevCtrl_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_GetDevCtrl_flag)
  {
    iAP_microphoneLingo_GetDevCtrl_flag = 0;
    cCtrl <: CMD_microphoneLingo_GetDevCtrl;
    cCtrl <: iAP_microphoneLingo_GetDevCtrl_numcontroltype;
    for (int i=0; i<iAP_microphoneLingo_GetDevCtrl_numcontroltype; i++)
      cCtrl <: iAP_microphoneLingo_GetDevCtrl_controltype[i];
    return 1;
  }
  return 0;
}


// --------------------------- iAP_microphoneLingo_SetDevCtrl_rx -------------------------------

int iAP_microphoneLingo_SetDevCtrl_flag = 0;
char iAP_microphoneLingo_SetDevCtrl_numcontroltype;
char iAP_microphoneLingo_SetDevCtrl_controltype[8];
int iAP_microphoneLingo_SetDevCtrl_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_microphoneLingo_SetDevCtrl_flag = 1;
  iAP_microphoneLingo_SetDevCtrl_numcontroltype = pktlen;
  for (int i=0; i<pktlen; i++)
    iAP_microphoneLingo_SetDevCtrl_controltype[i] = pkt[i];
  return 1;
}

int iAP_microphoneLingo_SetDevCtrl_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_SetDevCtrl_flag)
  {
    iAP_microphoneLingo_SetDevCtrl_flag = 0;
    cCtrl <: CMD_microphoneLingo_SetDevCtrl;
    cCtrl <: iAP_microphoneLingo_SetDevCtrl_numcontroltype;
    for (int i=0; i<iAP_microphoneLingo_SetDevCtrl_numcontroltype; i++)
      cCtrl <: iAP_microphoneLingo_SetDevCtrl_controltype[i];
    return 1;
  }
  return 0;
}
// --------------------------- Unsolicited checker ----------------------------------

int iAP_microphoneLingo_unsolicitedrx (streaming_chanend cCtrl, e_microphoneLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x05):
      return iAP_microphoneLingo_GetDevAck_rx (cCtrl, pktlen, pkt, transid);
    case (0x06):
      return iAP_microphoneLingo_iPodModeChange_rx (cCtrl, pktlen, pkt, transid);
    case (0x07):
      return iAP_microphoneLingo_GetDevCaps_rx (cCtrl, pktlen, pkt, transid);
    case (0x09):
      return iAP_microphoneLingo_GetDevCtrl_rx (cCtrl, pktlen, pkt, transid);
    case (0x0B):
      return iAP_microphoneLingo_SetDevCtrl_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }
  return 0;
}

int iAP_microphoneLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_microphoneLingo_GetDevAck_unsolicited(cCtrl))
    return 1;
  if (iAP_microphoneLingo_iPodModeChange_unsolicited(cCtrl))
    return 1;
  if (iAP_microphoneLingo_GetDevCaps_unsolicited(cCtrl))
    return 1;
  if (iAP_microphoneLingo_GetDevCtrl_unsolicited(cCtrl))
    return 1;
  if (iAP_microphoneLingo_SetDevCtrl_unsolicited(cCtrl))
    return 1;
  return 0;
  return 0;
}
