.text
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
.xtathreadstop
  entsp 1
.xtathreadstop
  ldc r0, 0x0
.xtathreadstop
  ldc r0, 0x0
.xtathreadstop
  ldc r0, 0x0
.xtathreadstop
  retsp 1

