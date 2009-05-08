/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef __ETHPHY_H__
#define __ETHPHY_H__

#include "ethPkt.h"

// ethPhyRx
// Receive packet over channel and place in s_packet structure

#ifdef __XC__
int ethPhyRx(chanend c,   s_packet &pkt,  unsigned numbytes);
#else
int ethPhyRx(unsigned c,   s_packet *pkt,  unsigned numbytes);
#endif

// ethPhyTx
// Send packet over channel from s_packet structure
#ifdef __XC__
void ethPhyTx(chanend cTx, s_packet &pkt, int &tstamp);
#else
void ethPhyTx(unsigned c,  s_packet *pkt,  int *tstamp);
#endif

// ethSwitch
// Layer 2 ethernet switch framework
// Supports two external interfaces, and one local
#ifdef __XC__
void ethSwitch(chanend cRx0, chanend cRx1, chanend cRx2,
                 chanend cTx0, chanend cTx1, chanend cTx2, chanend cWdog);
#else
void ethSwitch(unsigned cRx0, unsigned cRx1, unsigned cRx2,
                 unsigned cTx0, unsigned cTx1, unsigned cTx2, unsigned cWdog);
#endif

#endif /*__ETHPHY_H__*/


