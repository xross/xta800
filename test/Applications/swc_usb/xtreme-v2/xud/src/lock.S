
#include <xs1_user.h>

/////////////////////////////////////////////////////////////////////////
// void XUD_Sup_GetLock()
.extern XUD_Sup_GetLock
.globl XUD_Sup_GetLock.nstackwords
.linkset XUD_Sup_GetLock.nstackwords, 0
.globl XUD_Sup_GetLock
.text 
.cc_top XUD_Sup_GetLock.func, XUD_Sup_GetLock
XUD_Sup_GetLock:
  getr		r0, XS1_RES_TYPE_LOCK
  retsp 	0
.cc_bottom XUD_Sup_GetLock.func
