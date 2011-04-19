.text

.align 2
.xtathreadstart
.type _start, @function
.globl _start
_start:
  bl main
  clre
.xtathreadstop
  waiteu

# Function e is a function in its own right, but is also 'contained'
# within function f. Note: we are using xta endpoints here because we need 
# to duplicate it, and this cannot be done with standard assembly labels..

.type e, @function
e:
  ldc r0, 0
.xtaendpoint "A", "", "", 0
endpointA_1:
  ldc r0, 1
  ldc r0, 2
  ldc r0, 3
  ldc r0, 4
  ldc r0, 9
  retsp 0

.type f, @function
f:
  ldc r0, 0
  bt r0, e
  retsp 0

# Function g is a function in its own right, but is also 'contained'
# within function h. Note: we are using xta endpoints here because we need 
# to duplicate it, and this cannot be done with standard assembly labels..

.type g, @function
g:
  ldc r0, 0
.xtaendpoint "A", "", "", 0
endpointA_2:
  ldc r0, 1
  ldc r0, 2
  ldc r0, 3
  ldc r0, 4
  ldc r0, 9
  retsp 0

.type h, @function
h:
  ldc r0, 0
  bt r0, g
  retsp 0

.type main, @function
.globl main
main:
  bl f
  bl h
  retsp 0

.section .xtaendpointtable,       "", @progbits
startOfEndpointTable:
   .int      endOfEndpointTable - startOfEndpointTable
   .int      0x00000000
   .asciiz   ""

   .asciiz  ""
   .int      0x00000000
   .long    endpointA_1

   .asciiz  ""
   .int      0x00000000
   .long    endpointA_2
endOfEndpointTable:
