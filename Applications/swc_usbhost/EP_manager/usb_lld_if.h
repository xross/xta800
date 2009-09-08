#ifndef __USB_LLD_H__
#define __USB_LLD_H__

#include "bufferManager.h"

void USBLLD_ControlTransferIn(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength
    );

void USBLLD_ControlTransferOut(
    int addr,
    int bmRequestType_type, int bmRequestType_recipient,
    int bRequest, int wValue, int wIndex, int wLength, unsigned char data[]
    );

void USBLLD_InterruptTransfer_In(
    int addr, int ep,
    int maxdataLen);

void USBLLD_InterruptTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

void USBLLD_IsoTransfer_In(
    int addr, int ep,
    int maxdataLen);

void USBLLD_IsoTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

void USBLLD_BulkTransfer_In(
    int addr, int ep,
    int maxdataLen);

void USBLLD_BulkTransfer_Out(
    int addr, int ep,
    int dataLen, char data[]);

#if defined __XC__
void USBLLD_RxLoop(UsbTransfer &usbTrans);
#elif defined __STDC__
void USBLLD_RxLoop(UsbTransfer *usbTrans);
#endif


#endif // __USB_LLD_H__