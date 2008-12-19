.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 12
  set cp, r1
  ldaw r11, cp[1]
  sub r11, r11, 4
  eq r0, r11, r1
  retsp 0
  