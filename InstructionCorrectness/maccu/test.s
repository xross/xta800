.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 11
  ldc r1, 9
  maccu r0, r1, r0, r0
  ldc r2, 0x82
  sub r1, r1, r2
  ldc r2, 0xb
  sub r0, r0, r2
  eq r0, r0, r1
  retsp 0
  