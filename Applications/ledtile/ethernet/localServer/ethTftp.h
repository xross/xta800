/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef ETHTFTP_H_
#define ETHTFTP_H_

#include "ethPkt.h"

// TFTP Defines
#define TFTP_OPCODE_RRQ      1
#define TFTP_OPCODE_WRQ      2
#define TFTP_OPCODE_DATA     3
#define TFTP_OPCODE_ACK      4
#define TFTP_OPCODE_ERROR    5
#define PORT_TFTP            69
#define TFTP_SIZE            4

// TFTP Structure
typedef struct
{
  unsigned short opcode;
  unsigned char data[ETH_FRAME_SIZE - MAC_SIZE - IP_SIZE - UDP_SIZE - 2];
} s_packetTftp;

// processTftp
// TODO: Tftp not fully implemented.
void processTftp(s_packet *p, s_packetTftp *t, unsigned cTx, unsigned cFlash);

#endif /*ETHTFTP_H_*/
