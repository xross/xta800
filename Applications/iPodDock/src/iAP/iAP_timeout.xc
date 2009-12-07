#include "iAP.h"

/*
 * iAP_timeout:
 * Run every time interval by USB stack
 * Checks for any timeouts on the iAP protocol
 */
int iAP_timeout(streaming_chanend cCtrl, int timenow)
{
#if GENERAL_LINGO != 0
  iAP_generalLingo_timeout(cCtrl, timenow);
#endif
  
#if MICROPHONE_LINGO != 0
  iAP_microphoneLingo_timeout(cCtrl, timenow);
#endif
  
#if SIMPLE_REMOTE_LINGO != 0
  iAP_simpleLingo_timeout(cCtrl, timenow);
#endif
  
#if DISPLAY_REMOTE_LINGO != 0
  iAP_displayLingo_timeout(cCtrl, timenow);
#endif
  
#if EXT_REMOTE_LINGO != 0
  iAP_extendedLingo_timeout(cCtrl, timenow);
#endif
  
#if POWER_LINGO != 0
  iAP_powerLingo_timeout(cCtrl, timenow);
#endif  
  
#if SPORTS_LINGO != 0
  iAP_sportsLingo_timeout(cCtrl, timenow);
#endif  
  
#if EQUALIZER_LINGO != 0
  iAP_equalizerLingo_timeout(cCtrl, timenow);
#endif
  
#if AUDIO_LINGO != 0
  iAP_audioLingo_timeout(cCtrl, timenow);
#endif
  
#if STORAGE_LINGO != 0
  iAP_storageLingo_timeout(cCtrl, timenow);
#endif
  
#if RF_TUNER_LINGO != 0
  iAP_rftunerLingo_timeout(cCtrl, timenow);
#endif
  
#if LOCATION_LINGO != 0
  iAP_locationLingo_timeout(cCtrl, timenow);
#endif
  return 0;
}

