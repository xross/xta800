//setAddress.S

#include "XUD_UIFM_Defines.h"

////////////////////////////////////////////////////////////////////////////////////////
// void XUD_SetDeviceAddress(unsigned char a, lock l);
.extern XUD_SetDeviceAddress
.globl XUD_SetDeviceAddress.nstackwords
.linkset XUD_SetDeviceAddress.nstackwords, 1
.globl XUD_SetDeviceAddress
  
.text
.align 4
XUD_SetDeviceAddress:
  entsp   1
  ldc	  r2, UIFM_REG_ADDRESS
  mov     r3, r0
  ldw	  r0, dp[reg_write_port]
  bl      XUD_UIFM_RegWrite_Locked  		// XUD_UIFM_RegWrite_Locked(out port reg_write_port,
				        //                     lock l, 
					//                     unsigned regNo, 
					//                     unsigned val  
  retsp     1

