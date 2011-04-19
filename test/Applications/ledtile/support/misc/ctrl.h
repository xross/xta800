/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

unsigned int psread(unsigned int reg);
unsigned int ssread(unsigned int reg);

void pswrite(unsigned int reg, unsigned int val);
void sswrite(unsigned int reg, unsigned int val);

void psset(unsigned psreg, unsigned val);
