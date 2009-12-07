#ifndef __IAP_equalizerLingo_CLIENT_H__

#define __IAP_equalizerLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_equalizerLingo_Ack_client:
 * Client command for Ack
 */
int iAP_equalizerLingo_Ack_client(streaming_chanend cCtrl, int transid, int status, int cmdid);

/*
 * iAP_equalizerLingo_RetCurrentEQIndex_client:
 * Client command for RetCurrentEQIndex
 */
int iAP_equalizerLingo_RetCurrentEQIndex_client(streaming_chanend cCtrl, int transid, int eqIndex);

/*
 * iAP_equalizerLingo_RetEQSettingCount_client:
 * Client command for RetEQSettingCount
 */
int iAP_equalizerLingo_RetEQSettingCount_client(streaming_chanend cCtrl, int transid, int eqcount);

/*
 * iAP_equalizerLingo_RetEQIndexName_client:
 * Client command for RetEQIndexName
 */
int iAP_equalizerLingo_RetEQIndexName_client(streaming_chanend cCtrl, int transid, int eqIndex, int namelen, unsigned char name[]);


#endif //__IAP_equalizerLingo_CLIENT_H__

