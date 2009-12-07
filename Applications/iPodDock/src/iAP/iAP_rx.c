#include "iAP.h"

// This is modified by reception of a General Lingo ACK command
int iAP_expect_transid = 0;

/*
 * iAP_rx:
 * Function when receiving iAP packets from the HID layer
 */
int iAP_rx(streaming_chanend cCtrl, unsigned char *pkt,
    unsigned pktlen)
{
  int i;
  e_lingos lingoID;
  unsigned char cmdID;
  signed char checksum;
  int transid;

  // Look for start byte  
  while (pktlen && pkt[0] != 0x55)
  {
    pkt++;
    pktlen--;
  }
  
  // Sanity check pktlen
  if (pktlen < 6)
    return 1;

  // Sanity check payload size
  if (pkt[1] > pktlen - 2)
    return 1;
  if (pkt[1] < 2)
    return 1;

  
  // Verify checksum
  checksum = 0;
  for (i = 1; i < pktlen; i++)
  {
    checksum += pkt[i];
  }
  if (checksum != 0)
    return 1;
  
  pktlen = pkt[1];
  lingoID = pkt[2];
  if (lingoID == extendedLingo)
  {
    cmdID = (pkt[3] << 8) | pkt[4];
    pkt += 5;
    pktlen -= 3;
  }
  else
  {
    cmdID = pkt[3];
    pkt += 4;
    pktlen -= 2;    
  }
  
  // Special case GENLINGO_ACK
  if (lingoID == generalLingo && cmdID == generalLingo_ACK)
  {
    if ((pktlen == 0x02) || (pktlen == 0x6))
    {
      iAP_expect_transid = 0;
    }
    else if ((pktlen == 0x04) || (pktlen == 0x8))
    {
      iAP_expect_transid = 1;
    }
    else
    {
      vprint("Unexpected GL_ACK pktlen");
    }
  }

  if (iAP_expect_transid)
  {
    transid = ((pkt[0] << 8) | pkt[1]);
    pktlen -= 2;
    pkt += 2;
  }
  else
  {
    transid = -1;
  }
  
  switch (lingoID)
  {
#if GENERAL_LINGO != 0
    case (generalLingo):
      return iAP_generalLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if MICROPHONE_LINGO != 0
    case (microphoneLingo):
      return iAP_microphoneLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if SIMPLE_REMOTE_LINGO != 0
    case (simpleLingo):
      return iAP_simpleLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if DISPLAY_REMOTE_LINGO != 0
    case (displayLingo):
      return iAP_displayLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif
#if EXT_REMOTE_LINGO != 0
    case (extendedLingo):
      return iAP_extendedLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif
#if POWER_LINGO != 0
    case (powerLingo):
      return iAP_powerLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if SPORTS_LINGO != 0
    case (sportsLingo):
      return iAP_sportsLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if EQUALIZER_LINGO != 0
    case (equalizerLingo):
      return iAP_equalizerLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if RF_TUNER_LINGO != 0
    case (rftunerLingo):
      return iAP_rftunerLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if AUDIO_LINGO != 0
    case (audioLingo):
      return iAP_audioLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if STORAGE_LINGO != 0
    case (storageLingo):
      return iAP_storageLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
#if LOCATION_LINGO != 0
    case (locationLingo):
      return iAP_locationLingo_rx(cCtrl,
          cmdID, pktlen, pkt, transid);
#endif  
    default:
      return 1;
  }
}
