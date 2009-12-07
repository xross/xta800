#ifndef IPODEXTENDED_H_
#define IPODEXTENDED_H_

#include "global.h"
#include "iPod.h"

extern int in_extended_mode;
#if defined __XC__
int iPodEnterExtended(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);
int iPodExtendedDisplayImage(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);
#elif defined __XC__
int iPodEnterExtended(streaming_chanend ciPodHID, unsigned cAudioMixer[3], unsigned cLED, unsigned cI2CReset);
int iPodExtendedDisplayImage(streaming_chanend ciPodHID, unsigned cAudioMixer[3], unsigned cLED, unsigned cI2CReset);
#endif

#endif /*IPODEXTENDED_H_*/
