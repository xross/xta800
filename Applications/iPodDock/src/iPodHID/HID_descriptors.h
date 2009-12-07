#ifndef HID_DESCRIPTORS_H_
#define HID_DESCRIPTORS_H_

// Standard HID defines and descriptors

#pragma pack(1)
typedef struct
{
  char bLength;
  char bDescriptorType;
  short bcdHID;
  char bCountryCode;
  char bNumDescriptors;
  char bDescriptorType0;
  short bDescriptorLength0;
} s_hid_descriptor;

#define HID_SET_REPORT 0x09
#define HID_DESCRIPTOR_REPORT 0x22
#define HID_REPORT_TYPE_OUTPUT 0x02

#endif /*HID_DESCRIPTORS_H_*/
