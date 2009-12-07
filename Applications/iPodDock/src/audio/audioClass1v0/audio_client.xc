#include "global.h"

/*
 * audio_client_set_sample_rate:
 * Communicate with audio class in Endpoint Manager
 * Set sample rate
 */
int audio_client_set_sample_rate(streaming_chanend c, int freq)
{
  int ret;
  do
  {
    c <: 0;
    c :> ret;
  } while (ret);
  
  c <: 0;
  c <: freq;
  c :> int _;
  return 0;
}



