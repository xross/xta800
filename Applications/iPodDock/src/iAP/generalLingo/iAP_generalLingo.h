#ifndef __IAP_generalLingo_H__

#define __IAP_generalLingo_H__

#include "iAP.h"
#include "iAP_generalLingo_defines.h"
#include "iAP_generalLingo_defines2.h"
#include "iAP_generalLingo_client.h"
#include "iAP_generalLingo_unsolicited.h"

/*
 * iAP_generalLingo_init:
 * Called on iPod connection
 */
void iAP_generalLingo_init();

/*
 * iAP_generalLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_generalLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_generalLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_generalLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_generalLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_generalLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_generalLingo_flush:
 * Called on iPod disconnection
 */
void iAP_generalLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_generalLingo_H__

