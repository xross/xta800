.globl _start
.xtathreadstart
_start:
  bl main

.globl main
main:
  entsp 1

  add r0 , r11, 11
  add r0, r11, r11

label2:

  bt r1, label2
  bf r0, label2

label1:

  retsp 1
