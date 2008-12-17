.text
.align 4
data:
.word 0x1234567f
.word 0xfe6a2c48

.type function, @function
.globl function
function:
  ldap r11, data
  ldw r0, r11[0]
  ldw r1, r11[1]
  bitrev r0, r0
  sub r0, r0, r1
  bf r0, true
  ldc r0, 0
  bu done
true:
  ldc r0, 1
done:
  retsp 0
  