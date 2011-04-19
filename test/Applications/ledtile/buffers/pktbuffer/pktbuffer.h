/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef __PKTBUFFER_H__
#define __PKTBUFFER_H__

// Standard packet buffer FIFO

/* On cIn (use master transaction):
 *   -> packetlength ( >= 0)
 *   <- Ack(0) / NAck(1)
 *   -> data0
 *   -> data1
 *   -> .
 *   -> .
 *   -> data(packetlength - 1)
 */

/* On cOut (use streaming channel):
 *   -> X
 *   <- Ack(0) / NAck(1)
 *   <- packetlength
 *   <- data0
 *   <- data1
 *   <- .
 *   <- data(packetlength - 1)
 */
void pktbuffer(chanend cIn, chanend cOut);

#endif

