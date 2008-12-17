.text

.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
  entsp 1
  ldc r0, 1
  bu call

.align 4
call:
  bl test
  # A misaligned short instruction
  add r0, r0, r0
  retsp 1

.type test, @function
.globl test
test:
  entsp 1
  add r0, r0, r0
  retsp 1
