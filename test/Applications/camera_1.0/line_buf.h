/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/buffer/line_buf.h
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#ifndef LB_H_
#define LB_H_
#include <xs1.h>

#define LB_CMD_DATA 0x01
#define LB_CMD_DONE 0x02

void lb_lineDone(chanend c);

unsigned lb_getPix(chanend c);

void line_buffer( streaming chanend c_lcd_pro, streaming chanend c_lcd_con );

#endif /*LB_H_*/
