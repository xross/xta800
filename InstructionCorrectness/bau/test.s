.text
.align 4
data:
.word 0x100e2

.type function, @function
.globl function
function:
  ldap r11, data
  ldw r0, r11[0]
  bau r0
  ldc r0, 1
  bu done
  ldc r0, 0
done:
  retsp 0
