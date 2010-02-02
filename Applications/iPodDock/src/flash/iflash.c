// C file for iflash upgrade functions
#include <xs1.h>
#include <stdio.h>
#include <stdlib.h>
#include <global.h>
#include <flashlib.h>
#include "iflash.h"
#include "flash.h"


unsigned int supplyData( void* userPtr, unsigned int numBytes, unsigned char* dstBuf )
{
  int* supplyArguments = (int*)userPtr;
  //  dstBuf supplied by flashlib. numBytes - 1? How ever many supplied, we 
  // need to buffer from audio channel until met.
  // Also need to manage SOFs.

  // Don't really want a local buffer 
  // Although we know that dstBuf is big enough.
  
  int byte_ctr = 0;
  while (byte_ctr < numBytes) {
    unsigned char byte;
    byte = get_audio_byte( supplyArguments[0], supplyArguments[1]);
    dstBuf[byte_ctr] = byte;
    byte_ctr ++;
  };
  //  printf("%i",byte_ctr);

  return byte_ctr;
}

int iflash_done = 0;
#pragma stackfunction 2048
int iflash_support( unsigned len, unsigned version, streaming_chanend cAudioStreaming, unsigned cSOFGen)
{
  if (!iflash_done)
  {
#if 1
    fl_BootImageInfo bii;
    unsigned supplyArguments[2];
  unsigned tmpbuf[512];
  //    tmpbuf[0] =fl_getPageSize();
  // Got magic. Try read from flash.
  //    printhexln(tmpbuf[0]);
  //    printhexln(tmpbuf[1]);
  //    printhexln(tmpbuf[2]);
    //    printhexln(tmpbuf[3]);
    
    
    supplyArguments[0] = cAudioStreaming;
    supplyArguments[1] = cSOFGen;

    if (flash_pre(cAudioStreaming))
      vprintstrln("Failed to connect to flash");

    if (0 != fl_setProtection(0)) {
      vprintstrln("Failed to clear protection");
      flash_post(cAudioStreaming);
      return 1;
    };


    if( 0 != fl_getFirstBootImage( &bii ) ) {
      flash_post(cAudioStreaming);
      return 1;
    }
    if( 0 != fl_addBootImage( &bii, len, &supplyData, (void*)supplyArguments ) ) {
      vprintstrln("Error: failed to add new boot image.");
      flash_post(cAudioStreaming);
      return 1;
    } else {
      // Do some verification image has been added succesfully. This may break the
      // USB bus.
      if( 0 != fl_getFirstBootImage( &bii ) ){
	fprintf(stderr,"Error: failed to locate factory boot image.\n");
      } else {
	if( 0 != fl_getNextBootImage( &bii ) ){
	  fprintf(stderr,"Error: failed to find added boot image for verification.\n");
	}
      }
      flash_post(cAudioStreaming);
    }
#endif
  }
  iflash_done = 1;
  printstrln("woohoo. Upgrade complete");
  return 0;
}

#pragma stackfunction 1024
int iflash_erase(unsigned int imageSize) {
  fl_BootImageInfo bootImageInfo;

  //  unsigned int pageSize = fl_getPageSize();
  unsigned int numSectors = fl_getNumSectors();
  unsigned int nextSecNum = 0;
  unsigned int lastAddress;
  fl_getFirstBootImage( &bootImageInfo ) ;
  
  lastAddress = bootImageInfo.startAddress+bootImageInfo.size;
  while( (nextSecNum<numSectors) && (fl_getSectorAddress(nextSecNum)<lastAddress) ) {
    nextSecNum++;
  }
  if( nextSecNum == numSectors ) {
    return (1);
  }
  unsigned int writeAddress = fl_getSectorAddress(nextSecNum);
  unsigned int limitAddress = writeAddress+imageSize;

  fl_setProtection(0);

  unsigned int eraseSec = nextSecNum;
  unsigned int eraseAddress = writeAddress;
#if 0
  while( (eraseAddress>=writeAddress) && (eraseAddress<limitAddress) )
  {
    fl_eraseSector( eraseSec );
    eraseSec++;
    eraseAddress = fl_getSectorAddress(eraseSec);
  }
#endif
  return 0;
}
