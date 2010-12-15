

.extern GetSetupBufferLength
.globl GetSetupBufferLength.nstackwords
.linkset GetSetupBufferLength.nstackwords, 0
.globl GetSetupBufferLength.maxthreads
.linkset GetSetupBufferLength.maxthreads, 0
.globl GetSetupBufferLength.maxtimers
.linkset GetSetupBufferLength.maxtimers, 0
.globl GetSetupBufferLength.maxchanends
.linkset GetSetupBufferLength.maxchanends, 0
.globl GetSetupBufferLength
.text
.cc_top GetSetupBufferLength.func, GetSetupBufferLength

GetSetupBufferLength:
		ldaw	  r1, dp[sBuffer_Datasize]
    ldw     r0, r1[r0]
		retsp 0

.cc_bottom GetSetupBufferLength.func

/////////////

.extern GetOutBufferLength
.globl GetOutBufferLength.nstackwords
.linkset GetOutBufferLength.nstackwords, 0
.globl GetOutBufferLength.maxthreads
.linkset GetOutBufferLength.maxthreads, 0
.globl GetOutBufferLength.maxtimers
.linkset GetOutBufferLength.maxtimers, 0
.globl GetOutBufferLength.maxchanends
.linkset GetOutBufferLength.maxchanends, 0
.globl GetOutBufferLength
.text
.cc_top GetOutBufferLength.func, GetOutBufferLength

GetOutBufferLength:
		ldaw	  r1, dp[ep_datasize_table_OUT_A]
    ldw     r0, r1[r0]
		retsp 0

.cc_bottom GetOutBufferLength.func


.extern GetOutData
.globl GetOutData.nstackwords
.linkset GetOutData.nstackwords, 0
.globl GetOutData.maxthreads
.linkset GetOutData.maxthreads, 0
.globl GetOutData.maxtimers
.linkset GetOutData.maxtimers, 0
.globl GetOutData.maxchanends
.linkset GetOutData.maxchanends, 0
.globl GetOutData
.text
.cc_top GetOutData.func, GetOutData

GetOutData:
		ldaw			r2, dp[bufferSelectTable_OUT]		
    ldw     	r2, r2[r0]
		ldw				r0, r2[r1]
		retsp 0

.cc_bottom GetOutData.func

.globl SetOutBufferLength.nstackwords
.linkset SetOutBufferLength.nstackwords, 0
.globl SetOutBufferLength.maxthreads
.linkset SetOutBufferLength.maxthreads, 0
.globl SetOutBufferLength.maxtimers
.linkset SetOutBufferLength.maxtimers, 0
.globl SetOutBufferLength.maxchanends
.linkset SetOutBufferLength.maxchanends, 0
.globl SetOutBufferLength
.text
.cc_top SetOutBufferLength.func, SetOutBufferLength

SetOutBufferLength:
		ldaw	  r2, dp[ep_datasize_table_OUT_A]
    stw     r1, r2[r0]
		retsp 0

.cc_bottom SetOutBufferLength.func


////////
.extern GetSetupData
.globl GetSetupData.nstackwords
.linkset GetSetupData.nstackwords, 0
.globl GetSetupData.maxthreads
.linkset GetSetupData.maxthreads, 0
.globl GetSetupData.maxtimers
.linkset GetSetupData.maxtimers, 0
.globl GetSetupData.maxchanends
.linkset GetSetupData.maxchanends, 0
.globl GetSetupData
.text
.cc_top GetSetupData.func, GetSetupData

GetSetupData:
		ldaw			r2, dp[sBufferSelectTable]		
    ldw     	r2, r2[r0]
		ldw				r0, r2[r1]
		retsp 0

.cc_bottom GetSetupData.func

.globl SetSetupBufferLength.nstackwords
.linkset SetSetupBufferLength.nstackwords, 0
.globl SetSetupBufferLength.maxthreads
.linkset SetSetupBufferLength.maxthreads, 0
.globl SetSetupBufferLength.maxtimers
.linkset SetSetupBufferLength.maxtimers, 0
.globl SetSetupBufferLength.maxchanends
.linkset SetSetupBufferLength.maxchanends, 0
.globl SetSetupBufferLength
.text
.cc_top SetSetupBufferLength.func, SetSetupBufferLength

SetSetupBufferLength:
		ldaw	  r2, dp[sBuffer_Datasize]
    stw     r1, r2[r0]
		retsp 0

.cc_bottom SetSetupBufferLength.func

.extern GetInBufferLength
.globl GetInBufferLength.nstackwords
.linkset GetInBufferLength.nstackwords, 0
.globl GetInBufferLength.maxthreads
.linkset GetInBufferLength.maxthreads, 0
.globl GetInBufferLength.maxtimers
.linkset GetInBufferLength.maxtimers, 0
.globl GetInBufferLength.maxchanends
.linkset GetInBufferLength.maxchanends, 0
.globl GetInBufferLength
.text
.cc_top GetInBufferLength.func, GetInBufferLength

GetInBufferLength:
		ldaw      r1, dp[ep_datasize_table_IN_A]
    ldw       r0, r1[r0]
		retsp     0

.cc_bottom GetInBufferLength.func

// epNum, Index, Data
.extern SetInData
.globl SetInData.nstackwords
.linkset SetInData.nstackwords, 0
.globl SetInData.maxthreads
.linkset SetInData.maxthreads, 0
.globl SetInDataData.maxtimers
.linkset SetInData.maxtimers, 0
.globl SetInData.maxchanends
.linkset SetInData.maxchanends, 0
.globl SetInData
.text
.cc_top SetInData.func, GetSetupData

SetInData:
	  ldaw       r3, dp[bufferSelectTable_IN]   // Load buffer select table
  	ldw			   r3, r3[r0]		
    stw        r2, r3[r1]
		retsp 0

.cc_bottom SetInData.func


.extern SetInBufferLength
.globl SetInBufferLength.nstackwords
.linkset SetInBufferLength.nstackwords, 0
.globl SetInBufferLength.maxthreads
.linkset SetInBufferLength.maxthreads, 0
.globl SetInBufferLength.maxtimers
.linkset SetInBufferLength.maxtimers, 0
.globl SetInBufferLength.maxchanends
.linkset SetInBufferLength.maxchanends, 0
.globl SetInBufferLength
.text
.cc_top SetInBufferLength.func, SetInBufferLength

SetInBufferLength:
		ldaw      r2, dp[ep_datasize_table_IN_A]
    stw       r1, r2[r0]
		retsp     0

.cc_bottom SetInBufferLength.func



