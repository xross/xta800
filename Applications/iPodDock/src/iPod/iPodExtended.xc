#include "iPod.h"
#include "remote_cmds.h"
#include "flash.h"

int in_extended_mode = 0;

#define TELEGRAM_SIZE 168
int iPodExtendedDisplayImage(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  unsigned short imagedetails[3];
  int dataoffset = 6;
  char data[TELEGRAM_SIZE];
  int i = 1;
  int retval;
  timer t;
  int now;
  
  flash_read( cAudioMixer[0], 0, 6, (imagedetails, unsigned char[]), cI2CReset);
 
  // First one
  flash_read( cAudioMixer[0], dataoffset, TELEGRAM_SIZE, data , cI2CReset);
  // Pixel format: 2, image width: imagedetails[1], image height: imagedetails[2] 
  if (iAP_extendedLingo_SetDisplayImage_client(ciPodHID, gettransid(), 0, 2,
      imagedetails[1], imagedetails[2], imagedetails[1]*2, data, TELEGRAM_SIZE, retval))
  {
    vprint ("SetDisplayImage failed");
  }
  imagedetails[0] -= (TELEGRAM_SIZE >> 1);
  dataoffset += TELEGRAM_SIZE;

  while (imagedetails[0])
  {
    int templen = (imagedetails[0] << 1);
    if (templen > TELEGRAM_SIZE)
      templen = TELEGRAM_SIZE;
    flash_read( cAudioMixer[0], dataoffset, templen, data, cI2CReset);
    t :> now;
   
    if (iAP_extendedLingo_SetDisplayImage_data_client(ciPodHID, gettransid(), i, data, templen, retval))
    {
      vprintintln(i);
      vprint ("SetDisplayImage data failed");
    }
   
    imagedetails[0] -= (templen >> 1);
    dataoffset += templen;
    i++;
  }
  
  // Reset I2C devices
  t when timerafter(now + 50*XS1_TIMER_KHZ) :> int _;
  if (coProcessorInit())
    vprint("coProcessor failed init");
  
  return 0;
}

int iPodEnterExtended(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  int retval;
  
  if (iAP_generalLingo_EnterRemoteUIMode_client(ciPodHID, gettransid(), retval))
  {
    vprint("Enter RemoteUI (extended lingo) Failed");
  };

  in_extended_mode = 1;
  
  return 0;
}
