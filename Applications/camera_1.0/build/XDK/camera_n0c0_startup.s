#include <xs1.h>
    .core "XS1"
    .corerev "REVB"
    .globl main,"f{si}(0)"
    .extern exit,"f{0}(si)"
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
programScratch:
    getr    r1, 2
    ldw     r2, dp[ssctrlWordNode0]
    setd    res[r1], r2
    ldc     r6, 3
    shr     r3, r1, 8
    ldc     r11, 192
    outct   res[r1], r11
    shr     r2, r3, 16
    outt    res[r1], r2
    shr     r2, r3, 8
    outt    res[r1], r2
    outt    res[r1], r3
    shr     r2, r6, 8
    outt    res[r1], r2
    outt    res[r1], r6
    ldc     r11, 1
    out     res[r1], r11
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
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
    ldc     r6, 3
    shr     r7, r6, 8
    outt    res[r1], r7
    outt    res[r1], r6
    outct   res[r1], 1
    chkct   res[r1], 3
    in      r9, res[r1]
    chkct   res[r1], 1
    ldc     r2, 4
    eq      r2, r9, r2
    bf      r2, waitForScratchTop
    freer   res[r1]
    .section .dp.data,"awd",@progbits
.align 4
ssctrlWordNode0:
  .word 0x0000c30c
  .text
    ldw     r2, cp[destChanendZero0001]
    ldap    r11, chanEndStore0001
    mov     r3, r11
    ldc     r4, 4
    bl      wakeCore
    ldw     r2, cp[destChanendZero0002]
    ldap    r11, chanEndStore0002
    mov     r3, r11
    ldc     r4, 0
    bl      wakeCore
    ldw     r2, cp[destChanendZero0003]
    ldap    r11, chanEndStore0003
    mov     r3, r11
    ldc     r4, 2
    bl      wakeCore
    ldap    r11, chanEndStore0000
    ldc     r4, 0
    ldc     r0, 0
allocLocalChanends:
    eq      r5, r0, r4
    bt      r5, doneLocalChanendsAlloc
    getr    r1, 2
    stw     r1, r11[0]
dontAllocOne:
    add     r11, r11, 4
    add     r0, r0, 1
    bu      allocLocalChanends
doneLocalChanendsAlloc:
    ldap    r11, chanEndStore0000
    ldw     r1, cp[destChanendZero0000]
spreadRemoteEndsTo0001:
    ldw     r2, cp[destChanendZero0001]
    setd    res[r1], r2
    ldap    r11, chanEndStore0003
    ldw     r0, r11[0]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    ldap    r11, chanEndStore0001
    ldw     r0, r11[3]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    ldap    r11, chanEndStore0003
    ldw     r0, r11[1]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    ldap    r11, chanEndStore0001
    ldw     r0, r11[1]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
spreadRemoteEndsTo0003:
    ldw     r2, cp[destChanendZero0003]
    setd    res[r1], r2
    ldap    r11, chanEndStore0001
    ldw     r0, r11[0]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    ldap    r11, chanEndStore0001
    ldw     r0, r11[2]
    out     res[r1], r0
    outct   res[r1], 1
    chkct   res[r1], 3
    chkct   res[r1], 1
    ldap    r11, chanEndStore0000
    ldc     r5, 0
    ldc     r7, 1
    ldc     r8, 0xff00
    bl      __mcmain
closeDownTop:
    ldw     r0, cp[destChanendZero0000]
    ldw     r1, cp[destChanendZero0001]
    setd    res[r0], r1
    ldc     r2, 0
    outt    res[r0], r2
    outct   res[r0], 1
    int     r2, res[r0]
    chkct   res[r0], 1
    add     r2, r2, 1
    outt    res[r0], r2
    outct   res[r0], 1
    int     r2, res[r0]
    chkct   res[r0], 1
    freer   res[r0]
    ldap    r11, chanEndStore0000
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
wakeCore:
    ldw     r1, cp[destChanendZero0000]
    setd    res[r1], r2
    out     res[r1], r1
    outct   res[r1], 1
    in      r0, res[r1]
    chkct   res[r1], 1
    ldc     r0, 0x0
    out     res[r1], r0
    outct   res[r1], 1
    ldap    r11, chanEndStore0000
    ldc     r5, 0x0
readRemoteChanend:
    eq      r0, r4, r5
    bt      r0, doneRemoteChans
    in      r0, res[r1]
    stw     r0, r3[r5]
    add     r5, r5, 1
    bu      readRemoteChanend
doneRemoteChans:
    chkct   res[r1], 1
    retsp   0
endOfStartup:
    .extern __mcmain.nstackwords
    .extern __mcmain.maxthreads
    .extern __mcmain.maxtimers
    .extern __mcmain.maxchanends
    .call main, __mcmain
    .linkset .LLNK1, __mcmain.nstackwords + 0
    .linkset main.nstackwords, .LLNK1
    .linkset main.maxthreads, __mcmain.maxthreads
    .linkset main.maxtimers, __mcmain.maxtimers
    .linkset .LLNK2, __mcmain.maxchanends
    .linkset main.maxchanends, .LLNK2 + 1
    .globl main.nstackwords
    .globl main.maxthreads
    .globl main.maxtimers
    .globl main.maxchanends
    .section .dp.bss,"awd",@nobits
    .align 4
chanEndStore0000:
chanEndStore0001:
    .space  16
chanEndStore0002:
chanEndStore0003:
    .space  8
syncChanStore:
    .space  16
            .section .cp.const4,"acM",@progbits,4
thisChanendZero:
destChanendZero0000:
    .word   0x00000002
destChanendZero0001:
    .word   0x00010002
destChanendZero0002:
    .word   0x00020002
destChanendZero0003:
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
  .text
  .align 2
