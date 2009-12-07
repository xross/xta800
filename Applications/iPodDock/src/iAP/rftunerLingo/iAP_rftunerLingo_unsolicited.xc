#include "iAP.h"

// --------------------------- Unsolicited checker ----------------------------------

int iAP_rftunerLingo_unsolicitedrx (streaming_chanend cCtrl, e_rftunerLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  // No unsolicited commands
  return 0;
}

int iAP_rftunerLingo_unsolicited (streaming_chanend cCtrl)
{
  // No unsolicited commands
  return 0;
}
