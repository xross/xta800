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
  entsp 1
  ldaw r1, sp[0]
  set cp, r1
  ldc r0, 1
  bu call
 
.align 4
aligned:
call:
  bl test
  ldw r1, cp[64]
  retsp 1

.type test, @function
.globl test
test:
  add r0, r0, r0
  retsp 0

.align 4
data:
.word 0
.word 0

#include "common.s"
