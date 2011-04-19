

// Defines relating to USB/ULPI/UTMI/Phy specs

#define RESET_TIME               100000      // 1ms

// Device attach timing defines
#define T_SIGATT_ULPI            500000      // 5ms
#define T_ATTDB                  10000000    // 10ms
#define T_UCHEND_T_UCH           100000000   
#define T_UCHEND_T_UCH_ULPI      200000      // 2ms 
 

#define T_SUSPEND_TIMEOUT		 200000      // 3ms
#define SUSPEND_T_WTWRSTHS 20000 // How long before checking for J after asserting XcvrSelect and Termselect



#define OUT_TIMEOUT              50000 // How long we wait for data after OUT token
#define TX_HANDSHAKE_TIMEOUT     500  // How long we wait for handshake after sending tx data

///////////////////////////////////////////////////////////////
// Setup requests
//bmRequestType
#define BM_REQTYPE_DIRECTION_OUT 0
#define BM_REQTYPE_DIRECTION_IN  1

#define BM_REQTYPE_TYPE_STANDARD 0x00
#define BM_REQTYPE_TYPE_CLASS    0x01
#define BM_REQTYPE_TYPE_VENDOR   0x02

#define BM_REQTYPE_RECIP_DEV     0x00000
#define BM_REQTYPE_RECIP_INTER   0x00001
#define BM_REQTYPE_RECIP_EP      0x0002
#define BM_REQTYPE_RECIP_OTHER   0x003

//bRequest
#define B_REQ_GETDESC                0x06
#define B_REQ_SETADDR	             0x05
#define B_REQ_GETCONFIG              0x08
#define B_REQ_SETCONFIG              0x09
#define B_REQ_SETINTER               0x0b

// Audio
#define B_REQ_SET_CUR                0x1
#define B_REQ_GET_CUR               0x81
#define B_REQ_GET_MIN		        0x82
#define B_REQ_GET_MAX               0x83
#define B_REQ_GET_RES               0x84

// wValue for Get Descriptor (indicates required descriptor)
// Note: top byte used only
#define WVALUE_GETDESC_DEV       (XUD_DESC_DEV_B_DESCTYPE_DEVICE << 8)
#define WVALUE_GETDESC_CONFIG    (XUD_DESC_DEV_B_DESCTYPE_CONFIG << 8) 
#define WVALUE_GETDESC_STRING    (XUD_DESC_DEV_B_DESCTYPE_STRING << 8)
#define WVALUE_GETDESC_INTER     (XUD_DESC_DEV_B_DESCTYPE_INTER << 8)
#define WVALUE_GETDESC_DEVQUAL   (XUD_DESC_DEV_B_DESCTYPE_DEVQUAL << 8)
#define WVALUE_GETDESC_OSPEED_CFG (XUD_DESC_DEV_B_DESCTYPE_OSPEED_CFG << 8)
#define WVALUE_GETDESC_HID_REPORT (0x22 << 8)


// Low byte values:
#define WVALUE_GETDESC_STRING_LANGIDS	0x0
#define WVALUE_GETDESC_STRING_IPRODUCT  0x2


//////////////////////////////////////////////////////////////////////////////////
// TODO Seperate request defines from defines that user may wish to use for descriptors
// Descriptor defines 
//
// Descriptor types:
#define XUD_DESC_DEV_B_DESCTYPE_DEVICE     0x1
#define XUD_DESC_DEV_B_DESCTYPE_CONFIG     0x2
#define XUD_DESC_DEV_B_DESCTYPE_STRING     0x3
#define XUD_DESC_DEV_B_DESCTYPE_INTER      0x4
#define XUD_DESC_DEV_B_DESCTYPE_EP         0x5
#define XUD_DESC_DEV_B_DESCTYPE_DEVQUAL    0x6
#define XUD_DESC_DEV_B_DESCTYPE_OSPEED_CFG 0x7

#define XUD_DESC_DEV_B_DESCTYPE_HID	  0x21

/////////////////////////////////////////////////////////////////////////////////////
// Device Descriptor defines
//
// bcdUSB
#define XUD_DESC_DEV_BCD_USB_20_0         0x00
#define XUD_DESC_DEV_BCD_USB_20_1         0x02

#define XUD_DESC_DEV_BCD_USB_10           0x0100
#define XUD_DESC_DEV_BCD_USB_11_0           0x10
#define XUD_DESC_DEV_BCD_USB_11_1          0x01

//bDeviceClass
#define XUD_DESC_DEV_B_DEVCLASS_INTER     0x0   // Interface descriptor speicifies class
#define XUD_DESC_DEV_B_DEVCLASS_COMMS     0x2   // Communications

//////////////////////////////////////////////////////////////////////////////////
// String descriptor defines:
#define XUD_DESC_STR_USENG		 0x0409 // US English

////////////////////////////////////////////////////////////////////////////////
// Interface descriptor defines:
//
// Classes:
#define XUD_DESC_INT_B_CLASS_HID	0x3
#define XUD_DESC_INT_B_SUBCLASS_BOOT	0x1

#define XUD_DESC_INT_B_CLASS_AUDIO      0x1
#define XUD_DESC_INT_B_SUBCLASS_AUDIOCONTROL 0x1

// Protocols:
#define XUD_DESC_INT_B_PROTOCOL_MOUSE   0x2

////////////////////////////////////////////////////////////////////////////////
// Endpoint descriptor defines:
//
// bmAtributes.TransferType
#define DESC_EP_BM_ATTRIBS_TTYPE_INT	0x3	// Interupt
							// Control
							// Bulk... iso
							//
#define XUD_DESC_EP_ADDRESS_IN              0x80
#define XUD_DESC_EP_ADDRESS_OUT             0x00

///////////////////////////////////////////////////////////////////////////////////
// HID Descriptor defines


// Raw PIDs
#define PID_OUT   0x01
#define PID_ACK   0x02
#define PID_IN    0x09
#define PID_SOF   0x05
#define PID_SETUP 0x0d
#define PID_PING  0x04

// PIDs with error check
// Token
#define PIDn_OUT   0xe1
#define PIDn_IN    0x69
#define PIDn_SOF   0xa5
#define PIDn_SETUP 0x2d

// Data PIDs
#define PIDn_DATA0  0xc3
#define PIDn_DATA1  0x4b
#define PID_DATA0   0x3
#define PID_DATA1   0xb

#define PIDn_ACK 0xd2
#define PIDn_NAK 0x5a
#define PIDn_STALL 0x1e



#define XUD_EP_TYPE_CON 1
#define XUD_EP_TYPE_DIS 1
#define XUD_EP_TYPE_ISO 0
#define XUD_EP_TYPE_INT 1











// TODO Move these.. they are not usn spec related
#define EP_BUFFER_SIZE 1024



// OUT endpoints
#define EP_OUT_DATA        1
//
// // IN endpoints
#define EP_IN_DATA_AVAIL   1 
//
// // Generic
 #define EP_OK              0
 #define EP_TROUBLE         9
 #define EP_HALT            8
//
