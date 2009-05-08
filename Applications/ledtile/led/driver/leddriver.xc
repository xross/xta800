/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#include <xs1.h>
#include <mbi5026.h>
#include <mbi5030.h>

void leddrive(chanend cLedRaw,
    buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
    buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
    out port p_spi_addr, buffered out port:32 p_spi_clk ,
    buffered out port:32 p_spi_ltch, out port p_spi_oe ,
    clock b_clk, clock b_data, clock b_gsclk, clock b_ref)
{
  int retval;
  
  start_streaming_slave(cLedRaw);

#if defined MBI5030 || defined MBI5031
  retval = 2;
#else
  retval = 1;
#endif
  
  while (1)
  {
    switch (retval)
    {
      case (1):
#if defined MBI5026
        retval = leddrive_mbi5026(cLedRaw, p_led_out_r0, p_led_out_g0, p_led_out_b0,
                p_led_out_r1, p_led_out_g1, p_led_out_b1,
                p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
                b_clk, b_data, b_gsclk, b_ref);
        break;
#endif
        
        
      case (2):
#if defined MBI5030 || defined MBI5031
        retval = leddrive_mbi5030(cLedRaw, p_led_out_r0, p_led_out_g0, p_led_out_b0,
                p_led_out_r1, p_led_out_g1, p_led_out_b1,
                p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
                b_clk, b_data, b_gsclk, b_ref);
#endif
        break;
        
        
      default:
#if defined MBI5030 || defined MBI5031
        retval = leddrive_mbi5030(cLedRaw, p_led_out_r0, p_led_out_g0, p_led_out_b0,
                p_led_out_r1, p_led_out_g1, p_led_out_b1,
                p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
                b_clk, b_data, b_gsclk, b_ref);
#elif defined MBI5026
        retval = leddrive_mbi5026(cLedRaw, p_led_out_r0, p_led_out_g0, p_led_out_b0,
                        p_led_out_r1, p_led_out_g1, p_led_out_b1,
                        p_spi_addr, p_spi_clk , p_spi_ltch, p_spi_oe ,
                        b_clk, b_data, b_gsclk, b_ref);
#else
        return;
#endif
        break;
    }
  }
}

