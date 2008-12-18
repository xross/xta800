.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  ldc r0, 0x1
  retsp 0
  