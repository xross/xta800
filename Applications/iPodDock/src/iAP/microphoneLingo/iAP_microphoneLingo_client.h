#ifndef __IAP_microphoneLingo_CLIENT_H__

#define __IAP_microphoneLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_microphoneLingo_RetDevCaps_client:
 * Client command for RetDevCaps
 */
int iAP_microphoneLingo_RetDevCaps_client(streaming_chanend cCtrl, int transid, int stereoLineInput, int stereoSwitchingInput, int recordingLevelPresent, int recordingLevelLimitPresent, int duplexAudio);

/*
 * iAP_microphoneLingo_RetDevCtrl_client:
 * Client command for RetDevCtrl
 */
int iAP_microphoneLingo_RetDevCtrl_client(streaming_chanend cCtrl, int transid, int controlType, int controlData);


#endif //__IAP_microphoneLingo_CLIENT_H__

