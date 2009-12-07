#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_generalLingo_RequestRemoteUIMode_client:
 * Client command for RequestRemoteUIMode
 */
int iAP_generalLingo_RequestRemoteUIMode_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, rxmode))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 3);
  out(cCtrl,4 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *rxmode = (int)pkt[0];
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
 * iAP_generalLingo_EnterRemoteUIMode_client:
 * Client command for EnterRemoteUIMode
 */
int iAP_generalLingo_EnterRemoteUIMode_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 5);
  out(cCtrl,5 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
 * iAP_generalLingo_RequestiPodName_client:
 * Client command for RequestiPodName
 */
int iAP_generalLingo_RequestiPodName_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retlen), unsigned char ret[])
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
    out(cCtrl, generalLingo);
    out(cCtrl, 7);
  out(cCtrl,8 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *retlen = pktlen;
  smemcpy(ret, 0, pkt, 0, *retlen);
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
 * iAP_generalLingo_RequestiPodSoftwareVersion_client:
 * Client command for RequestiPodSoftwareVersion
 */
int iAP_generalLingo_RequestiPodSoftwareVersion_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, majorversion), REFERENCE_PARAM(int, minorversion), REFERENCE_PARAM(int, revisionversion))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x09);
  out(cCtrl,0x0A & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *majorversion = (int)pkt[0];
  *minorversion = (int)pkt[1];
  *revisionversion = (int)pkt[2];
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
 * iAP_generalLingo_RequestiPodSerialNum_client:
 * Client command for RequestiPodSerialNum
 */
int iAP_generalLingo_RequestiPodSerialNum_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retlen), unsigned char ret[])
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0B);
  out(cCtrl,0x0C & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *retlen = pktlen;
  smemcpy(ret, 0, pkt, 0, *retlen);
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
 * iAP_generalLingo_RequestiPodModelNum_client:
 * Client command for RequestiPodModelNum
 */
int iAP_generalLingo_RequestiPodModelNum_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, modelid))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0D);
  out(cCtrl,0x0E & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *modelid = CharArrayToInt(pkt, 0);
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
 * iAP_generalLingo_RequestLingoProtocolVersion_client:
 * Client command for RequestLingoProtocolVersion
 */
int iAP_generalLingo_RequestLingoProtocolVersion_client(streaming_chanend cCtrl, int transid, int lingoid, REFERENCE_PARAM(int, majorversion), REFERENCE_PARAM(int, minorversion))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0F);
  out(cCtrl,0x10 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)lingoid;
  pktlen = 1;
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
        *majorversion = (int)pkt[1];
  *minorversion = (int)pkt[2];
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
 * iAP_generalLingo_IdentifyDeviceLingos_client:
 * Client command for IdentifyDeviceLingos
 */
int iAP_generalLingo_IdentifyDeviceLingos_client(streaming_chanend cCtrl, int transid, int lingosSpoken, int options, int deviceID, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x13);
  out(cCtrl,0x13 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, lingosSpoken);
  IntToCharArray(pkt, 4, options);
  IntToCharArray(pkt, 8, deviceID);
  pktlen = 12;
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
 * iAP_generalLingo_RetDevAuthenticationInfo_client:
 * Client command for RetDevAuthenticationInfo
 */
int iAP_generalLingo_RetDevAuthenticationInfo_client(streaming_chanend cCtrl, int transid, int authversion, int x509currentindex, int x509maxindex, int x509datalen, int x509dataptr, unsigned char x509data[], REFERENCE_PARAM(int, authstatus))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x15);
  out(cCtrl,((0x15 & 0xFF) << 8) + (0x16 & 0xFF));
    out(cCtrl, transid);
    
    ShortToCharArray(pkt, 0, authversion);
  pkt[2] = (char)x509currentindex;
  pkt[3] =  (char)x509maxindex;
  for (i=0; i<x509datalen; i++)
  pkt[4 + i] = (char)x509data[i + x509dataptr];
  pktlen = 4 + x509datalen;
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
        *authstatus = (int)pkt[0];
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
 * iAP_generalLingo_RetDevAuthenticationSignature_client:
 * Client command for RetDevAuthenticationSignature
 */
int iAP_generalLingo_RetDevAuthenticationSignature_client(streaming_chanend cCtrl, int transid, int siglen, unsigned char sig[], REFERENCE_PARAM(int, authstatus))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x18);
  out(cCtrl,0x19 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = siglen;
  smemcpy(pkt, 0, sig, 0, pktlen);
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
        *authstatus = pkt[0];
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
 * iAP_generalLingo_GetiPodAuthenticationInfo_client:
 * Client command for GetiPodAuthenticationInfo
 */
int iAP_generalLingo_GetiPodAuthenticationInfo_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, authversion), REFERENCE_PARAM(int, x509currentindex),REFERENCE_PARAM(int, x509maxindex), REFERENCE_PARAM(int, x509datalen), unsigned char x509data[])
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x1A);
  out(cCtrl,0x1B & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *authversion = CharArrayToShort(pkt, 0);
  *x509currentindex = (int)pkt[2];
  *x509maxindex = pkt[3];
  *x509datalen = pktlen - 4;
  for (i=0; i<*x509datalen; i++)
  x509data[i] = pkt[4 + i];
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
 * iAP_generalLingo_AckiPodAuthenticationInfo_client:
 * Client command for AckiPodAuthenticationInfo
 */
int iAP_generalLingo_AckiPodAuthenticationInfo_client(streaming_chanend cCtrl, int transid, int ackstatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x1C);
  out(cCtrl,0x1C & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)ackstatus;
  pktlen=1;
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
        *retval = pkt[0];
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
 * iAP_generalLingo_GetiPodAuthenticationSignature_client:
 * Client command for GetiPodAuthenticationSignature
 */
int iAP_generalLingo_GetiPodAuthenticationSignature_client(streaming_chanend cCtrl, int transid, int challengelen, unsigned char challenge[], int retrycounter, REFERENCE_PARAM(int, siglen), unsigned char sig[])
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x1D);
  out(cCtrl,0x1E & 0xFF);
    out(cCtrl, transid);
    
    smemcpy(pkt, 0, challenge, 0, challengelen);
  pkt[challengelen] = (char)retrycounter;
  pktlen = challengelen+1;
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
        smemcpy(sig, 0, pkt, 0, pktlen);
  *siglen = pktlen;
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
 * iAP_generalLingo_AckiPodAuthenticationStatus_client:
 * Client command for AckiPodAuthenticationStatus
 */
int iAP_generalLingo_AckiPodAuthenticationStatus_client(streaming_chanend cCtrl, int transid, int ackstatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x1F);
  out(cCtrl,0x1F & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)ackstatus;
  pktlen=1;
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
 * iAP_generalLingo_GetiPodOptions_client:
 * Client command for GetiPodOptions
 */
int iAP_generalLingo_GetiPodOptions_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, options))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x24);
  out(cCtrl,0x25 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
        *options = (int)pkt[7];
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
 * iAP_generalLingo_RetAccessoryInfo_0_client:
 * Client command for RetAccessoryInfo_0
 */
int iAP_generalLingo_RetAccessoryInfo_0_client(streaming_chanend cCtrl, int transid, int accessoryInfoType, int accessoryCapabilities, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0028);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)accessoryInfoType;
  IntToCharArray(pkt, 1, accessoryCapabilities);
  pktlen = 5;
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
 * iAP_generalLingo_RetAccessoryInfo_1_client:
 * Client command for RetAccessoryInfo_1
 */
int iAP_generalLingo_RetAccessoryInfo_1_client(streaming_chanend cCtrl, int transid, int infoType, int len, char name[], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0128);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)infoType;
  smemcpy(pkt, 1, name, 0, len);
  pktlen = len + 1;
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
 * iAP_generalLingo_RetAccessoryInfo_4_client:
 * Client command for RetAccessoryInfo_4
 */
int iAP_generalLingo_RetAccessoryInfo_4_client(streaming_chanend cCtrl, int transid, int infotype, int version[3], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x0428);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)infotype;
  pkt[1] = (char)version[0];
  pkt[2] = (char)version[1];
  pkt[3] = (char)version[2];
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
 * iAP_generalLingo_SetiPodPreferences_client:
 * Client command for SetiPodPreferences
 */
int iAP_generalLingo_SetiPodPreferences_client(streaming_chanend cCtrl, int transid, int preferenceClassID, int preferenceSettingID, int restoreOnExit, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x2B);
  out(cCtrl,0x2B & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)preferenceClassID;
  pkt[1] = (char)preferenceSettingID;
  pkt[2] = (char)restoreOnExit;
  pktlen = 3;
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
 * iAP_generalLingo_StartIDPS_client:
 * Client command for StartIDPS
 */
int iAP_generalLingo_StartIDPS_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x38);
  out(cCtrl,0x38 & 0xFF);
    out(cCtrl, transid);
    
    pktlen = 0;
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
 * iAP_generalLingo_SetFIDTokenValues_Identify_client:
 * Client command for SetFIDTokenValues_Identify
 */
int iAP_generalLingo_SetFIDTokenValues_Identify_client(streaming_chanend cCtrl, int transid, int numLingos, char lingos[], int options, int deviceid, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x139);
  out(cCtrl,0x13A & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = 1;
  pkt[2] = 0;
  pkt[3] = 0;
  pkt[4] = (char)numLingos;
  smemcpy(pkt, 5, lingos, 0, numLingos);
  IntToCharArray(pkt, 5 + pkt[4], options);
  IntToCharArray(pkt, 9 + pkt[4], deviceid);
  pktlen = 13 + pkt[4];
  pkt[1] = 11 + pkt[4];
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
        *retval = (int)pkt[4];
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
 * iAP_generalLingo_SetFIDTokenValues_accCaps_client:
 * Client command for SetFIDTokenValues_accCaps
 */
int iAP_generalLingo_SetFIDTokenValues_accCaps_client(streaming_chanend cCtrl, int transid, int analog_line_out, int analog_line_in, int analog_video_out, int usb_audio, int iphone_os_app, int ipod_volume, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x239);
  out(cCtrl,0x23A & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = 1;
  pkt[1] = 0x0A;
  pkt[2] = 0x0;
  pkt[3] = 0x1;
  pktlen =  (analog_line_out & 1) | ((analog_line_in & 1) << 1) | ((analog_video_out & 1) << 2) | ((usb_audio & 1) << 4) | ((iphone_os_app & 1) << 9) | ((ipod_volume & 1) << 11);
  IntToCharArray(pkt, 4, 0);
  IntToCharArray(pkt, 8,pktlen);
  pktlen = 12;
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
        *retval = (int)pkt[4];
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
 * iAP_generalLingo_SetFIDTokenValues_accinfo_client:
 * Client command for SetFIDTokenValues_accinfo
 */
int iAP_generalLingo_SetFIDTokenValues_accinfo_client(streaming_chanend cCtrl, int transid, int accInfoType, int len, char s[], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x339);
  out(cCtrl,0x33A & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = 1;
  pkt[2] = 0x0;
  pkt[3] = 0x02;
  pkt[4] = (char)accInfoType;
  smemcpy(pkt, 5, s, 0, len);
  pkt[1] = 3 + len;
  pktlen = len + 5;
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
        *retval = (int)pkt[4];
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
 * iAP_generalLingo_SetFIDTokenValues_iPodPreference_client:
 * Client command for SetFIDTokenValues_iPodPreference
 */
int iAP_generalLingo_SetFIDTokenValues_iPodPreference_client(streaming_chanend cCtrl, int transid, char prefClassID, char prefSettingID, char restoreOnExit, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x439);
  out(cCtrl,0x43A & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = 1;
  pkt[1] = 0x05;
  pkt[2] = 0x00;
  pkt[3] = 0x03;
  pkt[4] = (char)prefClassID;
  pkt[5] = (char)prefSettingID;
  pkt[6] = (char)restoreOnExit;
  pktlen = 7;
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
        *retval = (int)pkt[4];
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
 * iAP_generalLingo_SetFIDTokenValues_bundleSeedIDPref_client:
 * Client command for SetFIDTokenValues_bundleSeedIDPref
 */
int iAP_generalLingo_SetFIDTokenValues_bundleSeedIDPref_client(streaming_chanend cCtrl, int transid, char s[11], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x539);
  out(cCtrl,0x53A & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = 1;
  pkt[1] = 0x0D;
  pkt[2] = 0x0;
  pkt[3] = 0x05;
  pktlen = 0x0B + 3;
  smemcpy(pkt,4, s, 0, 11);
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
        *retval = (int)pkt[4];
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
 * iAP_generalLingo_EndIDPS_client:
 * Client command for EndIDPS
 */
int iAP_generalLingo_EndIDPS_client(streaming_chanend cCtrl, int transid, int accEndIDPSStatus, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, generalLingo);
    out(cCtrl, 0x3B);
  out(cCtrl,0x3C & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)accEndIDPSStatus;
  pktlen = 1;
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
        *retval = (unsigned)pkt[0];
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



