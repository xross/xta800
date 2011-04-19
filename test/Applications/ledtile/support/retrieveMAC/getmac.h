/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

// Retrieves MAC address from OTP
// - macAddrNum - Which MAC address to retreive (0 - 7)
// - macAddr - destination array for MAC address
// - returns 0 for success
int getMacAddr(unsigned macAddrNum, unsigned macAddr[2]);

