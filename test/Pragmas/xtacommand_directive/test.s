.text

.xtacommand "analyse function main", "compilation_directory", "test.xc", 1
.xtacommand "print structure -", "compilation_directory", "test.xc", 2

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
  retsp 1

