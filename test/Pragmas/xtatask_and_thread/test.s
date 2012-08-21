.text
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
.xtataskstart
  entsp 1
.xtataskstart
  ldc r0, 0x0
.xtathreadstart
  ldc r0, 0x0
.xtataskstop
  ldc r0, 0x0
.xtathreadstop
  retsp 1

