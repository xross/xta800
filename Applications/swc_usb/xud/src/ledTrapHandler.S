/*
 * @ModuleName Trap handler for XDK which sets LED
 * @Author Ali Dixon
 * @Date 07/04/2008
 * @Version 1.0
 * @Description: Low level assembler support.
 *
 *
 * Copyright XMOS Ltd 2008
 */ 

#ifndef _LED_TRAP_HANDLER_S_
#define _LED_TRAP_HANDLER_S_ 1

#include <xs1_user.h>
#include <xs1_kernel.h>
#include <xs1.h>
 

.globl _userTrapHandleRegister
.globl _userTrapHandleRegister
.globl _userTrapHandleRegister.nstackwords
.globl _userTrapHandleRegister.maxtimers
.globl _userTrapHandleRegister.maxchanends
.globl _userTrapHandleRegister.maxthreads
.linkset _userTrapHandleRegister.nstackwords,   0
.linkset _userTrapHandleRegister.maxtimers,     0
.linkset _userTrapHandleRegister.maxchanends,   0
.linkset _userTrapHandleRegister.maxthreads,    1

.globl _userTrapInvoke
.globl _userTrapInvoke
.globl _userTrapInvoke.nstackwords
.globl _userTrapInvoke.maxtimers
.globl _userTrapInvoke.maxchanends
.globl _userTrapInvoke.maxthreads
.linkset _userTrapInvoke.nstackwords,   0
.linkset _userTrapInvoke.maxtimers,     0
.linkset _userTrapInvoke.maxchanends,   0
.linkset _userTrapInvoke.maxthreads,    1

.globl _userTrapHandle
.globl _userTrapHandle
.globl _userTrapHandle.nstackwords
.globl _userTrapHandle.maxtimers
.globl _userTrapHandle.maxchanends
.globl _userTrapHandle.maxthreads
.linkset _userTrapHandle.nstackwords,   0
.linkset _userTrapHandle.maxtimers,     0
.linkset _userTrapHandle.maxchanends,   0
.linkset _userTrapHandle.maxthreads,    1


// Register this trap handler to be called before main.
.section .ctors, "aw", @progbits
.align 4
.int _userTrapHandleRegister

.text

label_trap_led_port: 
.align 4
.word XS1_PORT_1F

/** This register the user trap handler
 */
_userTrapHandleRegister:
   entsp 1
   ldap r11, _userTrapHandle
   set kep, r11
      
   retsp 1

/** This allow test to invoke trap.
 */   
_userTrapInvoke:
   ldc r0, 1
   ecallt r0
   retsp 0
   
/** Simple user trap handler, turn ON led 3 and wait.
 */
.align XS1_KEP_ALIGNMENT
_userTrapHandle:
   //ldap r11, label_trap_led_port
   //ldw r0, r11[0]   
   //ldc r1, 1
   //out res[r0], r1
   clrsr 0x1
   clre
   waiteu 




#endif  //_LED_TRAP_HANDLER_S_

