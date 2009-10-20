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
  in r0, res[r0]
  retsp 0
