.text

.align 2
.globl _start
_start:
  bl main

.type test, @function
.globl test
test:
  ldc r1, 5

block5:
  sub r1, r1, 1
  ldc r3, 5
block4:
  sub r3, r3, 1
  bt r3, block4
  bt r1, block5
  
  retsp 0
  

.type main, @function
.globl main
main:
  entsp 1
  ldc r2, 5
  
loop:
  bl test
  sub r2, r2, 1
  bf r2, loop

  retsp 1
