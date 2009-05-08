/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#ifndef MBI5026_SUPPORT_H_
#define MBI5026_SUPPORT_H_

#include "leddriver.h"
#include "mbi5026.h"
#include "chanAbs.h"

#if defined MBI5026

int ledreformat_mbi5026(chanend cOut, unsigned drivebuf1[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE], unsigned drivebuf2[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE]);
void ledreformat_process_mbi5026(unsigned char buffers[INPUT_BUF_SIZE],
    unsigned drivebuf[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE]);

#ifdef __XC__
void leddrivepins_mbi5026(unsigned drivebuf1[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE], unsigned drivebuf2[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE],
    buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
     buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
     out port p_spi_addr, buffered out port:32 p_spi_clk ,
     buffered out port:32 p_spi_ltch, out port p_spi_oe);
#else
void leddrivepins_mbi5026(unsigned drivebuf1[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE], unsigned drivebuf2[NUM_PWM_CYCLES][OUTPUT_BUF_SIZE],
    unsigned p_led_out_r0, unsigned p_led_out_g0, unsigned p_led_out_b0,
     unsigned p_led_out_r1, unsigned p_led_out_g1, unsigned p_led_out_b1,
     unsigned p_spi_addr, unsigned p_spi_clk ,
     unsigned p_spi_ltch, unsigned p_spi_oe);
#endif

#endif

#endif /*MBI5026_SUPPORT_H_*/
