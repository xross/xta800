.text

.align 2
.globl _start
_start:
  bl main


.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  bf r0, end
  bau r1
end:
  retsp 1
