#ifndef __IAP_audioLingo_CLIENT_H__

#define __IAP_audioLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_audioLingo_RetAccSampleRateCaps_client:
 * Client command for RetAccSampleRateCaps
 */
int iAP_audioLingo_RetAccSampleRateCaps_client(streaming_chanend cCtrl, int transid, int nrates, int rates[]);

/*
 * iAP_audioLingo_SetVideoDelay_client:
 * Client command for SetVideoDelay
 */
int iAP_audioLingo_SetVideoDelay_client(streaming_chanend cCtrl, int transid, int videodelay, REFERENCE_PARAM(int, retval));

/*
 * iAP_audioLingo_AccAck_client:
 * Client command for AccAck
 */
int iAP_audioLingo_AccAck_client(streaming_chanend cCtrl, int transid, int cmdstatus, int cmdid);


#endif //__IAP_audioLingo_CLIENT_H__

