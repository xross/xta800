.text

.align 2
.globl _start
_start:
  bl main

.type test, @function
.globl test
test:
  add r5, r5, 0
  bt r1, block1
  bt r1, block2
  bt r1, block3
  
block1:
  bu block5
block2:
  bu block5
block3:
  bu block5

block4:
  ldc r0, 1

block5:
  ldc r1, 1
  bt r1, block5

block8:
  retsp 0
  

.type main, @function
.globl main
main:
  entsp 1
  
loop:
  bl test
  bf r0, loop
  

  retsp 1
