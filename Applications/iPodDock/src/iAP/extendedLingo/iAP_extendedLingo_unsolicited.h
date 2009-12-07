#ifndef __IAP_extendedLingo_UNSOLICITED_H__
#define __IAP_extendedLingo_UNSOLICITED__H__

/*
 * iAP_extendedLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for extendedLingo
 */
int iAP_extendedLingo_unsolicitedrx (streaming_chanend cCtrl, e_extendedLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_extendedLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_extendedLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_extendedLingo_UNSOLICITED__H__


