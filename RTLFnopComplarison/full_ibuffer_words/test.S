.text
.align 2
.xtathreadstart
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
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  ldc r0, 0
  retsp 0
#include "common.s"
data:
.word 0
