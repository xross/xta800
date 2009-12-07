#ifndef IPODOPTIONS_H_
#define IPODOPTIONS_H_

// --- Device Lingos Spoken ---

// Base Lingos
#define GENERAL_LINGO             (1 << 0x0)
#define AUDIO_LINGO               (1 << 0xA)
#define SIMPLE_REMOTE_LINGO       (1 << 0x2)

// Option Lingos
//#define MICROPHONE_LINGO          (1 << 0x1)
#define DISPLAY_REMOTE_LINGO      (1 << 0x3)
//#define EXT_REMOTE_LINGO          (1 << 0x4)
//#define POWER_LINGO               (1 << 0x5)
//#define RF_TUNER_LINGO            (1 << 0x7)
#define EQUALIZER_LINGO           (1 << 0x8)
//#define SPORTS_LINGO              (1 << 0x9)
//#define STORAGE_LINGO             (1 << 0xC)
//#define LOCATION_LINGO             (1 << 0xE)

// -- Device Options --
#define POWER_OPTION              0x00 // 0 = low power, 1 = high power
#define AUTH_OPTION               0x02 // 0 = no auth, 1 = defer auth (1.0), 2 = imm auth (2.0)

#define ACCESSORY_MODEL_NUMBER      "Model 1"
#define ACCESSORY_MANUFACTURER      "XMOS"
#define ACCESSORY_HARDWARE_VERSION  {1, 0, 0}
#define ACCESSORY_FIRMWARE_VERSION  {0, 9, 0}
#define ACCESSORY_NAME             "XMOS iPod Dock"






// ----- Calculated options -----

#ifndef GENERAL_LINGO
#define GENERAL_LINGO 0
#endif
#ifndef MICROPHONE_LINGO
#define MICROPHONE_LINGO 0
#endif
#ifndef SIMPLE_REMOTE_LINGO
#define SIMPLE_REMOTE_LINGO 0
#endif
#ifndef DISPLAY_REMOTE_LINGO
#define DISPLAY_REMOTE_LINGO 0
#endif
#ifndef EXT_REMOTE_LINGO
#define EXT_REMOTE_LINGO 0
#endif
#ifndef POWER_LINGO
#define POWER_LINGO 0
#endif
#ifndef RF_TUNER_LINGO
#define RF_TUNER_LINGO 0
#endif
#ifndef EQUALIZER_LINGO
#define EQUALIZER_LINGO 0
#endif
#ifndef SPORTS_LINGO
#define SPORTS_LINGO 0
#endif
#ifndef AUDIO_LINGO
#define AUDIO_LINGO 0
#endif
#ifndef STORAGE_LINGO
#define STORAGE_LINGO 0
#endif
#ifndef LOCATION_LINGO
#define LOCATION_LINGO 0
#endif
#define IPOD_LINGOS_SPOKEN (GENERAL_LINGO + MICROPHONE_LINGO + SIMPLE_REMOTE_LINGO + \
    DISPLAY_REMOTE_LINGO + EXT_REMOTE_LINGO + POWER_LINGO + \
    RF_TUNER_LINGO + EQUALIZER_LINGO + SPORTS_LINGO + AUDIO_LINGO + STORAGE_LINGO + LOCATION_LINGO)

#define IPOD_OPTIONS ((POWER_OPTION << 2) | (AUTH_OPTION << 0))


#endif /*IPODOPTIONS_H_*/
