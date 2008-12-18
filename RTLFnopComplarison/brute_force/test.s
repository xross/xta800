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
  # initialise data pointer
  ldap r11, data
  retsp 0
