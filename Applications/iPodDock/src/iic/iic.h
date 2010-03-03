#ifndef IIC_H_
#define IIC_H_

#include <xccompat.h>
#include <platform.h>
#define iic_speed_khz               10

int iic_initialise();

// Write to IIC device
int iic_write(unsigned int address, unsigned int reg, unsigned char data[], unsigned int numBytes);

// Read from IIC device
// It receives numBytes, acking each one and does not ack the final byte
int iic_read(unsigned int address, unsigned int reg, unsigned char data[], unsigned int numBytes);


#endif /*IIC_H_*/
