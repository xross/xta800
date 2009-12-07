#ifndef AUDIO_H_
#define AUDIO_H_

#include "audio_client.h"
#include "audio_ctrl.h"
#include "audio_defines.h"
#include "global.h"

/*
 * audio_rx:
 * Receive function for incoming data on the audio endpoint
 */
void audio_rx(streaming_chanend c, char data[], int datalen, int ackstate);


#endif /*AUDIO_H_*/
