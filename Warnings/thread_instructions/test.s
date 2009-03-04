.text

.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  ssync 
  msync res[r0]
  mjoin res[r0]
  retsp 0

