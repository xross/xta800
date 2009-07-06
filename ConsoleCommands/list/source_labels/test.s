.globl _start
.xtathreadstart
_start:
  bl main

.globl main
main:
  entsp 1

  ADD_2rus r0 , r11, 11
  ADD_3r r0, r11, r11

label2:

  BRBT_lru6 r1, label2
  BRBF_lru6 r0, label2

label1:

  retsp 1
