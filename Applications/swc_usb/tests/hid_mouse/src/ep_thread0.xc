

#include <xs1.h>
#include <print.h>
#include <safestring.h>
#include "xud.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_UIFM_Functions.h"
#include "XUD_Defines_Internal.h"

extern void ep0_thread(chanend c, chanend c_con);

int Endpoint0Wrapper(chanend c_ep0_data, chanend c_ep0_con)
{
  // A reset to this loop...
  while(1)
  {
    ep0_thread(c_ep0_data, c_ep0_con);
  }
  return 0;
}

#define HID 1

// This devices Device Descriptor:
#define DESC_DEVICE \
{ \
  18,                  /* 0  bLength : Size of descriptor in Bytes (18 Bytes) */ \
  XUD_DESC_DEV_B_DESCTYPE_DEVICE,    /* 1  bdescriptorType */ \
  XUD_DESC_DEV_BCD_USB_20_0,         /* 2  bcdUSB */ \
  XUD_DESC_DEV_BCD_USB_20_1,         /* 3  bcdUSB */ \
  XUD_DESC_DEV_B_DEVCLASS_INTER,     /* 4  bDeviceClass */ \
  0,                                 /* 5  bDeviceSubClass */ \
  0,                                 /* 6  bDeviceProtocol */ \
  64,                                /* 7  bMaxPacketSize */ \
  0x52,                              /* 8  idVendor */ \
  0x18,                              /* 9  idVendor */ \
  0xa4,                              /* 10 idProduct */ \
  0x00,                              /* 11 idProduct */ \
  0x10,                              /* 12 bcdDevice : Device release number */ \
  0x00,                              /* 13 bcdDevice : Device release number */ \
  0x01,                              /* 14 iManufacturer : Index of manufacturer string */ \
  0x02,                              /* 15 iProduct : Index of product string descriptor */ \
  0x00,                              /* 16 iSerialNumber : Index of serial number decriptor */ \
  0x01                               /* 17 bNumConfigurations : Number of possible configs */ \
}

#define CON_DESC_B_LENGTH 9
#define INT_DESC_B_LENGTH 9
#define HID_DESC_B_LENGTH 9

#define DESC_CONFIG \
{  \
  /* Configuration descriptor: */ \
  CON_DESC_B_LENGTH,                   /* 0  bLength : Size of descriptor in Bytes */ \
  XUD_DESC_DEV_B_DESCTYPE_CONFIG,      /* 1  bDescriptortype */ \
  ((CON_DESC_B_LENGTH + INT_DESC_B_LENGTH + HID_DESC_B_LENGTH + 7) & 0xff),  /* 2  wTotalLength */ \
  ((CON_DESC_B_LENGTH + INT_DESC_B_LENGTH + HID_DESC_B_LENGTH + 7) >> 8),    /* 3  wTotalLength */ \
  1,                                   /* 4  bNumInterfaces */ \
  1,                                   /* 5  bConfigurationValue : Value to use as argument to select this configuration */ \
  4,                                   /* 6  iConfiguration : Index of String Descriptor descripbing this configuration */ \
  8,                                   /* 7  bmAttributes    */ \
                                       /*    7.7 : Reserved : This bit is reserved and is set to 1 */ \
                                       /*    7.6 : 0 if buss powered */ \
                                       /*    7.5 : 1 If remote wakeup supported */ \
                   /*    7.4:0 : Reserved: Set to 0 */ \
  200,                                 /* 8  bMaxPower : Max Power consumption in 2mA units */ \
  \
  /* Interface descriptor details */ \
  INT_DESC_B_LENGTH,                   /* 0  bLength : Size of descriptor in Bytes */ \
  XUD_DESC_DEV_B_DESCTYPE_INTER,       /* 1  bDescriptorType (Interface: 0x04)*/ \
  0,                                   /* 2  bInterfacecNumber : Number of interface */ \
  0,                                   /* 3  bAlternateSetting : Value used  alternate interfaces using SetInterface Request */ \
  1,                                   /* 4: bNumEndpoints : Number of endpoitns for this interface (excluding 0) */ \
  XUD_DESC_INT_B_CLASS_HID,            /* 5: bInterfaceClass */ \
  XUD_DESC_INT_B_SUBCLASS_BOOT,        /* 6: bInterfaceSubClass */ \
  XUD_DESC_INT_B_PROTOCOL_MOUSE,       /* 7: bInterfaceProtocol*/ \
  0,                     /* 8  iInterface */ \
  \
  /* The device implements HID Descriptor: */ \
  HID_DESC_B_LENGTH,              /* 0  bLength : Size of descriptor in Bytes */ \
  XUD_DESC_DEV_B_DESCTYPE_HID,         /* 1  bDescriptorType (HID) */ \
  0x10,                                /* 2  bcdHID */ \
  0x01,                                /* 3  bcdHID */ \
  0,                                   /* 4  bCountryCode */ \
  1,                                   /* 5  bNumDescriptors */ \
  0x22,                                /* 6  bDescriptorType[0] (Report) */ \
  72,                                  /* 7  wDescriptorLength[0] */ \
  0,                                   /* 8  wDescriptorLength[0] */ \
  \
  /* Endpoint descriptor */ \
  0x7,                   /* 0  bLength */ \
  XUD_DESC_DEV_B_DESCTYPE_EP,          /* 1  bDescriptorType */ \
  XUD_DESC_EP_ADDRESS_IN | 0x1,        /* 2  bEndpointAddress */ \
                                       /*      2.7: Direction */ \
                                       /*      2.4..6: Reserved:0 */ \
                                       /*      2.0..3: EP number */ \
  DESC_EP_BM_ATTRIBS_TTYPE_INT | 0,    /* 3  bmAttributes */ \
                                       /*      0..1: Transfer type */ \
  64,                                  /* 4  wMaxPacketSize */ \
  0,                                   /* 5  wMaxPacketSize */ \
  1,                                   /* 6  bInterval */ \
} 



#define DESC_STR_LANGIDS \
{ \
  0x4, 					/* 0  bLength */ \
  XUD_DESC_DEV_B_DESCTYPE_STRING,       /* 1  bDescriptorType (STRING: 0x03) */ \
  XUD_DESC_STR_USENG,                   /* 2  wLangID[0] */ \
  XUD_DESC_STR_USENG>>8                 /* 3  wLangID[0] */ \
}


static unsigned char deviceDescriptor[] = DESC_DEVICE;
static unsigned char configDescriptor[] = DESC_CONFIG;
static unsigned char stringDescriptor_langIDs[] = DESC_STR_LANGIDS;

// Strings						             iManu     iProduct
static unsigned char stringDescriptors[][40] = {
	"string0", 			// Place holder for string 0 (LANGIDs) 
     	"XMOS",				// iManufacturer 
 	"XMOS DEV", 			// iProduct
 	"XMOS USB 2.0 Audio Stream OUT", 
	"XMOS USB 2.0 Audio Control", 
	"XMOS Feature Unit", 
	"XMOS Clock", 
	"Input term",
	"Output term", }; 

#ifdef HID
static unsigned char hidReportDescriptor[] = 
{
   0x05, 0x01,
   0x09, 0x02,
   0xA1, 0x01,
   0x05, 0x09,
   0x19, 0x01,
   0x29, 0x03,
   0x15, 0x00,
   0x25, 0x01,
   0x95, 0x03,
   0x75, 0x01,
   0x81, 0x02,
   0x95, 0x01,
   0x75, 0x05,
   0x81, 0x03,
   0x05, 0x01,
   0x09, 0x01,
   0xA1, 0x00,
   0x09, 0x30,
   0x09, 0x31,
   0x15, 0x81,
   0x25, 0x7F,
   0x75, 0x08,
   0x95, 0x02,
   0x81, 0x06,
   0xC0, 
   0x09, 0x38, 
   0x95, 0x01,
   0x81, 0x06,
   0x09, 0x3C,
   0x15, 0x00,
   0x25, 0x01, 
   0x75, 0x01,
   0x95, 0x01,
   0xB1, 0x22, 
   0x95, 0x07, 
   0xB1, 0x01, 
   0xC0,   

};
#else
static unsigned char hidReportDescriptor[] =
{
0x05 , 0x0C , 0x09 , 0x01 , 0xA1 , 0x01 , 0x15 , 0x00 ,  0x25 , 0x01 , 0x09 , 0xE9 , 0x09 , 0xEA , 0x75 , 0x01 , 
0x95 , 0x02 , 0x81 , 0x02 , 0x09 , 0xE2 , 0x09 , 0x00 ,  0x81 , 0x06 , 0x09 , 0x00 , 0x95 , 0x04 , 0x81 , 0x02 , 
0x26 , 0xFF , 0x00 , 0x09 , 0x00 , 0x75 , 0x08 , 0x95 ,  0x03 , 0x81 , 0x02 , 0x09 , 0x00 , 0x95 , 0x04 , 0x91 , 
0x02 , 0xC0
};
#endif

typedef struct bmRequestType{
  unsigned char Recipient;       // [4..0]   Request directed to:
                                 //          0b00000: Device
                                 //          0b00001: Specific interface
                                 //          0b00010: Specific endpoint
                                 //          0b00011: Other element in device                           
  unsigned char Type;            // [6..5]   0b00: Standard request
                                 //          0b01: Class specific request
                                 //          0b10: Request by vendor specific driver 
  unsigned char Direction;       // [7]      0 (Host->Dev)
                                 //          1 (Dev->Host)
} BmRequestType;

typedef struct setupPacket{
 
  BmRequestType bmRequestType;   // (1 byte) Specifies direction of dataflow, type of rquest and recipient
  unsigned char bRequest;        // Specifies the request
  unsigned short wValue;         // (2 bytes) Host can use this to pass info to the device in its own way
  unsigned short wIndex;         // (2 bytes) Typically used to pass index/offset such as interface or EP no
  unsigned short wLength;        // (2 Bytes) Number of data bytes in the data stage (for Host -> Device this                                   // this is exact count, for Dev->Host is a max.
} SetupPacket;



void XUD_SetDevAddr(chanend c, unsigned addr)
{
  outct(c, XUD_CT_SETADR);
  outuchar(c, addr);
}


void ParseSetupPacket(unsigned char b[], SetupPacket &p)
{
  // Byte 0: bmRequestType.
  p.bmRequestType.Recipient = b[0] & 0x1f;
  p.bmRequestType.Type      = (b[0] & 0x60) >> 5;
  p.bmRequestType.Direction = b[0] >> 7;

  // Byte 1:  bRequest 
  p.bRequest = b[1];

  // Bytes [2:3] wValue
  p.wValue = (b[3] << 8) | (b[2]);

  // Bytes [4:5] wIndex
  p.wIndex = (b[5] << 8) | (b[4]);

  // Bytes [6:7] wLength
  p.wLength = (b[7] << 8) | (b[6]);

}

void printSetupPacket(SetupPacket sp)
{
  printstr("Setup data\n");
  printstr("bmRequestType.Recipient: ");
  printhexln(sp.bmRequestType.Recipient);
  printstr("bmRequestType.Type: ");
  printhexln(sp.bmRequestType.Type);
  printstr("bmRequestType.Direction: ");
  printhexln(sp.bmRequestType.Direction);  
  printstr("bRequest: ");
  printhexln(sp.bRequest); 
  printstr("bmRequestType.wValue: ");
  printhexln(sp.wValue);  
  printstr("bmRequestType.wIndex: ");
  printhexln(sp.wIndex);  
  printstr("bmRequestType.wLength: ");
  printhexln(sp.wLength);  
}


void XUD_EnableControl(chanend c);
void XUD_SetDeviceAddress(unsigned char a, lock ifm);

unsigned count = 0;

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


void ep0_thread(chanend c_ep0, chanend c_ep0_con)
{
  int datalength;
  int j;
  unsigned char buffer[1024];
  SetupPacket sp;
   int loop = 1;
  unsigned char pid;
  //epThreadActive = 1;
  count = 0;
 
  //XUD_EnableControl(c_ep0_con);
 
  //start_streaming_slave(c_ep0);
  //start_streaming_slave(c_ep0_con);
 
  while(loop)
  { 
    // Wait for Setup data, if datasize == -1, then reset
    datalength = XUD_GetSetupBuffer(c_ep0, c_ep0_con, 0, buffer);
    if(datalength < 0)
      break;

    ParseSetupPacket(buffer, sp);

    switch(sp.bmRequestType.Type)
    {
      case BM_REQTYPE_TYPE_STANDARD:
      { 
        switch(sp.bmRequestType.Recipient)
        {
          case BM_REQTYPE_RECIP_INTER:
          { 
            switch(sp.bRequest)
            {
              case B_REQ_SETINTER:
                //TODO: Actually set the interface!!
                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
               break;
             
              case B_REQ_GETDESC:
              {
                switch(sp.wValue & 0xff00)
                {
                  case  WVALUE_GETDESC_HID_REPORT:
                  {
//TODO: dont hardcode lengths!
#ifdef HID
                    for(int i = 0; i < 64; i++) 
                      buffer[i] = hidReportDescriptor[i]; 

                     loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 64, PIDn_DATA1);
                             
                     XUD_Sup_Delay(10000);
 
                     for(int i = 0; i < 8; i+=1)                   
                       buffer[i] = hidReportDescriptor[i+64]; 

                     loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 8, PIDn_DATA0);
#else
                     for(int i = 0; i < sizeof(hidReportDescriptor); i++) 
                       buffer[i] = hidReportDescriptor[i]; 

                     loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sizeof(hidReportDescriptor), PIDn_DATA1);
#endif 
                     // Status stage
                     loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                     break;
                  }
                  default:
                    printstrln("ERR: unknown descriptor req\n");
                    break;
                }
                break;
              }

            }
            break;
          }

          // Standard request
          case BM_REQTYPE_RECIP_DEV:
          {      
            // Recipient: Device
            // Standard Device requests (8):
            switch(sp.bRequest)
            {      
              // Standard request: SetConfiguration
              case B_REQ_SETCONFIG:
              { 
                //TODO: Actually set the config!!

                XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                break;
              }
 
              // Standard request: Get descriptor
              case B_REQ_GETDESC:
              {
                // Which kind of descriptor required? High byte of wValue
                switch(sp.wValue & 0xff00)
                {
                  // Device descriptor 
                  case WVALUE_GETDESC_DEV:              
                  {
                    // Reply: Send device descriptor back
                    for(int ii = 0; ii < 18; ii++) // TODO, Copying because reading out in words, may trap 
                      buffer[ii] = deviceDescriptor[ii]; 
                    
                    loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 18, PIDn_DATA1);

                    // Status stage (0 length OUT)
                    loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                   // if(loop != -1)printstr("DID DESC STATUS");

                    //TODO: Status stage: Expect 0 length packet
                    break;
                  }

                  // Configuration descriptor
                  case WVALUE_GETDESC_CONFIG:
                  {
                    // Reply: Send configuration descriptor (TODO: Dev may have multiple configs)
                    // And related interface/endpoint descriptors
                    // TODO: check for packet overflow
                    
                    pid = PIDn_DATA1;
                    for(int k = 0; k < sizeof(configDescriptor) && k < sp.wLength; k+=64)
                    {
                      j = 0;
                    
                      for(int i = k; i < sizeof(configDescriptor)  && j < 64 && i < sp.wLength; i++) 
                      {  
                        buffer[j] = configDescriptor[i];
                        j++; 
                      }
                    
                      loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, j, pid);  
                      pid ^= 0x88;                    
                    }

                    //TODO: Status stage: Expect 0 length packet
                    // Status stage (0 length OUT)
                    loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                    break; 
                  }
                
                

                  // String descriptor
                  case WVALUE_GETDESC_STRING:
                  {
                    // Which string?
                    switch(sp.wValue & 0xff)
                    {
                      case WVALUE_GETDESC_STRING_LANGIDS:
                      {
                        datalength = sizeof(stringDescriptor_langIDs);
                        // Reply: Send String descriptor (TODO: Dev may have multiple languages)
                        for(int i = 0; i < datalength; i++) // TODO, why copy??
                          buffer[i] = stringDescriptor_langIDs[i]; 
                        loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, datalength, PIDn_DATA1);
                    
                        //Status stage: Expect 0 length packet
                        loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                        break; 
                      }

                      default:
                      {
                        // TODO: Bounds check
                        datalength = safestrlen(stringDescriptors[sp.wValue&0xff]);
                        datalength *=2;
                      
                        buffer[0] = datalength + 2;

                        if(sp.wLength < datalength + 2)
                          datalength = sp.wLength -2 ;
                        buffer[1] = XUD_DESC_DEV_B_DESCTYPE_STRING;
                        
                        // Add zero bytes for unicode..
                        for(int i = 0; i < datalength; i+=2)
                        {
                          buffer[i+2] = stringDescriptors[sp.wValue&0xff][i>>1];
                          buffer[i+3] = 0;
                        }
                        loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, datalength+2, PIDn_DATA1);

                         // Status stage (0 length OUT)
                         loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                        break;
                      }
                    }
                    break;
                  }
                }
                break;
              }
              
               // Standard request: Set device address
              case B_REQ_SETADDR:
              {
                // Status stage: Send a zero length packet
                loop =  XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                // HACK, We should wait until ACK is received for status stage before changing address
                XUD_Sup_Delay(50000);

                //XUD_SetDeviceAddress(sp.wValue, l_ifm); 
                XUD_SetDevAddr(c_ep0_con, sp.wValue);

                break;
              }
           
                       
  
              default:
                printstrln("ERR: UNRECOGNISED REQUEST");
                break;
          
            }
            break;
          }
          default:
            printstr("ERR: Dev\n");
            break;
        }
        break;
      }
        
      case BM_REQTYPE_TYPE_CLASS:
      { 
        switch(sp.bmRequestType.Recipient)
        {
          case BM_REQTYPE_RECIP_EP:
          {
            switch(sp.bRequest)
            {
              case B_REQ_SET_CUR:

                //printstrln("set cur\n");
                loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                // Status stage (0 length IN) 
                loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                break;

              case B_REQ_GET_CUR:
                printstrln("get cur\n");
                break;


              default:
                printstrln("Bad req\n");
                break;
            }
            break;
          }

          // Class request
          case BM_REQTYPE_RECIP_INTER:
          {      
            // Recipient: Interface
            // NOTE: CLASS SPECIFIC REQUESTS
            switch(sp.bRequest )
            { 
#if 0
              case 0x01: // Cur
              {
               switch(sp.wIndex >> 8)
               {
                 // TODO look up unit IDs
                 case 0x3:  // Clock unit
                 {
                   switch(sp.wValue >> 8)
                   {
                     case 1: // Sam Freq Control
                     {
                       if(sp.bmRequestType.Direction == 0) // Direction: Host-to-device
                       {
                         // Expect OUT here (with sr)
                         loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                         
                         params[9] = buffer[0] | (buffer[1] << 8) | buffer[2] << 16 | buffer[3] << 24;
                         
                         // Send 0 Length as status stage
                         XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
                       }
                       else
                       {
                         buffer[0] = params[9];
                         buffer[1] = params[9]>>8;
                         buffer[2] = params[9]>>16;
                         buffer[3] = params[9]>>24;
                         XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                         // Status stage (0 length OUT)
                         loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                       }
                       break;
                     }
                     case 2: // Clock valid control
                     {
                       buffer[0] = 1;
                       XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                       // Status stage (0 length OUT)
                       loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);
                       break;
                     }

                     default:
                     {
                       printstrln("unknown control selector: ");
                       printhexln(sp.wValue);
                       break;
                     }
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
                     {
                       printstrln("ERR: Unknown control selector: ");
                       printhexln(sp.wValue);
                       break;
                     }
                   }
                   break;
                 }

                 default:
                 { 
                   // We dont have a unit with this ID!
                   printstrln("ERR: Unknown unit: ");
                   printhexln(sp.wIndex);
                   break;
                 } 
               }  // switch(sp.wIndex >> 8)              
               break;
              } // case 0x01
              case 0x02: // Range
              {
               switch(sp.wIndex >> 8)
               {
                 // TODO look up unit IDs
                 case 0x3:  // Clock unit
                 {
                   switch(sp.wValue >> 8)
                   {
                     case 1: // Sam Freq Control
                     {
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
                     }
                     
                     default:
                       printstrln("unknown control selector: ");
                       printhexln(sp.wValue);
                       break;
                   }
                   break;
                 }
                 case 0xa:  // Feature unit ID
                   switch(sp.wValue >> 8)
                   {
                     case 2: // Volume Control
                     {
                       storeShort(buffer, 0, 1);
                       storeShort(buffer, 2, 0);
                       storeShort(buffer, 4, 0x7fff);
                       storeShort(buffer, 6, 1);

                       XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, sp.wLength, PIDn_DATA1);
                       // Status stage (0 length OUT)
                       loop = XUD_GetBuffer(c_ep0, c_ep0_con, 0, buffer);

                       break;
                     } 
                     default:
                       printstrln("unknown control selector: ");
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
#else          
     
              case 0x0A: // SET IDLE
              {
                  // TODO: Report ID and Duration from wValue
                  // TODO: Interface number from wIndex           
                  loop = XUD_SetBuffer_ResetPid(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);

                break;
              }  
              
              case B_REQ_SETINTER: 
              {
                //printstr("SET INTER\n");
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

                //printstrln("SET CUR INTER");
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
             printSetupPacket(sp);
             break;
         }
        break;
      }

      default:
      {
        printstr("ERR: Unknown request: ");
        printSetupPacket(sp);

        break;
      }
    }
  }

  // END control tokens.
  inct(c_ep0);
  inct(c_ep0_con);
  
  //endpoint1dead();
 //stop_streaming_slave(c_ep0);
 //stop_streaming_slave(c_ep0_con);

  //printstrln("ep thread exit");
}
