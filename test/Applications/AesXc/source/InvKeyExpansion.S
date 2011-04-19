/*
* The copyrights, all other intellectual and industrial
* property rights are retained by XMOS and/or its licensors.
* Terms and conditions covering the use of this code can
* be found in the Xmos End User License Agreement.
*
* Copyright XMOS Ltd 2008
*
* Author: Suleiman Abu Kharmeh
* Date: 19/November/2008
*/
#ifndef _INVKEYEXPANSION

#define _INVKEYEXPANSION

		  .text

.globl InvKeyExpansion.nstackwords
.linkset InvKeyExpansion.nstackwords, 12
.globl InvKeyExpansion
InvKeyExpansion:
          entsp     7
          stw       r1, sp[0] 
          
          stw       r4, sp[1] 
          stw       r5, sp[2] 
          stw       r6, sp[3] 
          stw       r7, sp[4] 
          stw       r8, sp[5] 
          stw       r9, sp[6] 
          
          
          bl        KeyExpansion 
          
          ldw		r0, sp[0]	//w

          ldaw		r1, dp[M0]
          ldaw		r2, dp[M1]
          ldaw		r3, dp[M2]
          ldaw		r4, dp[M3]

		  ldc		r5, 4          
          
IKELoopCheck:
          ldc       r6, 40
          lss       r6, r5, r6
          bf        r6, IKEFinish
          
IKELoopStart:
          ldw       r6, r0[r5] 
          shl       r8, r6, 24
          shr       r8, r8, 24
          ldw       r7, r1[r8] 
          
          shl       r8, r6, 16
          shr       r8, r8, 24
          ldw       r8, r2[r8] 
          xor       r7, r7, r8
          
          shl       r8, r6, 8
          shr       r8, r8, 24
          ldw       r8, r3[r8] 
          xor       r7, r7, r8
          
          shr       r8, r6, 24
          ldw       r8, r4[r8] 
          xor       r7, r7, r8
          
          stw       r7, r0[r5] 
          add		r5, r5, 1
          
          ldw       r6, r0[r5] 
          shl       r8, r6, 24
          shr       r8, r8, 24
          ldw       r7, r1[r8] 
          
          shl       r8, r6, 16
          shr       r8, r8, 24
          ldw       r8, r2[r8] 
          xor       r7, r7, r8
          
          shl       r8, r6, 8
          shr       r8, r8, 24
          ldw       r8, r3[r8] 
          xor       r7, r7, r8
          
          shr       r8, r6, 24
          ldw       r8, r4[r8] 
          xor       r7, r7, r8
          
          stw       r7, r0[r5] 
          add		r5, r5, 1
          
          ldw       r6, r0[r5] 
          shl       r8, r6, 24
          shr       r8, r8, 24
          ldw       r7, r1[r8] 
          
          shl       r8, r6, 16
          shr       r8, r8, 24
          ldw       r8, r2[r8] 
          xor       r7, r7, r8
          
          shl       r8, r6, 8
          shr       r8, r8, 24
          ldw       r8, r3[r8] 
          xor       r7, r7, r8
          
          shr       r8, r6, 24
          ldw       r8, r4[r8] 
          xor       r7, r7, r8
          
          stw       r7, r0[r5] 
          add		r5, r5, 1
          
          ldw       r6, r0[r5] 
          shl       r8, r6, 24
          shr       r8, r8, 24
          ldw       r7, r1[r8] 
          
          shl       r8, r6, 16
          shr       r8, r8, 24
          ldw       r8, r2[r8] 
          xor       r7, r7, r8
          
          shl       r8, r6, 8
          shr       r8, r8, 24
          ldw       r8, r3[r8] 
          xor       r7, r7, r8
          
          shr       r8, r6, 24
          ldw       r8, r4[r8] 
          xor       r7, r7, r8
          
          stw       r7, r0[r5] 
          add		r5, r5, 1
          
          bu        IKELoopCheck

IKEFinish:
          ldw       r4, sp[1] 
          ldw       r5, sp[2] 
          ldw       r6, sp[3] 
          ldw       r7, sp[4] 
          ldw       r8, sp[5] 
          ldw       r9, sp[6] 
          retsp     7


#endif
