.text
.align 4
data:
.word 0x1234567f
.word 0x7f563412

.type function, @function
.globl function
function:
  ldap r11, -4
  ldc r1, 0xda
  zext r11, 8
  eq r0, r11, r1
  retsp 0
  