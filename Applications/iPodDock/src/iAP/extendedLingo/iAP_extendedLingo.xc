#include "iAP.h"

// Flags to signal if command has been sent and we are expecting a response
#define NCOMMANDS 0x48
int iAP_extendedLingo_flags[NCOMMANDS];

/*
 * iAP_extendedLingo_init:
 * Called on iPod connection
 */
#pragma unsafe arrays
void iAP_extendedLingo_init()
{
  for (int i=0; i<NCOMMANDS; i++)
  {
    iAP_extendedLingo_flags[i] = 0;
  }
}

/*
 * iAP_extendedLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
#pragma unsafe arrays
int iAP_extendedLingo_rx(streaming_chanend cCtrl, unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid)
{  
  if (cmdID >= NCOMMANDS)
  {
    vprint("Received command ID overflow");
  }

  if ((e_extendedLingo_commands)cmdID == extendedLingo_ACK)
  {
    int ackcmdid = pkt[1];
    int ackresponse = pkt[0];
     ackcmdid = (pkt[1] << 8) + (pkt[2]);

    if (ackcmdid >= NCOMMANDS)
    {
      vprint("Received ack command overflow");
    }
    
    if (iAP_extendedLingo_flags[(int)ackcmdid])
    {
      if ((int)ackresponse != 0x6) {

      iAP_extendedLingo_flags[(int)ackcmdid] = 0;
      cCtrl <: ackresponse;
      cCtrl <: transid;
      transmit_char_array(cCtrl, pktlen, pkt, 0);
      }
    }
    return 1;
  }
  else if (iAP_extendedLingo_flags[(int)cmdID])
  {
    iAP_extendedLingo_flags[(int)cmdID] = 0;
    cCtrl <: 0;
    cCtrl <: transid;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    return 1;
  }
  else
  {
    if (!iAP_extendedLingo_unsolicitedrx(cCtrl, cmdID, pktlen, pkt, transid))
    {
      return 1;
    }    
  }
  return 0;
}

/*
 * iAP_extendedLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */
#pragma unsafe arrays
int iAP_extendedLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow)
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
    iAP_extendedLingo_flags[cmdrxID & 0xFF] = timenow;
    cmdrxID = (cmdrxID >> 8) & 0xFF;
    if (cmdrxID)
    {
      if (cmdrxID >= NCOMMANDS)
      {
        vprint("Command overflow");
      }
      iAP_extendedLingo_flags[cmdrxID] = timenow;
    }
  }
  
  receive_char_array(cCtrl, pktlen, pkt, 0);
  
  iAP_sendpacket ( lingoID, cmdsendID, pktlen, pkt, transid);
  
  cCtrl <: 0;
  
  return 0;
}

/*
 * iAP_extendedLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_extendedLingo_timeout(streaming_chanend cCtrl, int timenow)
{
  char dummy[1];
  for (int i=0; i<NCOMMANDS; i++)
  {
    if (iAP_extendedLingo_flags[i])
    {
      if (timenow - iAP_extendedLingo_flags[i] > IAP_TIMEOUT)
      {
        cCtrl <: -1;
        cCtrl <: -1;
        transmit_char_array(cCtrl, 0, dummy, 0);
        iAP_extendedLingo_flags[i] = 0;
      }
    }
  }
}

/*
 * iAP_extendedLingo_flush:
 * Called on iPod disconnection
 */
#pragma unsafe arrays
void iAP_extendedLingo_flush(streaming_chanend cCtrl)
{
  for (int i=0; i<NCOMMANDS; i++)
  {
    if (iAP_extendedLingo_flags[i])
    {
      cCtrl <: 1;
      iAP_extendedLingo_flags[i] = 0;
    }
  }
}
