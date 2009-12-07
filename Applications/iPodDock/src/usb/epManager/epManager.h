#ifndef EP_MANAGER_H_
#define EP_MANAGER_H_

#include <xccompat.h>

#include "UsbHostLLD.h"
#include "global.h"
#include "xc_support.h"

extern int deviceConnected;

/*
 * USBHostStack
 * Provides EndPoint management and client channels
 */
void USBHostStack(streaming_chanend cClients[NUM_CTRL_CHANENDS], chanend cUSBCtrl);

void usb_buf_wait_for_control();
#ifdef __STDC__
int WaitForResponse(UsbTransfer **usbTrans);
#endif

#endif /*EP_MANAGER_H_*/

