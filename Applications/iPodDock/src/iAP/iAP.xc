
#include "iAP.h"

/*
 * iAP_init:
 * Called on new iPod connection
 */
void iAP_init()
{

#if GENERAL_LINGO != 0
  iAP_generalLingo_init();
#endif
#if MICROPHONE_LINGO != 0
  iAP_microphoneLingo_init();
#endif
#if SIMPLE_REMOTE_LINGO != 0
  iAP_simpleLingo_init();
#endif
#if DISPLAY_REMOTE_LINGO != 0
  iAP_displayLingo_init();
#endif
#if EXT_REMOTE_LINGO != 0
  iAP_extendedLingo_init();
#endif
#if POWER_LINGO != 0
  iAP_powerLingo_init();
#endif
#if SPORTS_LINGO != 0
  iAP_sportsLingo_init();
#endif
#if AUDIO_LINGO != 0
  iAP_audioLingo_init();
#endif
#if RF_TUNER_LINGO != 0
  iAP_rftunerLingo_init();
#endif
#if STORAGE_LINGO != 0
  iAP_storageLingo_init();
#endif
#if EQUALIZER_LINGO != 0
  iAP_equalizerLingo_init();
#endif
#if LOCATION_LINGO != 0
  iAP_locationLingo_init();
#endif
}

/*
 * iAP_flush:
 * Called on iPod disconnection
 */
void iAP_flush(streaming_chanend c)
{

#if GENERAL_LINGO != 0
  iAP_generalLingo_flush(c);
#endif
#if MICROPHONE_LINGO != 0
  iAP_microphoneLingo_flush(c);
#endif
#if SIMPLE_REMOTE_LINGO != 0
  iAP_simpleLingo_flush(c);
#endif
#if DISPLAY_REMOTE_LINGO != 0
  iAP_displayLingo_flush(c);
#endif
#if EXT_REMOTE_LINGO != 0
  iAP_extendedLingo_flush(c);
#endif
#if POWER_LINGO != 0
  iAP_powerLingo_flush(c);
#endif
#if SPORTS_LINGO != 0
  iAP_sportsLingo_flush(c);
#endif
#if AUDIO_LINGO != 0
  iAP_audioLingo_flush(c);
#endif
#if RF_TUNER_LINGO != 0
  iAP_rftunerLingo_flush(c);
#endif
#if STORAGE_LINGO != 0
  iAP_storageLingo_flush(c);
#endif
#if EQUALIZER_LINGO != 0
  iAP_equalizerLingo_flush(c);
#endif
#if LOCATION_LINGO != 0
  iAP_locationLingo_flush(c);
#endif
}
