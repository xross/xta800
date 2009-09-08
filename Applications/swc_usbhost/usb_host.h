#ifndef __USB_HOST_H__
#define __USB_HOST_H__

#include "EP_manager/bufferManager.h"

unsigned do_sof (unsigned frame_count);
unsigned crc5_usb (unsigned data);
unsigned crc16_usb (unsigned curr_value, unsigned char data);
unsigned setup_device(void);
int do_setup_packet (unsigned address, unsigned endpoint, unsigned data[]) ;
int do_in_packet (unsigned address, unsigned endpoint, unsigned data_pid, unsigned data_offset, unsigned data[]) ;
int do_out_packet (unsigned address, unsigned endpoint, unsigned data_pid, unsigned data_len, unsigned data[]) ;
int do_packet (unsigned pid, unsigned address, unsigned endpoint, unsigned data_len, unsigned data_pid, unsigned data[]) ;
void do_ack ();
void enumerate_device();
void usbHostLLD ();
void XUD_PhyReset(void);

#define APPLE_VENDOR_ID 0x5AC

// DEBUG functions:
unsigned dump_ifm_state();
void sample_rx_err();

typedef enum
{
  BRT_DIRECTION_TO_DEV = 0,
  BRT_DIRECTION_FROM_DEV = 1,
} e_usb_brt_direction;

typedef enum
{
  BRT_RECIPIENT_DEVICE = 0,
  BRT_RECIPIENT_INTERFACE = 1, 
  BRT_RECIPIENT_ENDPOINT = 2,
  BRT_RECIPIENT_OTHER = 3,
} e_usb_brt_recipient;

// Class
typedef enum
{
  BRT_TYPE_STANDARD = 0,
  BRT_TYPE_CLASS = 1,
  BRT_TYPE_VENDOR = 2,
} e_usb_brt_type;

// Struct for setup packet. Each has 8 bytes.
typedef struct bmRequestType
{
  e_usb_brt_recipient Recipient;       // [4..0]   Request directed to:
                                 //          0b00000: Device
                                 //          0b00001: Specific interface
                                 //          0b00010: Specific endpoint
                                 //          0b00011: Other element in device
  e_usb_brt_type Type;            // [6..5]   0b00: Standard request
                                 //          0b01: Class specific request
                                 //          0b10: Request by vendor specific driver 
  e_usb_brt_direction Direction;       // [7]      0 (Host->Dev)
                                 //          1 (Dev->Host)
} BmRequestType;


/** Typedef for SetupPacket structure 
  *
  **/
typedef struct setupPacket
{ 
  BmRequestType bmRequestType;   // (1 byte) Specifies direction of dataflow, type of rquest and recipient
  unsigned char bRequest;        // Specifies the request
  unsigned short wValue;         // (2 bytes) Host can use this to pass info to the device in its own way
  unsigned short wIndex;         // (2 bytes) Typically used to pass index/offset such as interface or EP no
  unsigned short wLength;        // (2 Bytes) Number of data bytes in the data stage (for Host -> Device this                                   // this is exact count, for Dev->Host is a max.
} SetupPacket;


void ParseSetupPacket(SetupPacket setup_data, unsigned tx_data[]);
unsigned class_request(unsigned address, unsigned endpoint, SetupPacket setup_data, unsigned tx_data[]);

// Audio Class specific types - see "USB Device Class Definition for Audio Devices" for details
// Basic ac descriptor is identical to interface descriptor above.

typedef struct acDescriptor 
{
  unsigned char bLength;
  unsigned char bDescriptorType;

} ACDescriptor;

typedef enum
{
  USB_TRANSTYPE_BULK = 0x02,
  USB_TRANSTYPE_INTERRUPT = 0x03,
  USB_TRANSTYPE_ISO = 0x01,
  USB_TRANSTYPE_CONTROL = 0x00,  
} e_usb_transfertype;

typedef struct usbTransfer
{
  e_usb_brt_direction direction;
  e_usb_transfertype transferType;
  char address; // USB Address
  char endpoint; // USB Endpoint number
  SetupPacket setupPacket; // (Optional) Setup packet for control transfers
  int dataLen; // Length of following payload data
  char data[1024]; // Payload
  // TODO: These will probably change
  char transferComplete; // 0 = Incomplete, 1 = Complete
  char ackState; // 0 = ACK, 1 = NACK, 2 = STALL
} UsbTransfer;

#if defined __XC__
int do_lld_packet(UsbTransfer &usbTrans);
#elif defined __STDC__
int do_lld_packet(UsbTransfer *usbTrans);
#endif

#endif
