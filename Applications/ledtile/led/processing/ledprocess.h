/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef LEDPROCESS_H_
#define LEDPROCESS_H_

// ledreprocess
// Load pixel data from buffer and apply gamma LUT and intensity colour correction
#ifdef __XC__
void ledreprocess(chanend cLedData, chanend cLedCmd, chanend cOut, chanend cWdog);
#else
void ledreprocess(unsigned cLedData, unsigned cLedCmd, unsigned cOut, unsigned cWdog);
#endif

#include "led.h"

#define FRAME_SIZE       (FRAME_HEIGHT * FRAME_WIDTH)
#define BUFFER_SIZE      (FRAME_HEIGHT * NUM_MODULES_X)
#define NUM_DRIVERS      (BUFFER_SIZE/LEDS_PER_DRIVER)


#define START_IP_X               0x01
#define START_IP_Y               0x01

#define XMOS_VERSION             0x01
#define XMOS_DATA                0x02
#define XMOS_LATCH               0x03
#define XMOS_GAMMAADJ            0x04
#define XMOS_INTENSITYADJ        0x05
#define XMOS_SINTENSITYADJ       0x06
#define XMOS_RESET               0x07
#define XMOS_AC_1                0x08
#define XMOS_AC_2                0x09
#define XMOS_AC_3                0x0A
#define XMOS_AC_4                0x0B
#define XMOS_SINTENSITYADJ_PIX   0x0C
#define XMOS_CHANGEDRIVER        0x0D

// XMOS Packets come in on this port
#define PORT_XMOS          306


#endif /*LEDPROCESS_H_*/
