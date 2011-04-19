/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef ETHSERVERPROCESS_H_
#define ETHSERVERPROCESS_H_

#include "ethPkt.h"

// initAddresses
// Initialise the addresses structure with the default MAC address and IP address
#ifdef __XC__
void initAddresses(s_addresses &addresses);
#else
void initAddresses(s_addresses *addresses);
#endif

// ethServerProcess
// Process ethernet packets. Supports ICMP, ARP, DHCP(TODO), TFTP
// Also supports user-definable protocols with two user defined channels
#ifdef __XC__
void ethServerProcess(s_packet &packet, chanend cTx, chanend cOther0, chanend cOther1, chanend cFlash, s_addresses &addresses, int direction);
#else
void ethServerProcess(s_packet *packet, unsigned cTx, unsigned cOther0, unsigned cOther1, unsigned cFlash, s_addresses *addresses, int direction)
#endif

#endif /*ETHSERVERPROCESS_H_*/
