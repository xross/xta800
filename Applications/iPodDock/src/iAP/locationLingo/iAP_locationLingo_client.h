#ifndef __IAP_locationLingo_CLIENT_H__

#define __IAP_locationLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_locationLingo_RetDevCaps_System_client:
 * Client command for RetDevCaps_System
 */
int iAP_locationLingo_RetDevCaps_System_client(streaming_chanend cCtrl, int transid, int devVerMajor, int devVerMinor, int PowerManageControlSupport, int AsyncLocationNotificationSupport, int LocationSystemSupport, int LocationNMEASupport, int LocationAssSupport);

/*
 * iAP_locationLingo_RetDevCaps_NMEA_client:
 * Client command for RetDevCaps_NMEA
 */
int iAP_locationLingo_RetDevCaps_NMEA_client(streaming_chanend cCtrl, int transid, int nmeaGpsFiltering);

/*
 * iAP_locationLingo_RetDevCaps_LocAss_client:
 * Client command for RetDevCaps_LocAss
 */
int iAP_locationLingo_RetDevCaps_LocAss_client(streaming_chanend cCtrl, int transid, int satelliteEphRequired, int satEphURLString, int satEphDataMaxRefreshInterval, int satEphDataRecRefreshInterval);


#endif //__IAP_locationLingo_CLIENT_H__

