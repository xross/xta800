#ifndef __IAP_rftunerLingo_H__

#define __IAP_rftunerLingo_H__

#include "iAP.h"
#include "iAP_rftunerLingo_defines.h"
#include "iAP_rftunerLingo_defines2.h"
#include "iAP_rftunerLingo_client.h"
#include "iAP_rftunerLingo_unsolicited.h"

/*
 * iAP_rftunerLingo_init:
 * Called on iPod connection
 */
void iAP_rftunerLingo_init();

/*
 * iAP_rftunerLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_rftunerLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_rftunerLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_rftunerLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_rftunerLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_rftunerLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_rftunerLingo_flush:
 * Called on iPod disconnection
 */
void iAP_rftunerLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_rftunerLingo_H__

