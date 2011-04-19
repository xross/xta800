
/**
 * @file endpoint0.xc
 * @brief Implements endpoint zero for an audio 2.0 device. Includes all descriptors
 * @author Ross Owen, XMOS Semiconductor
 * @version 0.1
 */

#include <xs1.h>
#include <print.h>
#include <safestring.h>
#include "xud.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_UIFM_Functions.h"
#include "XUD_Defines_Internal.h"
#include "USBAudioCodes.h"
#include "dbTable.h"
#include "descriptors.h"
#include "param.h"
#include "mixer.h"
#include "sof_clock.h"

//#define AUDIO_1V0 1 // Note: Enumeration will complete with AUDIO v1.0 descriptors, however, the rest of the code is not currently guarenteed to stream audio in a correct manner.

#define AUDIO2 1
//#define ASYNC


#define MAX 0x7fffffff

int dbToMultiplier(short db) 
{
    db = - (db >> 8);  // 0x00 .. 0x80
    if (db >= 0) 
    {
        return dbTable[db];
    }
    return MAX;
}

unsigned longMul(unsigned a, unsigned b) 
{
    unsigned x,y;
    {x, y} = lmul(a, b, 0, 0);
    return x >> 1;
}


#define VOLCHANNELS   19    // 18 channels + master

unsigned int shared_params[7] = {48000, 1, 1, 0, 0, 0, 96000};

unsigned int usb_in_volumes[VOLCHANNELS] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0, 0, 0};
unsigned int usb_in_mutes[VOLCHANNELS] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
unsigned int usb_in_premult[VOLCHANNELS] = {MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX};
unsigned int analogue_in_volumes[VOLCHANNELS] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
unsigned int analogue_in_mutes[VOLCHANNELS] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
unsigned int analogue_in_premult[VOLCHANNELS] = {MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX};

/* USB Out mixer params */
unsigned mixer_params_USB[] = 
{                          
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,                        /* 0:10 Channel Weights */
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0                         /* 11:19 Channel Mutes */
};

/* Analog mixer params - L */
unsigned mixer_params_anaL[] = 
{                          
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,                        /* 0:10 Channel Weights */
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0                         /* 11:19 Channel Mutes */
};

/* Analog mixer params - R */
unsigned mixer_params_anaR[] = 
{                          
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,                        /* 0:10 Channel Weights */
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0                         /* 11:19 Channel Mutes */
};



/* Pre multiplied weights for mixer */
unsigned mixer_mults[] = {
                            MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX, MAX,            /* USB Out MLRLRLRLR */
                            MAX, 0  , MAX, 0  , MAX, 0,   MAX, 0,   MAX,            /* ADC, L channel  */
                            0  , MAX, 0,   MAX, 0,   MAX, 0,   MAX, 0,              /* ADC, R channel */
                        };


void updateMasterVol( int unitID )
{
    int x;
    {
    switch( unitID)
    {
        case 0xa:
            for (int i = 1; i < VOLCHANNELS; i++)
            {
                x = longMul(dbToMultiplier(usb_in_volumes[0]), dbToMultiplier(usb_in_volumes[i])) * !usb_in_mutes[0] * !usb_in_mutes[i];
                usb_in_premult[i] = x;

            }
            break;

        case 0xb:
            for (int i = 1; i < VOLCHANNELS; i++) {
                x =  longMul(dbToMultiplier(analogue_in_volumes[0]), dbToMultiplier(analogue_in_volumes[i]))
                    * !analogue_in_mutes[0] * !analogue_in_mutes[i];
                analogue_in_premult[i] = x;
            }
            break;

        case 12:
            break;
        case 13:
            break;
        case 14:  //compiler bug
            break;
        case 32:
            break;

        default:
            XUD_Error_hex("MVol: No such unit: ", unitID);
            break;
    }
    }
}  

void updateVol(int unitID, int channel)
{   
    int x;
    if (channel == 0)
    {
        updateMasterVol( unitID );
    }
    else
    {
        switch( unitID )
        {
            case 0xa:
                /*                                             MVol                             CVol            MMute         CMute */
                x = longMul(dbToMultiplier(usb_in_volumes[0]), dbToMultiplier(usb_in_volumes[channel]))
                                * !usb_in_mutes[0] * !usb_in_mutes[channel];
                usb_in_premult[channel] = x;
                break;

            case 0xb:
                /*                                             MVol           CVol             MMute        CMute */
                x = longMul(dbToMultiplier(analogue_in_volumes[0]), dbToMultiplier(analogue_in_volumes[channel]))
                                * !analogue_in_mutes[0] * !analogue_in_mutes[channel];
                analogue_in_premult[channel] = x;
            
                break;

            case 12: /* usb mix */
                x = longMul(dbToMultiplier(mixer_params_USB[0]), dbToMultiplier(mixer_params_USB[channel])) * 
                    !mixer_params_USB[9] * !mixer_params_USB[9 + channel];
                
                mixer_mults[channel] = x;
                break;

            case 13:
                x = longMul(dbToMultiplier(mixer_params_anaL[0]), dbToMultiplier(mixer_params_anaL[channel])); 
                    // * !mixer_params_anaL[9] * !mixer_params_anaL[9 + channel];
                
                mixer_mults[9+channel] = x;
              break;
            
            case 14: //compiler bug
                break;

             case 32:
                x = longMul(dbToMultiplier(mixer_params_anaR[0]), dbToMultiplier(mixer_params_anaR[channel])); 
                    //* !mixer_params_anaR[9] * !mixer_params_anaR[9 + channel];
                
                mixer_mults[18+channel] = x;
                break;


            default: 
                /* Don't hit */
                 XUD_Error_hex("Vol: No such unit: ",  unitID);
                break;
        }
    }
}

#ifdef AUDIO_1V0

// This devices Device Descriptor:
#define DESC_DEVICE \
{ \
    18,                                   /* 0  bLength : Size of descriptor in Bytes (18 Bytes) */ \
    XUD_DESC_DEV_B_DESCTYPE_DEVICE,       /* 1  bdescriptorType */ \
    XUD_DESC_DEV_BCD_USB_20_0,            /* 2  bcdUSB */ \
    XUD_DESC_DEV_BCD_USB_20_1,            /* 3  bcdUSB */ \
    XUD_DESC_DEV_B_DEVCLASS_INTER,        /* 4  bDeviceClass */ \
    0,                                    /* 5  bDeviceSubClass */ \
    0,                                    /* 6  bDeviceProtocol */ \
    64,                                   /* 7  bMaxPacketSize */ \
    VENDOR_LSB,                           /* 8  idVendor */ \
    VENDOR_MSB,                           /* 9  idVendor */ \
    PRODUCT_LSB,                          /* 10 idProduct */ \
    PRODUCT_MSB,                          /* 11 idProduct */ \
    0x10,                                 /* 12 bcdDevice : Device release number */ \
    0x00,                                 /* 13 bcdDevice : Device release number */ \
    0x01,                                 /* 14 iManufacturer : Index of manufacturer string */ \
    0x02,                                 /* 15 iProduct : Index of product string descriptor */ \
    0x00,                                 /* 16 iSerialNumber : Index of serial number decriptor */ \
    0x01                                  /* 17 bNumConfigurations : Number of possible configs */ \
}

#else

// This devices Device Descriptor:
#define DESC_DEVICE \
{ \
  18, 				                   /* 0  bLength : Size of descriptor in Bytes (18 Bytes) */ \
  XUD_DESC_DEV_B_DESCTYPE_DEVICE,    /* 1  bdescriptorType */ \
  XUD_DESC_DEV_BCD_USB_20_0,         /* 2  bcdUSB */ \
  XUD_DESC_DEV_BCD_USB_20_1,         /* 3  bcdUSB */ \
  0xef,                              /* 4  bDeviceClass */ \
  2,                                 /* 5  bDeviceSubClass */ \
  1,                                 /* 6  bDeviceProtocol */ \
  64,                                /* 7  bMaxPacketSize */ \
  VENDOR_LSB,                        /* 8  idVendor */ \
  VENDOR_MSB,                        /* 9  idVendor */ \
  PRODUCT_LSB,                       /* 10 idProduct */ \
  PRODUCT_MSB,                       /* 11 idProduct */ \
  0x10,                              /* 12 bcdDevice : Device release number */ \
  0x00,                              /* 13 bcdDevice : Device release number */ \
  0x01,                              /* 14 iManufacturer : Index of manufacturer string */ \
  0x02,                              /* 15 iProduct : Index of product string descriptor */ \
  0x00,                              /* 16 iSerialNumber : Index of serial number decriptor */ \
  0x01                               /* 17 bNumConfigurations : Number of possible configs */ \
}

#endif 
                                    
/* Device Qualifier Descriptor */
#define DESC_DEVQUAL \
{ \
    10,                              /* 0  bLength (10 Bytes) */ \
    XUD_DESC_DEV_B_DESCTYPE_DEVQUAL, /* 1  bDescriptorType */ \
    XUD_DESC_DEV_BCD_USB_20_0,       /* 2  bcdUSB */ \
    XUD_DESC_DEV_BCD_USB_20_1,       /* 3  bcdUSB */ \
    0xEF,                            /* 4  bDeviceClass */ \
    0x02,                            /* 5  bDeviceSubClass */ \
    0x01,                            /* 6  bDeviceProtocol */ \
    64,                              /* 7  bMaxPacketSize */ \
    0x01,                            /* 8  bNumConfigurations : Number of possible configs */ \
    0x00                             /* 9  bReserved (must be zero) */ \
}


#ifdef AUDIO_1V0

// Configuration descriptor for Audio v1.0
#define DESC_CONFIG { \
0x09, 0x02, 0xDD, 0x00, 0x04, 0x01, 0x00, 0xC0, 0x32, \
0x09, 0x04, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00, 0x04, \
0x0A, 0x24, 0x01, 0x00, 0x01, 0x47, 0x00, 0x02, 0x01, 0x02, 0x0C, 0x24, 0x02, 0x01, \
0x01, 0x01, 0x00, 0x02, 0x03, 0x00, 0x00, 0x00, 0x0C, 0x24, 0x02, 0x02, 0x01, 0x02, 0x00, 0x01, \
0x01, 0x00, 0x00, 0x00, 0x09, 0x24, 0x03, 0x06, 0x01, 0x03, 0x00, 0x09, 0x00, \
0x09, 0x24, 0x03, \
0x07, 0x01, 0x01, 0x00, 0x0A, 0x00, \
0x0A, 0x24, 0x06, 0x09, 0x01, 0x01, 0x01, 0x02, 0x02, 0x05, \
0x09, 0x24, 0x06, 0x0A, 0x02, 0x01, 0x03, 0x00, 0x05, \
0x09, 0x04, 0x01, 0x00, 0x00, 0x01, 0x02, 0x00, 0x03, /* audio streaming int */\
0x09, 0x04, 0x01, 0x01, 0x01, 0x01, 0x02, 0x00, 0x03,  /* audio streaming int */ \
0x07, 0x24, 0x01, 0x01, 0x01, \
0x01, 0x00, 0x0b, 0x24, 0x02, 0x01, 0x02, 0x02, 0x10, 0x01, 0x44, 0xAC, 0x00, \
0x09, 0x05, 0x01, 0x09, 0xC8, 0x00, 0x01, 0x05, 0x00, 0x07, 0x25, 0x01, 0x01, 0x01, 0x01, 0x00, \
0x09, 0x04, 0x02, 0x00, 0x00, 0x01, 0x02, 0x00, 0x06, /* Audio streaming int IN */ \
0x09, 0x04, 0x02, 0x01, 0x01, 0x01, 0x02, 0x00, 0x06, /* Audio steaming int IN */ \
0x07, 0x24, 0x01, 0x07, 0x01, 0x01, 0x00, 0x0E, 0x24, 0x02, 0x01, 0x01, 0x02, 0x10, \
0x02, 0x80, 0xBB, 0x00, 0x44, 0xAC, 0x00, 0x09, 0x05, 0x82, 0x05, 0x64, 0x00, 0x01, 0x05, 0x00, \
0x07, 0x25, 0x01, 0x00, 0x00, 0x00, 0x00, 0x09, 0x04, 0x03, 0x00, 0x01, 0x03, 0x00, 0x00, 0x00, \
0x09, 0x21, 0x00, 0x01, 0x00, 0x01, 0x22, 0x32, 0x00, 0x07, 0x05, 0x83, 0x03, 0x04, 0x00, 0x10, \
}

#else

/*Configuration Descriptor for Audio 2.0 */
/* 
    IDs:
    ------------------------------------
    Clock source                    : 3 
    Output term (Speakers)          : 7
    Input term (USB Streaming)      : 6
    Output term (USB Streaming)     : 8 
    Input term (Analog in)          : 9
    Feature unit (USB Streaming IN) : 10
    Feature unit (Analog IN)        : 11

    Endpoints:
    -----------------------------------
    0  IN/OUT  :  Control Endpoint 0 (special BiDi case) 
    1  OUT     :  Audio Streaming (Device is sink)
    1  IN      :  Async sink feedback 
    2  OUT     :  -
    2  IN      :  Audio Streaming (Device is source)
    3  OUT     :  -
    3  IN      :  HID
    4: MIDI (0x04 for OUT)
    5: MIDI (0x85 for IN)
*/
static unsigned char cfgDesc[] =
{
    /* Configuration descriptor: */ 
    0x09,                               /* 0  bLength */ 
    0x02,                               /* 1  bDescriptorType */ 
#ifdef ASYNC
    0x8F, 0x03,                         /* 2  wTotalLength */ 
#else
    0xCE, 0x02,                         /* 2  wTotalLength */ 
#endif
    0x06,                               /* 4  bNumInterface: Number of interfaces*/ 
    0x01,                               /* 5  bConfigurationValue */ 
    0x00,                               /* 6  iConfiguration */ 
    0xC0,                               /* 7  bmAttributes */ 
    0x32,                               /* 8  bMaxPower */  

    /* Interface Association Descriptor */ 
    0x08,                               /* 0  bLength */
    0x0b,                               /* 1  bDescriptorType */
    0x00,                               /* 2  bFirstInterface */
    0x03,                               /* 3  bInterfaceCount */
    0x01,                               /* 4  bFunctionClass: AUDIO_FUNCTION */
    0x00,                               /* 5  bFunctionSubClass: FUNCTION_SUBCLASS_UNDEFINED */
    0x20,                               /* 6  bFunctionProtocol: AF_VERSION_02_00 */
    0x00,                               /* 7  iFunction (String Index) */

    /* Standard Audio Control Interface Descriptor (Note: Must be first with lowest interface number)r */
    0x09,                               /* 0  bLength: 9 */
    0x04,                               /* 1  bDescriptorType: INTERFACE */
    0x00,                               /* 2  bInterfaceNumber */
    0x00,                               /* 3  bAlternateSetting: Must be 0 */
    0x01,                               /* 4  bNumEndpoints (0 or 1 if optional interupt endpoint is present */
    AUDIO,                              /* 5  bInterfaceClass: AUDIO */
    AUDIOCONTROL,                       /* 6  bInterfaceSubClass: AUDIOCONTROL*/
    IP_VERSION_02_00,                   /* 7  bInterfaceProtocol: IP_VERSION_02_00 */
    0x04,                               /* 8  iInterface */ 

    /* Class Specific Audio Control Interface Header Descriptor: */
    0x09,                               /* 0   bLength */
    CS_INTERFACE,                       /* 1   bDescriptorType: CS_INTERFACE */
    HEADER,                             /* 2   bDescriptorSubtype: HEADER */
    0x00, 0x02,                         /* 3:4 bcdUSB */
    IO_BOX,                             /* 5   bCatagory (Primary use of audio function) */
    0xB9, 0x01,                         /* 6:7 wTotalLength */
    0x00,                               /* 8   bmControls (0:1 Latency Control, 2:7 must be 0 */ 

#ifdef ASYNC
    /* Clock Selector Descriptor (4.7.2.1) */ 
    0x0A,                               /* 0   bLength: 8 */
    CS_INTERFACE,                       /* 1   bDescriptorType */
    CLOCK_SELECTOR,                     /* 2   bDescriptorSubtype */
    0x03,                               /* 3   bClockID */
    0x3, 0x1, 0x2, 0x5,                 /* 4: number of pins, 5-7: pins */
    0x03,                               /* 5   bmControls       D[1:0] : Clock Freq Control
                                                                D[3:2] : Clock Validity Control
                                                                D[7:4] : Reserved (0) */
    0x00,                               /* 7   iClockSource (String Index) */  

    /* Clock Source Descriptor (4.7.2.1) */ 
    0x08,                               /* 0   bLength: 8 */
    CS_INTERFACE,                       /* 1   bDescriptorType */
    CLOCK_SOURCE,                       /* 2   bDescriptorSubtype */
    0x1,                                /* 3   bClockID */
    0x03,                               /* 4   bmAttributes:    D[1:0] :
                                                                    00: External Clock \ 
                                                                    01: Internal Fixed Clock
                                                                    10: Internal Variable Clock
                                                                    11: Internal Progamable Clock 
                                                                D[2]   : Clock synced to SOF
                                                                D[7:3] : Reserved (0) */

    0x03,                               /* 5   bmControls       D[1:0] : Clock Freq Control
                                                                D[3:2] : Clock Validity Control
                                                                D[7:4] : Reserved (0) */
    0x00,                               /* 6   bAssocTerminal */
    0x06,                               /* 7   iClockSource (String Index) */  

    /* Clock Source Descriptor (4.7.2.1) */
    0x08,                               /* 0   bLength: 8 */
    CS_INTERFACE,                       /* 1   bDescriptorType */
    CLOCK_SOURCE,                       /* 2   bDescriptorSubtype */
    0x2,                                /* 3   bClockID */
    0x00,                               /* 4   bmAttributes:    D[1:0] :
                                                                    00: External Clock \ 
                                                                    01: Internal Fixed Clock
                                                                    10: Internal Variable Clock
                                                                    11: Internal Progamable Clock 
                                                                D[2]   : Clock synced to SOF
                                                                D[7:3] : Reserved (0) */

    0x01,                               /* 5   bmControls       D[1:0] : Clock Freq Control
                                                                D[3:2] : Clock Validity Control
                                                                D[7:4] : Reserved (0) */
    0x00,                               /* 6   bAssocTerminal */
    0x10,                               /* 7   iClockSource (String Index) */  

    /* Clock Source Descriptor (4.7.2.1) */
    0x08,                               /* 0   bLength: 8 */
    CS_INTERFACE,                       /* 1   bDescriptorType */
    CLOCK_SOURCE,                       /* 2   bDescriptorSubtype */
    0x5,                                /* 3   bClockID */
    0x00,                               /* 4   bmAttributes:    D[1:0] :
                                                                    00: External Clock \ 
                                                                    01: Internal Fixed Clock
                                                                    10: Internal Variable Clock
                                                                    11: Internal Progamable Clock 
                                                                D[2]   : Clock synced to SOF
                                                                D[7:3] : Reserved (0) */

    0x01,                               /* 5   bmControls       D[1:0] : Clock Freq Control
                                                                D[3:2] : Clock Validity Control
                                                                D[7:4] : Reserved (0) */
    0x00,                               /* 6   bAssocTerminal */
    0x0F,                               /* 7   iClockSource (String Index) */  


#else
    /* Synch */
    /* Clock Source Descriptor (4.7.2.1) */
    0x08,                               /* 0   bLength: 8 */
    CS_INTERFACE,                       /* 1   bDescriptorType */
    CLOCK_SOURCE,                       /* 2   bDescriptorSubtype */
    0x03,                               /* 3   bClockID */
    0x06,                               /* 4   bmAttributes:    D[1:0] :
                                                                    00: External Clock \ 
                                                                    01: Internal Fixed Clock
                                                                    10: Internal Variable Clock
                                                                    11: Internal Progamable Clock 
                                                                D[2]   : Clock synced to SOF
                                                                D[7:3] : Reserved (0) */

    0x03,                               /* 5   bmControls       D[1:0] : Clock Freq Control
                                                                D[3:2] : Clock Validity Control
                                                                D[7:4] : Reserved (0) */
    0x00,                               /* 6   bAssocTerminal */
    0x06,                               /* 7   iClockSource (String Index) */  
#endif

/* Input Terminal Descriptor (USB Streaming) */
    0x11, 				                      /* 0  bLength in bytes: 17 */
    0x24, 				                      /* 1  bDescriptorType: CS_INTERFACE */
    0x02, 				                      /* 2  bDescriptorSubType: INPUT_TERMINAL */
    0x06, 				                      /* 3  bTerminalID */
    0x01, 0x01,                         /* 4  wTerminalType: USB Streaming */
    0x00, 				                      /* 6  bAssocTerminal */
    0x03, 				                      /* 7  bCSourceID: ID of Clock Entity */
    0x02,				                        /* 8  bNrChannels */
    0x03, 0x00, 0x00, 0x00,             /* 9  bmChannelConfig */
    0x0,           		                /* 13 iChannelNames */
    0x00, 0x00,     		                /* 14 bmControls */
    0x07,           		                /* 16 iTerminal */

/* Input Terminal Descriptor (Analog in) */
    0x11, 				                      /* 0  bLength in bytes: 17 */
    0x24, 				                      /* 1  bDescriptorType: CS_INTERFACE */
    0x02, 				                      /* 2  bDescriptorSubType: INPUT_TERMINAL */
    0x09, 				                      /* 3  bTerminalID */
    0x03, 0x06,                         /* 4  wTerminalType: Line Connector */
    0x00, 				                      /* 6  bAssocTerminal */
    0x03, 				                      /* 7  bCSourceID: ID of Clock Entity */
    0x02,				                        /* 8  bNrChannels */
    0x03, 0x00, 0x00, 0x00,             /* 9  bmChannelConfig */
    0x0,           		                /* 13 iChannelNames */
    0x00, 0x00,     		                /* 14 bmControls */
    0x0b,           		                /* 16 iTerminal */


/* Output Terminal Descriptor (USB Streaming) */
    0x0C, 				                      /* 0  bLength */
    0x24,                               /* 1  bDescriptorType: CS_INTERFACE */
    0x03, 				                      /* 2  bDescriptorSubType: OUTPUT_TERMINAL */
    0x08, 				                      /* 3  bTerminalID */
    0x01, 0x01,                         /* 4  wTerminalType */
    0x00,                               /* 6  bAssocTerminal */
    0x0B,                               /* 7  bSourceID Connect to analog input feature unit*/
    0x03, 				                      /* 8  bCSourceUD */
    0x00, 0x00,                         /* 9  bmControls */
    0x0A,                               /* 11 iTerminal */


/* Feature Unit Descriptor (USB Streaming IN) */
/* UNIT_ID, SOURCE_ID, */
    FEATURE_UNIT_18(0x0A, 0x06)

/* Feature Unit Descriptor (Analog IN) */
    FEATURE_UNIT_18(0x0B, 0x09)

/* Feature Unit Descriptor (USB Streaming into Mixer LRLRLRLR) */
    FEATURE_UNIT_18(0x0C, 0x06)

/* Feature Unit Descriptor (Analog IN to Mixer L) */
    FEATURE_UNIT_18(0x0D, 0x09)

/* Feature Unit Descriptor (Analog IN to Mixer R) */
    FEATURE_UNIT_18(0x20, 0x09)

/* Mixer units to down-mix 8 -> 2 */
    MIXER_UNIT_1(15, 0x0A)
    MIXER_UNIT_1(16, 0x0A)
    MIXER_UNIT_1(17, 0x0A)
    MIXER_UNIT_1(18, 0x0A)

/* Mixer Unit Descriptor (4.7.2.6)
 * Main mixer, 2 input clusters (8 chans), 2 chans output
 * n (input channels)         = 16
 * m (output channels)        = 2
 * p (number of input pins)   = 2
 * N (number of bytes in array) = (16 * 2) / 8  = 4 */
    0x16,                               /* 0  bLength: 13 + p + N  */ 
    CS_INTERFACE,                       /* 1  bDescriptorType */ 
    MIXER_UNIT,                         /* 2  bDesctiptorSubtype */ 
    14,                                 /* 3  bUnitID */ 
    0x03,                               /* 4  bNrInPins */ 
    12,                                 /* 5  baSourceID[0] */ 
    13,                                 /* 6  baSourceID[1] */ 
    32,                                 /* 6  baSourceID[2] */ 
    0x02,                               /* 6  bNrChannels (Num channels in output cluster) */ 
    0x03,0x00,0x00,0x00,                /* 7  bmChannelConfig */ 
    0x00,                               /* 11 iChannelNames */ 
    0x99,0x99,0xaa,0xaa,0x55, 0x55,     /* 12 bmMixerControls */
    0x00,                               /* 14 bmControls */ 
    0x00,                               /* 15 iMixer */ 

    SELECTOR_UNIT_2(19, 15, 14)
    SELECTOR_UNIT_2(20, 16, 14)
    SELECTOR_UNIT_2(21, 17, 14)
    SELECTOR_UNIT_2(22, 18, 14)
    
    OUTPUT_TERM(23,19,3,8)
    OUTPUT_TERM(24,20,3,0xC)
    OUTPUT_TERM(25,21,3,0xD)
    OUTPUT_TERM(26,22,3,0xE)
    


/* Standard AS Interrupt Endpoint Descriptor (4.8.2.1): */
    0x07, 				                            /* 0  bLength: 7 */
    0x05,                                     /* 1  bDescriptorType: ENDPOINT */
    0x83,                                     /* 2  bEndpointAddress (D7: 0:out, 1:in) */
    0x03,                                     /* 3  bmAttributes (bitmap): INTERRUPT */ 
    0x06, 0x00,                               /* 4  wMaxPacketSize */
    0x08,                                     /* 6  bInterval */

     

/* Standard AS Interface Descriptor (Streaming IN, alternate) */
    0x09,                                     /* 0  bLength: (in bytes, 9) */
    0x04,                                     /* 1  bDescriptorType: INTERFACE */
    0x01,                                     /* 2  bInterfaceNumber: Number of interface */
    0x00,                                     /* 3  bAlternateSetting */
    0x00,                                     /* 4  bNumEndpoints */
    0x01,                                     /* 5  bInterfaceClass: AUDIO */
AUDIOSTREAMING,                               /* 6  bInterfaceSubClass: AUDIO_STREAMING */
IP_VERSION_02_00,                             /* 7  bInterfaceProtocol: IP_VERSION_02_00 */
    0x03,                                     /* 8  iInterface: (Sting index) */

/* Standard AS Interface Descriptor (Streaming IN): */
    0x09,                               /* 0  bLength: (in bytes, 9) */
    0x04,                               /* 1  bDescriptorType: INTERFACE */
    0x01,                               /* 2  bInterfaceNumber */
    0x01,                               /* 3  bAlternateSetting */
#ifdef ASYNC
    0x02,                               /* 4  bNumEndpoints */
#else
    0x01,                               /* 4  bNumEndpoints */
#endif
    0x01,                               /* 5  bInterfaceClass: AUDIO */
    AUDIOSTREAMING,                     /* 6  bInterfaceSubClas */
    IP_VERSION_02_00,                   /* 7  bInterfaceProtocol */
    0x03,                               /* 8  iInterface (String Index) */

/* Class Specific AS Interface Descriptor (Streaming IN) */
    0x10,                               /* 0  bLength: 16 */
    CS_INTERFACE,                       /* 1  bDescriptorType: CS_INTERFACE */
    AS_GENERAL,                         /* 2  bDescriptorSubType */
    0x06,                               /* 3  bTerminalLink */
    0x00,                               /* 4  bmControls */
    FORMAT_TYPE_I,                      /* 5  bFormatType */
    PCM, 0x00, 0x00, 0x00,              /* 6:10  bmFormats (note this is a bitmap) */
    0x02,                               /* 11 bNrChannels */
    0x03, 0x00, 0x00, 0x00,             /* 12:14: bmChannelConfig */
    0x0,                               /* 15 iChannelNames */

/* Type 1 Format Type Descriptor */
    0x06,                               /* 0  bLength (in bytes): 6 */
    CS_INTERFACE,                       /* 1  bDescriptorType: CS_INTERFACE */
    FORMAT_TYPE,                        /* 2  bDescriptorSubtype: FORMAT_TYPE */
    FORMAT_TYPE_I,                      /* 3  bFormatType: FORMAT_TYPE_1 */
    0x04,                               /* 4  bSubslotSize (Number of bytes per subslot) */
    0x18,                               /* 5  bBitResolution (Number of bits used per subslot) */ 

/* Standard AS Isochronous Audio Data Endpoint Descriptor (4.10.1.1): */
    0x07, 				                      /* 0  bLength: 7 */
    0x05, 				                      /* 1  bDescriptorType: ENDPOINT */
    0x01,                               /* 2  bEndpointAddress (D7: 0:out, 1:in) */
#ifdef ASYNC
    0x05,                               /* 3  bmAttributes (bitmap) */ 
#else 
    0b00001101,                         /* 3  bmAttributes (bitmap) */ 
#endif
    0x00, 0x04,                         /* 4  wMaxPacketSize */
    0x01,                               /* 6  bInterval */

/* Class-Specific AS Isochronous Audio Data Endpoint Descriptor: */
    0x08,                               /* 0   bLength */
    CS_ENDPOINT,                        /* 1   bDescriptorType */
    EP_GENERAL,                         /* 2   bDescriptorSubtype */
    0x00,                               /* 3   bmAttributes */
    0x00,                               /* 4   bmControls (Bitmap: Pitch control, over/underun etc) */
    0x02,                               /* 5   bLockDelayUnits: Decoded PCM samples */
    0x08, 0x00,                         /* 6:7 bLockDelay */

#ifdef ASYNC
/* AS Ischronous Feedback Endpoint Descriptor (4.10.2) */
/* AS Isochronous Feedback Descriptor (4.10.2.1)*/
    0x07,                               /* 0  bLength */
    0x05,                               /* 1  bDescriptorLength */
    0x81,                               /* 2  bEndpointAddress */
    0x11,                               /* */
    0x04, 0x00,                         /*  4 wMaxPacketSize */
    0x08,                               /*  bInterval */
#endif

/* Device source start */
/* Standard AS Interface Descriptor (Streaming OUT of the device, alternate) */
    0x09,                               /* 0  bLength: (in bytes, 9) */
    0x04,                               /* 1  bDescriptorType: INTERFACE */
    0x02,                               /* 2  bInterfaceNumber: Number of interface */
    0x00,                               /* 3  bAlternateSetting */
    0x00,                               /* 4  bNumEndpoints */
    0x01,                               /* 5  bInterfaceClass: AUDIO */
    AUDIOSTREAMING,                     /* 6  bInterfaceSubClass: AUDIO_STREAMING */
    IP_VERSION_02_00,                   /* 7  bInterfaceProtocol: IP_VERSION_02_00 */
    0x09,                               /* 8  iInterface: (Sting index) */

/* Standard AS Interface Descriptor (Streaming OUT of the device): */
    0x09,                               /* 0  bLength: (in bytes, 9) */
    0x04,                               /* 1  bDescriptorType: INTERFACE */
    0x02,                               /* 2  bInterfaceNumber */
    0x01,                               /* 3  bAlternateSetting */
    0x01,                               /* 4  bNumEndpoints */
    0x01,                               /* 5  bInterfaceClass: AUDIO */
    AUDIOSTREAMING,                     /* 6  bInterfaceSubClass */
    IP_VERSION_02_00,                   /* 7  bInterfaceProtocol */
    0x09,                               /* 8  iInterface (String Index) */

/* Class Specific AS Interface Descriptor (Streaming OUT of the device) */
    0x10,                               /* 0  bLength: 16 */
    CS_INTERFACE,                       /* 1  bDescriptorType: CS_INTERFACE */
    AS_GENERAL,                         /* 2  bDescriptorSubType */
    0x08,                               /* 3  bTerminalLink */
    0x00,                               /* 4  bmControls */
    FORMAT_TYPE_I,                      /* 5  bFormatType */
    PCM, 0x00, 0x00, 0x00,              /* 6:10  bmFormats (note this is a bitmap) */
    0x02,                               /* 11 bNrChannels */
    0x03, 0x00, 0x00, 0x00,             /* 12:14: bmChannelConfig */
    0x11,                               /* 15 iChannelNames */

/* Type 1 Format Type Descriptor */
    0x06,                               /* 0  bLength (in bytes): 6 */
CS_INTERFACE,                           /* 1  bDescriptorType: CS_INTERFACE */
FORMAT_TYPE,                            /* 2  bDescriptorSubtype: FORMAT_TYPE */
FORMAT_TYPE_I,                          /* 3  bFormatType: FORMAT_TYPE_1 */
    0x04,                               /* 4  bSubslotSize (Number of bytes per subslot) */
    0x18,                               /* 5  bBitResolution (Number of bits used per subslot) */ 

/* Standard AS Isochronous Audio Data Endpoint Descriptor: */
    0x07, 				                      /* 0  bLength: 7 */
    0x05, 				                      /* 1  bDescriptorType: ENDPOINT */
    0x82,                               /* 2  bEndpointAddress (D7: 0:out, 1:in) */ 
#ifdef ASYNC
0b00000101,                             /* 3  bmAttributes (bitmap) */ 
#else 
0b00001101,                             /* 3  bmAttributes (bitmap) */ 
#endif
    0x00, 0x04,                         /* 4  wMaxPacketSize */
    0x01,                               /* 6  bInterval */

/* Class-Specific AS Isochronous Audio Data Endpoint Descriptor: */
    0x08,                               /* 0   bLength */
CS_ENDPOINT,                            /* 1   bDescriptorType */
EP_GENERAL,                             /* 2   bDescriptorSubtype */
    0x00,                               /* 3   bmAttributes */
    0x00,                               /* 4   bmControls (Bitmap: Pitch control, over/underun etc) */
    0x02,                               /* 5   bLockDelayUnits: Decoded PCM samples */
    0x08, 0x00,                         /* 6:7 bLockDelay */




#if 0
/* HID: */
/* Interface Descriptor */
    0x09, 0x04, 0x03, 0x00, 0x01, 0x03, 0x00, 0x00, 0x00,
    0x09, 0x21, 0x00, 0x01, 0x00, 0x01, 0x22, 0x32, 0x00,

/* Endpoint Descriptor */
    0x07,                               /* 0  bLength */
    0x05, 0x83, 0x03, 0x04, 0x00, 0x08,
#endif

};

#endif

#define DESC_STR_LANGIDS \
{ \
  XUD_DESC_STR_USENG & 0xff,               /* 2  wLangID[0] */ \
  XUD_DESC_STR_USENG>>8,            /* 3  wLangID[0] */ \
  '\0' \
}

#define STRINGER(x) "Xtreme "#x
/* Standard descriptors */
static unsigned char devDesc[] = DESC_DEVICE;
//static unsigned char cfgDesc[] = DESC_CONFIG;
static unsigned char devQualDesc[] = DESC_DEVQUAL;

/* String descriptors */
static unsigned char strDesc_langIDs[] = DESC_STR_LANGIDS;

/* Strings table for string desctiptors. Note: for usabilty unicode and datalengh are dealt with automatically */
/* therefore just the strings are required below. string[0] (langIDs) is a speacial one... */
static unsigned char strDescs[][40] = 
{
    "string0", 				                    /* 0: Place holder for string 0 (LANGIDs) */ 
    STRINGER(),				                    /* 1: iManufacturer */ 
    STRINGER( USB Audio),			            /* 2: iProduct */
    STRINGER( USB 2.0 Audio Stream OUT),  /* 3: */  
    STRINGER( USB 2.0 Audio Control), 	  /* 4: */ 
    STRINGER( Feature Unit), 			        /* 5: */ 
    "Internal Clock",	                    /* 6: */
    "Input term",				                  /* 7: */
    "Outputs 1 & 2", 			                /* 8: */
    STRINGER(USB 2.0 Audio Stream IN),    /* 9: */
    "Output term (usb)",                  /* A: */
    "Input term (ana)",                   /* B: */
    "Outputs 3 & 4", 			                /* C: */
    "Outputs 5 & 6", 			                /* D: */
    "Outputs 7 & 8", 			                /* E: */
    "S/PDIF Clock", 			              /* F: */
    "ADAT Clock", 			                  /* 0x10: */
    "S/PDIF Left", 			                  /* 0x11: */
    "S/PDIF Right", 			                /* 0x12: */
}; 


/* HID Report Descriptor */
static unsigned char hidReportDesc[] =
{
0x05 , 0x0C , 0x09 , 0x01 , 0xA1 , 0x01 , 0x15 , 0x00 ,  0x25 , 0x01 , 0x09 , 0xE9 , 0x09 , 0xEA , 0x75 , 0x01 , 
0x95 , 0x02 , 0x81 , 0x02 , 0x09 , 0xE2 , 0x09 , 0x00 ,  0x81 , 0x06 , 0x09 , 0x00 , 0x95 , 0x04 , 0x81 , 0x02 , 
0x26 , 0xFF , 0x00 , 0x09 , 0x00 , 0x75 , 0x08 , 0x95 ,  0x03 , 0x81 , 0x02 , 0x09 , 0x00 , 0x95 , 0x04 , 0x91 , 
0x02 , 0xC0
};



void storeInt(unsigned char buffer[], int index, int val)
{
  buffer[index+3] = val>>24;
  buffer[index+2] = val>>16;
  buffer[index+1] = val>>8;
  buffer[index]  =  val;
}

void storeShort(unsigned char buffer[], int index, short val)
{
    buffer[index+1] = val>>8;
    buffer[index]  =  val;
}

void mixerEnable(int x, int y) {}


int clockQuery(chanend queryClockValidity, int query) {
    int i_tmp;
    queryClockValidity <: query;
    queryClockValidity :> i_tmp;
    return i_tmp;
}


void Endpoint0( chanend c_ep0_out, chanend c_ep0_in)
{
    int datalength;
    unsigned char buffer[1024];
    SetupPacket sp;
    int loop = 1;
    int i_tmp;
    int unitID;

    /* Copy langIDs string desc into string[0] */
    /* TODO: Macro? */
    safememcpy(strDescs[0], strDesc_langIDs, sizeof(strDesc_langIDs));
 
    /* Compute initial channel volume factors */ 
    //updateMasterVol(0xa);
    //updateMasterVol(0xb);
    
    while(1)
    {
        /* Do standard enumeration requests */ 
        int retVal = XUD_DoEnumReqs(c_ep0_out, c_ep0_in, devDesc, sizeof(devDesc), cfgDesc, sizeof(cfgDesc), devQualDesc, sizeof(devQualDesc), strDescs, sp);
        if (retVal == -1) 
        {
	          continue;
        }
        if (retVal == 0)
        {
            /* Request not covered by XUD_DoEnumReqs() so decode ourselves */
            switch(sp.bmRequestType.Type)
            {
                case BM_REQTYPE_TYPE_STANDARD:
                { 
                    switch(sp.bmRequestType.Recipient)
                    {
                        case BM_REQTYPE_RECIP_INTER:
             
                            switch(sp.bRequest)
                            {
                                /* Set Interface */
                                case B_REQ_SETINTER:

                                    /* TODO: Set the interface */

                                    /* No data stage for this request, just do data stage */
                                    XUD_DoSetRequestStatus(c_ep0_in, 0);
                                    break;
            
                                /* Get descriptor */ 
                                case B_REQ_GETDESC:
             
                                    /* Inspect which descriptor require (high byte of wValue) */ 
                                    switch( sp.wValue & 0xff00 )
                                    {
                                        /* HID Report Descriptor */
                                        case  WVALUE_GETDESC_HID_REPORT:
                  
                                            /* Go Get Request protocol */
                                            XUD_DoGetRequest_( c_ep0_out, c_ep0_in, 0, hidReportDesc, sizeof( hidReportDesc ));
                                            break;
                  
                                            default:
                                                XUD_Error( "Unknown descriptor request\n" );
                                                break;
                                    }
                                    break;
                            }
                            break;
            
                        /* Recipient: Device */
                        case BM_REQTYPE_RECIP_DEV:
                  
                            /* Standard Device requests (8) */
                            switch( sp.bRequest )
                            {      
                                /* TODO Check direction */
                                /* Standard request: SetConfiguration */
                                case B_REQ_SETCONFIG:
                
                                    /* TODO: Set the config */

                                    /* No data stage for this request, just do status stage */
                                    XUD_DoSetRequestStatus(c_ep0_in,  0);
                                    break;

                                case B_REQ_GETCONFIG:
                                    buffer[0] = 1;
                                    XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, 1);
                                    break; 
                
                                default:
                                    XUD_Error("Unknown device request");
                                    break;
          
                            }  
                            break;
          
                        default: 
                            /* Got a request to a recipient we didn't recognise... */ 
                            XUD_Error("Unknown Recipient"); 
                            break;
                    }
                    break;
                }
       
                /* Request Type: Class */ 
                case BM_REQTYPE_TYPE_CLASS:
                { 
                    switch(sp.bmRequestType.Recipient)
                    {
                        /* Recipient: Interface */
                        case BM_REQTYPE_RECIP_INTER:
                        {           
                            /* Inspect request, NOTE: these are class spefic requests */
                            switch(sp.bRequest )
                            {
                               
                               
                                
#ifdef AUDIO2
                                /* Cur */
                                case 0x01: 
                                {

                                    unitID = sp.wIndex >> 8;
                                    switch( unitID )
                                    {
                                        /* TODO look up unit IDs */
                                        /* Selector units */
                                        case 19:
                                        case 20:
                                        case 21:
                                        case 22:
                                        {

                                            // Expect OUT here (with input select)
                                            loop = XUD_GetBuffer(c_ep0_out, buffer);
                                            mixerEnable(unitID-19, buffer[0]); 
                                            // Send 0 Length as status stage
                                            XUD_DoSetRequestStatus(c_ep0_in, 0);
                                            break;
                                        }




                                        /* Clock Selector Unit */
#ifdef ASYNC
                                        case 0x3:  
                                        {
                                            if ((sp.wValue >> 8) == 1) 
                                            {
                                                if( sp.bmRequestType.Direction == 0 ) 
                                                { 
                                                     /* Direction: Host-to-device */
                                                    datalength = XUD_GetBuffer(c_ep0_out, buffer);
                                                    if(datalength != 1) 
                                                    {
                                                        XUD_Error( "Bad datalength for sample rate ");
                                                    }
                                                    
                                                    i_tmp = buffer[0]; 

                                                    XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);

                                                    /* Send command to update clock selection */
                                                    //c_clockControl <: 0;
                                                    //c_clockControl <: i_tmp;
                                                } 
                                                else 
                                                {  
                                                    /* Direction: Device-to-host: Send Current Sample Freq */
                                                    //buffer[0] = clockQuery(queryClockValidity, CLOCK_MODE_SELECTION);          /* Send back current selector setting */
                                                    buffer[0] = 1;
                                                    XUD_DoGetRequest_( c_ep0_out, c_ep0_in, 0, buffer, sp.wLength );
                                                }
                                            } 
                                            else 
                                            {
                                                XUD_Error_hex("Unknown control on clock selector", sp.wValue);
                                            }
                                            break;
                                        }
#endif

                                        /* Clock Unit */
#ifdef ASYNC
                                        case 0x1:  
                                        case 0x2:  
                                        case 0x5:  
#else
                                        case 0x3:  
#endif
                                        {
                                            switch( sp.wValue >> 8 )
                                            {
                                                /* Sample Frequency control */
                                                case 1: 
                                                {
                                                    /* Direction: Host-to-device */
                                                    if( sp.bmRequestType.Direction == 0 ) 
                                                    {
                                                        /* Get OUT data with Sample Rate into buffer*/
                                                        datalength = XUD_GetBuffer(c_ep0_out, buffer);
                                                        
                                                        if(datalength != 4)
                                                        {
                                                            XUD_Error( "Bad datalength for sample rate ");
                                                        }
                                                        
                                                        /* Re-construct Sample Freq */
                                                        i_tmp = buffer[0] | (buffer[1] << 8) | buffer[2] << 16 | buffer[3] << 24; 

                                                        /* Instruct clock thread to change sample freq */
                                                       
                                                        //c_clockControl <: 1;
                                                        //c_clockControl <: i_tmp;

                                                        //printstr("sent samp\n");
                                                        //c_aud_ctl <: i_tmp;

//                                                        SetParam(PARAM_CUR_FREQ, i_tmp);
/*                                                        switch(i_tmp) {
                                                        case 44100: outuchar(c_audioControl, 0); break;
                                                        case 48000: outuchar(c_audioControl, 1); break;
                                                        case 96000: outuchar(c_audioControl, 2); break;
                                                        case 192000: outuchar(c_audioControl, 4); break;
                                                        default: printstrln("HELP");
                                                            printintln(i_tmp); break;
                                                        }
                                                        outct(c_audioControl, 1);
*/

                                                        XUD_Sup_Delay(1000000);
                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    /* Direction: Device-to-host: Send Current Sample Freq */
                                                    else
                                                    {
                                                        /* Get current sample freq from audio thread */
                                                        //c_aud_ctl <: CMD_GET_SAMFREQ;
                                                        //c_aud_ctl :> i_tmp;

                                                        switch(unitID) {
                                                        case 1:
                                                            //(buffer, unsigned[])[0] = clockQuery(queryClockValidity, CLOCK_FREQ_LOCAL);
                                                            (buffer, unsigned[])[0] = 1;
                                                            break;
                                                        case 2:
                                                            //(buffer, unsigned[])[0] = clockQuery(queryClockValidity, CLOCK_FREQ_EXTERNAL_ADAT);
                                                            (buffer, unsigned[])[0] = 1;
                                                            break;
#ifndef ASYNC
                                                        case 3: (buffer, unsigned[])[0] = 48000; break;                       // SYNC only
#endif
                                                        case 5:
                                                            //(buffer, unsigned[])[0] = clockQuery(queryClockValidity, CLOCK_FREQ_EXTERNAL_SPDIF);
                                                            (buffer, unsigned[])[0] = 1;
                                                            break;
                                                        default:
                                                            XUD_Error_hex("Unknown freq query", unitID); break;
                                                        }
                                                        XUD_DoGetRequest_( c_ep0_out, c_ep0_in, 0, buffer, sp.wLength );
                                                    }
                                                    break;
                                                }

                                                /* Clock valid control */
                                                case 2: 
                                                {
                                                    switch(unitID) {
                                                    case 1:
                                                        //buffer[0] = clockQuery(queryClockValidity, CLOCK_MODE_LOCAL);
                                                        
                                                        buffer[0] = 1;
                                                        break;
                                                    case 2:
                                                    // buffer[0] = clockQuery(queryClockValidity, CLOCK_MODE_EXTERNAL_ADAT);

                                                        buffer[0] = 1;
                                                        break;
#ifndef ASYNC
                                                    case 3: buffer[0] = 1; break;                       // SYNC only
#endif
                                                    case 5:
                                                        //buffer[0] = clockQuery(queryClockValidity, CLOCK_MODE_EXTERNAL_SPDIF);
                                                        
                                                        buffer[0] = 1;
                                                        break;
                                                    default:
                                                        XUD_Error_hex("Unknown control validity", unitID); break;
                                                    }
                                                    XUD_SetBuffer_ResetPid_(c_ep0_in, 0, buffer, sp.wLength, PIDn_DATA1);
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer(c_ep0_out, buffer);
                                                    break;
                                                }

                                                default:
                                                    XUD_Error("Unknown control selector");
                                                    break;
                                                
                                            }
                                            break;
                                        }
                 
                                        // Feature unit ID (usb intput) TODO: Look this up
                                        case 12: /* This is USB mix */

                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer(c_ep0_out, buffer);
                                                        mixer_params_USB[sp.wValue & 0xff] = buffer[0] | (buffer[1] << 8);
                                                        updateVol( unitID, sp.wValue & 0xff );

                                                        // Send 0 Length as status stage
                                                        XUD_DoSetRequestStatus(c_ep0_in, 0);
                                                   }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = mixer_params_USB[sp.wValue&0xff];
                                                        buffer[1] = (mixer_params_USB[sp.wValue&0xff] >> 8);
                                                        XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, sp.wLength); 
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer(c_ep0_out, buffer);
                                                        mixer_params_USB[(sp.wValue&0xff)+9] = buffer[0];
                                                        updateVol( unitID, sp.wValue & 0xff );

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = mixer_params_USB[(sp.wValue & 0xff)+9];
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                default:
                                                
                                                    XUD_Error(" Unknown control selector");
                                                    break;
                                            }


                                            break;

                                        case 13:
                                        { 
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        setVolumeControl( sp.wValue & 0xff, buffer[0] | buffer[1] << 8);

                                                        // Send 0 Length as status stage
                                                        XUD_DoSetRequestStatus(c_ep0_in, 0);
                                                   }
                                                    else // Direction: Device-to-host
                                                    {
                                                        int x = getVolumeControl(sp.wValue & 0xff);
                                                        buffer[0] = x;
                                                        buffer[1] = x >> 8;
                                                        XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, sp.wLength); 
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        setMuteControl( sp.wValue & 0xff, buffer[0]);

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = getMuteControl(sp.wValue & 0xff);
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                default:
                                                
                                                    XUD_Error(" Unknown control selector");
                                                    break;
                                            }
                                            break; 

                                            
                                        }
                                        case 32: /* left mix */
                                        { 
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        mixer_params_anaR[(sp.wValue&0xff)] = buffer[0] | (buffer[1] << 8);
                                                        updateVol( unitID, sp.wValue & 0xff );

                                                        // Send 0 Length as status stage
                                                        XUD_DoSetRequestStatus(c_ep0_in, 0);
                                                   }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = mixer_params_anaR[(sp.wValue&0xff)];
                                                        buffer[1] = (mixer_params_anaR[(sp.wValue&0xff)] >> 8);
                                                        XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, sp.wLength); 
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        mixer_params_anaR[(sp.wValue&0xff)+9] = buffer[0];
                                                        updateVol( unitID, sp.wValue & 0xff );

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = mixer_params_anaR[(sp.wValue & 0xff)+9];
                                                        XUD_SetBuffer_ResetPid_(c_ep0_out,  0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                default:
                                                
                                                    XUD_Error(" Unknown control selector");
                                                    break;
                                            }
                                            break; 

                                            
                                        } 
                                         
                                        case 0xa: 
                                        {
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        setVolumeControl( sp.wValue & 0xff, buffer[0] | buffer[1] << 8);

                                                        // Send 0 Length as status stage
                                                        //XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                                                        XUD_DoSetRequestStatus(c_ep0_in, 0);
                                                   }
                                                    else // Direction: Device-to-host
                                                    {

                                                        int x = getVolumeControl(sp.wValue & 0xff);
                                                        buffer[0] = x;
                                                        buffer[1] = x >> 8;
                                                        XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, sp.wLength); 

                                                        //XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        //loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        setMuteControl( sp.wValue & 0xff, buffer[0]);

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = getMuteControl(sp.wValue & 0xff);
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, sp.wLength, PIDn_DATA1);
                                                    
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                default:
                                                
                                                    XUD_Error(" Unknown control selector");
                                                    break;
                     
                                             }
                                            break;
                                        }
                                        
                                        // Feature unit ID (Analog intput) TODO: Look this up 
                                        case 0xb: 
                                        {
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        analogue_in_volumes[ sp.wValue&0xff ] = buffer[0] | (buffer[1] << 8);
                                                        updateVol( unitID, ( sp.wValue & 0xff ) );

                                                        // Send 0 Length as status stage
                                                        //XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                                                        XUD_DoSetRequestStatus(c_ep0_in, 0);
                                                   }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = analogue_in_volumes[ sp.wValue&0xff ];
                                                        buffer[1] = analogue_in_volumes[ sp.wValue&0xff ] >> 8;
                                                        XUD_DoGetRequest_(c_ep0_out, c_ep0_in, 0, buffer, sp.wLength); 

                                                        //XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        //loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                        analogue_in_mutes[ sp.wValue&0xff ] = buffer[0];
                                                        updateVol( unitID, ( sp.wValue & 0xff ) );

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = analogue_in_mutes[ sp.wValue&0xff ];
                                                        XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                default:
                                                
                                                    XUD_Error(" Unknown control selector");
                                                    break;
                     
                                             }
                                            break;
                                        }




                                        default:
                                         
                                            // We dont have a unit with this ID!
                                            XUD_Error_hex("ERR: Unknown control unit: ", sp.wIndex);
                                            break;
                                          
                                    }  // switch(sp.wIndex >> 8)              
                                    break;
                                }
                                
                                /* Range */ 
                                case 0x02: // Range
                                {
                                    unitID = sp.wIndex >> 8;
                                    switch( unitID )
                                    {
                                        // TODO look up unit IDs
#ifdef ASYNC
                                        case 0x1:  // Clock unit
                                        case 0x2:  // Clock unit
                                        case 0x5:  // Clock unit
#else
                                        case 0x3:  // Clock unit
#endif                                        
                                            switch(sp.wValue >> 8)
                                            {
                                                case 1: // Sam Freq Control
                                        
                                                    switch(unitID) {
                                                    case 1:
                                                        storeShort(buffer, 0, 4);
                                                        storeInt(buffer, 2, 44100);
                                                        storeInt(buffer, 6, 44100);
                                                        storeInt(buffer, 10, 0);
                                                        storeInt(buffer, 14, 48000);
                                                        storeInt(buffer, 18, 48000);
                                                        storeInt(buffer, 22, 0);
                                                        storeInt(buffer, 26, 48000);
                                                        storeInt(buffer, 30, 48000);
                                                        storeInt(buffer, 34, 0);
                                                        storeInt(buffer, 38, 48000);
                                                        storeInt(buffer, 42, 48000);
                                                        storeInt(buffer, 46, 0);
                                                        break;
                                                    case 2:
                                                        //i_tmp = clockQuery(queryClockValidity, CLOCK_FREQ_EXTERNAL_ADAT);
                                                        i_tmp = 1;
                                                        storeShort(buffer, 0, 1);
                                                        storeInt(buffer, 2, i_tmp);
                                                        storeInt(buffer, 6, i_tmp);
                                                        storeInt(buffer, 10, 0);
                                                        break;
                                                    case 5:
                                                        i_tmp = 1;
                                                        //i_tmp = clockQuery(queryClockValidity, CLOCK_FREQ_EXTERNAL_SPDIF);
                                                        storeShort(buffer, 0, 1);
                                                        storeInt(buffer, 2, i_tmp);
                                                        storeInt(buffer, 6, i_tmp);
                                                        storeInt(buffer, 10, 0);
                                                        break;
                                                    }
                       
                                                    XUD_SetBuffer_ResetPid_(c_ep0_in, 0, buffer, sp.wLength, PIDn_DATA1);
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);

                                                    break;
                                                
                     
                                                default:
                                                    XUD_Error_hex("Unknown control selector: ", sp.wValue);
                                                    break;
                                            }
                                            break;

                                        case 0x0B:    // Feature Unit ID   (Analog in) 
                                        case 0x0A:    // Feature Unit ID   (USB in)
                                        case 0x0C:    // Feature Unit ID's 12 & 13 (L/R mix for analog inputs)
                                        case 0x0D:     

                                            switch(sp.wValue >> 8)
                                            {
                                                /* Volume control, send back range */
                                                case 2: 
                                                
                                                    storeShort(buffer, 0, 1);
                                                    storeShort(buffer, 2, 0x8001);  /* MIN -127.9961 db. Note 0x8000 represents silence (- infinity).
                                                                                     * This must always be implemented but NEVER reported as MIN.
                                                                                     */
                                                    storeShort(buffer, 4, 0);       /* MAX */
                                                    storeShort(buffer, 6, 0x100);   /* RES */

                                                    XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, sp.wLength, PIDn_DATA1);
                                                    
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer_(c_ep0_out, 0, buffer);

                                                     break;
                                                 
                                                default:
                                                    printstrln("unknown cont rol selector: ");
                                                    printhexln(sp.wValue);
                                                    break;

                                            }
                                            break;



                                        default:
                                            printstrln("unknown unit in range: ");
                                            printhexln(sp.wIndex);
                                            break;
                   
                                    }               
  
                                break;
                            }

     
                            case 0x0A: // SET IDLE
                            {
                                // TODO: Report ID and Duration from wValue
                                // TODO: Interface number from wIndex           
                                loop = XUD_SetBuffer_ResetPid_(c_ep0_in,  0, buffer, 0, PIDn_DATA1);

                                break;
                            }  
#else /* Audio 1.0 - Skeleton */       
                            case B_REQ_SETINTER: 
                            {
                                // TODO Actually set interface!
                                XUD_SetBuffer_ResetPid_(c_ep0, 0, buffer, 0, PIDn_DATA1);

                                break;
                            }
              
                            case B_REQ_SET_CUR:
                            {
                                // TODO Actually set_cur and use the volume!
                                // Expect OUT here
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                                XUD_SetBuffer_ResetPid_(c_ep0, 0, buffer, 0, PIDn_DATA1);

                                break;
                            }
                            case B_REQ_GET_CUR:
                            {
                                // TODO Actually get cur!!
                                buffer[0] = 0;
                                buffer[1] = 0x0;
                                XUD_SetBuffer_ResetPid_(c_ep0,  0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer_(c_ep0,  0, buffer);
                                 break;
                            }
                            case B_REQ_GET_MIN:
                            {
                                // TODO Actually get_cur!
                                buffer[0] = 0;
                                buffer[1] = 0x80;
                                XUD_SetBuffer_ResetPid_(c_ep0, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer_(c_ep0, 0, buffer);
                                break;
                            }
                            case B_REQ_GET_MAX:
                            {
                                // TODO Actually GET MAX!
                                buffer[0] = 0;
                                buffer[1] = 0x2;
                                XUD_SetBuffer_ResetPid_(c_ep0, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer_(c_ep0, 0, buffer);
                                break;
                            }
                            case B_REQ_GET_RES:
                            {
                                // TODO Actually GET RES!
                                buffer[0] = 0;
                                buffer[1] = 0x1;
                                XUD_SetBuffer_ResetPid_(c_ep0, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer_(c_ep0, 0, buffer);
                                break;
                            }
#endif
                            default:
                                printstr("Err bad req");
                                printhexln(sp.bRequest);
                                break;
                        }
                        break;
                    }
                    default:
                        printstrln("Err...");
                        XUD_PrintSetupPacket(sp);
                        break;
                }
                break;
            }

            default:
            {
                printstr("ERR: Unknown request: ");
                XUD_PrintSetupPacket(sp);

                break;
            }
    
        }

        } /* if XUD_DoEnumReqs() */
  }

  // END control tokens.
  //inct(c_ep0);
  //inct(c_ep0_con);
  
  //endpoint1dead();
 //stop_streaming_slave(c_ep0);
 //stop_streaming_slave(c_ep0_con);

  //printstrln("ep thread exit");
}
