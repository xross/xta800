#ifndef __IAP_displayLingo_UNSOLICITED_H__
#define __IAP_displayLingo_UNSOLICITED__H__

/*
 * iAP_displayLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for displayLingo
 */
int iAP_displayLingo_unsolicitedrx (streaming_chanend cCtrl, e_displayLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_displayLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_displayLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_displayLingo_UNSOLICITED__H__


