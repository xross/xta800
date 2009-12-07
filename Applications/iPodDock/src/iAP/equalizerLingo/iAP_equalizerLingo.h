#ifndef __IAP_equalizerLingo_H__

#define __IAP_equalizerLingo_H__

#include "iAP.h"
#include "iAP_equalizerLingo_defines.h"
#include "iAP_equalizerLingo_defines2.h"
#include "iAP_equalizerLingo_client.h"
#include "iAP_equalizerLingo_unsolicited.h"

/*
 * iAP_equalizerLingo_init:
 * Called on iPod connection
 */
void iAP_equalizerLingo_init();

/*
 * iAP_equalizerLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_equalizerLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_equalizerLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_equalizerLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_equalizerLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_equalizerLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_equalizerLingo_flush:
 * Called on iPod disconnection
 */
void iAP_equalizerLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_equalizerLingo_H__

