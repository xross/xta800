.text
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
.xtathreadstart
  entsp 1
.xtathreadstart
  ldc r0, 0x0
.xtathreadstart
  ldc r0, 0x0
.xtathreadstart
  ldc r0, 0x0
.xtathreadstart
  retsp 1

