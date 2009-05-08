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
#include "ethTftp.h"
#include "flashManager.h"
#include "flashPhy.h"
#include "chanAbs.h"
#include "reset.h"
#include "sleep.h"

#define NUM_CORES 4
#define FIRMWARE_SPI_OFFSET 4
#define FIRMWARE_SPI_SECTOR 1

// 0 = no activity
// x = ready for block number (x - 1)
int tftpstate = 0;
int spiaddress = 0;


// Transmit a Tftp PACKET
void transmitTftp(s_packet *p, s_packetTftp *t, unsigned cTx, unsigned blocknum, unsigned opcode, char msg[])
{
  int null;
  unsigned short *ptr;
  s_packetMac *m;
   s_packetIp *i; 
   s_packetUdp *u;

   m = (s_packetMac *)p->pdata;
   i = (s_packetIp *)m->payload;
   u = (s_packetUdp *)i->payload;
  
  t->opcode = getShort(opcode);
  ptr = (unsigned short*)t->data;
  *ptr = getShort(blocknum);
  ptr++;
  strcpy((char *)ptr, msg);
  
  p->plen_b = MAC_SIZE + IP_SIZE + UDP_SIZE + TFTP_SIZE + strlen(msg) + 1;
  i->length = getShort(IP_SIZE + UDP_SIZE + TFTP_SIZE + strlen(msg) + 1);
  u->length = getShort(UDP_SIZE + TFTP_SIZE + strlen(msg) + 1);
  
  memswap(m->destmac, m->sourcemac, 6);
  memswap(i->dest, i->source , 4);
  memswap(&u->sourceport, &u->destport, 2);
  
  udpChecksum((s_packetUdp *)i->payload);
  ipChecksum(i);

  ethPhyTx(cTx, p, &null);
}

// processTftp
void processTftp(s_packet *p, s_packetTftp *t, unsigned cTx, unsigned cFlash)
{
  s_packetMac *m;
  s_packetIp *i; 
   s_packetUdp *u;

   m = (s_packetMac *)p->pdata;
   i = (s_packetIp *)m->payload;
   u = (s_packetUdp *)i->payload;

  if (t->opcode == getShort(TFTP_OPCODE_WRQ))
  {
    char *filename;
    char *datatype;
    
    filename = (char *)t->data;
    datatype = filename + strlen(filename) + 1;

    if (strncmp(datatype, "octet",5) == 0)
    {
      if (strncmp(filename, "firmware",8) == 0)
      {
        char retstr[25] = "Firmware   Updating.";
        int retvalue;
        tftpstate = 1;
        spiaddress = 0;
        
        out(cFlash, SPI_FLASH_NEWFIRMWARE);
        retvalue = in(cFlash);
        retstr[9] = '0' + (retvalue & 0xF);
        transmitTftp(p, t, cTx, 0, TFTP_OPCODE_ACK, retstr);
      }
      else if (strncmp(filename, "reset",5) == 0)
      { 
        // Ack with a nack
        transmitTftp(p, t, cTx,0, TFTP_OPCODE_ERROR, "Resetting Device.");
        sleep(10000);
        chipReset();
      }
      else
      {
        // NACK
        transmitTftp(p, t, cTx,0, TFTP_OPCODE_ERROR, "Filename not recognised.");
      }
    }
    else
    {
      transmitTftp(p, t, cTx,0, TFTP_OPCODE_ERROR, "Transfer mode not binary.");
    }
  }
  else if (t->opcode == getShort(TFTP_OPCODE_DATA) &&
      tftpstate != 0)
  {
    unsigned short *blocknum = (unsigned short *)t->data;
    if (*blocknum == getShort(tftpstate))
    {
      int j, datalen = getShort(u->length)-UDP_SIZE-TFTP_SIZE;
      unsigned char *dataptr = (unsigned char *)t->data + 2;

      out(cFlash, SPI_FLASH_WRITEFIRMWARE);
      chanStartMaster(cFlash);
      outuint(cFlash, spiaddress);
      outuint(cFlash, datalen);
      for (j=0; j<datalen; j++)
        outuint(cFlash, *dataptr++);
      chanStopMaster(cFlash);
      transmitTftp(p, t, cTx, tftpstate, TFTP_OPCODE_ACK, "Firmware Updating.");
      
      tftpstate++;
      if (datalen < 512)
        tftpstate = 0;
      spiaddress += datalen;
    }
    else
    {
      transmitTftp(p, t, cTx,0, TFTP_OPCODE_ERROR, "Error.");
      tftpstate = 0;
    }
  }
  
  return;
}
