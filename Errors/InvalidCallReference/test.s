.text

.xtathreadstart
.align 2
.globl _start
_start:
  bl main
  clre
  waiteu

.type main, @function
.globl main
main:
  nop
  retsp 0
