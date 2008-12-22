.text
.align 4
data:
.word 0x100ec

.type function, @function
.globl function
function:
  entsp 1
  ldap r11, data
  ldw r11, r11[0]
  bla r11
  ldc r0, 1
  bu done
  ldc r0, 0
done:
  retsp 1
.type testfn, @function
.globl testfn
.align 4
testfn:
  entsp 0
  ldc r0, 1
  retsp 0
  