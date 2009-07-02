.text
.align 2
.globl _start
.xtathreadstart
_start:
  bl main
  clre 
.xtathreadstop
  waiteu

.type main, @function
.globl main
main:

INPUT_1:
  in r0, res[r0]

  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0

INPUT_2:
  in r0, res[r0]

  retsp 0

