#include "iAP.h"

// Flags to signal if command has been sent and we are expecting a response
#define NCOMMANDS 0x50
int iAP_locationLingo_flags[NCOMMANDS];

/*
 * iAP_locationLingo_init:
 * Called on iPod connection
 */
#pragma unsafe arrays
void iAP_locationLingo_init()
{
  for (int i=0; i<NCOMMANDS; i++)
  {
    iAP_locationLingo_flags[i] = 0;
  }
}

/*
 * iAP_locationLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
#pragma unsafe arrays
int iAP_locationLingo_rx(streaming_chanend cCtrl, unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid)
{  
  if (cmdID >= NCOMMANDS)
  {
    vprint("Received command ID overflow");
  }

  if ((e_locationLingo_commands)cmdID == locationLingo_ACK)
  {
    int ackcmdid = pkt[1];
    int ackresponse = pkt[0];

    if (ackcmdid >= NCOMMANDS)
    {
      vprint("Received ack command overflow");
    }
    
    if (iAP_locationLingo_flags[(int)ackcmdid])
    {
      if ((int)ackresponse != 0x6) {

      iAP_locationLingo_flags[(int)ackcmdid] = 0;
      cCtrl <: ackresponse;
      cCtrl <: transid;
      transmit_char_array(cCtrl, pktlen, pkt, 0);
      }
    }
    return 1;
  }
  else if (iAP_locationLingo_flags[(int)cmdID])
  {
    iAP_locationLingo_flags[(int)cmdID] = 0;
    cCtrl <: 0;
    cCtrl <: transid;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    return 1;
  }
  else
  {
    if (!iAP_locationLingo_unsolicitedrx(cCtrl, cmdID, pktlen, pkt, transid))
    {
      return 1;
    }    
  }
  return 0;
}

/*
 * iAP_locationLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */
#pragma unsafe arrays
int iAP_locationLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow)
{
  unsigned pktlen;
  unsigned char pkt[512];
  int cmdsendID, cmdrxID, transid;
  
  cCtrl :> cmdsendID; 
  cCtrl :> cmdrxID;
  cCtrl :> transid;
  if (cmdrxID != -1)
  {
    if ((cmdrxID & 0xFF) >= NCOMMANDS)
    {
      vprint("Command overflow");
    }
    iAP_locationLingo_flags[cmdrxID & 0xFF] = timenow;
    cmdrxID = (cmdrxID >> 8) & 0xFF;
    if (cmdrxID)
    {
      if (cmdrxID >= NCOMMANDS)
      {
        vprint("Command overflow");
      }
      iAP_locationLingo_flags[cmdrxID] = timenow;
    }
  }
  
  receive_char_array(cCtrl, pktlen, pkt, 0);
  
  iAP_sendpacket ( lingoID, cmdsendID, pktlen, pkt, transid);
  
  cCtrl <: 0;
  
  return 0;
}

/*
 * iAP_locationLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_locationLingo_timeout(streaming_chanend cCtrl, int timenow)
{
  char dummy[1];
  for (int i=0; i<NCOMMANDS; i++)
  {
    if (iAP_locationLingo_flags[i])
    {
      if (timenow - iAP_locationLingo_flags[i] > IAP_TIMEOUT)
      {
        cCtrl <: -1;
        cCtrl <: -1;
        transmit_char_array(cCtrl, 0, dummy, 0);
        iAP_locationLingo_flags[i] = 0;
      }
    }
  }
}

/*
 * iAP_locationLingo_flush:
 * Called on iPod disconnection
 */
#pragma unsafe arrays
void iAP_locationLingo_flush(streaming_chanend cCtrl)
{
  for (int i=0; i<NCOMMANDS; i++)
  {
    if (iAP_locationLingo_flags[i])
    {
      cCtrl <: 1;
      iAP_locationLingo_flags[i] = 0;
    }
  }
}
