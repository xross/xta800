.xtacommand "analyse path STARTPOINT ENDPOINT"
.xtacommand "print completed_paths"

.text
.align 2
.globl _start
_start:
  bl main

.type main, @function
.globl main
main:
STARTPOINT:
  ldc r0, 0x0
ENDPOINT:
  retsp 0

