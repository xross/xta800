.text

.align 2
.globl _start
_start:
  bu main

.type main, @function
.globl main
main:
  bl test
  retsp 0

.type test, @function
.globl test
test:
  retsp 0
