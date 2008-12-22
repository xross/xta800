.text
.align 4
data:
.word 0xFaaa0000
.word 0xFFaaa000

.type function, @function
.globl function
function:
  ldap r11, data
  ldw r0, r11[0]
  ldw r2, r11[1]
  ldc r1, 0x4
  ashr r0, r0, r1
  sub r0, r0, r2
  bf r0, true
  ldc r0, 0
  bu done
  true:
  ldc r0, 1
done:
  retsp 0
