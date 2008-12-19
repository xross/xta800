.text

.type function, @function
.globl function
function:
  ldc r0, 0x4f5
  ldc r1, 0x2f6
  sub r0, r0, r1
  ldc r1, 0x1ff
  eq r0, r0, r1
  retsp 0
