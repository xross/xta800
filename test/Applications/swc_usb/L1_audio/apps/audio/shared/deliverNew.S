/*  
 * this code needs to be preprocessed by m4 before the .S file is fed to the compiler
 */

/*

NOTE: first 2 params are not used 

deliverNew(
        buffered out port:32 ADAT, 
        buffered out port:32 LRCLK, 
        buffered out port:32 BCLK,
        buffered out port:32 ,
        chanend ADATin, chanend SPDIFin, chanend I2SInOut,
        DOUT1, DOUT2, DOUT3, DOUT4, 
        DIN1, DIN2, DIN3, DIN4,
        chanend sampleChannel, chanend c_spdif);
*/

#define FRAME 24

// ADAT  out port  0
// LRCLK out port  1
// BCLK  out port  2 
//  out port  3

#define ADATInChannel (FRAME + 2)
#define SPDIFInChannel (FRAME + 3)
#define I2SInChannel (FRAME + 4)
#define I2SOutChannel (FRAME + 5)
#define DOUT0 (FRAME + 6)
#define DOUT1 (FRAME + 7)
#define DOUT2 (FRAME + 8)
#define DOUT3 (FRAME + 9)
#define DIN0 (FRAME + 10)
#define DIN1 (FRAME + 11)
#define DIN2 (FRAME + 12)
#define DIN3 (FRAME + 13)
#define branchDistance (FRAME + 14)

#define sampleOut0 7
#define sampleOut1 8
#define sampleOut2 9
#define sampleOut3 10
#define sampleOut4 11
#define sampleOut5 12
#define sampleOut6 13
#define sampleOut7 14
#define sampleIn0 15
#define sampleIn1 16
#define sampleIn2 17
#define sampleIn3 18
#define sampleIn4 19
#define sampleIn5 20
#define sampleIn6 21
#define sampleIn7 22
    
.globl deliverNew
.globl deliverNew.nstackwords
.globl deliverNew.maxtimers
.globl deliverNew.maxchanends
.globl deliverNew.maxthreads
.linkset deliverNew.maxthreads, 0
.linkset deliverNew.nstackwords, FRAME + 4
.linkset deliverNew.maxtimers, 0
.linkset deliverNew.maxchanends, 0

    .cc_top deliverNew.func, deliverNew
    .align 4
deliverNew:    
    entsp FRAME + 1

RegSave:
    stw   r4, sp[0]
    stw   r5, sp[1]
    stw   r6, sp[2]
    stw   r7, sp[3]
    stw   r8, sp[4]
    stw   r9, sp[5]
    stw  r10, sp[6]

    ldw r4, sp[ADATInChannel]
    ldw r5, sp[SPDIFInChannel]
    ldw r9, sp[I2SInChannel]
    ldw r10, sp[I2SOutChannel]
    
deliverNew_:   

    /* Now preload the four output  registers */
    
    ldc r7, 0
    ldw r11, sp[DOUT0]
    out res[r11], r7
    ldw r11, sp[DOUT1]
    out res[r11], r7
    ldw r11, sp[DOUT2]
    out res[r11], r7
    ldw r11, sp[DOUT3]
    out res[r11], r7

    /* Compute the mask 0x99999999 (eight clocks on the BCLK),
     * This masks is always stored in r6. r1 and r2 always hold the ports for
     * LRCLK and BCLK. A sequence of outs on r1 and r2 with r6, and 0 or 1 will create a block
     * of clocks. An LRCLK with 0 on the BLCK creates a pause. Both will take 32 master
     * clocks, or approximately 640 ns.
     */
    ldc r6,0x9999
    shl r11, r6, 16
    add r6, r11, r6


    getts r11, res[r2]
    ldc   r7, 10000
    add   r11, r11, r7
//    setpt res[r1], r11
    setpt res[r2], r11

                           // first quadrant left
    mkmsk r7, 32
    out res[r1], r7
    out res[r2], r6
                           // second quadrant left
    out res[r2], r6
                           // third quadrant left
    out res[r2], r6
                           // fourth quadrant left
    out res[r2], r6

    ldc r7, 0
    out res[r1], r7   // Set LRCLK ready

    /* Preload the OUT  regsiters with anohter set of zeros, they are going to be shifted
       out in the loop */
    
    ldc r7, 0
    ldw r11, sp[DOUT0]
    out res[r11], r7
    ldw r11, sp[DOUT1]
    out res[r11], r7
    ldw r11, sp[DOUT2]
    out res[r11], r7
    ldw r11, sp[DOUT3]
    out res[r11], r7
    
// MAIN LOOP

// r2/r6: BLCK
// r1/r7: LRCLK
// r4/r5: ADAT/ IN Channel
// r0/r3: ADAT/ OUT Port
// r10: Channel to write I2S data
// r8/r11: scratch
// r9: Channel to read I2S data




    





    
Loop:
    
right1: 
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11


    mkmsk r7, 32
    out res[r1], r7        // Set NEXT LRCLK ready

    testct  r11, res[r9]
    bf      r11, readData

    ldw   r4, sp[0]
    ldw   r5, sp[1]
    ldw   r6, sp[2]
    ldw   r7, sp[3]
    ldw   r8, sp[4]
    ldw   r9, sp[5]
    ldw  r10, sp[6]
    retsp (FRAME + 1)

readData:   
    in r11, res[r9]
    stw r11,sp[sampleOut0]
    in r11, res[r9]
    stw r11,sp[sampleOut1]
    in r11, res[r9]
    stw r11,sp[sampleOut2]
    in r11, res[r9]
    stw r11,sp[sampleOut3]
    in r11, res[r9]
    stw r11,sp[sampleOut4]
    in r11, res[r9]
    stw r11,sp[sampleOut5]
    in r11, res[r9]
    stw r11,sp[sampleOut6]
    in r11, res[r9]
    stw r11,sp[sampleOut7]
    
right2: 
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11


    
    
    ldw r11, sp[sampleOut0] 
    bitrev r7, r11 
    ldw r8, sp[DOUT0] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut2] 
    bitrev r7, r11 
    ldw r8, sp[DOUT1] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut4] 
    bitrev r7, r11 
    ldw r8, sp[DOUT2] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut6] 
    bitrev r7, r11 
    ldw r8, sp[DOUT3] 
    out res[r8], r7

    
right3: 
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11



    ldw r11,sp[sampleIn0]
    out res[r10], r11
    ldw r11,sp[sampleIn1]
    out res[r10], r11
    ldw r11,sp[sampleIn2]
    out res[r10], r11
    ldw r11,sp[sampleIn3]
    out res[r10], r11
    ldw r11,sp[sampleIn4]
    out res[r10], r11
    ldw r11,sp[sampleIn5]
    out res[r10], r11
    ldw r11,sp[sampleIn6]
    out res[r10], r11
    ldw r11,sp[sampleIn7]
    out res[r10], r11

right4:
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11



    
    ldw r8, sp[DIN0] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn0]

    
    ldw r8, sp[DIN1] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn2]

    
    ldw r8, sp[DIN2] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn4]

    
    ldw r8, sp[DIN3] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn6]


    mkmsk  r7, 32

    ldw r8, sp[branchDistance]
    bf  r8, rightDone
moreSpDifRight: 
    out res[r2], r7
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11

    sub r8, r8, 1
    bt  r8, moreSpDifRight
rightDone:

    
left1:    
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11


    ldc r7, 0
    out res[r1], r7   // Set NEXT LRCLK ready
    

left2:  
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11



    
    ldw r8, sp[DIN0] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn1]

    
    ldw r8, sp[DIN1] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn3]

    
    ldw r8, sp[DIN2] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn5]

    
    ldw r8, sp[DIN3] 
    in r11, res[r8] 
    bitrev r11, r11 
    stw r11, sp[sampleIn7]


left3:  
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11



    
    ldw r11, sp[sampleOut1] 
    bitrev r7, r11 
    ldw r8, sp[DOUT0] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut3] 
    bitrev r7, r11 
    ldw r8, sp[DOUT1] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut5] 
    bitrev r7, r11 
    ldw r8, sp[DOUT2] 
    out res[r8], r7

    
    ldw r11, sp[sampleOut7] 
    bitrev r7, r11 
    ldw r8, sp[DOUT3] 
    out res[r8], r7


left4:  
    
    out res[r2], r6
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11



    mkmsk r7, 32

    ldw r8, sp[branchDistance]
    bf  r8, leftDone
moreSpDifLeft: 
    out res[r2], r7
    
    in  r11, res[r4]
    byterev r11, r11
    out res[r0], r11
    in  r11, res[r5]
    out res[r3], r11

    sub r8, r8, 1
    bt  r8, moreSpDifLeft
leftDone:

    bu Loop

    .cc_bottom deliverNew.func


.globl setMixerMultiplier
.globl setMixerMultiplier.nstackwords
.globl setMixerMultiplier.maxtimers
.globl setMixerMultiplier.maxchanends
.globl setMixerMultiplier.maxthreads
.linkset setMixerMultiplier.maxthreads, 0
.linkset setMixerMultiplier.nstackwords, FRAME + 4
.linkset setMixerMultiplier.maxtimers, 0
.linkset setMixerMultiplier.maxchanends, 0

    .cc_top setMixerMultiplier.func, setMixerMultiplier
setMixerMultiplier:
    ldaw  r11, dp[mixerMultipliers]
    stw r1, r11[r0]
    retsp 0
    .cc_bottom setMixerMultiplier.func

#define EEBLE 1


.globl channelContainsControlToken
.globl channelContainsControlToken.nstackwords
.globl channelContainsControlToken.maxtimers
.globl channelContainsControlToken.maxchanends
.globl channelContainsControlToken.maxthreads
.linkset channelContainsControlToken.maxthreads, 0
.linkset channelContainsControlToken.nstackwords, 0
.linkset channelContainsControlToken.maxtimers, 0
.linkset channelContainsControlToken.maxchanends, 0

    .cc_top channelContainsControlToken.func,  channelContainsControlToken

channelContainsControlToken:    
    LDAPF r11, checkControlToken
    setv res[r0], r11
    clre
    eeu   res[r0]
    setsr EEBLE
    clre
//    clrsr EEBLE
//    edu   res[r0]
    ldc   r0, 0
    retsp 0
    
checkControlToken:
    edu   res[r0]
    chkct res[r0], 1
    ldc   r0, 1
    retsp 0
    
    .cc_bottom channelContainsControlToken.func



// TODO: remove this function.
    
.globl doVolumeUsbIn
.globl doVolumeUsbIn.nstackwords
.globl doVolumeUsbIn.maxtimers
.globl doVolumeUsbIn.maxchanends
.globl doVolumeUsbIn.maxthreads
.linkset doVolumeUsbIn.maxthreads, 0
.linkset doVolumeUsbIn.nstackwords, FRAME + 4
.linkset doVolumeUsbIn.maxtimers, 0
.linkset doVolumeUsbIn.maxchanends, 0

    .cc_top doVolumeUsbIn.func, doVolumeUsbIn
doVolumeUsbIn:
    ldaw  r3, dp[analogue_in_premult]        /* load shared mixer_params array */
    ldw   r1, r3[r1]
    lsub r2, r3, r3, r3, r3 
    maccs r2, r3, r1, r0
    shl   r0, r2, 1
    retsp 0

    .cc_bottom doVolumeUsbIn.func
