.align 4
.type label1, @function
.globl label1
label1:
  entsp 1
  bl label2
  retsp 1

.align 4
.type label2, @function
.globl label2
label2:
  retsp 0
