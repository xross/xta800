/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include "xclib.h"
#include "ethSwitch.h" 
#include "watchdog.h"

#define getMiiData(var, c) { outuint(c, 0); var = inuint(c); }

// ethPhyRx
// Receive packet over channel and place in s_packet structure
// Returns 1 for success, 0 for invalid packet
#pragma unsafe arrays
int ethPhyRx(chanend c, s_packet &pkt, unsigned numbytes)
{
  int null;
  int numwords = (numbytes >> 2) + 1;
  int bufindex = 0;

  pkt.plen_b = numbytes;
  while (numwords)
  {
      #pragma xta label "ethPhyRx_loop"
	  numwords--;
	  getMiiData(pkt.pdata[bufindex], c);
	  bufindex++;
  }
  // Absorb crc
  getMiiData(null, c);
  // Receive Timestamp
  getMiiData(pkt.timestamp, c);
  pkt.timestamp = byterev(pkt.timestamp);
  
  return 1;
}

#pragma unsafe arrays
void localTx(chanend cTx, s_packet &pkt, int &tstamp)
{
  int numwords = (pkt.plen_b + 3)>>2;
  int retval;
  master
  {
    cTx <: numwords + 1;
    cTx :> retval;
    if (!retval)
    {
      cTx <: pkt.plen_b;
      while (numwords)
      {
        #pragma xta label "localTx_loop"
        numwords--;
        cTx <: pkt.pdata[numwords];
      }
    }
  }
}


// ethPhyTx
// Send packet over channel from s_packet structure
#pragma unsafe arrays
void ethPhyTx(chanend cTx, s_packet &pkt, int &tstamp)
{
  int numwords = (pkt.plen_b + 3)>>2;
  
  master
  {
    cTx <: pkt.plen_b;
    while (numwords)
    {
      #pragma xta label "ethPhyTx_loop"
      numwords--;
      cTx <: pkt.pdata[numwords];
    }
#ifdef MII_TX_TIMESTAMPS
    cTx :> tstamp;
#else
    tstamp = 0;
#endif
  }
}

#pragma unsafe arrays
void ethTx(chanend cTx, s_packet &pkt)
{
  int numwords = (pkt.plen_b + 3)>>2;
   
  outuint(cTx, pkt.plen_b);
  outuint(cTx, pkt.ptpcorrect);
  outuint(cTx, pkt.timestamp);
  while (numwords)
  {
    #pragma xta label "ethTx_loop"
    numwords--;
    outuint(cTx, pkt.pdata[numwords]);
  }
}

#define getSecondShort(a) (byterev(a) & 0xFFFF)
#define getFirstShort(a)  (byterev(a) >> 16 )
#define getByte(data, ptr) (unsigned char[], data)[ptr])
#define getShort(data, ptr) (((unsigned char[], data)[ptr] << 8) | ((unsigned char[], data)[ptr+1]))
#define getPWord(data, ptr) (((unsigned char[], data)[ptr] << 24) | ((unsigned char[], data)[ptr+1]  << 16) \
                            ((unsigned char[], data)[ptr+2] << 8) | ((unsigned char[], data)[ptr+3]))

int adjustTTL(s_packet &pkt)
{
  if ((pkt.pdata[3] & 0xFFFF) == 0x0008)
  {
    int ttl = (pkt.pdata[5] >> 16) & 0xFF;
    if (ttl)
    {
      int j = 0;
      unsigned ipchecksum;
      
      ttl--;
      pkt.pdata[5] &= 0xFF00FFFF;
      pkt.pdata[5] |= ttl << 16;
      
      pkt.pdata[6] &= 0xFFFF0000;
      
      ipchecksum = getSecondShort(pkt.pdata[3]);
            
      while (j < 4)
      {
        #pragma xta label "adjustTTL_loop"
        ipchecksum += getSecondShort(pkt.pdata[4+j]) + getFirstShort(pkt.pdata[4+j]);
        ++j;
      }
      
      ipchecksum += getFirstShort(pkt.pdata[8]);      

      ipchecksum = ~((ipchecksum & 0xFFFF) + (ipchecksum >> 16));

      pkt.pdata[6] |= ((ipchecksum >> 8) & 0xFF) | ((ipchecksum & 0xFF) << 8);
    }
    else
    {
      return 0;
    }
  }
  return 1;
}

#ifdef PTPAWARE
int correctPTPcheck(s_packet &pkt)
{
  unsigned ptr = 12;
  // Check for IP protocol
  pkt.ptpcorrect = 0;
  if (getShort(pkt.pdata, ptr) == 0x0800)
  {
    ptr += 2;
    // Check for UDP protocol
    if (getByte(pkt.pdata, ptr + 9) == 0x11)
    {
      ptr += (getByte(pkt.pdata, ptr) & 0xF) << 2;
      // Check for PTP port
      if (getShort(pkt.pdata, ptr+2) == 319 || getShort(pkt.pdata, ptr+2) == 320)
      {
        ptr += 8; // Add UDP
        ptr += 10; // PTP header + 2 bytes
        pkt.ptpcorrect = ptr >> 2; // Guaranteed to be word aligned
      }
    }
  }
  
}
#endif

// ethSwitch
// Layer 2 ethernet switch framework
// Supports two external interfaces, and one local
#pragma unsafe arrays
void ethSwitch(chanend cExtRx0, chanend cExtRx1, chanend cLocRx,
                 chanend cExtTx0, chanend cExtTx1, chanend cLocTx,
                 chanend cWdog)
{
  timer t;
  int time;
  unsigned mymacaddress[2] = {0x0, 0x0};
  unsigned macset = 0;
  unsigned broadcastaddress[2] = {0xFFFFFFFF, 0xFFFF0000};
  s_packet packet;
  int timestamp;
  unsigned int numbytes;
  
  // Init channel comms to clients
  start_streaming_master(cExtRx0);
  start_streaming_master(cExtRx1);
  start_streaming_master(cLocRx);
  start_streaming_master(cExtTx0);
  start_streaming_master(cExtTx1);
  set_thread_fast_mode_on();

  // Initially request data from external receivers
  outuint(cExtRx0, 0);
  outuint(cExtRx1, 0);
  
  t:> time;
  while(1)
  {
#pragma ordered
    select
    {
      #pragma xta endpoint "watchdog"
      case t when timerafter (time + SWITCH_WAIT) :> time:
        cWdog <: (int)1;
        break;

      // Received data from external receiver 0
      #pragma xta endpoint "recieve_external_0"
      case inuint_byref(cExtRx0, numbytes):
        // Retrieve and validate packet
        if (ethPhyRx(cExtRx0, packet, numbytes))
        {
          // Do MAC comparison
          if ((getWord(packet.pdata[0]) == mymacaddress[0]) && ((getWord(packet.pdata[1]) & 0xFFFF0000) == mymacaddress[1]))
          {
            // Packet is destined for local node
            localTx(cLocTx, packet, timestamp);
          }
          else if (((getWord(packet.pdata[0]) == broadcastaddress[0]) && ((getWord(packet.pdata[1]) & 0xFFFF0000) == broadcastaddress[1]))  || macset == 0 )
          {
            // Packet is broadcast
            localTx(cLocTx, packet, timestamp);
            if (adjustTTL(packet))
              ethTx(cExtTx1, packet);
          }
          else
          {
            // Packet is neither broadcast or local, forward onwards
            if (adjustTTL(packet))
              ethTx(cExtTx1, packet);
          }
        }
        // Request more data
        outuint(cExtRx0, 0);
        break;

      // Received data from external receiver 1
      #pragma xta endpoint "recieve_external_1"
      case inuint_byref(cExtRx1, numbytes):
      // Retrieve and validate packet
        if (ethPhyRx(cExtRx1, packet, numbytes))
        {
          // Do MAC comparison
          if ((getWord(packet.pdata[0]) == mymacaddress[0]) && ((getWord(packet.pdata[1]) & 0xFFFF0000) == mymacaddress[1]))
          {
          // Packet is destined for local node
            localTx(cLocTx, packet, timestamp);
          }
          else if (((getWord(packet.pdata[0]) == broadcastaddress[0]) && ((getWord(packet.pdata[1]) & 0xFFFF0000) == broadcastaddress[1]))  || macset == 0 )
          {
            // Packet is broadcast
            localTx(cLocTx, packet, timestamp);
            if (adjustTTL(packet))
              ethTx(cExtTx0, packet);
          }
          else
          {
            // Packet is neither broadcast or local, forward onwards
            if (adjustTTL(packet))
              ethTx(cExtTx0, packet);
          }
        }
        // Request more data
        outuint(cExtRx1, 0);
        break;

      // Received data from local node
      #pragma xta endpoint "recieve_local_node"
      case slave {
                    cLocRx :> numbytes;
                    // Receive data into packet structure
                    packet.plen_b = numbytes;
                    {
                      int numwords = (numbytes + 3) >> 2;
                      while (numwords)
                      {
                        #pragma xta label "recieve_local_node_loop"
                        numwords--;
                        cLocRx :> packet.pdata[numwords];
                      }
                    }
#ifdef MII_TX_TIMESTAMPS
                    cLocRx :> int _;
#endif
                 }:
        // Update local MAC
        mymacaddress[0] = ((getWord(packet.pdata[1]) & 0x0000FFFF) << 16) | ((getWord(packet.pdata[2]) & 0xFFFF0000) >> 16);
        mymacaddress[1] = ((getWord(packet.pdata[2]) & 0x0000FFFF) << 16);
        macset=1;
        
        ethTx(cExtTx1, packet);
        ethTx(cExtTx0, packet);
        break;   
    }
  }
}



