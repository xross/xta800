#ifndef __IAP_simpleLingo_UNSOLICITED_H__
#define __IAP_simpleLingo_UNSOLICITED__H__

/*
 * iAP_simpleLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for simpleLingo
 */
int iAP_simpleLingo_unsolicitedrx (streaming_chanend cCtrl, e_simpleLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_simpleLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_simpleLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_simpleLingo_UNSOLICITED__H__


