.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 0x48
  ldc r1, 0x2
  lda16 r0, r0[r1]
  ldc r1, 0x4c
  eq r0, r0, r1
  retsp 0
  