#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_audioLingo_RetAccSampleRateCaps_client:
 * Client command for RetAccSampleRateCaps
 */
int iAP_audioLingo_RetAccSampleRateCaps_client(streaming_chanend cCtrl, int transid, int nrates, int rates[])
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, audioLingo);
    out(cCtrl, 0x03);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    smemcpy(pkt, 0, ((char*)rates), 0, nrates*4);
  pktlen = nrates * 4;
  __noresponse = 1;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_audioLingo_SetVideoDelay_client:
 * Client command for SetVideoDelay
 */
int iAP_audioLingo_SetVideoDelay_client(streaming_chanend cCtrl, int transid, int videodelay, REFERENCE_PARAM(int, retval))
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, audioLingo);
    out(cCtrl, 0x05);
  out(cCtrl,0x05 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, videodelay);
  pktlen = 4;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        *retval = (int)pkt[0];
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}


/*
 * iAP_audioLingo_AccAck_client:
 * Client command for AccAck
 */
int iAP_audioLingo_AccAck_client(streaming_chanend cCtrl, int transid, int cmdstatus, int cmdid)
{
  unsigned pktlen;
  unsigned char pkt[512];
  int __retval=0;
  int __noresponse=0;
  int __transid;
  int i;
  i=0;
  out(cCtrl, 0);
  __retval = in(cCtrl);
  if (!__retval)
  {
    out(cCtrl, audioLingo);
    out(cCtrl, 0x00);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)cmdstatus;
  pkt[1] = (char)cmdid;
  pktlen = 2;
  __noresponse = 1;
    transmit_char_array(cCtrl, pktlen, pkt, 0);
    in(cCtrl);
      
    if (!__noresponse)
    {
      // Response
      __retval = in(cCtrl);
      // Transaction ID
      __transid = in(cCtrl);
      
      receive_char_array(cCtrl, &pktlen, pkt, 0);
      if (__retval == -1)
      {
        return 1;
      }
      else if (__transid == transid)
      {
        
        return 0;
      }
      else
      {
        return 1;
      }
    }
    else
    {
      return 0;
    }
  }
  return __retval;
}



