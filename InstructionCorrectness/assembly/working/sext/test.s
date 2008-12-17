.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldc r0, 0x4f
  sext r0, 4
  mkmsk r1, 32
  eq r0, r0, r1
  retsp 0
