#ifndef __IAP_generalLingo_DEFINES_H__
#define __IAP_generalLingo_DEFINES_H__

#include "iAP.h"

/*
 * Enum for iAP protocol stack commands
 */
typedef enum
{
  generalLingo_ACK = 2,
  generalLingo_RequestRemoteUIMode = 3,
    generalLingo_ReturnRemoteUIMode = 4,
  generalLingo_EnterRemoteUIMode = 5,
  generalLingo_RequestiPodName = 7,
    generalLingo_ReturniPodName = 8,
  generalLingo_RequestiPodSoftwareVersion = 0x09,
    generalLingo_ReturniPodSoftwareVersion = 0x0A,
  generalLingo_RequestiPodSerialNum = 0x0B,
    generalLingo_ReturniPodSerialNum = 0x0C,
  generalLingo_RequestiPodModelNum = 0x0D,
    generalLingo_ReturniPodModelNum = 0x0E,
  generalLingo_RequestLingoProtocolVersion = 0x0F,
    generalLingo_ReturnLingoProtocolVersion = 0x10,
  generalLingo_IdentifyDeviceLingos = 0x13,
  generalLingo_RetDevAuthenticationInfo = 0x15,
  generalLingo_RetDevAuthenticationSignature = 0x18,
    generalLingo_AckDevAuthenticationSignature = 0x19,
  generalLingo_GetiPodAuthenticationInfo = 0x1A,
    generalLingo_RetiPodAuthenticationInfo = 0x1B,
  generalLingo_AckiPodAuthenticationInfo = 0x1C,
  generalLingo_GetiPodAuthenticationSignature = 0x1D,
    generalLingo_RetiPodAuthenticationSignature = 0x1E,
  generalLingo_AckiPodAuthenticationStatus = 0x1F,
  generalLingo_GetiPodOptions = 0x24,
    generalLingo_RetiPodOptions = 0x25,
  generalLingo_RetAccessoryInfo_0 = 0x0028,
  generalLingo_RetAccessoryInfo_1 = 0x0128,
  generalLingo_RetAccessoryInfo_4 = 0x0428,
  generalLingo_SetiPodPreferences = 0x2B,
  generalLingo_StartIDPS = 0x38,
  generalLingo_SetFIDTokenValues_Identify = 0x139,
    generalLingo_RetFIDTokenValueACKs_Identify = 0x13A,
  generalLingo_SetFIDTokenValues_accCaps = 0x239,
    generalLingo_RetFIDTokenValueACKs_accCaps = 0x23A,
  generalLingo_SetFIDTokenValues_accinfo = 0x339,
    generalLingo_RetFIDTokenValueACKs_accinfo = 0x33A,
  generalLingo_SetFIDTokenValues_iPodPreference = 0x439,
    generalLingo_RetFIDTokenValueACKs_iPodPreference = 0x43A,
  generalLingo_SetFIDTokenValues_bundleSeedIDPref = 0x539,
    generalLingo_RetFIDTokenValueACKs_bundleSeedIDPref = 0x53A,
  generalLingo_EndIDPS = 0x3B,
    generalLingo_IDPSStatus = 0x3C,
  
    generalLingo_AckDevAuthenticationInfo = 0x16,

} e_generalLingo_commands;


#endif //__IAP_generalLingo_DEFINES_H__
