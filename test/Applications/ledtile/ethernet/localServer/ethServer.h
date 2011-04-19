/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef ETHSERVER_H_
#define ETHSERVER_H_

// Standard local ethernet server
// Supports ICMP, ARP, DHCP(TODO), TFTP
// Also supports user-definable protocols with two user defined channels
void ethServer(chanend cRx, chanend cTx, chanend cLedData, chanend cLedCmd, chanend cSpiFlash, chanend cWdog);

#endif /*ETHSERVER_H_*/
