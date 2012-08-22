.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main
.xtathreadstop
  ldc r0, 0x0

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
path_1:
  ldc r0, 0x0
  bt r0, main
  ldc r0, 0x0
  ldc r0, 0x0
path_2:
  ldc r0, 0x0
  bt r0, main
  ldc r0, 0x0
path_3:
  ldc r0, 0x0
  ldc r0, 0x0
  bt r0, main
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  retsp 1

