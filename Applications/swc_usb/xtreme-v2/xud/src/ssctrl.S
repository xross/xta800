#include <xs1.h>

/////////////////////////////////////////////////////////////////////////
//
// Arguments:
//   unsigned ssctrl_read(int regn);
//
// Registers:
//   r0  regn
//   r0  value
//   r1  ch
//   r2  returnHeader
//
.globl ssctrl_read
.globl ssctrl_read.nstackwords
.linkset ssctrl_read.nstackwords, 2
.cc_top ssctrl_read.func, ssctrl_read
ssctrl_read:
  entsp    2
  stw      r10, sp[1]
  getr     r1, XS1_RES_TYPE_CHANEND
  ecallf   r1
  shr      r2, r1, XS1_CHAN_ID_CHANNUM_SHIFT
ssctrl_read_address:
  ldc      r10, XS1_RES_TYPE_CONFIG
  ldc      r11, XS1_CT_SSCTRL
  shl      r11, r11, XS1_CHAN_ID_CHANNUM_SHIFT
  or       r10, r11, r10
  shr      r11, r1, XS1_CHAN_ID_NODE_SHIFT
  shl      r11, r11, XS1_CHAN_ID_NODE_SHIFT
  or       r10, r11, r10
  setd     res[r1], r10
ssctrl_read_readmsg:
  ldc      r11, XS1_CT_READC
  outct    res[r1], r11
  shr      r11, r2, 16
  outt     res[r1], r11
  shr      r11, r2, 8
  outt     res[r1], r11
  outt     res[r1], r2
  shr      r11, r0, 8
  outt     res[r1], r11
  outt     res[r1], r0
  ldc      r11, XS1_CT_END
  outct    res[r1], r11
ssctrl_read_response:
  chkct    res[r1], XS1_CT_ACK
  in       r0, res[r1]
  chkct    res[r1], XS1_CT_END
ssctrl_read_done:
  freer    res[r1]
  ldw      r10, sp[1]
  retsp    2
.cc_bottom ssctrl_read.func

/////////////////////////////////////////////////////////////////////////
//
// Arguments:
//   void ssctrl_check(int regn, unsigned x);
//
.globl ssctrl_check
.globl ssctrl_check.nstackwords
.linkset ssctrl_check.nstackwords, 2
.cc_top ssctrl_check.func, ssctrl_check
ssctrl_check:
	entsp    2
	stw      r1, sp[1]
	bl       ssctrl_read
	ldw      r1, sp[1]
	eq       r11, r0, r1
	ecallf   r11
  retsp    2
.cc_bottom ssctrl_check.func

/////////////////////////////////////////////////////////////////////////
//
// Arguments:
//   void ssctrl_write(int regn, unsigned x);
//
// Registers:
//   r0  regn
//   r1  ch
//   r2  returnHeader
//   r3  x
//
.globl ssctrl_write
.globl ssctrl_write.nstackwords
.linkset ssctrl_write.nstackwords, 2
.cc_top ssctrl_write.func, ssctrl_write
ssctrl_write:
  entsp    2
  stw      r10, sp[1]
	mov      r3, r1
  getr     r1, XS1_RES_TYPE_CHANEND
  ecallf   r1
  shr      r2, r1, XS1_CHAN_ID_CHANNUM_SHIFT
ssctrl_write_address:
  ldc      r10, XS1_RES_TYPE_CONFIG
  ldc      r11, XS1_CT_SSCTRL
  shl      r11, r11, XS1_CHAN_ID_CHANNUM_SHIFT
  or       r10, r11, r10
  shr      r11, r1, XS1_CHAN_ID_NODE_SHIFT
  shl      r11, r11, XS1_CHAN_ID_NODE_SHIFT
  or       r10, r11, r10
  setd     res[r1], r10
ssctrl_write_sendmsg:
  ldc      r11, XS1_CT_WRITEC
  outct    res[r1], r11
  shr      r11, r2, 16
  outt     res[r1], r11
  shr      r11, r2, 8
  outt     res[r1], r11
  outt     res[r1], r2
  shr      r11, r0, 8
  outt     res[r1], r11
  outt     res[r1], r0
  out      res[r1], r3
  ldc      r11, XS1_CT_END
  outct    res[r1], r11
ssctrl_write_response:
  chkct    res[r1], XS1_CT_ACK
  chkct    res[r1], XS1_CT_END
ssctrl_write_done:
  freer    res[r1]
  ldw      r10, sp[1]
  retsp    2
.cc_bottom ssctrl_write.func

/////////////////////////////////////////////////////////////////////////
//
// Arguments:
//   void ssctrl_delay(int ntmrticks);
//
// Registers:
//   r0  ntmrticks
//   r1  timer
//
.globl ssctrl_delay
.globl ssctrl_delay.nstackwords
.linkset ssctrl_delay.nstackwords, 2
.cc_top ssctrl_delay.func, ssctrl_delay
ssctrl_delay:
  getr     r1, XS1_RES_TYPE_TIMER
  in       r11, res[r1]
  add      r11, r11, r0
  setd     res[r1], r11
  setc     res[r1], XS1_SETC_COND_AFTER
  in       r11, res[r1]
  freer    res[r1]
  retsp    0
.cc_bottom ssctrl_delay.func
