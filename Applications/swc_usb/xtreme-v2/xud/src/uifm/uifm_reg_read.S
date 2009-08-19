
#include "XUD_UIFM_Defines.h"

/////////////////////////////////////////////////////////////////////////////////////////////
// unsigned UifmReadRead(out port reg_write_port, in port reg_read_port, unsigned regNo);
.extern XUD_UIFM_RegRead
.globl XUD_UIFM_RegRead.nstackwords
.linkset XUD_UIFM_RegRead.nstackwords, 0
.globl XUD_UIFM_RegRead
.text
.align 4
.skip 0

XUD_UIFM_RegRead:
 
  ldc r11, UIFM_CMD_READ         // make register ID
  or r3, r11, r2  
  
  out res[r0], r3
  in r0, res[r1]
  
  retsp 0


/////////////////////////////////////////////////////////////////////////////////////////////////
// unsigned UifmReadRead_Locked(out port reg_write_port, in port reg_read_port, lock l, unsigned regNo);
.extern XUD_UIFM_RegRead_Locked
.globl XUD_UIFM_RegRead_Locked.nstackwords
.linkset XUD_UIFM_RegRead_Locked.nstackwords, 0
.globl XUD_UIFM_RegRead_Locked
.text
.align 4
.skip 0
XUD_UIFM_RegRead_Locked:
  in 	r2, res[r2]		// Claim lock
 
  ldc r11, UIFM_CMD_READ        // Construct UIFM reg ID/Read command 
  or r3, r11, r3  
  
  out res[r0], r3		// Output command
  in r0, res[r1]		// Read back result
  
  out   res[r2], r2		// Free lock

  retsp 0

