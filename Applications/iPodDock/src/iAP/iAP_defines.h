
#ifndef __IAP_DEFINES_H__
#define __IAP_DEFINES_H__

/* 
 * Some useful macros for the iAP
 * protocol stack
 */

#define IntToCharArray(pkt, i, v) { \
  pkt[i] = (v >> 24) & 0xFF; \
  pkt[i+1] = (v >> 16) & 0xFF; \
  pkt[i+2] = (v >> 8) & 0xFF; \
  pkt[i+3] = v & 0xFF; \
}
#define ShortToCharArray(pkt, i, v) { \
  pkt[i] = (v >> 8) & 0xFF; \
  pkt[i+1] = v & 0xFF; \
}

#define CharArrayToInt(pkt, i) ((pkt[i] << 24) | (pkt[i+1] << 16) | (pkt[i+2] << 8) | pkt[i+3])
#define CharArrayToShort(pkt, i) ((pkt[i] << 8) | pkt[i+1])

/*
 * New memcpy for xc with no pointers.
 * Room for optimisation
 */
#define smemcpy(d, dp, s, sp, l) { int i; for (i = 0; i < l; i++) d[dp+i] = s[sp+i]; }

typedef enum
{
  generalLingo = 0x00,
  microphoneLingo = 0x01,
  simpleLingo = 0x02,
  displayLingo = 0x03,
  extendedLingo = 0x04,
  powerLingo = 0x05,
  rftunerLingo = 0x07,
  equalizerLingo = 0x08,
  sportsLingo = 0x09,
  audioLingo = 0x0A,
  storageLingo = 0x0C,
  locationLingo = 0x0E,
  ctrlLingo = 0xFE,
  unsolicitedLingo = 0xFF,
} e_lingos;

#endif //__IAP_DEFINES_H__

