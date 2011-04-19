#include <xs1_user.h>

#define SR_EEBLE_BIT 0x1
#define STACK_EXTEND 16

//int LineSelect(timer t,   uint time, chanend c_con_ep); 
//                 r0          r1           r2

.globl   LineSelect
.globl   LineSelect.nstackwords
.linkset LineSelect.nstackwords, STACK_EXTEND
.text

    .cc_top LineSelect.func, LineSelect
LineSelect:					
   //entsp    STACK_EXTEND
   //stw      r10, sp[1]
   //stw      r9, sp[2]

SetEventVector_Timer: 
   ldap     r11, Select_a    	      
   setv     res[r0], r11           
  
SetEventVector_Chanend:
   ldap     r11, Select_b   			
   setv     res[r2], r11

SetEventCond_Timer: 
   setc     res[r0], XS1_SETC_COND_AFTER	
   setd     res[r0], r1				
   
EnableEvents:
   eeu      res[r0]				
   eeu      res[r2]

ThreadEventEnable:				         
  setsr     	SR_EEBLE_BIT	   
  clrsr				SR_EEBLE_BIT
 
Select_default:			                // Nothing is in list to be serviced so wait for event
  clre
  ldc      	r11, 3
  bu       	Return

Select_a:
  clre
  ldc      	r11, 0
  bu       	Return

Select_b:
  clre
  ldc      	r11, 1
  bu	      	Return

Return:                             
  setc    	res[r0], XS1_SETC_COND_NONE
    
  mov      	r0, r11
    
  //ldw      	r9, sp[2]
  //ldw      	r10, sp[1] 
  retsp    	0
    .cc_bottom LineSelect.func
