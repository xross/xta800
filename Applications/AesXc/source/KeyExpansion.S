#ifndef _KEYEXPANSION

#define _KEYEXPANSION

		  .text
.globl KeyExpansion
.globl KeyExpansion.nstackwords
.linkset KeyExpansion.nstackwords, 5
KeyExpansion:
          extsp     5
		  stw		r4, sp[0]
		  stw		r5, sp[1]
		  stw		r6, sp[2]
		  stw		r7, sp[3]
		  stw		r8, sp[4]
		  

          ldw       r2, r0[0] 
          stw       r2, r1[0] 

          ldw       r2, r0[1] 
          stw       r2, r1[1] 

          ldw       r2, r0[2] 
          stw       r2, r1[2] 

          ldw       r2, r0[3] 
          stw       r2, r1[3] 
          
		  ldc		r0, 1
		  ldaw		r2, dp[sBox]
		  ldaw		r3, dp[rCon]
          
KELoopCheck:
          ldc       r4, 11
          lss       r4, r0, r4
          bf        r4, KEFinish
          
KELoopStart:

		  ldc		r4, 4
		  mul		r4, r4, r0
		  
          sub       r5, r4, 1
          ldw       r5, r1[r5] 
          
          //barrel shift
          shr       r6, r5, 8
          shl       r5, r5, 24
          xor       r5, r5, r6
          
          //sub word
          shl       r6, r5, 24
          shr       r6, r6, 24
          ld8u      r7, r2[r6] 
          
          shl       r6, r5, 16
          shr       r6, r6, 24
          ld8u      r6, r2[r6] 
          shl       r6, r6, 8
          xor       r7, r7, r6
          
          shl       r6, r5, 8
          shr       r6, r6, 24
          ld8u      r6, r2[r6] 
          shl       r6, r6, 16
          xor       r7, r7, r6
          
          shr       r6, r5, 24
          ld8u      r6, r2[r6] 
          shl       r6, r6, 24
          xor       r5, r7, r6
          
          ld8u		r6, r3[r0]
          xor		r5, r5, r6
          
		  //1
          sub       r6, r4, 4
          ldw       r6, r1[r6] 
          xor       r5, r5, r6
          stw       r5, r1[r4] 
          
          //2
          sub       r5, r4, 3
          ldw       r5, r1[r5] 
          ldw		r6, r1[r4]
          xor		r5, r5, r6
          add       r4, r4, 1
          stw       r5, r1[r4] 
          
          //3
          sub       r5, r4, 3
          ldw       r5, r1[r5] 
          ldw		r6, r1[r4]
          xor		r5, r5, r6
          add       r4, r4, 1
          stw       r5, r1[r4] 
          
          //4
          sub       r5, r4, 3
          ldw       r5, r1[r5] 
          ldw		r6, r1[r4]
          xor		r5, r5, r6
          add       r4, r4, 1
          stw       r5, r1[r4] 
          
          add       r0, r0, 1
          bu        KELoopCheck

KEFinish:

		  ldw		r4, sp[0]
		  ldw		r5, sp[1]
		  ldw		r6, sp[2]
		  ldw		r7, sp[3]
		  ldw		r8, sp[4]
		  
		  ldaw      sp, sp[5] 
          retsp     0 

.section .dp.data,"awd",@progbits
.globl rCon
          .align    4
rCon:
          .byte     0
          .byte     1
          .byte     2
          .byte     4
          .byte     8
          .byte     16
          .byte     32
          .byte     64
          .byte     128
          .byte     27
          .byte     54



#endif

