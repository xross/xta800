#ifndef __IAP_audioLingo_UNSOLICITED_H__
#define __IAP_audioLingo_UNSOLICITED__H__

/*
 * iAP_audioLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for audioLingo
 */
int iAP_audioLingo_unsolicitedrx (streaming_chanend cCtrl, e_audioLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_audioLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_audioLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_audioLingo_UNSOLICITED__H__


