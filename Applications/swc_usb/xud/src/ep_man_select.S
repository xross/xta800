#include <xs1_user.h>


#define SR_EEBLE_BIT 0x1
#define STACK_EXTEND (3)

//int EpManSelect(chanend c_con_ep, chanend dataChans[], int epWaiting, int epCount); 
//                          r0                r1                 r2              r3  

.extern EpManSelect
.globl EpManSelect.nstackwords
.globl EpManSelect.maxthreads
.globl EpManSelect.maxtimers
.globl EpManSelect.maxchanends

.linkset EpManSelect.nstackwords, 32
.linkset EpManSelect.maxchanends, 0
.linkset EpManSelect.maxtimers, 0
.linkset EpManSelect.maxthreads, 0

.globl EpManSelect
 
.text

// TODO: Do we want any priority?
EpManSelect:
   entsp    STACK_EXTEND
   stw      r10, sp[1]
   stw      r9, sp[2]

   ldap r11, select_a
   setv res[r0], r11
   eeu res[r0]
	
   ldc     r10, 3
   ldap     r11, select_endpoint
	
EnableLoop:
   bf       r10, EnableLoopEnd  	// Enable events on all channels in array
   sub      r10, r10, 1
   ldw      r9, r1[r10]
   setv     res[r9], r11
   eeu      res[r9]
   
   bu       EnableLoop

EnableLoopEnd:
   bf       r2, NothingWaiting		// Nothing waiting to be serviced

EndpointsWaiting: 		      	  // We have stuff that wants to be serviced, check if anything else waiting
  setsr     SR_EEBLE_BIT	   	  // This allows use to try and service the waiting endpoints once again
  clrsr	    SR_EEBLE_BIT
  bu        select_default
 
NothingWaiting:			      	  // Nothing is in list to be serviced so wait for event
   waiteu

select_endpoint:
   get      r11, ed		      	// Get source of event
   bu 	    Return

select_default:
  ldc       r11, 3            		// Note: Dont use 0x02 since its a valid chanend ID!
 bu Return

select_a:
  ldc 	    r11, 0
	
	
Return:

DisableLoop:				// Disable events
   clre
   
   mov      r0, r11
    
   ldw      r9, sp[2]
   ldw      r10, sp[1] 
   retsp    STACK_EXTEND



// void EpManDataSend(chanend c, unsigned address, unsigned datalength)
.globl   EpManDataSend
.globl   EpManDataSend.nstackwords
.linkset EpManDataSend.nstackwords, 0
.text

//for (i = 0; i < (datalength >> 2); i++)
//{
//  tmp = getUint(address, i);
//  XUD_Sup_outuint(xc, tmp);
//}

EpManDataSend:
   ldc r3, 0
   shr      r2, r2, 0x2
          ldc       r11, 0x0
          lss       r11, r11, r2
          bt        r11, .L3
.L0:
          retsp     0x0
.L3:
          ldw       r11, r1[r3]
          out       res[r0], r11
          add       r3, r3, 0x1
          lss       r11, r3, r2
          bt        r11, .L3
          bu        .L0

