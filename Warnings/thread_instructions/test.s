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
  ssync 
  msync res[r0]
  mjoin res[r0]
  retsp 0

