#ifndef IAP_TRANSACTIONS_H_
#define IAP_TRANSACTIONS_H_

/*
 * transmit_char_array:
 * Helper function for transmitting an array over a channel
 */
void transmit_char_array(streaming_chanend c, unsigned pktlen, unsigned char pkt[], int offset);

/*
 * receive_char_array:
 * Helper function for receiving an array over a channel
 */
void receive_char_array(streaming_chanend c, REFERENCE_PARAM(unsigned, pktlen), unsigned char pkt[], int offset);

#endif /*IAP_TRANSACTIONS_H_*/
