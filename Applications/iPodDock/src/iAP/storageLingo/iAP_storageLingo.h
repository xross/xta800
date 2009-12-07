#ifndef __IAP_storageLingo_H__

#define __IAP_storageLingo_H__

#include "iAP.h"
#include "iAP_storageLingo_defines.h"
#include "iAP_storageLingo_defines2.h"
#include "iAP_storageLingo_client.h"
#include "iAP_storageLingo_unsolicited.h"

/*
 * iAP_storageLingo_init:
 * Called on iPod connection
 */
void iAP_storageLingo_init();

/*
 * iAP_storageLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_storageLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_storageLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_storageLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_storageLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_storageLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_storageLingo_flush:
 * Called on iPod disconnection
 */
void iAP_storageLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_storageLingo_H__

