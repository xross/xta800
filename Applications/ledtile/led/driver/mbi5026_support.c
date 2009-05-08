/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#include "print.h"
#include "leddriver.h"
#include "ledprocess.h"
#include "chanAbs.h"
#include "mbi5026.h"
 
#if defined MBI5026

#define driveout(ptr) { \
        outp(p_led_out_r0, ptr[0]); \
        outp(p_led_out_g0, ptr[1]); \
        outp(p_led_out_b0, ptr[2]); \
        outp(p_led_out_r1, ptr[3]); \
        outp(p_led_out_g1, ptr[4]); \
        outp(p_led_out_b1, ptr[5]); \
        outp(p_spi_clk, 0xAAAAAAAA); \
        outp(p_spi_clk, 0xAAAAAAAA); \
        sync(p_spi_clk); \
        outp(p_spi_oe, 1); \
        outpw(p_spi_ltch, 0x00, 8); \
        sync(p_spi_ltch); \
        outp(p_spi_addr, scan); \
        outpw(p_spi_ltch, 0x000F, 32); \
        sync(p_spi_ltch); \
        outp(p_spi_oe, 0); \
        ptr += OUTS_PER_CYCLE; \
}

//#define DRIVE_ALGORITHM_ADVANCED

void leddrivepins_mbi5026(unsigned drivebuf1[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE], unsigned drivebuf2[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE],
    unsigned p_led_out_r0, unsigned p_led_out_g0, unsigned p_led_out_b0,
     unsigned p_led_out_r1, unsigned p_led_out_g1, unsigned p_led_out_b1,
     unsigned p_spi_addr, unsigned p_spi_clk ,
     unsigned p_spi_ltch, unsigned p_spi_oe)
{
  unsigned interpwm, intrapwm;
  unsigned *ptr, *ptr2;
  int scan;

#ifdef DRIVE_ALGORITHM_ADVANCED
  /* This algorithm scans across the rows very quickly.
     Unfortunately it requires a pull down resistor to turn off the rows
     quickly enough. SMD32X161R1G1BFP6 supplied with XC-3 do not support this
  */
  for (interpwm = 0; interpwm < (1 << PWMRES); interpwm++)
  { 
    // Drive msbs
      for (intrapwm = 0; intrapwm < (1 << PWMRES); intrapwm++)
      {
        ptr = (unsigned *)drivebuf1[intrapwm];     
        for (scan = 0; scan < SCAN_RATE; scan++)
        {
          driveout(ptr);
        }
      }
    // Drive one cycle of lsb
    ptr2 = (unsigned *)drivebuf2[interpwm];
    for (scan = 0; scan < SCAN_RATE; scan++)
    {
      driveout(ptr2);
    }
  }
#else
  for (scan = 0; scan < SCAN_RATE; scan++)
  {
    for (interpwm = 0; interpwm < (1 << PWMRES); interpwm++)
    { 
      // Drive msbs
      for (intrapwm = 0; intrapwm < (1 << PWMRES); intrapwm++)
      {
        ptr = (unsigned *)drivebuf1[intrapwm] + scan * OUTS_PER_CYCLE;     
        driveout(ptr);
      }
      ptr2 = (unsigned *)drivebuf2[interpwm] + scan * OUTS_PER_CYCLE;     
          
      // Drive one cycle of lsb
      driveout(ptr2);
    }
  }
#endif
}

inline static void updateBufs(unsigned vptr, unsigned drivebuf[OUTPUT_BUF_SIZE], unsigned pwmcycle)
{
  unsigned intrachain, interchain;
  intrachain = 31 - (vptr & (LEDS_PER_CHAIN - 1)); // Assumes LEDS_PER_CHAIN is power of 2, and <= 32
  interchain = (vptr / LEDS_PER_CHAIN);
  drivebuf[interchain] &= ~(1<<intrachain); 
}

void ledreformat_process_mbi5026(unsigned char buffers[INPUT_BUF_SIZE],
    unsigned drivebuf[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE])
{
  unsigned vptr;
  unsigned i, intrapwm;

  // Cap all input buffers
  for (vptr=0; vptr<INPUT_BUF_SIZE; vptr++)
  {
    if (buffers[vptr] >= NUM_PWM_CYCLES)
    {
      buffers[vptr] = NUM_PWM_CYCLES - 1;
    }
  }
    
  // Reset output buffers
  for (i=0; i<OUTPUT_BUF_SIZE; i++)
    drivebuf[0][i] = -1;
  
  // Cycle 0
  for (vptr=0; vptr<INPUT_BUF_SIZE; vptr++)
  {
    if (buffers[vptr] == 0)
    {
      updateBufs(vptr, drivebuf[0], 0);
    }
  }

#define dither(a) (((a) >> 2) + (((a) & 0x3) << (PWMRES - 2)))

  // Loop through the pwm cycles
  for (intrapwm = 1; intrapwm < NUM_PWM_CYCLES; intrapwm++)
  {
    // Copy from previous iteration
    for (i=0; i<OUTPUT_BUF_SIZE; i++)
      drivebuf[dither(intrapwm)][i] = drivebuf[dither(intrapwm-1)][i];
    
    // Check all buffers
    for (vptr=0; vptr<INPUT_BUF_SIZE; vptr++)
    {
      if (buffers[vptr] == intrapwm)
      {
        updateBufs(vptr, drivebuf[dither(intrapwm)], intrapwm);     
      }
    }
  }
}
#endif


