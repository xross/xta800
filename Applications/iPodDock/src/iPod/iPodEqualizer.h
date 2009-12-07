#ifndef IPODEQUALIZER_H_
#define IPODEQUALIZER_H_

#include "global.h"

/*
 * iPod_Equalizer_Rx
 * Call on receipt of equalizer command from iAP stack
 */
#ifdef __XC__
void iPod_Equalizer_Rx(streaming_chanend ciPodHID, int cmdVal, int rettransid, streaming chanend cAudioMixer[3]);
#elif defined __STDC__
void iPod_Equalizer_Rx(streaming_chanend ciPodHID, int cmdVal, int rettransid, unsigned cAudioMixer[3]);
#endif

#endif /*IPODEQUALIZER_H_*/
