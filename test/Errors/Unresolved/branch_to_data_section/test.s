.section data
DataSection:
.word 0x000000

.text
.align 2
.globl _start
.xtathreadstart
_start:
  bl main

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  bu DataSection

COMPLETE:
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  retsp 1
