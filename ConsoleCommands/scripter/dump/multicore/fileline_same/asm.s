.text
.align 2
.type asmfunc, @function
.globl asmfunc
asmfunc:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0

Lbranch:
  bau r1

Ltarget1:
  retsp 1

Ltarget2:
  retsp 1

Ltarget3:
  retsp 1

Ltarget4:
  retsp 1

