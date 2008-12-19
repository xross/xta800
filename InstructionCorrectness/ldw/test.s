.text
.align 4
data:
.word 0x0
.word 0x0
.word 0x0

.type function, @function
.globl function
function:
  ldap r11, data
  ldw r0, r11[0]
  add r1, r0, 0
  add r0, r0, 5
  stw r0, r11[2]
  ldw r0, r11[2]
  sub r0, r0, 5
  stw r0, r11[0]
  eq r0, r0, r1
  retsp 0
  