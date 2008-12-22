.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 10
  bru r1
  ldc r0, 0
  retsp 0
  .skip 16, 2
  ldc r0, 1
  retsp 0

  