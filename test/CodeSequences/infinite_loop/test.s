.text
.xtathreadstart
.align 2
.globl _start
_start:
  bl main

.type test, @function
.globl test
test:
  bu test
  retsp 0

.type main, @function
.globl main
main:
  entsp 1
  bl test
  retsp 1
