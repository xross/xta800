/*  
 * this code needs to be preprocessed by m4 before the .S file is fed to the compiler
 */

/*

NOTE: first 2 params are not used 

deliver(int, int, int sampleFrequency, int jumpDistance, chanend playChannel, chanend recordChannel,
        buffered out port:32 LRCLK, 
        buffered out port:32 BCLK,
        DOUT1, DOUT2, DOUT3, DOUT4, 
        DIN1, DIN2, DIN3, DIN4,
        chanend sampleChannel, chanend c_spdif);
*/

#define FRAME 22
#define EEBLE 1

#define bufferPlayChannel (FRAME + 2)
#define bufferRecordChannel (FRAME + 3)
#define LRCLK (FRAME + 4)
#define BCLK  (FRAME + 5)
#define DOUT0 (FRAME + 6)
#define DOUT1 (FRAME + 7)
#define DOUT2 (FRAME + 8)
#define DOUT3 (FRAME + 9)
#define DIN0 (FRAME + 10)
#define DIN1 (FRAME + 11)
#define DIN2 (FRAME + 12)
#define DIN3 (FRAME + 13)
#define sampleChannel (FRAME + 14)
#define spdifChannel (FRAME + 15)
//#define debugTheLeds (FRAME + 16)

/* Stack locations for vars */
#define playDataLength      7
#define recordDataLength    8
#define recordState         9
#define datapointer         10
#define channelSpace        11
#define recordBufferNumber  12
#define sampleFrequencyDiv100    13
#define recordCountModulo   14
#define leftMixedValue      15
#define rightMixedValue     16
#define branchDistance      17
#define underRuns           18
#define underRunPort        19
#define recordLowWater      20
#define recordHighWater     21

#ifdef SPDIF
#define SPDIF_SIZE 8
#else
#define SPDIF_SIZE 0
#endif

#ifdef ADAT
#define ADAT_SIZE 32
#else
#define ADAT_SIZE 0
#endif

#define ANALOGUE_OUT_CHANNELS    8
#define ANALOGUE_IN_CHANNELS  8
    
#define BYTES_PER_PLAY_SAMPLE     (ANALOGUE_OUT_CHANNELS * 4 + SPDIF_SIZE + ADAT_SIZE)
#define BYTES_PER_RECORD_SAMPLE   (ANALOGUE_IN_CHANNELS * 4 + SPDIF_SIZE + ADAT_SIZE)
    

    
.globl deliver
.globl deliver.nstackwords
.globl deliver.maxtimers
.globl deliver.maxchanends
.globl deliver.maxthreads
.linkset deliver.maxthreads, 0
.linkset deliver.nstackwords, FRAME + 4
.linkset deliver.maxtimers, 0
.linkset deliver.maxchanends, 0

    .cc_top deliver.func, deliver
    .align 4
deliver:    
    entsp FRAME + 1

RegSave:
    stw   r4, sp[0]
    stw   r5, sp[1]
    stw   r6, sp[2]
    stw   r7, sp[3]
    stw   r8, sp[4]
    stw   r9, sp[5]
    stw  r10, sp[6]

    stw   r0, sp[recordLowWater]
    stw   r1, sp[recordHighWater]
    stw   r3, sp[branchDistance]
    stw   r0, sp[underRunPort]
    
    ldc   r4, 100
    divs   r2, r2, r4
    stw   r2, sp[sampleFrequencyDiv100]

deliver_:   
    ldaw  r0, dp[mixer_mults]        /* load shared mixer_params array */
    ldaw  r11, dp[0]
    stw   r11, sp[datapointer]
    mov   r11, r0
    
    set   dp, r11
    LDAPF r11, defaultSampleValues                 
    add   r10, r11, 0

    /*
     * The recording state machine comprises a circular linked list of three elements
     * Each element consists of an address of a buffer (at address 0), the link to the
     * next state (at address 1), and the number of the buffer (at address 2).
     * The linked list is stored globally, and the pointer to
     * the current state is stored in the vairable "recordState"
     * In addition, r9 holds the current address where to dump data.
     */
    ldc   r11, 2
    stw   r11, sp[recordBufferNumber]

    LDAPF r11, linkedList
    stw   r11, sp[recordState]
    
    LDAPF r11, bufferRecord2
    add   r9, r11, 0
    
    
    /*
    * Two variables store the remaining length of data to be played or recorded. These are
    * storing a number of 8-channel samples. 1 means there is one more 8-channel sample (32 bytes)
    * to be recorded.
    */
    
    ldc  r7, 0
    stw  r7, sp[playDataLength]
    stw  r7, sp[recordCountModulo]
    stw  r7, sp[recordDataLength]
    stw  r7, sp[underRuns]

    /*
    * Two variables on the stack contain the left and right mixed values. They are summed into a register
    * r5 at run time, and then stored to be output at the next clock cycle.
    */
    stw  r7, sp[leftMixedValue]
    stw  r7, sp[rightMixedValue]
    
    LDAPF r11, channelSpacePersists
    ldw  r7, r11[0]
    stw  r7, sp[channelSpace]

    /*
    * The process has three input channel, a record channel, a play-channel, and a sample-rate channel.
    * Each is handled by an event handler. on recordVector, playVector and sampleVector.
    *
    * On the record channel an external process can request a buffer of recorded samples by sending
    * an END token. This process then replies with an address in memory and a length. The address in memory
    * is the top address of the buffer, the length is the remaining length in the buffer. The maximum buffer length
    * is 32 8-channel samples (1Kbyte). So, the receiver should compute the length (= 32-l) and the buffer base
    * (=b-32*l). No END is send afterwards, since the other process must reside on this core anyway.
    * r9 always holds the base address.
    *
    * On the play channel an external process can send a request to play a buffer. It does this by first posting an
    * the buffer length in 8-channel samples minus one, followed by address of the base of the buffer. This process
    * guarantees to answer this request within a single sample. (20 us, 10 us, or 5 us depending on the sampling
    * frequency). r10 always holds the base address
    *
    * On the sample channel an external process can send a request to change sample frequency. A single int should
    * be sent which should be 0 (48 or 44.1 Khz) 8 (96 or 88.2) or 24 (192 Khz). This will take effect immediately.
    * r11 always holds this number.
    */
    ldw r0, sp[bufferRecordChannel]
    LDAPF r11, recordVector
    setv res[r0], r11

    ldw r0, sp[bufferPlayChannel]
    LDAPF r11, playVector
    setv res[r0], r11

    ldw r0, sp[sampleChannel]
    LDAPF r11, sampleVector
    setv res[r0], r11

    /* Now preload the four output shift registers */
    
    ldc r7, 0
    ldw r0, sp[DOUT0]
    out res[r0], r7
    ldw r0, sp[DOUT1]
    out res[r0], r7
    ldw r0, sp[DOUT2]
    out res[r0], r7
    ldw r0, sp[DOUT3]
    out res[r0], r7

    /* Compute the mask 0x99999999 (eight clocks on the BCLK),
     * This masks is always stored in r6. r1 and r2 always hold the ports for
     * LRCLK and BCLK. A sequence of outs on r1 and r2 with r6, and 0 or 1 will create a block
     * of clocks. An LRCLK with 0 on the BLCK creates a pause. Both will take 32 master
     * clocks, or approximately 640 ns.
     */
    ldc r6,0x9999
    shl r0, r6, 16
    add r6, r0, r6

    ldw r1, sp[LRCLK]
    ldw r2, sp[BCLK]

    getts r0, res[r2]
    ldc   r7, 10000
    add   r0, r0, r7
//    setpt res[r1], r0
    setpt res[r2], r0

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

    /* Preload the OUT shift regsiters with anohter set of zeros, they are going to be shifted
       out in the loop */
    
    ldc r7, 0
    ldw r0, sp[DOUT0]
    out res[r0], r7
    ldw r0, sp[DOUT1]
    out res[r0], r7
    ldw r0, sp[DOUT2]
    out res[r0], r7
    ldw r0, sp[DOUT3]
    out res[r0], r7

    /* Two little macros that we should  use a #define for */

define(`outSample_OLD',`
ldInstr$1$4: 
    ldw r8, r10[$2] 
    ldw r4, dp[$3] 
    lsub r0, r7, r10, r10, r10 
maccInstr$1$4: 
    maccs r0, r7, r8, r4 
    bitrev r0, r0 
    ldw r8, sp[$1] 
    out res[r8], r0
')
    
define(`outSample',`
ldInstr$1$4: 
    ldw r0, r10[$2] 
    bitrev r7, r0 
    ldw r8, sp[$1] 
    out res[r8], r7
    ldw r4, dp[$3] 
    ldw r0, r10[$2] 
maccInstr$1$4: 
    maccs $5, r7, r0, r4    // r7 is destroyed
')

ifdef(`ADAT',`
define(`inSample',`
    ldw r8, sp[$1] 
    in r0, res[r8] 
    ldc r8, 1
    shl r8, r8, 16
    ldw r4, r8[$2]
    stw r4, r9[$2]')
',`
define(`inSample',`
    ldw r8, sp[$1] 
    in r0, res[r8] 
    bitrev r0, r0 
    ldw r4, dp[$3] 
    maccs r5, r7, r0, r4 
    ldw r4, dp[$4] 
    maccs r11, r7, r0, r4 
    stw r0, r9[$2]')
')

define(`spDifOut',`
    ldw   r0, r10[$1]
    shr   r0, r0, 8
    ldw   r8, sp[spdifChannel]
    out   res[r8], r0
    outct res[r8], 1
')
ifdef(`ADAT',`
define(`spDifOut2',`spDifOut($1)')
',`
define(`spDifOut2',`')
')
    
    /* Finally initialise to 192  */
    //ldc  r11, 24
    //ldc  r11, 0
    
// MAIN LOOP

Loop:
    
right1: 
    out res[r2], r6
    mkmsk r7, 32
    out res[r1], r7        // Set LRCLK ready
    lsub  r5, r11, r1, r1, r1                // initialise Left and Right mixer.
    
    ldw   r0, sp[playDataLength]
    bt    r0, moreDataToBePlayed
    ldw   r0, sp[bufferPlayChannel]
    eeu   res[r0]
    setsr EEBLE
playPoll: 
    clrsr EEBLE
    edu   res[r0]
                             // buffer underrun
#if 0
    ldw   r0, sp[underRuns]
    add   r0, r0, 1
    stw   r0, sp[underRuns]
    ldw   r4, sp[underRunPort]
    shl   r0, r0, 4  
    out   res[r4], r0
#endif
#if 0
    ldw   r0, sp[underRuns]
    ldc   r4, 32
    add   r0, r0, r4
    stw   r0, sp[underRuns]
    ldw   r4, sp[debugTheLeds]
    out   res[r4], r0
#endif
    
    bu    playContinues
    
moreDataToBePlayed: 
    sub   r0, r0, 1
    stw   r0, sp[playDataLength]
    ldc   r0, BYTES_PER_PLAY_SAMPLE
    add   r10, r10, r0
    bu    playContinues

playVector:
    edu   res[r0]
    int   r10, res[r0] // datalength
    sub   r10, r10, 1
    stw   r10, sp[playDataLength]
    int   r4, res[r0]
    outct res[r0], 1
    or    r0, r11, r11
    ldap  r11, playBuffers
    ldw   r10, r11[r4]
    or    r11, r0, r0

#if 0
    ldw   r0, sp[underRuns]
    ldc   r4, 16
    or   r0, r0, r4
    stw   r0, sp[underRuns]
    ldw   r4, sp[debugTheLeds]
    out   res[r4], r0
#endif

    
playContinues:
   
    
    nop
    outSample(DOUT0, 0, 1, R, r11)
    outSample(DOUT1, 2, 3, R, r11)

right2: 
    out res[r2], r6


    
checkRecord:
    ldw   r7, sp[recordDataLength]
    ldw   r4, sp[recordLowWater]
    lsu   r4, r7, r4
    bt    r4, incrementPointer

    ldw   r4, sp[channelSpace]
    bt    r4, dispatchBlock

    ldw   r4, sp[recordHighWater]
    lsu   r4, r7, r4
    bf    r4, recordContinues

incrementPointer:
    add   r7, r7, 1
    stw	  r7, sp[recordDataLength]
    ldc	  r7, BYTES_PER_RECORD_SAMPLE
    add   r9, r9, r7
    bu	  recordContinues
    
dispatchBlock:  
    sub   r4, r4, 2
    stw   r4, sp[channelSpace]

    ldw   r0, sp[bufferRecordChannel]
    ldw   r7, sp[recordBufferNumber]
    outt  res[r0], r7
    ldw   r7, sp[recordDataLength]
    outt  res[r0], r7

    ldc   r4, 0
    stw   r4, sp[recordDataLength]

//FetchNewBuffer: 
    ldw   r7, sp[recordState]
    ldw   r9, r7[0]
    ldw   r4, r7[2]
    stw   r4, sp[recordBufferNumber]
    ldw   r7, r7[1]
    stw   r7, sp[recordState]










#if 0
    
    ldw   r7, sp[channelSpace]
    bu checkRecord2

IncrementPointer:
    ldw   r7, sp[recordDataLength]
    add   r7, r7, 1
    stw	  r7, sp[recordDataLength]
    stw   r8, sp[recordCountModulo]
    ldc	  r7, 32
    add   r9, r9, r7
    bu	  recordContinues

checkRecord2:
    ldc   r4, 80                                 // 8 Khz
    ldw   r8, sp[recordCountModulo]
    add   r8, r8, r4
    ldw   r4, sp[sampleFrequencyDiv100]
    sub   r4, r8, r4
    lsu   r0, r8, r4
    bt    r0, IncrementPointer
    stw   r4, sp[recordCountModulo]

    bf    r7, noSpaceInChannel
    sub   r7, r7, 2
    stw   r7, sp[channelSpace]

    ldw   r0, sp[bufferRecordChannel]
    ldw   r7, sp[recordBufferNumber]
    outt  res[r0], r7
    ldw   r7, sp[recordDataLength]
    outt  res[r0], r7


noSpaceInChannel:
    ldc   r4, 0
    stw   r4, sp[recordDataLength]

//FetchNewBuffer: 
    ldw   r7, sp[recordState]
    ldw   r9, r7[0]
    ldw   r4, r7[2]
    stw   r4, sp[recordBufferNumber]
    ldw   r7, r7[1]
    stw   r7, sp[recordState]

#endif
    
recordContinues:


right3: 
    out res[r2], r6

    outSample(DOUT2, 4, 5, R, r11)
    outSample(DOUT3, 6, 7, R, r11)
    inSample(DIN0, 0, 10, 19)

right4:
    out res[r2], r6

    spDifOut((ANALOGUE_OUT_CHANNELS + 0))

    inSample(DIN1, 2, 12, 21)
    inSample(DIN2, 4, 14, 23)
    inSample(DIN3, 6, 16, 25)

    mkmsk  r7, 32
    
right4EndLabel:
    ldw r4, sp[branchDistance]
    bru r4
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
bruRightDefaultLabel:
    
left1:    
    out res[r2], r6
    ldc r7, 0
    out res[r1], r7   // Set LRCLK ready

    
checkSample:
    ldw   r0, sp[sampleChannel]
    eeu   res[r0]
    setsr EEBLE
    clrsr EEBLE
    edu   res[r0]
    bu    sampleContinues

sampleVector:
    edu   res[r0]
    int   r11,res[r0]
    chkct res[r0], 1
    mov   r0, r11

Return:
    LDAPF r11, channelSpacePersists
    ldw  r7, sp[channelSpace]
    stw  r7, r11[0]

    ldw   r4, sp[0]
    ldw   r5, sp[1]
    ldw   r6, sp[2]
    ldw   r7, sp[3]
    ldw   r8, sp[4]
    ldw   r9, sp[5]
    ldw  r10, sp[6]
    ldw  r11,sp[datapointer]
    set  dp, r11
    retsp (FRAME + 1)

sampleContinues:   

    outSample(DOUT0, 1, 2, L, r5)

    


left2:  
    out res[r2], r6

    outSample(DOUT1, 3, 4, L, r5)
    outSample(DOUT2, 5, 6, L, r5)
    outSample(DOUT3, 7, 8, L, r5)


left3:  
    out res[r2], r6

checkRecordConsumed:
    ldw   r0, sp[bufferRecordChannel]
    eeu   res[r0]
    setsr EEBLE
recordPoll: 
    clrsr EEBLE
    edu   res[r0]
    bu inputDidNotConsume

recordVector:
    edu   res[r0]
    ldw   r7, sp[channelSpace]
    chkct res[r0], 1
    add   r7, r7, 2
    stw   r7, sp[channelSpace]

inputDidNotConsume:

    inSample(DIN0, 1, 11, 20)
    inSample(DIN1, 3, 13, 22)

left4:  
    out res[r2], r6

    spDifOut((ANALOGUE_OUT_CHANNELS + 1))
    
    inSample(DIN2, 5, 15, 24)
    inSample(DIN3, 7, 17, 26)

    shl r11, r11, 1
    stw   r11, sp[rightMixedValue]
    shl r5, r5, 1
    stw   r5, sp[leftMixedValue]
    mkmsk r7, 32
left4EndLabel:
    ldw r4, sp[branchDistance]
    bru r4
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
    out res[r2], r7
bruLeftDefaultLabel:

    bu Loop

    .cc_bottom deliver.func

    
    .align 4
linkedList:
    .word    bufferRecord0, linkedList+12, 0
    .word    bufferRecord1, linkedList+24, 1
    .word    bufferRecord2, linkedList+0, 2
playBuffers:
    .word    bufferA, bufferB, bufferC
defaultSampleValues:
    .word 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
channelSpacePersists:
    .word    2                                         // Initial space.

.globl stword
.globl stword.nstackwords
.globl stword.maxtimers
.globl stword.maxchanends
.globl stword.maxthreads
.linkset stword.maxthreads, 0
.linkset stword.nstackwords, FRAME + 4
.linkset stword.maxtimers, 0
.linkset stword.maxchanends, 0

    .cc_top stword.func, stword
stword:
    stw r1, r0[0]
    retsp 0
    .cc_bottom stword.func




    

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


    

.globl doVolumeUsbOut
.globl doVolumeUsbOut.nstackwords
.globl doVolumeUsbOut.maxtimers
.globl doVolumeUsbOut.maxchanends
.globl doVolumeUsbOut.maxthreads
.linkset doVolumeUsbOut.maxthreads, 0
.linkset doVolumeUsbOut.nstackwords, FRAME + 4
.linkset doVolumeUsbOut.maxtimers, 0
.linkset doVolumeUsbOut.maxchanends, 0

    .cc_top doVolumeUsbOut.func, doVolumeUsbOut
doVolumeUsbOut:
    ldaw  r3, dp[usb_in_premult]        /* load shared mixer_params array */
    ldw   r1, r3[r1]
    lsub r2, r3, r3, r3, r3 
    maccs r2, r3, r1, r0
    shl   r0, r2, 1
    retsp 0

    .cc_bottom doVolumeUsbOut.func



    

.globl GetX
.globl GetX.nstackwords
.globl GetX.maxtimers
.globl GetX.maxchanends
.globl GetX.maxthreads
.linkset GetX.maxthreads, 0
.linkset GetX.nstackwords, 0
.linkset GetX.maxtimers, 0
.linkset GetX.maxchanends, 0

    .cc_top GetX.func, GetX
GetX:
    ldc   r0, 0x0206
    getd  r0, res[r0]
    retsp 0

    .cc_bottom GetX.func



    
    /*
    * mixerEnable(int channel, int on)
    * if (on)
    * Replaces ldw r0, r10[$2] with   ldw r0, sp[left/right]
    *          maccs  r5 ...   with   maccs r7 ...
    * else
    * Restores ldw r0, r10[$2] 
    *          maccs  r5 ...   
    */


.globl mixerEnable
.globl mixerEnable.nstackwords
.globl mixerEnable.maxtimers
.globl mixerEnable.maxchanends
.globl mixerEnable.maxthreads
.linkset mixerEnable.maxthreads, 0
.linkset mixerEnable.nstackwords, FRAME + 4
.linkset mixerEnable.maxtimers, 0
.linkset mixerEnable.maxchanends, 0

//.linkset maccInstrOffseta, (maccInstrDOUT0L - ldInstrDOUT0L) / 2
.linkset maccInstrOffset, 7

    .cc_top mixerEnable.func, mixerEnable
mixerEnable:
    ldc   r11, 3
    and   r0, r0, r11
    bru   r0
    bu    mix0
    bu    mix1
    bu    mix2
    bu    mix3
mix0:
    ldc   r2, 0x0988
    ldap  r11, ldInstrDOUT0L
    or     r3, r11, r11
    ldap  r11, ldInstrDOUT0R
    bu    setMixer
mix1:
    ldc   r2, 0x098A
    ldap  r11, ldInstrDOUT1L
    or     r3, r11, r11
    ldap  r11, ldInstrDOUT1R
    bu    setMixer
mix2:
    ldc   r2, 0x0BC8
    ldap  r11, ldInstrDOUT2L
    or     r3, r11, r11
    ldap  r11, ldInstrDOUT2R
    bu    setMixer
mix3:
    ldc   r2, 0x0BCA
    ldap  r11, ldInstrDOUT3L
    or     r3, r11, r11
    ldap  r11, ldInstrDOUT3R
setMixer:
    bt    r1, mixerOn

mixerOff:
    ldc   r0, 0
    st16  r2, r11[r0]
    add   r2, r2, 1
    st16  r2, r3[r0]
    retsp 0
mixerOn:
    ldc   r0, 0
    ldc   r2, 0x5c0f     // LDW r0, sp[leftMixedValue]
    st16  r2, r3[r0]
    ldc   r2, 0x5c10     // LDW r0, sp[rightMixedValue]
    st16  r2, r11[r0]
    retsp 0
    
    .cc_bottom mixerEnable.func




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
