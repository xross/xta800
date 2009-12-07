#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_storageLingo_GetiPodCaps_client:
 * Client command for GetiPodCaps
 */
int iAP_storageLingo_GetiPodCaps_client(streaming_chanend cCtrl, int transid, int totalSpace[2], REFERENCE_PARAM(int, maxFileSize), REFERENCE_PARAM(int, maxWriteSize), REFERENCE_PARAM(int, version))
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x01);
  out(cCtrl,0x02 & 0xFF);
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
        totalSpace[0] = CharArrayToInt(pkt, 0);
  totalSpace[1] = CharArrayToInt(pkt, 4);
  *maxFileSize = CharArrayToInt(pkt, 8);
  *maxWriteSize = CharArrayToShort(pkt, 12);
  *version = CharArrayToShort(pkt, 14);
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
 * iAP_storageLingo_OpeniPodFeatureFile_client:
 * Client command for OpeniPodFeatureFile
 */
int iAP_storageLingo_OpeniPodFeatureFile_client(streaming_chanend cCtrl, int transid, int featureType, int fileOptionsMask, int filedatalen, unsigned char filedata[], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x12);
  out(cCtrl,0x12 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)featureType;
  IntToCharArray(pkt, 1, fileOptionsMask);
  smemcpy(pkt, 5, filedata, 0, filedatalen);
  pktlen = filedatalen + 5;
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
 * iAP_storageLingo_WriteiPodFileData_client:
 * Client command for WriteiPodFileData
 */
int iAP_storageLingo_WriteiPodFileData_client(streaming_chanend cCtrl, int transid, int offset, int handle, int datalen, unsigned char data[], REFERENCE_PARAM(int, retval))
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x07);
  out(cCtrl,0x07 & 0xFF);
    out(cCtrl, transid);
    
    IntToCharArray(pkt, 0, offset);
  pkt[4] = (char)handle;
  smemcpy(pkt, 5, data, 0,datalen);
  pktlen = datalen + 5;
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
 * iAP_storageLingo_CloseiPodFile_client:
 * Client command for CloseiPodFile
 */
int iAP_storageLingo_CloseiPodFile_client(streaming_chanend cCtrl, int transid, int handle, REFERENCE_PARAM(int, retval))
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x08);
  out(cCtrl,0x08 & 0xFF);
    out(cCtrl, transid);
    
    pkt[0] = (char)handle;
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
 * iAP_storageLingo_GetiPodFreeSpace_client:
 * Client command for GetiPodFreeSpace
 */
int iAP_storageLingo_GetiPodFreeSpace_client(streaming_chanend cCtrl, int transid, int freeSpace[2])
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x10);
  out(cCtrl,0x11 & 0xFF);
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
        freeSpace[0] = CharArrayToInt(pkt, 0);
  freeSpace[1] = CharArrayToInt(pkt, 4);
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
 * iAP_storageLingo_RetDeviceCaps_client:
 * Client command for RetDeviceCaps
 */
int iAP_storageLingo_RetDeviceCaps_client(streaming_chanend cCtrl, int transid, int majorVersion, int minorVersion)
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
    out(cCtrl, storageLingo);
    out(cCtrl, 0x12);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    for (i=0; i<19; i++)
  pkt[i] = 0;
  pkt[19] = 0xFF;
  pkt[20] = (char)majorVersion;
  pkt[21] = (char)minorVersion;
  pktlen = 22;
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



