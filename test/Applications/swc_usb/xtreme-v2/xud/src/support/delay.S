
#include <xs1.h>
#define STACK_EXTEND 8

.text
.align    2
.globl XUD_Sup_Delay, "f{0}(ui)"
.globl XUD_Sup_Delay.nstackwords
.globl XUD_Sup_Delay.maxthreads
.globl XUD_Sup_Delay.maxtimers
.globl XUD_Sup_Delay.maxchanends
.globl XUD_Sup_Delay.maxsync

.type  XUD_Sup_Delay, @function
.linkset XUD_Sup_Delay.locnoside, 1
.linkset XUD_Sup_Delay.locnochandec, 1
.linkset XUD_Sup_Delay.nstackwords, STACK_EXTEND
.cc_top XUD_Sup_Delay.function,XUD_Sup_Delay
.align    4


XUD_Sup_Delay:
  entsp     STACK_EXTEND
  stw       r10, sp[1]

  getr      r11, XS1_RES_TYPE_TIMER
  ecallf    r11
  in        r10, res[r11]                 // Get current time
  
  add       r0, r0, r10                   // Add delay to current time
  setc      res[r11], XS1_SETC_COND_AFTER
  setd      res[r11], r0
  in        r0, res[r11]
  freer     res[r11] 

  ldw       r10, sp[1]
  retsp     STACK_EXTEND
.cc_bottom XUD_Sup_Delay.function
.linkset XUD_Sup_Delay.maxchanends, 0
.linkset XUD_Sup_Delay.maxtimers, 1
.linkset XUD_Sup_Delay.maxthreads, 0

