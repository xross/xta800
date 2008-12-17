.text

.type function, @function
.globl function
function:
  entsp 0
  ldc r0, 0
  bu lbl0
lbl1:
  branch:  
  ldc r0, 1
  retsp 0
  .skip 128, 2
lbl0:  
  bu branch
  retsp 0
  

