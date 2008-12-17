.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r0, 2
  ldc r1, 5
  ladd r0, r1, r0, r0, r0
  sub r1, r1, 4
  sub r0, r0, 0
  eq r0, r0, r1
  retsp 0
  