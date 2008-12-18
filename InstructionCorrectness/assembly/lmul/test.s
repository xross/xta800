.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 7
  lmul r0, r1, r0, r0, r0, r0
  ldc r2, 0x3f
  add r0, r0, r2
  eq r0, r0, r1
  retsp 0
  