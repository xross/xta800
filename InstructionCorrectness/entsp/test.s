.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 2
  ldaw r1, sp[0]
  bl testfn
  eq r0, r1, r2
  retsp 2
  
.type testfn, @function
.globl testfn
testfn:
  entsp 1
  ldaw r2, sp[0]
  add r2, r2, 4
  retsp 1
  
  
  