.text

.align 2
.xtathreadstart
.globl _start
_start:
  bl main
  clre
  waiteu

.align 4
.type main, @function
.globl main
main:
  entsp 1
  bl f
  retsp 1

.type f, @function
f:
  nop
  nop
  nop
  retsp 0
