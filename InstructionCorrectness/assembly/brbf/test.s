.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 0
  bu branch
true:  
  ldc r0, 1  
  bu done
branch:  
  bf r1, true
  ldc r0, 0
done:
  retsp 0

  