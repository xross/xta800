/*
 * @ModuleName  iic
 * @Author  Ali Dixon & Corin Rathbone
 * @Date  30/07/2008
 * @Version   1.1
 * @Description IIC bus interface
 *
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */ 


#include <xs1.h>
#include "print.h"
#include "iic.h"
#define EXPECT_ACK 0

#define IIC_WE 0
#define IIC_RE 1

#define iic_bittime                 (XS1_TIMER_KHZ / iic_speed_khz)

// times are in units of 10ns
#define iic_scl_high_time           (iic_bittime >> 1)
#define iic_scl_low_time            (iic_bittime >> 1)
#define iic_bus_free_time           iic_bittime
#define iic_start_cond_setup_time   iic_bittime
#define iic_start_cond_hold_time    iic_bittime
#define iic_write_cycle_time        500000  // 5ms
#define iic_write_ack_poll_time     5000 

extern port PORT_IIC_SCL;
extern port PORT_IIC_SDA;

timer IIC_timer;

#define SCLHIGH() \
  { \
  int onedetect = 0; \
  while (1) { \
    int val; \
    PORT_IIC_SCL :> val; \
    if (val == 1) { \
      onedetect++; \
      if (onedetect > 20) \
        break; \
    } else { \
      onedetect = 0; \
    } \
  } \
  }

// Wait for the specified amount of time
void iic_phy_wait(unsigned delay)
{
  unsigned int time;
  IIC_timer :> time;
  IIC_timer when timerafter (time + delay) :> unsigned int tmp;
}


// Release the bus
int iic_initialise()
{
  int returnCode = 0;
  unsigned int scl_value;
  unsigned int sda_value;

  scl_value = 0;
  sda_value = 0;

  while ((scl_value != 1) || (sda_value != 1))
  {
    PORT_IIC_SCL :> scl_value;
    PORT_IIC_SDA :> sda_value;
  }

  iic_phy_wait(10 * iic_bittime);

  scl_value = 0;
  sda_value = 0;
  while ((scl_value != 1) || (sda_value != 1))
  {
    PORT_IIC_SCL :> scl_value;
    PORT_IIC_SDA :> sda_value;
  }
  
  return returnCode;
}


// Generate start condition
void iic_phy_master_start()
{
  SCLHIGH();
  PORT_IIC_SDA <: 1;
  iic_phy_wait(iic_start_cond_setup_time);

  PORT_IIC_SDA <: 0;
  iic_phy_wait(iic_start_cond_hold_time);

  PORT_IIC_SCL <: 0;
}


// Generate stop condition
void iic_phy_master_stop()
{
  PORT_IIC_SDA <: 0;
  iic_phy_wait(iic_scl_low_time);
  SCLHIGH();
  iic_phy_wait(iic_scl_high_time);

  PORT_IIC_SDA <: 1;
  iic_phy_wait(iic_bus_free_time);
}


// Send a bit
unsigned int iic_phy_sendBit(unsigned int bit)
{
  unsigned int time;
  
  IIC_timer :> time;
  PORT_IIC_SDA <: bit;      
            
  time += iic_scl_low_time;   
  IIC_timer when timerafter(time) :> time;
  SCLHIGH();
  IIC_timer :> time;
  
  time += iic_scl_high_time;         
  IIC_timer when timerafter(time) :> time; 
  PORT_IIC_SCL <: 0;        // set clock low
    
  return 0;
}


// Receive a bit
unsigned int iic_phy_receiveBit()
{
  unsigned int bit;
  unsigned int time;
  
  IIC_timer :> time;
  PORT_IIC_SDA :> int tmp;
      
  time += iic_scl_low_time;   
  IIC_timer when timerafter(time) :> time;
  SCLHIGH();
  IIC_timer :> time;
  
  PORT_IIC_SDA :> bit;    
  
  time += iic_scl_high_time;
  IIC_timer when timerafter(time) :> time; 
  PORT_IIC_SCL <: 0;        // set clock low

  time += iic_scl_low_time;       
  IIC_timer when timerafter(time) :> time; 
  return bit;
}


// Send a byte 
int iic_phy_sendByte(unsigned int inByte, unsigned int expectAck)
{
  unsigned int bitCount;
  unsigned int bit;
  unsigned int byte;
  unsigned int ack;
  unsigned int time;

  byte = inByte;
  bitCount = 0;      

  IIC_timer :> time;
    
  while (bitCount < 8) 
  {
    
    bit = (byte & 0x80) >> 7; 
    time += (iic_scl_low_time>>1);   
    IIC_timer when timerafter(time) :> time;
   
    PORT_IIC_SDA <: bit;      
       
    time += (iic_scl_low_time>>1);   
    IIC_timer when timerafter(time) :> time;
    
    SCLHIGH();
    IIC_timer :> time;
  
    byte = byte << 1;
    bitCount = bitCount + 1;

    time += iic_scl_high_time;   
    IIC_timer when timerafter(time) :> time; 
    PORT_IIC_SCL <: 0;        // set clock low
  }
  
  PORT_IIC_SDA :> int tmp;

  ack = iic_phy_receiveBit();
  
  if (ack != expectAck)
  {
    return 1;
  }
  else
  {
    return 0;
  }
}


// Receive a byte
unsigned int iic_phy_receiveByte(unsigned int ack)
{
  unsigned int bitCount;
  unsigned int bit;
  unsigned int time;
  unsigned int byte;

  byte = 0;
  bitCount = 0;      

  IIC_timer :> time;
    
  // set to input  
  PORT_IIC_SDA :> int tmp;
          
  while (bitCount < 8) 
  {
    time += iic_scl_low_time;   
    IIC_timer when timerafter(time) :> time;
    SCLHIGH();
    IIC_timer :> time;
    
    PORT_IIC_SDA :> bit;    
      
    byte = (byte << 1) | bit;
    bitCount = bitCount + 1;
  
    time += iic_scl_high_time;     
    IIC_timer when timerafter(time) :> time; 
    PORT_IIC_SCL <: 0;        // set clock low
  }
    
  PORT_IIC_SDA :> int tmp;

  iic_phy_sendBit(ack);
  
  return byte;
}


// Poll to determine when write completes
unsigned int iic_checkWriteComplete(unsigned int address)
{ 
  unsigned int time;
    
  IIC_timer :> time;

  iic_phy_master_start(); 
  do
  {    
    iic_phy_master_stop();
    IIC_timer when timerafter(time + iic_write_ack_poll_time) :> time; 
    iic_phy_master_start();  
  }  while (iic_phy_sendByte((address << 1) | IIC_WE, EXPECT_ACK));
  
  if (iic_phy_sendByte(0, EXPECT_ACK))
  {
  }
  
  if (iic_phy_sendByte(0, EXPECT_ACK))
  {
  }
  
  iic_phy_master_stop();
  iic_phy_master_start();  
  
  iic_phy_sendByte((address << 1) | IIC_WE, EXPECT_ACK);
  return 0;
}


// Write to IIC device
int iic_write(unsigned int address, unsigned int reg, char data[], unsigned int numBytes)
{
  unsigned int i = 0;
  iic_phy_master_start();

  if (iic_phy_sendByte((address << 1) | IIC_WE, EXPECT_ACK))
    return 4;
  if (iic_phy_sendByte((reg), EXPECT_ACK))
    return 3;
  
  for ( i=0; i<numBytes; i++)
  {
    if (iic_phy_sendByte(data[i], EXPECT_ACK))
      break;
  }
    
  iic_phy_master_stop();  
  iic_checkWriteComplete(address);

  return 0;
}

// Read from IIC device
// It receives numBytes, acking each one and does not ack the final byte
int iic_read(unsigned int address, unsigned int reg, char data[], unsigned int numBytes)
{ 
  unsigned int i; 
  
  iic_phy_master_start();
  
  if (iic_phy_sendByte((address << 1) | IIC_WE, EXPECT_ACK))
    return 1;
  if (iic_phy_sendByte((reg), EXPECT_ACK))
    return 1;
  
  iic_phy_master_start();
  if (iic_phy_sendByte((address << 1) | IIC_RE, EXPECT_ACK))
    return 1;
  
  for (i=0; i<numBytes-1; i++)
  {
    data[i] = iic_phy_receiveByte(0);
  }
  
  // receive final byte and dont ack to signal end of transfer
  data[i] = iic_phy_receiveByte(1);
  iic_phy_master_stop();
  
  return 0;
}
