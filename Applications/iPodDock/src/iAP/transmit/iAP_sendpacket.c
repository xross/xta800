#include <xs1.h>
#include "iAP.h"
#include "iPodHID.h"
#include "iAP_sendpacket.h"
#include "global.h"
#include <string.h>

extern int iAP_expect_transid;

#define SIZE_THRESHOLD 512
int iAP_sendpacket ( unsigned char lingoID, unsigned char commandID, unsigned datalength, unsigned char commandData[], int transid)
{
  char iAPPacket[SIZE_THRESHOLD + 4];
  int ptr;
  int i;
  signed char checksum;
  int enable_transid;
  int hdr_size = 2;

  enable_transid = (transid >= 0);
  
  iAPPacket[0] = 0x55;

  if (lingoID == extendedLingo) {
    hdr_size += 1;
  };

  if (datalength + hdr_size < 256)
  {
    iAPPacket[1] = datalength + hdr_size + (enable_transid << 1);
    checksum = (datalength + hdr_size + (enable_transid << 1));
    ptr = 2;
  }
  else if (datalength + hdr_size < SIZE_THRESHOLD)
  {    
    iAPPacket[1] = 0x00;
    iAPPacket[2] = (datalength + hdr_size + (enable_transid << 1)) >> 8;
    checksum = (datalength + hdr_size + (enable_transid << 1)) >> 8;
    iAPPacket[3] = (datalength + hdr_size + (enable_transid << 1)) & 0xFF;
    checksum += (datalength + hdr_size + (enable_transid << 1)) & 0xFF;
    ptr = 4;
  }
  else
  {
    return 1;
  }
  
  iAPPacket[ptr] = lingoID;
  ptr++;
  checksum += lingoID;
  if (lingoID == extendedLingo){
    iAPPacket[ptr] = 0x0;
    ptr++;
  };

  iAPPacket[ptr] = commandID;
  checksum += commandID;
  ptr++;
  
  if (enable_transid)
  {
    iAPPacket[ptr] = (transid >> 8) & 0xFF;
    ptr++;
    checksum += (transid >> 8) & 0xFF;
    iAPPacket[ptr] = transid & 0xFF;
    ptr++;
    checksum += transid & 0xFF;
    iAP_expect_transid = 1;
  }
  else
  {
    iAP_expect_transid = 0;
  }
  memcpy(&iAPPacket[ptr], commandData, datalength);
  for (i = 0; i < datalength; i++)
    checksum += commandData[i];
  iAPPacket[ptr+datalength] = -checksum;

  if (iPodHIDTx((unsigned char*)iAPPacket, datalength + ptr + 1))
    return 1;
  
  return 0;
}
