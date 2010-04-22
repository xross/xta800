/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/camera/sccb.xc
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#include <xs1.h>
#include <xclib.h>
#include "sw_comps_common.h"

//times are in units of 10ns
#define I2C_WAIT_LOW_HALF 150000
#define I2C_WAIT_HIGH_HALF 15000
#define I2C_WAIT_HIGH 30000

#define I2C_START_HOLD I2C_WAIT_HIGH
#define I2C_STOP_SETUP I2C_WAIT_HIGH

#define I2C_PAUSE 100000
 #define iic_scl_high_time                           6000
#define iic_scl_low_time                              1300
 #define iic_bus_free_time                           600
 #define iic_start_cond_setup_time       600
 #define iic_start_cond_hold_time        600
 #define iic_write_cycle_time        500000  // 5ms
 #define iic_write_ack_poll_time     5000
timer IIC_timer;

extern port p_iic_sda;
extern port p_iic_scl;


//
// Wait for the specified amount of time
void iic_phy_wait(unsigned delay)
{
   uint time;
   IIC_timer :> time;
   IIC_timer when timerafter (time + delay) :> uint tmp;
}


//
// Receive bit
//
uint iic_phy_receiveBit()
{
  uint bit;
  uint time;

  IIC_timer :> time;
  p_iic_sda :> int tmp;

  time += iic_scl_low_time;
  IIC_timer when timerafter(time) :> time;
  p_iic_scl <: 1;        // set clock high

  p_iic_sda :> bit;

  time += iic_scl_high_time;
  IIC_timer when timerafter(time) :> time;
  p_iic_scl <: 0;        // set clock low

  return bit;
}


//
// Generate start transfer
//
void iic_phy_master_start()
{
   p_iic_scl <: 1;
   p_iic_sda <: 1;
   iic_phy_wait(iic_start_cond_setup_time);

   p_iic_sda <: 0;
   iic_phy_wait(iic_start_cond_hold_time);

   p_iic_scl <: 0;
}

//
// Generate stop transfer
//
void iic_phy_master_stop()
{
   p_iic_sda <: 0;
   iic_phy_wait(iic_scl_low_time);
   p_iic_scl <: 1;
   iic_phy_wait(iic_scl_high_time);

   p_iic_sda <: 1;
   iic_phy_wait(iic_bus_free_time);
}

//
// Send byte
//
XMOS_RTN_t iic_phy_sendByte(uint inByte, uint expectAck)
{
  uint bitCount;
  uint bit;
  uint byte;
  uint ack;
  uint time;
  XMOS_RTN_t returnCode = XMOS_SUCCESS;

  byte = inByte;
  bitCount = 0;
  byte = bitrev(byte);
  byte >>= 24;

  IIC_timer :> time;

  while (bitCount < 8)
  {
    bit = (byte & 0x80) >> 7;

    p_iic_sda <: bit;

    time += iic_scl_low_time;
    IIC_timer when timerafter(time) :> time;
    p_iic_scl <: 1;        // set clock high

    byte = byte << 1;
    bitCount = bitCount + 1;

    time += iic_scl_high_time;
    IIC_timer when timerafter(time) :> time;
    p_iic_scl <: 0;        // set clock low
  }

  p_iic_sda :> int tmp;

  ack = iic_phy_receiveBit();
  if (ack != expectAck)
  {
    returnCode = XMOS_FAIL;
  }

  return returnCode;
}



void WaitUntil(timer t, unsigned time)
{
  unsigned temp;
  t when timerafter(time) :> temp;
}

void PeekUntil(port p, int val)
{
  int temp;
  {
    p when pinseq(val) :> temp;
  }
}

int MasterWaitForAck(timer t, out port SCL, port SDAin)
{
  unsigned time;
  int x;
  t :> time;
  time += I2C_WAIT_LOW_HALF;
  SDAin :> int tmp;
  {
    //in port SDAin = SDA;
    time += I2C_WAIT_LOW_HALF;
    SCL @ time <: 1;

    SDAin @ time :> x;

    time += I2C_WAIT_HIGH;
    SCL @ time <: 0;

    WaitUntil(t,time);
  }
  SDAin <: 0;

  return x;
}

int MasterWrite(timer t, unsigned  value)
{
  unsigned time;
  int i;

  value = bitrev(value);
  value = value >> 24;

  for (i=0; i < 8; i += 1)
  {
    t :> time;
    time += I2C_WAIT_LOW_HALF;
    t when timerafter(time) :> int tmp;

    p_iic_sda <: >> value;
    //value = value >> 1;

    t :> time;
    time += I2C_WAIT_LOW_HALF;
    t when timerafter(time) :> int tmp;
    p_iic_scl <: 1;

    t:>time;
    t when timerafter(time += I2C_WAIT_HIGH) :> int tmp;
    p_iic_scl  <: 0;

  }

  p_iic_sda :> int tmp;
  //WaitUntil(t,time);

  return 1;
}

unsigned sccbRegWritedd(unsigned address, unsigned value)
{
  XMOS_RTN_t retVal = XMOS_SUCCESS;

  iic_phy_master_start();

  retVal = iic_phy_sendByte(0x42, 0);

  retVal = iic_phy_sendByte(address, 0);

  retVal = iic_phy_sendByte(value, 0);

  iic_phy_master_stop();

  return retVal;
}

unsigned sccbRegWrite(unsigned  address, unsigned value)
{
  unsigned temp, temp2;
  unsigned time;
  timer t;

  iic_phy_master_start();
  /*// Set SDA and SCL high
  p_iic_scl <: 1;
  p_iic_sda <: 1;

  //Check nothing is forcing either port low.
  PeekUntil(p_iic_scl,1);
  PeekUntil(p_iic_sda,1);

  // Master start
  t :> time;

  //Force SDA low
  p_iic_sda <: 0;

  //Wait until hold time passed then set SCL low
  t when timerafter(time + I2C_START_HOLD) :> int tmp;
  p_iic_scl <: 0;
  */

  //temp = iic_phy_sendByte(0x42, 0);
  // Send data
  temp = MasterWrite(t, 0x42);
  temp2 = MasterWaitForAck(t,p_iic_scl,p_iic_sda);

  temp = MasterWrite(t,  address);
  temp2 += MasterWaitForAck(t, p_iic_scl, p_iic_sda);

  // default: 0x24
  // colour lines : 0x26
  // mirror : 64

  temp = MasterWrite(t, value);  //0x26
  temp2 += MasterWaitForAck(t, p_iic_scl, p_iic_sda);

  // Master Stop
  t :> time;
  time += I2C_WAIT_LOW_HALF;
  t when timerafter(time) :> int tmp;
  p_iic_sda <: 0;

  t :> time;
  time += I2C_WAIT_LOW_HALF;
  t when timerafter(time) :> int tmp;
  p_iic_scl <: 1;

  t :> time;
  time += I2C_STOP_SETUP;
  t when timerafter(time) :> int tmp;
  p_iic_sda <: 1;

  t :> time;
  t when timerafter(time + I2C_PAUSE) :> int tmp;
  //targettime += I2C_PAUSE;
  //WaitUntil(t,targettime);

  p_iic_scl <: 1;
  p_iic_sda <: 1;

  return temp2;
}

unsigned MasterRead(timer t, out port SCL, port SDA, int value)
{
  unsigned time;
   unsigned x = 0;
  int i;
  t :> time;
  time += I2C_WAIT_LOW_HALF;
  {
    //in port SDAin = SDA;

    for(i = 0; i< 8; i+=1)
    {
      time += I2C_WAIT_LOW_HALF;
      SCL @ time <: 1;

      SDA @ time :> >> x;

      time += I2C_WAIT_HIGH;
      SCL @ time <: 0;

      WaitUntil(t,time);
    }
  }
  SDA <: 0;

  return x;
}




