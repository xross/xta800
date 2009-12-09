.file     "in_main.xc"
.text

.align 2
.globl _start
_start:
.xtathreadstart
  bl main
  clre
.xtathreadstop
_stop:
.xtabranch _stop
  waiteu

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
.extern sext, "f{si}(ui,ui)"
.extern zext, "f{ui}(ui,ui)"
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
.extern read, "f{si}(0)"
.extern func1, "f{si}(0)"
.extern func2, "f{si}(0)"
          .text
          .align    2
.cc_top main.function,main
          .align    4
.call main, func1
.call main, func2
.call main, read
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 1
.linkset .LLNK3, func1.nstackwords $M func2.nstackwords
.linkset .LLNK2, .LLNK3 $M read.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 1
.linkset main.nstackwords, .LLNK0
main:
          entsp     0x1 
.L3:
.xtacall "func1", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail2", "in_main.xc", 9
          bl        func1 
.L0:
.L1:
.xtacall "func2", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail2", "in_main.xc", 11
.L4:
          bl        func2 
.L2:
.xtacall "read3", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail2", "in_main.xc", 13
.L5:
          bl        read 
          retsp     0x1 
.cc_bottom main.function
.linkset .LLNK5, func1.maxchanends $M func2.maxchanends
.linkset .LLNK4, .LLNK5 $M read.maxchanends
.linkset main.maxchanends, .LLNK4
.linkset .LLNK7, func1.maxtimers $M func2.maxtimers
.linkset .LLNK6, .LLNK7 $M read.maxtimers
.linkset main.maxtimers, .LLNK6
.linkset .LLNK12, func1.maxthreads - 1
.linkset .LLNK11, 1 + .LLNK12
.linkset .LLNK10, 1 $M .LLNK11
.linkset .LLNK14, func2.maxthreads - 1
.linkset .LLNK13, 1 + .LLNK14
.linkset .LLNK9, .LLNK10 $M .LLNK13
.linkset .LLNK16, read.maxthreads - 1
.linkset .LLNK15, 1 + .LLNK16
.linkset .LLNK8, .LLNK9 $M .LLNK15
.linkset main.maxthreads, .LLNK8
# Thread names for recovering thread graph in linker
.extern __builtin_getid, "f{si}(0)"
.extern __builtin_getid, "f{si}(0)"
          .section .xtacalltable,       "", @progbits
.L6:
          .int      .L7-.L6
          .int      0x00000000
          .asciiz   "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail2"
.cc_top main.function, main
          .asciiz  "in_main.xc"
          .int      0x0000000e
          .long    .L5
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "in_main.xc"
          .int      0x0000000c
          .long    .L4
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "in_main.xc"
          .int      0x0000000a
          .long    .L3
.cc_bottom main.function
.L7:
          .section .xtalabeltable,       "", @progbits
.L8:
          .int      .L9-.L8
          .int      0x00000000
          .asciiz   "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail2"
.cc_top main.function, main
          .asciiz  "in_main.xc"
          .int      0x0000000e
          .int      0x0000000e
          .long    .L2
          .asciiz  "in_main.xc"
          .int      0x0000000c
          .int      0x0000000c
          .long    .L1
          .asciiz  "in_main.xc"
          .int      0x0000000a
          .int      0x0000000a
          .long    .L0
.cc_bottom main.function
.L9:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
