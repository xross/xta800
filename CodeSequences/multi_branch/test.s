.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  entsp 1

  BRFU_u6 .L2 
.L2:
  BRFF_lru6 r0, .L3
.L3:
  BRFT_lru6 r1, .L4
.L4:
  BRFU_lu6 .L5
.L5:
  BRFF_ru6 r0, .L6
.L6:
  BRFT_ru6 r1, .L7
.L7:
  BRFU_lu6 .L8
.L8:

  retsp 1
