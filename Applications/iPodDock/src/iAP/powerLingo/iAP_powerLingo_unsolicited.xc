#include "iAP.h"

// --------------------------- iAP_powerLingo_BeginHighPower_rx -------------------------------

int iAP_powerLingo_BeginHighPower_rx_flag = 0;
int iAP_powerLingo_BeginHighPower_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_powerLingo_BeginHighPower_rx_flag = 1;
  return 1;
}

int iAP_powerLingo_BeginHighPower_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_powerLingo_BeginHighPower_rx_flag)
  {
    iAP_powerLingo_BeginHighPower_rx_flag = 0;
    cCtrl <: CMD_powerLingo_BeginHighPower;
    return 1;
  }
  return 0;
}


// --------------------------- iAP_powerLingo_EndHighPower_rx -------------------------------

int iAP_powerLingo_EndHighPower_rx_flag = 0;
int iAP_powerLingo_EndHighPower_rx (streaming_chanend cCtrl
    , unsigned pktlen, unsigned char pkt[], int transid)
{
  iAP_powerLingo_EndHighPower_rx_flag = 1;
  return 1;
}

int iAP_powerLingo_EndHighPower_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_powerLingo_EndHighPower_rx_flag)
  {
    iAP_powerLingo_EndHighPower_rx_flag = 0;
    cCtrl <: CMD_powerLingo_EndHighPower;
    return 1;
  }
  return 0;
}

// --------------------------- Unsolicited checker ----------------------------------

int iAP_powerLingo_unsolicitedrx (streaming_chanend cCtrl, e_powerLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  switch (cmdID)
  {
    case (0x02):
      return iAP_powerLingo_BeginHighPower_rx (cCtrl, pktlen, pkt, transid);
    case (0x03):
      return iAP_powerLingo_EndHighPower_rx (cCtrl, pktlen, pkt, transid);
    default:
      return 0;
  }  return 0;
}

int iAP_powerLingo_unsolicited (streaming_chanend cCtrl)
{
  if (iAP_powerLingo_BeginHighPower_unsolicited(cCtrl))
    return 1;
  if (iAP_powerLingo_EndHighPower_unsolicited(cCtrl))
    return 1;
  return 0;
}
