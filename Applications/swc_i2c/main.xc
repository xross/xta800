/*
 * @ModuleName test
 * @Author Ali Dixon
 * @Date 07/06/2008
 * @Version 1.0
 * @Description: test for IIC bus interface
 *
 * Copyright XMOS Ltd 2008
*/

#include <print.h>
#include <xs1.h>
#include "iic.h"
#include "sw_comps_common.h"

port PORT_IIC_SCL = XS1_PORT_1E;
port PORT_IIC_SDA = XS1_PORT_1F;

#define testPageSize 64
#define testDeviceSize 0x7FFF

XMOS_RTN_t iicSelfTestLocal()
{
  uint i;
  char wrData[testPageSize];
  char rdData[testPageSize];
  XMOS_RTN_t returnCode = XMOS_SUCCESS;
  
  // write
  for (i=0; i<testPageSize; i++)
  {
    wrData[i] = 0;
    rdData[i] = 0xEE;
  }
    
  for (i=0; i<testDeviceSize; i+=testPageSize)
  {
    iic_write(i, wrData, testPageSize);        
  }

  // read
  for (i=0; i<testDeviceSize; i+=testPageSize)
  {
    iic_read(i, rdData, testPageSize);  
    for (uint j=0; j<testPageSize; j++)
    {
      if (rdData[j] != 0)
      {
        returnCode = XMOS_FAIL;
        break;
      }
    }      
  }  
  
  return returnCode;
}

void iictestLocal()
{
  uint ack;
  uint tmp;
  char wrData[testPageSize];
  char rdData[testPageSize];
  XMOS_RTN_t returnCode;
  
  if (iic_initialise() != XMOS_SUCCESS)
     printstr("Error: Failed to initialse\n");
  
  returnCode = iicSelfTestLocal();  
  if (returnCode == XMOS_SUCCESS)
  {
    printstr("Passed Test\n");
  }  
  else
  {
     printstr("Failed Test\n");
  }
}


int main()
{  
  iictestLocal();
  return 0;
}



