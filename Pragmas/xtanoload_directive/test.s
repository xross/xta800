.text
.xtanoload "printf", "compilation_directory", "test.xc", 1

.align 2
.globl _start
_start:
.xtathreadstart
  bl main
  clre
.xtathreadstop
  waiteu

.type main, @function
.globl main
main:
  entsp 1
  bl printf
  retsp 1

printf:
  retsp 0
