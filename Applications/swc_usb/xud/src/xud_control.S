
#include <xs1_user.h>


/////////////////////////////////////////////////////////////////////////
// void XUD_ControlHandler()
.extern XUD_ControlHandler
.globl XUD_ControlHandler.nstackwords
.linkset XUD_ControlHandler.nstackwords, 0
.globl XUD_ControlHandler
.text 
XUD_ControlHandler:
  clrsr 	0x2					// Disable thread interrupts
  get		r11, ed					// Get origin of interrupt (resource ID)
  inct          r11, res[r11]
  ldc		r11, 0
  stw		r11, dp[epThreadActive]
  bl printhexln
  kret




/////////////////////////////////////////////////////////////////////////
// void XUD_EnableControl(chanend c)
.extern XUD_EnableControl
.globl XUD_EnableControl.nstackwords
.linkset XUD_EnableControl.nstackwords, 0
.globl XUD_EnableControl
.text 
XUD_EnableControl:
  setsr     0x2						// Enable thread interupts
  ldap      r11, XUD_ControlHandler			// Setup the interrupts handlers (TODO: pass function pointer)
  setv	    res[r0], r11
  setc      res[r0], XS1_SETC_IE_MODE_INTERRUPT
  eeu       res[r0] 					// Enable interrupts on resource
  retsp 	0


