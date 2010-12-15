.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.xtasystem "main", "dir", "test.s", 0

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0

.xtabranch Ltarget1, Ltarget2, Ltarget3, Ltarget4
  bau r1

Ltarget1:
  bla r2
  ldc r0, 4
  bu return

Ltarget2:
  ldc r0, 3
  bu return

Ltarget3:
  ldc r0, 2
  bu return

Ltarget4:
  ldc r0, 1
  bu return

return:
  retsp 1
