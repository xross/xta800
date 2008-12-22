.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 0
  sub r0, r0, 4
  ldc r1, 5
  maccs r0, r1, r0, r0
  ldc r2, 0x15
  sub r1, r1, r2
  add r0, r0, 4
  eq r0, r0, r1
  retsp 0
  