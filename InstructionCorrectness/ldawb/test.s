.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 0xda
  ldc r1, 0x0
  sub r1, r1, 4
  ldaw r0, r0[r1]
  ldc r1, 0xca
  eq r0, r0, r1
  retsp 0
  