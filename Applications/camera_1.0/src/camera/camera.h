/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/camera/camera.h
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#ifndef CAMERA_H_
#define CAMERA_H_
#include <xs1.h>
#include <print.h>
#include "buffer/line_buf.h"

/* camera thread */
void doCamera(streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, clock clk, clock clk2, in port Y_UV_port, in port PCLK_port, out port EXCLK_port, in port HREF_port, in port VSYNC_port, out port p_cam_rst);

#endif /*CAMERA_H_*/
