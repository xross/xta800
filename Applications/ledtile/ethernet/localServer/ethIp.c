/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include "ethPkt.h"

// Ethernet IP / UDP Functions

// udpChecksum
// (Re:)Calculate UDP checksum
void udpChecksum(s_packetUdp *u)
{
  u->checksum=0;
}

void setUdpSize(s_packet *p, int size)
{
  s_packetMac *m;
  s_packetIp *i; 
  s_packetUdp *u;
  
  m = (s_packetMac *)p->pdata;
  i = (s_packetIp *)m->payload;
  u = (s_packetUdp *)i->payload;
  
  p->plen_b = MAC_SIZE + IP_SIZE + UDP_SIZE + size;
  i->length = getShort(IP_SIZE + UDP_SIZE + size);
  u->length = getShort(UDP_SIZE + size);
}

// ipInit
// Initialise standard UDP / IP packet
void ipInit(s_packetIp *i)
{
  i->version = 0x45;
  i->ToS = 0x10;
  i->id  = 0;
  i->flags_fragoffset = 0;
  i->ttl = getChar(99);
  i->proto = getChar(PROTO_UDP); //udp
}

// ipChecksum
// (Re:)Calculate IP checksum
void ipChecksum(s_packetIp *i)
{
  int ipchecksum = 0, j;
  unsigned short *ipptr = (unsigned short *)i;

  i->headercheck = getWord(0);

  for (j=0; j<10; j++)
    ipchecksum += getShort(ipptr[j]);
  
  i->headercheck = ~getShort((ipchecksum & 0xFFFF) + (ipchecksum >> 16));
}