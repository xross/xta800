
.text 

// Expects 
// r5: buffer address 
// RXD: Rx Data port
// r4: zero
// r7: crc init
// r9: crc poly
.align 4
doRXData:
  eeu    res[RXD]
 
  waiteu				// Wait for data

NextRxWord:				// RXD port has event vector here
  in 	r11, res[RXD]
  stw   r11, r5[r4]
  add   r4, r4, 1
  crc32 r7, r11, r9
  waiteu 


/////////////////////////////////////////////////////////////////////////////
.align 16
.skip 10
RxALow:
  endin	r8, res[RXD]
  in    r3, res[RXD]
  bru	r8

// Word aligned
.align 16
OutTail0:
  //in     r11, res[RXD]                    // Do IN to get rid of junk                  
  crc32  r7, r8, r9      		  // CRC zero step                 
  ldc    r11, 0x7000
  bu     CheckCrc16

// 1 Tail Byte
.align 16
OutTail1:
  //in     r3, res[RXD]
  shr    r3, r3, 24                       // Shift off junk
  crc32  r7, r3, r9
  ldc    r11, 0x80be                      // Load CRC magic number     
  bu     CheckCrc16

// Two Tail Bytes
.align 16
OutTail2:
  //in     r3, res[RXD]
  bt	 r10, RxDataReturn 
  shr    r3, r3, 16
  crc32  r7, r3, r9
  ldc    r11, 0x3ffe
  BRFU_u6 CheckCrc16

// Three Tail Bytes
.align 16
OutTail3:
  //in     r3, res[RXD]
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
  crc32  r7, r3, r9
  ldc    r11, 0
  crc32  r7, r11, r9 
  ldc    r11, 0x0070
  bu     SendNak

.align 16
OutTail5:
//bl ERR_EndIn5
  bu     SendNak


SendNak:
  ldc    r11, PIDn_NAK
  outpw  res[TXD], r11, 8
  bu RxDataReturn


CheckCrc16:
  eq     r7, r7, r11                      // If CRC16 good, send ACK (if not ISO ep)       
  bf     r7, Err_BadDataCrc

DoOutHandShake:       // Data CRC good, send ack 
  ldc    r11, PIDn_ACK
  outpw  res[TXD], r11, 8
    
StoreTailData:
  stw    r3, r5[r4]

RxDataReturn:
  retsp 0

Err_BadDataCrc:
  //bu SendNak
  //bl ERR_BadCrc
  retsp 0


.text 
.align 4

// We received a setup token.  This is much the same as out token, apart from:
// - Can't NAK a setup
// - Data PID will always be DATA0
// - Payload will always be 8 bytes
// r10 = ep number
// TODO: All these loads on a row... not good!
Pid_Setup:
  ldw       r1, sp[10]           // Load RxA Port ID (r1) 
  eeu       res[r1]                       // Enable events on RXA
  ldw	r1, sp[9]			// Load out timeout timer from stack
 

LoadSETUPStatTable:
  ldaw	r6, dp[sBuffer_Datasize]  	// Get status table
 
LoadSetupBuffStatus: 
  ldw	r4, r6[r10]			// Get status of relevent EP setup buff
  bt	r4, SBuffFull

					// TODO Do a timeout here?
		
ReadSetupDataPid:			// Expect 8 byte setup data (1 byte pid, +2 byte CRC)
  inpw 	r6, res[RXD], 8			// Data PID in r6 (keep for sequence checking)

LoadSetupBuffSelectTable:
  ldaw	r5, dp[sBufferSelectTable]		
			
LoadSetupBuffer:
  ldw	r5, r5[r10]			
  bl    doRXData			// RXData writes available data to buffer and does crc check. 					      		// Sends ACK if CRC ok
  
  ldc	r11, 1				// Update SBuffer status
  ldaw	r6, dp[sBuffer_Datasize]
  stw   r11, r6[r10]

// TODO Check data pid is data1?



  bu NextToken 				// Go to next wait for next token 
                 
SBuffFull:	
  bl    ERR_SetupBuffFull			// Wait for end of data then go to next token
  in 	r11, res[RXA]			// RXA event cond = 0 TODO: Wait for RXA high first?
  endin r11, res[RXD]
  in 	r11, res[RXD]
 
  bu NextToken

