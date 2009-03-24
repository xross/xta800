
#include <xs1.h>
#include <print.h>

#include "XUD_UIFM_Functions.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_USB_Defines.h"
#include "XUD_Support.h"

extern out port reg_write_port;
extern in  port reg_read_port; 
extern in  port flag0_port;  
extern in  port flag1_port;
extern in  port flag2_port; 
extern out port p_usb_txd; 

// Assumptions:
// - In full speed mode
// - No flags sticky 
void XUD_DeviceAttachHS()
{
  unsigned tmp;
 
  // Set flag port 0 to J linestate symbol
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK0, 0x8);

  // Set flag port 1 to K linestate symbol
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK1, 0x10);

  // Set flag port 2 to SE0 linestate symbol
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK2, 0x20);

  XUD_UIFM_RegWrite (reg_write_port, UIFM_REG_FLAGS, 0x0);

  //XUD_Sup_Delay(30000000);

  // Clear flag port buffers
  //clearbuf(flag0_port);
  //clearbuf(flag1_port);
  //clearbuf(flag2_port);

  // This assumes the PHY is already connected to an enabled bus, so
  // doesn't give any idle period for VBus to stabilise.

  // Timing diag p40 of ULPI spec, this is pre T0.
  // DEBUG: shows write to phy ctrl reg of 0x45. This is termsel =1, 
  // xcvrsel = 01 (FS) ?
  // From RTL, 0x7 sets suspend, xcvrsel, termsel.   
  //XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, 0x7);

  // At t0 in Figure 35 of ULPI spec, linestate changes from J to SE0.
  // Once the SE0 is detected, we can clear the flags reg and start
  // the HS chirping.

  //clearbuf(flag1_port);
  //clearbuf(flag2_port);
  //flag1_port when pinseq(1) :> tmp;
  flag2_port when pinseq(1) :> tmp;
  //XUD_UIFM_RegWrite (reg_write_port, UIFM_REG_FLAGS, 0x0);


  // On detecting the SE0:
  // De-assert XCVRSelect and set opmode=2
  // DEBUG - write to ulpi reg 0x54. This is:
  // opmode = 0b10, termsel = 1, xcvrsel = 0b00;

  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, 0x15);
  // DEBUG: This sets IFM mode to DecodeLineState
  // Bit 5 of the CtRL reg (DONTUSE) has a serious effect on 
  // driving the k-chirp, regardless of if we actually want to yet or not.
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, 0x14);
  // Should out a K chirp.
  XUD_Sup_Outpw8(p_usb_txd, 0);

  // Wait for TUCHEND - TUCH
  XUD_Sup_Delay(T_UCHEND_T_UCH_ULPI);

  // Clear port buffers to remove k chirp
  clearbuf(p_usb_txd);
  clearbuf(p_usb_txd);

  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, 0x04);

  // Host sends KJ chirp pairs.  Wait for 3 KJ pairs.  If we dont get them something has lost the plot...
  // J, K, SE0 on flag ports 0, 1, 2 respectively
  // Wait for fs chirp k (i.e. HS chirp j)
  flag0_port when pinseq(0) :> tmp; // Wait for out k to go
  flag0_port when pinseq(1) :> tmp;
  // Wait for fs chirp j (i.e. JS chirp k)
  flag1_port when pinseq(1) :> tmp;
  flag0_port when pinseq(1) :> tmp;
  flag1_port when pinseq(1) :> tmp;
  flag0_port when pinseq(1) :> tmp;
  flag1_port when pinseq(1) :> tmp;

  // Wait for last chirp to go low
  flag1_port when pinseq(0) :> tmp;

  // Three pairs of KJ received... de-assert TermSelect... (and opmode = 0, suspendm = 1)
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, 0x1);
  
  //wait for SE0 (TODO consume other chirps?)
  flag2_port when pinseq(1) :> tmp;
  //wait for SE0 end
  flag2_port when pinseq(0) :> tmp;
 
}

