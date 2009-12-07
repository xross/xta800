#include "audio.h"

/*
 * audio_ctrl
 * Function receiving audio class requests from the clients
 * Currently only accepts sample rate adjustment
 */

int audio_set_sample_freq(int freq);

int audio_ctrl(streaming_chanend cCtrl,
    int cmd, int timenow)
{
  switch (cmd)
  {
    case 0:
    {
      int freq;
      cCtrl :> freq;

      audio_set_sample_freq(freq);
      
      cCtrl <: 0;
    }
    break;
  }
  
  return 0;
}

