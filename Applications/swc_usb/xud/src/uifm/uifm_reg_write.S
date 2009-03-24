
#include "XUD_UIFM_Defines.h"

////////////////////////////////////////////////////////////////////////////////////////
// void UifmWriteRead(out port reg_write_port, unsigned regNo, unsigned val);
.extern XUD_UIFM_RegWrite
.globl XUD_UIFM_RegWrite.nstackwords
.linkset XUD_UIFM_RegWrite.nstackwords, 0
.globl XUD_UIFM_RegWrite
  
.text
.align 4

XUD_UIFM_RegWrite:
  ldc       r11, UIFM_CMD_WRITE
  or        r3, r11, r1

  out       res[r0], r3
  out       res[r0], r2

  retsp     0

////////////////////////////////////////////////////////////////////////////////////////
// void UifmWriteReg_Locked(out port reg_write_port, lock l, unsigned regNo, unsigned val);
.extern XUD_UIFM_RegWrite_Locked
.globl XUD_UIFM_RegWrite_Locked.nstackwords
.linkset XUD_UIFM_RegWrite_Locked.nstackwords, 0
.globl XUD_UIFM_RegWrite_Locked
  
.text
.align 4
XUD_UIFM_RegWrite_Locked:

  in        r1, res[r1]

  ldc       r11, UIFM_CMD_WRITE
  or        r2, r11, r2

  out       res[r0], r2
  out       res[r0], r3

  out       res[r1], r1

  retsp     0
