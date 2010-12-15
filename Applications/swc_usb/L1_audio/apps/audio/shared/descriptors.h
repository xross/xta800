
#include "USBAudioCodes.h"

/* Feature Unit (8 channel) */
#define FEATURE_UNIT_8(UNIT_ID, SOURCE_ID) \
0x2A,                               /* 0  bLength: 6+(ch+1)*4 */ \
0x24,                               /* 1  bDescriptorType: CS_INTERFACE */ \
0x06,                               /* 2  bDescriptorSubType: FEATURE_UNIT */\
UNIT_ID,                            /* 3  bUnitID */\
SOURCE_ID,                          /* 4  bSourceID */\
0x0F, 0x00, 0x00, 0x00,             /* 5  bmaControls(0) (Master) */ \
0x0F, 0x00, 0x00, 0x00,             /* 10 bmaControls(1) */ \
0x0F, 0x00, 0x00, 0x00,             /* 15 bmaControls(2) */ \
0x0F, 0x00, 0x00, 0x00,             /* 20 bmaControls(3) */ \
0x0F, 0x00, 0x00, 0x00,             /* 25 bmaControls(4) */ \
0x0F, 0x00, 0x00, 0x00,             /* 30 bmaControls(5) */ \
0x0F, 0x00, 0x00, 0x00,             /* 35 bmaControls(6) */ \
0x0F, 0x00, 0x00, 0x00,             /* 40 bmaControls(7) */ \
0x0F, 0x00, 0x00, 0x00,             /* 45 bmaControls(8) */ \
0x05,                               /* 40 iFeature */ \


/* Feature Unit (10 channel) */
#define FEATURE_UNIT_10(UNIT_ID, SOURCE_ID) \
0x32,                               /* 0  bLength: 6+(ch+1)*4 */ \
0x24,                               /* 1  bDescriptorType: CS_INTERFACE */ \
0x06,                               /* 2  bDescriptorSubType: FEATURE_UNIT */\
UNIT_ID,                            /* 3  bUnitID */\
SOURCE_ID,                          /* 4  bSourceID */\
0x0F, 0x00, 0x00, 0x00,             /* 5  bmaControls(0) (Master) */ \
0x0F, 0x00, 0x00, 0x00,             /* 10 bmaControls(1) */ \
0x0F, 0x00, 0x00, 0x00,             /* 15 bmaControls(2) */ \
0x0F, 0x00, 0x00, 0x00,             /* 20 bmaControls(3) */ \
0x0F, 0x00, 0x00, 0x00,             /* 25 bmaControls(4) */ \
0x0F, 0x00, 0x00, 0x00,             /* 30 bmaControls(5) */ \
0x0F, 0x00, 0x00, 0x00,             /* 35 bmaControls(6) */ \
0x0F, 0x00, 0x00, 0x00,             /* 40 bmaControls(7) */ \
0x0F, 0x00, 0x00, 0x00,             /* 45 bmaControls(8) */ \
0x0F, 0x00, 0x00, 0x00,             /* 50 bmaControls(9) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(10) */ \
0x05,                               /* 60 iFeature */ \

/* Feature Unit (18 channel) */
#define FEATURE_UNIT_18(UNIT_ID, SOURCE_ID) \
0x52,                               /* 0  bLength: 6+(ch+1)*4 */ \
0x24,                               /* 1  bDescriptorType: CS_INTERFACE */ \
0x06,                               /* 2  bDescriptorSubType: FEATURE_UNIT */\
UNIT_ID,                            /* 3  bUnitID */\
SOURCE_ID,                          /* 4  bSourceID */\
0x0F, 0x00, 0x00, 0x00,             /* 5  bmaControls(0) (Master) */ \
0x0F, 0x00, 0x00, 0x00,             /* 10 bmaControls(1) */ \
0x0F, 0x00, 0x00, 0x00,             /* 15 bmaControls(2) */ \
0x0F, 0x00, 0x00, 0x00,             /* 20 bmaControls(3) */ \
0x0F, 0x00, 0x00, 0x00,             /* 25 bmaControls(4) */ \
0x0F, 0x00, 0x00, 0x00,             /* 30 bmaControls(5) */ \
0x0F, 0x00, 0x00, 0x00,             /* 35 bmaControls(6) */ \
0x0F, 0x00, 0x00, 0x00,             /* 40 bmaControls(7) */ \
0x0F, 0x00, 0x00, 0x00,             /* 45 bmaControls(8) */ \
0x0F, 0x00, 0x00, 0x00,             /* 50 bmaControls(9) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(10) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(11) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(12) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(13) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(14) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(15) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(16) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(17) */ \
0x0F, 0x00, 0x00, 0x00,             /* 55 bmaControls(18) */ \
0x05,                               /* 60 iFeature */ \


/* Mixer Unit Descriptor (with 1 input clusters with 8 chans, one output cluster with 2 chans ) 
 * Therefore   n (input channels)         = 8
 *             m (output channels)        = 2
 *             p (number of input pins)   = 1
 *             N (number of bytes in array) = (8 * 2) / 8 = 2 */  
#define MIXER_UNIT_1(UNIT_ID, SOURCE_ID) \
0x10,                               /* 0  bLength: 13 + p + N  */ \
CS_INTERFACE,                       /* 1  bDescriptorType */ \
MIXER_UNIT,                         /* 2  bDesctiptorSubtype */ \
UNIT_ID,                            /* 3  bUnitID */ \
0x01,                               /* 4  bNrInPins */ \
SOURCE_ID,                          /* 5  baSourceID[0] */ \
0x02,                               /* 6  bNrChannels (Num channels in output cluster) */ \
0x03,0x00,0x00,0x00,                /* 7  bmChannelConfig */ \
0x00,                               /* 11 iChannelNames */ \
0xff,0xff,                          /* 12 bmMixerControls */ \
0x00,                               /* 14 bmControls */ \
0x00,                               /* 15 iMixer */ 

/* Mixer Unit Descriptor (with 2 input clusters with 10/10 channels, one output cluster with 
 * 10 chans) 
 * Therefore   n (input channels)            = 20
 *             m (output channels)           = 10                  
 *                                                  Note: m * n must be less than 256
 *             p (number of input pins)      = 2
 *             N (number of bytes in array)  = (n * m)  / 8 
 *                                           = (200) / 8 
 *                                           = 25
 *
 * TODO Proper channel special locations/channel names                                           
 */  
#define MIXER_UNIT_2_10__10( UNIT_ID, SOURCE_ID_1, SOURCE_ID_2 ) \
0x28,                               /* 0     bLength: 13 + p + N  */ \
                                    /*             = 13 + 2 + 25 */ \
                                    /*             = 40          */ \
CS_INTERFACE,                       /* 1     bDescriptorType */ \
MIXER_UNIT,                         /* 2     bDesctiptorSubtype */ \
UNIT_ID,                            /* 3     bUnitID */ \
0x02,                               /* 4     bNrInPins */ \
SOURCE_ID_1,                        /* 5     baSourceID[1] */ \
SOURCE_ID_2,                        /* 5+(p-1)  baSourceID[2] */ \
0x0A,                               /* 5+p   bNrChannels (Num channels in output cluster) */ \
0xFF,0x03,0x00,0x00,                /* 6+p   bmChannelConfig */ \
0x00,                               /* 10+p  iChannelNames */ \
0xff,0xff, 0xff, 0xff,              /* 11+p  bmMixerControls */ \
0xff,0xff, 0xff, 0xff,  \
0xff,0xff, 0xff, 0xff,  \
0xff,0xff, 0xff, 0xff,  \
0xff,0xff, 0xff, 0xff,  \
0xff,0xff, 0xff, 0xff,  \
0xff,\
0x00,                               /* 11+p+N bmControls */ \
0x00,                               /* 12+p+N iMixer */ 





/* Output Terminal Descriptor: */
#define OUTPUT_TERM(TERM_ID, SOURCE_ID, CLOCK_ID, STRING_ID) \
0x0C,                               /* 0  bLength */ \
CS_INTERFACE,                       /* 1  bDescriptorType: CS_INTERFACE */ \
OUTPUT_TERMINAL,                    /* 2  bDescriptorSubType: OUTPUT_TERMINAL */ \
TERM_ID,                            /* 3  bTerminalID */ \
0x01, 0x03,                         /* 4  wTerminalType */ \
0x00,                               /* 6  bAssocTerminal */ \
SOURCE_ID,                          /* 7  bSourceID */ \
CLOCK_ID,                           /* 8  bCSourceUD */ \
0x00, 0x00,                         /* 9  bmControls */ \
STRING_ID,                               /* 11 iTerminal */ 

/* Selector unit */
#define SELECTOR_UNIT_2(UNIT_ID, SOURCE_ID_1, SOURCE_ID_2) \
0x9,                                /* bLength */ \
CS_INTERFACE, \
SELECTOR_UNIT, \
UNIT_ID,\
2 ,\
SOURCE_ID_1, \
SOURCE_ID_2, \
0, \
0,

