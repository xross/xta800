.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  get r11, id
  ldc r0, 0
  eq r0, r0, r11
  retsp 0
