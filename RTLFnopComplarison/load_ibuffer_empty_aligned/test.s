.text

.align 2
.globl _start
_start:
  ldap r11, data
  set sp, r11
  bl main
  bl _exit
  
.type main, @function
.globl main
main:
  entsp 0
  ldap r11, data
  bu empty

.align 4
empty:
  ldw r0, r11[0]
  ldw r0, r11[0]
  retsp 0
  
  
#include "common.s"
data:
.word 0
