// r10: EP number
// OUT Token Packet -----------------------------------------------------------------------------
// Ports
#define RXD r0
#define RXA r1
#define TXD r2
#define RXE r3

.align 4
.skip 0
.cc_top Pid_Out.func, Pid_Out
Pid_Out:
    ldw       r5, r5[r10]                     // Load relevant EP chanend
    eeu       res[r5]                         // Enable events on channel. Event vector should be restored to OutReady already

    setsr     0x1                             // Enable thread events

OutNotready:                                  // Endpoint not ready...                               
    clre 
    //edu       res[r5]                                     
    bu        PrimaryBufferFull

.align 4
OutReady:
    //clre                                      // Clear all events and flags.... nooo RXD!
    //edu  res[r5]           
    clre                     
    
    //inct      r11, res[r5]                  // TODO Should use end CT here.  Bug?
    in          r11, res[r5]
    
ReadDataPidByte:          
    inpw      r6, res[RXD], 8               	// Data PID in r6 (keep for sequence checking later)
    eeu       res[RXD]
  
    ldw       r1, sp[10]           		        // Load RxA Port ID (r1) 
    eeu       res[r1]                       	// Enable events on RXA
    in        r11, res[r1]
    ldw       r1, sp[(STACK_EXTEND+5)]        // Load ep type table
   
    bl        doRXData                                

    bf        r7, NextToken                   // Check for bad crc

CheckEpTypeOut:
    
    ldw       r1, r1[r10]                     // load EP type
    bf        r1, CalcDataLength         // ISO endpoint 

DoOutHandShakeOut:                            // Data CRC good and EP not Iso: Send Ack 
    ldc       r11, PIDn_ACK
    outpw     res[TXD], r11, 8

CalcDataLength:                               // Calculate length of received data (in bytes)
    //shl       r4, r4, 2                       // Convert complete received words to bytes 
    shr       r8, r8, 3                       // r8: number of tail bits, convert to bytes
    //add       r4, r4, r8                      // r4: total bytes

StoreTailDataOut:
    //stw       r3, r5[r4]
    out       res[r5], r3
    add       r8, r8, 10                      // TODO could just send num bits instead of bytes + 10
    outct     res[r5], r8                     // Send tail length
   // outct     res[r5], XS1_CT_END             // Close route
    //chkct     res[r5], XS1_CT_END

 
 

PidSequenceCheck:                         // Check PID sequencing, if bad we still send ACK, but junk data.
  //ldaw      r11, dp[ep_pid_sequence_table_OUT]
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
    //ldw       r11, sp[18]                   // Load channel from stack (5th arg)
    //out       res[r11], r4                  // Send data length to bufferManager
    //out       res[r11], r10                 // ...and EP number

BadDataCrcOut:
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
  endin     r11, res[RXD]
  in        r11, res[RXD]
  setc      res[RXD], XS1_SETC_RUN_CLRBUF  
  bu        NextToken

// Timedout waiting for data after OUT... go back to waiting for tokens
OutDataTimeOut:
  //clre
  bl	ERR_OutDataTimeout
  bu Loop


    
.cc_bottom Pid_Out.func
