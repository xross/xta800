.text

.align 2
.xtathreadstart
.globl _start
_start:
  bl main
  clre
.xtathreadstop
  waiteu

.type main, @function
.globl main
main:
  retsp 0
