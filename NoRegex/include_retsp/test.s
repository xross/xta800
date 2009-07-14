.text

.xtathreadstart
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  ldc r0, 0
  retsp 0
