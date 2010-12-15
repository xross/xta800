
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
#include "XUD_UIFM_Defines.h"


#define SUSPEND_TIMEOUT 300000   // 300,000 timer ticks = 3ms

.section        .cp.const4,"aMc",@progbits,4
.align  4
suspendTimeout:
.long  SUSPEND_TIMEOUT
.text

.section        .cp.const4,"aMc",@progbits,4
.align  4
suspend_t_wtwrsths:
.long SUSPEND_T_WTWRSTHS
.text

.extern SetupChannelVectors
.globl SetupChannelVectors.nstackwords
.globl SetupChannelVectors.maxthreads
.globl SetupChannelVectors.maxtimers
.globl SetupChannelVectors.maxchanends

.linkset SetupChannelVectors.nstackwords, 16
.linkset SetupChannelVectors.maxchanends, 0
.linkset SetupChannelVectors.maxtimers, 0
.linkset SetupChannelVectors.maxthreads, 0

.globl SetupChannelVectors
.cc_top SetupChannelVectors.func, SetupChannelVectors
.text 


// void SetupChannelVectors(chansOut, Count);

.align 4
.skip 0
SetupChannelVectors:
    entsp 16
    stw         r5, sp[1]
    stw         r6, sp[2]
    
EnableEpChanEvents_Out2:
    mov           r2, r1

EventLoop2:                                      // Set event vectors for all channels 
    bf          r1, EventLoopEnd2                
    sub         r1, r1, 1
    ldw         r5, r0[r1]
    ldap        r11, OutReady
    setv        res[r5], r11

    add         r6, r2, r1                    // Add in channel offset
    ldw         r5, r0[r6]
    ldap        r11, InReady
    setv        res[r5], r11
    bu          EventLoop2

EventLoopEnd2:
    ldw         r5, sp[1]
    ldw         r6, sp[2]
    retsp       16

.cc_bottom SetupChannelVectors.func


#define STACK_EXTEND 16


#include "token_out.S"
#include "utmi_ping.S"
#include "token_in.S"
#include "utmi_setup.S"
#include "token_sof.S"

// Stack frame:
// 0
// 1..7:             : Reg save
// 8                 : TX handshake timer
// 9                 : Out timer
// 10                : RXA_port
// 11                : Valid token port
// 12                : ?
// 13                : Saved SP
// 14                : Suspend/Reset timer 


// Params
// STACK_EXTEND + 1  : Flag0_port
// STACK_EXTEND + 2  : reg_read_port
// STACK_EXTEND + 3  : reg_write_port
// STACK_EXTEND + 4  : 0
// STACK_EXTEND + 5  : c_ctl_buf
// STACK_EXTEND + 6  : EP Type table (out)
#define SP_EPTYPES_IN (STACK_EXTEND + 7)  //: EP Type table (in)
#define SP_EPCHANS    (STACK_EXTEND + 7)
#define SP_EPCOUNT    (STACK_EXTEND + 9)
#define SP_REGREAD    (STACK_EXTEND + 10)
#define SP_REGWRITE   (STACK_EXTEND + 11)

/////////////////////////////////////////////////////////////////////////
// void ResetIntHandler()
// Interupt handler for reset/suspend timer
.extern ResetIntHandler
.globl ResetIntHandler.nstackwords
.linkset ResetIntHandler.nstackwords, 0
.globl ResetIntHandler
.text 
.cc_top ResetIntHandler.func, ResetIntHandler
ResetIntHandler:
    clrsr   0x18                                // Clear InInterrupt bit (and InKernel)
  
    get     r11, ed                             // Get timer resource ID
    setc    res[r11], XS1_SETC_IE_MODE_EVENT    // Set IE mode back to events
    clre                                        // Clear all events
    setc    res[r11], XS1_SETC_IE_MODE_INTERRUPT  // Set IE mode back to events

    ldw     r10, dp[SavedSp]                    // Restore stack pointer
    set     sp, r10

    // Reset flags
    ldc     r10, UIFM_CMD_WRITE                 
    ldc     r9, UIFM_REG_FLAGS
    or      r10, r10, r9
    ldw     r9, sp[19]                          // Reg write port
    out     res[r9], r10
    ldc     r10, 0
    out     res[r9], r10

    // Put in FS mode s(XcvrSelect and TermSelect = 1)
    ldc     r10, UIFM_CMD_WRITE                 
    ldc     r9, UIFM_REG_PHYCON
    or      r10, r10, r9
    ldw     r9, sp[19]
    out     res[r9], r10
    ldc     r10, 0x7
    out     res[r9], r10

    // Delay by SUSPEND_WTWRSTHS
    setc    res[r11], XS1_SETC_COND_NONE     // Read current time
    in      r10, res[r11]
    ldw     r9, cp[suspend_t_wtwrsths]
    add     r10, r10, r9
    setd    res[r11], r10   
    setc    res[r11], XS1_SETC_COND_AFTER    
    in      r10, res[r11]

    // Check for J state. If so the initial SE0 was due to suspend (remain in HS) else was reset 
    ldc     r10, UIFM_CMD_READ                 
    ldc     r9, UIFM_REG_FLAGS
    or      r10, r10, r9
    ldw     r9, sp[19]                       // Reg Write Port
    out     res[r9], r10
    ldw     r9, sp[18]                       // Reg Read Port
    in      r10, res[r9]                    

    // Check for reset (high speed K so full speed j)
    ldc        r9, 0x10                      // HS K / FS J bit
    and        r9, r10, r9
    bt         r9, SuspendDetect
ResetDetect:
    ldc        r0, 0                         // Return 0 for reset
    bu         Return


SuspendDetect:
    ldc        r0, 1
    bu 		     Return                        // Return 0 for suspend 

.cc_bottom ResetIntHandler.func



/////////////////////////////////////////////////////////////////////////////////////////////////////////
//    void XUD_LLD_IoLoop(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
//    chanend c_out, chanend c_in, chanend c_con_io, chanend c_ctl_buf,
//    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], XUD_chan epChans[], epCount) ;
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



.align 4
.skip 0
XUD_LLD_IoLoop:
utmi_io:
    entsp       STACK_EXTEND        
    stw         r4, sp[1]
    stw         r5, sp[2]
    stw         r6, sp[3]
    stw         r7, sp[4]
    stw         r8, sp[5]
    stw         r9, sp[6]
    stw         r10, sp[7]

SaveStackPointer:
    ldaw        r11, sp[0]
    stw         r11, dp[SavedSp]

    ldc           r11, 0
    stw           r11, sp[15]

//SetControlChannelEvent:                       // Currently we don't need this interrupt handler in XUD_lite
    //ldap        r11, ConChanIntHandler
    //ldw	        r10, sp[(STACK_EXTEND + 4)]
    //setv        res[r10], r11
    //setc        res[r10], XS1_SETC_IE_MODE_INTERRUPT 
    //eeu         res[r10]                      // Enable events/interupts on resource 
    //setsr       0x2                           // Enable thread interupts on kill channel


#if 0                                           // To speed up reset this is now only done once
EnableEpChanEvents_Out:
    ldw         r9, sp[SP_EPCOUNT]              // Load passed EP count from stack
    ldw         r7, sp[SP_EPCOUNT]
    ldw         r8, sp[SP_EPCHANS]              // Load EP Channels array

EventLoop:                                      // Set event vectors for all channels 
    bf          r9, EventLoopEnd                
    sub         r9, r9, 1
    ldw         r5, r8[r9]
    ldap        r11, OutReady
    setv        res[r5], r11

    add         r6, r7, r9                    // Add in channel offset
    ldw         r5, r8[r6]
    ldap        r11, InReady
    setv        res[r5], r11
    bu          EventLoop

EventLoopEnd:
#endif     
        

ConfigRxDEventVector:                     	  // Configure events
    setc        res[RXD], XS1_SETC_IE_MODE_EVENT 	
    ldap        r11, NextRxWord
    setv        res[RXD], r11
    //eeu         res[RXD]        

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


PortsOnStack:					                          // Put ports on stack (loads therefore short insts)
    ldw        r11, dp[flag1_port]           	  // RxA
    stw        r11, sp[10]
    ldw        r11, dp[flag0_port]            	// Valid token port
    stw        r11, sp[11]

    ldc        r11, 0x0070                          
    stw        r11, sp[12]

    ldw         r11, sp[23]
    stw         r11, dp[chanArray]



SetupSuspendResetTimer:
    getr       r10, XS1_RES_TYPE_TIMER
    ecallf     r10

    stw        r10, sp[14]

    ldap       r11, ResetIntHandler
    setv       res[r10], r11
    
    setc       res[r10], XS1_SETC_COND_NONE
    in         r11, res[r10]                    // Get current time
    ldw        r9, cp[suspendTimeout]
    add        r11, r11, r9
    setd       res[r10], r11

    setc       res[r10], XS1_SETC_COND_AFTER
    setc       res[r10], XS1_SETC_IE_MODE_INTERRUPT 
    eeu        res[r10]                         // Enable events/interupts on resource 
    setsr       0x2                             // Enable thread interrupts        

    bu         Loop

// Main IO Loop
.align 4
.skip 0
Loop:
NextToken:
    clre
PreLoads:
    ldc           r3, 0
    //ldaw       r3, dp[ep_stat_table_IN]
    //ldaw       r4, dp[bufferSelectTable_IN]
    ldw         r5, dp[chanArray]                            // EP channes array
    ldaw       r6, dp[ep_pid_sequence_table_IN_A]
    ldc        r7, 0x3334                   	        // CRC16 init (out)
    //ldaw       r8, dp[ep_datasize_table_IN_A]
    ldc        r9, 0xa001                    	        // CRC16 poly
    ldaw       r10, dp[PidJumpTable]

   // ldw        r1, sp[10]           		              // Disable events on RxA
    //edu        res[r1]
    //setc       res[r1], XS1_SETC_RUN_CLRBUF

    ldap       r11, NextRxWord                        // Restore RXD event vector
    setv       res[RXD], r11
    //eeu        res[RXD]

Loop_BadPid: 
//------------------------------------------------------------------------------------------------

.align 4
.skip 0
ReadToken_PID:

    setc      res[RXD], XS1_SETC_RUN_CLRBUF   // TODO Investigate why this is required 
                                                      // Original USB Token: | CRC | EP | ADDR | PID | junk 
                                         	          // Received token is address and CRC checked.
                                                      // We receive: | 0000 4-bit EP | 0000 4-bit PID | 
    inpw       r11, res[RXD], 8                       // Read 8 bit PID
    shr        r11, r11, 24                           // shift off junk  
    ldw        r11, r10[r11]                 	      // Load relevant branch address

ReadToken_EP:
    nop 
    nop
    inpw      r10, res[RXD], 8   			          // Read EP Number 
    shr       r10, r10, 24          

CheckValidToken:
    ldw       r1, sp[11]                    	      // Valid token port
    in        r1, res[r1]
    bf        r1, InvalidToken                        // If VALID_TOKEN not high, ignore token

BranchOnTokenPid:
    bau         r11                           	     // Branch on PID to relevant token handing code 
                                                     // (e.g. in utmi_out.S etc). When done branchs back to NextToken.

// Un-implemented PID list
Pid_Reserved:           			                 // Should never get here     
nop 
//Pid_Out:          
Pid_Ack: 
nop         
Pid_Data0:           
//Pid_Ping:           
//Pid_Sof:                			                     // Should never get here: (G4) hardware swallows SOF       
Pid_Nyet:           
nop
Pid_Data2:          
nop
Pid_Split:  
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
    bu       	Loop                                 // Invalid token received Ignore unknown toks

Pid_Bad:                                           // Bad PID received, ignore
    //bl           ERR_UnsupportedToken
    bu      Loop

.align 4
Return:
    ldw        r11, sp[8]                       # Free timers
    freer      res[r11]
    ldw        r11, sp[9]
    freer      res[r11]
    ldw        r11, sp[14]
    freer      res[r11]
 
    ldw        r4, sp[1]                        # Register restore
    ldw        r5, sp[2]
    ldw        r6, sp[3]
    ldw        r7, sp[4]
    ldw        r8, sp[5]
    ldw        r9, sp[6]
    ldw        r10, sp[7]
    retsp     STACK_EXTEND
.cc_bottom XUD_LLD_IoLoop.func

// Tables of tables...
#include "utmi_pid_jump_table.S"

