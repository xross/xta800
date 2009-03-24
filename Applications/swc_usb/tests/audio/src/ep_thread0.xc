

#include <xs1.h>
#include <print.h>
#include <safestring.h>
#include "xud.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_UIFM_Functions.h"
#include "XUD_Defines_Internal.h"
#include "audio.h"

//#define AUDIO_1V0 1 // Note: Enumeration will complete with AUDIO v1.0 descriptors, however, the rest of the code is not currently guarenteed to stream audio in a correct manner.

#define AUDIO2 1

unsigned params[] = {0x2000, 0x2000, 0x2000, 0x2000, 0x2000, 0x2000, 0x2000, 0x2000, 0x2000,  /* Channel volumes */
                     96000,      							      /* Sample rate */
                     0, 0, 0, 0, 0, 0, 0, 0, 0,  				/* Mute */
                     0, 0, 0, 0, 0, 0, 0, 0, 0};            /* Premultiplcation of channel volumes */

void updateMasterVol()
{
  int tmp = params[0] * !params[10]; // Master volume and mute

  for (int i = 1; i < 9; i++)
  {
     params[18+i] = ((tmp * params[i] * !params[10+i]) >> 8);
  }
}

void updateVol(int channel)
{
  if (channel == 0)
    updateMasterVol();
  else
  // MVol      MMute,          channel vol
  params[18+channel] = ((params[0] * !params[10] * params[channel] * !params[10+channel])>> 8);
}

#ifdef AUDIO_1V0

// This devices Device Descriptor:
#define DESC_DEVICE \
{ \
  18, 				     /* 0  bLength : Size of descriptor in Bytes (18 Bytes) */ \
  XUD_DESC_DEV_B_DESCTYPE_DEVICE,    /* 1  bdescriptorType */ \
  XUD_DESC_DEV_BCD_USB_20_0,         /* 2  bcdUSB */ \
  XUD_DESC_DEV_BCD_USB_20_1,         /* 3  bcdUSB */ \
  XUD_DESC_DEV_B_DEVCLASS_INTER,     /* 4  bDeviceClass */ \
  0,                                 /* 5  bDeviceSubClass */ \
  0,                                 /* 6  bDeviceProtocol */ \
  64,                                /* 7  bMaxPacketSize */ \
  0x52,                              /* 8  idVendor */ \
  0x18,                              /* 9  idVendor */ \
  0x30,                              /* 10 idProduct */ \
  0x70,                              /* 11 idProduct */ \
  0x10,                              /* 12 bcdDevice : Device release number */ \
  0x00,                              /* 13 bcdDevice : Device release number */ \
  0x01,                              /* 14 iManufacturer : Index of manufacturer string */ \
  0x02,                              /* 15 iProduct : Index of product string descriptor */ \
  0x00,                              /* 16 iSerialNumber : Index of serial number decriptor */ \
  0x01                               /* 17 bNumConfigurations : Number of possible configs */ \
}

#else

// This devices Device Descriptor:
#define DESC_DEVICE \
{ \
  18, 				     /* 0  bLength : Size of descriptor in Bytes (18 Bytes) */ \
  XUD_DESC_DEV_B_DESCTYPE_DEVICE,    /* 1  bdescriptorType */ \
  XUD_DESC_DEV_BCD_USB_20_0,         /* 2  bcdUSB */ \
  XUD_DESC_DEV_BCD_USB_20_1,         /* 3  bcdUSB */ \
  0xef,                              /* 4  bDeviceClass */ \
  2,                                 /* 5  bDeviceSubClass */ \
  1,                                 /* 6  bDeviceProtocol */ \
  64,                                /* 7  bMaxPacketSize */ \
  0x00,                              /* 8  idVendor */ \
  0x00,                              /* 9  idVendor */ \
  0x30,                              /* 10 idProduct */ \
  0x70,                              /* 11 idProduct */ \
  0x10,                              /* 12 bcdDevice : Device release number */ \
  0x00,                              /* 13 bcdDevice : Device release number */ \
  0x01,                              /* 14 iManufacturer : Index of manufacturer string */ \
  0x02,                              /* 15 iProduct : Index of product string descriptor */ \
  0x00,                              /* 16 iSerialNumber : Index of serial number decriptor */ \
  0x01                               /* 17 bNumConfigurations : Number of possible configs */ \
}

#endif 


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

// Configuration descriptor for Audio 2.0
#define DESC_CONFIG { \
/* Configuration descriptor: */ \
0x09, 0x02, 0xc2,0x00, 0x04, 0x01, 0x00, 0xC0, 0x32, \
0x08, 0x0b, 0x00, 0x02, 0x01, 0x00, 0x20, 0x0,                 /* Interface assoc desc */ \
0x09, 0x04, 0x00, 0x00, 0x00, 0x01, 0x01, 0x20, 0x04, \
0x09, 0x24, 0x01, 0x00, 0x02, 0x01, 0x58, 0x00, 0x00,          /* Class specific AC Int desc */ \
0x08, 0x24, 0x0A, 0x03, 0x03, 0x03, 0x00, 0x06,                /* Clock source */ \
\
/* Input Terminal Descriptor: */ \
0x11, 				/* 0  bLength in bytes: 17 */ \
0x24, 				/* 1  bDescriptorType: CS_INTERFACE */ \
0x02, 				/* 2  bDescriptorSubType: INPUT_TERMINAL */ \
0x06, 				/* 3  bTerminalID */ \
0x01, 0x01, 			/* 4  wTerminalType: USB Streaming */ \
0x00, 				/* 6  bAssocTerminal */ \
0x03, 				/* 7  bCSourceID: ID of Clock Entity */ \
0x08,				/* 8  bNrChannels */ \
0xFF, 0x00, 0x00, 0x00, 	/* 9  bmChannelConfig */ \
0x00,           		/* 13 iChannelNames */ \
0x00, 0x00,     		/* 14 bmControls */ \
0x07,           		/* 16 iTerminal */ \
\
/* Output Terminal Descriptor: */ \
0x0C, 				/* 0  bLength (7) */ \
0x24,                           /* 1  bDescriptorType: CS_INTERFACE */ \
0x03, 				/* 2  bDescriptorSubType: OUTPUT_TERMINAL */ \
0x07, 				/* 3  bTerminalID */ \
0x01, 0x03, 			/* 4  wTerminalType */ \
0x00,                           /* 6  bAssocTerminal */ \
0x0A,                           /* 7  bSourceID */ \
0x03, 				/* 8  bCSourceUD */ \
0x00, 0x00,                     /* 9  bmControls */ \
0x08, 				/* 11 iTerminal */ \
\
/* Feature Unit Descriptor: */ \
0x2A, 				/* 0  bLength: 6+(ch+1)*4 */ \
0x24,                           /* 1  bDescriptorType: CS_INTERFACE */ \
0x06,                           /* 2  bDescriptorSubType: FEATURE_UNIT */ \
0x0A,                           /* 3  bUnitID */ \
0x06,                           /* 4  bSourceID */ \
0x0F, 0x00, 0x00, 0x00,         /* 5  bmaControls(0) (Master) */ \
0x0F, 0x00, 0x00, 0x00,         /* 10 bmaControls(1) */ \
0x0F, 0x00, 0x00, 0x00,         /* 15 bmaControls(2) */ \
0x0F, 0x00, 0x00, 0x00,         /* 20 bmaControls(3) */ \
0x0F, 0x00, 0x00, 0x00,         /* 25 bmaControls(4) */ \
0x0F, 0x00, 0x00, 0x00,         /* 30 bmaControls(5) */ \
0x0F, 0x00, 0x00, 0x00,         /* 35 bmaControls(6) */ \
0x0F, 0x00, 0x00, 0x00,         /* 40 bmaControls(7) */ \
0x0F, 0x00, 0x00, 0x00,         /* 45 bmaControls(8) */ \
0x05,                           /* 40 iFeature */ \
\
0x09, 0x04, 0x01, 0x00, 0x00, 0x01, 0x02, 0x20, 0x03,          /* Audio streaming int */\
0x09, 0x04, 0x01, 0x01, 0x01, 0x01, 0x02, 0x20, 0x03,          /* Audio streaming int */ \
0x10, 0x24, 0x01, 0x06, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x08, 0xFF, 0x00, 0x00, 0x00, 0x00, /* AS_GENERAL */ \
0x06, 0x24, 0x02, 0x01, 0x04, 0x20,                            /* Format type descriptor */ \
\
/* Standard AS Isonchrous Audio Data Endpoint Descriptor: */ \
0x07, 				/* 0  bLength (7) */ \
0x05, 				/* 1  bDescriptorType: ENDPOINT */ \
0x01, 0x0d, 0xC8, 0x03, 0x01,                      \
\
/* Class Specific AS Isochronous Audio Data Endpoint Descriptor: */ \
0x08, 0x25, 0x01, 0x00, 0x00, 0x02, 0x05, 0x00,                  \
\
/* HID: */ \
0x09, 0x04, 0x03, 0x00, 0x01, 0x03, 0x00, 0x00, 0x00, \
0x09, 0x21, 0x00, 0x01, 0x00, 0x01, 0x22, 0x32, 0x00, \
0x07, 0x05, 0x82, 0x03, 0x04, 0x00, 0x08, \
}

#endif


#define DESC_STR_LANGIDS \
{ \
  0x4, 					/* 0  bLength */ \
  XUD_DESC_DEV_B_DESCTYPE_STRING,       /* 1  bDescriptorType (STRING: 0x03) */ \
  XUD_DESC_STR_USENG,                   /* 2  wLangID[0] */ \
  XUD_DESC_STR_USENG>>8,                /* 3  wLangID[0] */ \
  '\0' \
}


/* Standard descriptors */
static unsigned char devDesc[] = DESC_DEVICE;
static unsigned char cfgDesc[] = DESC_CONFIG;

/* String descriptors */
static unsigned char strDesc_langIDs[] = DESC_STR_LANGIDS;

/* Strings table for string desctiptors. Note: for usabilty unicode and datalengh are dealt with automatically */
/* therefore just the strings are required below. string[0] (langIDs) is a speacial one... */
static unsigned char strDescs[][40] = 
{
  "string0", 				// 0: Place holder for string 0 (LANGIDs) 
  "XMOS",				// 1: iManufacturer 
  "XMOS USB Audio",			// 2: iProduct
  "XMOS USB 2.0 Audio Stream OUT",	// 3:  
  "XMOS USB 2.0 Audio Control", 	// 4: 
  "XMOS Feature Unit", 			// 5: 
  "XMOS Clock", 			// 6:
  "Input term",				// 7:
  "Output term", 			// 8: 
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

void ep0_thread( chanend c_ep0, chanend c_ep0_con  )
{
    int datalength;
    int j;
    unsigned char buffer[1024];
    SetupPacket sp;
    int loop = 1;
    unsigned char pid;
    int i_tmp;

    /* Copy langIDs string desc into string[0] */
    /* TODO: Macro? */
    safememcpy(strDescs[0], strDesc_langIDs, sizeof(strDesc_langIDs));
 
    while(1)
    {
        /* Do standard enumeration requests */ 
      int retVal = XUD_DoEnumReqs(c_ep0, devDesc, sizeof(devDesc), cfgDesc, sizeof(cfgDesc), strDescs, sp);
      if (retVal == -1) {
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
                                    XUD_DoSetRequestStatus(c_ep0, 0);
                                    break;
            
                                /* Get descriptor */ 
                                case B_REQ_GETDESC:
             
                                    /* Inspect which descriptor require (high byte of wValue) */ 
                                    switch( sp.wValue & 0xff00 )
                                    {
                                        /* HID Report Descriptor */
                                        case  WVALUE_GETDESC_HID_REPORT:
                  
                                            /* Go Get Request protocol */
                                            XUD_DoGetRequest( c_ep0, 0, hidReportDesc, sizeof( hidReportDesc ));
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
                                /* Standard request: SetConfiguration */
                                case B_REQ_SETCONFIG:
                
                                    /* TODO: Set the config */

                                    /* No data stage for this request, just do status stage */
                                    XUD_DoSetRequestStatus(c_ep0, 0);
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
                                    switch( sp.wIndex >> 8 )
                                    {
                                        /* TODO look up unit IDs */
                                        /* Clock Unit */
                                        case 0x3:  
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
                                                        datalength = XUD_GetBuffer_(c_ep0, 0, buffer);
                        
                                                        if(datalength != 4)
                                                        {
                                                            XUD_Error( "Bad datalength for sample rate ");
                                                        }
                                                        
                                                        /* Re-construct Sample Freq */
                                                        i_tmp = buffer[0] | (buffer[1] << 8) | buffer[2] << 16 | buffer[3] << 24; 

                                                        /* Instruct audio thread to change sample freq */
                                                        //c_aud_ctl <: CMD_SET_SAMFREQ;
                                                        //c_aud_ctl <: i_tmp;
                                                        params[9] = i_tmp;
                                                        //XUD_Sup_Delay(1000000);
                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    /* Direction: Device-to-host: Send Current Sample Freq */
                                                    else
                                                    {
                                                        /* Get current sample freq from audio thread */
                                                        //c_aud_ctl <: CMD_GET_SAMFREQ;
                                                        //c_aud_ctl :> i_tmp;

                                                        //buffer[0] = i_tmp;
                                                        //buffer[1] = i_tmp>>8;
                                                        //buffer[2] = i_tmp>>16;
                                                        //buffer[3] = i_tmp>>24;
                                                        //printhexln(i_tmp);
                                                        (buffer, unsigned[])[0] = params[9];

                                                        /* Send back current sample freq */
                                                        XUD_DoGetRequest( c_ep0, 0, buffer, sp.wLength );
                                                    }
                                                    break;
                                                }
                                                /* Clock valid control */
                                                case 2: 
                                                {
                                                    buffer[0] = 1;

                                                    XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);

                                                  
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                                                    break;
                                                }

                                                default:
                                                
                                                    XUD_Error("Unknown control selector");
                                                    break;
                                                
                                            }
                                            break;
                                        }
                 
                                        // Feature unit ID TODO: Look this up 
                                        case 0xa: 
                                        {
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here (with volume)
                                                        loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                                        params[sp.wValue&0xff] = buffer[0] | (buffer[1] << 8);
                                                        updateVol(sp.wValue&0xff);

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = params[sp.wValue&0xff];
                                                        buffer[1] = (params[sp.wValue&0xff] >> 8);
                                                        XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                                    }
                                                    break;
                                                }
                                                case 1: // Mute Control
                                                {
                                                    if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                                                    {
                                                        // Expect OUT here
                                                        loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                                        params[(sp.wValue&0xff)+10] = buffer[0];
                                                        updateVol(sp.wValue&0xff);

                                                        // Send 0 Length as status stage
                                                        XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                                                    }
                                                    else // Direction: Device-to-host
                                                    {
                                                        buffer[0] = params[(sp.wValue&0xff)+10];
                                                        XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                        // Status stage (0 length OUT)
                                                        loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
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
                                            XUD_Error_hex("ERR: Unknown unit: ", sp.wIndex);
                                            break;
                                          
                                    }  // switch(sp.wIndex >> 8)              
                                    break;
                                }
                                
                                /* Range */ 
                                case 0x02: // Range
                                {
                                    switch(sp.wIndex >> 8)
                                    {
                                        // TODO look up unit IDs
                                        case 0x3:  // Clock unit
                                        
                                            switch(sp.wValue >> 8)
                                            {
                                                case 1: // Sam Freq Control
                                                
                                                    storeShort(buffer, 0, 4);
                                                    storeInt(buffer, 2, 44100);
                                                    storeInt(buffer, 6, 44100);
                                                    storeInt(buffer, 10, 0);
                                                    storeInt(buffer, 14, 48000);
                                                    storeInt(buffer, 18, 48000);
                                                    storeInt(buffer, 22, 0);
                                                    storeInt(buffer, 26, 96000);
                                                    storeInt(buffer, 30, 96000);
                                                    storeInt(buffer, 34, 0);
                                                    storeInt(buffer, 38, 192000);
                                                    storeInt(buffer, 42, 192000);
                                                    storeInt(buffer, 46, 0);
                       
                                                    XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                                                    break;
                                                
                     
                                                default:
                                                    XUD_Error_hex("Unknown control selector: ", sp.wValue);
                                                    break;
                                            }
                                            break;
                                         
                                        case 0xa:  // Feature unit ID
                                            switch(sp.wValue >> 8)
                                            {
                                                case 2: // Volume Control
                                                
                                                    storeShort(buffer, 0, 1);
                                                    storeShort(buffer, 2, 0);
                                                    storeShort(buffer, 4, 0x7fff);
                                                    storeShort(buffer, 6, 1);

                                                    XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                                    // Status stage (0 length OUT)
                                                    loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                                                     break;
                                                 
                                                default:
                                                    printstrln("unknown cont rol selector: ");
                                                    printhexln(sp.wValue);
                                                    break;

                                            }
                                            break;


                                        default:
                                            printstrln("unknown unit: ");
                                            printhexln(sp.wIndex);
                                            break;
                   
                                    }               
  
                                break;
                            }

#else          /* Audio 1.0 - Skeleton */
     
                            case 0x0A: // SET IDLE
                            {
                                // TODO: Report ID and Duration from wValue
                                // TODO: Interface number from wIndex           
                                loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                                break;
                            }  
              
                            case B_REQ_SETINTER: 
                            {
                                // TODO Actually set interface!
                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                                break;
                            }
              
                            case B_REQ_SET_CUR:
                            {
                                // TODO Actually set_cur and use the volume!
                                // Expect OUT here
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                                break;
                            }
                            case B_REQ_GET_CUR:
                            {
                                // TODO Actually get cur!!
                                buffer[0] = 0;
                                buffer[1] = 0x0;
                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                 break;
                            }
                            case B_REQ_GET_MIN:
                            {
                                // TODO Actually get_cur!
                                buffer[0] = 0;
                                buffer[1] = 0x80;
                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                break;
                            }
                            case B_REQ_GET_MAX:
                            {
                                // TODO Actually GET MAX!
                                buffer[0] = 0;
                                buffer[1] = 0x2;
                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                                break;
                            }
                            case B_REQ_GET_RES:
                            {
                                // TODO Actually GET RES!
                                buffer[0] = 0;
                                buffer[1] = 0x1;
                                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                                // Status stage (0 length OUT)
                                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
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
  inct(c_ep0);
  inct(c_ep0_con);
  
  //endpoint1dead();
 //stop_streaming_slave(c_ep0);
 //stop_streaming_slave(c_ep0_con);

  //printstrln("ep thread exit");
}
