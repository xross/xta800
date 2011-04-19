
.text 

// Expects 
// r5: buffer address 
// RXD: Rx Data port
// r4: zero
// r7: crc init
// r9: crc poly
.cc_top doRXData.func, doRXData
.align 4
.skip 0
doRXData:
    nop                                             // TODO Should not require this nop
    nop
    waiteu				                            // Wait for data or RXA low

.align 4 
NextRxWord:				                            // RXD port has event vector here
    in 	  r8, res[RXD]
    out   res[r5], r8
    crc32 r7, r8, r9
    waiteu 


/////////////////////////////////////////////////////////////////////////////
.align 16
.skip 10
RxALow:
    endin	  r8, res[RXD]
    in      r3, res[RXD]
    bru	    r8

// Word aligned data (0 byte tail)
.align 16
OutTail0:
    crc32   r7, r8, r9                   // CRC zero step                 
    ldc     r11, 0x7000                  // Load relevant CRC magic number
    bu      CheckCrc16

// 1 Tail Byte
.align 16
OutTail1:
    shr     r3, r3, 24                    // Shift off junk
    crc32   r7, r3, r9
    ldc     r11, 0x80be                   // Load CRC magic number     
    bu      CheckCrc16

// Two Tail Bytes
.align 16
OutTail2:
    //bf      r1, StoreTailData
    shr     r3, r3, 16
    crc32   r7, r3, r9
    ldc     r11, 0x3ffe
    BRFU_u6 CheckCrc16

// Three Tail Bytes
.align 16
OutTail3:
    shr    r3, r3, 8
    crc32  r7, r3, r9
    ldc    r11, 0
    crc32  r7, r11, r9 
    //ldc    r11, 0x0070
    ldw      r11, sp[12]
    BRFU_u6     CheckCrc16

// Four Tail Bytes
.align 16
OutTail4:
    bu     ERR_EndIn4


.align 16
OutTail5:
    mov    r0, r3
    bu     ERR_EndIn5


SendNak:
    ldc     r11, PIDn_NAK
    outpw   res[TXD], r11, 8
    bu      doRXDataReturn



.align 4
.skip 0
CheckCrc16:
    eq      r7, r7, r11                              // Check for good CRC16   

doRXDataReturn:
    retsp   0
    
    .cc_bottom doRXData.func
    

.text 

.align 4

    

// We received a setup token.  This is much the same as out token, apart from:
// - Can't NAK a setup
// - Data PID will always be DATA0
// - Payload will always be 8 bytes
// r10 = ep number
Pid_Setup:
    ldap      r11, SetupReady
    ldw       r5, r5[0]                     // Load relevant EP chanend
    setv      res[r5], r11
    eeu       res[r5]                         // Enable events on channel
    setsr     0x1                             // Enable thread events

SetupNotready:                               
    clre                                     // Cleaer events
    bu        SBuffFull

SetupReady:	
    clre
    in        r11, res[r5]                    // TODO should only need CT
     
                                              // TODO Do a timeout here?	
ReadSetupDataPid:			                  // Expect 8 byte setup data (1 byte pid, +2 byte CRC)
    inpw 	    r6, res[RXD], 8			      // Data PID in r6 (keep for checking).  Note RXA should be hi here so can now
                                              // enable RXA low event

    ldw       r1, sp[10]                      // Load RxA Port ID (r1)
    eeu       res[RXD]
    eeu       res[r1]                         // Enable events on RXA

LoadSetupBuffer:
    bl      doRXData			              // RXData writes available data to buffer and does crc check. 
                                              // r7: CRC check outcome	
                                              // r8: Data tail size (bytes)   

    bf      r7, BadDataCrcSetup

//CheckEpType:                                // Always handshake for control EP/Setup transactions
//  bf      r1, StoreTailData                       

DoOutHandShake:                               // Data CRC good: Send Ack 
    ldc     r11, PIDn_ACK
    outpw   res[TXD], r11, 8

StoreTailData:                                // TODO: don't assume setups are 8 bytes + crc
    //out       res[r5], r3
    //ldc       r3, 12
    //outct     res[r5], r3
   // outct     res[r5], XS1_CT_END
    //chkct     res[r5], XS1_CT_END

BadDataCrcSetup:                              // TODO Handle bad CRCs properly

RestoreVector:
    ldap      r11, OutReady
    setv      res[r5], r11
    bu NextToken 				// Go to next wait for next token 


SBuffFull:	
 bl    ERR_SetupBuffFull			// Wait for end of data then go to next token
 in 	r11, res[RXA]			// RXA event cond = 0 TODO: Wait for RXA high first?
 endin r11, res[RXD]
 in 	r11, res[RXD]

 bu NextToken

