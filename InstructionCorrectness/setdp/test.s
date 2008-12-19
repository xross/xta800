.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  entsp 0
  ldc r1, 12
  set dp, r1
  ldaw r11, dp[0]
  eq r0, r11, r1
  retsp 0
  

  
  
  