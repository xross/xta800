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
  ldc r0, 5
loopA:
  ldc r1, 5
loopR:
  sub r1, r0, 1
  bt r1, loopR
  sub r0, r0, 1
  bt r0, loopA
done:
  retsp 0
