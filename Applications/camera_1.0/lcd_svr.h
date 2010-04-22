/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/lcd/lcd_svr.h
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#ifndef LCD_SVR_H_
#define LCD_SVR_H_

#include <xclib.h>
#include <print.h>
#include <platform.h>

#include "line_buf.h"

#define LCD_LINE_COUNT 240
#define LCD_COLM_COUNT 320

#define LCD_888_RED 0xFF
#define LCD_888_GRN 0xFF00
#define LCD_888_BLU 0xFF0000

#define LCD_666_RED 0x0003F
#define LCD_666_GRN 0x00FC0
#define LCD_666_BLU 0x3F000

/* LCD interface thread */
void lcd_physical( streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, port out DTMG, port out DCLK, buffered out port:32 RGB, clock CLK);

#endif /*LCD_SVR_H_*/
