.extern GetParam
.globl GetParam.nstackwords
.linkset GetParam.nstackwords, 0
.globl GetParam
.type GetParam, @function
.text
GetParam:
ldaw r1, dp[params]
ldw  r0, r1[r0]
retsp 0

.extern GetSamp
.globl GetSamp.nstackwords
.linkset GetSamp.nstackwords, 0
.globl GetSamp
.type GetSamp, @function
.text
GetSamp:
ldw  r0, dp[samp]
retsp 0

.extern SetSamp
.globl SetSamp.nstackwords
.linkset SetSamp.nstackwords, 0
.globl SetSamp
.type SetSamp, @function
.text
SetSamp:
stw  r0, dp[samp]
retsp 0

.extern GetBufferAddress
.globl GetBufferAddress.nstackwords
.linkset GetBufferAddress.nstackwords, 0
.globl GetBufferAddress
.type GetBufferAddress, @function
.text
GetBufferAddress:
retsp 0

.extern GetInt
.globl Getint.nstackwords
.linkset GetInt.nstackwords, 0
.globl GetInt
.type GetInt, @function
.text
GetInt:
ldw r0, r0[r1]
retsp 0

.extern GetData
.globl GetData.nstackwords
.linkset GetData.nstackwords, 0
.type GetData, @function
.globl GetData
.text
GetData:
ldw r0, dp[data]
retsp 0


