#ifndef __IAP_powerLingo_UNSOLICITED_H__
#define __IAP_powerLingo_UNSOLICITED__H__

/*
 * iAP_powerLingo_unsolicitedrx:
 * Called when an unsolicited iAP packet is received for powerLingo
 */
int iAP_powerLingo_unsolicitedrx (streaming_chanend cCtrl, e_powerLingo_commands cmdID,
    unsigned pktlen, unsigned char pkt[], int transid);

/*
 * iAP_powerLingo_unsolicited:
 * Called when client checks if any unsolicited messages have been received
 */
int iAP_powerLingo_unsolicited (streaming_chanend cCtrl);

#endif //__IAP_powerLingo_UNSOLICITED__H__


