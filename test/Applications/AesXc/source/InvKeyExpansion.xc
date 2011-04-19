/*
* The copyrights, all other intellectual and industrial
* property rights are retained by XMOS and/or its licensors.
* Terms and conditions covering the use of this code can
* be found in the Xmos End User License Agreement.
*
* Copyright XMOS Ltd 2008
*
* Author: Suleiman Abu Kharmeh
* Date: 19/November/2008
*
* This the most performance optimised XC version.
*	Further hand crafted optimisations are implemented in the assembly version
* See InvKeyExpansion.S for more details.
*
*/
#ifndef _INVKEYEXPANSION
#define _INVKEYEXPANSION

#include "AESincludes.h"
#pragma unsafe arrays
void InvKeyExpansion(unsigned int key[], unsigned int w[]){
        int i;
	
	KeyExpansion(key, w);
	
	#pragma loop unroll(36)
	for(i = 4; i < Nb * Nr; i++){
		//InvMixColumn for each key except for the first and final round
		w[i] = M0[(w[i] << 24) >> 24] ^ M1[(w[i] << 16) >> 24] ^ M2[(w[i] << 8) >> 24] ^ M3[w[i] >> 24];
	}
	return;
}
#endif

