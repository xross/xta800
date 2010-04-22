/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/lcd/lcd_svr.xc
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#include "lcd_svr.h"


void delay( unsigned instr_count )
{
	timer tmr;
	unsigned ts;
	
	tmr :> ts;
	ts += instr_count;
	tmr when timerafter(ts) :> ts;
}

unsigned LCD_RGB565_to_RGB666( unsigned pxl )
{
	unsigned r;
	
	r  = ((pxl & 0x001F) << 1);
	r |= ((pxl & 0x07E0) << 1);
	r |= ((pxl & 0xF800) << 2);
	
	return r;
}

void lcd_init(port out DTMG, port out DCLK, buffered out port:32 RGB, clock CLK)
{	
	configure_clock_rate(CLK, 100, 60); // 100 / 12 for QVGA
	configure_out_port(RGB, CLK, 0); // RGB port controlled by the clock
	configure_out_port(DTMG, CLK, 0); // DTMG port controlled by the clock
	configure_port_clock_output( DCLK, CLK ); // output CLK on DCLK	
	set_port_inv(DCLK); // make it so data is set on +ve edge (not seen on VCD 
	start_clock(CLK); 
}

void lcd_physical( streaming chanend c_lcd_con, streaming chanend c_lcd_cntrl, port out DTMG, port out DCLK, buffered out port:32 RGB, clock CLK)
{
	unsigned pxl, time;
		
	lcd_init( DTMG, DCLK, RGB, CLK);
	
	while (1)
	{ 				
		DTMG <: 0; // set DTMG to 0 and get TS		
		for (int lines = 0; lines < LCD_LINE_COUNT; lines++)
		{	
			c_lcd_cntrl :> int; // line trigger
			c_lcd_con <: 0; // lb req
			c_lcd_con :> pxl;		
			DTMG <: 1;
			RGB <: pxl; // pixel 0
			for (int col = 1; col < LCD_COLM_COUNT; col+=1)
			{				
				c_lcd_con <: 0; // lb req
				c_lcd_con :> pxl;
				RGB <: LCD_RGB565_to_RGB666( pxl );
			}			
			DTMG <: 0;			
		}
	}
	
}


