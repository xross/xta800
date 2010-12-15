.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main
.xtathreadstop
  clre
  waiteu

.type main, @function
.globl main
main:
  entsp 1

.xtacall "call1", "", "", 0
  bl test

.xtacall "call2", "", "", 0
  bl test
  retsp 1
  
.type test, @function
.globl test
test:
  bla r1
  retsp 0

.type foo, @function
.globl foo
foo:
  retsp 0

.type bar, @function
.globl bar
bar:
  retsp 0

