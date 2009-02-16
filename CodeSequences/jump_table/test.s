.text

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
  loop:  
  bru r1
Ltarget1:
  bl test
Ltarget2:
  bl test
Ltarget3:
  bl test
Ltarget4:
  bl test
Ltarget5:
  bl test
Ltarget6:
  bl test
Ltarget7:
  bl test
Ltarget8:
  bl test
Ltarget9:
  bl test
Ltarget10:
  bl test
Ltarget11:
  bl test
Ltarget12:
  bl test
Ltarget13:
  bl test
Ltarget14:
  add r1, r1,2
Ltarget15:
  bu loop
Ltarget16:
  ldc r1, 1
  retsp 1

.section .xtabranch
    .int loop, Ltarget1
    .int loop, Ltarget2
    .int loop, Ltarget3
    .int loop, Ltarget4
    .int loop, Ltarget5
    .int loop, Ltarget6
    .int loop, Ltarget7
    .int loop, Ltarget8
    .int loop, Ltarget9
    .int loop, Ltarget10
    .int loop, Ltarget11
    .int loop, Ltarget12
    .int loop, Ltarget13
    .int loop, Ltarget14
    .int loop, Ltarget15
    .int loop, Ltarget16
    
