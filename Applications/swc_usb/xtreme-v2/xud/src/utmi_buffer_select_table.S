


// utmi_buffer_select_table.S



//getPidSeq_IN_B(epNum)
.globl getPidSeq_IN_B
.globl getPidSeq_IN_B.nstackwords
.linkset getPidSeq_IN_B.nstackwords, 0 
.cc_top getPidSeq_IN_B.func, getPidSeq_IN_B
getPidSeq_IN_B:
 ldaw	r1, dp[ep_pid_sequence_table_IN_B]
 ldw	r0, r1[r0]			    // Pid B
 //ldaw  r1, dp[ep_pid_sequence_table_IN_A]
 //stw   r2, r1[r0]                          // Store to A
 retsp 0
//ldaw         r11, dp[ep_pid_sequence_table_IN]
 //ld8u         r9, r11[r10]
 //ldc          r8, 0x88
 //xor          r9, r9, r8
 //st8          r9, r11[r10]
.cc_bottom getPidSeq_IN_B.func



////////////////////////////////////////////////////////////////////////////
// unsigned setPidBtoA_IN(unsigned epNum);
.globl setPidAtoB_IN
.globl setPidAtoB_IN.nstackwords
.linkset setPidAtoB_IN.nstackwords, 0 
.cc_top setPidAtoB_IN.func, setPidAtoB_IN
setPidAtoB_IN:
 ldaw	r1, dp[ep_pid_sequence_table_IN_B]
 ld8u	r2, r1[r0]			    // Pid B
 ldaw  r1, dp[ep_pid_sequence_table_IN_A]
 st8   r2, r1[r0]                          // Store to A
 retsp 0
.cc_bottom setPidAtoB_IN.func




////////////////////////////////////////////////////////////////////////////
// unsigned setDatasizeAtoB_IN(unsigned epNum);
.globl setDatasizeAtoB_IN
.globl setDatasizeAtoB_IN.nstackwords
.linkset setDatasizeAtoB_IN.nstackwords, 0 
.cc_top setDatasizeAtoB_IN.func, setDatasizeAtoB_IN
setDatasizeAtoB_IN:
 ldaw	r1, dp[ep_datasize_table_IN_B]
 ldw	r2, r1[r0]			// Buffer B data size
 ldc   r11, 0			        // Reset buffer B datasize
 stw   r11, r1[r0]	
 ldaw  r1, dp[ep_datasize_table_IN_A]
 stw   r2, r1[r0]
 retsp 0
.cc_bottom setDatasizeAtoB_IN.func

////////////////////////////////////////////////////////////////////////////
// void setDatasizeBtoA_OUT(unsigned epNum);
.globl   SetDatasizeBtoA_OUT
.globl   SetDatasizeBtoA_OUT.nstackwords
.linkset SetDatasizeBtoA_OUT.nstackwords, 0 
.cc_top SetDatasizeBtoA_OUT.func, SetDatasizeBtoA_OUT
SetDatasizeBtoA_OUT:
 ldaw	r1, dp[ep_datasize_table_OUT_A]
 ldw	r2, r1[r0]			
 ldc   r11, 0			        
 stw   r11, r1[r0]	
 ldaw  r1, dp[ep_datasize_table_OUT_B]
 stw   r2, r1[r0]
 retsp 0
.cc_bottom SetDatasizeBtoA_OUT.func

////////////////////////////////////////////////////////////////////////////
// unsigned swapBufferPointers_OUT(unsigned epNum);
.globl   swapBufferPointers_OUT
.globl   swapBufferPointers_OUT.nstackwords
.linkset swapBufferPointers_OUT.nstackwords, 0 
.cc_top swapBufferPointers_OUT.func, swapBufferPointers_OUT
swapBufferPointers_OUT:
 ldaw	r1, dp[bufferSelectTable2_OUT]
 ldw	r2, r1[r0]			// Buffer B Address
 ldaw  r3, dp[bufferSelectTable_OUT]
 ldw	r11, r3[r0]			// Buffer A Address
 stw	r11, r1[r0]			// Do the swap
 stw   r2, r3[r0]
 retsp 0
.cc_bottom swapBufferPointers_OUT.func


////////////////////////////////////////////////////////////////////////////
// unsigned swapBufferPointers_OUT(unsigned epNum);
.globl swapBufferPointers_IN
.globl swapBufferPointers_IN.nstackwords
.linkset swapBufferPointers_IN.nstackwords, 0 
.cc_top swapBufferPointers_IN.func, swapBufferPointers_IN
swapBufferPointers_IN:
 ldaw	r1, dp[bufferSelectTable2_IN]
 ldw	r2, r1[r0]			// Buffer B Address
 ldaw  r3, dp[bufferSelectTable_IN]
 ldw	r11, r3[r0]			// Buffer A Address
 stw	r11, r1[r0]			// Do the swap
 stw   r2, r3[r0]
 retsp 0
.cc_bottom swapBufferPointers_IN.func


// TODO: Move these functions
//////////////////////////////////////////////////////////////////////////////
// unsigned getUint(unsigned address, int index);
.globl getUint
.globl getUint.nstackwords
.linkset getUint.nstackwords, 0 
.cc_top getUint.func, getUint
getUint:
 ldw	r0, r0[r1]
 retsp	0
.cc_bottom getUint.func

//////////////////////////////////////////////////////////////////////////////
// unsigned setUint(unsigned address, int index, unsigned data);
.globl setUint
.globl setUint.nstackwords
.linkset setUint.nstackwords, 0
.cc_top setUint.func, setUint
setUint:
 stw	r2, r0[r1]
 retsp 0
.cc_bottom setUint.func

//////////////////////////////////////////////////////////////////////////////
// unsigned GetArrayAddress(unsigned array[]);
.globl GetArrayAddress
.globl GetArrayAddress.nstackwords
.linkset GetArrayAddress.nstackwords, 0
.cc_top GetArrayAddress.function,GetArrayAddress
GetArrayAddress:
 retsp 0
.cc_bottom GetArrayAddress.function

//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_IN_B(unsigned epNum);
.globl GetDatasize_IN_B
.globl GetDatasize_IN_B.nstackwords
.linkset GetDatasize_IN_B.nstackwords, 0
.cc_top GetDatasize_IN_B.func, GetDatasize_IN_B
GetDatasize_IN_B:
 ldaw	r11, dp[ep_datasize_table_IN_B]
 ldw   r0, r11[r0] 
 retsp 0
.cc_bottom GetDatasize_IN_B.func

//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_IN_A(unsigned epNum);
.globl GetDatasize_IN_A
.globl GetDatasize_IN_A.nstackwords
.linkset GetDatasize_IN_A.nstackwords, 0
.cc_top GetDatasize_IN_A.func, GetDatasize_IN_A
GetDatasize_IN_A:
 ldaw	r11, dp[ep_datasize_table_IN_A]
 ldw   r0, r11[r0] 
 retsp 0
.cc_bottom GetDatasize_IN_A.func



//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_OUT_B(unsigned epNum);
.globl 	 GetDatasize_OUT_B
.globl   GetDatasize_OUT_B.nstackwords
.linkset GetDatasize_OUT_B.nstackwords, 0
.cc_top GetDatasize_OUT_B.func, GetDatasize_OUT_B
GetDatasize_OUT_B:
 ldaw	r11, dp[ep_datasize_table_OUT_B]
 ldw   r0, r11[r0] 
 retsp 0
.cc_bottom GetDatasize_OUT_B.func

//////////////////////////////////////////////////////////////////////////////
//void SetDatasize_OUT_B(unsigned epNum);
.globl 	 SetDatasize_OUT_B
.globl   SetDatasize_OUT_B.nstackwords
.linkset SetDatasize_OUT_B.nstackwords, 0
.cc_top SetDatasize_OUT_B.func, SetDatasize_OUT_B
SetDatasize_OUT_B:
 ldaw	r11, dp[ep_datasize_table_OUT_B]
 stw   r1, r11[r0] 
 retsp 0
.cc_bottom SetDatasize_OUT_B.func



//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_OUT_A(unsigned epNum);
.globl 	 GetDatasize_OUT_A
.globl   GetDatasize_OUT_A.nstackwords
.linkset GetDatasize_OUT_A.nstackwords, 0
.cc_top GetDatasize_OUT_A.func, GetDatasize_OUT_A
GetDatasize_OUT_A:
 ldaw	r11, dp[ep_datasize_table_OUT_A]
 ldw   r0, r11[r0] 
 retsp 0
.cc_bottom GetDatasize_OUT_A.func



.globl sBufferSelectTable
.globl sBufferSelectTable.globound
.set sBufferSelectTable.globound, 5
.section .dp.data,       "adw", @progbits
.align    4
.cc_top sBufferSelectTable.func, sBufferSelectTable
sBufferSelectTable:
.word sBuffer_ep0
.word sBuffer_ep0
.word sBuffer_ep0
.word sBuffer_ep0
.cc_bottom sBufferSelectTable.func



.globl bufferSelectTable_IN
.globl bufferSelectTable_IN.globound
.set bufferSelectTable_IN.globound, 16
.cc_top bufferSelectTable_IN.func, bufferSelectTable_IN
bufferSelectTable_IN:
.word buffer_ep0_IN_A
.word buffer_ep1_IN_A    
.word buffer_ep2_IN_A    
.word buffer_ep3_IN_A    
.word buffer_ep4_IN_A    
.word buffer_ep5_IN_A    
.word 0
.word 0    
.word 0   
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0    
.word 0   
.cc_bottom bufferSelectTable_IN.func


.globl bufferSelectTable_OUT.globound
.set bufferSelectTable_OUT.globound, 16
.globl bufferSelectTable_OUT
.cc_top bufferSelectTable_OUT.func, bufferSelectTable_OUT
bufferSelectTable_OUT:
.word buffer_ep0_OUT_A
.word buffer_ep1_OUT_A    
.word buffer_ep2_OUT_A    
.word buffer_ep3_OUT_A    
.word buffer_ep4_OUT_A    
.word buffer_ep5_OUT_A    
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0    
.word 0   
.cc_bottom bufferSelectTable_OUT.func

.globl bufferSelectTable2_IN
.globl bufferSelectTable2_IN.globound
.set bufferSelectTable2_IN.globound, 16
.cc_top bufferSelectTable2_IN.func, bufferSelectTable2_IN
bufferSelectTable2_IN:
.word buffer_ep0_IN_B
.word buffer_ep1_IN_B    
.word buffer_ep2_IN_B   
.word buffer_ep3_IN_B
.word buffer_ep4_IN_B
.word buffer_ep5_IN_B
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0    
.word 0   
.cc_bottom bufferSelectTable2_IN.func



.globl bufferSelectTable2_OUT
.globl bufferSelectTable2_OUT.globound
.set bufferSelectTable2_OUT.globound, 16
.cc_top bufferSelectTable2_OUT.func, bufferSelectTable2_OUT
bufferSelectTable2_OUT:
.word buffer_ep0_OUT_B
.word buffer_ep1_OUT_B    
.word buffer_ep2_OUT_B    
.word buffer_ep3_OUT_B
.word buffer_ep4_OUT_B    
.word buffer_ep5_OUT_B
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0   
.word 0    
.word 0    
.word 0    
.word 0   
.cc_bottom bufferSelectTable2_OUT.func
.text


