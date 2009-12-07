#ifndef IFLASH_H_
#define IFLASH_H_

int iflash_upgrade(streaming_chanend cAudioStreaming, streaming chanend cSOFGen);

extern const char FLASH_MAGIC[9];
extern const int FLASH_MAGIC_LEN;
extern int iflash_state;


#endif /*IFLASH_H_*/
