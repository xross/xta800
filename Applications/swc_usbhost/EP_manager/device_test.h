#ifndef DEVICE_TEST_H_
#define DEVICE_TEST_H_


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
      0, // Unset
      (int (*)(int, char[]))&dummyfn
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
      (int (*)(int, char[]))&dummyfn,
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
      (int (*)(int, char[]))&dummyfn,
    },
};



#endif /*DEVICE_TEST_H_*/
