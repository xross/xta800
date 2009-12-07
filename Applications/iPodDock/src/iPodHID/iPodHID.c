#include <string.h>
#include "usb_descriptors.h"
#include "HID_descriptors.h"
#include "UsbHostLLD.h"
#include "iAP.h"
#include "epManager.h"
#include "device.h"

#define MAX_NUM_HID_REPORTS 16
char hidreports[MAX_NUM_HID_REPORTS];
int favhidreport = 0;

int hid_find_audiostreaming_if(int *val)
{
  int i;
  for (i=0; i<nDeviceInterfaces; i++)
  {
    if (deviceInterfaces[i].interfaceClass == EPIFClass_HID &&
        deviceInterfaces[i].interfaceSubClass == 0)
    {
      break;
    }
  }
  if (i == nDeviceInterfaces)
  {
    return 1;
  }
  else
  {
    *val = i;
    return 0;
  }
}

/*
 * iPodHIDInit:
 * Call on iPod connection
 */
void iPodHIDInit(int IFNum)
{
  int i;
  for (i=0; i<MAX_NUM_HID_REPORTS; i++)
    hidreports[i] = -1;
}

/*
 * iPodHIDTx:
 * Call to transmit a packet over the HID abstraction layer
 */
#define HID_REPORT_LENGTH 64
int iPodHIDTx(unsigned char iAPData[], unsigned iAPDataLen)
{
  unsigned iAPDataPtr = 0;
  unsigned char HIDReport[HID_REPORT_LENGTH];
  int IFNum;
  
  // Set report ID
  HIDReport[0] = favhidreport;
  
  // Set link control byte to set start
  HIDReport[1] = 0x2;
  
  if (hid_find_audiostreaming_if(&IFNum))
    return 1;
  
  while (iAPDataLen > 0)
  {
    int templen = iAPDataLen;
    
    if (templen > hidreports[favhidreport] - 2)
    {
      templen = hidreports[favhidreport] - 2;
    }
    else
    {
      // Last packet
      HIDReport[1] &= ~(0x2); // No more to follow
      
      // Fill empty report space with zeros
      memset(&HIDReport[2+templen], 0x0, hidreports[favhidreport] - 2 - templen);
    }
    
    memcpy(&HIDReport[2], &iAPData[iAPDataPtr], templen);
    iAPDataPtr += templen;
    iAPDataLen -= templen;
    if (USBLLD_ControlTransferOut(deviceAddress,
        BRT_TYPE_CLASS, BRT_RECIPIENT_INTERFACE,
        HID_SET_REPORT, (HID_REPORT_TYPE_OUTPUT<<8) | (favhidreport) , IFNum, hidreports[favhidreport], (char*)HIDReport))
    {
      vprintstrln("Buffer overflow");
    }
    usb_buf_wait_for_control();
    
    // Now a continuation
    HIDReport[1] |= 0x1;
  }
  
  return 0;
}

// NOTE: This size will need to be increased if authenticating iPod
#define MAX_IAP_RECEIVE_PACKET 64
unsigned char iPodHIDRxPacketData[MAX_IAP_RECEIVE_PACKET];
unsigned iPodHIDRxPacketPtr = 0;

/*
 * iPodHIDRx:
 * Call when a packet is received over the HID abstraction layer
 */
void iPodHIDRx(chanend c, char data[], int datalen, int ackstate)
{
  int reportID, linkControlByte;  
  
  if (ackstate == USB_TRANS_ACKSTATE_ACK)
  {
    // Sanity check data size
    if (datalen > 2)
    {
      
      reportID = data[0];
      linkControlByte = data[1];
      if (!(linkControlByte & 1))
      {
        // Packet is first in a set
        if (iPodHIDRxPacketPtr > 0)
        {
          // iAP packets received
          iAP_rx(c, iPodHIDRxPacketData, iPodHIDRxPacketPtr);
          // Reset ptr
          iPodHIDRxPacketPtr = 0;
        }
      }
      
      // Sanity check for overflow
      if (iPodHIDRxPacketPtr + datalen > MAX_IAP_RECEIVE_PACKET)
      {
        vprintstrln("iAP Receive buffer overflow");
        // Reset ptr
        iPodHIDRxPacketPtr = 0;
        USBLLD_readfinished();
        return;
      }
      
      // Append data to current iAP packet
      memcpy(&iPodHIDRxPacketData[iPodHIDRxPacketPtr], &data[2], datalen - 2);
      iPodHIDRxPacketPtr += datalen - 2;
      
      if (iPodHIDRxPacketPtr >= MAX_IAP_RECEIVE_PACKET)
      {
        vprint("HID Receive buffer overflow");
      }
      
      if (!(linkControlByte & 2))
      {
        // Packet is last in a set
        if (iPodHIDRxPacketPtr > 0)
        {
          // iAP packet(s) received
          iAP_rx(c, iPodHIDRxPacketData, iPodHIDRxPacketPtr);
          // Reset ptr
          iPodHIDRxPacketPtr = 0;
        }
      }    
    }
  }
  
  USBLLD_readfinished();
}

/*
 * iPodHIDParseDescriptor:
 * Called to parse the configuration descriptor
 */
int iPodHIDParseDescriptor(char data[], int datalen)
{
  s_hid_descriptor *hiddesc;
    
  // Sanity check len
  if (datalen <= 0)
    return 1;
  hiddesc = (s_hid_descriptor *)data;
  
  if (datalen != hiddesc->bLength)
    return 1;
  
  // Sanity check descriptor type
  if (hiddesc->bDescriptorType != 0x21)
    return 1;
  
  // Sanity check number of descriptors
  if (hiddesc->bNumDescriptors != 1)
    return 1;
  
  if (hiddesc->bDescriptorType0 == HID_DESCRIPTOR_REPORT)
  {
    int datalen;
    char *dataptr;
    int currentreportid = -1;
    UsbTransfer *usbTrans;
    // Receive report descriptor
    
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_INTERFACE, BREQ_GET_DESCRIPTOR, HID_DESCRIPTOR_REPORT << 8, 0x2, hiddesc->bDescriptorLength0);
    WaitForResponse(&usbTrans);

    datalen = usbTrans->dataLen;
    dataptr = (char *)usbTrans->data;
    
    while (datalen > 0)
    {
      // We look for short item report_ids, followed by report_counts
      if (dataptr[0] == 0xFE)
      {
        // Long item, skip it
        datalen -= dataptr[1]+1;
        dataptr += dataptr[1]+1;
      }
      else
      {
        int tag = (dataptr[0] >> 4) & 0xF;
        int type = (dataptr[0] >> 2) & 0x3;
        int size = (dataptr[0] >> 0) & 0x3;
        
        if (type == 0x01)
        {
          // Global item
          switch (tag)
          {
          case 0x7:
            // Report Size
            if (dataptr[1] != 0x8)
              return 1;
            break;
          case 0x8:
            // Report ID
            currentreportid = dataptr[1];
            break;
          case 0x9:

            // Report count
            if (currentreportid == -1)
              return 1;
            else
            {
              if (currentreportid < MAX_NUM_HID_REPORTS) {
                hidreports[currentreportid] = dataptr[1];
                if (dataptr[1] > 0) {
                  favhidreport = currentreportid;
                }
              }
              currentreportid = -1;
            }
          }
        }
        
        datalen -= size + 1;
        dataptr += size + 1;
      }
    }
    USBLLD_readfinished();
    
  }

  return 0;
}
