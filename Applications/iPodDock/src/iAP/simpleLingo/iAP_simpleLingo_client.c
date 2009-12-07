#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_simpleLingo_CONTEXTBUTTONSTATUS_client:
 * Client command for CONTEXTBUTTONSTATUS
 */
int iAP_simpleLingo_CONTEXTBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus)
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
    out(cCtrl, simpleLingo);
    out(cCtrl, 0);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    buttonstatus = byterev(buttonstatus);
  IntToCharArray(pkt, 0, buttonstatus);
  pktlen = 4;
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
 * iAP_simpleLingo_IMAGEBUTTONSTATUS_client:
 * Client command for IMAGEBUTTONSTATUS
 */
int iAP_simpleLingo_IMAGEBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, simpleLingo);
    out(cCtrl, 2);
  out(cCtrl,2 & 0xFF);
    out(cCtrl, transid);
    
    buttonstatus = byterev(buttonstatus);
  IntToCharArray(pkt, 0, buttonstatus);
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
 * iAP_simpleLingo_VIDEOBUTTONSTATUS_client:
 * Client command for VIDEOBUTTONSTATUS
 */
int iAP_simpleLingo_VIDEOBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, simpleLingo);
    out(cCtrl, 3);
  out(cCtrl,3 & 0xFF);
    out(cCtrl, transid);
    
    buttonstatus = byterev(buttonstatus);
  IntToCharArray(pkt, 0, buttonstatus);
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
 * iAP_simpleLingo_AUDIOBUTTONSTATUS_client:
 * Client command for AUDIOBUTTONSTATUS
 */
int iAP_simpleLingo_AUDIOBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, simpleLingo);
    out(cCtrl, 4);
  out(cCtrl,4 & 0xFF);
    out(cCtrl, transid);
    
    buttonstatus = byterev(buttonstatus);
  IntToCharArray(pkt, 0, buttonstatus);
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



