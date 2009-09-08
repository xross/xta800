/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/main.xc
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#include <xs1.h>

#include "camera/camera.h"
#include "lcd/lcd_svr.h"
#include "buffer/line_buf.h"


#define LCD_CORE 3
#define CAM_CORE 1

// LCD ports

on stdcore[LCD_CORE] :  out port p_lcd_dtm               = XS1_PORT_1B;
on stdcore[LCD_CORE] :  out port p_lcd_clk               = XS1_PORT_1A;
on stdcore[LCD_CORE] :  buffered out port:32 p_lcd_rgb   = XS1_PORT_32A;
on stdcore[LCD_CORE] :  clock clk_lcd                    = XS1_CLKBLK_1;

on stdcore[CAM_CORE] : out port p_cam_rst = XS1_PORT_1B;

// Camera data ports
on stdcore[CAM_CORE] : in port HREF_port = XS1_PORT_1F;
on stdcore[CAM_CORE] : in port VSYNC_port = XS1_PORT_1H;
on stdcore[CAM_CORE] : in port PCLK_port = XS1_PORT_1G;
on stdcore[CAM_CORE] : out port EXCLK_port = XS1_PORT_1I;

#ifdef COLOUR
on stdcore[CAM_CORE] : in port Y_port = XS1_PORT_8A;
on stdcore[CAM_CORE] : in port UV_port = XS1_PORT_8B;
#else
on stdcore[CAM_CORE] : in port Y_UV_port = XS1_PORT_16A;
#endif

// camera config ports
on stdcore[CAM_CORE] : port p_iic_sda = XS1_PORT_1C;
on stdcore[CAM_CORE] : port p_iic_scl = XS1_PORT_1E;

on stdcore[CAM_CORE] : clock clk = XS1_CLKBLK_3;
on stdcore[CAM_CORE] : clock clk2 = XS1_CLKBLK_2;

int main (void)
{
  streaming chan c_lcd_pro, c_lcd_con, c_lcd_cntrl;

  par
  {
	// Core 0
	
	  
    // Core 1
    on stdcore[CAM_CORE] : doCamera(c_lcd_pro, c_lcd_cntrl, clk, clk2, Y_UV_port, PCLK_port, EXCLK_port, HREF_port, VSYNC_port, p_cam_rst);
    
    // Core 2
    on stdcore[1] : line_buffer( c_lcd_pro,  c_lcd_con);
    
    // Core 3
    on stdcore[LCD_CORE] : lcd_physical( c_lcd_con, c_lcd_cntrl, p_lcd_dtm, p_lcd_clk, p_lcd_rgb, clk_lcd );
   
  }
  
  return 0;
}

