#ifndef __USBHOSTLLD_H__
#define __USBHOSTLLD_H__

// USB Ports
// Clock port (for 60Mhz ULPI clock)
#define UIFM_USB_CLK_PORT        XS1_PORT_1H

// External data ports
#define UIFM_TX_DATA_PORT        XS1_PORT_8A
#define UIFM_RX_DATA_PORT        XS1_PORT_8B

// Ports used internally for interfacing to USB
#define UIFM_REG_WRITE_PORT      XS1_PORT_8C
#define UIFM_REG_READ_PORT       XS1_PORT_8D
#define UIFM_FLAG_0_PORT         XS1_PORT_1N
#define UIFM_FLAG_1_PORT         XS1_PORT_1O
#define UIFM_FLAG_2_PORT         XS1_PORT_1P


// LLD transfer types.
// Each endpoint type (control, bulk, interrupt and isochronous) 
// has a function for each direction. 
int USBLLD_ControlTransferIn(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength
    );

// This is a special case version of an IN control transfer when
// data size is 0 so no ACK is needed.
int USBLLD_ControlTransferInDir(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength, int direction
    );

int USBLLD_ControlTransferOut(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength, char data[]
    );

int USBLLD_InterruptTransfer_In(
    int addr, int ep,
    int maxdataLen);

int USBLLD_InterruptTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

int USBLLD_IsoTransfer_In(
    int addr, int ep,
    int maxdataLen);

int USBLLD_IsoTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

int USBLLD_BulkTransfer_In(
    int addr, int ep,
    int maxdataLen);

int USBLLD_BulkTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

// Subsections of setup packet
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

// Return values to epManager from LLD
typedef enum
{
  USB_TRANS_ACKSTATE_ACK = 0x0,
  USB_TRANS_ACKSTATE_NAK = 0x1,
  USB_TRANS_ACKSTATE_STALL = 0x2,
  USB_TRANS_ACKSTATE_IGNORE = 0x3,
  USB_TRANS_ACKSTATE_HOSTDISCON = 0x4
} e_usb_trans_ackstate;

// Endpoint/transfer types
typedef enum
{
  USB_TRANSTYPE_BULK = 0x02,
  USB_TRANSTYPE_INTERRUPT = 0x03,
  USB_TRANSTYPE_ISO = 0x01,
  USB_TRANSTYPE_CONTROL = 0x00,  
  USB_TRANSTYPE_SOF = 0x04,
} e_usb_transfertype;


// usbTransfer is the atomic element that is transferred between epManager and the LLD
// This defines all aspects of a usb transaction and the response is sent back in the
// same format.
// A Setup transaction can still be defined through this interface even though it 
// consists of a setup packet, followed by IN/OUT transactions.
#define MAX_PKT_SIZE_WORDS 129
typedef struct usbTransfer
{
  e_usb_brt_direction direction;
  e_usb_transfertype transferType;
  int address; // USB Address
  int endpoint; // USB Endpoint number
  int dataLen; // Length of following payload data
  int transferComplete; // 0 = Incomplete, 1 = Complete
  e_usb_trans_ackstate ackState; 
  unsigned data[MAX_PKT_SIZE_WORDS]; // Payload
  SetupPacket setupPacket; // (Optional) Setup packet for control transfers
} UsbTransfer;

#if defined __XC__
void usbHostLLD (streaming chanend cUSBReset, chanend cCtrl, streaming chanend cSOF, streaming chanend cUSBReg);
int do_lld_packet(UsbTransfer &usbTrans, streaming chanend cSOF);
#elif defined __STDC__
void usbHostLLD (unsigned cUSBReset, unsigned cCtrl, unsigned cSOF, unsigned cUSBReg);
int do_lld_packet(UsbTransfer *usbTrans, unsigned cSOF);
#endif

typedef enum 
{
  USB_CMD_POWER_CYCLE = 1,
  USB_CMD_SOFT_RESET = 2,
} e_usb_ctrl_cmd;

  
#ifdef __STDC__

int USBLLD_removesof(char *ackstate);
int USBLLD_getreadhandle(UsbTransfer **handle);

#endif

void USBLLD_readfinished();

#endif
