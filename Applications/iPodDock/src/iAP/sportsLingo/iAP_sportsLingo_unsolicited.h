#ifndef __IAP_sportsLingo_UNSOLICITED_H__
#define __IAP_sportsLingo_UNSOLICITED__H__

/*
 * iAP_sportsLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for sportsLingo
 */
int iAP_sportsLingo_unsolicitedrx (streaming_chanend cCtrl, e_sportsLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_sportsLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_sportsLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_sportsLingo_UNSOLICITED__H__


