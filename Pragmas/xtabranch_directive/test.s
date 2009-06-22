.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main
.xtathreadstop

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0

.xtabranch Ltarget1, Ltarget2, Ltarget3, Ltarget4
  bau r1

Ltarget1:
  retsp 1

Ltarget2:
  retsp 1

Ltarget3:
  retsp 1

Ltarget4:
  retsp 1

