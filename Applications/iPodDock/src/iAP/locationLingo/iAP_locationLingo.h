#ifndef __IAP_locationLingo_H__

#define __IAP_locationLingo_H__

#include "iAP.h"
#include "iAP_locationLingo_defines.h"
#include "iAP_locationLingo_defines2.h"
#include "iAP_locationLingo_client.h"
#include "iAP_locationLingo_unsolicited.h"

/*
 * iAP_locationLingo_init:
 * Called on iPod connection
 */
void iAP_locationLingo_init();

/*
 * iAP_locationLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_locationLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_locationLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_locationLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_locationLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_locationLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_locationLingo_flush:
 * Called on iPod disconnection
 */
void iAP_locationLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_locationLingo_H__

