.text

.type function, @function
.globl function
function:
  ldc r0, 0x4f5
  not r0, r0
  ldc r1, 0x4f5
  not r0, r0
  eq r0, r0, r1
  retsp 0
