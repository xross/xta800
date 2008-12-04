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
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  bl test
  add r1, r1,2
  bu loop
  ldc r1, 1
  retsp 1
