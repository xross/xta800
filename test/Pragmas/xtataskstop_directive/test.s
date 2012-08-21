.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main

.type main, @function
.globl main
main:
.xtataskstop
  entsp 1
.xtataskstop
  ldc r0, 0x0
.xtataskstop
  ldc r0, 0x0
.xtataskstop
  ldc r0, 0x0
.xtataskstop
  retsp 1

