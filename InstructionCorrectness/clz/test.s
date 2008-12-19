.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 0xF
  shl r1, r1, 24
  clz r0, r1
  sub r1, r0, 4
  ldc r0, 0
  bf r1, done
  retsp 0
done:  
  ldc r0, 1
  retsp 0

  