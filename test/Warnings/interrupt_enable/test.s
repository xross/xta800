.text

.align 2
.xtathreadstart
.globl _start
_start:
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.type main, @function
.globl main
main:
  setsr 0x2
  retsp 0

