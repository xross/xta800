#ifndef __IAP_H__

#define __IAP_H__

#include <xs1.h>
#include <xccompat.h>
#include <xclib.h>
#include "global.h"
#include "iPodOptions.h"
#include "iAP_commands.h"
#include "iAP_defines.h"
#include "iAP_lingos.h"
#include "iAP_sendpacket.h"
#include "iAP_timeout.h"
#include "iAP_transactions.h"

/*
 * iAP_rx:
 * Function when receiving iAP packets from the HID layer
 */
int iAP_rx(streaming_chanend cCtrl, unsigned char pkt[], 
    unsigned pktlen);

/*
 * iAP_init:
 * Called on new iPod connection
 */
void iAP_init();

/*
 * iAP_ctrl:
 * Called on client request from upper layer
 */
int iAP_ctrl(streaming_chanend cCtrl,
    int lingo, int timenow);

/*
 * iAP_flush:
 * Called on iPod disconnection
 */
void iAP_flush(streaming_chanend c);

#endif //__IAP_H__
