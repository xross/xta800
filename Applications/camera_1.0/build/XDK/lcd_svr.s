          .file     "../../src/lcd/lcd_svr.xc"
.extern bitrev, "f{ui}(ui)"
.extern byterev, "f{ui}(ui)"
.extern clz, "f{si}(ui)"
.extern printchar, "f{si}(uc)"
.extern printcharln, "f{si}(uc)"
.extern printint, "f{si}(si)"
.extern printintln, "f{si}(si)"
.extern printuint, "f{si}(ui)"
.extern printuintln, "f{si}(ui)"
.extern printhex, "f{si}(ui)"
.extern printhexln, "f{si}(ui)"
.extern printstr, "f{si}(&(a(:c:uc)))"
.extern printstrln, "f{si}(&(a(:c:uc)))"
.extern configure_in_port_handshake, "f{0}(w:p,i:p,o:p,ck)"
.extern configure_out_port_handshake, "f{0}(w:p,i:p,o:p,ck,ui)"
.extern configure_in_port_strobed_master, "f{0}(w:p,o:p,:ck)"
.extern configure_out_port_strobed_master, "f{0}(w:p,o:p,:ck,ui)"
.extern configure_in_port_strobed_slave, "f{0}(w:p,i:p,ck)"
.extern configure_out_port_strobed_slave, "f{0}(w:p,i:p,ck,ui)"
.extern configure_in_port, "f{0}(w:p,:ck)"
.extern configure_out_port, "f{0}(w:p,:ck,ui)"
.extern configure_port_clock_output, "f{0}(w:p,:ck)"
.extern start_port, "f{0}(w:p)"
.extern stop_port, "f{0}(w:p)"
.extern configure_clock_src, "f{0}(ck,w:p)"
.extern configure_clock_ref, "f{0}(ck,uc)"
.extern configure_clock_rate, "f{0}(ck,ui,ui)"
.extern configure_clock_rate_at_least, "f{0}(ck,ui,ui)"
.extern configure_clock_rate_at_most, "f{0}(ck,ui,ui)"
.extern set_clock_src, "f{0}(ck,w:p)"
.extern set_clock_ref, "f{0}(ck)"
.extern set_clock_div, "f{0}(ck,uc)"
.extern set_clock_rise_delay, "f{0}(ck,ui)"
.extern set_clock_fall_delay, "f{0}(ck,ui)"
.extern set_port_clock, "f{0}(w:p,:ck)"
.extern set_port_ready_src, "f{0}(w:p,w:p)"
.extern set_clock_ready_src, "f{0}(ck,w:p)"
.extern set_clock_on, "f{0}(ck)"
.extern set_clock_off, "f{0}(ck)"
.extern start_clock, "f{0}(ck)"
.extern stop_clock, "f{0}(ck)"
.extern set_port_use_on, "f{0}(w:p)"
.extern set_port_use_off, "f{0}(w:p)"
.extern set_port_mode_data, "f{0}(w:p)"
.extern set_port_mode_clock, "f{0}(w:p)"
.extern set_port_mode_ready, "f{0}(w:p)"
.extern set_port_drive, "f{0}(w:p)"
.extern set_port_drive_low, "f{0}(w:p)"
.extern set_port_pull_up, "f{0}(w:p)"
.extern set_port_pull_down, "f{0}(w:p)"
.extern set_port_pull_none, "f{0}(w:p)"
.extern set_port_master, "f{0}(w:p)"
.extern set_port_slave, "f{0}(w:p)"
.extern set_port_no_ready, "f{0}(w:p)"
.extern set_port_strobed, "f{0}(w:p)"
.extern set_port_handshake, "f{0}(w:p)"
.extern set_port_no_sample_delay, "f{0}(w:p)"
.extern set_port_sample_delay, "f{0}(w:p)"
.extern set_port_no_inv, "f{0}(w:p)"
.extern set_port_inv, "f{0}(w:p)"
.extern set_port_shift_count, "f{0}(w:p,ui)"
.extern set_pad_delay, "f{0}(w:p,ui)"
.extern set_thread_fast_mode_on, "f{0}(0)"
.extern set_thread_fast_mode_off, "f{0}(0)"
.extern start_streaming_slave, "f{0}(chd)"
.extern start_streaming_master, "f{0}(chd)"
.extern stop_streaming_slave, "f{0}(chd)"
.extern stop_streaming_master, "f{0}(chd)"
.extern outuchar, "f{0}(chd,uc)"
.extern outuint, "f{0}(chd,ui)"
.extern inuchar, "f{uc}(chd)"
.extern inuint, "f{ui}(chd)"
.extern inuchar_byref, "f{0}(chd,&(uc))"
.extern inuint_byref, "f{0}(chd,&(ui))"
.extern sync, "f{0}(w:p)"
.extern peek, "f{ui}(w:p)"
.extern clearbuf, "f{0}(w:p)"
.extern endin, "f{ui}(w:p)"
.extern partin, "f{ui}(w:p,ui)"
.extern partout, "f{0}(w:p,ui,ui)"
.extern partout_timed, "f{ui}(w:p,ui,ui,ui)"
.extern partin_timestamped, "f{ui,ui}(w:p,ui)"
.extern partout_timestamped, "f{ui}(w:p,ui,ui)"
.extern outct, "f{0}(chd,uc)"
.extern chkct, "f{0}(chd,uc)"
.extern inct, "f{uc}(chd)"
.extern inct_byref, "f{0}(chd,&(ui))"
.extern testct, "f{si}(chd)"
.extern testwct, "f{si}(chd)"
.extern out_char_array, "ft{0}(chd,&(a(:c:uc)),ui)"
.extern in_char_array, "ft{0}(chd,&(a(:uc)),ui)"
.extern crc32, "f{0}(&(ui),ui,ui)"
.extern crc8shr, "f{ui}(&(ui),ui,ui)"
.extern lmul, "f{ui,ui}(ui,ui,ui,ui)"
.extern mac, "f{ui,ui}(ui,ui,ui,ui)"
.extern macs, "f{si,ui}(si,si,si,ui)"
.extern pinseq, "f{0}(ui)"
.extern pinsneq, "f{0}(ui)"
.extern pinseq_at, "f{0}(ui,ui)"
.extern pinsneq_at, "f{0}(ui,ui)"
.extern timerafter, "f{0}(ui)"
.extern getps, "f{ui}(ui)"
.extern setps, "f{0}(ui,ui)"
.extern read_pswitch_reg, "f{si}(ui,ui,&(ui))"
.extern read_sswitch_reg, "f{si}(ui,ui,&(ui))"
.extern write_pswitch_reg, "f{si}(ui,ui,ui)"
.extern write_sswitch_reg, "f{si}(ui,ui,ui)"
.extern get_core_id, "f{ui}(0)"
.extern get_thread_id, "f{ui}(0)"
.extern __builtin_getid, "f{si}(0)"
.extern lb_lineDone, "f{0}(chd)"
.extern lb_getPix, "f{ui}(chd)"
.extern line_buffer, "f{0}(m:chd,m:chd)"
.extern lcd_physical, "f{0}(m:chd,m:chd,o:p,o:p,bo:p:32,ck)"
          .text
          .align    2
.cc_top delay.function,delay
          .align    4
.call delay, __builtin_timer_after
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
.globl delay, "f{0}(ui)"
.globl delay.nstackwords
.globl delay.maxthreads
.globl delay.maxtimers
.globl delay.maxchanends
.globl delay.maxsync
.type  delay, @function
.linkset delay.locnoside, 0
.linkset delay.locnochandec, 1
.linkset delay.nstackwords, 0
delay:
.L0:
          getr      r2, 0x1
          setc      res[r2], 0x1
.L1:
          in        r1, res[r2] 
          add       r1, r1, r0
          setd      res[r2], r1
          setc      res[r2], 0x9
.L2:
          in        r1, res[r2] 
          freer     res[r2]
          retsp     0x0 
.cc_bottom delay.function
.linkset delay.maxchanends, 0
.linkset delay.maxtimers, 1
.linkset delay.maxthreads, 1
.cc_top LCD_RGB565_to_RGB666.function,LCD_RGB565_to_RGB666
          .align    4
.globl LCD_RGB565_to_RGB666, "f{ui}(ui)"
.globl LCD_RGB565_to_RGB666.nstackwords
.globl LCD_RGB565_to_RGB666.maxthreads
.globl LCD_RGB565_to_RGB666.maxtimers
.globl LCD_RGB565_to_RGB666.maxchanends
.globl LCD_RGB565_to_RGB666.maxsync
.type  LCD_RGB565_to_RGB666, @function
.linkset LCD_RGB565_to_RGB666.locnoside, 1
.linkset LCD_RGB565_to_RGB666.locnochandec, 1
.linkset LCD_RGB565_to_RGB666.nstackwords, 0
LCD_RGB565_to_RGB666:
.L3:
          mkmsk     r1, 0x5
          and       r1, r0, r1
          shl       r1, r1, 0x1
          ldc       r2, 0x7e0
          and       r2, r0, r2
          shl       r2, r2, 0x1
          or        r1, r1, r2
          ldc       r2, 0xf800
          and       r0, r0, r2
          shl       r0, r0, 0x2
          or        r1, r1, r0
          mov       r0, r1
          retsp     0x0 
.cc_bottom LCD_RGB565_to_RGB666.function
.linkset LCD_RGB565_to_RGB666.maxchanends, 0
.linkset LCD_RGB565_to_RGB666.maxtimers, 0
.linkset LCD_RGB565_to_RGB666.maxthreads, 1
.cc_top lcd_init.function,lcd_init
          .align    4
.call lcd_init, configure_clock_rate
.call lcd_init, configure_out_port
.call lcd_init, configure_out_port
.call lcd_init, configure_port_clock_output
.call lcd_init, __builtin_set_port_inv
.call lcd_init, __builtin_start_clock
.set __builtin_set_port_inv, 0
.linkset __builtin_set_port_inv.locnoside, 0
.linkset __builtin_set_port_inv.locnochandec, 1
.set __builtin_start_clock, 0
.linkset __builtin_start_clock.locnoside, 0
.linkset __builtin_start_clock.locnochandec, 1
.globl lcd_init, "f{0}(o:p,o:p,bo:p:32,ck)"
.globl lcd_init.nstackwords
.globl lcd_init.maxthreads
.globl lcd_init.maxtimers
.globl lcd_init.maxchanends
.globl lcd_init.maxsync
.type  lcd_init, @function
.linkset lcd_init.locnoside, 1
.linkset lcd_init.locnochandec, 1
.linkset .LLNK4, configure_clock_rate.nstackwords $M configure_out_port.nstackwords
.linkset .LLNK3, .LLNK4 $M configure_out_port.nstackwords
.linkset .LLNK2, .LLNK3 $M configure_port_clock_output.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 5
.linkset lcd_init.nstackwords, .LLNK0
lcd_init:
          entsp     0x5 
.L4:
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          stw       r6, sp[0x3] 
          stw       r7, sp[0x4] 
          mov       r7, r0
          mov       r6, r1
          mov       r5, r2
          mov       r4, r3
          mov       r0, r3
          ldc       r1, 0x64
          ldc       r2, 0x3c
.L5:
          bl        configure_clock_rate 
          mov       r0, r5
          mov       r1, r4
          ldc       r2, 0x0
.L6:
          bl        configure_out_port 
          mov       r0, r7
          mov       r1, r4
          ldc       r2, 0x0
.L7:
          bl        configure_out_port 
          mov       r0, r6
          mov       r1, r4
.L8:
          bl        configure_port_clock_output 
          setc      res[r6], 0x600f
          setc      res[r4], 0xf
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          ldw       r6, sp[0x3] 
          ldw       r7, sp[0x4] 
          retsp     0x5 
.cc_bottom lcd_init.function
.linkset .LLNK7, configure_clock_rate.maxchanends $M configure_out_port.maxchanends
.linkset .LLNK6, .LLNK7 $M configure_out_port.maxchanends
.linkset .LLNK5, .LLNK6 $M configure_port_clock_output.maxchanends
.linkset lcd_init.maxchanends, .LLNK5
.linkset .LLNK10, configure_clock_rate.maxtimers $M configure_out_port.maxtimers
.linkset .LLNK9, .LLNK10 $M configure_out_port.maxtimers
.linkset .LLNK8, .LLNK9 $M configure_port_clock_output.maxtimers
.linkset lcd_init.maxtimers, .LLNK8
.linkset .LLNK16, configure_clock_rate.maxthreads - 1
.linkset .LLNK15, 1 + .LLNK16
.linkset .LLNK14, 1 $M .LLNK15
.linkset .LLNK18, configure_out_port.maxthreads - 1
.linkset .LLNK17, 1 + .LLNK18
.linkset .LLNK13, .LLNK14 $M .LLNK17
.linkset .LLNK20, configure_out_port.maxthreads - 1
.linkset .LLNK19, 1 + .LLNK20
.linkset .LLNK12, .LLNK13 $M .LLNK19
.linkset .LLNK22, configure_port_clock_output.maxthreads - 1
.linkset .LLNK21, 1 + .LLNK22
.linkset .LLNK11, .LLNK12 $M .LLNK21
.linkset lcd_init.maxthreads, .LLNK11
.cc_top lcd_physical.function,lcd_physical
          .align    4
.call lcd_physical, lcd_init
.globl lcd_physical, "f{0}(m:chd,m:chd,o:p,o:p,bo:p:32,ck)"
.globl lcd_physical.nstackwords
.globl lcd_physical.maxthreads
.globl lcd_physical.maxtimers
.globl lcd_physical.maxchanends
.globl lcd_physical.maxsync
.type  lcd_physical, @function
.linkset lcd_physical.locnoside, 0
.linkset lcd_physical.locnochandec, 1
.linkset .LLNK24, lcd_init.nstackwords $M lcd_init.nstackwords
.linkset .LLNK23, .LLNK24 + 3
.linkset lcd_physical.nstackwords, .LLNK23
lcd_physical:
          entsp     0x3 
.L27:
          mov       r9, r0
          stw       r1, sp[0x1] 
          stw       r2, sp[0x2] 
          ldw       r10, sp[0x4] 
          ldw       r4, sp[0x5] 
          mov       r0, r2
          mov       r1, r3
          mov       r2, r10
          mov       r3, r4
.L40:
          bl        lcd_init 
          ldc       r4, 0x0
          mkmsk     r1, 0x5
          ldc       r3, 0x7e0
          ldc       r2, 0xf800
          ldc       r0, 0x140
.L12:
.L28:
          ldw       r5, sp[0x2] 
.L41:
          out       res[r5], r4 
          mov       r7, r4
.L17:
.xtaloop 240
.L29:
          ldw       r5, sp[0x1] 
.L42:
          in        r5, res[r5] 
.L43:
          out       res[r9], r4 
.L44:
          in        r8, res[r9] 
          ldw       r6, sp[0x2] 
          mkmsk     r5, 0x1
.L45:
          out       res[r6], r5 
.L46:
          out       res[r10], r8 
          mkmsk     r6, 0x1
.L23:
.xtaloop 319
.L30:
.L47:
          out       res[r9], r4 
.L48:
          in        r8, res[r9] 
          and       r5, r8, r1
          shl       r5, r5, 0x1
          and       r11, r8, r3
          shl       r11, r11, 0x1
          or        r5, r5, r11
          and       r8, r8, r2
          shl       r8, r8, 0x2
          or        r5, r5, r8
.L31:
.L49:
          out       res[r10], r5 
          add       r6, r6, 0x1
.L32:
          lss       r5, r6, r0
          bt        r5, .L23 
.L33:
          ldw       r5, sp[0x2] 
.L50:
          out       res[r5], r4 
          add       r7, r7, 0x1
.L34:
          ldc       r5, 0xf0
          lss       r5, r7, r5
          bt        r5, .L17 
.L35:
          bu        .L12 
.cc_bottom lcd_physical.function
.linkset lcd_physical.maxchanends, lcd_init.maxchanends
.linkset lcd_physical.maxtimers, lcd_init.maxtimers
.linkset .LLNK27, lcd_init.maxthreads - 1
.linkset .LLNK26, 1 + .LLNK27
.linkset .LLNK25, 1 $M .LLNK26
.linkset lcd_physical.maxthreads, .LLNK25
# Thread names for recovering thread graph in linker
.extern stdcore, "a(4:cr)"
.extern __builtin_getid, "f{si}(0)"
          .section .xtaendpointtable,       "", @progbits
.L51:
          .int      .L52-.L51
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000046
          .long    .L50
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000044
          .long    .L49
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000043
          .long    .L48
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000042
          .long    .L47
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003f
          .long    .L46
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003e
          .long    .L45
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003d
          .long    .L44
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003c
          .long    .L43
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003b
          .long    .L42
.cc_bottom lcd_physical.function
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000038
          .long    .L41
.cc_bottom lcd_physical.function
.cc_top delay.function, delay
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000018
          .long    .L2
.cc_bottom delay.function
.cc_top delay.function, delay
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000016
          .long    .L1
.cc_bottom delay.function
.L52:
          .section .xtacalltable,       "", @progbits
.L53:
          .int      .L54-.L53
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000034
          .long    .L40
.cc_bottom lcd_physical.function
.cc_top lcd_init.function, lcd_init
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000002b
          .long    .L8
.cc_bottom lcd_init.function
.cc_top lcd_init.function, lcd_init
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000002a
          .long    .L7
.cc_bottom lcd_init.function
.cc_top lcd_init.function, lcd_init
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000029
          .long    .L6
.cc_bottom lcd_init.function
.cc_top lcd_init.function, lcd_init
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000028
          .long    .L5
.cc_bottom lcd_init.function
.L54:
          .section .xtalabeltable,       "", @progbits
.L55:
          .int      .L56-.L55
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top lcd_physical.function, lcd_physical
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000036
          .int      0x00000036
          .long    .L35
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000039
          .int      0x00000039
          .long    .L34
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000046
          .int      0x00000039
          .long    .L33
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000040
          .int      0x00000040
          .long    .L32
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000044
          .int      0x00000040
          .long    .L31
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000042
          .int      0x00000023
          .long    .L30
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000003b
          .int      0x00000040
          .long    .L29
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000038
          .int      0x00000039
          .long    .L28
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000031
          .int      0x00000048
          .long    .L27
.cc_bottom lcd_physical.function
.cc_top lcd_init.function, lcd_init
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000027
          .int      0x0000002e
          .long    .L4
.cc_bottom lcd_init.function
.cc_top LCD_RGB565_to_RGB666.function, LCD_RGB565_to_RGB666
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x0000001c
          .int      0x00000023
          .long    .L3
.cc_bottom LCD_RGB565_to_RGB666.function
.cc_top delay.function, delay
          .asciiz  "../../src/lcd/lcd_svr.xc"
          .int      0x00000012
          .int      0x00000019
          .long    .L0
.cc_bottom delay.function
.L56:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
