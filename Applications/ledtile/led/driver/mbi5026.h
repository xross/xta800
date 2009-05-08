/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#ifndef MBI5026_H_
#define MBI5026_H_

#include "led.h"

#if defined MBI5026

#ifdef __XC__
int leddrive_mbi5026(chanend cLedRaw,
    buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
    buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
    out port p_spi_addr, buffered out port:32 p_spi_clk ,
    buffered out port:32 p_spi_ltch, out port p_spi_oe ,
    clock b_clk, clock b_data, clock b_gsclk, clock b_ref);
#endif


#define SPI_CLKDIV        1

#define REFRESH_RATE      500
#define FRAME_TIME       (100000000 / (NUM_ROWS * REFRESH_RATE))

#define GET_DATA(a) a
#define BITREVERSE

#define LOCAL_LATCH    0b1000
#define GLOBL_LATCH    0b1100

#define SELF_SYNC      (1 << 0xA)
#define GCLK_TIMEOUT   (1 << 0x0)
#define PWM_GS_12BIT   (1 << 0xD)
#define PARITY         (1 << 0xF)
#define CURRENT_GAIN   (0b00000010 << 2)

#define PWMRES           4
#define PWMRES1          (PWMRES)
#define PWMRES2          (PWMRES)
#define PORT_WIDTH       6


#define NUM_PWM_CYCLES  (1 << PWMRES)
#define LEDS_PER_CHAIN   (MODULE_HEIGHT * NUM_MODULES_X * NUM_MODULES_Y)
#define INPUT_BUF_SIZE  (LEDS_PER_CHAIN * SCAN_RATE * PORT_WIDTH)
#define OUTPUT_BUF_SIZE ((LEDS_PER_CHAIN * SCAN_RATE * PORT_WIDTH) / 32)
#define OUTS_PER_CYCLE  6
#endif

#endif /*MBI5026_H_*/
