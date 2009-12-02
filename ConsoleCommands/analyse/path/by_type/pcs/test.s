.text
.align 2
.globl _start
_start:
.xtathreadstart
  ldap r11, stack
  set sp, r11
  bl main
  clre
.xtathreadstop
stop:
.xtabranch stop
  waiteu

.align 0x40
.type main, @function
.globl main
main:
  entsp 1
  bl g
  bu done

.align 0x40
done:
  retsp 1

.align 4
.type g, @function
.globl g
g:
  retsp 0

.section .dp.data, "adw", @progbits
.align 4
stack:
.word 0,0,0,0
