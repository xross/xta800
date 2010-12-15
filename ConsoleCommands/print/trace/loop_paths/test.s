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
  ldc r0, 10
  ldc r2, 0
  ldc r3, 100
  ldc r4, 1
  ldc r5, 3
loop:
  and r1, r0, r4
  bt r1, else
if:
  add r2, r2, 1
  bu loopEnd
else:
  divu r3, r3, r5
loopEnd:
  sub r0, r0, 1
  bt r0, loop
done:
  retsp 0
