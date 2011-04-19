/*
 * @ModuleName iic
 * @Author Ali Dixon
 * @Date 07/06/2008
 * @Version 1.0
 * @Description: IIC bus interface
 *
 * Copyright XMOS Ltd 2008
*/

#ifndef __IIC_H__
#define __IIC_H__

//#define IIC_DEBUG 

extern port PORT_IIC_SCL;
extern port PORT_IIC_SDA;

#include "sw_comps_common.h"


//
// Initialise the IIC device
//
XMOS_RTN_t iic_initialise();

//
// Read from IIC device
//
XMOS_RTN_t iic_read(uint address, char data[], uint numBytes);

//
// Write to IIC device
//
XMOS_RTN_t iic_write(uint address, char data[], uint numBytes);


#define IIC_ADDR 0xA0
#define IIC_WE 0
#define IIC_RE 1

// I2C PHY
#define iic_scl XS1_PORT_1A
#define iic_sda XS1_PORT_1B

#define iic_bittime 						    250 // 400Khz

// times are in units of 10ns
#define iic_scl_high_time 			    60
#define iic_scl_low_time 			      130
#define iic_bus_free_time 			    60
#define iic_start_cond_setup_time 	60
#define iic_start_cond_hold_time   	60
#define iic_write_cycle_time        500000  // 5ms
#define iic_write_ack_poll_time     5000 

#endif

