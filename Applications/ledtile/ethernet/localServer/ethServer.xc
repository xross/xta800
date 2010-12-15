/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include "xs1.h"
#include "ethPkt.h"
#include "ethSwitch.h"
#include "ethServerProcess.h"
#include "flashManager.h"
#include "ledprocess.h"
#include "watchdog.h"

#define TIMEOUT 100000000
#define sendMac(c, s) { outuint(c, s.macAddress[0]);  outuint(c, s.macAddress[1]); }


void read_spi_gamma(int offset, char val, chanend cLedCmd, chanend cFlash)
{
  unsigned short gbuf[256];
  int i;
  unsigned retval;
  cFlash <: SPI_FLASH_READGAMMA;
  master
  {
    cFlash <: offset;
    cFlash <: 256;
    for (i=0; i<256; i++)
    {
      unsigned val;
      cFlash :> val;
      gbuf[i] = (unsigned short)val;
    }
  }

  
  do
  {
    // Store in command buffer
    // Response indicates if buffer is full 
    master
    {

      cLedCmd <: 256+2;
      cLedCmd :> retval;
      if (retval == 0)
      {
        cLedCmd <: (unsigned int)XMOS_GAMMAADJ;
        cLedCmd <: (unsigned int)val;
        for (i=0; i<256; i++)
          cLedCmd <: (unsigned int)gbuf[i];        
      }
    }
    {
      timer t;
      int now;
      t :> now;
      t when timerafter (now + 10000) :> now;
    }
  } while (retval != 0);
}

// Standard local ethernet server
// Supports ICMP, ARP, DHCP(TODO), TFTP
// Also supports user-definable protocols with two user defined channels
#pragma unsafe arrays
void ethServer(chanend cRx, chanend cTx, chanend cLedData, chanend cLedCmd, chanend cSpiFlash, chanend cWdog)
{
  s_packet packet;
  s_addresses addresses;
  timer t;
  int time, direction;
  int num_words;
  unsigned int packet_received;
  
  // Initialise addresses
  initAddresses(addresses);
  
  t :> time;
  
  // Initialise communications with ethernet switch
  start_streaming_slave(cTx);
  start_streaming_slave(cRx);
  
#ifdef LOADGAMMAFROMSPI
  // Read gamma tables from SPI Flash
  read_spi_gamma(0, 'R', cLedCmd, cSpiFlash);
  read_spi_gamma(1, 'G', cLedCmd, cSpiFlash);
  read_spi_gamma(2, 'B', cLedCmd, cSpiFlash);
#endif
  
  // Request data from buffer
  outuint(cRx, 0);
  while (1)
  {

    select
    {
      case t when timerafter (time + SERV_WAIT) :> time:
        cWdog <: (int)1;
        break;
      case inuint_byref(cRx, packet_received):
          if (packet_received == 0)
          {
            num_words = inuint(cRx) - 1; // Absorb num_words
            packet.plen_b = inuint(cRx);
      	  
            // Receive ethernet packet from switch
            while (num_words)
            {
              num_words--;
              packet.pdata[num_words] = inuint(cRx);
            }
#ifdef MII_TX_TIMESTAMPS
            inuint(cRx);
#endif

            // Process the packet in the server
            ethServerProcess(packet, cTx, cLedData, cLedCmd, cSpiFlash, addresses, direction);
          }
          outuint(cRx, 0);
        break;
    }
  }
  
}
