#ifndef IPODUSER_H_
#define IPODUSER_H_

#define IPOD_TIME_INTERVAL   (200 * XS1_TIMER_KHZ)

#ifdef __XC__

/*
 * iPod_UserPostAuth:
 * Called when authentication has completed
 */
int iPod_UserPostAuth(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);

/*
 * iPod_UserUnsolicited:
 * Called when an unknown unsolicited iAP message was received
 */
int iPod_UserUnsolicited(streaming_chanend ciPodHID, unsigned cmdval, int rettransid, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);

/*
 * iPod_timer:
 * Called every IPOD_TIME_INTERVAL by the iPod stack
 */ 
void iPod_timer(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);

/*
 * iPod_IR:
 * Called on receipt of an Infra-Red command
 */
int iPod_IR(streaming_chanend ciPodHID, unsigned irval, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset);

#endif

#endif /*IPODUSER_H_*/
