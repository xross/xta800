.text

.align 2
.xtathreadstart
.globl _start
_start:
  bl main

.type rems, @function
.globl rems
rems:
  ldc r1, 4
  ldc r0, 17
  rems r0, r0, r1
  retsp 0

.type remu, @function
.globl remu
remu:
  ldc r1, 4
  ldc r0, 17
  remu r0, r0, r1
  retsp 0

.type divs, @function
.globl divs
divs:
  ldc r1, 4
  ldc r0, 17
  divs r0, r0, r1
  retsp 0

.type divu, @function
.globl divu
divu:
  ldc r1, 4
  ldc r0, 17
  divu r0, r0, r1
  retsp 0 

.type main, @function
.globl main
main:
  entsp 1

  bl rems
  bl remu
  bl divs
  bl divu
  retsp 1
