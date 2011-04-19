.text

.align 2
.globl _start
.xtathreadstart
_start:
  bl main

.align 4
.type main, @function
.globl main
main:
  mov r0, r1
  bu main
