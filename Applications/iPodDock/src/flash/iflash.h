#ifndef __AUDIOFLASH_H__
#define __AUDIOFLASH_H__


#ifdef __XC__
unsigned char get_audio_byte(streaming_chanend cAudioStreaming, streaming chanend cSOFGen);
//unsigned int supplyData( void* userPtr, unsigned int numBytes, unsigned char* dstBuf );
int iflash_support (unsigned len, unsigned version, streaming_chanend cAudioStreaming, streaming chanend cSOFGen);
int iflash_erase(unsigned int imageSize);
#else
unsigned char get_audio_byte(streaming_chanend cAudioStreaming, unsigned cSOFGen);
unsigned int supplyData( void* userPtr, unsigned int numBytes, unsigned char* dstBuf );
int iflash_support (unsigned len, unsigned version, streaming_chanend cAudioStreaming, unsigned cSOFGen);
int iflash_erase(unsigned int imageSize);
#endif




#endif
