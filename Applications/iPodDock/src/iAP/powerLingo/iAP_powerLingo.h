#ifndef __IAP_powerLingo_H__

#define __IAP_powerLingo_H__

#include "iAP.h"
#include "iAP_powerLingo_defines.h"
#include "iAP_powerLingo_defines2.h"
#include "iAP_powerLingo_client.h"
#include "iAP_powerLingo_unsolicited.h"

/*
 * iAP_powerLingo_init:
 * Called on iPod connection
 */
void iAP_powerLingo_init();

/*
 * iAP_powerLingo_rx:
 * Called on receipt of a iAP packet for this lingo
 */
int iAP_powerLingo_rx(streaming_chanend cCtrl,
    unsigned char cmdID,
    unsigned pktlen, unsigned char pkt[],
    int transid);

/*
 * iAP_powerLingo_ctrl:
 * Called on receipt of a client request from the upper layer
 */    
int iAP_powerLingo_ctrl(streaming_chanend cCtrl, int lingoID, int timenow);

/*
 * iAP_powerLingo_timeout:
 * Called every time period to check for iAP command timeout
 */
void iAP_powerLingo_timeout(streaming_chanend cCtrl, int timenow);

/*
 * iAP_powerLingo_flush:
 * Called on iPod disconnection
 */
void iAP_powerLingo_flush(streaming_chanend cCtrl);

#endif //__IAP_powerLingo_H__

