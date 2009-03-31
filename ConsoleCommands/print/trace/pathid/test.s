.globl test, "f{si}(si)"
.globl test.nstackwords
.globl test.maxthreads
.globl test.maxtimers
.globl test.maxchanends
.globl test.maxsync
.type  test, @function
.linkset test.locnoside, 1
.linkset test.locnochandec, 1
.linkset test.nstackwords, 0
.cc_top test.function,test
          .align    4
test:
          ldc       r1, 0x6
loop:
          sub       r1, r1, 1
          bt        r1, loop
conditional:
          ldc       r1, 0x0
          eq        r0, r0, 0x2
          bt        r0, .L1 
          bu        .L2 
.L1:
          ldc       r1, 0x2
          bu        .L0 
.L2:
          ldc       r1, 0x3
.L0:
          mov       r0, r1
          retsp     0x0 
.cc_bottom test.function
.linkset test.maxchanends, 0
.linkset test.maxtimers, 0
.linkset test.maxthreads, 1
.call main, test
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 1
.linkset .LLNK1, test.nstackwords $M test.nstackwords
.linkset .LLNK0, .LLNK1 + 1
.linkset main.nstackwords, .LLNK0
.cc_top main.function,main
          .align    4
main:
          entsp     0x1 
          ldc       r0, 0x2
          bl        test 
          retsp     0x1 
.cc_bottom main.function
.linkset main.maxchanends, test.maxchanends
.linkset main.maxtimers, test.maxtimers
.linkset .LLNK4, test.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset main.maxthreads, .LLNK2
# Thread names for recovering thread graph in linker
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
