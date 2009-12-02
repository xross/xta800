.text

.xtathreadstart
.align 2
.globl _start
_start:
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.type main, @function
.globl main
main:
  ldc r0, 0
  retsp 0
