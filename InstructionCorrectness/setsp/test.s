.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldaw r11, sp[0]
  add r2, r11, 0
  ldc r1, 12
  set sp, r1
  ldaw r11, sp[0]
  eq r0, r11, r1
  set sp, r2
  retsp 0
  

  
  
  