#ifndef __IAP_microphoneLingo_H__

#define __IAP_microphoneLingo_H__

#include "iAP.h"
#include "iAP_microphoneLingo_defines.h"
#include "iAP_microphoneLingo_defines2.h"
#include "iAP_microphoneLingo_client.h"
#include "iAP_microphoneLingo_unsolicited.h"

/*
 * iAP_microphoneLingo_init:
 * Called on iPod connection
 */
void iAP_microphoneLingo_init();

/*
 * iAP_microphoneLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_microphoneLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_microphoneLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_microphoneLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_microphoneLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_microphoneLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_microphoneLingo_flush:
 * Called on iPod disconnection
 */
void iAP_microphoneLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_microphoneLingo_H__

