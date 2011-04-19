/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include "ethPkt.h"
#include "ethSwitch.h"
#include "ethIp.h"
#include "string.h"
#include "stdlib.h"
#include <xclib.h>
#include "ethOther.h"
#include "getmac.h"
#include "led.h"

#ifdef DHCP
#include "ethDhcp.h"
#endif

#ifdef TFTP
#include "ethTftp.h"
#endif


// Reset the addresses structure with default mac address and IP address
void initAddresses(s_addresses *addresses)
{
#ifndef SIMULATION
  
  unsigned mac[2];
  if (!getMacAddr(0, mac))
  {
    addresses->macAddress[0] = (byterev(mac[0]) >> 16) | (byterev(mac[1] >> 16));
    addresses->macAddress[1] = byterev(mac[1]) >> 16;
  }
  else
#endif
  {
    addresses->macAddress[0] = 0x00972200;
    addresses->macAddress[1] = 0x0000FE01;
  }
  addresses->ipAddress[0] = 192;
  addresses->ipAddress[1] = 168;
  addresses->ipAddress[2] = 0;
  addresses->ipAddress[3] = 254;
}

// Process a packet received from the ethernet switch
// Supports user-defined protocols with two other channends
void ethServerProcess(s_packet *packet, unsigned cTx, unsigned cOther0, unsigned cOther1, unsigned cFlash, s_addresses *addresses, int direction)
{
  int null;
  s_packetMac *m;
  s_packetIp *i; 
  
  m = (s_packetMac *)packet->pdata;
  i = (s_packetIp *)m->payload;

#ifdef ICMP
  // ICMP Packets
  if (getShort(m->ethertype) == ETHERTYPE_IP && getChar(i->proto) == PROTO_ICMP)
  {
    ethServerProcessICMP(m, addresses, packet, cTx, &null);
  }
#endif
  
#ifdef ARP
  // ARP Packets
  if (getShort(m->ethertype) == ETHERTYPE_ARP)
  {
    ethServerProcessARP(m, addresses, packet, cTx, &null);
  }
#endif
  
#ifdef DHCP
  // TODO: DHCP. Replace with BOOTP?
#endif

#ifdef TFTP
  if (getShort(m->ethertype) == ETHERTYPE_IP && getChar(i->proto) == PROTO_UDP)
  { 
    ethServerProcessTTFP(i, packet, cTx, cFlash);
  }
#endif
  
#ifdef OTHER
  // Process other packet types
  ethOtherProcess(packet, cTx, cOther0, cOther1, cFlash, addresses, direction);
#endif
}
