/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include <xclib.h>

#include "led.h"
#include "ledprocess.h"
#include "leddriver.h"
#include "mbi5030.h"
#include "math.h"
#include "resetres.h"


#if defined MBI5030 || defined MBI5031

// Write to the LED Drivers' configuration registers
void writeConfiguration_mbi5030(
    buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
    buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
    buffered out port:32 p_spi_ltch, buffered out port:32 p_spi_clk,  
                        unsigned value, unsigned currentgain, unsigned mask)
{
  // Create an invalid value (false parity) for the other drivers
  unsigned dummyvalue=PARITY;
  int driver = BUFFER_SIZE/LEDS_PER_DRIVER;

  // Include current gain
  value = value | currentgain;
  
  // Correct parity for real value
  if (countOnes(value) & 1)
    value |= PARITY;

  // Bit reverse if necessary
#ifdef BITREVERSE
  dummyvalue = bitrev(dummyvalue) >> 16;
  value = bitrev(value) >> 16;
#endif
  
  while (driver)
  {
    driver--;
    // First 3 channels
    if (mask & 0b000001)
      p_led_out_r0 :16 <: value;
    else
      p_led_out_r0 :16 <: dummyvalue;
    if (mask & 0b000010)
      p_led_out_g0 :16 <: value;
    else
      p_led_out_g0 :16 <: dummyvalue;
    if (mask & 0b000100)
      p_led_out_b0 :16 <: value;
    else
      p_led_out_b0 :16 <: dummyvalue;
    // Second three channels
    if (mask & 0b001000)
      p_led_out_r1 :16 <: value;
    else
      p_led_out_r1 :16 <: dummyvalue;
    if (mask & 0b010000)
      p_led_out_g1 :16 <: value;
    else
      p_led_out_g1 :16 <: dummyvalue;
    if (mask & 0b100000)
      p_led_out_b1 :16 <: value;
    else
      p_led_out_b1 :16 <: dummyvalue;
    
    if (driver == 0)
      p_spi_ltch :16 <: REGISTER_WRITE_LATCH;
    else
      p_spi_ltch :16 <: 0;
    
    p_spi_clk <: 0x55555555;
  }
  
  // Bring latch low
  p_spi_ltch:1 <: 0;
  p_spi_clk :2 <: 0x01;
}

// leddrivepins
// Receive reformatted data and output to pins, with correct latch signals
#pragma unsafe arrays
int leddrive_mbi5030(chanend c, 
                   buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
                   buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
                   out port p_spi_addr, buffered out port:32 p_spi_clk ,
                   buffered out port:32 p_spi_ltch, out port p_spi_oe ,
                   clock b_clk, clock b_data, clock b_gsclk, clock b_ref
               )
{
  timer t;
  int now;
  unsigned short buffers[SCAN_RATE][BUFFER_SIZE][MODULE_WIDTH*3/SCAN_RATE]; 


  p_spi_clk :1 <: 0;
  
  resetresources(p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
      p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
      b_clk, b_data, b_gsclk, b_ref);
  
  // Initialise ports
  set_clock_ref(b_clk);
  set_clock_ref(b_gsclk);
  set_clock_ref(b_data);
  
  stop_clock(b_clk);
  stop_clock(b_data);
  stop_clock(b_gsclk);
  
  set_clock_div(b_gsclk, GSCLK_CLKDIV);
  set_clock_div(b_clk, SPI_CLKDIV);
  
  set_port_clock(p_spi_clk, b_clk);
  configure_port_clock_output(p_spi_oe, b_gsclk);

  set_clock_src(b_data, p_spi_clk);
  // Clock ports
  set_port_clock(p_led_out_r0, b_data);
  set_port_clock(p_led_out_g0, b_data);
  set_port_clock(p_led_out_b0, b_data);
  set_port_clock(p_led_out_r1, b_data);
  set_port_clock(p_led_out_g1, b_data);
  set_port_clock(p_led_out_b1, b_data);
  // Latch is clocked off clock
  set_port_clock(p_spi_ltch, b_data);

  start_clock(b_data);
  start_clock(b_clk);
  start_clock(b_gsclk);

  set_thread_fast_mode_on();
  
  p_spi_addr <: 0;
  //p_spi_clk :2 <: 1;
  

  
  t :> now;

  while (1)
  { 
    // Setup the default configuration register
    writeConfiguration_mbi5030(p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
    p_spi_ltch, p_spi_clk, SELF_SYNC | GCLK_TIMEOUT, CURRENT_GAIN, 0xFFFFFFFF);
  
    // Check if any commands come down the pipeline
    // Receive data from reprocessing thread
    while (inuint(c) == 0)
    {
      // Commands
      switch (inuint(c))
      {
        case (XMOS_INTENSITYADJ):
        {
          // Adjust the "current gain" value of the controllers
          unsigned mask, value;
          mask = inuint(c);
          value = inuint(c);
          writeConfiguration_mbi5030(p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
              p_spi_ltch, p_spi_clk, SELF_SYNC | GCLK_TIMEOUT, value, mask);
          break;
        }
        case (XMOS_CHANGEDRIVER):
        {
          return (inuint(c));
        }
        default:
          break;
      }
    }

    // Read into the buffers
    for (int scanx=0; scanx < SCAN_RATE; scanx++)
    {
      for (int j=0; j < (3 * MODULE_WIDTH)/(SCAN_RATE); j++)
      {
        for (int i=0; i < BUFFER_SIZE; i++)
        {
          buffers[scanx][i][j] = bitrev( inuint(c) ) >> 16;
        }
      }
    }
    
    for (int scanx=0; scanx < SCAN_RATE; scanx++)
    {
      for (int channel=0; channel < LEDS_PER_DRIVER; channel++)
      {
        for (int drivernum=0; drivernum < NUM_DRIVERS; drivernum++)
        {
          unsigned ptr = (LEDS_PER_DRIVER - (channel + 1)) + ((NUM_DRIVERS - (1 + drivernum)) * LEDS_PER_DRIVER);
          // Pass new data to the output ports
          p_led_out_r0 :16 <: (unsigned)buffers[scanx][ptr][0];
          p_led_out_g0 :16 <: (unsigned)buffers[scanx][ptr][1];
          p_led_out_b0 :16 <: (unsigned)buffers[scanx][ptr][2];
          p_led_out_r1 :16 <: (unsigned)buffers[scanx][ptr][3];
          p_led_out_g1 :16 <: (unsigned)buffers[scanx][ptr][4];
          p_led_out_b1 :16 <: (unsigned)buffers[scanx][ptr][5];
          
          if (drivernum == (BUFFER_SIZE/LEDS_PER_DRIVER) - 1)
          {
            if (channel == LEDS_PER_DRIVER - 1)
              p_spi_ltch :16 <: GLOBL_LATCH;
            else
              p_spi_ltch :16 <: LOCAL_LATCH;
          }
          else
          {
            p_spi_ltch :16 <: 0;
          }        
          
          // Clock out this data
          p_spi_clk <: 0x55555555;
        }
      }
      // Wait for the latch point
      t when timerafter(now + FRAME_TIME) :> now;   
      // Bring down the latch
      p_spi_ltch :1 <: 0;
      p_spi_clk :2 <: 0x1;
      p_spi_addr <: (unsigned)scanx;   
    }
  }  
  return 0;
}

#endif
