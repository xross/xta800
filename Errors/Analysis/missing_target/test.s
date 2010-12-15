.text

.align 2
.globl _start
_start:
.xtathreadstart
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.type main, @function
.globl main
main:
  entsp 1
  bl targetInData
  retsp 1

.section .dp.data,       "adw", @progbits
.align    4
targetInData:
.int 0x0
