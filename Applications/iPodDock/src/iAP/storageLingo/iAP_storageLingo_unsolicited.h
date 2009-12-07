#ifndef __IAP_storageLingo_UNSOLICITED_H__
#define __IAP_storageLingo_UNSOLICITED__H__

/*
 * iAP_storageLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for storageLingo
 */
int iAP_storageLingo_unsolicitedrx (streaming_chanend cCtrl, e_storageLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_storageLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_storageLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_storageLingo_UNSOLICITED__H__


