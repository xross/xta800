#ifndef __IAP_audioLingo_H__

#define __IAP_audioLingo_H__

#include "iAP.h"
#include "iAP_audioLingo_defines.h"
#include "iAP_audioLingo_defines2.h"
#include "iAP_audioLingo_client.h"
#include "iAP_audioLingo_unsolicited.h"

/*
 * iAP_audioLingo_init:
 * Called on iPod connection
 */
void iAP_audioLingo_init();

/*
 * iAP_audioLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_audioLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_audioLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_audioLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_audioLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_audioLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_audioLingo_flush:
 * Called on iPod disconnection
 */
void iAP_audioLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_audioLingo_H__

