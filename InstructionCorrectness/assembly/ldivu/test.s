.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 20
  ldc r2, 0
  ldc r1, 0xf53
  ldivu r0, r1, r2, r1, r0
  ldc r2, 0xc4
  sub r0, r0, r2
  sub r1, r1, 3
  eq r0, r0, r1
  retsp 0
  