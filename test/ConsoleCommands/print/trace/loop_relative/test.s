.text

.align 2
.globl _start
.xtathreadstart
_start:
  bl main

.align 4
.type main, @function
.globl main
main:
  ldc r0, 10
loop:
  sub r0, r0, 1
  bt r0, loop
done:
  retsp 0
