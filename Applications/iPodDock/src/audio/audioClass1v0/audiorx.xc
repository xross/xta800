#include <xs1.h>
#include "usb_descriptors.h"
#include "UsbHostLLD.h"
#include "audio_defines.h"
#include "audio.h"
#include "global.h"
#include "UsbHostLLD.h"

#pragma unsafe arrays
void audio_rx(streaming_chanend c, char data[], int datalen, int ackstate)
{
  if (ackstate == USB_TRANS_ACKSTATE_ACK)
  {
    if (datalen > 0)
    {
  #ifdef DSP_ENABLED
  #ifdef USE_STREAMING_CHANNELS
      c <: datalen>>1;
  #else
      outuint(c,datalen>>1);
  #endif
  #endif //DSP_ENABLED
      
      if ((datalen>>1) & 1)
      {
        vprint("Audio datalen odd");
      }
      // Send audio data to audio thread
      for (int i=0; i<datalen>>1; i++)
      {
        int val = (int) (data, short[])[i];
    #ifdef USE_STREAMING_CHANNELS
        c <: val;
    #else
        outuint(c,val);
    #endif
      }
    }
  }
  USBLLD_readfinished();
}
