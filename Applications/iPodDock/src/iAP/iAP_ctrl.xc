#include "iAP.h"

/*
 * iAP_ctrl:
 * Called on client request from upper layer
 */
int iAP_ctrl(streaming_chanend cCtrl,
    int lingo, int timenow)
{
  if (lingo == ctrlLingo)
  {
    iAP_ctrlLingo(cCtrl, timenow);
  }
  else if (lingo == unsolicitedLingo)
  {
#if GENERAL_LINGO != 0
    if (iAP_generalLingo_unsolicited (cCtrl))
      return 1;
#endif
#if MICROPHONE_LINGO != 0
    if (iAP_microphoneLingo_unsolicited (cCtrl))
      return 1;
#endif  
#if SIMPLE_REMOTE_LINGO != 0
    if (iAP_simpleLingo_unsolicited (cCtrl))
      return 1;
#endif
#if DISPLAY_REMOTE_LINGO != 0
    if (iAP_displayLingo_unsolicited (cCtrl))
      return 1;
#endif
#if EXT_REMOTE_LINGO != 0
    if (iAP_extendedLingo_unsolicited (cCtrl))
      return 1;
#endif
#if POWER_LINGO != 0
    if (iAP_powerLingo_unsolicited (cCtrl))
      return 1;
#endif  
#if SPORTS_LINGO != 0
    if (iAP_sportsLingo_unsolicited (cCtrl))
      return 1;
#endif  
#if EQUALIZER_LINGO != 0
    if (iAP_equalizerLingo_unsolicited (cCtrl))
      return 1;
#endif  
#if AUDIO_LINGO != 0
    if (iAP_audioLingo_unsolicited (cCtrl))
      return 1;
#endif
#if STORAGE_LINGO != 0
    if (iAP_storageLingo_unsolicited (cCtrl))
      return 1;
#endif
#if RF_TUNER_LINGO != 0
    if (iAP_rftunerLingo_unsolicited (cCtrl))
      return 1;
#endif
#if LOCATION_LINGO != 0
    if (iAP_locationLingo_unsolicited (cCtrl))
      return 1;
#endif
    cCtrl <: CMD_none;
    return 1;
  }
#if GENERAL_LINGO != 0
  if (lingo == generalLingo)
  {
    iAP_generalLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if MICROPHONE_LINGO != 0
  if (lingo == microphoneLingo)
  {
    iAP_microphoneLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if SIMPLE_REMOTE_LINGO != 0
  if (lingo == simpleLingo)
  {
    iAP_simpleLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if DISPLAY_REMOTE_LINGO != 0
  if (lingo == displayLingo)
  {
    iAP_displayLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if EXT_REMOTE_LINGO != 0
  if (lingo == extendedLingo)
  {
    iAP_extendedLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if POWER_LINGO != 0
  if (lingo == powerLingo)
  {
    iAP_powerLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if SPORTS_LINGO != 0
  if (lingo == sportsLingo)
  {
    iAP_sportsLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if EQUALIZER_LINGO != 0
  if (lingo == equalizerLingo)
  {
    iAP_equalizerLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if AUDIO_LINGO != 0
  if (lingo == audioLingo)
  {
    iAP_audioLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if STORAGE_LINGO != 0
  if (lingo == storageLingo)
  {
    iAP_storageLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if LOCATION_LINGO != 0
  if (lingo == locationLingo)
  {
    iAP_locationLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
#if RF_TUNER_LINGO != 0
  if (lingo == rftunerLingo)
  {
    iAP_rftunerLingo_ctrl(cCtrl, lingo, timenow);
  }
#endif
  return 0;
}

