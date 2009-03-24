// r10: EP number
// OUT Token Packet -----------------------------------------------------------------------------
// Ports
#define RXD r0
#define RXA r1
#define TXD r2
#define RXE r3

.align 4
.skip 0
Pid_Out:
  ldw       r1, sp[9]                     	// Load Out timeout timer from stack TODO Use port count rather than timer?
    
LoadOUTStatTable:
  ldaw      r6, dp[ep_stat_table_OUT]     	// Load EP OUT status table 

LoadEPStatus:
  ldw       r4, r6[r10]                   	// Load EP Status from table
  //eq        r11, r11, EP_OUT_IDLE       	// EP_OUT_IDLE == 0
  //bt        r4, Err_EndpointProblem       	// Endpoint is halted/in trouble

                                          	// r4:0 for buffer index
SetupOutTimeOut:
  in        	r11, res[r1]                  	// Get current time
  //ldc       	r6, 10000
  //add       	r11, r11, r6                  	// Add timeout to current time
  //setd      	res[r1], r11                  	// Set timer event cond
  //eeu       	res[r1]                       	// Enable events on timer
  //setsr		0x1				// Enable thread events

  //ldw         r1, sp[10]
ReadDataPidByte:          
  //clre	
  ldw       r1, sp[10]           		// Load RxA Port ID (r1) 
  eeu       res[r1]                       	// Enable events on RXA
					// Clear events                
  inpw      r6, res[RXD], 8               	// Data PID in r6 (keep for sequence checking later)
  //edu       res[r1]                       	// Disable events on timer
  //in   	      r1, res[r1]
  //setc        res[r1], XS1_SETC_RUN_CLRBUF	// TODO Experiments have shown that this clrbuf is no longer required

CheckSpaceAvail:  
  ldw       r5, r5[r10]                    // Check datasize == 0 
  bt        r5, PrimaryBufferFull          // Branch if data in primary buffer
                                         
LoadBufferSelectTable_OUT:                                            
  ldaw      r5, dp[bufferSelectTable_OUT]
 
LoadReleventEPBuffer:
  ldw       r5, r5[r10]                   // r10: EP number
   
  bl        doRXData

 //ldc    r11, PIDn_ACK                     
  //in  r11, res[RXD]
  //syncr res[TXD]
  //dc    r11, PIDn_ACK
  //outpw  res[TXD], r11, 8
  //setc res[TXD], CTRL_RUN_CLRBUF
 
 // bl	    printintln
 
//WaitForData:
 // waiteu 

//.align 4
//.skip 0
//NextRxWord:
//  in     r11, res[RXD]
//  stw    r11, r5[r4]
//  add    r4, r4, 1
//  crc32  r7, r11, r9                      // crc32 <crc> <data> <poly> 
//  waiteu 

// ------------------------------------------------------------------------------------------------

//.align 16
//.skip 12
//RxALow:                                   // Event on evelope signal low
  //endin  r8, res[RXD]
  //bru    r8

// Word aligned
//.align 16
//OutTail4:
  //in     r11, res[RXD]                    // Do IN to get rid of junk                  
  //xor    r3, r3, r3
  //crc32  r7, r3, r9                       
  //ldc    r11, 0x7000
  //bu     CheckCrc16
      
// 1 Tail Byte
//.align 16
//OutTail1:
  //in     r3, res[RXD]
  //shr    r3, r3, 24                       // Shift off junk
  //crc32  r7, r3, r9
  //ldc    r11, 0x80be                      // Load CRC magic number     
  //bu     CheckCrc16

// Two Tail Bytes
//.align 16
//OutTail2:
  //in     r3, res[RXD]
  //shr    r3, r3, 16
  //crc32  r7, r3, r9
  //ldc    r11, 0x3ffe
  //bu CheckCrc16

// Three Tail Bytes
//.align 16
//OutTail3:
  //in     r3, res[RXD]
  //shr    r3, r3, 8
  //crc32  r7, r3, r9
  //xor    r11, r11, r11
  //crc32  r7, r11, r9 
  //ldc    r11, 0x0070

                                          // r11 : Expected CRC magic number
                                          // r10 :
                                          // r9  :
                                          // r8  : Tail length (bytes)
                                          // r7  : Calculated CRC magic number
                                          // r6  : Data PID
                                          // r5  : Buffer 
                                          // r4  : Buffer index
                                          // r3  : Tail data

//CheckCrc16:                               // If bad CRC16 dont save data.  Dont handshake  
  //eq     r7, r7, r11                      // If CRC16 good, send ACK (if not ISO ep)       
  //bf     r7, Err_BadDataCrc
    
//DoOutHandShake:                           // Data CRC good, only send ack if not Iso EP
  // ldaw   r7, dp[ep_type_table_OUT]                                          
  // ldw    r7, r7[r10]
  // bf     r7, GoodData_ISO
  //ldc    r11, PIDn_ACK                     
  //outpw  res[TXD], r11, 8


// Rx to Tx turnaround interpacket delay end ----------------------------------------------
// Note.. must meet tx to rx when receiving next token .. how long is this?

//SaveTail:                                 // Save Tail into buffer
  //stw    r3, r5[r4]
  //bt 	    r10, CheckDataSize
  CalcDataLength:                           // Calculate length of received data (in bytes)
  shl       r4, r4, 2                     // Convert complete received words to bytes 
  shr       r8, r8, 3                     // r6: number of tail bits, convert to bytes
  add       r4, r4, r8                    // r4: total bytes
 
 

PidSequenceCheck:                         // Check PID sequencing, if bad we still send ACK, but junk data.
  ldaw      r11, dp[ep_pid_sequence_table_OUT]
  //shr       r6, r6, 24                    // Shift off junk off data PID byte
  //ld8u      r9, r11[r10]
  //eq        r6, r6, r9
  //bf        r6, Err_BadPidSeq 
 
//ToggleDataPid:                            // Toggle data pid and save back 
  //ldc       r6, 0x8
  //xor       r9, r9, r6
  //st8       r9, r11[r10] 

// Data is ok, data saved in primary buffer (as there was room)
// Trigger bufferManager with datalength received
TriggerBufferManager:
    ldw       r11, sp[18]                   // Load channel from stack (5th arg)
  out       res[r11], r4                  // Send data length to bufferManager
  out       res[r11], r10                 // ...and EP number

  bu        NextToken 


// Various Error handling functions -------------------------------------------------------------------
Err_RxErr:                                // RxError signal high during data packet receive:
  in        r11, res[RXA]            // Wait for rxa low and ignore packet (let transaction timout)
  bu        NextToken
   
Err_EndpointProblem:                   
  // TODO:Endpoint in trouble (send STALL) or disabled (ignore?)...
  bu        NextToken

//Err_BadDataCrc:                         // Data packet received with bad crc: Ignore; wait for timeout (host will re-send)
  //bu        NextToken
  
Err_BadPidSeq:                            // Bad pid sequencing (already ACKed good data).  Ignore data
  bu        NextToken

PrimaryBufferFull:                        // Wait for end of data then send NAK
  //ldaw      r5, dp[epTypeTable_OUT]
  //ldw       r5, r5[r10]
  //bt        r5, PrimaryBufferFull_NotIso

PrimaryBufferFull_NotIso:
  ldw       RXA, dp[flag1_port] 
  setc      res[RXA], XS1_SETC_COND_NEQ 
  in        r11, res[RXA]
  setc      res[RXA], XS1_SETC_COND_EQ 
  in        r11, res[RXA]
                                          // TODO: Observe interpacket delay 
  ldc       r11, 0x5a 
  outpw     res[TXD], r11, 8
  //endin     r11, res[RXD]
  //in        r11, res[RXD]
  setc      res[RXD], XS1_SETC_RUN_CLRBUF  
  bu        NextToken

// Timedout waiting for data after OUT... go back to waiting for tokens
OutDataTimeOut:
  clre
  bl	ERR_OutDataTimeout
  bu Loop


CheckDataSize:
  shl       r4, r4, 2                     // Convert complete received words to bytes 
  shr       r8, r8, 3                     // r6: number of tail bits, convert to bytes
  add       r4, r4, r8                    // r4: total bytes
 
  ldc	    r11, 50
  eq        r11, r11, r4
  bt        r11, NextToken2
 
  mov r0, r6
  ldw       r1, r5[0]
  mov r2, r3
  mov r3, r10
  //bl	    printhex2
  bu 	    NextToken

NextToken2:
 ldc       r10, 0x88 
 ldc       r6, 0x99
 bu NextToken
