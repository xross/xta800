// r0: in port
// r1: input stream
// r2: temp data.
// r3: mask
// r4: temp
// r5: SPARE
// r6: pointer to first table
// r7: data that has been read in
// r8: pointer to second table
// r9: second word count
// r10: 0
// r11: second word

//   void parseAdat48000(buffered in port:16 adatRx, chanend dataOut);


.cc_top parseAdat48000.func, parseAdat48000

    .section data
    .align 4
#include "adat48000.hS"
    .text
    
.globl parseAdat48000
.globl parseAdat48000.nstackwords
.globl parseAdat48000.maxtimers
.globl parseAdat48000.maxchanends
.globl parseAdat48000.maxthreads
.linkset parseAdat48000.maxthreads, 1
.linkset parseAdat48000.nstackwords, 11
.linkset parseAdat48000.maxtimers, 0
.linkset parseAdat48000.maxchanends, 0
    
parseAdat48000:
    entsp   11
    stw     r1, sp[9]
    stw     r4, sp[1]
    stw     r5, sp[2]
    stw     r6, sp[3]
    stw     r7, sp[4]
    stw     r8, sp[5]
    stw     r9, sp[6]
    stw     r10, sp[7]
    ldc     r10, 32
    stw     r10, sp[8]
    ldc     r10, 0
    ldap    r11, tab0
    add     r6, r11, 0
    ldap    r11, tab1
    add     r8, r11, 0
    ldc     r3, 24
    mkmsk   r3, r3
    ldc     r5, 32
    ldc     r1, 0
    ldc     r7, 0xff

    ldc     r9, 0
findStart:  
    in      r11, res[r0]
    bf      r11, startFrameZERO
    add     r4, r11, 1
    bt      r4, findStart

startFrameONE:
    or      r1, r11, r11
InONE0:    
    in      r11, res[r0]

    shl     r4, r11, r9
    or      r1, r1, r4
    sub     r4, r5, r9
    shr     r11, r11, r4

    add     r4, r1, 1
    bf      r4, startFrameONE
    bitrev  r4, r1
    not     r4, r4
    clz     r2, r4
    bu      setR3StartReading

startFrameZERO:
    or      r1, r11, r11
InZERO0:    
    in      r11, res[r0]

    shl     r4, r11, r9
    or      r1, r1, r4
    sub     r4, r5, r9
    shr     r11, r11, r4

    bf      r1, startFrameZERO
    bitrev  r4, r1
    clz     r2, r4


setR3StartReading:


    shr     r1, r1, r2

    sub     r4, r5, r2
    shl     r4, r11, r4
    
    or      r1, r1, r4
    shr     r11, r11, r2
    sub     r9, r9, r2
    lss     r4, r9, r10
    bf      r4, readyForUserBits

    in      r11, res[r0]
    add     r9, r9, r5

    shl     r4, r11, r9
    or      r1, r1, r4
    sub     r4, r5, r9
    shr     r11, r11, r4

readyForUserBits:  
    ldc     r7, 0x0800
    shl     r7, r7, 16
//    out     res[r5], r10

    
firstTwoBits:
    bf      r1, startFrameZERO
    add     r4, r1, 1
    bf      r4, startFrameONE
   
    ldc     r4, poly0
    and     r2, r1, r3
    crc32   r2, r10, r4
    ld8u    r2, r6[r2]
    bt      r2, decoded

    ldc     r4, poly1
    and     r2, r1, r3
    crc32   r2, r10, r4
    ld8u    r2, r8[r2]

decoded:
    stw     r1, sp[10]
    shr     r4, r2, 4
    shl     r7, r7, 4
    or      r7, r7, r4
    lss     r4, r7, r10
    bf      r4, dontOut
    ldw     r4, sp[9]
    out     res[r4], r7
    ldc     r7, 0xff
dontOut:    

    ldc     r4, 0xf         
    and     r2, r2, r4
    bf      r2, Return
    add     r2, r2, 9   // shift value
    add     r2, r2, 9   // shift value
    shr     r1, r1, r2

    sub     r4, r5, r2
    shl     r4, r11, r4
    
    or      r1, r1, r4
    shr     r11, r11, r2
    sub     r9, r9, r2
    lss     r4, r9, r10
    bf      r4, firstTwoBits
In1:    
    in      r11, res[r0]
    add     r9, r9, r5

    shl     r4, r11, r9
    or      r1, r1, r4
    sub     r4, r5, r9
    shr     r11, r11, r4
    bu      firstTwoBits

Return:
    and     r0, r1, r3
    ldw     r1, sp[10]
    ldw     r4, sp[1]
    ldw     r5, sp[2]
    ldw     r6, sp[3]
    ldw     r7, sp[4]
    ldw     r8, sp[5]
    ldw     r9, sp[6]
    ldw     r10, sp[7]
    retsp 11


    .cc_bottom parseAdat48000.func

