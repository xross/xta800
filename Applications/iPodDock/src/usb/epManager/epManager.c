/*
 * USB Host Endpoint Manager
 * Talks to USB LLD via buffer manager
 */

#include <xccompat.h>
#include "chanabs.h"
#include "usb_descriptors.h"
#include "epManager.h"
#include "UsbHostLLD.h"
#include "string.h"
#include "global.h"
#include <xs1.h>
#include "xc_support.h"

#include "device.h"
#include "global.h"

int deviceAddress = 1;
DeviceDescriptor devicedesc;

int disconnectflag[NUM_CTRL_CHANENDS];

int dummyrx(streaming_chanend c, unsigned char data[], int datalen, int ackstate) {return 0;}
int dummyfn(unsigned char data[], int datalen) {return 0;}
int dummyctrl(streaming_chanend c, int v, int timenow) {return 0;}
int dummytimeout(streaming_chanend c, int timenow) {return 0;}
int dummyflush(streaming_chanend c) {return 0; }
void dummyinit(void) {return;}

int deviceConnected;

/*
 * WaitForResponse:
 * Wait for a USB Transfer to complete
 */
__attribute__((noinline)) int WaitForResponse(UsbTransfer **usbTrans)
{
  char sofstate;
  
  while (1)
  {   
    if (!USBLLD_removesof((char *)&sofstate))
    {
      if (sofstate == 3)
      {
        deviceConnected = 0;
        return 2;
      }
    }
    
    if (!USBLLD_getreadhandle(usbTrans))
    {
      if ((*usbTrans)->ackState == USB_TRANS_ACKSTATE_ACK)
      {
        return 0;
      }
      else
      {
        return 1;
      }
    }
  }

  return 0;
}

 /*
  * USBHostStackParseDeviceDescriptor:
  * Check the device Descriptor matches the expected values for the device.
  * Returns 0 on success, 1 for error.
  */
int USBHostStackParseDeviceDescriptor(UsbTransfer *usbTrans, int *numDeviceConfigs)
{ 
  memcpy((char *)&devicedesc, usbTrans->data, usbTrans->dataLen);
  
  if (((devicedesc.idVendor & vendorIDMask) != vendorID) || ((devicedesc.idProduct & productIDMask) != productID))
  {
    vprintstr("ERROR: Unsupported Product. Vendor id: 0x");
    vprinthexln(devicedesc.idVendor);
    vprintstr("Product id: 0x");
    vprinthexln(devicedesc.idProduct);
    return 1;
  }
  else
  {
    *numDeviceConfigs = devicedesc.bNumConfigurations;
    return 0;
  }
}

/*
 * USBHostStackParseConfigurationDescriptor:
 * Parse the device configuration descriptor.
 * Check for the expected number of device interfaces and endpoints.
 * nDeviceInterfaces is defined externally as it is device specific.
 */
extern int ms_state;
int USBHostStackParseConfigurationDescriptor(UsbTransfer *usbTrans, int time, int configNum)
{
  int i;
  char *usbTransPtr;
  int currentIF = -1;
  int dataLen;
  char descriptorData[256];
  int configSet = 0;
  ConfigDescriptor *configdesc;
  InterfaceDescriptor *ifdesc;
  EndpointDescriptor *epdesc;

  dataLen = usbTrans->dataLen;
  memcpy(descriptorData, (char *)(usbTrans->data), dataLen);
  usbTransPtr = (char *)descriptorData;  
  USBLLD_readfinished();
  
  // Wipe all IFs and EPs found flags
  for (i = 0; i < nDeviceInterfaces; i++)
  {
    deviceInterfaces[i].interfaceFound = 0;
  }
  for (i = 0; i < nDeviceEPs; i++)
  {
    deviceEPs[i].epfound = 0;
  }
  
  while (dataLen > 0)
  {
    // Check for zero length
    if (usbTransPtr[0] == 0)
    {
      vprint("Error on config parse");
    }

    // Switch on descriptor type
    switch (usbTransPtr[1])
    {
    
      case (0x2):
        
        // Configuration Descriptor
        configdesc = (ConfigDescriptor *)usbTransPtr;

        // Sanity check number of interfaces
        if (configdesc->bNumInterfaces != nDeviceInterfaces)
        {
          return 1;
        }
        break;
        
      case (0x4):
        
        // Interface Descriptor
        ifdesc = (InterfaceDescriptor *)usbTransPtr;
        currentIF = -1;
        
        // Seek to match interface with device.h
        for (i = 0; i < nDeviceInterfaces; i++)
        {
          if (ifdesc->bInterfaceClass == (deviceInterfaces[i].interfaceClass & 0xFF) &&
              ifdesc->bInterfaceSubClass == (deviceInterfaces[i].interfaceSubClass & 0xFF) &&
              ifdesc->bInterfaceProtocol == (deviceInterfaces[i].interfaceProtocol & 0xFF) &&
              ifdesc->bNumEndpoints == deviceInterfaces[i].numEndpoints &&
              deviceInterfaces[i].interfaceFound == 0)
          {
            deviceInterfaces[i].interfaceFound = 1;
            deviceInterfaces[i].interfaceNumber = ifdesc->bInterfaceNumber;
            currentIF = i;
            break;
          }
        }
          
        break;
        
      case (0x5):
    
        // Endpoint Descriptor
        epdesc = (EndpointDescriptor *)usbTransPtr;  
        
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
            deviceEPs[i].interval = epdesc->bInterval; // mS frames
            deviceEPs[i].nextInterruptPacketTime = epdesc->bInterval;
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
          if (!configSet)
          {
            {
              // Set iPod configuration
              USBLLD_ControlTransferInDir(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_SET_CONFIGURATION, configNum+1, 0, 0, /* null data */ 0);
              if (WaitForResponse(&usbTrans))
              {
                USBLLD_readfinished();
                return 7;
              }
              USBLLD_readfinished();
            }
            configSet = 1;
          }
          (*deviceInterfaces[currentIF].parseDescriptor)(usbTransPtr, usbTransPtr[0]);
        }

        break;        
      default:
        break;
    }
    
    // Now skip      
    dataLen -= usbTransPtr[0];
    usbTransPtr += usbTransPtr[0];
  }
  
  // Check now that all IFs and EPs were found
  for (i = 0; i < nDeviceInterfaces; i++)
  {
    if (!deviceInterfaces[i].interfaceFound)
    {
      return 1;
    }
  }
  for (i = 0; i < nDeviceEPs; i++)
  {
    if (!deviceEPs[i].epfound)
    {
      return 1;
    }
  }
  
  if (!configSet)
  {
    {
      // Set iPod configuration
      USBLLD_ControlTransferInDir(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_SET_CONFIGURATION, configNum+1, 0, 0, /* null data */ 0);
      if (WaitForResponse(&usbTrans))
      {
        USBLLD_readfinished();
        return 7;
      }
      USBLLD_readfinished();
    }
    configSet = 1;
  }

  return 0;
}

int cGlobalClients[NUM_CTRL_CHANENDS];
/*
 * usb_buf_pull:
 * Checks for returned packets from LLD. This includes SOFs as
 * well as responses to other requests.
 * Returns 0 on packet detected and 1 on packet not detected
 */
int usb_buf_pull(int *transType)
{ 
  int i;
  char sofstate;
  int retval = 1;
  UsbTransfer *usbTrans;

  *transType = 0;
  // Check for SOFs
  if (!USBLLD_removesof((char *)&sofstate))
  {
    if (sofstate == 3)
    {
      printstrln("p1");
      deviceConnected = 0;
    }
    else
    {
      int time = getTime();
      
      // Look for timeouts
      for (i = 0; i < nDeviceInterfaces; i++)
      {
        (*deviceInterfaces[i].timeout)(cGlobalClients[i], time);
      }
      
      // Poll isochronous and interrupt endpoints at the required interval
      for (i = 0; i < nDeviceEPs; i++)
      {
        if (deviceEPs[i].dir == BRT_DIRECTION_FROM_DEV && deviceEPs[i].transferType == EPTransferType_Iso && (deviceInterfaces[deviceEPs[i].interfaceNumber].alternateSetting & 2))
        {
          if (deviceEPs[i].nextInterruptPacketTime > 0)
          {
            deviceEPs[i].nextInterruptPacketTime--;
            
            // Check if time for transmitting isochronous request
            if (deviceEPs[i].nextInterruptPacketTime == 0)
            {
              USBLLD_IsoTransfer_In(deviceAddress, deviceEPs[i].EPnum, deviceEPs[i].maxPacketSize);                    
            }
          }
        }
        else if (deviceEPs[i].dir == BRT_DIRECTION_FROM_DEV && deviceEPs[i].transferType == EPTransferType_Interrupt)
        {
          if (deviceEPs[i].nextInterruptPacketTime > 0)
          {
            deviceEPs[i].nextInterruptPacketTime--;
            // Check if time for transmitting interrupt request
            if (deviceEPs[i].nextInterruptPacketTime == 0)
            {
              USBLLD_InterruptTransfer_In(deviceAddress, deviceEPs[i].EPnum, deviceEPs[i].maxPacketSize);
            }
          }
        }
      }      
    }
  }

  
  // Check rx buffer to see if any transfers have completed
  while (!USBLLD_getreadhandle(&usbTrans))
  {
    if ((usbTrans)->ackState == USB_TRANS_ACKSTATE_IGNORE)
    {
      deviceConnected = 0;
    }
    else
    {
      // Check address matches the expected device address
      if ((usbTrans)->address == deviceAddress)
      {
        int i;
        
        if ((usbTrans)->transferType == USB_TRANSTYPE_CONTROL)
        {
          *transType |= 1;
          USBLLD_readfinished();
        }
        else
        { 
          // Look up EP
          for (i = 0; i < nDeviceEPs; i++)
          {
            if ((usbTrans)->endpoint == deviceEPs[i].EPnum)
            {
              break;
            }
          }
  
          if (i < nDeviceEPs)
          {
            (*deviceEPs[i].rx)(cGlobalClients[deviceEPs[i].interfaceNumber], (char*)(usbTrans)->data, (usbTrans)->dataLen, (usbTrans)->ackState);
                      
            if (deviceEPs[i].transferType == EPTransferType_Interrupt || deviceEPs[i].transferType == EPTransferType_Iso)
            {
              deviceEPs[i].nextInterruptPacketTime = deviceEPs[i].interval;
            }
          }
          else
          {
            USBLLD_readfinished();
          }
        }        
      }
    }
    retval = 0;

  }
  
  return retval;
}

/*
 * usb_buf_wait_for_control
 * Sit waiting for a Control Transfer to complete
 */
void usb_buf_wait_for_control()
{
  int transType;
  
  while (deviceConnected)
  {
    while (usb_buf_pull(&transType) && deviceConnected);    
    if (transType == 1)
    {
      break;
    }
    
    if (!deviceConnected)
      break;
  }
}

/*
 * USBHostStack_main:
 * This function is the mid-level of the USB stack. It tries to enumerate
 * a newly connected ipod, or enter a wait state on ipod disconnect.
 * In error cases it will try to power cycle the ipod and reconnect.
 *
 * Once enumeration has successfully completed, this layer stays in a loop
 * accepting requests from the audio layer and passing them down to the LLD.
 * Disconnect of the ipod is also detected and resets both this level and the LLD.
 *
 */
// Warning: This value will change and must be manually tested
#pragma stackfunction 1000
int USBHostStack_main(streaming_chanend cClients[NUM_CTRL_CHANENDS], chanend cUSBCtrl)
{
  int now, i;
  int configNum;
  UsbTransfer *usbTrans;
  int retlen;
  int numDeviceConfigs;
  
  for (i = 0; i  < NUM_CTRL_CHANENDS; i++)
    cGlobalClients[i] = cClients[i];
  deviceConnected = 1;
  
  // Set device address - this is unacknowledged
  USBLLD_ControlTransferInDir(0, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_SET_ADDRESS, deviceAddress, 0, 0, /* null data */ 0);
  if (WaitForResponse(&usbTrans))
  {
    USBLLD_readfinished();
    return 1;
  }
  USBLLD_readfinished();
  // wait 200ms before doing anything else
  pause(200*XS1_TIMER_KHZ);

 // Get Device Descriptor for size
  USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_DEVICE << 8, 0, 4);
  if (WaitForResponse(&usbTrans))
  {
    USBLLD_readfinished();
    return 2;
  }
  
  retlen = (usbTrans->data[0] & 0xFF);
  USBLLD_readfinished();
  USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, DESC_TYPE_DEVICE << 8, 0, retlen);
  if (WaitForResponse(&usbTrans))
  {
    USBLLD_readfinished();
    return 2;
  }

  if (USBHostStackParseDeviceDescriptor(usbTrans, &numDeviceConfigs))
  {
    // If the descriptor doesn't match the expected, reset and retry.
    USBLLD_readfinished();
    return 4;
  }
  USBLLD_readfinished();

  for (configNum = 0; configNum < numDeviceConfigs; configNum++)
  {        
    for (i = 0; i < nDeviceInterfaces; i++)
    {
      deviceInterfaces[i].interfaceFound = 0;
      deviceInterfaces[i].alternateSetting &= 1;
    }
    for (i = 0; i < nDeviceEPs; i++)
    {
      deviceEPs[i].epfound = 0;
    }

    // Get size of configuration descriptor
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, (DESC_TYPE_CONFIGURATION << 8) | configNum, 0, 4);
    if (WaitForResponse(&usbTrans))
    {
      USBLLD_readfinished();
      return 5;
    }
  
    retlen = (usbTrans->data[0] >> 16);
    USBLLD_readfinished();
    // Get config descriptor
    USBLLD_ControlTransferIn(deviceAddress, BRT_TYPE_STANDARD, BRT_RECIPIENT_DEVICE, BREQ_GET_DESCRIPTOR, (DESC_TYPE_CONFIGURATION << 8) | configNum, 0, retlen);
    if (WaitForResponse(&usbTrans))
    {
      USBLLD_readfinished();
      return 6;
    }
    
    now = getTime();

    if (!USBHostStackParseConfigurationDescriptor(usbTrans, now, configNum))
    {
      break;
    }
  }

  if (configNum==numDeviceConfigs)
  {
    vprint("No configurations matched");
  }
  
  // 100mS pause  
  pause(200*XS1_TIMER_KHZ);
  
  // Init
  for (i = 0; i < nDeviceInterfaces; i++)
  {
    (*deviceInterfaces[i].init)();
  }
    
  // Main usb loop. Poll the control channels for requests, and pass to the LLD.
  // If the device disconnects, quit.
  while (1)
  {
    // Check control channels
    int ctrlchan;
    int val;
    int transType;
    
    pollChans(cClients, &ctrlchan, &val);
    if (ctrlchan != -1)
    {
      (*deviceInterfaces[ctrlchan].ctrlRequest)(cClients[ctrlchan], val, getTime());

      if (!deviceConnected)
        break;
    }

    usb_buf_pull(&transType);

    if (!deviceConnected)
      break;
  }

  // Flush
  for (i = 0; i < nDeviceInterfaces; i++)
  {
    (*deviceInterfaces[i].flush)(cGlobalClients[i]);
  }
  
  endChans(cClients, disconnectflag);
  
  return 12;
}

/*
 * USBHostStack
 * Provides EndPoint management and client channels
 * Wraps USBHostStack_main allowing it to exit on error and restart
 * the ipod. Because a hard (power off) reset is done, enumeration
 * has to restart
 */
void USBHostStack(streaming_chanend cClients[NUM_CTRL_CHANENDS], chanend cUSBCtrl)
{
  UsbTransfer *usbTrans;
  
  // Receive info on which clients require disconnect notification
  startChans(cClients, disconnectflag);
  
  while (1)
  {
    int status;
    
    // Run enumeration
    status = USBHostStack_main(cClients, cUSBCtrl);

    // Disconnection event
    vprintintln(status);
    vprintstrln("reset");
    USB_Ctrl(cUSBCtrl, USB_CMD_POWER_CYCLE);

    // 100mS pause  
    pause(100*XS1_TIMER_KHZ);
    
    // Drain buffer from any old transactions that have appeared meantime,
    while(!USBLLD_getreadhandle(&usbTrans))
    {
      USBLLD_readfinished();
    }

  };
}
