#ifndef AUDIO_CTRL_H_
#define AUDIO_CTRL_H_

/*
 * audio_ctrl
 * Function receiving audio class requests from the clients
 * Currently only accepts sample rate adjustment
 */
int audio_ctrl(streaming_chanend cCtrl,
    int cmd, int timenow);

#endif /*AUDIO_CTRL_H_*/
