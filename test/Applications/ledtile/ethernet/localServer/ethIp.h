/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef __ETHIP_H__
#define __ETHIP_H__

// Ethernet IP / UDP Functions

// (Re:)Calculate UDP checksum
void udpChecksum(s_packetUdp *u);

// Set UDP datagram size
void setUdpSize(s_packet *p, int size);

// (Re:)Calculate IP checksum
void ipChecksum(s_packetIp *i);

// Initialise standard UDP / IP packet
void ipInit(s_packetIp *i);

#endif