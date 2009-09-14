.text
.xtathreadstart
.align 2
.globl _start
_start:
  bl main

.type test, @function
.globl test
test:
  ldc r2, 1
loopStart:
.xtabranch Ltarget1, Ltarget2, Ltarget3, Ltarget4, Ltarget5
  bru r1
Ltarget1:
  add r1, r1, r1
  bu loopStart
Ltarget2:
  add r1, r1, 1
  bu loopStart
Ltarget3:
  add r1, r1, 2
  bu loopStart
Ltarget4:
  add r1, r1, 3
  bu loopStart
Ltarget5:
  add r1, r1, 4
  bu loopStart
  
.type main, @function
.globl main
main:
  entsp 1
  ldc r1, 1
  bl test
  retsp 1

