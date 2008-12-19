.text

.type function, @function
.globl function
function:
  ldc r0, 0x35
  ldc r1, 0x26
  mul r0, r0, r1
  ldc r1, 0x7de
  eq r0, r0, r1
  retsp 0
