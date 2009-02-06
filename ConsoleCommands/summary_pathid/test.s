.globl function1, "f{si}(0)"
.globl function1.nstackwords
.globl function1.maxthreads
.globl function1.maxtimers
.globl function1.maxchanends
.globl function1.maxsync
.type  function1, @function
.linkset function1.locnoside, 1
.linkset function1.locnochandec, 1
.linkset function1.nstackwords, 0
.cc_top function1.function,function1
          .align    4
function1:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom function1.function
.linkset function1.maxchanends, 0
.linkset function1.maxtimers, 0
.linkset function1.maxthreads, 1
.globl function2, "f{si}(0)"
.globl function2.nstackwords
.globl function2.maxthreads
.globl function2.maxtimers
.globl function2.maxchanends
.globl function2.maxsync
.type  function2, @function
.linkset function2.locnoside, 1
.linkset function2.locnochandec, 1
.linkset function2.nstackwords, 0
.cc_top function2.function,function2
          .align    4
function2:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom function2.function
.linkset function2.maxchanends, 0
.linkset function2.maxtimers, 0
.linkset function2.maxthreads, 1
.globl function3, "f{si}(0)"
.globl function3.nstackwords
.globl function3.maxthreads
.globl function3.maxtimers
.globl function3.maxchanends
.globl function3.maxsync
.type  function3, @function
.linkset function3.locnoside, 1
.linkset function3.locnochandec, 1
.linkset function3.nstackwords, 0
.cc_top function3.function,function3
          .align    4
function3:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom function3.function
.linkset function3.maxchanends, 0
.linkset function3.maxtimers, 0
.linkset function3.maxthreads, 1
.globl function4, "f{si}(0)"
.globl function4.nstackwords
.globl function4.maxthreads
.globl function4.maxtimers
.globl function4.maxchanends
.globl function4.maxsync
.type  function4, @function
.linkset function4.locnoside, 1
.linkset function4.locnochandec, 1
.linkset function4.nstackwords, 0
.cc_top function4.function,function4
          .align    4
function4:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom function4.function
.linkset function4.maxchanends, 0
.linkset function4.maxtimers, 0
.linkset function4.maxthreads, 1
.globl function5, "f{si}(0)"
.globl function5.nstackwords
.globl function5.maxthreads
.globl function5.maxtimers
.globl function5.maxchanends
.globl function5.maxsync
.type  function5, @function
.linkset function5.locnoside, 1
.linkset function5.locnochandec, 1
.linkset function5.nstackwords, 0
.cc_top function5.function,function5
          .align    4
function5:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom function5.function
.linkset function5.maxchanends, 0
.linkset function5.maxtimers, 0
.linkset function5.maxthreads, 1
.call test, function1
.call test, function2
.call test, function3
.call test, function4
.call test, function5
.globl test, "f{si}(si)"
.globl test.nstackwords
.globl test.maxthreads
.globl test.maxtimers
.globl test.maxchanends
.globl test.maxsync
.type  test, @function
.linkset test.locnoside, 1
.linkset test.locnochandec, 1
.linkset .LLNK5, function1.nstackwords $M function2.nstackwords
.linkset .LLNK4, .LLNK5 $M function3.nstackwords
.linkset .LLNK3, .LLNK4 $M function4.nstackwords
.linkset .LLNK2, .LLNK3 $M function5.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 1
.linkset test.nstackwords, .LLNK0
.cc_top test.function,test
          .align    4
test:
          entsp     0x1 
          ldc       r1, 0x0
          bf        r0, .L1 
          bu        .L0 
.L1:
          ldc       r1, 0x1
.L0:
          bl        function1 
          bl        function2 
          bl        function3 
          bl        function4 
          bl        function5 
          ldc       r0, 0x0
          retsp     0x1 
.cc_bottom test.function
.linkset .LLNK9, function1.maxchanends $M function2.maxchanends
.linkset .LLNK8, .LLNK9 $M function3.maxchanends
.linkset .LLNK7, .LLNK8 $M function4.maxchanends
.linkset .LLNK6, .LLNK7 $M function5.maxchanends
.linkset test.maxchanends, .LLNK6
.linkset .LLNK13, function1.maxtimers $M function2.maxtimers
.linkset .LLNK12, .LLNK13 $M function3.maxtimers
.linkset .LLNK11, .LLNK12 $M function4.maxtimers
.linkset .LLNK10, .LLNK11 $M function5.maxtimers
.linkset test.maxtimers, .LLNK10
.linkset .LLNK20, function1.maxthreads - 1
.linkset .LLNK19, 1 + .LLNK20
.linkset .LLNK18, 1 $M .LLNK19
.linkset .LLNK22, function2.maxthreads - 1
.linkset .LLNK21, 1 + .LLNK22
.linkset .LLNK17, .LLNK18 $M .LLNK21
.linkset .LLNK24, function3.maxthreads - 1
.linkset .LLNK23, 1 + .LLNK24
.linkset .LLNK16, .LLNK17 $M .LLNK23
.linkset .LLNK26, function4.maxthreads - 1
.linkset .LLNK25, 1 + .LLNK26
.linkset .LLNK15, .LLNK16 $M .LLNK25
.linkset .LLNK28, function5.maxthreads - 1
.linkset .LLNK27, 1 + .LLNK28
.linkset .LLNK14, .LLNK15 $M .LLNK27
.linkset test.maxthreads, .LLNK14
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
.linkset .LLNK30, test.nstackwords $M test.nstackwords
.linkset .LLNK29, .LLNK30 + 1
.linkset main.nstackwords, .LLNK29
.cc_top main.function,main
          .align    4
main:
          entsp     0x1 
          ldc       r0, 0x0
          bl        test 
          retsp     0x1 
.cc_bottom main.function
.linkset main.maxchanends, test.maxchanends
.linkset main.maxtimers, test.maxtimers
.linkset .LLNK33, test.maxthreads - 1
.linkset .LLNK32, 1 + .LLNK33
.linkset .LLNK31, 1 $M .LLNK32
.linkset main.maxthreads, .LLNK31
# Thread names for recovering thread graph in linker
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVA"
