.text

.type function, @function
.globl function
function:
  ldc r0, 0x4
  ldc r1, 0
  sub r1, r1, r0
  neg r0, r0
  eq r0, r0, r1
  retsp 0
