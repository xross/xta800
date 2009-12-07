.text
.xtathreadstart
.align 2
.globl _start
_start:
  bl main

.type test2, @function
.globl test2
test2:
  add r0, r0, 1
  bu test2
  retsp 0

.type test1, @function
.globl test1
test1:
  ldc r0, 100
  ldc r1, 100
  ldc r2, 100
  ldc r3, 100
  retsp 0

.type main, @function
.globl main
main:
  entsp 1
  bl test1
  bl test2
  retsp 1
