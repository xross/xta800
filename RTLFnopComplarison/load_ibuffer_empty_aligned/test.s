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
  bu empty
empty:
  ldw r0, r11[0]
  ldw r0, r11[0]
  retsp 0
