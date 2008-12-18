.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  ldc r0, 0xff
  zext r0, 4
  ldc r1, 0xf
  eq r0, r0, r1
  retsp 0
  