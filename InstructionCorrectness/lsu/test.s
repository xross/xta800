.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldc r0, 0x0
  sub r0, r0, 2
  ldc r1, 0xf0
  lsu r0, r1, r0 
  retsp 0
