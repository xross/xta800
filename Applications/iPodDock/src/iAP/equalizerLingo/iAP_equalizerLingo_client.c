#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_equalizerLingo_Ack_client:
 * Client command for Ack
 */
int iAP_equalizerLingo_Ack_client(streaming_chanend cCtrl, int transid, int status, int cmdid)
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
    out(cCtrl, equalizerLingo);
    out(cCtrl, 0x00);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)status;
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


/*
 * iAP_equalizerLingo_RetCurrentEQIndex_client:
 * Client command for RetCurrentEQIndex
 */
int iAP_equalizerLingo_RetCurrentEQIndex_client(streaming_chanend cCtrl, int transid, int eqIndex)
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
    out(cCtrl, equalizerLingo);
    out(cCtrl, 0x02);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)eqIndex;
  pktlen = 1;
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
 * iAP_equalizerLingo_RetEQSettingCount_client:
 * Client command for RetEQSettingCount
 */
int iAP_equalizerLingo_RetEQSettingCount_client(streaming_chanend cCtrl, int transid, int eqcount)
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
    out(cCtrl, equalizerLingo);
    out(cCtrl, 0x05);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)eqcount;
  pktlen = 1;
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
 * iAP_equalizerLingo_RetEQIndexName_client:
 * Client command for RetEQIndexName
 */
int iAP_equalizerLingo_RetEQIndexName_client(streaming_chanend cCtrl, int transid, int eqIndex, int namelen, unsigned char name[])
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
    out(cCtrl, equalizerLingo);
    out(cCtrl, 0x07);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)eqIndex;
  smemcpy(pkt, 1, name, 0, namelen);
  pktlen = namelen+1;
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



