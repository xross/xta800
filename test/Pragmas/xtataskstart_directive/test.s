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
.xtataskstart
  ldc r0, 0x0
.xtataskstart
  ldc r0, 0x0
.xtataskstart
  retsp 1

