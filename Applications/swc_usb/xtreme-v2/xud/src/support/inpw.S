
// XUD_Sup_Inpw24.S

.extern XUD_Sup_Inpw24
.globl XUD_Sup_Inpw24.nstackwords
.globl XUD_Sup_Inpw24.maxthreads
.globl XUD_Sup_Inpw24.maxtimers
.globl XUD_Sup_Inpw24.maxchanends
 
.linkset XUD_Sup_Inpw24.nstackwords, 0
.linkset XUD_Sup_Inpw24.maxchanends, 0
.linkset XUD_Sup_Inpw24.maxtimers, 0
.linkset XUD_Sup_Inpw24.maxthreads, 0
 
.globl XUD_Sup_Inpw24
 
.text

//unsigned XUD_Sup_Inpw24(in port p) 
XUD_Sup_Inpw24:
   inpw      r0, res[r0], 24  
   retsp     0

.extern XUD_Sup_Inpw8
.globl XUD_Sup_Inpw8.nstackwords
.globl XUD_Sup_Inpw8.maxthreads
.globl XUD_Sup_Inpw8.maxtimers
.globl XUD_Sup_Inpw8.maxchanends
 
.linkset XUD_Sup_Inpw8.nstackwords, 0
.linkset XUD_Sup_Inpw8.maxchanends, 0
.linkset XUD_Sup_Inpw8.maxtimers, 0
.linkset XUD_Sup_Inpw8.maxthreads, 0
 
.globl XUD_Sup_Inpw8
 
.text

//unsigned XUD_Sup_Inpw24(in port p) 
XUD_Sup_Inpw8:
   inpw      r0, res[r0], 8  
   retsp     0


.extern  XUD_Sup_inuint
.globl   XUD_Sup_inuint.nstackwords
.linkset XUD_Sup_inuint.nstackwords, 0
.globl   XUD_Sup_inuint
.text
//unsigned XUD_Sup_inuint(XUD_chan c) 
XUD_Sup_inuint:
  in	  r0, res[r0] 
  retsp 0

.extern  XUD_Sup_inct
.globl   XUD_Sup_inct.nstackwords
.linkset XUD_Sup_inct.nstackwords, 0
.globl   XUD_Sup_inct
.text
//unsigned XUD_Sup_inuint(XUD_chan c) 
XUD_Sup_inct:
  inct	  r0, res[r0] 
  retsp 0
  
  
 .extern  XUD_Sup_testct
.globl   XUD_Sup_testct.nstackwords
.linkset XUD_Sup_testct.nstackwords, 0
.globl   XUD_Sup_testct
.text
//unsigned XUD_Sup_test(XUD_chan c) 
XUD_Sup_testct:
  testct	  r0, res[r0] 
  retsp 0
