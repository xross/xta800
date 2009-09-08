

.extern XUD_Sup_Outpw8
.globl XUD_Sup_Outpw8.nstackwords
.globl XUD_Sup_Outpw8.maxthreads
.globl XUD_Sup_Outpw8.maxtimers
.globl XUD_Sup_Outpw8.maxchanends

.linkset XUD_Sup_Outpw8.nstackwords, 0
.linkset XUD_Sup_Outpw8.maxchanends, 0
.linkset XUD_Sup_Outpw8.maxtimers, 0
.linkset XUD_Sup_Outpw8.maxthreads, 0
 
.globl XUD_Sup_Outpw8
  
.text

XUD_Sup_Outpw8:
  outpw     res[r0], r1, 8
  retsp  0


.extern XUD_Sup_Outpw16
.globl XUD_Sup_Outpw16.nstackwords
.globl XUD_Sup_Outpw16.maxthreads
.globl XUD_Sup_Outpw16.maxtimers
.globl XUD_Sup_Outpw16.maxchanends

.linkset XUD_Sup_Outpw16.nstackwords, 0
.linkset XUD_Sup_Outpw16.maxchanends, 0
.linkset XUD_Sup_Outpw16.maxtimers, 0
.linkset XUD_Sup_Outpw16.maxthreads, 0
 
.globl XUD_Sup_Outpw16
  
.text

XUD_Sup_Outpw16:
  outpw  	res[r0], r1, 16
  retsp 0


.extern XUD_Sup_Outpw24
.globl XUD_Sup_Outpw24.nstackwords
.globl XUD_Sup_Outpw24.maxthreads
.globl XUD_Sup_Outpw24.maxtimers
.globl XUD_Sup_Outpw24.maxchanends

.linkset XUD_Sup_Outpw24.nstackwords, 0
.linkset XUD_Sup_Outpw24.maxchanends, 0
.linkset XUD_Sup_Outpw24.maxtimers, 0
.linkset XUD_Sup_Outpw24.maxthreads, 0
 
.globl XUD_Sup_Outpw24
  
.text

XUD_Sup_Outpw24:
  outpw  	res[r0], r1, 24
  retsp 0


.extern XUD_Sup_GetResourceId
.globl XUD_Sup_GetResourceId.nstackwords

.linkset XUD_Sup_GetResourceId.nstackwords, 0
.text

.global XUD_Sup_GetResourceId
XUD_Sup_GetResourceId:
  retsp       0
           


//void XUD_Sup_outuint(XUD_chan, unsigned x)
.extern  XUD_Sup_outuint
.globl   XUD_Sup_outuint.nstackwords
.linkset XUD_Sup_outuint.nstackwords, 0
.globl   XUD_Sup_outuint
.text
//unsigned XUD_Sup_inuint(XUD_chan c) 
XUD_Sup_outuint:
  out	  res[r0], r1
  retsp   0


//void XUD_Sup_outct(XUD_chan, unsigned char x)
.extern  XUD_Sup_outct
.globl   XUD_Sup_outct.nstackwords
.linkset XUD_Sup_outct.nstackwords, 0
.globl   XUD_Sup_outct
.text
XUD_Sup_outct:
  outct	  res[r0], r1
  retsp 0

//void XUD_Sup_stop_streaming_master(XUD_chan)
.extern  XUD_Sup_stop_streaming_master
.globl   XUD_Sup_stop_streaming_master.nstackwords
.linkset XUD_Sup_stop_streaming_master.nstackwords, 0
.globl   XUD_Sup_stop_streaming_master
.text
XUD_Sup_stop_streaming_master:
  chkct	  res[r0], 1
  outct   res[r0], 1
  retsp 0




