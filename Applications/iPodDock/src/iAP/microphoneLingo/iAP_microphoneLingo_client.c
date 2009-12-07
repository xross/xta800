#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_microphoneLingo_RetDevCaps_client:
 * Client command for RetDevCaps
 */
int iAP_microphoneLingo_RetDevCaps_client(streaming_chanend cCtrl, int transid, int stereoLineInput, int stereoSwitchingInput, int recordingLevelPresent, int recordingLevelLimitPresent, int duplexAudio)
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
    out(cCtrl, microphoneLingo);
    out(cCtrl, 0x08);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = ((stereoLineInput & 1) << 0) | ((stereoSwitchingInput & 1) << 1) | ((recordingLevelPresent & 1) << 2) | ((recordingLevelLimitPresent & 1) << 3) | ((duplexAudio & 1) << 4);
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
 * iAP_microphoneLingo_RetDevCtrl_client:
 * Client command for RetDevCtrl
 */
int iAP_microphoneLingo_RetDevCtrl_client(streaming_chanend cCtrl, int transid, int controlType, int controlData)
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
    out(cCtrl, microphoneLingo);
    out(cCtrl, 0x0A);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = (char)controlType;
  pkt[1] = (char)controlData;
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



