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
  ldc r1, 5
loopA:
  sub r1, r1, 1
  bt r1, loopA
loopR:
  sub r0, r0, 1
  mov r1, r0
  bt r0, loopA
done:
  retsp 0
