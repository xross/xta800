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
  ldc r2, 0
  ldc r3, 100
  ldc r4, 1
  ldc r5, 3
loop:
  and r1, r0, r4
  bt r1, else
if:
  sub r2, r2, 1
  bu case
else:
  divu r3, r3, r5

case:
  sub r0, r0, 1
  shl r1, r0, 1
.xtabranch case0, case1, case2, case3, case4
  bru r1
case0:
  add r2, r2, 1
  bu loopEnd
case1:
  add r2, r2, 2
  bu loopEnd
case2:
  add r2, r2, 3
  bu loopEnd
case3:
  add r2, r2, 4
  bu loopEnd
case4:
  add r2, r2, 5

loopEnd:
  bt r0, loop
done:
  retsp 0
