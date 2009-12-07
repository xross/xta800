#ifndef __IAP_microphoneLingo_UNSOLICITED_H__
#define __IAP_microphoneLingo_UNSOLICITED__H__

/*
 * iAP_microphoneLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for microphoneLingo
 */
int iAP_microphoneLingo_unsolicitedrx (streaming_chanend cCtrl, e_microphoneLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_microphoneLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_microphoneLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_microphoneLingo_UNSOLICITED__H__


