/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#include "chanAbs.h"
#include "print.h"

void genericwrite(unsigned psctrl, unsigned reg, unsigned val)
{
  unsigned dest,c;
  dest = ((psctrl ? getprocnum() : 0) << 16) | 
         ((psctrl ? XS1_CT_PSCTRL : XS1_CT_SSCTRL) << 8) |
         XS1_RES_TYPE_CONFIG;
  c = getchanend(dest);
  outct(c, XS1_CT_WRITEC);
  outuchar(c, (c >> 24));
  outuchar(c, (c >> 16));
  outuchar(c, (c >>  8));
  outuchar(c, (reg >> 8));
  outuchar(c, reg);
  outuint(c, val);
  outct(c, XS1_CT_END);
  checkct(c, XS1_CT_ACK);
  checkct(c, XS1_CT_END);
  freechanend(c);
}

unsigned genericread(unsigned psctrl, unsigned reg)
{
  unsigned dest,c,val;
  dest = ((psctrl ? getprocnum() : 0) << 16) | 
         ((psctrl ? XS1_CT_PSCTRL : XS1_CT_SSCTRL) << 8) |
         XS1_RES_TYPE_CONFIG;
  c = getchanend(dest);
  outct(c, XS1_CT_READC);
  outuchar(c, (c >> 24));
  outuchar(c, (c >> 16));
  outuchar(c, (c >>  8));
  outuchar(c, (reg >> 8));
  outuchar(c, reg);
  outct(c, XS1_CT_END);
  checkct(c, XS1_CT_ACK);
  val = inuint(c);
  checkct(c, XS1_CT_END);
  freechanend(c);
  return val;
}

void pswrite(unsigned reg, unsigned val)
{
  genericwrite(1, reg, val);
}

unsigned psread(unsigned reg)
{
  return genericread(1, reg);
}

void sswrite(unsigned reg, unsigned val)
{
  genericwrite(0, reg, val);
}

unsigned ssread(unsigned reg)
{
  return genericread(0, reg);
}

void psset(unsigned psreg, unsigned val)
{
  psreg = (psreg << 8) | 0xb;
  __asm__ __volatile__ ("set ps[%0], %1": : "r" (psreg) , "r" (val));
}