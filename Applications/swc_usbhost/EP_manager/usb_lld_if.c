#include "usb_lld_if.h"
#include "bufferManager.h"
#include "string.h"

void USBLLD_ControlTransferIn(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength
    )
{
  UsbTransfer usbTrans;
  usbTrans.dataLen = wLength;
  usbTrans.direction = BRT_DIRECTION_FROM_DEV;
  usbTrans.transferType = USB_TRANSTYPE_CONTROL;
  usbTrans.address = addr;
  usbTrans.endpoint = 0;
  usbTrans.setupPacket.bmRequestType.Direction = 1;
  usbTrans.setupPacket.bmRequestType.Type = bmRequestType_type;
  usbTrans.setupPacket.bmRequestType.Recipient = bmRequestType_recipient;
  usbTrans.setupPacket.bRequest = bRequest;
  usbTrans.setupPacket.wValue = wValue;
  usbTrans.setupPacket.wIndex = wIndex;
  usbTrans.setupPacket.wLength = wLength;
  ep_add((char *)&usbTrans);
}

void USBLLD_ControlTransferOut(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength, unsigned char data[]
    )
{
  UsbTransfer usbTrans;  
  usbTrans.dataLen = wLength;
  usbTrans.direction = BRT_DIRECTION_TO_DEV;
  usbTrans.transferType = USB_TRANSTYPE_CONTROL;
  usbTrans.address = addr;
  usbTrans.endpoint = 0;
  usbTrans.setupPacket.bmRequestType.Direction = 0;
  usbTrans.setupPacket.bmRequestType.Type = bmRequestType_type;
  usbTrans.setupPacket.bmRequestType.Recipient = bmRequestType_recipient;
  usbTrans.setupPacket.bRequest = bRequest;
  usbTrans.setupPacket.wValue = wValue;
  usbTrans.setupPacket.wIndex = wIndex;
  usbTrans.setupPacket.wLength = wLength;
  memcpy(usbTrans.data, data, wLength);
  ep_add((char *)&usbTrans);
}

void USBLLD_InterruptTransfer_In(
    int addr, int ep,
    int maxdataLen)
{
  UsbTransfer usbTrans;  
  usbTrans.direction = BRT_DIRECTION_TO_DEV;
  usbTrans.transferType = USB_TRANSTYPE_INTERRUPT;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = maxdataLen;
  ep_add((char *)&usbTrans);
}

void USBLLD_InterruptTransfer_Out(
    int addr, int ep,
    int dataLen, char data[])
{
  UsbTransfer usbTrans;  
  usbTrans.direction = BRT_DIRECTION_FROM_DEV;
  usbTrans.transferType = USB_TRANSTYPE_INTERRUPT;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = dataLen;
  memcpy(usbTrans.data, data, dataLen);
  ep_add((char *)&usbTrans);
}

void USBLLD_IsoTransfer_In(
    int addr, int ep,
    int maxdataLen)
{
  UsbTransfer usbTrans;  
  usbTrans.direction = BRT_DIRECTION_FROM_DEV;
  usbTrans.transferType = USB_TRANSTYPE_ISO;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = maxdataLen;
  ep_add((char *)&usbTrans);
}

void USBLLD_IsoTransfer_Out(
    int addr, int ep,
    int dataLen, char data[])
{
  UsbTransfer usbTrans;
  usbTrans.direction = BRT_DIRECTION_TO_DEV;
  usbTrans.transferType = USB_TRANSTYPE_ISO;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = dataLen;
  memcpy(usbTrans.data, data, dataLen);
  ep_add((char *)&usbTrans);
}

void USBLLD_BulkTransfer_In(
    int addr, int ep,
    int maxdataLen)
{
  UsbTransfer usbTrans;  
  usbTrans.direction = BRT_DIRECTION_FROM_DEV;
  usbTrans.transferType = USB_TRANSTYPE_BULK;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = maxdataLen;
  ep_add((char *)&usbTrans);
}

void USBLLD_BulkTransfer_Out(
    int addr, int ep,
    int dataLen, char data[])
{
  UsbTransfer usbTrans;
  usbTrans.direction = BRT_DIRECTION_TO_DEV;
  usbTrans.transferType = USB_TRANSTYPE_BULK;
  usbTrans.address = addr;
  usbTrans.endpoint = ep;
  usbTrans.dataLen = dataLen;
  memcpy(usbTrans.data, data, dataLen);
  ep_add((char *)&usbTrans);
}

void USBLLD_RxLoop(UsbTransfer *usbTrans)
{
  while (1)
  {
    if (!ep_remove((char *)usbTrans))
      return;
  }
}

