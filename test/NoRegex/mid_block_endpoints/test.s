.text

.align 2
.xtathreadstart
.globl _start
_start:
  bu main

.type main, @function
.globl main
main:
  ldc r0, 0
  ldc r0, 1
  ldc r0, 2
  ldc r0, 3
  ldc r0, 4
  ldc r0, 5
  ldc r0, 6
  ldc r0, 7
  ldc r0, 8
  ldc r0, 9
  retsp 0
