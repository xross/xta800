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
#include "resetres.h"
#include "led.h"
#include "ledprocess.h"
#include "leddriver.h"
#include "print.h"
#include "mbi5026_support.h"


#if defined MBI5026

void ledinit_mbi5026(buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
              buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
              out port p_spi_addr, buffered out port:32 p_spi_clk ,
              buffered out port:32 p_spi_ltch, out port p_spi_oe ,
              clock b_clk, clock b_data, clock b_gsclk, clock b_ref
              )
{
  resetresources(p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
      p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
      b_clk, b_data, b_gsclk, b_ref);
  
  // Initialise ports
  p_spi_oe <: 0;
  p_spi_clk :1 <: 1;

  
  set_clock_off(b_clk);
  set_clock_off(b_gsclk);
  set_clock_off(b_data);
  set_clock_on(b_clk);
  set_clock_on(b_gsclk);
  set_clock_on(b_data);
  stop_clock(b_clk);
  stop_clock(b_data);
  set_clock_ref(b_clk);
  
  set_clock_div(b_clk, SPI_CLKDIV);
  set_port_clock(p_spi_clk, b_clk);

  set_clock_src(b_data, p_spi_oe);
  set_clock_src(b_data, p_spi_clk);
  set_port_clock(p_led_out_r0, b_data);
  set_port_clock(p_led_out_g0, b_data);
  set_port_clock(p_led_out_b0, b_data);
  set_port_clock(p_led_out_r1, b_data);
  set_port_clock(p_led_out_g1, b_data);
  set_port_clock(p_led_out_b1, b_data);
  set_port_clock(p_spi_ltch, b_clk);
  set_port_pull_up(p_led_out_r0);
  //set_pad_delay(p_spi_clk, 5);
  start_clock(b_data);
  start_clock(b_clk);
  //set_thread_fast_mode_on();

  p_spi_addr <: 0;
}

#pragma unsafe arrays
int ledreformat_mbi5026(chanend cIn, unsigned drivebuf1[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE], unsigned drivebuf2[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE])
{
  unsigned char buffers1[INPUT_BUF_SIZE];  
  unsigned char buffers2[INPUT_BUF_SIZE]; 
  int i;
  
  // Receive from processing thread
  while (inuint(cIn) == 0)
  {
     // Commands
     switch (inuint(cIn))
     {
       case (XMOS_INTENSITYADJ):
       {
         // MBI5026 do not support current gain adjustment
         // Ignore command
         inuint(cIn);
         inuint(cIn);
         break;
       }
       case (XMOS_CHANGEDRIVER):
       {
         return (inuint(cIn));
       }
       default:
         break;
     }
  }
  
  for (i=0; i < INPUT_BUF_SIZE; i++)
  {
    unsigned val = inuint(cIn);
    buffers1[i] = (val >> (16 - PWMRES2)) & ((1 << PWMRES1) - 1);
    buffers2[i] = (val >> (16 - (PWMRES1 + PWMRES2))) & ((1 << PWMRES2) - 1);        
  }
  
  par
  {
    ledreformat_process_mbi5026(buffers1, drivebuf1);
    ledreformat_process_mbi5026(buffers2, drivebuf2);
  }
  
  return 0;
}



// leddrive
// Macroblock LED Driver threads
// Requires channel to LED Pixel buffer, and channel to LED Command buffer
// Splits to three threads
//  - Reprocessing thread for colour correction etc
//  - Reformatting thread for splitting onto 8-bit port (not needed if have enough 1-bit ports)
//  - Pins thread for actually driving the IO and clocks
int leddrive_mbi5026(chanend cLedRaw,
    buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
    buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
    out port p_spi_addr, buffered out port:32 p_spi_clk ,
    buffered out port:32 p_spi_ltch, out port p_spi_oe ,
    clock b_clk, clock b_data, clock b_gsclk, clock b_ref)
{
  unsigned drivebuf1a[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE];
  unsigned drivebuf1b[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE];
  unsigned drivebuf2a[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE];
  unsigned drivebuf2b[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE];
  timer t;
  int now;
  int retval;
  
  ledinit_mbi5026(p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
      p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
      b_clk, b_data, b_gsclk, b_ref);
  ledreformat_mbi5026(cLedRaw, drivebuf1a, drivebuf2a); 

  while (1)
  {
    t :> now;
    par
    {
      retval = ledreformat_mbi5026(cLedRaw, drivebuf1b, drivebuf2b); 
      leddrivepins_mbi5026(drivebuf1a, drivebuf2a,
          p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
          p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe);
    }
    // Increment the scan
#if 0
    {
      int n2;
      t :> n2;
      printintln((n2 - now)/100);
    }
#endif
    if (retval)
      return retval;
    par
    {
      retval = ledreformat_mbi5026(cLedRaw, drivebuf1a, drivebuf2a); 
      leddrivepins_mbi5026(drivebuf1b, drivebuf2b,
          p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
          p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe);
    }
    if (retval)
      return retval;
  }
  return 0;
}

#endif
