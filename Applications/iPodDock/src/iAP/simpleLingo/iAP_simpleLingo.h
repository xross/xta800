#ifndef __IAP_simpleLingo_H__

#define __IAP_simpleLingo_H__

#include "iAP.h"
#include "iAP_simpleLingo_defines.h"
#include "iAP_simpleLingo_defines2.h"
#include "iAP_simpleLingo_client.h"
#include "iAP_simpleLingo_unsolicited.h"

/*
 * iAP_simpleLingo_init:
 * Called on iPod connection
 */
void iAP_simpleLingo_init();

/*
 * iAP_simpleLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_simpleLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_simpleLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_simpleLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_simpleLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_simpleLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_simpleLingo_flush:
 * Called on iPod disconnection
 */
void iAP_simpleLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_simpleLingo_H__

