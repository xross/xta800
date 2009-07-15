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
  ldc r0, 5
  bt r0, else
if:
  divu r0, r1, r2
  bu done
else:
loop:
  sub r0, r0, 1
  bt r0, loop
done:
  retsp 0
