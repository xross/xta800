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
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  .word 0xbaddf00d

COMPLETE:
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  retsp 1
