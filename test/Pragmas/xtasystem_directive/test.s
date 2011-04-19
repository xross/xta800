.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main

.xtasystem "main", "dir", "test.s", 0

.type main, @function
.globl main
main:
  entsp 1
.xtathreadstop
  retsp 1

