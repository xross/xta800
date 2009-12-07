#ifndef PORTMANAGER_H_
#define PORTMANAGER_H_

#include "audioManager.h"

/*
 * portManagerWrapper:
 * Wraps the audio manager and port manager threads
 */
void portManagerWrapper(
    out port p, streaming chanend cOsc, streaming chanend cUSBReset, streaming chanend cLED, streaming chanend cI2CReset, streaming chanend cIR, in port pIR,
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports,
    in port pRegWrite0, in port pRegWrite1,
    streaming chanend cUSBReg);

#endif /*PORTMANAGER_H_*/
