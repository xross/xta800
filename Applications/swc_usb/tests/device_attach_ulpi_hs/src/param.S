.extern GetParam
.globl GetParam.nstackwords
.linkset GetParam.nstackwords, 0
.globl GetParam
.text
GetParam:
ldaw r1, dp[params]
ldw  r0, r1[r0]
retsp 0

.extern GetSamp
.globl GetSamp.nstackwords
.linkset GetSamp.nstackwords, 0
.globl GetSamp
.text
GetSamp:
ldw  r0, dp[samp]
retsp 0

.extern SetSamp
.globl SetSamp.nstackwords
.linkset SetSamp.nstackwords, 0
.globl SetSamp
.text
SetSamp:
stw  r0, dp[samp]
retsp 0

