.text
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
Lsource:
  bau r1

Ltarget1:
  retsp 1

Ltarget2:
  retsp 1

Ltarget3:
  retsp 1

Ltarget4:
  retsp 1

.section .xtabranch
    .int Lsource, Ltarget1
    .int Lsource, Ltarget2
    .int Lsource, Ltarget3
    .int Lsource, Ltarget4

