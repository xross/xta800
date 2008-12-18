.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 0
  bf r1, true
  ldc r0, 0
  bu done
true:  
  ldc r0, 1
done:
  retsp 0

  