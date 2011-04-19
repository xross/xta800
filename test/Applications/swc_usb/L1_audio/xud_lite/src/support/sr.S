
/////////////////////////////////////////////////////////////////////////
// void XUD_Sup_EnableThreadInterrupts()
.extern XUD_Sup_EnableThreadInterrupts
.globl XUD_Sup_EnableThreadInterrupts.nstackwords
.linkset XUD_Sup_EnableThreadInterrupts.nstackwords, 0
.globl XUD_Sup_EnableThreadInterrupts
.text 
XUD_Sup_EnableThreadInterrupts:
  setsr    	 0x2				// Enable thread interupts
  retsp 	0


/////////////////////////////////////////////////////////////////////////
// void XUD_Sup_DisableThreadInterrupts()
.extern XUD_Sup_DisableThreadInterrupts
.globl XUD_Sup_DisableThreadInterrupts.nstackwords
.linkset XUD_Sup_DisableThreadInterrupts.nstackwords, 0
.globl XUD_Sup_DisableThreadInterrupts
.text 
XUD_Sup_DisableThreadInterrupts:
  clrsr    	 0x2				// Disable thread interupts
  retsp 	0
