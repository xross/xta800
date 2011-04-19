/*  
 * this code needs to be preprocessed by m4 before the .S file is fed to the compiler
 */

/*
*/

#define FRAME 4
#define EEBLE 1

#define recordChannelParameter (FRAME + 2)
#define i2sArrayParameter         (FRAME + 3)
#define digitalArrayParameter         (FRAME + 4)

    
.globl usbRecord
.globl usbRecord.nstackwords
.globl usbRecord.maxtimers
.globl usbRecord.maxchanends
.globl usbRecord.maxthreads
.linkset usbRecord.maxthreads, 0
.linkset usbRecord.nstackwords, FRAME + 4
.linkset usbRecord.maxtimers, 0
.linkset usbRecord.maxchanends, 0

    .cc_top usbRecord.func, usbRecord
    .align 4
usbRecord:    
    entsp FRAME + 1

RegSave:
    stw   r4, sp[0]
    stw   r5, sp[1]
    stw   r6, sp[2]
    stw   r7, sp[3]

    ldw   r4, sp[recordChannelParameter]
    
    ldw   r11, dp[recordState]
    bt    r11, recordStateInitialised
    ldap r11, linkedList
    stw   r11, dp[recordState]
    ldc   r11, 2
    stw   r11, dp[recordBufferNumber]
    ldap r11, bufferRecord2
    stw   r11, dp[recordPointer]
    ldap r11, recordVector
    setv  res[r4], r11
recordStateInitialised: 

    ldw   r6, dp[recordPointer]
    ldaw  r6, r6[r0]
    
    ldw   r5, sp[i2sArrayParameter]
    ldw   r11, r5[0]
    stw   r11, r6[0]
    ldw   r11, r5[1]
    stw   r11, r6[1]
    ldw   r11, r5[2]
    stw   r11, r6[2]
    ldw   r11, r5[3]
    stw   r11, r6[3]
    ldw   r11, r5[4]
    stw   r11, r6[4]
    ldw   r11, r5[5]
    stw   r11, r6[5]
    ldw   r11, r5[6]
    stw   r11, r6[6]
    ldw   r11, r5[7]
    stw   r11, r6[7]
    ldw   r5, sp[digitalArrayParameter]
    ldaw  r6, r6[8]
    ldw   r11, r5[8]
    stw   r11, r6[0]
    ldw   r11, r5[9]
    stw   r11, r6[1]
    ldw   r11, r5[0]
    stw   r11, r6[2]
    ldw   r11, r5[1]
    stw   r11, r6[3]
    ldw   r11, r5[2]
    stw   r11, r6[4]
    ldw   r11, r5[3]
    stw   r11, r6[5]
    ldw   r11, r5[4]
    stw   r11, r6[6]
    ldw   r11, r5[5]
    stw   r11, r6[7]
    ldw   r11, r5[6]
    stw   r11, r6[8]
    ldw   r11, r5[7]
    stw   r11, r6[9]

// r0: index
// r1: low water (in words eg 40)
// r2: high water (in words eg 60)
// r3: block size (in words, eg 10)
    // r4: record channel
    // r5: pointer of data to be stored
    // r6: pointer where to store data
    
    // r11 sc


    
checkRecord:
    lsu   r11, r0, r1
    bt    r11, incrementPointer

    ldw   r11, dp[channelSpace]
    bt    r11, dispatchBlock

    lsu   r11, r0, r2
    bf    r11, recordContinues

incrementPointer:
    add   r0, r0, r3
    bu	  recordContinues
    
dispatchBlock:  
    sub   r11, r11, 2
    stw   r11, dp[channelSpace]

    ldw   r7, dp[recordBufferNumber]
    outt  res[r4], r7
    outt  res[r4], r0                 // data length

    ldc   r0, 0

//FetchNewBuffer: 
    ldw   r11, dp[recordState]
    ldw   r6, r11[0]
    stw   r6, dp[recordPointer]
    ldw   r7, r11[2]
    stw   r7, dp[recordBufferNumber]
    ldw   r11, r11[1]
    stw   r11, dp[recordState]
    
recordContinues:

checkRecordConsumed:
    eeu   res[r4]
    setsr EEBLE
recordPoll: 
    clrsr EEBLE
    edu   res[r4]
    bu inputDidNotConsume

recordVector:
    edu   res[r4]
    ldw   r11, dp[channelSpace]
    chkct res[r4], 1
    add   r11, r11, 2
    stw   r11, dp[channelSpace]

inputDidNotConsume:

    ldw   r4, sp[0]
    ldw   r5, sp[1]
    ldw   r6, sp[2]
    ldw   r7, sp[3]
    retsp FRAME+1


    
    
    .align 4
linkedList:
    .word    bufferRecord0, linkedList+12, 0
    .word    bufferRecord1, linkedList+24, 1
    .word    bufferRecord2, linkedList+0, 2
    
    .cc_bottom usbRecord.func
