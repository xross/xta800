#ifndef FLASH_H_
#define FLASH_H_

#include <xclib.h>
#include "global.h"

#ifdef __XC__
int flash_pre();
void flash_post();

int flash_read(streaming chanend cAudio, unsigned int offset, unsigned int size, unsigned char buffer[] , streaming chanend cI2CReset);
int flash_write(streaming chanend cAudio, unsigned int offset, unsigned int size, unsigned char buffer[], streaming chanend cI2CReset );
#elif defined __STDC__
int flash_pre();
void flash_post();

int flash_read(unsigned cAudio, unsigned int offset, unsigned int size, unsigned char buffer[], unsigned cI2CReset);
int flash_write(unsigned cAudio, unsigned int offset, unsigned int size, unsigned char buffer[], unsigned cI2CReset );
#endif

#endif /*FLASH_H_*/
