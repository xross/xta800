.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 0xFFFF
  mkmsk r0, 16
  eq r0, r0, r1
  retsp 0
  

  
  
  