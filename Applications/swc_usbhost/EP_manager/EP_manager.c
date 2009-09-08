/*
 * USB Host Endpoint Manager
 * Talks to Low-level Pinwiggler via buffer manager
 */

#include "bufferManager.h"
#include <xccompat.h>
#include "usb_descriptors.h"
#include "usb_host.h"
#include "EP_manager.h"
#include "usb_lld_if.h"
#include "string.h"

// Device is described in here
#include "device.h"

int deviceAddress = 106;
DeviceDescriptor devicedesc;

int dummyfn(int datalen, unsigned char data[])
{
  
}

int USBHostStackParseDeviceDescriptor(UsbTransfer *usbTrans)
{ 
  memcpy((char *)&devicedesc, usbTrans->data, usbTrans->dataLen);
  
  if (devicedesc.idVendor != APPLE_VENDOR_ID || ((devicedesc.idProduct & 0xff00) != 0x1200))
  {
    printstr("ERROR: Unsupported Product. Vendor id: 0x");
    printhexln(devicedesc.idVendor);
    printstr("Product id: 0x");
    printhexln(devicedesc.idProduct);
    return 1;
  }
  else
  {
    return 0;
  }
}

int USBHostStackParseConfigurationDescriptor(UsbTransfer *usbTrans)
{
  int i;
  char *usbTransPtr = (char *)(usbTrans->data);
  int currentIF = -1;
  ConfigDescriptor *configdesc;
  InterfaceDescriptor *ifdesc;
  EndpointDescriptor *epdesc;
  
  while (usbTrans->dataLen)
  {
    // Switch on descriptor type
    switch (usbTransPtr[1])
    {
    
      case (0x2):
        // Configuration Descriptor
        configdesc = (ConfigDescriptor *)usbTransPtr;
        usbTransPtr += configdesc->bLength;
        // Sanity check num interfaces
        if (configdesc->bNumInterfaces != nDeviceInterfaces)
          return 1;
        // TODO: Other configuration validation
        break;
        
      case (0x4):
        // Interface Descriptor
        ifdesc = (InterfaceDescriptor *)usbTransPtr;
        usbTransPtr += ifdesc->bLength;
        currentIF = -1;
        
        // Seek to match interface with device.h
        for (i = 0; i < nDeviceInterfaces; i++)
        {
          if (ifdesc->bInterfaceClass == deviceInterfaces[i].interfaceClass &&
              ifdesc->bInterfaceSubClass == deviceInterfaces[i].interfaceSubClass &&
              ifdesc->bNumEndpoints == deviceInterfaces[i].numEndpoints &&
              deviceInterfaces[i].interfaceFound == 0)
          {
            deviceInterfaces[i].interfaceFound = 1;
            deviceInterfaces[i].interfaceNumber = ifdesc->bInterfaceNumber;
            deviceInterfaces[i].alternateSetting = ifdesc->bAlternateSetting;
            currentIF = i;
            break;
          }
        }
        break;
        
      case (0x5):
        // Endpoint Descriptor
        epdesc = (EndpointDescriptor *)usbTransPtr;
        usbTransPtr += epdesc->bLength;
        
        // Seek to match interface with device.h
        for (i = 0; i < nDeviceEPs; i++)
        {
          if (currentIF == deviceEPs[i].interfaceNumber &&
              epdesc->bmAttributes == 
                (deviceEPs[i].transferType |
                 (deviceEPs[i].syncType << 2) |
                 (deviceEPs[i].usageType << 4)) &&
              (epdesc->bEndpointAddress >> 7) == deviceEPs[i].dir)
          {
            deviceEPs[i].epfound = 1;
            deviceEPs[i].EPnum = epdesc->bEndpointAddress & 0xF;
            deviceEPs[i].maxPacketSize = epdesc->wMaxPacketSize;
            deviceEPs[i].interval = epdesc->bInterval;
            break;
          }
        }
        if (i == nDeviceEPs)
        {
          // Ignore unknown EP
        }
        break;
        
      case (0x21):
      case (0x24):
      case (0x25):
        // Class specific descriptor
        // Pass to class specific descriptor processor
        if (currentIF != -1)
        {
#pragma stackfunction 10
          (*deviceInterfaces[currentIF].parseDescriptor)(usbTransPtr[0], usbTransPtr);
        }
        // Now skip
        usbTransPtr += usbTransPtr[0];
        break;        
      default:
        usbTransPtr += usbTransPtr[0];
        break;
    }
  }
}

// TODO: Will become array of channels with bugfix compiler
void USBHostStack(chanend cCtrl)
{
  int now;
  UsbTransfer usbTrans;

  while (1)
  {
    int connected = 1;
    
    // Set device address - this is unacknowledged
    USBLLD_ControlTransferIn(0, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_SET_ADDRESS, deviceAddress, 0, 0);
    USBLLD_RxLoop(&usbTrans);
    
    // Get Device Descriptor
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_DEVICE, 0, 18);
    USBLLD_RxLoop(&usbTrans);
    if (USBHostStackParseDeviceDescriptor(&usbTrans))
    {
      // panic
      while (1);
    }

    // Get size of configuration descriptor
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_CONFIGURATION, 0, 4);
    USBLLD_RxLoop(&usbTrans);

    // Get config descriptor
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_CONFIGURATION, 0, usbTrans.data[2] + (usbTrans.data[3] << 8));
    USBLLD_RxLoop(&usbTrans);
    if (USBHostStackParseConfigurationDescriptor(&usbTrans))
    {
      // Failed configuration, try config 2
      USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_CONFIGURATION, 0, usbTrans.data[2] + (usbTrans.data[3] << 8));
          USBLLD_RxLoop(&usbTrans);
    }
    
    {
      while (connected)
      {  
        int i;
        // Look for any interrupt endpoints
        for (i = 0; i < nDeviceEPs; i++)
        {
          if (deviceEPs[i].transferType == EPTransferType_Interrupt)
          {
            // Check if time for transmitting interrupt request
            // TODO: Initialise NIPT
            if (now - deviceEPs[i].nextInterruptPacketTime > 0)
            {
              // Generate interrupt transfer
              if (deviceEPs[i].dir == 1)
              {
                USBLLD_InterruptTransfer_In(deviceAddress, deviceEPs[i].EPnum, deviceEPs[i].maxPacketSize);
              }
              else
              {
                // TODO: USBLLD_InterruptTransfer_Out(0, deviceEPs[i].EPnum);              
              }
              //TODO: deviceEPs[i].nextInterruptPacketTime += endPoints[epnum].InterruptTimeInterval;
            }
          }
        }
        
        // Check rx buffer to see if any transfers have completed
        if (!ep_remove((char*)&usbTrans))
        {
          // Sanity check address
          if (usbTrans.address == 0)
          {
            int i;
            
            // Look up EP
            for (i = 0; i < nDeviceEPs; i++)
            {
              if (usbTrans.endpoint == deviceEPs[i].EPnum)
              {
                break;
              }
            }
            if (i < nDeviceEPs)
            {
              // Sanity check endpoint number
              if (deviceEPs[i].rx != 0)
              {
#pragma stackfunction 10
                (*deviceEPs[i].rx)(usbTrans.dataLen, usbTrans.data);
              }
            }
          }
        }
        
        
        // Check control channels
        {
          int ctrlchan;
          
          //ctrlchan = pollChans(cCtrl);
          
          if (ctrlchan >= 0)
          {
            // TODO: Call fn ptr associated with channel ctrlchan 
          }
        }
      }
    }
  }
}