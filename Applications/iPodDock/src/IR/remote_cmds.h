#ifndef REMOTE_CMDS_H_
#define REMOTE_CMDS_H_

// Define the commands for the iPod
static const int nButtons = 7;
typedef enum e_remotecommands
{
  remoteNoPress = 0,
  remotePlayPause,
  remoteVolumeUp,
  remoteVolumeDown,
  remoteTrackNext,
  remoteTrackPrev,
  remoteMenu
};

static const int simpleContextButtonValues[] = {
    0x00000000, 0x01000000, 0x02000000, 0x04000000,
    0x08000000, 0x10000000, 0x00004000
};

static const int extendedButtonValues[] = {
    0x00,       0x01,       0x00,       0x00,
    0x03,       0x04,       0x00
};

#endif /*REMOTE_CMDS_H_*/
