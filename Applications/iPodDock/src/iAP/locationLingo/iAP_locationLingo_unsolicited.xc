#include "iAP.h"

// --------------------------- Unsolicited checker ----------------------------------

int iAP_locationLingo_unsolicitedrx (streaming_chanend cCtrl, e_locationLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  // No unsolicited commands
  return 0;
}

int iAP_locationLingo_unsolicited (streaming_chanend cCtrl)
{
  // No unsolicited commands
  return 0;
}
