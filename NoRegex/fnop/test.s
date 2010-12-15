.text

.xtathreadstart
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  bl test
  retsp 0

.type test, @function
.globl test
test:
  retsp 0
