// Ports
#define RXD r0
#define RXA r1
#define TXD r2
#define RXE r3



// R10: ep number
.align 4
Pid_In:


InitCrc16_In:
  ldc       r7, 0xf335

LoadDataLength:                         // Check there is data to send
  ldw       r8, r8[r10]
  bf        r8, NoDataInPrimaryBuffer   // Branch if no data
 
SetupLoopTerm:       
  sub	    r8, r8, 1        		// NEW: We store data length + 1 so we can bf
  shr       r5, r8, 2        		// datalength (bytes) ---> datalength (words)
 
LoadEpStatus_In:
  ldw       r3, r3[r10]

BranchOnBadEpStatus:
  bf        r3, EndpointOk    		// EP_OK = 0

Err_EndpointProblem_In:
  bu        NextToken

///////////////////////////////////////////////////////////////////////////////////
.align 32
.skip 8
EndpointOk:

CalcTailLength1:
  mkmsk     r11, 2

LoadBuffer_In:                       	// Load the correct buffer pointer
  ldw       r4, r4[r10]

CalcTailLength2:
  and       r8, r8, r11                   
  shl       r8, r8, 4		        // Tail length bytes to bits * 2
                                        // Additon of this instruction allows removeal of branch branch
					// to tail code
LoadDataPid:
  ld8u      r11, r6[r10]

NegativeIndex:
  sub       r3, r3, r5
  
AdjustBufferPointer:
  shl       r6, r5, 2
  add       r4, r4, r6
  
LoadFirstWord:
  ldw         r6, r4[r3]                // Load first word.. (done to save branching to load)

SmallTxPacketCheck:
  bt        r5, NormalTxPacket     	// < 4 bytes... send PID then tail


SmallTxPacket:
  bru       r8                          // branch taillength

//--------------------------------------------------------------------------
.align 32
.skip 0
TxTail0s:				// We know this is a < 4 byte packet, so is 0 length packet
  // Word aligned data			// So crc =0. Note our normal crc calculation works for this, it is not a special CRC case, but helps with timing.
  outpw     res[TXD], r11, 8		// PID
  //crc32        r7, r11, r9            // r11: 0
  //not          r7, r7
  outpw        res[TXD], r5, 16		// r5: 0
  bu           DoneTail  

.align 32
.skip 0
TxTail1s:
  // One tail byte
  outpw     res[TXD], r11, 8		// PID
  crc8         r7, r4, r6, r9
  outpw        res[TXD], r6, 8 
  crc32        r7, r5, r9               // r5: 0 
 
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail

.align 32
.skip 0
TxTail2s:
  // Two tail byte 
  outpw     res[TXD], r11, 8		// PID
  outpw        res[TXD], r6, 16 
  crc8         r7, r6, r6, r9
  crc8         r7, r6, r6, r9

  crc32        r7, r5, r9               // r5: 0     
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail

.align 32
.skip 0
TxTail3s:
  // Two tail byte 
  outpw        res[TXD], r11, 8		// PID
  outpw        res[TXD], r6, 24 
  crc8         r7, r6, r6, r9
  crc8         r7, r6, r6, r9
  crc8	       r7, r6, r6, r9

  crc32        r7, r5, r9               // r5: 0     
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail




NoDataInPrimaryBuffer:                  // Endpoint has no data to send.  Send a NAK
ldc       r11, PIDn_NAK			// TODO Min inter-packet delay	
outpw     res[TXD], r11, 8

bu        NextToken 


.align 32
.skip 12
NormalTxPacket:
SendDataPid1:
  syncr	    res[TXD]
  outpw     res[TXD], r11, 8

TxLoop:
  add       r3, r3, 1 
  crc32     r7, r6, r9
  out       res[TXD], r6 
  ldw       r6, r4[r3]
  bt        r3, TxLoop

TxTailProcessing:
  bru       r8                            // branch taillength

//--------------------------------------------------------------------------
.align 32
.skip 0
TxTail0:
  // Word aligned data (just output CRC)
  crc32        r7, r3, r9                 // r3: 0
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail  

.align 32
.skip 0
TxTail1:
  // One tail byte
  crc8         r7, r4, r6, r9
  outpw        res[TXD], r6, 8 
  crc32        r7, r3, r9                 // r3: 0 
 
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail

.align 32
.skip 0
TxTail2:
  // Two tail byte 
  outpw        res[TXD], r6, 16 
  crc8         r7, r6, r6, r9
  crc8         r7, r6, r6, r9

  crc32        r7, r3, r9                 // r3: 0     
  not          r7, r7
  outpw        res[TXD], r7, 16
  bu           DoneTail

.align 32
.skip 0
TxTail3:
  // Three tail byte 
  outpw        res[TXD], r6, 24 

  crc8         r7, r6, r6, r9
  crc8         r7, r6, r6, r9
  crc8         r7, r6, r6, r9

  crc32        r7, r3, r9                // r3: 0     
  not          r7, r7
  outpw        res[TXD], r7, 16
  
// Wait for handshake... or timeout
DoneTail:
SetupReceiveHandShake:			  
  ldc	       	r11, 8
  setpsc       	res[RXD], r11		// Set port shift count (expect 8 bit handshake)
  ldap         	r11,  TxHandShakeReceived
  setv         	res[RXD], r11
  eeu		res[RXD]

SetupTimeout:				// Timeout done using another port off same clock block we dont happen to already be using events on. Cunning.
  ldw 		r1, sp[11]        	// Load saved ValidToken port. Event vector already set.
  in           	r11, res[r1]		// Do input and get port time/timestamps
  getts	       	r11, res[r1]
  ldc          	r9,  60			// TODO Check this val
  add          	r11, r11, r9
  setpt	       	res[r1], r11		// Set port time and enable events
  eeu	       	res[r1]			

WaitForHandshake:			// Wait for timeout or handshake
  waiteu


// We sent some data but the host didn't handshake in time.  Dont't swap buffers, re-send.
.align 4
TxHandshakeTimeOut:
  //clrpt     	res[r1] 		// Not required, just do IN
  in 	    	r11, res[r1]
  bl        	WAR_TxHandshakeTimeout 	// TODO Debug only
  bu        	NextToken

// Transmitted data, and got something back within the timeout. Check for valid handshake...
.align 4
TxHandShakeReceived:
  in 		r11, res[RXD]
  clrpt		res[r1]			// Clear port time
  shr          	r11, r11, 24            // Shift off junk data
  ldc          	r9, PID_ACK             // Check for good ACK
  xor          	r9, r11, r9              
  bt           	r9, BadHandShake        // Valid handshake not received from host. we need to resend...
   
TriggerBufferManager_IN:                // Trigger buffer manager to signal data sent 
  ldw          	r11, sp[19]        	// Load channel from stack. (6 arg, 16 stack word)
  out        	res[r11], r10  		// Send EP number

DoneTx:
  bu        	NextToken

BadHandShake:				// Received a bad handshake
  mov 		r0, r11			// TODO Debug only
  bl 		ERR_BadTxHandshake			
  bu 		NextToken


