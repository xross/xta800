.text

.align 2
.globl _start
.xtathreadstart
_start:
  ldap r11, stack
  set sp, r11
  bl main
  clre
.xtathreadstop
  waiteu

.align 4
.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 5
  ldc r3, 1
  ldc r4, 400
  ldc r2, 200
loop:
  and r1, r0, r3
  bt r1, else
if:
  bl a
  bu loopEnd
else:
  bl b
loopEnd:
  sub r0, r0, 1
  bt r0, loop
  retsp 1

.align 4
.type a, @function
.globl a
a:
  ldc r5, 3
a_loop:
  and r1, r5, r3
  bt r1, a_else
a_if:
  xor r1, r1, r2
  bu a_loopEnd
a_else:
  not r1, r1
a_loopEnd:
  sub r5, r5, 1
  bt r5, a_loop
  retsp 0
  
.align 4
.type b, @function
.globl b
b:
  ldc r5, 2
b_loop:
  and r1, r5, r3
  bt r1, b_else
b_if:
  neg r1, r1
  bu b_loopEnd
b_else:
  andnot r1, r1
b_loopEnd:
  sub r5, r5, 1
  bt r5, b_loop
  retsp 0

.section .dp.data, "adw", @progbits
.align 4
stack:
.word 0,0,0,0
