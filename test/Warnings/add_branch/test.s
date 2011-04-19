.text
.align 2
.globl _start
_start:
.xtathreadstart
  bl main
.xtathreadstop

.type main, @function
.globl main
main:
  entsp 1
  bl test
  retsp 1
  
.type test, @function
.globl test
test:
source1:
.xtabranch target1
  bau r1
  
source2:
.xtabranch target4
  bau r1

target1:
  ldc r0, 0
  retsp 0

target2:
  retsp 0

target3:
  retsp 0

target4:
  retsp 0

