.text
.xtathreadstart
.align 2
.globl _start
_start:
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r1, 0xa
loop:
.xtaloop 22
  add r0, r0, 1
  eq r2, r0, r1
  bf r2, loop
  retsp 1


