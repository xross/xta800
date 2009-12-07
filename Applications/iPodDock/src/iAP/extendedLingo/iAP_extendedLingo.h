#ifndef __IAP_extendedLingo_H__

#define __IAP_extendedLingo_H__

#include "iAP.h"
#include "iAP_extendedLingo_defines.h"
#include "iAP_extendedLingo_defines2.h"
#include "iAP_extendedLingo_client.h"
#include "iAP_extendedLingo_unsolicited.h"

/*
 * iAP_extendedLingo_init:
 * Called on iPod connection
 */
void iAP_extendedLingo_init();

/*
 * iAP_extendedLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_extendedLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_extendedLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_extendedLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_extendedLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_extendedLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_extendedLingo_flush:
 * Called on iPod disconnection
 */
void iAP_extendedLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_extendedLingo_H__

