.text
.align 4
data:
.word 0x1234567f

.type function, @function
.globl function
function:
  ldap r11, data
  set dp, r11
  extdp 2
  sub r1, r11, 8
  ldaw r0, dp[0]
  eq r0, r0, r1
  retsp 0
  