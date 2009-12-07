#ifndef __IAP_storageLingo_CLIENT_H__

#define __IAP_storageLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_storageLingo_GetiPodCaps_client:
 * Client command for GetiPodCaps
 */
int iAP_storageLingo_GetiPodCaps_client(streaming_chanend cCtrl, int transid, int totalSpace[2], REFERENCE_PARAM(int, maxFileSize), REFERENCE_PARAM(int, maxWriteSize), REFERENCE_PARAM(int, version));

/*
 * iAP_storageLingo_OpeniPodFeatureFile_client:
 * Client command for OpeniPodFeatureFile
 */
int iAP_storageLingo_OpeniPodFeatureFile_client(streaming_chanend cCtrl, int transid, int featureType, int fileOptionsMask, int filedatalen, unsigned char filedata[], REFERENCE_PARAM(int, retval));

/*
 * iAP_storageLingo_WriteiPodFileData_client:
 * Client command for WriteiPodFileData
 */
int iAP_storageLingo_WriteiPodFileData_client(streaming_chanend cCtrl, int transid, int offset, int handle, int datalen, unsigned char data[], REFERENCE_PARAM(int, retval));

/*
 * iAP_storageLingo_CloseiPodFile_client:
 * Client command for CloseiPodFile
 */
int iAP_storageLingo_CloseiPodFile_client(streaming_chanend cCtrl, int transid, int handle, REFERENCE_PARAM(int, retval));

/*
 * iAP_storageLingo_GetiPodFreeSpace_client:
 * Client command for GetiPodFreeSpace
 */
int iAP_storageLingo_GetiPodFreeSpace_client(streaming_chanend cCtrl, int transid, int freeSpace[2]);

/*
 * iAP_storageLingo_RetDeviceCaps_client:
 * Client command for RetDeviceCaps
 */
int iAP_storageLingo_RetDeviceCaps_client(streaming_chanend cCtrl, int transid, int majorVersion, int minorVersion);


#endif //__IAP_storageLingo_CLIENT_H__

