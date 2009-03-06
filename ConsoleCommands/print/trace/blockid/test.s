.text

.align 2
.globl _start
_start:
  bl main

.align 4
.type main, @function
.globl main
main:
  ldc r0, 0
  retsp 0
