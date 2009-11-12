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
a:
  in r0, res[r0]
  nop
  nop
  nop
b:
  in r0, res[r0]
c:
  in r0, res[r0]
  retsp 0
