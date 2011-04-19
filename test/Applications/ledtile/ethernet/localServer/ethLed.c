/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include "reset.h"
#include "ethOther.h"
#include "ethPkt.h"
#include "ethSwitch.h"
#include "ethIp.h"
#include "xmosAC.h"
#include "ledprocess.h"
#include "string.h"
#include <xclib.h>
#include "led.h"
#include "flashManager.h"
#include "chanAbs.h"
#include "stdlib.h"

#ifdef DHCP
#include "ethDhcp.h"
#endif

#ifdef TFTP
#include "ethTftp.h"
#endif

s_xmosAC xmosACdata;

void flash_write_gamma(int block, unsigned short *gt, unsigned cFlash)
{
  int i;
  out(cFlash, SPI_FLASH_NEWGAMMA);
  chanStartMaster(cFlash);
  outuint(cFlash, block);
  outuint(cFlash, 256);
  for (i=0; i<256; i++)
    outuint(cFlash, (unsigned int)gt[i]);
  chanStopMaster(cFlash);
}

void sendACforwardPackets(s_packet *packet, s_addresses *addresses, unsigned cTx)
{
  s_packetMac *m;
  s_packetIp *i; 
  s_packetUdp *u;
  s_packetXmos *x;
  int null;
  
  m = (s_packetMac *)packet->pdata;
  i = (s_packetIp *)m->payload;
  u = (s_packetUdp *)i->payload;
  x = (s_packetXmos *)u->payload;
  
  x->identifier = getShort(XMOS_AC_4);
  setUdpSize(packet, XMOS_SIZE+8);
  packet->plen_b = 60;

  // If there's another node after us
  if (xmosACdata.XendFlag == 0)
  {
    i->ttl = 0;
    memcpy(x->payload, xmosACdata.nextXNodeMac, 6);
    x->payload[7]++;
    udpChecksum(u);
    ipChecksum(i);
    ethPhyTx(cTx, packet, &null);
    x->payload[7]--;
  }
  
  if (xmosACdata.YendFlag == 0)
  {
    memcpy(x->payload, xmosACdata.nextYNodeMac, 6);
    x->payload[6]++;
    i->ttl = 255;
    udpChecksum(u);
    ipChecksum(i);
    ethPhyTx(cTx, packet, &null);
    x->payload[6]--;
  }  
}


// Local Ethernet server for receiving XMOS-Specific packets (i.e., not ICMP etc)
void ethOtherProcess(s_packet *packet, unsigned cTx, unsigned cLedData, unsigned cLedCmd, unsigned cFlash, s_addresses *addresses, int direction)
{
  int null;
  const char magicNumber[4] = {'X', 'M', 'O', 'S' };
  s_packetMac *m;
  s_packetIp *i; 
  m = (s_packetMac *)packet->pdata;
  i = (s_packetIp *)m->payload;
  // Check we are targeting the correct IP
  if (memcmp(i->dest, addresses->ipAddress, 4) && i->dest[3] != 0xFF)
    return;
  
  // Ipv4 UDP packets
  if (getShort(m->ethertype) == ETHERTYPE_IP && getChar(i->proto) == PROTO_UDP)
  {
    if (memcmp(i->dest, addresses->ipAddress, 4) == 0 ||
        (i->dest[2] == 0xFF && i->dest[3] == 0xFF))
    {
      s_packetUdp *u;
      u = (s_packetUdp *)i->payload;
        
      if(getShort(u->destport) == PORT_XMOS)
      {
        s_packetXmos *x = (s_packetXmos *)u->payload;
        
        // Verify magic word
        if (memcmp( (void*)x->magicNumber , (void*)magicNumber, 4) == 0)
        {
      	// Check the XMOS identifier
          switch (getShort(x->identifier))
          {
            case (XMOS_VERSION):
            {
              while (1);
            }
            break;
            case (XMOS_DATA):
            {
          	// This is a LED data packet
              unsigned char *ptr;
              unsigned iptr;
              
              // if not our IP, exit
              if (memcmp(i->dest, addresses->ipAddress, 4))
                return;
              
              s_packetData *d = (s_packetData *)x->payload;
              ptr = d->data;
              
              // Check packet
              if (d->datalen + 3*d->pixptr <= 3*FRAME_SIZE)
              {
                // Send to LED frame buffer
                chanStartMaster(cLedData);
                outuint(cLedData, (unsigned int)d->pixptr);
                outuint(cLedData, d->datalen);
                iptr = d->datalen;
                while (iptr)
                {
                  iptr -= 3;
                  outuchar(cLedData, (unsigned)*ptr++);
                  outuchar(cLedData, (unsigned)*ptr++);
                  outuchar(cLedData, (unsigned)*ptr++);
                  outuchar(cLedData, 0);
                }
                chanStopMaster(cLedData);
              }
            }
            break;
            case (XMOS_LATCH):
            {
            	// This is a "New Frame" latch signal
              chanStartMaster(cLedData);
              outuint(cLedData, (unsigned int)-1);
              chanStopMaster(cLedData);
            }
            break;
            case (XMOS_GAMMAADJ):
            {
          	// This is a "adjust gamma table" packet
              int i;

              s_packetGammaTable *gt = (s_packetGammaTable *)x->payload;
              
              // Store in command buffer
              chanStartMaster(cLedCmd);
              outuint(cLedCmd,  256 + 2);
              // Response indicates if buffer is full
              inuint_byref(cLedCmd, i);
              if (i == 0)
              {
                outuint(cLedCmd, XMOS_GAMMAADJ);
                outuint(cLedCmd, gt->colchan[0]);
                for (i=0; i<256; i++)
                  outuint(cLedCmd, (unsigned int)gt->gammaTable[i]);
              }
              chanStopMaster(cLedCmd);
              
              // Write to flash
              if (gt->colchan[0] == 'A')
              {
                flash_write_gamma(0, gt->gammaTable, cFlash);
                flash_write_gamma(1, gt->gammaTable, cFlash);
                flash_write_gamma(2, gt->gammaTable, cFlash);
              }
              else if (gt->colchan[0] == 'R')
              {
                flash_write_gamma(0, gt->gammaTable, cFlash);
              }
              else if (gt->colchan[0] == 'G')
              {
                flash_write_gamma(1, gt->gammaTable, cFlash);
              }
              else if (gt->colchan[0] == 'B')
              {
                flash_write_gamma(2, gt->gammaTable, cFlash);
              }

            }
            break;
            case (XMOS_INTENSITYADJ):
            case (XMOS_SINTENSITYADJ):
            {
          	// This is an intentsity adjustment packet
              int i;
              s_packetIntensity *inte = (s_packetIntensity *)x->payload;
              
              chanStartMaster(cLedCmd);
              // Store in command buffer
              outuint(cLedCmd,  3);
              inuint_byref(cLedCmd, i);
              // Response indicates if buffer is full
              if (i == 0)
              {
                outuint(cLedCmd, getShort(x->identifier));
                outuint(cLedCmd, inte->colchan[0]);
                outuint(cLedCmd, inte->intensity);
              }
              chanStopMaster(cLedCmd);
            }
            break;
            case (XMOS_SINTENSITYADJ_PIX):
            {
            // This is an intentsity adjustment packet for a single pixel
              int i;
              s_packetIntensityPix *inte = (s_packetIntensityPix *)x->payload;
              
              chanStartMaster(cLedCmd);
              // Store in command buffer
              outuint(cLedCmd,  5);
              inuint_byref(cLedCmd, i);
              // Response indicates if buffer is full
              if (i == 0)
              {
                outuint(cLedCmd, getShort(x->identifier));
                outuint(cLedCmd, inte->colchan);
                outuint(cLedCmd, inte->y);
                outuint(cLedCmd, inte->x);
                outuint(cLedCmd, inte->intensity);
              }
              chanStopMaster(cLedCmd);
            }
            break;
            case (XMOS_RESET):
              chipReset();
            break;
            case (XMOS_CHANGEDRIVER):
            {
              s_packetDriverType *dt = (s_packetDriverType *)x->payload;
              // Store a command to change the LED Driver
              chanStartMaster(cLedCmd);
              // Store in command buffer
              outuint(cLedCmd,  2);
              inuint_byref(cLedCmd, i);
              // Response indicates if buffer is full
              if (i == 0)
              {
                outuint(cLedCmd, getShort(x->identifier));
                outuint(cLedCmd, dt->drivertype);
              }
              chanStopMaster(cLedCmd);
            }
            break;
            case (XMOS_AC_1):
              // Broadcast AC_2 message, include our TTL of AC_1 message
              x->identifier = getShort(XMOS_AC_2);
              x->payload[0] = i->ttl;
              
              xmosACdata.myCommandTTL = i->ttl;
              xmosACdata.XstartFlag = 1;
              xmosACdata.XendFlag = 1;
              xmosACdata.YstartFlag = 1;
              xmosACdata.YendFlag = 1;
              
              memcpy( (void*)m->sourcemac, (void*)addresses->macAddress, 6);

              i->ttl = 255;
              setUdpSize(packet, XMOS_SIZE+1);
              packet->plen_b = 60;
              udpChecksum(u);
              ipChecksum(i);
              ethPhyTx(cTx, packet, &null);
            break;
            case (XMOS_AC_2):
              // If we think we are at the beginning of this chain
              if (xmosACdata.XstartFlag && x->payload[0] == xmosACdata.myCommandTTL)
              {
                // Look to see if they are before us in the list
                if (memcmp(m->sourcemac, addresses->macAddress, 6) < 0)
                {
                  // We are not at the beginning of the Y chain
                  xmosACdata.YstartFlag = 0;
                }
                else if ((xmosACdata.YendFlag == 0) && (memcmp(m->sourcemac, xmosACdata.nextYNodeMac, 6) < 0))
                {
                  memcpy(xmosACdata.nextYNodeMac, m->sourcemac, 6);
                }
                else
                {
                  xmosACdata.YendFlag = 0;
                  memcpy(xmosACdata.nextYNodeMac, m->sourcemac, 6);
                }
              }
            
              if (i->ttl == 255)
              {
                // Are they on the same chain but before us?
                if (x->payload[0] > xmosACdata.myCommandTTL)
                {
                  xmosACdata.XstartFlag = 0;
                }
                // Are they the next node on our chain?
                else if (x->payload[0] < xmosACdata.myCommandTTL)
                {
                  memcpy(xmosACdata.nextXNodeMac, m->sourcemac, 6);
                  xmosACdata.XendFlag = 0;
                }
              }
              break;
            case (XMOS_AC_3):
              memcpy( (void*)m->sourcemac, (void*)addresses->macAddress, 6);
              if (xmosACdata.XstartFlag && xmosACdata.YstartFlag)
              {
                x->payload[6] = START_IP_Y;
                x->payload[7] = START_IP_X;
                addresses->ipAddress[2] = START_IP_Y;
                addresses->ipAddress[3] = START_IP_X;                                
                sendACforwardPackets(packet, addresses, cTx);
              }
            break;
            case (XMOS_AC_4):
              memcpy( (void*)m->sourcemac, (void*)addresses->macAddress, 6);
              if (memcmp(x->payload, addresses->macAddress, 6) == 0)
              {
                addresses->ipAddress[2] = x->payload[6];
                addresses->ipAddress[3] = x->payload[7];
                sendACforwardPackets(packet, addresses, cTx);
              }
            break;
          }
        }
      }
    }
  }
}

void ethServerProcessICMP(s_packetMac *m, s_addresses *addresses, s_packet *packet, unsigned cTx, int *null)
{
  s_packetIp *i; 
  s_packetIcmp *icmp;
    
  i = (s_packetIp *)m->payload;
  icmp = (s_packetIcmp *)i->payload;
    
  // Check if targetting our IP address
  if (memcmp(i->dest, addresses->ipAddress, 4) == 0)
  {
    // Check if valid IP echo request
    if (icmp->type == ICMP_ECHOREQUEST
        && icmp->code == ICMP_CODE)
    {
      unsigned short *ptr;
      unsigned icmpchecksum=0;
      int j;
      // Create reply
      icmp->type = ICMP_ECHOREPLY;
        
      // Recalculate ICMP checksum
      icmp->checksum = 0;
      ptr = (unsigned short *)&icmp->type;
      for (j=0; j < (getShort(i->length) - IP_SIZE) >> 1; j++)
      {
      	icmpchecksum += getShort(ptr[j]);
      }
      icmp->checksum = ~getShort((icmpchecksum & 0xFFFF) + (icmpchecksum >> 16));
        
      // Reverse destination and source MAC addresses
      memswap(m->destmac, m->sourcemac, 6);
       
      // Reverse destination and source IP addresses
      memswap(i->dest, i->source , 4);
        
      // Transmit packet in direction it came from
      ethPhyTx(cTx, packet, null);
    }
  }
}

void ethServerProcessARP(s_packetMac *m, s_addresses *addresses, s_packet *packet, unsigned cTx, int *null)
{
  s_packetArp *a = (s_packetArp *)m->payload;
  
  if (getShort(a->htype)     == ARP_ETHERNET
      && getShort(a->ptype)  == ARP_PROTO_IP
      && getChar(a->hlen)    == ARP_ETHERNET_HLEN
      && getChar(a->plen)    == ARP_IP_PROTOLEN)
  {
    // Valid ARP packet
    // Look for requests
    if (getShort(a->oper) == ARP_REQUEST)
    {
  	// Check if asked for location of my IP address
      if (memcmp(a->tpa, addresses->ipAddress, 4) == 0)
      {
        // Requested me
        a->oper = getShort(ARP_REPLY);
        memcpy(a->tha, a->sha, 6);
        memcpy(a->tpa, a->spa, 4);
        memcpy(a->sha, addresses->macAddress, 6);
        memcpy(a->spa, addresses->ipAddress, 4);
        
        // Reverse destination and source MAC addresses
        memcpy(m->destmac, m->sourcemac, 6);
        memcpy(m->sourcemac, addresses->macAddress, 6);
        
        // Tranmit packet in direction it came from
        ethPhyTx(cTx, packet, null);
      }
    }
  }
}

void ethServerProcessTTFP(s_packetIp *i, s_packet *packet, unsigned cTx, unsigned cFlash)
{
  s_packetUdp *u;
  u = (s_packetUdp *)i->payload;
           
  if(getShort(u->destport) == PORT_TFTP)
  {
    processTftp(packet, (s_packetTftp *)u->payload, cTx, cFlash);
  }
}

