/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#ifndef MBI5030_H_
#define MBI5030_H_

#include <led.h>

#if defined MBI5030 || defined MBI5031

#ifdef __XC__
int leddrive_mbi5030(chanend c, 
                   buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
                   buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
                   out port p_spi_addr, buffered out port:32 p_spi_clk ,
                   buffered out port:32 p_spi_ltch, out port p_spi_oe ,
                   clock b_clk, clock b_data, clock b_gsclk, clock b_ref);
#endif

#define SPI_CLKDIV        4
#define GSCLK_CLKDIV      8

#define REFRESH_RATE        500
#define FRAME_TIME       (100000000 / (SCAN_RATE * REFRESH_RATE))

#define GET_DATA(a) a
#define BITREVERSE

#define LOCAL_LATCH             (bitrev((1 <<  1) - 1) >> 16)
#define GLOBL_LATCH             (bitrev((1 <<  3) - 1) >> 16)
#define REGISTER_WRITE_LATCH    (bitrev((1 << 11) - 1) >> 16)


#define SELF_SYNC      (1 << 0xA)
#define GCLK_TIMEOUT   (1 << 0x0)
#define PWM_GS_12BIT   (1 << 0xD)
#define PARITY         (1 << 0xF)
#define CURRENT_GAIN   (0b00000010 << 2)


#endif

#endif /*MBI5030_H_*/
