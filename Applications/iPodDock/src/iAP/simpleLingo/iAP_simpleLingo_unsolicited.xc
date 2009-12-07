#include "iAP.h"

// --------------------------- Unsolicited checker ----------------------------------

int iAP_simpleLingo_unsolicitedrx (streaming_chanend cCtrl, e_simpleLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid)
{
  // No unsolicited commands
  return 0;
}

int iAP_simpleLingo_unsolicited (streaming_chanend cCtrl)
{
  // No unsolicited commands
  return 0;
}
