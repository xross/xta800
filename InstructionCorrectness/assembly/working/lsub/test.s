.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 1
  ldc r1, 9
  lsub r0, r1, r0, r0, r0
  add r1, r1, 1
  sub r0, r0, 1
  eq r0, r0, r1
  retsp 0
  