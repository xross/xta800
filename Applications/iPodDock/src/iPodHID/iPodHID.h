#ifndef IPODHID_H_
#define IPODHID_H_

#include "xccompat.h"

/*
 * iPodHIDInit:
 * Call on iPod connection
 */
void iPodHIDInit(int IFNum);

/*
 * iPodHIDTx:
 * Call to transmit a packet over the HID abstraction layer
 */
int iPodHIDTx(unsigned char iAPData[], unsigned iAPDataLen);

/*
 * iPodHIDRx:
 * Call when a packet is received over the HID abstraction layer
 */
void iPodHIDRx(chanend c, unsigned char data[], int datalen, int ackstate);

/*
 * iPodHIDParseDescriptor:
 * Called to parse the configuration descriptor
 */
int iPodHIDParseDescriptor(unsigned char data[], int datalen);

#endif /*IPODHID_H_*/

