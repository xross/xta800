    .core "XS1"
    .corerev "REVB"
    .globl main,"f{si}(0)"
    .text
    .align 2
main:
    entsp   2
getAllChannels:
    getr    r0, 2
    bt      r0, getAllChannels
    ldc     r2, 31
    ldc     r3, 1
    ldc     r1, 0x100
    ldw     r0, cp[thisChanendZero]
    add     r0, r0, r1
freeAllChannels:
    freer   res[r0]
skipFree:
    add     r0, r0, r1
    eq      r4, r3, r2
    add     r3, r3, 1
    bf      r4, freeAllChannels
    ldc     r2, 2
    getr    r1, 2
    bl      waitForScratch
    ldw     r0, cp[thisChanendZero]
receiveEnd:
getByte1:
    int     r1, res[r0]
    shl     r1, r1, 8
getByte2:
    int     r2, res[r0]
    or      r1, r1, r2
    shl     r1, r1, 8
getByte3:
    int     r2, res[r0]
    or      r1, r1, r2
    shl     r1, r1, 8
getByte4:
    int     r2, res[r0]
    or      r1, r1, r2
    chkct   res[r0], 1
    setd    res[r0], r1
    out     res[r0], r0
    outct   res[r0], 1
waitForZero:
    in      r1, res[r0]
    chkct   res[r0], 1
    bt      r1, waitForZero
noChans:
    outct   res[r0], 1
    bl      __mcmain
    bu      syncAndReturn
syncAndReturn:
    ldw     r0, cp[thisChanendZero]
    ldw     r1, cp[nextChanendZero]
    setd    res[r0], r1
    ldc     r2, 0
waitForSyncToken:
    int     r1, res[r0]
    chkct   res[r0], 1
    add     r1, r1, 1
    outt    res[r0], r1
    outct   res[r0], 1
    mov     r3, r2
    ldc     r2, 1
    bf      r3, waitForSyncToken
    freer   res[r0]
    ldap    r11, chanEndStore
    ldc     r4, 0
    ldc     r0, 0
    ldc     r3, 1
freeLocalChanendSend:
    eq      r5, r0, r4
    bt      r5, doneLocalChanendCloseSendDone
    ldw     r1, r11[r0]
    and     r2, r1, r3
    bt      r2, noFinalSend
    andnot  r1, r3
    outct   res[r1], 3
    outct   res[r1], 1
noFinalSend:
    add     r0, r0, 1
    bu      freeLocalChanendSend
doneLocalChanendCloseSendDone:
    ldc     r0, 0
freeLocalChanendRec:
    eq      r5, r0, r4
    bt      r5, doneLocalChanendCloseRecDone
    ldw     r1, r11[r0]
    and     r2, r1, r3
    andnot  r1, r3
    bt      r2, noFinalRec
    chkct   res[r1], 3
    chkct   res[r1], 1
noFinalRec:
    freer   res[r1]
    add     r0, r0, 1
    bu      freeLocalChanendRec
doneLocalChanendCloseRecDone:
    ldc     r0, 0
    retsp   2

waitForScratch:
    ldc     r6, 3
    ldw     r7, dp[ssctrlWordNode0]
    setd    res[r1], r7
    shr     r3, r1, 8
    ldc     r11, 193
waitForScratchTop:
    outct   res[r1], r11
    shr     r7, r3, 16
    outt    res[r1], r7
    shr     r7, r3, 8
    outt    res[r1], r7
    outt    res[r1], r3
    shr     r7, r6, 8
    outt    res[r1], r7
    outt    res[r1], r6
    outct   res[r1], 1
    chkct   res[r1], 3
    in      r9, res[r1]
    chkct   res[r1], 1
    eq      r10, r9, r2
    bf      r10, waitForScratchTop
writeToScratch:
    shr     r3, r1, 8
    ldc     r11, 192
    outct   res[r1], r11
    shr     r4, r3, 16
    outt    res[r1], r4
    shr     r4, r3, 8
    outt    res[r1], r4
    outt    res[r1], r3
    shr     r4, r6, 8
    outt    res[r1], r4
    outt    res[r1], r6
    add     r2, r2, 1
    out     res[r1], r2
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    freer   res[r1]
    retsp   0
    .section .dp.data,"awd",@progbits
.align 4
ssctrlWordNode0:
  .word 0x0000c30c
  .text
endOfStartup:
    .section .dp.bss,"awd",@nobits
    .align 4
chanEndStore:
    .space  0
    .section .cp.const4,"acM",@progbits,4
    .align 4
thisChanendZero:
    .word   0x00020002
nextChanendZero:
    .word   0x00030002
    .section .dp.data,       "adw", @progbits
  .globl p_lcd_dtm, "oa:p"
p_lcd_dtm:
  .globl p_lcd_clk, "oa:p"
p_lcd_clk:
  .globl p_lcd_rgb, "boa:p:32"
p_lcd_rgb:
  .globl clk_lcd, "a:ck"
clk_lcd:
  .globl p_cam_rst, "oa:p"
p_cam_rst:
  .globl HREF_port, "ia:p"
HREF_port:
  .globl VSYNC_port, "ia:p"
VSYNC_port:
  .globl PCLK_port, "ia:p"
PCLK_port:
  .globl EXCLK_port, "oa:p"
EXCLK_port:
  .globl Y_UV_port, "ia:p"
Y_UV_port:
  .globl p_iic_sda, "a:p"
p_iic_sda:
  .globl p_iic_scl, "a:p"
p_iic_scl:
  .globl clk, "a:ck"
clk:
  .globl clk2, "a:ck"
clk2:

    .extern __mcmain.nstackwords
    .extern __mcmain.maxthreads
    .extern __mcmain.maxtimers
    .extern __mcmain.maxchanends
    .call main, __mcmain
    .linkset .LLNK1, __mcmain.nstackwords + 2
    .linkset main.nstackwords, .LLNK1
    .linkset main.maxthreads, __mcmain.maxthreads
    .linkset main.maxtimers, __mcmain.maxtimers
    .linkset .LLNK2, __mcmain.maxchanends
    .linkset main.maxchanends, .LLNK2 + 1
    .globl main.nstackwords
    .globl main.maxthreads
    .globl main.maxtimers
    .globl main.maxchanends

  .text
  .align 2
