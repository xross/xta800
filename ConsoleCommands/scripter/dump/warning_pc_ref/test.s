.text

.align 2
.globl _start
.xtathreadstart
_start:
  bl main

.align 4
.type main, @function
.globl main
main:
  ldc r0, 0
  ldc r2, 10
loop:
  add r1, r1, 1
  lss r0, r1, r2
  bt r0, loop
  retsp 0
