.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r0, 0
  bu branch
  retsp 0
branch:  
  ldc r0, 1
  retsp 0

  