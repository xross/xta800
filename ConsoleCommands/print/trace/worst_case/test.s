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
  lss r0, r1, r2
  bt r0, COMPLETE
  ldc r0, 0
COMPLETE:
  retsp 0
