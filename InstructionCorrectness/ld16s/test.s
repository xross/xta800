.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  ldap r11, data
  ldc r0, 0
  ld16s r0, r11[r0]
  ldc r1, 0x567f
  eq r0, r0, r1
  retsp 0
  