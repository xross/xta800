.text
.align 4
data:
.word 0x0
.word 0x0
.type function, @function
.globl function
function:
  ldap r11, data
  ldc r0, 0xef
  ldc r1, 1
  st8 r0, r11[r1]
  ldc r1, 0
  ldw r0, r11[r1]
  ldc r1, 0xef00
  eq r0, r0, r1
  retsp 0
  