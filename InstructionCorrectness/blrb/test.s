.text

.type testfn, @function
.globl testfn
.align 4
testfn:
  entsp 0
  ldc r0, 1
  retsp 0
  
.type function, @function
.globl function
function:
  entsp 1
  bl testfn
  ldc r0, 1
  bu done
  ldc r0, 0
done:
  retsp 1

  