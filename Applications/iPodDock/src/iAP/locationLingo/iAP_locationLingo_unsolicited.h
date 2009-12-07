#ifndef __IAP_locationLingo_UNSOLICITED_H__
#define __IAP_locationLingo_UNSOLICITED__H__

/*
 * iAP_locationLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for locationLingo
 */
int iAP_locationLingo_unsolicitedrx (streaming_chanend cCtrl, e_locationLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_locationLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_locationLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_locationLingo_UNSOLICITED__H__


