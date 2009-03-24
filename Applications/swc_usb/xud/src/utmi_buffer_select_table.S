
// utmi_buffer_select_table.S



//getPidSeq_IN_B(epNum)
.globl getPidSeq_IN_B
.globl getPidSeq_IN_B.nstackwords
.linkset getPidSeq_IN_B.nstackwords, 0 
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



////////////////////////////////////////////////////////////////////////////
// unsigned setPidBtoA_IN(unsigned epNum);
.globl setPidAtoB_IN
.globl setPidAtoB_IN.nstackwords
.linkset setPidAtoB_IN.nstackwords, 0 
setPidAtoB_IN:
  ldaw	r1, dp[ep_pid_sequence_table_IN_B]
  ld8u	r2, r1[r0]			    // Pid B
  ldaw  r1, dp[ep_pid_sequence_table_IN_A]
  st8   r2, r1[r0]                          // Store to A
  retsp 0


//.extern GetParam
//.globl GetParam.nstackwords
//.linkset GetParam.nstackwords, 0
//.globl GetParam
//.text
//GetParam:
//ldaw r1, dp[params]
//ldw  r0, r1[r0]
//retsp 0



////////////////////////////////////////////////////////////////////////////
// unsigned setDatasizeAtoB_IN(unsigned epNum);
.globl setDatasizeAtoB_IN
.globl setDatasizeAtoB_IN.nstackwords
.linkset setDatasizeAtoB_IN.nstackwords, 0 
setDatasizeAtoB_IN:
  ldaw	r1, dp[ep_datasize_table_IN_B]
  ldw	r2, r1[r0]			// Buffer B data size
  ldc   r11, 0			        // Reset buffer B datasize
  stw   r11, r1[r0]	
  ldaw  r1, dp[ep_datasize_table_IN_A]
  stw   r2, r1[r0]
  retsp 0

////////////////////////////////////////////////////////////////////////////
// void setDatasizeBtoA_OUT(unsigned epNum);
.globl   SetDatasizeBtoA_OUT
.globl   SetDatasizeBtoA_OUT.nstackwords
.linkset SetDatasizeBtoA_OUT.nstackwords, 0 
SetDatasizeBtoA_OUT:
  ldaw	r1, dp[ep_datasize_table_OUT_A]
  ldw	r2, r1[r0]			
  ldc   r11, 0			        
  stw   r11, r1[r0]	
  ldaw  r1, dp[ep_datasize_table_OUT_B]
  stw   r2, r1[r0]
  retsp 0

////////////////////////////////////////////////////////////////////////////
// unsigned swapBufferPointers_OUT(unsigned epNum);
.globl   swapBufferPointers_OUT
.globl   swapBufferPointers_OUT.nstackwords
.linkset swapBufferPointers_OUT.nstackwords, 0 
swapBufferPointers_OUT:
  ldaw	r1, dp[bufferSelectTable2_OUT]
  ldw	r2, r1[r0]			// Buffer B Address
  ldaw  r3, dp[bufferSelectTable_OUT]
  ldw	r11, r3[r0]			// Buffer A Address
  stw	r11, r1[r0]			// Do the swap
  stw   r2, r3[r0]
  retsp 0


////////////////////////////////////////////////////////////////////////////
// unsigned swapBufferPointers_OUT(unsigned epNum);
.globl swapBufferPointers_IN
.globl swapBufferPointers_IN.nstackwords
.linkset swapBufferPointers_IN.nstackwords, 0 
swapBufferPointers_IN:
  ldaw	r1, dp[bufferSelectTable2_IN]
  ldw	r2, r1[r0]			// Buffer B Address
  ldaw  r3, dp[bufferSelectTable_IN]
  ldw	r11, r3[r0]			// Buffer A Address
  stw	r11, r1[r0]			// Do the swap
  stw   r2, r3[r0]
  retsp 0


// TODO: Move these functions
//////////////////////////////////////////////////////////////////////////////
// unsigned getUint(unsigned address, int index);
.globl getUint
.globl getUint.nstackwords
.linkset getUint.nstackwords, 0 
getUint:
  ldw	r0, r0[r1]
  retsp	0

//////////////////////////////////////////////////////////////////////////////
// unsigned setUint(unsigned address, int index, unsigned data);
.globl setUint
.globl setUint.nstackwords
.linkset setUint.nstackwords, 0
setUint:
  stw	r2, r0[r1]
  retsp 0

//////////////////////////////////////////////////////////////////////////////
// unsigned GetArrayAddress(unsigned array[]);
.globl GetArrayAddress
.globl GetArrayAddress.nstackwords
.linkset GetArrayAddress.nstackwords, 0
GetArrayAddress:
  retsp 0

//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_IN_B(unsigned epNum);
.globl GetDatasize_IN_B
.globl GetDatasize_IN_B.nstackwords
.linkset GetDatasize_IN_B.nstackwords, 0
GetDatasize_IN_B:
  ldaw	r11, dp[ep_datasize_table_IN_B]
  ldw   r0, r11[r0] 
  retsp 0

//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_IN_A(unsigned epNum);
.globl GetDatasize_IN_A
.globl GetDatasize_IN_A.nstackwords
.linkset GetDatasize_IN_A.nstackwords, 0
GetDatasize_IN_A:
  ldaw	r11, dp[ep_datasize_table_IN_A]
  ldw   r0, r11[r0] 
  retsp 0



//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_OUT_B(unsigned epNum);
.globl 	 GetDatasize_OUT_B
.globl   GetDatasize_OUT_B.nstackwords
.linkset GetDatasize_OUT_B.nstackwords, 0
GetDatasize_OUT_B:
  ldaw	r11, dp[ep_datasize_table_OUT_B]
  ldw   r0, r11[r0] 
  retsp 0

//////////////////////////////////////////////////////////////////////////////
//void SetDatasize_OUT_B(unsigned epNum);
.globl 	 SetDatasize_OUT_B
.globl   SetDatasize_OUT_B.nstackwords
.linkset SetDatasize_OUT_B.nstackwords, 0
SetDatasize_OUT_B:
  ldaw	r11, dp[ep_datasize_table_OUT_B]
  stw   r1, r11[r0] 
  retsp 0



//////////////////////////////////////////////////////////////////////////////
//unsigned GetDatasize_OUT_A(unsigned epNum);
.globl 	 GetDatasize_OUT_A
.globl   GetDatasize_OUT_A.nstackwords
.linkset GetDatasize_OUT_A.nstackwords, 0
GetDatasize_OUT_A:
  ldaw	r11, dp[ep_datasize_table_OUT_A]
  ldw   r0, r11[r0] 
  retsp 0



.globl sBufferSelectTable
.globl sBufferSelectTable.globound
.set sBufferSelectTable.globound, 5
.section .dp.data,       "adw", @progbits
.align    4
sBufferSelectTable:
.word sBuffer_ep0
.word sBuffer_ep0
.word sBuffer_ep0
.word sBuffer_ep0






/*
.globl bufferSelectTable_OUT

.globl bufferSelectTable2_IN.globound
.set bufferSelectTable2_IN.globound, 16
.globl bufferSelectTable2_IN

.globl bufferSelectTable2_OUT


.globl bufferSelectTable_IN
.globl bufferSelectTable_IN.globound
.set bufferSelectTable_IN.globound, 16
bufferSelectTable_IN:
.word buffer_ep0_IN_A
.word buffer_ep1_IN_A    
.word buffer_ep2_IN_A    
.word buffer_ep3_IN_A    
.word buffer_ep4_IN_A    
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
.word 0   

bufferSelectTable_OUT:
.word buffer_ep0_OUT_A
.word buffer_ep1_OUT_A    
.word buffer_ep2_OUT_A    
.word buffer_ep3_OUT_A    
.word buffer_ep4_OUT_A    
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
.word 0   


bufferSelectTable2_IN:
.word buffer_ep0_IN_B
.word buffer_ep1_IN_B    
.word buffer_ep2_IN_B   
.word buffer_ep3_IN_B
.word buffer_ep4_IN_B
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
.word 0   

bufferSelectTable2_OUT:
.word buffer_ep0_OUT_B
.word buffer_ep1_OUT_B    
.word buffer_ep2_OUT_B    
.word buffer_ep3_OUT_B
.word buffer_ep4_OUT_B    
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
.word 0   
.text

*/
