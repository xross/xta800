#include "audioManager.h"
#include "safestring.h"
#include <flashlib.h>
#include "flash.h"

int iflash_state = 0;

const char FLASH_MAGIC[9] = "SOMX1234";
const int FLASH_MAGIC_LEN = 8;

int now;
timer tSOF;

unsigned char get_audio_byte(streaming_chanend cAudioStreaming, streaming chanend cSOFGen)
{
  unsigned val;
  unsigned char ret;

  while (1)
  {
    select
    {
#ifdef USE_STREAMING_CHANNELS
    case cAudioStreaming :> val:
#else
    case inuint_byref(cAudioStreaming, val):
#endif
      ret = (val & 0xff);
      //      printstrln("foo");
      return ret;
    case tSOF when timerafter(now + XS1_TIMER_KHZ) :> int _:
      now += XS1_TIMER_KHZ;
      cSOFGen :> int _;
      cSOFGen <: 1;
      break;  
    }
  }
};

int iflash_upgrade(streaming_chanend cAudioStreaming, streaming chanend cSOFGen)
{
  //  unsigned int pageSize = fl_getPageSize();
  unsigned int writeAddress;
  unsigned int numSectors = fl_getNumSectors();
  int upgrade_complete = 0;
  unsigned val;
  unsigned len = 0;
  unsigned version = 0;
  unsigned int nextSecNum = 0;
  int audiodata;
  fl_BootImageInfo bii;
  
  tSOF :> now;
  len = get_audio_byte(cAudioStreaming, cSOFGen);
  len |= (get_audio_byte(cAudioStreaming, cSOFGen) << 8);
  len |= (get_audio_byte(cAudioStreaming, cSOFGen) << 16);
  len |= (get_audio_byte(cAudioStreaming, cSOFGen) << 24);
  version = get_audio_byte(cAudioStreaming, cSOFGen);
  version |= (get_audio_byte(cAudioStreaming, cSOFGen) << 8);
  version |= (get_audio_byte(cAudioStreaming, cSOFGen) << 16);
  version |= (get_audio_byte(cAudioStreaming, cSOFGen) << 24);  
  //    printhexln(len);
  
  // Tell LLD to enter autotiming mode for SOFs. This means the LLD won't
  // block while we flash erase. The audio stream has to be padded by
  // at least the erase time.

  cSOFGen :> int _;
  cSOFGen <: 2;
  
  if (fl_getFirstBootImage(bii))
  {
    return 1;
  }
  writeAddress = bii.startAddress+bii.size;
  while( (nextSecNum<numSectors) && (fl_getSectorAddress(nextSecNum)<writeAddress) ) {
    nextSecNum++;
  }
  if( nextSecNum == numSectors )
  {
    return (1);
  }
  if (fl_setProtection(0)) {
    vprintstrln("Failed to clear protection");
    return 1;
  };

  // Erase partition  
  {
    unsigned int limitAddress = writeAddress+len;
    unsigned int eraseAddress = writeAddress;
    unsigned int eraseSec = nextSecNum;
    while( (eraseAddress>=writeAddress) && (eraseAddress<limitAddress) )
    {
      fl_eraseSector( eraseSec );
      eraseSec++;
      eraseAddress = fl_getSectorAddress(eraseSec);
    }
  }

  // Tell LLD to re-enter standard mode
  cSOFGen :> int _;
  cSOFGen <: 1;

  // Look for end of padding area
  tSOF :> now;
  do
  {
    audiodata = get_audio_byte(cAudioStreaming, cSOFGen);
  }
  while (audiodata == 0);
  
  {
    unsigned int bytesToWrite = len;
    unsigned int isFirstPage = 1;
    unsigned int initialWriteAddress = writeAddress;
    unsigned char pageBuf[256];
    unsigned char pageZero[256];
    while( bytesToWrite > 0 )
    {
      int templen = bytesToWrite;
      
      if (templen > 256)
        templen = 256;
      
      for (int i=0; i<templen; i++)
      {
        pageBuf[i] = get_audio_byte(cAudioStreaming, cSOFGen);
      }
      
      if( isFirstPage )
      {
        safememcpy( pageZero, pageBuf, 256 );
        isFirstPage = 0;
      }
      else
      {
        if(fl_writePage( writeAddress, pageBuf ) )
        {
          return 1;
        }
      }
      writeAddress += templen;
      bytesToWrite -= templen;
    }

    if(fl_writePage( initialWriteAddress, pageZero ) )
    {
      return 1;
    }
  }
  
  return 0;
};

