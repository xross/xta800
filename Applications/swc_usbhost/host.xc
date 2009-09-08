/* Host functions for USB
 *
 * Author : Jon Taylor
 * Date:  16 July 2009
 * Copyright (C) XMOS 2009

*/

#include <xs1.h>
#include <print.h>

#include "XUD_UIFM_Functions.h"
#include "XUD_UIFM_Defines.h"
#include "uifm_otg_defines.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"
#include "usb_time_fs.h"
#include "usb_host.h"
#include <platform.h>
#include "usb_descriptors.h"
#include <safestring.h>

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

void XUD_PhyReset(void)
{
  //stp_sus_port <: 0;      // TODO Required?
  clearbuf(p_usb_rxd);
  //clearbuf(stp_sus_port); // TODO Required?

  p_usb_rxd <: 0;            // While in reset, drive 0 on data bus and clear port buffers
  clearbuf(p_usb_rxd);
  p_usb_rst <: 0;

  XUD_Sup_Delay(RESET_TIME);
  p_usb_rst <: 1;
}

int device_detect() 
{
  unsigned set_address[2] = {0x006A0500, 0x00000000 };
  unsigned tmp, timeout, time;
  timer t;
  // setup flags reg on linestate
  // On device attachement, D+ pulled up.

  // Detect before vbus turned on?
  // Turn on pulldowns (OTG_CTRL)
  XUD_UIFM_RegWrite (reg_write_port, UIFM_OTG_CONTROL, UIFM_OTG_CONTROL_DPPULLDOWN |UIFM_OTG_CONTROL_DMPULLDOWN );

  // Try doing set address. If device is connected - great. Skip rest.
  // If not, wait on connect.
  timeout = do_packet(PID_SETUP, 0, 0, 8, PID_SETUP, set_address);
  // 0 is success
  if (!timeout) {
    timeout = do_packet(PID_IN, 0, 0, 0, PID_DATA0, set_address); 
     XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, UIFM_REG_PHYCON_SUSPENDM | UIFM_REG_PHYCON_XCVRSELECT |UIFM_REG_PHYCON_TERMSELECT ); 
     // device already attahced. No point waiting on pulls.
    return 1;
  }
    
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_IFM_FLAGS, 0x0);
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, UIFM_IFM_FLAGS_J |UIFM_IFM_FLAGS_K);
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK0,UIFM_IFM_FLAGS_J) ;
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK1,UIFM_IFM_FLAGS_K);


    flag1_port when pinseq(1) :> tmp; // Wait for pullup
    
  // Waiting on a flag port is fine if the device isn't already connected. But if it is
  // at power up, we'll never detect it.
  // VBus should cause RX CMD?


    // Wait 3 ms to do bus reset.
    
 t:> time;
  time += 300000; 
  t when timerafter(time) :> int _;

    //    //  tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
    //  printhexln(tmp);



    // Drive VBUS
    XUD_UIFM_RegWrite(reg_write_port, UIFM_OTG_CONTROL, UIFM_OTG_CONTROL_DRVVBUS);
    
    // Do bus reset?
    // Drive SE0.// Host FS on TS?
    // TX CMD for LS?
    
    // host chirp?
    // xcvrsel = 0, opmode = 2b10, ts = 0
    XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON,  UIFM_REG_PHYCON_SUSPENDM | UIFM_REG_PHYCON_OPMODE_1);

    // Now wait for device chirp (1ms K), wait TWTFS then end reset (send SOF).

  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_IFM_FLAGS, 0x0);
  // clear all sticky bits
    XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, 0);
    // t :> time;
    // t:> timeout;
    //    while ((timeout < (time + T_WFTFS) )) {
    //    t:> timeout;
    //    };

    return 0;
};

void do_init () {
  timer t;
  unsigned time, tmp;
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, UIFM_REG_PHYCON_XCVRSELECT | UIFM_REG_PHYCON_TERMSELECT | UIFM_REG_PHYCON_SUSPENDM);
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_OTG_FLAGS, 0);
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, 0);
      tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
      //      printhexln(tmp); // read SE0 here.
  // TX SE0?
  // Drive TS to 0 and xcvrsel to 0. Opmode -> 10 for chirp
  // Should get an RX CMD back with ls se0.
 t :> time;
  time += 1000000; // pause for detect?
  t when timerafter(time) :> int _;
    XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, UIFM_REG_PHYCON_OPMODE_1 | UIFM_REG_PHYCON_SUSPENDM);
    // Look for RXCMD
    tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
    //        printhexln(tmp); // read k here
    //  wait >2.5us then chirp k for 1ms.
 t:>time;
    time += 250; // pause for detect?
    t when timerafter(time) :> int _;

      tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
      //      printhexln(tmp); // read SE0 here.


 t:> time;
    time += 100000; // pause for detect - 1ms
    t when timerafter(time) :> int _;
      tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
      //      printhexln(tmp); // should show k -chirp.

      // LS reads 0x20 - se0?
      // Switch into normal mode
     XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, UIFM_REG_PHYCON_SUSPENDM | UIFM_REG_PHYCON_XCVRSELECT |UIFM_REG_PHYCON_TERMSELECT ); 


     XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK2, UIFM_IFM_FLAGS_RXERROR);
};


void usb_host() {
  timer t;
  unsigned tmp, time;
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

 p_usb_rxd :> tmp; // turn rxd port around prior to setting up.
    


	/* Configure ports and clock blocks for use with UIFM.  */
	XUD_UIFM_PortConfig(p_usb_clk, reg_write_port, reg_read_port, flag0_port, flag1_port, flag2_port, p_usb_txd, p_usb_rxd) ;
	clearbuf(p_usb_rxd);

	// Turn RX port off to turnaround properly
	//      set_port_use_off(p_usb_rxd);
	//      set_port_use_on(p_usb_rxd);
	//      clearbuf(p_usb_rxd);

	/* Enable UIFM in appropriate mode. Note: Returns value read back */
	XUD_UIFM_Enable(UIFM_MODE);

	/* Set UIFM to DECODE LINESTATE mode (CHKTOKENS disabled since would breaks power signaling) */
	// This is native mode.
	XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, 0x4);

	tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
	//      printhexln(tmp); // read SE0 here.

	if (!device_detect()) {
	  do_init();
	}

	//  printstrln("Run Device detect");
	tmp = 0;
 t:> time;
	while ( tmp < 3200) {
	  do_sof(tmp);
	  //    time += 100000; // pause for sof time - 1ms
	  time += 99920; // pause for sof time - 1ms
	  t when timerafter(time) :> int _;
	    tmp ++;
	}

	     //enumerate_ipod(tmp);

 t:> time;
	  while ( tmp < 10) {
	    do_sof(tmp);
	    time += 100000; // pause for sof time - 1ms
	    t when timerafter(time) :> int _;
	      tmp ++;
	  }


};

unsigned do_sof (unsigned frame_count){
  // Send SOF packet. Assumes ports are available.
  // Byte order?
  unsigned data;
  unsigned sof_crc = crc5_usb(frame_count);
  // sof pid = 0xa5;
  
  // Need to do an outpw of 24 bits for sof.
  data =  (PID_SOF | 0x40)  | ((frame_count & 0x7ff) << 8 ) | ((sof_crc & 0x1f) << 19);
    partout (p_usb_txd, 24, data);
#if 0
  //  printhexln (data);
  p_usb_txd <: (data & 0xff);
  data = data >> 8;
  //  printhexln (data);
  p_usb_txd <: (data & 0xff);
  data = data >> 8;
  //  printhexln (data);
  p_usb_txd <: (data & 0xff);
  data = data >> 8;
  //  printhexln (data);
#endif
};

// Need CRC 5 and CRC16 functions too.
unsigned crc16_usb(unsigned curr_value, unsigned char data) {
  // CRC 16 may stream from a channel. 
  // Part data?
  unsigned result = curr_value; // first pass to crc16 should use 0xffff here
  unsigned mbit, i, tmp;
  for (i=0; i <8; i++ ){
    mbit = (result & 0x8000 )>> 15;
    result <<= 1;
    if (mbit  ^ ((data >>i) & 0x1)) {
      result ^= CRC16_POLY ;
    };    
  };
    result &= 0xffff;
  return result;
}

unsigned crc5_usb (unsigned data) {
  /*
         result = 0b11111;
       var mbit : bit;
       for each bit (b) in data {
           mbit = result[4:4];
           result <<= 1;
           if (mbit ^ b) == 1 {
               result ^= VR_CRC5_POLY;
           };
       };
       result = pack(packing.low, pack(packing.low, ~result).reverse());
  */
  
  unsigned result = 0x1f;
  unsigned mbit, i, tmp;
  for (i=0; i < 11; i++) {
    mbit = (result & 0x10) >> 4;
    result = (result << 1) ;
    if (mbit ^ ((data >> i) & 0x1)) {
      result ^= CRC5_POLY;
    };
  };
  // bit rev inverse of CRC.
  tmp=0;
  for (i =0; i <5; i++ ){
    tmp = (tmp << 1) + (~result & 1); 
    result >>= 1; 
  }
  result = tmp & 0x1f;
  return result;
};

int do_setup_packet (unsigned address, unsigned endpoint, unsigned data[]) {
  // data is 8 bytes.
  timer t;
  unsigned setup_data , time, i, tmp;
  // setup goes to endpoint 0. Device address? 0 for first case.
  unsigned data_crc = 0, setup_crc = crc5_usb((endpoint << 7) | address);
  int retry_count = 20;
  //  unsigned data_crc = 0, setup_crc = crc5_usb((address << 4) | endpoint);
  //  clearbuf(p_usb_txd);
  //  printhexln(setup_crc);
  setup_data = (PID_SETUP | 0x40) | ((setup_crc & 0x1f) << 19) | (address << 8) | (endpoint <<15) ;
  //printhexln(setup_data);
  // use builtin functions for crc.
  data_crc = 0xf335;
  //data_crc =0x3334;
  crc32(data_crc, data[0], 0xA001);
  crc32(data_crc, data[1], 0xA001);
  crc32(data_crc, 0, 0xA001);
  data_crc = ~data_crc;
		

    tmp = 0;

  do {
  t :> time;
      time += 1350;
      t when timerafter(time) :> int _;
   partout(p_usb_txd, 24, setup_data);

  // t :> time;
  //  time +=250;
  //  t when timerafter(time) :> int _;
      
    // follow with Data0 pid, 8 bytes data.
    // out PID, 8 bytes data, 16 CRC.
    // crc16 takes 2 bytes at a time. 
    // send with Bad crc intially?
    set_port_shift_count(p_usb_rxd, 8);
    t :> time;
      time += 310;
      t when timerafter(time) :> int _;
	partout(p_usb_txd,8, (PID_DATA0 | 0x40));
	//printhexln(setup_data);
	p_usb_txd <: data[0];
	p_usb_txd <: data[1];
	partout(p_usb_txd,16, data_crc);
	//      printhexln(data_crc);
	// Get ACK or resend if NACK.
	// Or timeout.
  t:> time;
	time += PACKET_TIMEOUT;
    select {
	case p_usb_rxd :> tmp:
	  tmp = tmp >> 24;
	  //	  printhexln(tmp);
	  //received packet ok
	  break;
	case t when timerafter(time) :> int _:
	  //wait interpacket delay/timeout?
	  //printstrln("TIMEOUT");
	  clearbuf(p_usb_txd);
	  break;
	}
  }	while (((tmp & 0xf) != PID_ACK) && (retry_count --));

  if (retry_count <= 0 ) {
    return -1;
  } else {
    return 0;
  }
    //  printhexln(tmp);
    //  printhexln(time);

    // end of setup packet here?
};

// START PID_IN

int do_in_packet(unsigned address, unsigned endpoint, unsigned data_pid, unsigned data_offset, unsigned data[] ){
  unsigned rxdata, token_data, data_len = (data_offset >> 2), tmp, magic_crc, time, offset;
  timer t;
  unsigned setup_crc = crc5_usb(address | (endpoint << 7));
  unsigned data_crc = 0;
  //	printstrln("in packet");
  //  data_len = data_offset >> 2;
 t :> time;
  time += 10000;

  t when timerafter(time) :> int _;
    
    // do IN
    // get 8 bytes desc back.
    // SETUP CRC unchanged.
    token_data = (PID_IN | 0x40) | ((setup_crc & 0x1f) << 19) | (address << 8) | (endpoint <<15) ;
    partout(p_usb_txd, 24, token_data);

    // now RX data?
    // PID + data + CRC.
    // Buffer until RXA low?
    XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK0, UIFM_IFM_FLAGS_RXACTIVE );

    tmp = partin(p_usb_rxd, 8);
    // swallow PID
    // Wait for RXa high.
    //		data_crc = 0xf335;
    while ((tmp & 0xf) == PID_NAK) {
      partout(p_usb_txd, 24, token_data);
      tmp = partin(p_usb_rxd, 8);
    };

    // Could also get a stall here.
    if ((tmp & 0xf )== PID_STALL) {
      return -1;
    };

    data_crc = 0x3334;
    flag0_port when pinseq(1) :> tmp;
      tmp = 1;
      while (tmp) {
	select {
	case p_usb_rxd :> rxdata :
	  // do crc, store data
	  data[data_len] = rxdata;
	  crc32(data_crc, rxdata, CRC16_POLY_INV);
	  data_len ++;
	  break;
	case flag0_port when pinseq(0) :> tmp :
	  tmp = 0;
	  break;
	}
      }

      // Drain rx port and finish crc.
      tmp = endin(p_usb_rxd);
      switch(tmp) {
      case 0:
	crc32(data_crc, 0, CRC16_POLY_INV);
	magic_crc = 0x7000;
	break;
      case 8:
	//				rxdata = rxdata >> 24;
	rxdata = partin(p_usb_rxd, tmp);
	data[data_len] = rxdata;
	crc32(data_crc, rxdata, CRC16_POLY_INV);
	magic_crc = 0x80be;
	break;
      case 16:
	//				rxdata = rxdata >> 16;
	rxdata = partin(p_usb_rxd, tmp);
	data[data_len] = rxdata;
	crc32(data_crc, rxdata, CRC16_POLY_INV);
	magic_crc = 0x3ffe;
	break;
      case 24:
	//				rxdata = rxdata >> 8;
	rxdata = partin(p_usb_rxd, tmp);
	data[data_len] = rxdata;
	crc32(data_crc, rxdata, CRC16_POLY_INV);
	crc32(data_crc, 0, CRC16_POLY_INV);
				
	magic_crc = 0x0070;
	break;
      case 32:
	printstrln("ERROR - unexpected data byte tail");
	return -1;
	break;
      };
#if 0
      printhexln(magic_crc);
      printhexln(data_crc);
      printhexln(data[0]);
      printhexln(data[1]);
      printhexln(rxdata);
#endif
      if (magic_crc == data_crc) {
	do_ack();
      } else {
	// Bad CRC - ignore packet.
	// Depending on the device implementation, we may need to start enumeration again.
      }
      data_len = (data_len  * 4) + (tmp >> 3) - 2 - data_offset; // calculate bytes from words less CRC and initial offset.
      // Pause interpacket delay.
      // t :> time;
      //      time += 1500;
      //      t when timerafter(time) :> int _;
	return data_len;
}
// END PID _IN



int do_out_packet(unsigned address, unsigned endpoint, unsigned data_pid, unsigned data_len, unsigned data[]){
  //START PID_OUT
  // Now send OUT, DATA1 (no data).
  unsigned token_data, setup_crc = crc5_usb(address | (endpoint << 7));
  timer s,t;
  unsigned byte_count, word_count, time, tx_data, tmp, data_crc = 0xf335;
  // @!@!@ DEBUG
  unsigned tail_data, tx_data_size = 0;

  //	printstrln("out packet");
  byte_count = data_len & 0x3;

  if (byte_count) {
    tail_data = data[(data_len >> 2)];
  }

  do {
    data_crc = 0xf335;  // reset crc for next loop iteration.
    token_data = (PID_OUT | 0x40) | ((setup_crc & 0x1f) << 19) | (address << 8) | (endpoint <<15) ;
    partout(p_usb_txd, 24, token_data);
    

    // Out data:
    // Do CRC on the fly?
    // When data is 0 size, the delay from the token out to the next item of data
    // is too long and the IFM underflows. We need to special case
    // short packets to keep the data stream fast enough. 
    // Bit time on FS is 83ns, so byte time should be quite long, but the PHY
    // appears to do some buffering and expect the next byte much quicker.
    // Back to back out's are fast enough, but we don't know where the threshold lies.
    // This time can be used instead to pre-empt part of the crc?
    // Essentially we have 300 instruction times.
    word_count = data_len >> 2;
    // Pre-empt the tail word here before we start doing out, then special case 
    // if needed. Use timer at the end to keep the timing right.
    // This won't work if word count is non-zero as the data_crc will be wrong.
    

    tx_data = data_pid | 0x40;
    tx_data_size = 32;
    if (word_count == 0) {
    t :> time;
      
      time += 300;
      switch(byte_count) {
	// word aligned data. Out crc.
      case 0:
	crc32(data_crc, 0, CRC16_POLY_INV);
	data_crc =~ data_crc;
	//      partout(p_usb_txd, 16, data_crc);
	tx_data_size = 8; // only output pid.
	tmp = tx_data | ((data_crc & 0xffff) << 8); 
	t when timerafter(time) :> int _;
	  //		partout(p_usb_txd, 8, tx_data);
	  //		partout(p_usb_txd, 16, data_crc);
    clearbuf(p_usb_txd);
	  partout(p_usb_txd, 24, tmp);
	break;
      case 1:
	// crc8 and add in crc.
	tmp = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	//      partout(p_usb_txd, 8, tail_data);
	crc32(data_crc, 0, CRC16_POLY_INV);
	data_crc =~ data_crc;
	//      partout(p_usb_txd, 16, data_crc);
	tx_data = (tx_data << 8) | (tmp & 0xff);
	tx_data_size = 16;
	      t when timerafter(time) :> int _;
		partout(p_usb_txd, 16, tx_data);
		partout(p_usb_txd, 16, data_crc);
	break;
      case 2:
	//      partout(p_usb_txd, 16, tx_data);
	tmp = crc8shr(data_crc, tail_data, CRC16_POLY_INV);
	tmp = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	crc32(data_crc, 0, CRC16_POLY_INV);
	data_crc =~ data_crc;
	//      partout(p_usb_txd, 16, data_crc);
	tx_data =( tx_data << 16 ) | (tmp & 0xffff);
	tx_data_size = 24;
	t when timerafter(time) :> int _;
	  partout(p_usb_txd, 24, tx_data);
	  partout(p_usb_txd, 16, data_crc);
	break;
      case 3:
	//      partout(p_usb_txd, 24, tx_data);
	tmp = crc8shr(data_crc, tail_data, CRC16_POLY_INV);
	tmp = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	tmp = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	crc32(data_crc, 0, CRC16_POLY_INV);
	data_crc =~ data_crc;
	//      partout(p_usb_txd, 16, data_crc);

	// @!@!@!@ This needs adding back in, but compiler bug at the moment.
	//	tx_data_size = 32;
	tx_data = (tx_data << 24) | (tmp & 0xffffff);
	      t when timerafter(time) :> int _;
		partout(p_usb_txd, 32, tx_data);
		partout(p_usb_txd, 16, data_crc);
	break;
      }

#if 1
      // it appears partout can't take a variable width, therefore this
      // has to be hard coded above.
      //      t when timerafter(time) :> int _;
      //	partout(p_usb_txd, tx_data_size, tx_data);
      //	partout(p_usb_txd, 16, data_crc);
	//	printhexln(tmp);
      //    t:> time; 
      //      time += 50;
      //      t when timerafter(time) :> int _;


#endif
    } else {
      
    s:> time; 
      time += 300;
      //		sync(p_usb_txd);

      // Deal with dubious sync? Port not clear?
      //	clearbuf(p_usb_txd);
	tmp = data_pid | 0x40;
	tx_data = data[0];
	s when timerafter(time) :> int _;

	partout(p_usb_txd, 8, tmp);
	p_usb_txd <: tx_data;
	  crc32(data_crc, tx_data, CRC16_POLY_INV);
#pragma unsafe arrays
	for (tmp = 1; tmp < word_count; tmp ++) {
	  tx_data = data[tmp];
	  p_usb_txd <: tx_data;
	  //	  sync(p_usb_txd);
	  //	  	s:> time; 
	  //	  	  time += 250;
	  crc32(data_crc, tx_data, CRC16_POLY_INV);
	  //	  	  s when timerafter(time) :> int _;

	}; 
	switch(byte_count) {
	  // word aligned data. Out crc.
	case 0:
	  crc32(data_crc, 0, CRC16_POLY_INV);
	  data_crc =~ data_crc;
	  partout(p_usb_txd, 16, data_crc);
	  break;
	case 1:
	  // crc8 and add in crc.
	  tmp = crc8shr(data_crc, tail_data, CRC16_POLY_INV);
	  partout(p_usb_txd, 8, tail_data);
	  crc32(data_crc, 0, CRC16_POLY_INV);
	  data_crc =~ data_crc;
	  partout(p_usb_txd, 16, data_crc);
	  break;
	case 2:
	  partout(p_usb_txd, 16, tail_data);
	  tx_data = crc8shr(data_crc, tail_data, CRC16_POLY_INV);
	  tx_data = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	  crc32(data_crc, 0, CRC16_POLY_INV);
	  data_crc =~ data_crc;
	  partout(p_usb_txd, 16, data_crc);
	  break;
	case 3:
	  partout(p_usb_txd, 24, tail_data);
	  tx_data = crc8shr(data_crc, tail_data, CRC16_POLY_INV);
	  tx_data = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	  tx_data = crc8shr(data_crc, tx_data, CRC16_POLY_INV);
	  crc32(data_crc, 0, CRC16_POLY_INV);
	  data_crc =~ data_crc;
	  partout(p_usb_txd, 16, data_crc);
	  break;
	}
    }


    // swallow ACK (or resend if Nack/timeout)
    set_port_shift_count(p_usb_rxd, 8);
  t:> time;
    time += PACKET_TIMEOUT;
    select {
    case p_usb_rxd:> tmp:
      tmp = tmp >> 24;
      // if NAK, pause briefly as interpacket delay.
      //      sync (p_usb_txd);
#if 0
      if ((tmp&0xf) == PID_NAK) {
	unsigned t2;
      s :> t2;
	t2 += 300;
	s when timerafter(t2) :> int _;
      }
#endif
      break;
    case t when timerafter(time) :> int _:
      //      clearbuf (p_usb_txd);
      break;
    }
  } while ((tmp &0xf) != PID_ACK);
  //END PID_OUT
  //    printhexln(data_crc);

}

void do_ack() {
  unsigned data = 0x40 | PID_ACK;
  partout(p_usb_txd, 8, data);
};


int do_packet(unsigned pid, unsigned address, unsigned endpoint, unsigned data_len, unsigned data_pid, unsigned data[]) {
  // Pseudo code for generic usb io.
  unsigned in_len, status;
  // As host all of this is under our control, but 
  // will depend on higher level as to what we do.
  // (post enum)
  // Also really want generic packet handling for retry/bad crc etc.
  // Are timeouts handled here? Resends can be.

  // switch on pid?

  // Can have a timer loop around generic do_packet for SOFs?
  // Would mean NAK/retry loop needs to be in this function rather 
  // than within sub-functions.
  switch(pid) {
  case PID_SETUP:
    // has device, endpoint, 8 bytes of data only.
    status = do_setup_packet(address, endpoint, data);
    break;
  case PID_OUT:
    // send token + data (up to 1023 bytes), wait on ack.
    status = do_out_packet(address, endpoint, data_pid, data_len, data);
    break;
  case PID_IN:
    // send token, wait on data + check CRC - ack/ignore on CRC
    // Data_len is offset into data array for rx_data as single packet might use multiple INs
    // and this saves copying data around later.
    status = do_in_packet(address, endpoint, data_pid, data_len, data);
    break;
  };
  return status;
};



unsigned get_descriptor (unsigned address, unsigned endpoint, SetupPacket setup_data, unsigned rx_data[]) {
  // do a standard descriptor.
  // Setup transaction is 8 bytes of data.
  // We have an expected data length to receive that's put into rx_data.


  unsigned tx_data[2]; // fixed length setup packet
  
  unsigned i, data_len = 8;
  unsigned j, word_count = (setup_data.wLength >> 3) + 1;
  unsigned local_buf[128];
  unsigned retry_count = 10;
  ParseSetupPacket(setup_data, tx_data);

  do_packet(PID_SETUP, address, endpoint, data_len, PID_DATA0, tx_data);
  data_len = 0;
  retry_count = 10;
  // Either do data in until nothing more comes back, or for expected len from packet?

  // Need to local buffer each time and memcpy into rx_data as base offset will change.

  do {
    data_len += do_packet(PID_IN, address, endpoint, data_len, PID_DATA0, rx_data); // length is unknown on IN.
  } while ((data_len < setup_data.wLength) || ((data_len == -1) && retry_count--));
  // if data_len == -1 then retry until you give up.
  // if data count is 8, get next.  

  //while ((data_len == 8) || retry_count --); // odd number of bytes is end of data stream.
  if (data_len != -1) {
    do_packet(PID_OUT, address, endpoint, 0, PID_DATA1, rx_data); // length is unknown on IN
  };

  return data_len;

};

// @!@!@@ DEBUG FUNCTIONS 
unsigned dump_ifm_state() {
  unsigned tmp;
  printstrln("IFM Flags");
  tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_IFM_FLAGS);
  printhexln(tmp);
  printstrln("OTG Flags");
  tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_OTG_FLAGS);
  printhexln(tmp);


};


void ParseSetupPacket(SetupPacket setup_data, unsigned tx_data[]) {
  tx_data[0] = (setup_data.bmRequestType.Direction << 7) | \
    (setup_data.bmRequestType.Type << 5) | \
    (setup_data.bmRequestType.Recipient);
  tx_data[0] |= setup_data.bRequest << 8;
  tx_data[0] |= setup_data.wValue << 16;
  tx_data[1] = setup_data.wIndex;
  tx_data[1] |= (setup_data.wLength << 16);
}

unsigned class_request(unsigned address, unsigned endpoint, SetupPacket setup_data, unsigned tx_data[]){
  unsigned setup_tx_data[2];
  ParseSetupPacket(setup_data, setup_tx_data);
   do_packet(PID_SETUP, address, 0, 8, PID_DATA0, setup_tx_data);
   do_packet(PID_OUT, address, endpoint, setup_data.wLength, PID_DATA1, tx_data);
   do_packet(PID_IN, address, 0, 8, PID_DATA0, tx_data); 

}

void ParseDeviceDescriptor(unsigned data[], DeviceDescriptor &desc, unsigned data_len){
  //   safememcpy((desc, char[]), (data, char[]), data_len);
  // Hack until bug is fixed.
  desc.bLength = data[0] & 0xff;
  desc.bDescriptorType = (data[0] >> 8) & 0xff;
  desc.bcdUSB = (data[0] >> 16) & 0xffff;
  desc.bDeviceClass = data[1] & 0xff;
  desc.bDeviceSubClass = (data[1] >> 8) & 0xff;
  desc.bDeviceProtocol = (data[1] >> 16) & 0xff;
  desc.bMaxPacketSize = (data[1] >> 24) & 0xff;
  desc.idVendor = data[2] & 0xffff;
  desc.idProduct =( data[2] >> 16) & 0xffff;
};

ParseConfigDescriptor(unsigned data[], ConfigDescriptor &config, unsigned data_len) {
  config.bLength = data[0] & 0xff;
  config.bDescriptorType = (data[0] >> 8) & 0xff;
  config.wTotalLength = (data[0] >> 16) & 0xffff;
  config.bNumInterfaces = (data[1]) & 0xff;
  config.bConfigurationValue = (data[1] >> 8) & 0xff;
  config.iConfiguration = (data[1] >> 16) & 0xff;
  config.bmAttributes = (data[1] >> 24) & 0xff;
  config.bMaxPower = (data[2]) & 0xff;
};

void printDesc(DeviceDescriptor desc) {
  // Debug print function.
  printstr ("bLen           0x");
  printhexln(desc.bLength);
  printstr ("bDescType      0x");
  printhexln(desc.bDescriptorType);
  printstr ("bcdUSB         0x");
  printhexln(desc.bcdUSB);
  /// @!@@! To be finished!
  printstr("idVendor        0x");
  printhexln(desc.idVendor);
  printstr("idProduct       0x");
  printhexln(desc.idProduct);
};

