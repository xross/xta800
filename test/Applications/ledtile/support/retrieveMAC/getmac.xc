/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>

#define OTP_DATA_PORT XS1_PORT_32B
#define OTP_ADDR_PORT XS1_PORT_16C
#define OTP_CTRL_PORT XS1_PORT_16D

#define OTPADDRESS 0x7FF
#define OTPREAD 1
#define OTP_tACC_TICKS 4 // 40nS

port otp_data = OTP_DATA_PORT;
out port otp_addr = OTP_ADDR_PORT;
port otp_ctrl = OTP_CTRL_PORT;

static int otpRead(unsigned address)
{
  unsigned value;
  timer t;
  int now;
  
  otp_ctrl <: 0;
  otp_addr <: 0;
  otp_addr <: address;
  sync(otp_addr);
  otp_ctrl <: OTPREAD;
  sync(otp_addr);
   t :> now;
   t when timerafter(now + OTP_tACC_TICKS) :> now;
  otp_data :> value;
  otp_ctrl <: 0;

  return value;
}

int retrieveBitmap(unsigned int &bitmap, unsigned int &address)
{
  int validbitmapfound = 0;
  
  address = OTPADDRESS;
  while (!validbitmapfound)
  {      
    bitmap = otpRead(address);
#ifdef VERBOSE
  printhexln(bitmap);
#endif  

    if (bitmap >> 31)
    {
      // Bitmap has not been written
      return 2;
    }
    else if (bitmap >> 30)
    {
      validbitmapfound = 1;
    }
    else
    {
      unsigned headersize = (bitmap >> 25) & 0x1F;
      if (headersize == 0)
        headersize = 8;
      // Invalid bitmap
      address -= headersize;
    }
  }
  return !validbitmapfound;
  
}


int getMacAddr(unsigned MACAddrNum, unsigned macAddr[2])
{
  unsigned address = OTPADDRESS;
  unsigned bitmap;

  if (MACAddrNum < 7)
  {
    if (retrieveBitmap(bitmap, address))
      return 2;
    
    if (((bitmap >> 22) & 0x7) > MACAddrNum)
    {
      address -= ((MACAddrNum << 1) + 1);
      macAddr[0] = otpRead(address);
      address--;
      macAddr[1] = otpRead(address);
      return 0;
    }
    else
    {
      // MAC Address cannot be found
      return 3;
    }
  }
  else
  {
    // Incorrect number of MAC addresses given
    return 1;
  }
}
