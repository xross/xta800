#ifndef __IAP_sportsLingo_H__

#define __IAP_sportsLingo_H__

#include "iAP.h"
#include "iAP_sportsLingo_defines.h"
#include "iAP_sportsLingo_defines2.h"
#include "iAP_sportsLingo_client.h"
#include "iAP_sportsLingo_unsolicited.h"

/*
 * iAP_sportsLingo_init:
 * Called on iPod connection
 */
void iAP_sportsLingo_init();

/*
 * iAP_sportsLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_sportsLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_sportsLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_sportsLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_sportsLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_sportsLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_sportsLingo_flush:
 * Called on iPod disconnection
 */
void iAP_sportsLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_sportsLingo_H__

