.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 1
  ldw r1, dp[0]
  ldc r2, 5
  stw r2, dp[0]
  ldw r0, dp[0]
  stw r1, dp[0]
  eq r0, r0, r2
  retsp 1
  

  
  
  