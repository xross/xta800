#ifndef IPODMAIN_H_
#define IPODMAIN_H_

#include "global.h"

/*
 * iPodMain
 * Main iPod Control Stack
 */
#ifdef __XC__
void iPodMain(
    streaming_chanend ciPodHID, streaming chanend cI2CReset,
    streaming chanend cAudioMixer[3], streaming chanend cOsc,
    streaming_chanend ciPodAudio, streaming chanend cIR, streaming chanend cLED);
#elif defined __STDC__
void iPodMain(
    streaming_chanend ciPodHID,  chanend cI2CReset,
    chanend cAudioMixer[3],  chanend cOsc,
    streaming_chanend ciPodAudio,  chanend cIR,  chanend cLED);
#endif

extern int volume;
extern int iPodusingIDPS;

#endif /*IPODMAIN_H_*/
