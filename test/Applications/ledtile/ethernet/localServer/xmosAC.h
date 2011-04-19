/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef XMOSAC_H_
#define XMOSAC_H_


typedef struct
{
  unsigned char myCommandTTL;
  
  unsigned char XstartFlag;
  unsigned char YstartFlag;
  unsigned char XendFlag;
  unsigned char YendFlag;
  
  unsigned char nextXNodeMac[6];
  unsigned char nextXNodeFlag;
  
  unsigned char nextYNodeMac[6];
  unsigned char nextYNodeFlag;
} s_xmosAC;

#endif /*XMOSAC_H_*/
