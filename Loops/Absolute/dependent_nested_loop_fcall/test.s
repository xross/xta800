          .file     "test.xc"
          .text
          .align    2
.globl loop, "f{si}(si)"
.globl loop.nstackwords
.globl loop.maxthreads
.globl loop.maxtimers
.globl loop.maxchanends
.globl loop.maxsync
.type  loop, @function
.linkset loop.locnoside, 1
.linkset loop.locnochandec, 1
.linkset loop.nstackwords, 0
.cc_top loop.function,loop
          .align    4
loop:
.L8:
          ldc       r1, 0x0
          ldc       r2, 0x0
.L1:
.L9:
          lss       r3, r2, r0
          bt        r3, .L2 
          bu        .L0 
.L2:
.xtalabel outer_loop, "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall", "test.xc", 7, loop
.L10:
          ldc       r3, 0x0
.L5:
.xtalabel outer_loop, "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall", "test.xc", 7, loop
.L11:
          ldc       r11, 0x3
          lss       r11, r3, r11
          bt        r11, .L6 
          bu        .L4 
.L6:
.xtalabel outer_loop, "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall", "test.xc", 7, loop
.L12:
          mul       r11, r3, r0
          add       r1, r1, r11
.L7:
          add       r3, r3, 0x1
          bu        .L5 
.L4:
.L13:
          mul       r3, r2, r0
          add       r1, r1, r3
.L3:
          add       r2, r2, 0x1
          bu        .L1 
.L0:
.xtalabel inner_loop, "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall", "test.xc", 10, loop
.L14:
          mov       r0, r1
          retsp     0x0 
.cc_bottom loop.function
.linkset loop.maxchanends, 0
.linkset loop.maxtimers, 0
.linkset loop.maxthreads, 1
.call main, loop
.call main, loop
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 1
.linkset .LLNK2, loop.nstackwords $M loop.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 4
.linkset main.nstackwords, .LLNK0
.cc_top main.function,main
          .align    4
main:
          entsp     0x4 
.L19:
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          stw       r6, sp[0x3] 
          ldc       r4, 0x0
          ldc       r5, 0xa
          ldc       r6, 0x0
.L16:
.L20:
          ldc       r0, 0x3
          lss       r0, r6, r0
          bt        r0, .L17 
          bu        .L15 
.L17:
.L21:
          mov       r0, r5
          bl        loop 
          add       r4, r4, r0
          mov       r0, r5
          bl        loop 
          add       r4, r4, r0
.L18:
          add       r6, r6, 0x1
          bu        .L16 
.L15:
.xtalabel main_loop, "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall", "test.xc", 26, main
.L22:
          mov       r0, r4
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          ldw       r6, sp[0x3] 
          retsp     0x4 
.cc_bottom main.function
.linkset .LLNK3, loop.maxchanends $M loop.maxchanends
.linkset main.maxchanends, .LLNK3
.linkset .LLNK4, loop.maxtimers $M loop.maxtimers
.linkset main.maxtimers, .LLNK4
.linkset .LLNK8, loop.maxthreads - 1
.linkset .LLNK7, 1 + .LLNK8
.linkset .LLNK6, 1 $M .LLNK7
.linkset .LLNK10, loop.maxthreads - 1
.linkset .LLNK9, 1 + .LLNK10
.linkset .LLNK5, .LLNK6 $M .LLNK9
.linkset main.maxthreads, .LLNK5
# Thread names for recovering thread graph in linker
          .section .xtalabeltable,       "", @progbits
.L23:
          .int      .L24-.L23
          .int      0x00000000
          .asciiz   "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Loops/Absolute/dependent_nested_loop_fcall"
.cc_top main.function, main
          .asciiz  "test.xc"
          .int      0x0000001e
          .int      0x0000001e
          .long    .L22
          .asciiz  "test.xc"
          .int      0x0000001b
          .int      0x00000018
          .long    .L21
          .asciiz  "test.xc"
          .int      0x00000018
          .int      0x00000018
          .long    .L20
          .asciiz  "test.xc"
          .int      0x00000014
          .int      0x00000018
          .long    .L19
.cc_bottom main.function
.cc_top loop.function, loop
          .asciiz  "test.xc"
          .int      0x0000000f
          .int      0x0000000f
          .long    .L14
          .asciiz  "test.xc"
          .int      0x0000000d
          .int      0x00000005
          .long    .L13
          .asciiz  "test.xc"
          .int      0x0000000b
          .int      0x00000008
          .long    .L12
          .asciiz  "test.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L11
          .asciiz  "test.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L10
          .asciiz  "test.xc"
          .int      0x00000005
          .int      0x00000005
          .long    .L9
          .asciiz  "test.xc"
          .int      0x00000003
          .int      0x00000005
          .long    .L8
.cc_bottom loop.function
.L24:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
