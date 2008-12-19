.text
.align 4
data:
.word 0x1234567f
.word 0xfe6a2c48
.word 0x56435434
.word 0xcd9c2745

.type function, @function
.globl function
function:
  ldap r11, data
  ldw r0, r11[0]
  ldw r1, r11[1]
  ldw r2, r11[2]
  crc32 r0, r1, r2
  ldw r1, r11[3]
  sub r0, r0, r1
  bf r0, true
  ldc r0, 0
  bu done
true:
  ldc r0, 1
done:
  retsp 0
  