.text

.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  setsr 0x2
  retsp 0

