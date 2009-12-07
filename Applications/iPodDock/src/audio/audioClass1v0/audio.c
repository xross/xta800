#include "usb_descriptors.h"
#include "audio_defines.h"
#include "audio.h"
#include "device.h"

int audio_find_audiostreaming_if(int *val)
{
  int i;
  for (i=0; i<nDeviceInterfaces; i++)
  {
    if (deviceInterfaces[i].interfaceClass == EPIFClass_Audio &&
        deviceInterfaces[i].interfaceSubClass == EPIFSubClass_AudioStreaming)
    {
      break;
    }
  }
  if (i == nDeviceInterfaces)
  {
    return 1;
  }
  else
  {
    *val = i;
    return 0;
  }
}

int audio_find_audiostreaming_ep(int *val)
{
  int i, ifnum;
  if (audio_find_audiostreaming_if(&ifnum))
    return 1;
    
  for (i=0; i<nDeviceEPs; i++)
  {
    if (deviceEPs[i].interfaceNumber == ifnum)
    {
      break;
    }
  }
  if (i == nDeviceEPs)
  {
    return 1;
  }
  else
  {
    *val = i;
    return 0;
  }
}

int audio_set_sample_freq(int freq)
{
  int epnum;
  
  s_usb_endpoint *ep;
  
  if (audio_find_audiostreaming_ep(&epnum))
    return 1;

  if (epnum < nDeviceEPs)
  {
    int freqarray[1];

    ep = &deviceEPs[epnum];
    
    freqarray[0] = freq;
    
    USBLLD_ControlTransferOut(deviceAddress,
      BRT_TYPE_CLASS, BRT_RECIPIENT_ENDPOINT,
      1, (CS_ENDPOINT_SAMPLING_FREQ<<8), ((ep->dir & 1) << 7) | (ep->EPnum), 3, (char*)freqarray);
  }

  return 0;
}
