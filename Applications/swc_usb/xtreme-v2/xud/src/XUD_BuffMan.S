
#include <xs1.h>
#include "XUD_Defines_Internal.h"

.extern XUD_LLD_BuffMan
.globl XUD_LLD_BuffMan.nstackwords
.globl XUD_LLD_BuffMan.maxthreads
.globl XUD_LLD_BuffMan.maxtimers
.globl XUD_LLD_BuffMan.maxchanends

.linkset XUD_LLD_BuffMan.nstackwords, 64
.linkset XUD_LLD_BuffMan.maxchanends, 0
.linkset XUD_LLD_BuffMan.maxtimers, 0
.linkset XUD_LLD_BuffMan.maxthreads, 0

.globl XUD_LLD_BuffMan
.cc_top XUD_LLD_BuffMan.func, XUD_LLD_BuffMan
.text 
//int XUD_LLD_BuffMan(chanend c_out, chanend c_in, chanend c_ep, int datalength);

.align 4
XUD_LLD_BuffMan:
    entsp      	64
                        
    stw        	r4, sp[1]
    stw        	r5, sp[2]
    stw        	r6, sp[3]
    stw        	r7, sp[4]
    stw        	r8, sp[5]
    stw        	r9, sp[6]
    stw        	r10, sp[7]

    stw         r3, sp[8]

SetupChannelEvents:	
	ldap       r11, c_out                       // c_out event
    setv       res[r0], r11
    eeu        res[r0]

    ldap       r11, c_in                        // c_in event
    setv       res[r1], r11
    eeu        res[r1]

    ldap       r11, c_ep                       // c_ctl event
    setv       res[r2], r11
    eeu        res[r2]


BuffManLoop: 
	ldaw       r4, dp[ep_datasize_table_OUT_A] 
    ldaw       r5, dp[ep_datasize_table_OUT_B]
    ldaw       r6, dp[ep_datasize_table_IN_A]
	ldaw	   r7, dp[ep_datasize_table_IN_B]

	waiteu

c_out:											
    testct     r10, res[r0]                     // Check for reset
    bt         r10, GotReset_out

    
	in         r10, res[r0]                     // Input datalength  
	shr		   r3, r10, 16					    // r3: epNum 
	mkmsk	   r11, 16
	and		   r10, r10, r11				    // r10: datalength 


    eq         r11, r3, 1
    bf         r11, go_on2
    ldw        r11, sp[8]
    outct      res[r11], 4
    outct      res[r11], XS1_CT_PAUSE
go_on2:  

    
	stw        r10, r4[r3]                      // Store datasize into table 
    ldw        r10, r5[r3]                      // Check to see if buffer B is available
    waitet     r10                              // If no space in buffer wait for next channel event

SwapBufferPointers_OUT:
    ldaw       r11, dp[bufferSelectTable2_OUT]
    ldw        r10, r11[r3]                     // Buffer B Address
    
    ldaw       r9, dp[bufferSelectTable_OUT]
    ldw        r8, r9[r3]                       // Buffer A Address
    
    stw        r10, r9[r3]                      // Do the swap
    stw        r8, r11[r3]
                   
SetDatasizeBtoA_OUT:
    ldw        r10, r4[r3]                     // Get Datasize of A
    ldc        r9, 0
    stw        r9, r4[r3]                      // Reset to 0
    stw        r10, r5[r3]
		
c_out_return:
    waiteu                                      // Wait for next channel event

c_in:
    testct     r10, res[r1]                     // Check for reset
    bt         r10, GotReset_in 

	in         r3, res[r1]                      // Input EP num	TODO check for reset
                                                // TODO This could be token

    ldc        r10, 0        
    stw        r10, r6[r3]   

    eq         r10, r3, 2
    bf         r10, go_on
    ldw        r10, sp[8]
    outct      res[r10], 5
    outct      res[r10], XS1_CT_PAUSE
go_on:  
    
                                                // If data ready to go in buffer B, if so then do buffer swap
    ldw        r11, r7[r3]                      // Datasize of buffer B
    waitef     r11

SwapBufferPointers_IN:
    ldaw       r11, dp[bufferSelectTable2_IN]
    ldw        r10, r11[r3]                     // Buffer B Address
    
    ldaw       r9, dp[bufferSelectTable_IN]
    ldw        r8, r9[r3]                       // Buffer A Address
    
    stw        r10, r9[r3]                      // Do the swap
    stw        r8, r11[r3]

setPidAtoB_IN:
    ldaw       r11, dp[ep_pid_sequence_table_IN_B]
    ld8u       r10, r11[r3]                     // Pid B
    ldaw       r11, dp[ep_pid_sequence_table_IN_A]
    st8        r10, r11[r3]   


setDatasizeAtoB_IN:
    ldw  r10, r7[r3]      // Buffer B data size
    ldc   r9, 0             // Reset buffer B datasize
    stw   r9, r7[r3]
    stw   r10, r6[r3]


InBufferEmpty:
    waiteu

//////////////////////////
c_ep:
	in         r10, res[r2]                      // Input cmd
    in         r3, res[r2]                      // input ep num

    ldc        r11, XUD_CMD_IN
    eq         r11, r10, r11
    bt         r11, c_ep_in

c_ep_out:
	ldw        r11, r4[r3]                      // Datasize of buffer A
    bf         r11, NoDataInBufferA

DataInBufferA:
    ldaw       r11, dp[bufferSelectTable2_OUT]
    ldw        r10, r11[r3]                     // Buffer B Address
    
    ldaw       r9, dp[bufferSelectTable_OUT]
    ldw        r8, r9[r3]                       // Buffer A Address
    
    stw        r10, r9[r3]                      // Do the swap
    stw        r8, r11[r3]

SetDataSize:
    //ldaw r4, dp[ep_datasize_table_OUT_A]
    ldw  r10, r4[r3]      // Buffer B data size
    ldc   r9, 0             // Reset buffer B datasize
    stw   r9, r4[r3]
    //ldaw  r11, dp[ep_datasize_table_OUT_B]
    stw   r10, r5[r3]
    waiteu

NoDataInBufferA:
    //ldc        r11, 0                         // Reset buffer b datasize. R11: 0
    stw        r11, r5[r3]
    waiteu

c_ep_in:
	ldw        r11, r6[r3]                      // Datasize of buffer A
    waitet     r11

SwapBufferPointers_IN_:
    ldaw       r11, dp[bufferSelectTable2_IN]
    ldw        r10, r11[r3]                     // Buffer B Address
    
    ldaw       r9, dp[bufferSelectTable_IN]
    ldw        r8, r9[r3]                       // Buffer A Address
    
    stw        r10, r9[r3]                      // Do the swap
    stw        r8, r11[r3]

setPidAtoB_IN_:
    ldaw       r11, dp[ep_pid_sequence_table_IN_B]
    ld8u       r10, r11[r3]          // Pid B
    ldaw       r11, dp[ep_pid_sequence_table_IN_A]
    st8        r10, r11[r3]   


setDatasizeAtoB_IN_:
    ldw  r10, r7[r3]      // Buffer B data size
    ldc   r9, 0             // Reset buffer B datasize
    stw   r9, r7[r3]
    stw   r10, r6[r3]
	waiteu	

Return:
    ldw        	r4, sp[1]
    ldw        	r5, sp[2]
    ldw        	r6, sp[3]
    ldw        	r7, sp[4]
    ldw        	r8, sp[5]
    ldw        	r9, sp[6]
    ldw        	r10, sp[7]
    retsp      	64

GotReset_out:
    chkct      res[r0], XS1_CT_END
    
GotReset_out_loop:
    testct      r11, res[r1]
    bt          r11, GotReset_out_done
    in          r11, res[r1]
    bu          GotReset_out_loop

GotReset_out_done:
    chkct       res[r1], XS1_CT_END
  
    outct       res[r2], XS1_CT_END                     // Pass on kill
    chkct       res[r2], XS1_CT_END
    bu          Return
    
GotReset_in:
    chkct      res[r1], XS1_CT_END
    
GotReset_in_loop:
    testct      r11, res[r0]
    bt          r11, GotReset_in_done
    in          r11, res[r0]
    bu          GotReset_in_loop

GotReset_in_done:
    chkct       res[r0], XS1_CT_END
  
    outct       res[r2], XS1_CT_END                     // Pass on kill
    chkct       res[r2], XS1_CT_END
    bu          Return



.cc_bottom XUD_LLD_BuffMan.func
