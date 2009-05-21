.text

.align 2
.globl _start
_start:
  bl main

.align 4
.type main, @function
.globl main
main:
  ldc r0, 0
  bt r0, end
  ldc r0, 1
end:
  retsp 0
