#define SP_EXTEND 16




.extern BuffLite
.globl BuffLite.nstackwords
.linkset BuffLite.nstackwords, SP_EXTEND
.globl BuffLite.maxthreads
.linkset BuffLite.maxthreads, 0
.globl BuffLite.maxtimers
.linkset BuffLite.maxtimers, 0
.globl BuffLite.maxchanends
.linkset BuffLite.maxchanends, 0
.globl BuffLite
.text
.cc_top BuffLite.func, BuffLite


// void BuffLite (chanend c_setup, chanend c_in, chanend c_out)
BuffLite:
    entsp   SP_EXTEND
    stw     r4, sp[1]
    stw     r5, sp[2]
    stw     r6, sp[3]
    stw     r7, sp[4]
    stw     r8, sp[5]
    stw     r9, sp[6]
    stw     r10, sp[7]

ppp:
bu ppp

SetupEvents:
    ldap      r11,  DoSetup
    setv      res[r0], r11
    eeu       res[r0]

    ldap      r11,  DoIn
    setv      res[r1], r11
    eeu       res[r1]

    ldap      r11, DoOut
    setv       res[r2], r11
    eeu        res[r2]
 

BuffLiteLoop:

    ldaw       r10, dp[bufferSelectTable_IN]   // Load buffer select table
    ldaw       r9, dp[ep_datasize_table_IN_A]  // Load datasize
    mkmsk      r8, 2

    waiteu

// Handle Setup Transaction
DoSetup:
    in        r8,   res[r0]                    // Input EP number
   
    xor        r9, r9, r9                      // Buffer index

    ldaw	    r10,  dp[sBufferSelectTable]	   // Buffer table for Setup data	
    ldw       r10,  r10[r8]                    // Load buffer

    testct    r11,  res[r0]
    bt        r11,  PacketEnd

InputLoop:
    in        r11,  res[r0]
    stw       r11,  r10[r9]
    add       r9,   r9,   1
    testct    r11,  res[r0]
    bf        r11,  InputLoop

PacketEnd:
    inct      r11,  res[r0]                   // Should be 8 bytes so current don't bother with tail TODO should deal with bad packets

UpdateSetupBufferStatus:
    ldc	      r11,  1				                  // Update SBuffer status
    ldaw	    r6,   dp[sBuffer_Datasize]
    stw       r11,  r6[r8]                    // r8: epNum


    bu        BuffLiteLoop

// Handle In transaction
DoIn:
    in         r11, res[r1]                     // Get EP Number
    ldw        r10, r10[r11]                    // Load buffer
    ldw        r9, r9[r11]                      // Load datasize

SetupLoopTerm:       
    sub	       r9, r9, 1        		           // NEW: We store data length + 1 so we can bf
    shr        r5, r9, 2        		           // datalength (bytes) ---> datalength (words)

CalcTailLength:
    and        r8, r8, r9
    //shl        r8, r8, 4                        // Tail length bits to bytes

NegativeIndex:
    ldc        r3, 0
    sub        r3, r3, r5

AdjustBufferPointer:
    shl        r6, r5, 2
    add        r10, r10, r6
    
LoadFirstWord:
    ldw        r6, r10[r3]                    // Load first word.. (done to save branching to load)
  
    bf         r5, SendTail

OutputLoop:
    out        res[r1], r6
    add        r3, r3, 1
    ldw        r6, r10[r3]
    bt         r3, OutputLoop

SendTailLength:
    add        r10, r8, 10                   // Send tail length (+10 since cts < 5 are reserved)
    outct      res[r1], r10

SendTail:
    out        res[r1], r6

    ldaw       r9, dp[ep_datasize_table_IN_A]  // Load datasize
    ldc        r10, 0
    stw        r10, r9[r11]

    bu         BuffLiteLoop


////
DoOut:
    in         r11, res[r2]
    xor        r9, r9, r9                     // Buffer index

LoadBufferSelectTable_OUT:                                            
    ldaw       r10, dp[bufferSelectTable_OUT]
    ldw        r10, r10[r11]                   

    testct     r11,  res[r2]
    bt         r11,  OutPacketEnd

OutLoop:
    in         r8,  res[r2]
    stw        r8,  r10[r9]
    add        r9,   r9,   1
    testct     r8,  res[r2]
    bf         r8,  OutLoop

OutPacketEnd:
    inct       r8,  res[r2]                  // Should be 8 bytes so current don't bother with tail TODO should deal with bad packets
    sub        r8, r8, 10                   // r11: Tail length in bytes
                                              // TODO check for bad crc

CalcDataLength:
    shl        r9, r9, 2                       // Num received words to bytes
    add        r8, r8, r9                   // Total bytes received

StoreDataLength:
    ldaw       r10, dp[ep_datasize_table_OUT_A]
    stw        r8, r10[r11] 

    bu         BuffLiteLoop




    ldw       r4,   sp[1]
    ldw       r5,   sp[2]
    ldw       r6,   sp[3]
    ldw       r7,   sp[4]
    ldw       r8,   sp[5]
    ldw       r9,   sp[6]
    ldw       r10,  sp[7]


   
    retsp SP_EXTEND
.cc_bottom BuffLite.func
