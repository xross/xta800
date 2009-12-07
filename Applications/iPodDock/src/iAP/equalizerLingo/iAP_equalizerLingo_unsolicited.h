#ifndef __IAP_equalizerLingo_UNSOLICITED_H__
#define __IAP_equalizerLingo_UNSOLICITED__H__

/*
 * iAP_equalizerLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for equalizerLingo
 */
int iAP_equalizerLingo_unsolicitedrx (streaming_chanend cCtrl, e_equalizerLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_equalizerLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_equalizerLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_equalizerLingo_UNSOLICITED__H__


