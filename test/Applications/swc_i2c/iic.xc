/*
 * @ModuleName iic
 * @Author Ali Dixon
 * @Date 07/06/2008
 * @Version 1.0
 * @Description: IIC bus interface
 *
 * Copyright XMOS Ltd 2008
*/ 

#include <xs1.h>
#include "iic.h"

timer IIC_timer;

//
// Wait for the specified amount of time
//
void iic_phy_wait(unsigned delay)
{
	uint time;
    #pragma xta endpoint "iic_phy_wait_start"
	IIC_timer :> time;
    #pragma xta endpoint "iic_phy_wait_stop"
	IIC_timer when timerafter (time + delay) :> uint tmp;
}

//
// Release the bus
//
XMOS_RTN_t iic_initialise()
{
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
  
	uint scl_value;
	uint sda_value;

	scl_value = 0;
	sda_value = 0;

	while ((scl_value != 1) && (sda_value != 1))
	{
		PORT_IIC_SCL :> scl_value;
		PORT_IIC_SDA :> sda_value;
	}

    #pragma xta call "iic_initialise_wait1"
	iic_phy_wait(10 * iic_bittime);

	scl_value = 0;
	sda_value = 0;
	while ((scl_value != 1) && (sda_value != 1))
	{
		PORT_IIC_SCL :> scl_value;
		PORT_IIC_SDA :> sda_value;
	}
	
	return returnCode;
}

//
// Generate start transfer
//
void iic_phy_master_start()
{
	PORT_IIC_SCL <: 1;
	PORT_IIC_SDA <: 1;
    #pragma xta call "iic_phy_master_start_wait1"
	iic_phy_wait(iic_start_cond_setup_time);

	PORT_IIC_SDA <: 0;
    #pragma xta call "iic_phy_master_start_wait2"
	iic_phy_wait(iic_start_cond_hold_time);

	PORT_IIC_SCL <: 0;
}

//
// Generate stop transfer
//
void iic_phy_master_stop()
{
	PORT_IIC_SDA <: 0;
    #pragma xta call "iic_phy_master_stop_wait1"
	iic_phy_wait(iic_scl_low_time);

	PORT_IIC_SCL <: 1;
    #pragma xta call "iic_phy_master_stop_wait2"
	iic_phy_wait(iic_scl_high_time);

	PORT_IIC_SDA <: 1;
    #pragma xta call "iic_phy_master_stop_wait3"
	iic_phy_wait(iic_bus_free_time);
}

//
// Send bit
//
uint iic_phy_sendBit(uint bit)
{
  uint time;
  
  #pragma xta endpoint "iic_phy_sendBit_start"
  IIC_timer :> time;
  PORT_IIC_SDA <: bit;      
            
  time += iic_scl_low_time;   
  #pragma xta endpoint "iic_phy_sendBit_high"
  IIC_timer when timerafter(time) :> time;
  PORT_IIC_SCL <: 1;        // set clock high 
  
  time += iic_scl_high_time;         
  #pragma xta endpoint "iic_phy_sendBit_low"
  IIC_timer when timerafter(time) :> time; 
  PORT_IIC_SCL <: 0;        // set clock low
    
  return 0;
}

//
// Receive bit
//
uint iic_phy_receiveBit()
{
  uint bit;
  uint time;
  
  #pragma xta endpoint "iic_phy_receiveBit_start"
  IIC_timer :> time;
  PORT_IIC_SDA :> int tmp;
      
  time += iic_scl_low_time;   
  #pragma xta endpoint "iic_phy_receiveBit_high"
  IIC_timer when timerafter(time) :> time;
  PORT_IIC_SCL <: 1;        // set clock high 
  
  PORT_IIC_SDA :> bit;    
  
  time += iic_scl_high_time;       
  #pragma xta endpoint "iic_phy_receiveBit_low"
  IIC_timer when timerafter(time) :> time; 
  PORT_IIC_SCL <: 0;        // set clock low
    
  return bit;
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

  #pragma xta endpoint "iic_phy_sendByte_start"
  IIC_timer :> time;
    
  while (bitCount < 8) 
  {
    bit = (byte & 0x80) >> 7;        
   
    PORT_IIC_SDA <: bit;      
       
    time += iic_scl_low_time;   
    #pragma xta endpoint "iic_phy_sendByte_high"
    IIC_timer when timerafter(time) :> time;
    PORT_IIC_SCL <: 1;        // set clock high 
  
    byte = byte << 1;
    bitCount = bitCount + 1;

    time += iic_scl_high_time;   
    #pragma xta endpoint "iic_phy_sendByte_low"
    IIC_timer when timerafter(time) :> time; 
    PORT_IIC_SCL <: 0;        // set clock low
  }
  
  PORT_IIC_SDA :> int tmp;

  ack = iic_phy_receiveBit();
  if (ack != expectAck)
  {
    returnCode = XMOS_FAIL;
  }
  #pragma xta label "iic_phy_sendByte_done"
  return returnCode;
}

//
// Receive byte
//
uint iic_phy_receiveByte(uint ack)
{
  uint bitCount;
  uint bit;
  uint time;
  uint byte;

  byte = 0;
  bitCount = 0;      

  #pragma xta endpoint "iic_phy_receiveByte_start"
  IIC_timer :> time;
    
  // set to input  
  PORT_IIC_SDA :> int tmp;
          
  while (bitCount < 8) 
  {
    time += iic_scl_low_time;   
    #pragma xta endpoint "iic_phy_receiveByte_high"
    IIC_timer when timerafter(time) :> time;
    PORT_IIC_SCL <: 1;        // set clock high 
  
    PORT_IIC_SDA :> bit;    
      
    byte = (byte << 1) | bit;
    bitCount = bitCount + 1;
  
    time += iic_scl_high_time;     
    #pragma xta endpoint "iic_phy_receiveByte_low"
    IIC_timer when timerafter(time) :> time; 
    PORT_IIC_SCL <: 0;        // set clock low
  }
    
  PORT_IIC_SDA :> int tmp;

  iic_phy_sendBit(ack);
  
  return byte;
}

//
// Poll the card to determine when write completes
//
uint iic_checkWriteComplete()
{ 
  uint time;
    
  while (1)
  {    
    #pragma xta call "master_start_call"
    iic_phy_master_start();  
    if (iic_phy_sendByte(IIC_ADDR | IIC_WE, 0) == XMOS_SUCCESS)
      break;        
      
    #pragma xta endpoint "iic_checkWriteComplete_start"
    IIC_timer :> time;
    #pragma xta endpoint "iic_checkWriteComplete_stop"
    IIC_timer when timerafter(time + iic_write_ack_poll_time) :> time; 
  }   
  iic_phy_master_stop();
    
  return 0;
}


//
// Write to iic device
// Each write must not cross block boundaries (64 byte aligned)
//
XMOS_RTN_t iic_blockWrite(uint address, char data[], uint startIndex, uint numBytes)
{ 
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
    
  iic_phy_master_start();
  
  returnCode = iic_phy_sendByte(IIC_ADDR | IIC_WE, 0);  
  
  if (returnCode == XMOS_SUCCESS)
    returnCode = iic_phy_sendByte(address >> 8, 0);
    
  if (returnCode == XMOS_SUCCESS)  
    returnCode = iic_phy_sendByte(address, 0);
  
  for (uint i=0; i<numBytes; i++)
  {
    if (returnCode == XMOS_SUCCESS) 
      returnCode = iic_phy_sendByte(data[startIndex+i], 0);  
    else
      break;  
  }
    
  if (returnCode == XMOS_SUCCESS) 
  {
    iic_phy_master_stop();
    
    iic_checkWriteComplete();
  } 
  return returnCode;
}

//
// Write to IIC device
// Converts writes into block writes 
//
XMOS_RTN_t iic_write(uint address, char data[], uint numBytes)
{
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
  uint currentIndex;
  
  currentIndex = 0;
  
  // write first sub block
  if ((address & 0x3F) != 0)
  {
    #pragma xta label "iic_write_block1"
    iic_blockWrite(address, data, currentIndex, numBytes & 0x3F);
    
    // align address
    address += numBytes & 0x3F;;
    currentIndex += currentIndex & 0x3F;
  }    
  
  // write blocks  
  while ((numBytes-currentIndex) >= 64)
  {  
    iic_blockWrite(address, data, currentIndex, 64);
    address += 64;
    currentIndex += 64;
  }
  
  // write last block
  if ((numBytes-currentIndex) != 0)
  {
    iic_blockWrite(address, data, currentIndex, numBytes);
  }
  
  return returnCode;
}

//
// Read from IIC device
// Send ctrl byte and address, then receive bytes, acking each one
// Dont ack the final byte to signal the end of transfer
//
XMOS_RTN_t iic_read(uint address, char data[], uint numBytes)
{ 
  uint i; 
  uint time;
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
    
  iic_phy_master_start();
  
  iic_phy_sendByte(IIC_ADDR | IIC_WE, 0);  
  iic_phy_sendByte(address >> 8, 0);  
  iic_phy_sendByte(address, 0);
  
  iic_phy_master_start();
      
  #pragma xta call "iic_read_sendByte"
  iic_phy_sendByte(IIC_ADDR | IIC_RE, 0);
  
  for (i=0; i<numBytes-1; i++)
  {
    #pragma xta call "iic_read_receiveByte"
    data[i] = iic_phy_receiveByte(0);
  }
  
  // receive final byte and dont ack to signal end of transfer
  data[i] = iic_phy_receiveByte(1);
  
  iic_phy_master_stop();
  
  #pragma xta label "iic_read_done"
  return returnCode;
}

