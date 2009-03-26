#include <xs1.h>

// Inline assembly:
// http://www.ibiblio.org/gferg/ldp/GCC-Inline-Assembly-HOWTO.html

.globl hda_io
.globl hda_io.nstackwords
.globl hda_io.maxthreads
.linkset hda_io.nstackwords, 16
.linkset hda_io.maxthreads, 1
.type hda_io, @function
////////////////////////////////////////////////////////////////////////////
//
// void hda_io(chanend c_sync, chanend c_sdo, const unsigned morton1[256], const unsigned morton2[256]);
//
// NOTE This is the "input bytes, output word" version
//
// HDA budget is 16.6 @ 50MIPS or 19 @ 57MIPS (7 threads)
// Currently 17 instructions per SDR byte (excluding FNOPS and IO hiccups)
//
//  r0   c_sync
//  r1   c_sdo
//  r2   morton1
//  r3   morton2
//  r4   p_sync0
//  r5   p_sync1
//  r6   p_sdo0
//  r7   p_sdo1
//  r8   scratch
//  r9   scratch
// r10   scratch
// r11   scratch
//
.align 4
.skip 2
.text
hda_io:
  entsp    16
hda_io_init:
  ldw      r4, dp[p_sync0]
  ldw      r5, dp[p_sync1]
  ldw      r6, dp[p_sdo0]
  ldw      r7, dp[p_sdo1]
  setsr    XS1_SR_FAST_MASK
eat_reset:
  ldw      r10, dp[p_reset]
  ldc      r11, 0
  setd     res[r10], r11
  setc     res[r10], XS1_SETC_RUN_CLRBUF
  setc     res[r10], XS1_SETC_COND_EQ
  in       r11, res[r10]
  setc     res[r10], XS1_SETC_COND_NEQ
  in       r11, res[r10]
  setc     res[r10], XS1_SETC_COND_EQ
  in       r11, res[r10]
  setc     res[r10], XS1_SETC_COND_NEQ
  in       r11, res[r10]
sync_ports:
  // Needs ticking clock
  in       r11, res[r4]
  getts    r10, res[r4]
  ldc      r11, 100
  add      r10, r10, r11
  setpt    res[r4], r10
  setpt    res[r5], r10
  setpt    res[r6], r10
  setpt    res[r7], r10
sdr_halfword0:
sdr_input0_0:
  in       r8, res[r4]
  ldw      r8, r2[r8]
sdr_input1_0:
  in       r9, res[r5]
  ldw      r9, r3[r9]
  or       r8, r8, r9
sdr_input2_0:
  in       r10, res[r6]
  ldw      r10, r2[r10]
sdr_input3_0:
  in       r11, res[r7]
  ldw      r11, r3[r11]
  or       r9, r10, r11
sdr_input0_1:
  in       r10, res[r4]
  ldw      r10, r2[r10]
sdr_input1_1:
  in       r11, res[r5]
  ldw      r11, r3[r11]
  or       r10, r10, r11
  shl      r10, r10, 16
  or       r8, r8, r10
  out      res[r0], r8
sdr_input2_1:
  in       r8, res[r6]
  ldw      r8, r2[r8]
sdr_input3_1:
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r8, r8, r10
  ldw      r11, dp[p_sdi]//
  shl      r8, r8, 16
  or       r8, r8, r9
  out      res[r1], r8
  in       r10, res[r0]//
sdr_halfword1:
sdr_input0_2:
  in       r8, res[r4]
  ldw      r8, r2[r8]
sdr_input1_2:
  in       r9, res[r5]
  ldw      r9, r3[r9]
  or       r8, r8, r9
  out      res[r11], r10//
sdr_input2_2:
  in       r10, res[r6]
  ldw      r10, r2[r10]
sdr_input3_2:
  in       r11, res[r7]
  ldw      r11, r3[r11]
  or       r9, r10, r11

sdr_input0_3:
  in       r10, res[r4]
  ldw      r10, r2[r10]
sdr_input1_3:
  in       r11, res[r5]
  ldw      r11, r3[r11]
  or       r10, r10, r11
  shl      r10, r10, 16
  or       r8, r8, r10
  out      res[r0], r8
sdr_input2_3:
  in       r8, res[r6]
  ldw      r8, r2[r8]
sdr_input3_3:
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r8, r8, r10
  shl      r8, r8, 16
  or       r8, r8, r9
  out      res[r1], r8
  bu       sdr_halfword0

.globl hda_align
.globl hda_align.nstackwords
.globl hda_align.maxthreads
.linkset hda_align.nstackwords, 16
.linkset hda_align.maxthreads, 1
.type hda_align, @function
// void hda_align(chanend c_sync, chanend c_sdo);
//  r0   c_sync
//  r1   c_sdo
// r10   scratch
// r11   scratch
.text
hda_align:
  entsp    16
hda_align_init:
  // ...
next:
  ldc      r11, 0
  out      res[r0], r11
ddr_word0:
  in       r10, res[r0]
  in       r11, res[r1]
ddr_word1:
  in       r10, res[r0]
  in       r11, res[r1]
	bu       next

.globl hda_align_debug
.globl hda_align_debug.nstackwords
.globl hda_align_debug.maxthreads
.linkset hda_align_debug.nstackwords, 16
.linkset hda_align_debug.maxthreads, 1
.text
hda_align_debug:
  entsp    16
  ldc      r9, 128 / 2
  ldaw     r2, dp[dump_buffer]
debug_next:
  ldc      r11, 0
  out      res[r0], r11
debug_ddr_word0:
  in       r10, res[r0]
  in       r11, res[r1]
  stw      r10, r2[0]
  stw      r11, r2[1]
debug_ddr_word1:
  in       r10, res[r0]
  in       r11, res[r1]
  stw      r10, r2[2]
  stw      r11, r2[3]
  ldaw     r2, r2[4]
  sub      r9, r9, 1
  bt       r9, debug_next
  ldaw     r0, dp[dump_buffer]
  mkmsk    r1, 32
  bl       dump
  ldc      r0, 0
  bl       _exit
.section .dp.data, "adw", @progbits
.align 4
dump_buffer:
.skip 2 * 128 * 4
