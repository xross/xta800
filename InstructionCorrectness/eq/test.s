.text
.type function, @function
.globl function
function:
  ldc r0, 23
  ldc r1, 23
  eq r0, r1, r0
  retsp 0
  