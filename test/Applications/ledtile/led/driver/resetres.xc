/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */


#include <xs1.h>

void resetresources(buffered out port:32 p_led_out_r0, buffered out port:32 p_led_out_g0, buffered out port:32 p_led_out_b0,
                   buffered out port:32 p_led_out_r1, buffered out port:32 p_led_out_g1, buffered out port:32 p_led_out_b1,
                   out port p_spi_addr, buffered out port:32 p_spi_clk ,
                   buffered out port:32 p_spi_ltch, out port p_spi_oe ,
                   clock b_clk, clock b_data, clock b_gsclk, clock b_ref)
{
  configure_out_port_no_ready(p_led_out_r0, b_ref, 0);
  configure_out_port_no_ready(p_led_out_g0, b_ref, 0);
  configure_out_port_no_ready(p_led_out_b0, b_ref, 0);
  configure_out_port_no_ready(p_led_out_r1, b_ref, 0);
  configure_out_port_no_ready(p_led_out_g1, b_ref, 0);
  configure_out_port_no_ready(p_led_out_b1, b_ref, 0);
  configure_out_port_no_ready(p_spi_addr, b_ref, 0);
  configure_out_port_no_ready(p_spi_clk, b_ref, 0);
  configure_out_port_no_ready(p_spi_ltch, b_ref, 0);
  configure_out_port_no_ready(p_spi_oe, b_ref, 0);

  set_clock_off(b_clk);
  set_clock_off(b_data);
  set_clock_off(b_gsclk);
  
  set_clock_on(b_clk);
  set_clock_on(b_data);
  set_clock_on(b_gsclk);
  
  set_clock_ref(b_clk);
  set_clock_ref(b_data);
  set_clock_ref(b_gsclk);
}

