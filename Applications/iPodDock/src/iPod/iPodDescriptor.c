#include "audio.h"
#include "iPodHID.h"
#include "iAP.h"
#include "dummyFns.h"
#include "UsbHostLLD.h"
#include "usb_descriptors.h"

/* 
 * This describes the expected device Interface/Endpoint
 * arrangement and the functions for dealing with receiving/sending 
 * packets to/from the various endpoints
 */
int nDeviceEPs = 2;
s_usb_endpoint deviceEPs[2] = 
{
    {
      EPTransferType_Iso,
      EPSyncType_None,
      EPUsageType_Data,
      1, // IN EP
      0, // Unset
      0, // Unset
      0, // Unset
      0, // NIPT
      1, // IF:1
      0, 
      (int (*)(streaming_chanend, char[], int, int))&audio_rx, // Unset
    },
    {
      EPTransferType_Interrupt,
      EPSyncType_None,
      EPUsageType_Data,
      1, // IN EP
      0, // Unset
      0, // Unset
      0, // Unset
      0, // NIPT
      2, // IF:2
      0, // Unset
      (int (*)(streaming_chanend, char[], int, int))&iPodHIDRx,
    },
};

int nDeviceInterfaces = 3;
s_usb_interface deviceInterfaces[3] = 
{
    {
      0, // IF Num (unset)
      0, // IF Found flag (unset)
      0, // Not alternate
      0, // Num EPs
      EPIFClass_Audio,
      EPIFSubClass_AudioControl,
      0, // IF Protocol
      0, // IF String Index
      (int (*)(char[], int))&dummyfn, // Descriptor FnPtr
      (int (*)(streaming_chanend, int, int))&audio_ctrl, // Ctrl FnPtr
      (int (*)(streaming_chanend, int))&dummytimeout, // Timeout fnptr
      (int (*)(streaming_chanend))&dummyflush,
      (void (*)(void))&dummyinit,
    },
    {
      0, // IF Num (unset)
      0, // IF Found flag (unset)
      1, // Alternate
      1, // Num EPs
      EPIFClass_Audio,
      EPIFSubClass_AudioStreaming,
      0, // IF Protocol
      0, // IF String Index
      (int (*)(char[], int))&dummyfn, // Descriptor FnPtr
      (int (*)(streaming_chanend, int, int))&dummyctrl, // Ctrl FnPtr
      (int (*)(streaming_chanend, int))&dummytimeout, // Timeout fnptr
      (int (*)(streaming_chanend))&dummyflush,
      (void (*)(void))&dummyinit,
    },
    {
      0, // IF Num (unset)
      0, // IF Found flag (unset)
      0, // Not alternate
      1, // Num EPs
      EPIFClass_HID,
      0, // IF Subclass
      0, // IF Protocol
      0, // IF String Index
      (int (*)(char[], int))&iPodHIDParseDescriptor,
      (int (*)(streaming_chanend, int, int))&iAP_ctrl, // Ctrl FnPtr
      (int (*)(streaming_chanend, int))&iAP_timeout, // Timeout fnptr
      (int (*)(streaming_chanend))&iAP_flush,
      (void (*)(void))&iAP_init,
    },
};

const int vendorID = 0x5AC;
const int vendorIDMask = 0xFFFF;
const int productIDMask = 0xFF00;
const int productID = 0x1200;


