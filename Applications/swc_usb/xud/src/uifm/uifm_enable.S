
// uifm_enable.S

#define PS_XCORE_CTRL0 0x20b

#include <xs1.h>

.text
.align    2
.globl XUD_UIFM_Enable, "f{ui}(ui)"
.globl XUD_UIFM_Enable.nstackwords
.globl XUD_UIFM_Enable.maxthreads
.globl XUD_UIFM_Enable.maxtimers
.globl XUD_UIFM_Enable.maxchanends
.globl XUD_UIFM_Enable.maxsync
.type  XUD_UIFM_Enable, @function
.linkset XUD_UIFM_Enable.locnoside, 1
.linkset XUD_UIFM_Enable.locnochandec, 1
.linkset XUD_UIFM_Enable.nstackwords, 16
.cc_top XUD_UIFM_Enable.function,XUD_UIFM_Enable
.align    4


// void XUD_UIFM_Enable (unsigned mode);
XUD_UIFM_Enable:
  entsp     16
  stw       r10, sp[1]

  ldc       r10, PS_XCORE_CTRL0 
  set       ps[r10], r0
 
  // Sanity check, read back and check
  get	    r10, ps[r10]
  eq        r0, r0, r10
  ecallf    r0
 
  ldw       r10, sp[1]
  retsp     16
.cc_bottom XUD_UIFM_Enable.function
.linkset XUD_UIFM_Enable.maxchanends, 0
.linkset XUD_UIFM_Enable.maxtimers, 0
.linkset XUD_UIFM_Enable.maxthreads, 1

