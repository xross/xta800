.globl CanSettw, "f{0}(bi:p:32,ui)"
.globl CanSettw.nstackwords
.linkset CanSettw.nstackwords, 0 
.cc_top CanSettw.function,CanSettw
.align    4
CanSettw:
	settw res[r0], r1	
	retsp 0
.cc_bottom CanSettw.function



.globl CanClrpt, "f{0}(bi:p:32)"
.globl CanClrpt.nstackwords
.linkset CanClrpt.nstackwords, 0 
.cc_top CanClrpt.function,CanClrpt
.align    4
CanClrpt:
	clrpt res[r0]	
	retsp 0
.cc_bottom CanClrpt.function



.globl CanGetts, "f{ui}(p)"
.globl CanGetts.nstackwords
.globl CanGetts.maxthreads
.globl CanGetts.maxtimers
.globl CanGetts.maxchanends
.globl CanGetts.maxsync
.type  CanGetts, @function
.linkset CanGetts.locnoside, 1
.linkset CanGetts.locnochandec, 1
.linkset CanGetts.nstackwords, 0
.cc_top CanGetts.function,CanGetts
          .align    4
CanGetts:
					getts r0, res[r0]
          retsp     0x0 
.cc_bottom CanGetts.function
.linkset CanGetts.maxchanends, 0
.linkset CanGetts.maxtimers, 0
.linkset CanGetts.maxthreads, 1



.globl CanOut, "f{}(chd,ui)"
.globl CanOut.nstackwords
.globl CanOut.maxthreads
.globl CanOut.maxtimers
.globl CanOut.maxchanends
.globl CanOut.maxsync
.type  CanOut, @function
.linkset CanOut.locnoside, 1
.linkset CanOut.locnochandec, 1
.linkset CanOut.nstackwords, 0
.cc_top CanOut.function,CanOut
          .align    4
CanOut:
					out res[r0], r1
          retsp     0x0 
.cc_bottom CanOut.function
.linkset CanOut.maxchanends, 0
.linkset CanOut.maxtimers, 0
.linkset CanOut.maxthreads, 1



.globl CanIn, "f{ui}(chd)"
.globl CanIn.nstackwords
.globl CanIn.maxthreads
.globl CanIn.maxtimers
.globl CanIn.maxchanends
.globl CanIn.maxsync
.type  CanIn, @function
.linkset CanIn.locnoside, 1
.linkset CanIn.locnochandec, 1
.linkset CanIn.nstackwords, 0
.cc_top CanIn.function,CanIn
          .align    4
CanIn:
					in r0, res[r0]
          retsp     0x0 
.cc_bottom CanIn.function
.linkset CanIn.maxchanends, 0
.linkset CanIn.maxtimers, 0
.linkset CanIn.maxthreads, 1



