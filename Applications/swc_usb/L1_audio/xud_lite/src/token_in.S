#include <xs1.h>

// Ports
#define RXD r0
#define RXA r1
#define TXD r2
#define RXE r3

.text





//OutReady:
//bu OutReady
// R10: ep number
.align 4

Pid_In:
    ldw       r11, sp[24]                       // Load channel count TODO use addi?
    add       r11, r11, r10                     // In channel offset
    ldw       r5, r5[r11]                         // Load relevant EP chanend
    eeu       res[r5]                           // Enable events on channel

    setsr     0x1                               // Enable thread events

InNotReady:
    clre                                        // Clear all events/flags
                                                // TODO Don't NAK Iso endpoints 
    ldc       r11, PIDn_NAK			                // TODO Min inter-packet delay	
	  outpw     res[TXD], r11, 8
	  syncr     res[TXD]
    nop 
    nop
    nop
    bu        NextToken

.align 4
.skip 2
InReady:     
    //TODO LOOKUP EP TYPE
    clre                                        // Endpoint has data to send 
    in         r11, res[r5]
    //chkct       res[r5], XS1_CT_END
    //out		     res[r5], r10                     // Reply to request to send

    bt         r11, PidReset
    
PidToggle:
    ld8u       r11, r6[r10]                     // Load data pid from table
    ldc        r7, 0x88
    xor        r8, r11, r7
    st8        r8, r6[r10]
    
    ldc        r7, 0xf335                       // Init CRC
    bu         NormalTxPacket

.align 4
PidReset:
    //ldc        r7, PIDn_DATA1
    nop
    ldc        r7, 0xf335                       // Init CRC
    st8       r11, r6[r10]                     // Load data pid from table
    bu         NormalTxPacket



.align 64
.skip 22
NormalTxPacket:
SendDataPid1:
    testct   r6, res[r5]                        // Get first word      
	  bt       r6,  SmallTxPacket
	
	  in				r6, res[r5]                       // Get data word (loop unrolled once)
	  crc32      r7, r6, r9
	  
    outpw      res[TXD], r11, 8                 // Output PID to bus
    
	  testct     r11, res[r5]
    out        res[TXD], r6 
    bt         r11, TxTailProcessing_

TxLoop:
	  in				r6, res[r5]                       // Get data word
	  crc32      r7, r6, r9
	  testct     r11, res[r5]
    out        res[TXD], r6 
    bf         r11, TxLoop

TxTailProcessing_:
    inct       r11, res[r5]                     // Input tail length + 10
    sub        r11, r11, 10
    in         r6, res[r5]                      // Input tail data

TxTailProcessing:
    bru        r11                              // Branch on taillength for tail processing

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
    //chkct         res[r5], XS1_CT_END
    ldw           r11, sp[22]       
    ldw           r11, r11[r10]
    bf            r11, NextToken


SetupReceiveHandShake:			  
    ldc	       	r11, 8
    setpsc       	res[RXD], r11		                  // Set port shift count (expect 8 bit handshake)
    ldap         	r11,  TxHandShakeReceived
    setv         	res[RXD], r11
    eeu		res[RXD]                                  // Events on RXD always enabled - Can;t be any more due to using events on channels

SetupTimeout:				                              // Timeout done using another port off same clock block we dont happen to already be using events on. Cunning.
    ldw 		r1, sp[11]        	                    // Load saved ValidToken port. Event vector already set.
    in           	r11, res[r1]		                  // Do input and get port time/timestamps
    getts	       	r11, res[r1]
    ldc          	r9,  60			                      // TODO Check this val
    add          	r11, r11, r9
    setpt	       	res[r1], r11		                  // Set port time and enable events
    eeu	       	res[r1]			

WaitForHandshake:			                            // Wait for timeout or handshake
    waiteu


// We sent some data but the host didn't handshake in time.  Dont't swap buffers, re-send.
.align 4
TxHandshakeTimeOut:
  //clrpt     	res[r1] 		// Not required, just do IN
  in 	    	r11, res[r1]
  edu       res[r1]
  bl        	ERR_TxHandshakeTimeout 	// TODO Debug only
  bu        	NextToken

// Transmitted data, and got something back within the timeout. Check for valid handshake...
.align 4
TxHandShakeReceived:
  in 		r11, res[RXD]
  clrpt		res[r1]			// Clear port time
  
  edu   res[r1]
  shr          	r11, r11, 24            // Shift off junk data
  ldc          	r9, PID_ACK             // Check for good ACK
  xor          	r9, r11, r9              
  bt           	r9, BadHandShake        // Valid handshake not received from host. we need to resend...
   
DoneTx:
  bu        	NextToken

BadHandShake:				                            // Received a bad handshake
  mov 		r0, r11			                          // TODO Debug only
  bl 		ERR_BadTxHandshake			
  bu 		NextToken

.align 16
.skip 6
SmallTxPacket:
    inct       r8, res[r5]                      // Input tail length + 10
    sub        r8, r8, 10
    in         r6, res[r5]                      // Input tail data
    //shl        r8, r8, 4                        // Tail length bytes to bits *2

    ldc        r5, 0
    bru       r8                                // branch taillength

//--------------------------------------------------------------------------
.align 32
.skip 0
TxTail0s:                                       // We know this is a < 4 byte packet, so is 0 length packet
                                                // So crc = 0. Note our normal crc calculation works for this, it is 
                                                //not a special CRC case, but helps with timing.
    outpw      res[TXD], r11, 8                 // PID
    //crc32    r7, r11, r9                      // r11: 0
    //not      r7, r7
    outpw      res[TXD], r5, 16                 // r5: 0
    bu         DoneTail

.align 32
.skip 0
TxTail1s:
    // One tail byte
    outpw     res[TXD], r11, 8        // PID
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
    outpw     res[TXD], r11, 8        // PID
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
                                                    outpw        res[TXD], r11, 8     // PID
                                                      outpw        res[TXD], r6, 24
                                                        crc8         r7, r6, r6, r9
                                                          crc8         r7, r6, r6, r9
                                                            crc8         r7, r6, r6, r9

                                                              crc32        r7, r5, r9               // r5: 0     
                                                                not          r7, r7
                                                                  outpw        res[TXD], r7, 16
                                                                    bu           DoneTail

