.text

.align 2
.globl _start
_start:
  bl main

.type test, @function
.globl test
test:
  add r0, r0, 1
  bu test
  retsp 0

.type main, @function
.globl main
main:
  entsp 1
  bl test
  retsp 1
