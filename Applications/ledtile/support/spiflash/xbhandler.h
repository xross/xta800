/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef XBHANDLER_H_
#define XBHANDLER_H_

int getTStamp(int addr, int type,
#ifdef OTPSUPPORT
    in port otp_data, out port otp_addr, out port otp_ctrl ,
#endif
    out port p_flash_ss, buffered in port:8 p_flash_miso,
        buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi);

int checkXbCrc(unsigned addr, int type, 
#ifdef OTPSUPPORT
    in port otp_data, out port otp_addr, out port otp_ctrl ,
#endif
    out port p_flash_ss, buffered in port:8 p_flash_miso,
        buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi);

int bootXb(unsigned addr, int type, in port otp_data, out port otp_addr, out port otp_ctrl ,
    out port p_flash_ss, buffered in port:8 p_flash_miso,
        buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi);

#endif /*XBHANDLER_H_*/
