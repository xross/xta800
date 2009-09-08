#ifndef DEVICE_IPOD_H_
#define DEVICE_IPOD_H_


#include "iPodHID.h"

/* 
 * This header describes the expected device Interface/Endpoint
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
      (int (*)(int, char[]))&dummyfn, // Unset
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
      (int (*)(int, char[]))&iPodHIDRx,
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
      (int (*)(int, char[]))&dummyfn, // Descriptor FnPtr
    },
    {
      0, // IF Num (unset)
      0, // IF Found flag (unset)
      0, // Not alternate
      1, // Num EPs
      EPIFClass_Audio,
      EPIFSubClass_AudioStreaming,
      0, // IF Protocol
      0, // IF String Index
      (int (*)(int, char[]))&dummyfn, // Descriptor FnPtr
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
      (int (*)(int, char[]))&iPodHIDParseDescriptor,
    },
};



#endif /*DEVICE_IPOD_H_*/
