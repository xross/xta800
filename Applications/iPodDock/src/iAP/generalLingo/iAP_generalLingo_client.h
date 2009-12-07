#ifndef __IAP_generalLingo_CLIENT_H__

#define __IAP_generalLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_generalLingo_RequestRemoteUIMode_client:
 * Client command for RequestRemoteUIMode
 */
int iAP_generalLingo_RequestRemoteUIMode_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, rxmode));

/*
 * iAP_generalLingo_EnterRemoteUIMode_client:
 * Client command for EnterRemoteUIMode
 */
int iAP_generalLingo_EnterRemoteUIMode_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_RequestiPodName_client:
 * Client command for RequestiPodName
 */
int iAP_generalLingo_RequestiPodName_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_generalLingo_RequestiPodSoftwareVersion_client:
 * Client command for RequestiPodSoftwareVersion
 */
int iAP_generalLingo_RequestiPodSoftwareVersion_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, majorversion), REFERENCE_PARAM(int, minorversion), REFERENCE_PARAM(int, revisionversion));

/*
 * iAP_generalLingo_RequestiPodSerialNum_client:
 * Client command for RequestiPodSerialNum
 */
int iAP_generalLingo_RequestiPodSerialNum_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retlen), unsigned char ret[]);

/*
 * iAP_generalLingo_RequestiPodModelNum_client:
 * Client command for RequestiPodModelNum
 */
int iAP_generalLingo_RequestiPodModelNum_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, modelid));

/*
 * iAP_generalLingo_RequestLingoProtocolVersion_client:
 * Client command for RequestLingoProtocolVersion
 */
int iAP_generalLingo_RequestLingoProtocolVersion_client(streaming_chanend cCtrl, int transid, int lingoid, REFERENCE_PARAM(int, majorversion), REFERENCE_PARAM(int, minorversion));

/*
 * iAP_generalLingo_IdentifyDeviceLingos_client:
 * Client command for IdentifyDeviceLingos
 */
int iAP_generalLingo_IdentifyDeviceLingos_client(streaming_chanend cCtrl, int transid, int lingosSpoken, int options, int deviceID, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_RetDevAuthenticationInfo_client:
 * Client command for RetDevAuthenticationInfo
 */
int iAP_generalLingo_RetDevAuthenticationInfo_client(streaming_chanend cCtrl, int transid, int authversion, int x509currentindex, int x509maxindex, int x509datalen, int x509dataptr, unsigned char x509data[], REFERENCE_PARAM(int, authstatus));

/*
 * iAP_generalLingo_RetDevAuthenticationSignature_client:
 * Client command for RetDevAuthenticationSignature
 */
int iAP_generalLingo_RetDevAuthenticationSignature_client(streaming_chanend cCtrl, int transid, int siglen, unsigned char sig[], REFERENCE_PARAM(int, authstatus));

/*
 * iAP_generalLingo_GetiPodAuthenticationInfo_client:
 * Client command for GetiPodAuthenticationInfo
 */
int iAP_generalLingo_GetiPodAuthenticationInfo_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, authversion), REFERENCE_PARAM(int, x509currentindex),REFERENCE_PARAM(int, x509maxindex), REFERENCE_PARAM(int, x509datalen), unsigned char x509data[]);

/*
 * iAP_generalLingo_AckiPodAuthenticationInfo_client:
 * Client command for AckiPodAuthenticationInfo
 */
int iAP_generalLingo_AckiPodAuthenticationInfo_client(streaming_chanend cCtrl, int transid, int ackstatus, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_GetiPodAuthenticationSignature_client:
 * Client command for GetiPodAuthenticationSignature
 */
int iAP_generalLingo_GetiPodAuthenticationSignature_client(streaming_chanend cCtrl, int transid, int challengelen, unsigned char challenge[], int retrycounter, REFERENCE_PARAM(int, siglen), unsigned char sig[]);

/*
 * iAP_generalLingo_AckiPodAuthenticationStatus_client:
 * Client command for AckiPodAuthenticationStatus
 */
int iAP_generalLingo_AckiPodAuthenticationStatus_client(streaming_chanend cCtrl, int transid, int ackstatus, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_GetiPodOptions_client:
 * Client command for GetiPodOptions
 */
int iAP_generalLingo_GetiPodOptions_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, options));

/*
 * iAP_generalLingo_RetAccessoryInfo_0_client:
 * Client command for RetAccessoryInfo_0
 */
int iAP_generalLingo_RetAccessoryInfo_0_client(streaming_chanend cCtrl, int transid, int accessoryInfoType, int accessoryCapabilities, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_RetAccessoryInfo_1_client:
 * Client command for RetAccessoryInfo_1
 */
int iAP_generalLingo_RetAccessoryInfo_1_client(streaming_chanend cCtrl, int transid, int infoType, int len, char name[], REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_RetAccessoryInfo_4_client:
 * Client command for RetAccessoryInfo_4
 */
int iAP_generalLingo_RetAccessoryInfo_4_client(streaming_chanend cCtrl, int transid, int infotype, int version[3], REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetiPodPreferences_client:
 * Client command for SetiPodPreferences
 */
int iAP_generalLingo_SetiPodPreferences_client(streaming_chanend cCtrl, int transid, int preferenceClassID, int preferenceSettingID, int restoreOnExit, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_StartIDPS_client:
 * Client command for StartIDPS
 */
int iAP_generalLingo_StartIDPS_client(streaming_chanend cCtrl, int transid, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetFIDTokenValues_Identify_client:
 * Client command for SetFIDTokenValues_Identify
 */
int iAP_generalLingo_SetFIDTokenValues_Identify_client(streaming_chanend cCtrl, int transid, int numLingos, char lingos[], int options, int deviceid, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetFIDTokenValues_accCaps_client:
 * Client command for SetFIDTokenValues_accCaps
 */
int iAP_generalLingo_SetFIDTokenValues_accCaps_client(streaming_chanend cCtrl, int transid, int analog_line_out, int analog_line_in, int analog_video_out, int usb_audio, int iphone_os_app, int ipod_volume, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetFIDTokenValues_accinfo_client:
 * Client command for SetFIDTokenValues_accinfo
 */
int iAP_generalLingo_SetFIDTokenValues_accinfo_client(streaming_chanend cCtrl, int transid, int accInfoType, int len, char s[], REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetFIDTokenValues_iPodPreference_client:
 * Client command for SetFIDTokenValues_iPodPreference
 */
int iAP_generalLingo_SetFIDTokenValues_iPodPreference_client(streaming_chanend cCtrl, int transid, char prefClassID, char prefSettingID, char restoreOnExit, REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_SetFIDTokenValues_bundleSeedIDPref_client:
 * Client command for SetFIDTokenValues_bundleSeedIDPref
 */
int iAP_generalLingo_SetFIDTokenValues_bundleSeedIDPref_client(streaming_chanend cCtrl, int transid, char s[11], REFERENCE_PARAM(int, retval));

/*
 * iAP_generalLingo_EndIDPS_client:
 * Client command for EndIDPS
 */
int iAP_generalLingo_EndIDPS_client(streaming_chanend cCtrl, int transid, int accEndIDPSStatus, REFERENCE_PARAM(int, retval));


#endif //__IAP_generalLingo_CLIENT_H__

