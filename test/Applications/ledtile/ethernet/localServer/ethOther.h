/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef ETHOTHER_H_
#define ETHOTHER_H_

#include "ethPkt.h"
#include "led.h"
// ---------

// Structures for XMOS packets
// --------------------------
#define OTHER
#define XMOS_SIZE 6

typedef struct
{
  unsigned char macPrefix[6];
  unsigned char ipPrefix[4];
} s_packetMacSet;

typedef struct
{
  unsigned char colchan[2];
  unsigned short gammaTable[256];
} s_packetGammaTable;

typedef struct
{
  unsigned char colchan[2];
  unsigned short intensity;
} s_packetIntensity;

typedef struct
{
  unsigned char colchan;
  unsigned char x;
  unsigned char y;
  unsigned char dummy;
  unsigned short intensity;
  unsigned short dummy2;
} s_packetIntensityPix;

typedef struct
{
  unsigned char drivertype;
  unsigned char dummy[3];
} s_packetDriverType;


typedef struct
{
  unsigned short segwidth;
  unsigned short segheight;
  unsigned short segid;
  unsigned short pixptr;
  unsigned short datalen;
  unsigned short dummy;
  // data is now word aligned
  unsigned char data[ETH_FRAME_SIZE - MAC_SIZE - IP_SIZE - UDP_SIZE - 6 - 10];
} s_packetData;

typedef struct
{
  unsigned char  magicNumber[4];
  unsigned short identifier;
  unsigned char  payload[ETH_FRAME_SIZE - MAC_SIZE - IP_SIZE - UDP_SIZE - 6];
} s_packetXmos;
// --------------------------

// ethOtherProcess
// Called by base ethernet server when "OTHER" is defined and packet does not match other protocols
#ifdef __XC__
void ethOtherProcess(s_packet &packet, chanend cTx, chanend cOther0, chanend cOther1, chanend cOther2, s_addresses &addresses, int direction);
#else
void ethOtherProcess(s_packet *packet, unsigned cTx, unsigned cOther0, unsigned cOther1, unsigned cOther2, s_addresses *addresses, int direction);
#endif

void ethServerProcessICMP(s_packetMac *m, s_addresses *addresses, s_packet *packet, unsigned cTx, int *null);
void ethServerProcessARP(s_packetMac *m, s_addresses *addresses, s_packet *packet, unsigned cTx, int *null);
void ethServerProcessTTFP(s_packetIp *i, s_packet *packet, unsigned cTx, unsigned cFlash);

#endif /*ETHOTHER_H_*/
