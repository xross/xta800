#include "iAP.h"
#include "chanabs.h"

/*
 * iAP_locationLingo_RetDevCaps_System_client:
 * Client command for RetDevCaps_System
 */
int iAP_locationLingo_RetDevCaps_System_client(streaming_chanend cCtrl, int transid, int devVerMajor, int devVerMinor, int PowerManageControlSupport, int AsyncLocationNotificationSupport, int LocationSystemSupport, int LocationNMEASupport, int LocationAssSupport)
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
    out(cCtrl, locationLingo);
    out(cCtrl, 0x02);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = 0x00;
  pkt[1] = (char)devVerMajor;
  pkt[2] = (char)devVerMinor;
  for (i=3; i<10; i++)
  pkt[i]=0;
  pkt[10] = ((PowerManageControlSupport & 1) << 0) | ((AsyncLocationNotificationSupport & 1) << 2);
  for (i=11; i<18; i++)
  pkt[i]=0;
  pkt[18] = ((LocationSystemSupport & 1) << 0) | ((LocationNMEASupport & 1) << 1) | ((LocationAssSupport & 1) << 2);
  __noresponse = 1;
  pktlen =19;
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
 * iAP_locationLingo_RetDevCaps_NMEA_client:
 * Client command for RetDevCaps_NMEA
 */
int iAP_locationLingo_RetDevCaps_NMEA_client(streaming_chanend cCtrl, int transid, int nmeaGpsFiltering)
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
    out(cCtrl, locationLingo);
    out(cCtrl, 0x102);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = 0x01;
  for (i=1; i<8; i++)
  pkt[8] = (nmeaGpsFiltering & 1) << 0;
  __noresponse = 1;
  pktlen = 9;
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
 * iAP_locationLingo_RetDevCaps_LocAss_client:
 * Client command for RetDevCaps_LocAss
 */
int iAP_locationLingo_RetDevCaps_LocAss_client(streaming_chanend cCtrl, int transid, int satelliteEphRequired, int satEphURLString, int satEphDataMaxRefreshInterval, int satEphDataRecRefreshInterval)
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
    out(cCtrl, locationLingo);
    out(cCtrl, 0x202);
  out(cCtrl, -1);
    out(cCtrl, transid);
    
    pkt[0] = 0x02;
  for (i=1; i<8; i++)
  pkt[8] = ((satelliteEphRequired & 1) << 1) | ((satEphURLString & 1) << 2) | ((satEphDataMaxRefreshInterval & 1) << 3) | ((satEphDataRecRefreshInterval & 1) << 4);
  __noresponse = 1;
  pktlen = 9;
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



