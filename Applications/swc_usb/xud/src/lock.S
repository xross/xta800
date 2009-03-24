
#include <xs1_user.h>

/////////////////////////////////////////////////////////////////////////
// void XUD_Sup_GetLock()
.extern XUD_Sup_GetLock
.globl XUD_Sup_GetLock.nstackwords
.linkset XUD_Sup_GetLock.nstackwords, 0
.globl XUD_Sup_GetLock
.text 
XUD_Sup_GetLock:
  getr		r0, XS1_RES_TYPE_LOCK
  retsp 	0
