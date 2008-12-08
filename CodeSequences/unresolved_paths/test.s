.text

.align 2
.globl _start
_start:
  bl main


.type main, @function
.globl main
main:
  entsp 1
  bau r1


  retsp 1
