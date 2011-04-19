
// XUD_LLD_IoLoop.S
// Main USB interfacing loop
//
// XMOS Semiconductor Ltd
// Ross Owen
//
///////////////////////////////////////////////////////

#include <xs1.h>

#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"

#define STACK_EXTEND 16


#include "utmi_out.S"
#include "utmi_ping.S"
#include "utmi_in.S"
#include "utmi_setup.S"

/////////////////////////////////////////////////////////////////////////
// void ConChanIntHandler()
// Interupt handler for control channel 
.extern ConChanIntHandler
.globl ConChanIntHandler.nstackwords
.linkset ConChanIntHandler.nstackwords, 0
.globl ConChanIntHandler
.text 
.cc_top ConChanIntHandler.func, ConChanIntHandler
ConChanIntHandler:
    clrsr     0x18                              // Clear InInterrupt bit (and InKernel)
   
    get       r11, ed                           // Get channel ID
    setc      res[r11], XS1_SETC_IE_MODE_EVENT  // Set IE mode back to events
    clre                                        // Clear all events

    ldw     r10, dp[SavedSp]                    // Restore stack pointer
    set     sp, r10

    chkct   res[r11], XS1_CT_END                // Channel shutdown
    bu 		  Return                              // Branch to IoLoop return code

.cc_bottom ConChanIntHandler.func


/////////////////////////////////////////////////////////////////////////////////////////////////////////
// XUD_LLD_IoLoop(rxd_port, rxa_port,txd_port, rxe_port,  flag0_port, c_out,  c_in, c_ctl_io, c_ctl_buf)
//
.extern XUD_LLD_IoLoop
.globl XUD_LLD_IoLoop.nstackwords
.globl XUD_LLD_IoLoop.maxthreads
.globl XUD_LLD_IoLoop.maxtimers
.globl XUD_LLD_IoLoop.maxchanends

.linkset XUD_LLD_IoLoop.nstackwords, STACK_EXTEND
.linkset XUD_LLD_IoLoop.maxchanends, 0
.linkset XUD_LLD_IoLoop.maxtimers, 0
.linkset XUD_LLD_IoLoop.maxthreads, 0

.globl XUD_LLD_IoLoop
.cc_top XUD_LLD_IoLoop.func, XUD_LLD_IoLoop
.text 

// Stack Usage:
// 0
// 1..7:             : Reg save
// 8                 : TX handshake timer
// 9                 : Out timer
// 10                : RXA_port
// 11                : Valid token port
// 12                : 
// 13                : Saved SP

// Params
// STACK_EXTEND + 1  : Flag0_port
// STACK_EXTEND + 2  : c_out
// STACK_EXTEND + 3  : c_in
// STACK_EXTEND + 4  : c_ctl_io
// STACK_EXTEND + 5  : c_ctl_buf
// STACK_EXTEND + 6  : EP Type table (out)
#define SP_EPTYPES_IN (STACK_EXTEND + 7)  //: EP Type table (in)

.align 4
.skip 0
XUD_LLD_IoLoop:
utmi_io:
    entsp      STACK_EXTEND        
    stw        r4, sp[1]
    stw        r5, sp[2]
    stw        r6, sp[3]
    stw        r7, sp[4]
    stw        r8, sp[5]
    stw        r9, sp[6]
    stw        r10, sp[7]

SaveStackPointer:
    ldaw       r11, sp[0]
    stw        r11, dp[SavedSp]


SetControlChannelEvent:
    ldap       r11, ConChanIntHandler
    ldw	       r10, sp[(STACK_EXTEND + 4)]
    setv       res[r10], r11
    setc       res[r10], XS1_SETC_IE_MODE_INTERRUPT 
    eeu        res[r10]                               // Enable events/interupts on resource 
    setsr      0x2                                    // Enable thread interupts on kill channel

ConfigRxDEventVector:                     	          // Configure events
    setc       res[RXD], XS1_SETC_IE_MODE_EVENT 	
    ldap       r11, NextRxWord
    setv       res[RXD], r11
    eeu        res[RXD]        

ConfigRxErrEventVector:
    setc       res[RXE], XS1_SETC_IE_MODE_EVENT 
    ldap       r11, Err_RxErr
    setv       res[RXE], r11

ConfigRxA:
    setc       res[RXA], XS1_SETC_COND_EQ
    ldc        r11, 0                       
    setd       res[r1], r11    
    ldap       r11, RxALow
    setv       res[r1], r11
    eeu        res[RXA]

ConfigValidToken:
    ldw        r10, dp[flag0_port]                    // Valid token port
    setc       res[r10], XS1_SETC_COND_NONE
    ldap	     r11, TxHandshakeTimeOut		            // ValidToken is used for timing timeout period when expecting an ack after tx
    setv	     res[r10], r11		

TxHandShakeTimer:                         	          // Used for timeout waiting for handshake after tx
    getr       r10, XS1_RES_TYPE_TIMER
    ecallf     r10

    ldap       r11, TxHandshakeTimeOut 
    setv       res[r10], r11
    setc       res[r10], XS1_SETC_COND_AFTER

    stw        r10, sp[8]                    	        // Save timer resource ID to the stack

SetupOutTimer:
    getr       r10, XS1_RES_TYPE_TIMER
    ecallf     r10

    ldap       r11, OutDataTimeOut
    setv       res[r10], r11
    setc       res[r10], XS1_SETC_COND_AFTER

    stw        r10, sp[9]

PortsOnStack:					                                // Put ports on stack (loads therefore short insts)
    ldw        r11, dp[flag1_port]           	        // RxA
    stw        r11, sp[10]
    ldw        r11, dp[flag0_port]            	      // Valid token port
    stw        r11, sp[11]

    ldc        r11, 0x0070                          
    stw        r11, sp[12]

    bu         Loop

// Main IO Loop
.align 4
.skip 0
Loop:
NextToken:

PreLoads:
    //ldaw       r3, dp[ep_stat_table_IN]
    ldaw       r4, dp[bufferSelectTable_IN]
    ldaw       r5, dp[ep_datasize_table_OUT_A] 
    ldaw       r6, dp[ep_pid_sequence_table_IN_A]
    ldc        r7, 0x3334                   	        // CRC16 init (out)
    ldaw       r8, dp[ep_datasize_table_IN_A]
    ldc        r9, 0xa001                    	        // CRC16 poly
    ldaw       r10, dp[PidJumpTable]

    ldw        r1, sp[10]           		              // Disable events on RxA
    edu        res[r1]
    
    ldap       r11, NextRxWord                        // Restore RXD event vector
    setv       res[RXD], r11
              	 
Loop_BadPid: 
//------------------------------------------------------------------------------------------------

.align 4
.skip 0
ReadToken_PID:
//    setc       res[RXD], XS1_SETC_RUN_CLRBUF
                                                   // Original USB Token: | CRC | EP | ADDR | PID | junk 
                                         	         // Received token is address and CRC checked.
                                                   // We receive: | 0000 4-bit EP | 0000 4-bit PID | 
    inpw       r11, res[RXD], 8                    // Read 8 bit PID
    shr        r11, r11, 24                        // shift off junk  
    ldw        r11, r10[r11]                 	     // Load relevant branch address

CheckValidToken:
    ldw       r1, sp[11]                    	     // Valid token port
    in        r1, res[r1]
    bf        r1, InvalidToken                     // If VALID_TOKEN not high, ignore token

ReadToken_EP:
    inpw      r10, res[RXD], 8   			             // Read EP Number 
    shr       r10, r10, 24          

BranchOnTokenPid:
    bau       r11                           	     // Branch on PID to relevant token handing code 
                                                   // (e.g. in utmi_out.S etc). When done branchs back to NextToken.

// Un-implemented PID list
Pid_Reserved:           			                     // Should never get here     
nop 
//Pid_Out:          
Pid_Ack: 
nop         
Pid_Data0:           
//Pid_Ping:           
Pid_Sof:                			                     // Should never get here: (G4) hardware swallows SOF       
nop
Pid_Nyet:           
nop
Pid_Data2:          
nop
Pid_Split:  
nop
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

    add         r0, r11, 0
    bl          ERR_UnsupportedToken
InvalidToken:
    bu       	Loop                                 // Invalid token received Ignore unknown toks

Pid_Bad:                                           // Bad PID received, ignore
    //bl           ERR_UnsupportedToken
    bu      Loop

.align 4
Return:
    mkmsk      r10, 32                             // Pass on kill (send -1) and close down routes
    //ldw        r11, sp[18]                        
    //out        res[r11], r10
 
    //ldw        r11, sp[19]    
    //out        res[r11], r10

    ldw        r11, sp[18]                       
    outct      res[r11], XS1_CT_END

    ldw        r11, sp[19]                        
    outct      res[r11], XS1_CT_END

    ldw        r11, sp[8]                          // Free timers
    freer      res[r11]
    ldw        r11, sp[9]
    freer      res[r11]
 
    ldw        r4, sp[1]                           // Register restore
    ldw        r5, sp[2]
    ldw        r6, sp[3]
    ldw        r7, sp[4]
    ldw        r8, sp[5]
    ldw        r9, sp[6]
    ldw        r10, sp[7]
    retsp     STACK_EXTEND
.cc_bottom XUD_LLD_IoLoop.func

// Tables of tables...
#include "utmi_buffer_select_table.S"
#include "utmi_pid_jump_table.S"
// DP Here:
DP:

