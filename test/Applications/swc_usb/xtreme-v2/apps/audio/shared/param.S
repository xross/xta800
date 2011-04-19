.extern GetParam
.globl GetParam.nstackwords
.linkset GetParam.nstackwords, 0
.globl GetParam.maxthreads
.linkset GetParam.maxthreads, 0
.globl GetParam.maxtimers
.linkset GetParam.maxtimers, 0
.globl GetParam.maxchanends
.linkset GetParam.maxchanends, 0
.globl GetParam
.text
.cc_top GetParam.func, GetParam
GetParam:
ldaw r1, dp[shared_params]
ldw  r0, r1[r0]
retsp 0
.cc_bottom GetParam.func

    .extern SetParam
.globl SetParam.nstackwords
.linkset SetParam.nstackwords, 0
.globl SetParam.maxthreads
.linkset SetParam.maxthreads, 0
.globl SetParam.maxtimers
.linkset SetParam.maxtimers, 0
.globl SetParam.maxchanends
.linkset SetParam.maxchanends, 0
.globl SetParam
.text
.cc_top SetParam.func, SetParam
SetParam:
ldaw r2, dp[shared_params]
stw  r1, r2[r0]
retsp 0
.cc_bottom SetParam.func

.extern GetSamp
.globl GetSamp.nstackwords
.linkset GetSamp.nstackwords, 0
.globl GetSamp.maxthreads
.linkset GetSamp.maxthreads, 0
.globl GetSamp
.text
.cc_top GetSamp.func, GetSamp
GetSamp:
ldw  r0, dp[samp]
retsp 0
.cc_bottom GetSamp.func

.extern SetSamp
.globl SetSamp.nstackwords
.linkset SetSamp.nstackwords, 0
.globl SetSamp.maxthreads
.linkset SetSamp.maxthreads, 0
.globl SetSamp
.text
.cc_top SetSamp.func, SetSamp
SetSamp:
stw  r0, dp[samp]
retsp 0
.cc_bottom SetSamp.func

.extern GetBufferAddress


.globl GetBufferAddress.maxtimers
.globl GetBufferAddress.maxchanends
.linkset GetBufferAddress.maxtimers, 0
.linkset GetBufferAddress.maxchanends, 0
.globl GetBufferAddress.nstackwords
.linkset GetBufferAddress.nstackwords, 0
.globl GetBufferAddress.maxthreads
.linkset GetBufferAddress.maxthreads, 0
.globl GetBufferAddress
.text
.cc_top GetBufferAddress.func, GetBufferAddress
GetBufferAddress:
retsp 0
.cc_bottom GetBufferAddress.func

.extern GetInt
.globl GetInt.nstackwords
.linkset GetInt.nstackwords, 0
.globl GetInt.maxthreads
.linkset GetInt.maxthreads, 0
.globl GetInt.maxtimers
.linkset GetInt.maxtimers, 0
.globl GetInt.maxchanends
.linkset GetInt.maxchanends, 0
.globl GetInt
.text
.cc_top GetInt.func, GetInt
GetInt:
ldw r0, r0[r1]
retsp 0
.cc_bottom GetInt.func

.extern SetInt
.globl SetInt.nstackwords
.linkset SetInt.nstackwords, 0
.globl SetInt.maxthreads
.linkset SetInt.maxthreads, 0
.globl SetInt.maxtimers
.linkset SetInt.maxtimers, 0
.globl SetInt.maxchanends
.linkset SetInt.maxchanends, 0
.globl SetInt
.text
.cc_top SetInt.func, SetInt
SetInt:
stw r2, r0[r1]
retsp 0
.cc_bottom SetInt.func


.extern GetData
.globl GetData.nstackwords
.linkset GetData.nstackwords, 0
.globl GetData.maxthreads
.linkset GetData.maxthreads, 0
.globl GetData.maxtimers
.linkset GetData.maxtimers, 0
.globl GetData.maxchanends
.linkset GetData.maxchanends, 0
.globl GetData
.text
.cc_top GetData.func, GetData
GetData:
ldw r0, dp[data]
retsp 0
.cc_bottom GetData.func
