.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  ldaw r2, sp[0]
  ldap r11, data
  set sp, r11
  extsp 2
  sub r1, r11, 8
  ldaw r0, sp[0]
  eq r0, r0, r1
  set sp, r2
  retsp 0
  