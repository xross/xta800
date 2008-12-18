.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldc r1, 4
  ldaw r11, cp[8]
  add r2, r11, 0
  set cp, r1
  ldaw r11, cp[8]
  set cp, r2
  ldc r1, 0x24
  eq r0, r11, r1
  retsp 0
  