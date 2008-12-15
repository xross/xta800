.text

data:
.word 0
 
.align 2
.globl _start
_start:
  bl main
  
.type main, @function
.globl main
main:
  entsp 0
  ldap r11, data
  bu empty0
empty0:
  ldw r0, r11[0]
  ldw r0, r11[0]
  
  ldc r0, 0
  ldw r0, r11[0]
  .align 4
align0:

.skip 2, 0
  ldc r0, 0
  
  bu empty1
empty1:
  ldc r0, 0
  ldc r0, 0
  #ldw sould get blocked
  ldw r0, r11[0]
  .align 4
align1:

.skip 2, 0
  ldc r0,0
  
  retsp 0
