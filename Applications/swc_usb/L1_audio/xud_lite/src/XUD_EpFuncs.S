#include <xs1_user.h>
#include "XUD_UIFM_Defines.h"

#define SR_EEBLE_BIT 0x1
#define STACK_EXTEND 16

// void XUD_SetDevAddr(unsigned addr)
// Writes device address
// TODO Use a lock or similar
.globl   XUD_SetDevAddr
.globl   XUD_SetDevAddr.nstackwords
.linkset XUD_SetDevAddr.nstackwords, 0
.text

.cc_top XUD_SetDevAddr.func, XUD_SetDevAddr
XUD_SetDevAddr:					
    ldc       r11, UIFM_CMD_WRITE
    ldc       r1, UIFM_REG_ADDRESS
    or        r3, r11, r1                       // TODO Don't really need to generate this!
    ldw       r2, dp[reg_write_port]

    out       res[r2], r3
    out       res[r2], r0
    retsp     0
.cc_bottom XUD_SetDevAddr.func

//int XUD_GetSetupData(chanend c, unsigned buffer[]); 
//                 r0          r1           
// TODO just use GetData
.globl   XUD_GetSetupData
.globl   XUD_GetSetupData.nstackwords
.linkset XUD_GetSetupData.nstackwords, 0
.text

.cc_top XUD_GetSetupData.func, XUD_GetSetupData
XUD_GetSetupData:					

    out        res[r0], r1                    // TODO Should only need CT
     
SetupDataLoop:
    in         r11,  res[r0]                  // r2: 0
    stw        r11,  r1[0]
    in         r11,  res[r0]                  // r2: 0
    stw        r11,  r1[1]
    
    ldc r0, 8
        
    retsp    	0
.cc_bottom XUD_GetSetupData.func




//int XUD_GetData(chanend c, unsigned buffer[]); 
//                 r0          r1           

.globl   XUD_GetData
.globl   XUD_GetData.nstackwords
.linkset XUD_GetData.nstackwords, 0
.text

.cc_top XUD_GetData.func, XUD_GetData
XUD_GetData:					

    out        res[r0], r1                    // TODO Should only need CT
   // outct      res[r0], XS1_CT_END            // Request data
    
    //xor        r2, r2, r2                     // Buffer index

    testct     r2,  res[r0]                  // Wait for data (or end)
    bt         r2,  DataEnd
     
DataLoop:
    in         r11,  res[r0]                  // r2: 0
    stw        r11,  r1[r2]
    add        r2,   r2,   1
    testct     r11,  res[r0]
    bf         r11,  DataLoop
                    
DataEnd:
    inct       r11, res[r0]                  // r11 is tail length (bytes + 10)
   // chkct      res[r0], XS1_CT_END
    //outct      res[r0], XS1_CT_END
    sub        r11, r11, 10
    
CalcdataLength:
    shl        r2, r2, 2                    // Num received words to bytes
    mov        r0, r2
    add        r0, r11, r2                   // r11: Total bytes received (Note this includes 2 byte crc)
   
    sub        r0, r0, 6                     // CRC correction and extra increment 
        
    retsp    	0
.cc_bottom XUD_GetData.func




// Note: Assumes startIndex is word aligned
//int XUD_SetData_indexed(chanend c, unsigned buffer[], unsigned datasize, unsigned pidToggle, unsigned startIndex); 
//                            r0             r1                  r2                r3
.globl   XUD_SetData
.globl   XUD_SetData.nstackwords
.linkset XUD_SetData.nstackwords, 16
.text

.cc_top XUD_SetData.func, XUD_SetData
XUD_SetData:		
    entsp     16
    stw       r5, sp[2]
    stw       r6, sp[3]
    stw       r8, sp[5]
    stw       r10, sp[6]
    

    ldw        r11, sp[17]                      // Load start index
    add        r1, r1, r11                      // Add start index to buffer address

SetupLoopTerm_Indexed:
    mkmsk     r8, 2                             // Tail length mask 
    shr       r5, r2, 2                         // datalength (bytes) ---> datalength (words)
        
CalcTailLength:
     and      r8, r8, r2
     shl      r8, r8, 4                         // Tail length bytes to bits * 2
    add       r8, r8, 10                        // Tail length +10 since cts < 5 are reserved)
               
                
NegativeIndex:                                  // Produce negtive offset from end of buffer
     ldc        r10, 0
     sub        r10, r10, r5
                        
AdjustBufferPointer:
     shl        r6, r5, 2                       // Get end off buffer address
     add        r1, r1, r6
               
LoadFirstWord:
     ldw        r6, r1[r10]                      // Load first word.. (done to save branching to load)
    
     out         res[r0], r3                     // Send PID toggle flag
    // in         r11, res[r0]                     // Wait for response to request to send 

     bf         r5, SendTailLength

OutputLoop:
     add        r10, r10, 1
     out        res[r0], r6
     ldw        r6, r1[r10]
     bt         r10, OutputLoop
                                                        
SendTailLength:
     outct      res[r0], r8
                                                                
SendTail:
    out        res[r0], r6
    //outct      res[r0], XS1_CT_END
                                        
    ldw       r5, sp[2]
    ldw       r6, sp[3]
    ldw       r8, sp[5]
    ldw       r10, sp[6]
                                                                                        
    retsp    	16
.cc_bottom XUD_SetData.func


