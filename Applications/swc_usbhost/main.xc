/* test harness for FS Host.
 * Eventually want to test HID.
 * Author : Jon Taylor
 * Date   : 20 July 2009
 * Copyright (c) XMOS 2009
 */

#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "XUD_UIFM_Functions.h"
#include "XUD_UIFM_Defines.h"
#include "uifm_otg_defines.h"
#include "XUD_USB_Defines.h"
#include "usb_host.h"
#include "EP_manager.h"

#define USB_CORE 0

on stdcore[USB_CORE] : in port  p_usb_clk       = UIFM_USB_CLK_PORT;
on stdcore[USB_CORE] : out port reg_write_port  = UIFM_REG_WRITE_PORT;
on stdcore[USB_CORE] : in  port reg_read_port   = UIFM_REG_READ_PORT;
on stdcore[USB_CORE] : in  port flag0_port      = UIFM_FLAG_0_PORT;
on stdcore[USB_CORE] : in  port flag1_port      = UIFM_FLAG_1_PORT;
on stdcore[USB_CORE] : in  port flag2_port      = UIFM_FLAG_2_PORT;
on stdcore[USB_CORE] : out buffered port:32 p_usb_txd       = UIFM_TX_DATA_PORT;
on stdcore[USB_CORE] : buffered port:32 p_usb_rxd       = UIFM_RX_DATA_PORT;
on stdcore[USB_CORE] : out port p_usb_rst       = XS1_PORT_1I; // XTAG reset port
on stdcore[USB_CORE] : out port p_test          = XS1_PORT_1B;
on stdcore[USB_CORE] : out port p_test2         = XS1_PORT_1C;
on stdcore[USB_CORE] : clock    clk             = XS1_CLKBLK_3;


void usb_host();

void debug() {
  int i = 1;
  printstrln("foo");
  while (i);
};

void sample_rx_err() {
  unsigned tmp;
  set_port_use_on(flag2_port);
  while (1) {
    flag2_port when pinseq(1) :> tmp;
      printstrln("RXerr detected");
  };
};


void main () {
  chan cCtrl;
  par {
    //      on stdcore[0] : debug();
    //        on stdcore[0] : sample_rx_err();
    //    on stdcore[0] : usb_host();
    on stdcore[0]: USBHostStack(cCtrl);
    on stdcore[0] : usbHostLLD();
  };
};
