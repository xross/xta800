/* USB FS Host low level driver.
 * Author    : Jon Taylor
 * (C) XMOS 2009
 */
#include <xs1.h>

#include "print.h"
#include "XUD_UIFM_Functions.h"
#include "XUD_UIFM_Defines.h"
#include "uifm_otg_defines.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"
#include "usb_time_fs.h"
#include "usb_host.h"
#include <platform.h>

unsigned endpoint_datapid[16]; // Value of next data_pid for each endpoint.
extern in  port p_usb_clk;      
extern out port reg_write_port;
extern in  port reg_read_port; 
extern in  port flag0_port;  
extern in  port flag1_port;
extern in  port flag2_port; 
extern out buffered port:32  p_usb_txd ; 
extern buffered  port:32 p_usb_rxd       ;
extern out port p_usb_rst      ; 

extern clock clk;

void usbHostLLD () {
  
  // Manage timings for SOFs while dealing with standard packets too.
  // This will eventually need modifying to take the audio clock.
  timer sof_timer;
  unsigned sof_time, status;
  unsigned frame_count = 0;
  unsigned tx_data[128];
  UsbTransfer usbTrans;
  unsigned tx_time;
  // Only applicable for BULK endpoints, but use of array makes finding the right one quicker.
  // properties and data_pid toggling.

  // Do init/enumeration.
  set_clock_on(clk);
  set_clock_ref(clk);
  set_port_clock(p_usb_rst, clk);
  start_clock(clk);

  /* Reset port states */
  set_port_use_on(p_usb_clk);
  set_port_clock(p_usb_clk, clk);
  set_port_use_on(p_usb_txd);
  set_port_use_on(p_usb_rxd);
  set_port_use_on(flag0_port);
  set_port_use_on(flag1_port);
  //  set_port_use_on(flag2_port);
  set_port_use_on(reg_read_port);
  set_port_use_on(reg_write_port);

  /* Reset transceiver */
  XUD_PhyReset();

  /* Wait for USB clock (typically 1ms after reset) */
  p_usb_clk when pinseq(1) :> int _;
    p_usb_clk when pinseq(0) :> int _;
      p_usb_clk when pinseq(1) :> int _;
	p_usb_clk when pinseq(0) :> int _;

 p_usb_rxd :> int _; // turn rxd port around prior to setting up.
   


  sof_timer :> sof_time;
  while (1) {
    sof_time += 1000000; // SOF time is 1ms +/- 500ns. Should probably use USB clock for accuracy.

    select {
    case sof_timer when timerafter (sof_time) :> int _ :
      do_sof(frame_count ++);
      break;
    default:
      break;
    };

    // Check for request
    if (lld_remove((usbTrans, char[])) == 0)
    {
      // Check timing before SOF.
      // Also have to save the trans to retry if there's not time before the sof.
      // Packet duration is a combination of length, but also allow for retries etc.

      unsigned current_time, frame_time_remaining;

    sof_timer :> current_time;
      frame_time_remaining = sof_time - current_time; // This is in multiples of 10ns.
      // 12MBit/s -> 83ns bit time. Max packet time is max packet size * bit time +
      // overhead for control and response. 
      // Max length is passed in transfer. Also need to allow for setupPacket.
      tx_time =  usbTrans.dataLen << 4; // bit count * 16 approx eq time.
      frame_time_remaining = frame_time_remaining *10; 

      // @!@!@ When we move to 60Mhz clock this will need adjusting. Audio clock is 28
      // and changeable.
      if (tx_time > frame_time_remaining) {
	// wait until sof time before doing packet
	sof_timer when timerafter (sof_time) :> int _ ;
	  do_sof(frame_count ++);
      };


      // decode + do packet.
      do_lld_packet (usbTrans);

      // Finished?
      lld_add((usbTrans, char[]));
    }
  }
};

int do_lld_packet(UsbTransfer &usbTrans){
  unsigned pid;
  // may also include setup packet.
  if(usbTrans.transferType == USB_TRANSTYPE_CONTROL) {
    unsigned tx_data[2];
    ParseSetupPacket(usbTrans.setupPacket, tx_data);
    do_setup_packet(usbTrans.address, usbTrans.endpoint, tx_data);
  };
  
  // extract packet info for do_packet;
  do_packet(usbTrans.direction, usbTrans.address, usbTrans.endpoint, usbTrans.dataLen, endpoint_datapid[usbTrans.endpoint], (usbTrans.data, unsigned[]));

  if (endpoint_datapid[usbTrans.endpoint] == PID_DATA1) {
    endpoint_datapid[usbTrans.endpoint] = PID_DATA0;
  } else {
    endpoint_datapid[usbTrans.endpoint] = PID_DATA1;
  };
}
