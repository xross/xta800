
///////////////////////////////////////////////////////
// utmi_io.S
//
// XMOS Semiconductor Ltd
// Ross Owen
//
///////////////////////////////////////////////////////

//#include <AutoDefines.h>
#include <xs1_user.h>


#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"

// Tables of tables...
#include "utmi_buffer_select_table.S"

#include "utmi_pid_jump_table.S"

#include "utmi_out.S"

#include "utmi_ping.S"
#include "utmi_setup.S"
#include "utmi_in.S"

/////////////////////////////////////////////////////////////////////////
// void ConChanIntHandler()
.extern ConChanIntHandler
.globl ConChanIntHandler.nstackwords
.linkset ConChanIntHandler.nstackwords, 0
.globl ConChanIntHandler
.text 
ConChanIntHandler:
  stw		r0, sp[1]
  
  clrsr 	0x1			     // Disable thread events
  ldw   	r11, sp[20]		  // Load channel ID
  inct  	r0, res[r11]   


  ldc		r11, XUD_CT_RESET	// Check for RESET
  eq    	r11, r0, r11
  bt 		r11, Return

  retsp 	0


.extern XUD_LLD_IoLoop
.globl XUD_LLD_IoLoop.nstackwords
.globl XUD_LLD_IoLoop.maxthreads
.globl XUD_LLD_IoLoop.maxtimers
.globl XUD_LLD_IoLoop.maxchanends

.linkset XUD_LLD_IoLoop.nstackwords, 20
.linkset XUD_LLD_IoLoop.maxchanends, 0
.linkset XUD_LLD_IoLoop.maxtimers, 0
.linkset XUD_LLD_IoLoop.maxthreads, 0

.globl XUD_LLD_IoLoop

.text 

//utmi_io(rxd_port, rxa_port, txd_port, rxe_port, flag_port, c0, c1) 
//XUD_LLD_Io(c0, c1)  
//c0 for OUT, c1 for IN

// sp 1..7 reg save
// sp 8 tx handshake timer
// sp 9 out timer

.align 4
.skip 0
XUD_LLD_IoLoop:
utmi_io:
  entsp     16            
  stw       r4, sp[1]
  stw       r5, sp[2]
  stw       r6, sp[3]
  stw       r7, sp[4]
  stw       r8, sp[5]
  stw       r9, sp[6]
  stw       r10, sp[7]

  clrsr     0x1
 
SetControlChannelEvent:
  ldap      r11, ConChanIntHandler
  ldw	      r10, sp[20]
  setv	   res[r10], r11
  setc      res[r10], XS1_SETC_IE_MODE_EVENT 
  eeu       res[r10] 
 
ConfigRxDEventVector:                     	// Configure events
  setc      res[RXD], XS1_SETC_IE_MODE_EVENT 	// Default?
  ldap      r11, NextRxWord
  setv      res[RXD], r11

ConfigRxErrEventVector:
  setc      res[RXE], XS1_SETC_IE_MODE_EVENT 
  ldap      r11, Err_RxErr
  setv      res[RXE], r11

ConfigRxA:
  setc      res[RXA], XS1_SETC_COND_EQ
  ldc       r11, 0                       
  setd      res[r1], r11    
  ldap      r11, RxALow
  setv      res[r1], r11


ConfigValidToken:
   ldw       r10, dp[flag0_port]            	// Valid token port
   setc      res[r10], XS1_SETC_COND_NONE
   ldap	     r11, TxHandshakeTimeOut		// ValidToken is used for timing timeout period when expecting an ack after tx
   setv	     res[r10], r11		

TxHandShakeTimer:                         	// Used for timeout waiting for handshake after tx
  getr      r10, XS1_RES_TYPE_TIMER
  ecallf    r10

  ldap      r11, TxHandshakeTimeOut 
  setv      res[r10], r11
  setc      res[r10], XS1_SETC_COND_AFTER

  stw       r10, sp[8]                    	// Save timer resource ID to the stack

SetupOutTimer:
  getr      r10, XS1_RES_TYPE_TIMER
  ecallf    r10
  
  ldap      r11, OutDataTimeOut
  setv      res[r10], r11
  setc      res[r10], XS1_SETC_COND_AFTER

  stw       r10, sp[9]

PortsOnStack:					// Put ports on stack (loads therefore short insts)
  ldw       r11, dp[flag1_port]           	// RxA
  stw       r11, sp[10]
  ldw       r11, dp[flag0_port]            	// Valid token port
  stw       r11, sp[11]

  ldc    r11, 0x0070
  stw    r11, sp[12]

EventEnable:
  //eeu       res[RXD]                      // RxData
  //eeu       res[RXA]                      // RxActive

  bu        Loop

.align 4
.skip 0
Loop:
NextToken:
  
PreLoads:
  ldaw      r3, dp[ep_stat_table_IN]
  ldaw      r4, dp[bufferSelectTable_IN]
  ldaw      r5, dp[ep_datasize_table_OUT_A] 
  ldaw      r6, dp[ep_pid_sequence_table_IN_A]
  //ldaw      r6, dp[pidSelectTable_IN_A] 
  ldc       r7, 0x3334                   	// CRC16 init (out)
  ldaw      r8, dp[ep_datasize_table_IN_A]
  ldc       r9, 0xa001                    	// CRC16 poly
  ldaw      r10, dp[PidJumpTable]

  clre						// Clear events. inpw on rxd will trap when thread events enabled
  ldw       r1, sp[10]           		// Load RxA Port ID 
  //edu       res[r1]
  ldap      r11, NextRxWord
  setv      res[RXD], r11
  //edu       res[RXD]       

EnableCtlChanEvents:
  ldw	     r11, sp[20]
  eeu        res[r11] 

               	 
Loop_BadPid: 
//------------------------------------------------------------------------------------------------

.align 4
.skip 0
ReadToken_PID:
  setsr      0x1		          	// Enable thread events
                                          	// Original USB Token: | CRC | EP | ADDR | PID | junk 
                                          	// External logic does address checking and crc check
                                          	// and passes | 0000 4-bit EP | 0000 4-bit PID | 
  inpw      r11, res[RXD], 8              	// Read PID
  clrsr     0x1
  //clre
  shr       r11, r11, 24
  ldw       r11, r10[r11]                 	// Load relevant branch address

CheckValidToken:
  ldw       r1, sp[11]                    	// Valid token port
  in        r1, res[r1]
  bf        r1, InvalidToken    	  	// If VALID_TOKEN not high, ignore token

ReadToken_EP:
  inpw      r10, res[RXD], 8   			// Read EP Number 
  shr       r10, r10, 24          
  
BranchOnTokenPid:
  bau       r11                           	// Branch on PID
  // Branches to relevant token handing code (e.g. in utmi_out.S). When done branchs back to NextToken.

// (un-implemented) Pid list: 
Pid_Reserved:           			// Should never get here     
nop 
//Pid_Out:          
Pid_Ack: 
nop         
Pid_Data0:           
//Pid_Ping:           
Pid_Sof:                			// Should never get here: (G4) hardware swallows SOF       
nop
Pid_Nyet:           
nop
Pid_Data2:          
nop
Pid_Split:  
  //bl ERR_GotSplit  
//Pid_In:             
nop
Pid_Nak:            
nop
Pid_Data1:          
nop
Pid_Pre:            
//Pid_Setup:          
nop
Pid_Stall:          

Pid_Datam:    

 
InvalidToken:  
  //bl    	ERR_UnsupportedToken 
  bu       	Loop   // Ignore unknown toks


Pid_Bad:
  bl           ERR_UnsupportedToken


.align 4
Return:
  ldw   	r11, sp[20]		  // c_con_io
  inct  	r11, res[r11]   

  ldw       	r11, sp[18]                   // Load channel from stack (5th arg)
  outct     	res[r11], XS1_CT_END
  ldw          	r11, sp[19]                   // Load channel from stack. (6 arg, 16 stack word)
  outct        	res[r11], XS1_CT_END  		     // Send EP number

  ldw     r11, sp[8]
  freer   res[r11]
  ldw     r11, sp[9]
  freer   res[r11]
  ldw       r4, sp[1]
  ldw       r5, sp[2]
  ldw       r6, sp[3]
  ldw       r7, sp[4]
  ldw       r8, sp[5]
  ldw       r9, sp[6]
  ldw       r10, sp[7]
  retsp     16
                       

// DP Here:
DP:
