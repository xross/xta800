#include "iAP.h"
#include "device.h"
#include "chanabs.h"

#include "usb_descriptors.h"
#include "epManager.h"
#include "UsbHostLLD.h"

void iAP_ctrlLingo(streaming_chanend cCtrl, int timenow)
{
  int i;
  int cmd;
  
  cmd = in(cCtrl);
  if (cmd == 0)
  {
    for (i = 0; i < nDeviceInterfaces; i++)
    {
      if (deviceInterfaces[i].alternateSetting)
      {
        UsbTransfer *usbTrans2;
        USBLLD_ControlTransferInDir(deviceAddress,
            BRT_TYPE_STANDARD, BRT_RECIPIENT_INTERFACE,
            BREQ_SET_INTERFACE, deviceInterfaces[i].alternateSetting, deviceInterfaces[i].interfaceNumber, 0, /* null data */ 0);
        if (WaitForResponse(&usbTrans2))
        {
          vprintstrln ("Set Alternate Failed");
        }
        deviceInterfaces[i].alternateSetting |= 2;
        USBLLD_readfinished();
      }
    }  
    out(cCtrl, 0);
  }
  else
  {
    deviceConnected = 0;
    out(cCtrl, 0);
  }
}
