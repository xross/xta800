.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldc r0, 0xff3
  ldc r1, 0x3d
  rems r0, r0, r1
  ldc r1, 0x39
  eq r0, r0, r1
  retsp 0
