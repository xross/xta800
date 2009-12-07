#ifndef AUDIO_CLIENT_H_
#define AUDIO_CLIENT_H_

#include "global.h"

/*
 * audio_client_set_sample_rate:
 * Communicate with audio class in Endpoint Manager
 * Set sample rate
 */
int audio_client_set_sample_rate(streaming_chanend c, int freq);

#endif /*AUDIO_CLIENT_H_*/
