#ifndef __IAP_displayLingo_H__

#define __IAP_displayLingo_H__

#include "iAP.h"
#include "iAP_displayLingo_defines.h"
#include "iAP_displayLingo_defines2.h"
#include "iAP_displayLingo_client.h"
#include "iAP_displayLingo_unsolicited.h"

/*
 * iAP_displayLingo_init:
 * Called on iPod connection
 */
void iAP_displayLingo_init();

/*
 * iAP_displayLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_displayLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_displayLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_displayLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_displayLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_displayLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_displayLingo_flush:
 * Called on iPod disconnection
 */
void iAP_displayLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_displayLingo_H__

