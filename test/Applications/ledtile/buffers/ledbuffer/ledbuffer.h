/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef __LEDBUFFER_H__
#define __LEDBUFFER_H__

// ledbuffer
// Frame buffer for pixel data
// Uses "double-buffer" scheme with tearing prevention

/* On cIn:
 *   -> pixelptr ( >= 0)
 *   -> length (bytes == 3*pixels)
 *   -> pixel0
 *   -> pixel1
 *   -> .
 *   -> .
 *   -> pixel(len/3 - 1)
 *    .
 *   -> -1 (end of frame)
 */

/* On cOut:
 *   -> columnnumber ( >= 0)
 *   <- pixel0
 *   <- pixel1
 *   <- .
 *   <- .
 *   <- pixel(FRAME_HEIGHT - 1)
 *   .
 *   .
 *   -> -1 (end of frame)
 */

#ifdef __XC__
void ledbuffer(chanend cIn, chanend cOut);
#else
void ledbuffer(unsigned cIn, unsigned cOut);
#endif

#endif

