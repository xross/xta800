.text

.align 2
.xtathreadstart
.globl _start
_start:
  bl main
  clre
  waiteu

.type main, @function
.globl main
main:
    add r0, r0, r0
    add r0, r0, 0x1
    and r0, r0, r0
    andnot r0, r0
    ashr r0, r0, r0
    ashr r0, r0, 0x1
    bau r0
    bitrev r0, r0
    bla r0
    bla cp[1023] 
    bla cp[1048575]
    blat 63
    blat 65535
    bl 1023
    bl 1048575
    bl -1023
    bl -1048575
    bf r0, 63
    bf r0, 65535
    bt r0, -63
    bt r0, -65535
    bu 63
    bu 65535
    bf r0, 63
    bf r0, 65535
    bt r0, 63
    bt r0, 65535
    bu 63
    bu 65535
    bru r0
    byterev r0, r0
    chkct res[r0], r0
    chkct res[r0], 11
    clre
    clrpt res[r0]
    clrsr 63
    clrsr 65535
    clz r0, r0
    crc32 r0, r0, r0
    crc8 r0, r0, r0, r0
    dcall
    dentsp
    dgetreg r0
    divs r0, r0, r0
    divu r0, r0, r0
    drestsp
    dret
    ecallf r0
    ecallt r0
    edu res[r0]
    eef r0, res[r0]
    eet r0, res[r0]
    eeu res[r0]
    endin r0, res[r0]
    entsp 63
    entsp 65535
    eq r0, r0, r0
    eq r0, r0, 11
    extdp 63
    extdp 65535
    extsp 63
    extsp 65535
    freer res[r0]
    freet
    getd r0, res[r0]
    get r11, ed
    get r11, et
    get r11, id
    get r11, kep
    get r11, ksp
    getn r0, res[r0]
    get r0, ps[r0]
    getr r0, 11
    getsr r11, 63
    getsr r11, 65535
    getst r0, res[r0]
    getts r0, res[r0]
    in r0, res[r0]
    inct r0, res[r0]
    inpw r0, res[r0], 24
    inshr r0, res[r0]
    int r0, res[r0]
    kcall 63
    kcall 65535
    kcall r0
    kentsp 63
    kentsp 65535
    krestsp 63
    krestsp 65535
    kret
    ladd r0, r0, r0, r0, r0
    ld16s r0, r0[r0]
    ld8u r0, r0[r0]
    lda16 r0, r0[-r0]
    lda16 r0, r0[r0]
    ldap r11, 1023
    ldap r11, 1048575
    ldap r11, -1023
    ldap r11, -1048575
    ldaw r0, r0[-r0]
    ldaw r0, r0[-11]
    ldaw r11, cp[63]
    ldaw r11, cp[65535]
    ldaw r0, dp[63]
    ldaw r0, dp[65535]
    ldaw r0, r0[r0]
    ldaw r0, r0[11]
    ldaw r0, sp[63]
    ldaw r0, sp[65535]
    ldc r0, 63
    ldc r0, 65535
    ldw et, sp[4]
    ldivu r0, r0, r0, r0, r0
    ldw sed, sp[3]
    ldw spc, sp[1]
    ldw ssr, sp[2]
    ldw r0, r0[r0]
    ldw r0, r0[11]
    ldw r0, cp[63]
    ldw r0, cp[65535]
    LDWCPL r11, cp[1023]
    LDWCPL r11, cp[1048575]
    ldw r0, dp[63]
    ldw r0, dp[65535]
    ldw r0, sp[63]
    ldw r0, sp[65535]
    lmul r0, r0, r0, r0, r0, r0
    lss r0, r0, r0
    lsu r0, r0, r0
    lsub r0, r0, r0, r0, r0
    maccu r0, r0, r0, r0
    maccs r0, r0, r0, r0
    mjoin res[r0]
    mkmsk r0, r0
    mkmsk r0, 24
    msync res[r0]
    mul r0, r0, r0
    neg r0, r0
    not r0, r0
    or r0, r0, r0
    out res[r0], r0
    outct res[r0], r0
    outct res[r0], 11
    outpw res[r0], r0, 24
    outshr res[r0], r0
    outt res[r0], r0
    peek r0, res[r0]
    rems r0, r0, r0
    remu r0, r0, r0
    setc res[r0], 63
    setc res[r0], 65535
    setc res[r0], r0
    setclk res[r0], r0
    set cp, r0
    setd res[r0], r0
    set dp, r0
    setev res[r0], r11
    set kep, r11
    setn res[r0], r0
    set ps[r0], r0
    setpsc res[r0], r0
    setpt res[r0], r0
    setrdy res[r0], r0
    set sp, r0
    setsr 63
    setsr 65535
    settw res[r0], r0
    setv res[r0], r11
    sext r0, r0
    sext r0, 24
    shl r0, r0, r0
    shl r0, r0, 24
    shr r0, r0, r0
    shr r0, r0, 24
    ssync
    st16 r0, r0[r0]
    st8 r0, r0[r0]
    stw sed, sp[3]
    stw et, sp[4]
    stw spc, sp[1]
    stw ssr, sp[2]
    stw r0, r0[r0]
    stw r0, r0[11]
    stw r0, dp[63]
    stw r0, dp[65535]
    stw r0, sp[63]
    stw r0, sp[65535]
    sub r0, r0, r0
    sub r0, r0, 11
    syncr res[r0]
    testlcl r0, res[r0]
    testct r0, res[r0]
    testwct r0, res[r0]
    init t[r0]:cp, r0
    init t[r0]:dp, r0
    init t[r0]:lr, r0
    init t[r0]:pc, r0
    init t[r0]:sp, r0
    tsetmr r0, r0
    set t[r0]:r0, r0
    start t[r0]
    waitef r0
    waitet r0
    waiteu
    xor r0, r0, r0
    zext r0, r0
    zext r0, 24
    retsp 0x1
    retsp 0x1FF
