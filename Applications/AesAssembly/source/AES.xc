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
*/
#include "AESincludes.h"

#define TESTCASES 2

unsigned int plainTextVector[TESTCASES][4] = {\
{0xa8f64332, 0x8d305a88, 0xa2983131, 0x340737e0},\
{0x33221100, 0x77665544, 0xbbaa9988, 0xffeeddcc},\
};

unsigned int keyVector[TESTCASES][Nk] = {\
{0x16157e2b, 0xa6d2ae28, 0x8815f7ab, 0x3c4fcf09},\
{0x03020100, 0x07060504, 0x0b0a0908, 0x0f0e0d0c},\
};

unsigned int cipherTextVector[TESTCASES][4]	= {\
{0x1d842539, 0xfb09dc02, 0x978511dc, 0x320b6a19},\
{0xd8e0c469, 0x30047b6a, 0x80b7cdd8, 0x5ac5b470},\
};

unsigned int decryptedTextVector[TESTCASES][4] = {{0}};


#pragma unsafe arrays
int main(){
	int error = 0;
	
	//this can change depending on the keylength used
	unsigned int key[Nk];

	//the total number of round key bits is equal to the block length multiplied by the number of rounds plus 1
	unsigned int w[Nb * (Nr + 1)];
	unsigned int wDecrypt[Nb * (Nr + 1)];

	//these are always fixed size for AES with block size = 128 bit
	unsigned int plainText[4];

	unsigned int cipherText[4];
	unsigned int decryptedText[4];
	timer t;
	int start, end;
	int i, j;
	for(i = 0; i < TESTCASES; i+=1){
		for(j = 0; j < 4; j+=1){
			plainText[j] = plainTextVector[i][j];
		}
		for(j = 0; j < Nk; j+=1){
			key[j] = keyVector[i][j];
		}
		
		//this will populate the w vector	
		KeyExpansion(key, w);
		//this will populate the cipher text vector (the encryption process)
		_Encrypt(plainText, w, cipherText);
		InvKeyExpansion(key, wDecrypt);
		//and this will popluate the decrypted text vector (this will complete the loop and decrypt 
		//the test case back to the original form)
		_Decrypt(cipherText, wDecrypt, decryptedText);
		
		//check validity
		for(j = 0; j < 4; j+=1){
			//did encryption work?
			if(cipherText[j] != cipherTextVector[i][j])
				error = 1;
			//did decryption work?
			if(decryptedText[j] != plainTextVector[i][j])
				error = 1;
		}
	}

	return error;
}



