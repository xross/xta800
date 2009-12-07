#ifndef __IAP_generalLingo_UNSOLICITED_H__
#define __IAP_generalLingo_UNSOLICITED__H__

/*
 * iAP_generalLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for generalLingo
 */
int iAP_generalLingo_unsolicitedrx (streaming_chanend cCtrl, e_generalLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_generalLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_generalLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_generalLingo_UNSOLICITED__H__


