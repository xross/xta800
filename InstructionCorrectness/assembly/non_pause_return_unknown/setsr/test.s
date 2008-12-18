.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  getsr r11, 0xff
  clrsr 0xff
  add r1, r11, 0
  getsr r11, 0xff
  ldc r0, 0
  eq r0, r11, r0
  setsr 0xff
  retsp 0
