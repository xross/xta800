.globl c, "ck"
.type  c, @object
          .section .dp.data,       "adw", @progbits
.cc_top c.data, c
          .align    4
c:
          .int      0x00000106
.cc_bottom c.data



          .align    4
          .section .globcode,      "ax",  @progbits
          .align    2
s.LC0:
          ldw       r0, dp[c] 
          setc      res[r0], 0x8
          retsp     0x0 
          .section .ctors,         "aw",  @progbits
          .align    4
          .int      s.LC0


          .section .globcode,      "ax",  @progbits
s.CLOCK:
          ldw       r0, dp[c] 
          setc      res[r0], 0x0
          retsp     0x0 
          .section .dtors,         "aw",  @progbits
          .align    4
          .int      s.CLOCK


.call CanPhyRx, printstrln
          .section .cp.string,     "ac", @progbits
          .align    4
s.CRC_DEL_ERROR:
          .ascii    "CRC DEL ERROR\0"


.call CanPhyRx, printstrln
          .align    4
s.EOF_DEL_ERROR:
          .ascii    "EOF DEL ERROR\0"

.call CanPhyRx, printstrln
          .align    4
s.TIMING_ERROR:
          .ascii    "TIMING ERROR\0"


.call CanPhyRx, printnum
          .align    4
s.RX_PHY_FINISHED:
          .ascii    "RX PHY finished %d frames.\n\0"

.call CanPhyRx, printstr
          .align    4
s.CRC_DEL:
          .ascii    "CRC DEL:\0"
.call CanPhyRx, printhexln
.call CanPhyRx, printstr
          .align    4
s.TMP_TIME:
          .ascii    "tmpTime:\0"
.call CanPhyRx, printhexln
.call CanPhyRx, printstr
          .align    4
s.BIT_FINISH_TIME:
          .ascii    "bitFinishTime:\0"
.call CanPhyRx, printhexln
.call CanPhyRx, printstr
          .align    4
s.ACK_TIME:
          .ascii    "ackTime:\0"
.call CanPhyRx, printhexln
.call CanPhyRx, printstr
          .align    4
s.CURRENT_TIME:
          .ascii    "currentTime:\0"
.call CanPhyRx, printhexln

          .text
          .align    2
.globl CanPhyRx, "f{0}(chd,bi:p:32,p)"
.globl CanPhyRx.nstackwords
.type  CanPhyRx, @function
.linkset .LLNK4, configure_clock_ref.nstackwords $M printnum.nstackwords
.linkset .LLNK3, .LLNK4 $M printstrln.nstackwords
.linkset .LLNK2, .LLNK3 $M printstrln.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 12
.linkset CanPhyRx.nstackwords, .LLNK0
.cc_top CanPhyRx.function,CanPhyRx
          .align    4
CanPhyRx:
          entsp     0xc 
          stw       r4, sp[0x3] 
          stw       r5, sp[0x4] 
          stw       r6, sp[0x5] 
          stw       r7, sp[0x6] 
          stw       r8, sp[0x7] 
          stw       r9, sp[0x8] 
          stw       r10, sp[0x9] 
          stw       r0, sp[0x1] 
          mov       r6, r1
          stw       r2, sp[0x2] 
          ldc       r0, 0x0
          stw       r0, sp[0xb] 
          ldw       r0, sp[0xb] 
          stw       r0, sp[0xa]


					//stop clock
          ldw       r0, dp[c] 
          setc      res[r0], 0x7
					//configure clock
          ldc       r1, 0x2
          bl        configure_clock_ref 
					//assign clock to port RX
          ldw       r0, dp[c] 
          setclk    res[r6], r0
					//assign clock to port TX
          ldw       r1, sp[0x2] 
          setclk    res[r1], r0
					//start clock
          setc      res[r0], 0xf
			
					//set data 0 for rx
          ldw       r0, sp[0xb] 
          setd      res[r6], r0

          ldw       r11, cp[s.STRESS_LIMIT]
          .section .cp.const4,     "acM", @progbits, 4
s.STRESS_LIMIT:
          .align    4
          .int      0x0003e8	//counterLimit
          .text

//start of main loop
.L3:
          clre

          ldw       r0, sp[0xa] 
          add       r0, r0, 0x1
          stw       r0, sp[0xa] 
//input start of frame
          setc      res[r6], 0x11
          in        r4, res[r6] 
          getts     r8, res[r6] 
          ldc       r0, 0x21
          add       r8, r8, r0


          ldc       r5, 0x19//QUANTA_TOTAL

//set up the handle for the data channel
          ldap      r11, .L29
          ldw       r0, sp[0x1] 
          setv      res[r0], r11
//set up the handle for the rx port					
          ldap      r11, .L44
          setv      res[r6], r11
					

//start of while(!done)	loop

//enable events and wait for an event
.L10:
          ldw       r0, sp[0x1] 
          eeu       res[r0]//enable the channel event
	        setpt     res[r6], r8
          eeu       res[r6]//enable the port event after setting the pause time
.xtabranch .L29, .L44
          waiteu    



//handle the channel input
//do we need to disable events?
.L29:
commandReceived:
          ldw       r0, sp[0x1] 
          in        r0, res[r0] //no control tockens
          eq        r0, r0, 0x4
          bt        r0, .L33 //ACK?


.L32:
					//after sending the ack, might need to check for the end of frame
					//for now will just go to get the next frame
          ldw       r0, sp[0xa] 
          ldw       r11, cp[s.STRESS_LIMIT]
          lsu       r0, r0, r11
          bt        r0, .L3 //chech frameCounter loop
.LFINISH:
          ldaw      r11, cp[s.RX_PHY_FINISHED] 
          mov       r0, r11
          ldw       r1, sp[0xa] 
          ldc       r2, 0x1c
          bl        printnum 
          ldw       r4, sp[0x3] 
          ldw       r5, sp[0x4] 
          ldw       r6, sp[0x5] 
          ldw       r7, sp[0x6] 
          ldw       r8, sp[0x7] 
          ldw       r9, sp[0x8] 
          ldw       r10, sp[0x9] 
//end of CanPhyRx call
					retsp     0xc 


//send the ACK
.L33:

#define   tmpValue  		r4
#define 	QUANTA_TOTAL 	r5
#define		CanRX 				r6
#define  	tmp						r7
#define 	tmpTime   		r8
#define  	currentTime 	r9 
#define		i							r10

#define 	CanTX 		sp[2]


          ldw       r1, sp[0x2] 
					getts 		currentTime, res[r1]
					sub       tmpTime, tmpTime, 0x9//QUANTA_PROP + QUANTA_SYNC
					setpt 		res[r1], tmpTime
          ldc       r0, 0x0
          out       res[r1], r0

					//assert(currentTime + 5< tmpTime)
					add				r0, currentTime, 5 //allowing 20 instructions as a safeguard
          lsu       r0, r0, tmpTime
          bt        r0, .CONTINUE
					
	        ldaw      r11, cp[s.TIMING_ERROR] 
          mov       r0, r11
          ldc       r1, 0xd
          bl        printstr 
	
					bu .LFINISH
				


.CONTINUE:					

					//now after setting the output, will do the input
					clrpt 		res[CanRX]
					setpt			res[CanRX], tmpTime
					in				tmpValue, res[CanRX] //this is when the ack is outputted

					//tmpValue should have the CRC_DEL (8 samples ago)

					add 			tmpTime, tmpTime, QUANTA_TOTAL
					setpt 		res[r1], tmpTime
					ldc 			r0, 0x1
					out				res[r1], r0
					add				r0, tmpTime, 2	//just drive high for 2 time quantas, and then leave the bus					
					setpt			res[r1], r0
					ldc 			r0, 0x1
					setc 			res[r1], r0		//XS1_SETC_VALUE_COND_AFTER 
					in 				r0, res[r1]		//release the bus

					//tmpTime now at the end of the ACK_BIT

					//then clock ACK_EL and EOF, no stuffing, no synchronisation(timing calculations)
					//then go for the next packet

					//now checking old inputed CRC_DEL

					ldc				tmp, 1

          shl       r1, tmpValue, 0x8
          ldc       r0, 0x1f
          shr       r0, r1, r0
          eq        r0, r0, tmp
          bf        r0, .L35 

.L34:
					//r10 = loop counter	
					//here will check for both ACK_DEL and EOF in the same loop
          ldc       i, 0
.L39:
          add       tmpTime, tmpTime, QUANTA_TOTAL 
          setpt     res[CanRX], tmpTime
          in        tmpValue, res[CanRX] 
          shl       r1, tmpValue, 0x8
          ldc       r0, 0x1f
          shr       r0, r1, r0
          eq        r0, r0, tmp
          bf        r0, .L43 
.L42:
          add       i, i, 0x1
          ldc       r0, 0x8			//7 EOF and 1 ACK_DEL
          lsu       r0, i, r0
          bt        r0, .L39 
          bu        .L32

.L35:
          ldaw      r11, cp[s.CRC_DEL_ERROR] 
          mov       r0, r11
          ldc       r1, 0xe
          bl        printstrln
          bu        .LFINISH			
.L43:
          ldaw      r11, cp[s.EOF_DEL_ERROR] 
          mov       r0, r11
          ldc       r1, 0xe
          bl        printstrln 
          bu        .LFINISH 

//these were used for debugging, not used anymore...
/*
          ldaw      r11, cp[s.CRC_DEL] 
          mov       r0, r11
          ldc       r1, 0x9
          bl        printstr 

          mov       r0, r4
          bl        printhexln 

          ldaw      r11, cp[s.BIT_FINISH_TIME] 
          mov       r0, r11
          ldc       r1, 0xf
          bl        printstr 

          mov       r0, r9
          bl        printhexln 


          ldaw      r11, cp[s.TMP_TIME] 
          mov       r0, r11
          ldc       r1, 0x9
          bl        printstr 

          mov       r0, r8
          bl        printhexln 

          ldaw      r11, cp[s.ACK_TIME] 
          mov       r0, r11
          ldc       r1, 0x9
          bl        printstr 

          mov       r0, ackTime
          bl        printhexln 


          ldaw      r11, cp[s.CURRENT_TIME] 
          mov       r0, r11
          ldc       r1, 0xd
          bl        printstr 

          mov       r0, currentTime
          bl        printhexln 
					
*/




//handle the rx port input					
.L44:
dataReceiveLoop:
          in        r4, res[r6] 
          ldc       r0, 0x11
          shl       r2, r4, r0
          ldc       r0, 0x1f
          shr       r2, r2, r0
          ldw       r0, sp[0x1] 
          out       res[r0], r2//no control tokens 


//update the time here
.L8:
//update the next input time
          clz       r0, r4
          eq        r2, r0, 0x8
          bt        r2, .L11 
          ldc       r3, 0x12
          lsu       r2, r0, r3
          bf        r2, .L11 
          bf        r0, .L11 
          ldc       r2, 0x8
          lsu       r2, r0, r2
          bt        r2, .L16 
          lsu       r2, r0, r3
          bt        r2, .L22


//zeros == 8, zeros >= 18, zeros	== 0				
//just add a QUANTA_TOTAL to the time
.L11:
          add       r8, r8, r5
          bu        ._L10 
//zeros < 8, 
//edge is late, in the propagation delay segment
//need to extend the bit time
.L16:
          ldc       r2, 0x8
          sub       r2, r2, r0
          ldc       r0, 0x4
          lsu       r0, r2, r0
          bt        r0, .L19 
          ldc       r0, 0x1d
          add       r8, r8, r0
          bu        ._L10 
.L19:
          add       r0, r5, r2
          add       r8, r8, r0
          bu        ._L10 

//zeros < 18
//edge is early, in the phase buffer 2 segment
//need to shorten the bit time
.L22:
          sub       r0, r0, 0x8
          sub       r2, r0, 0x1
          ldc       r0, 0x4
          lsu       r0, r2, r0
          bt        r0, .L24 
          ldc       r0, 0x1d
          add       r8, r8, r0
          bu        ._L10 
.L24:
          sub       r0, r5, r2
          add       r8, r8, r0
          bu        ._L10 

//after updating the times, set up the port and input again
._L10:			
	        setpt     res[r6], r8
          eeu       res[r6]//enable the port event after setting the pause time
.xtabranch .L29, .L44
          waiteu    



.L26:
.cc_bottom CanPhyRx.function
.linkset .LLNK7, configure_clock_ref.maxchanends $M printnum.maxchanends
.linkset .LLNK6, .LLNK7 $M printstrln.maxchanends
.linkset .LLNK5, .LLNK6 $M printstrln.maxchanends
.linkset CanPhyRx.maxchanends, .LLNK5
.linkset .LLNK10, configure_clock_ref.maxtimers $M printnum.maxtimers
.linkset .LLNK9, .LLNK10 $M printstrln.maxtimers
.linkset .LLNK8, .LLNK9 $M printstrln.maxtimers
.linkset CanPhyRx.maxtimers, .LLNK8
.linkset .LLNK16, configure_clock_ref.maxthreads - 1
.linkset .LLNK15, 1 + .LLNK16
.linkset .LLNK14, 1 $M .LLNK15
.linkset .LLNK18, printnum.maxthreads - 1
.linkset .LLNK17, 1 + .LLNK18
.linkset .LLNK13, .LLNK14 $M .LLNK17
.linkset .LLNK20, printstrln.maxthreads - 1
.linkset .LLNK19, 1 + .LLNK20
.linkset .LLNK12, .LLNK13 $M .LLNK19
.linkset .LLNK22, printstrln.maxthreads - 1
.linkset .LLNK21, 1 + .LLNK22
.linkset .LLNK11, .LLNK12 $M .LLNK21
.linkset CanPhyRx.maxthreads, .LLNK11
.extern stdcore, "a(*:cr)"
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler 8.11.1 (build 1195)"
          .core     "XS1"
          .corerev  "REVB"
