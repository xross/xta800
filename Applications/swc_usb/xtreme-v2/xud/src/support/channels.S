.extern XUD_Sup_getd
.globl XUD_Sup_getd.nstackwords
.globl XUD_Sup_getd.maxthreads
.globl XUD_Sup_getd.maxtimers
.globl XUD_Sup_getd.maxchanends

.linkset XUD_Sup_getd.nstackwords, 0
.linkset XUD_Sup_getd.maxchanends, 0
.linkset XUD_Sup_getd.maxtimers, 0
.linkset XUD_Sup_getd.maxthreads, 0
 
.globl XUD_Sup_getd
  
.text

XUD_Sup_getd:
  getd     	r0, res[r0]
  retsp  		0
