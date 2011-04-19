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
  retsp 0
  
.type main, @function
.globl main
main:
  entsp 1
  ldc r1, 1
.xtabranch Ltarget1, Ltarget2, Ltarget3, Ltarget4, Ltarget5
  bru r1
Ltarget1:
  bl test
  retsp 1
Ltarget2:
  bl test
  retsp 1
Ltarget3:
  bl test
  retsp 1
Ltarget4:
  bl test
  retsp 1
Ltarget5:
  bl test
  retsp 1

