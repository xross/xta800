          .file     "../../src/buffer/line_buf.xc"
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
          .text
          .align    2
.cc_top init.function,init
          .align    4
.globl init, "f{si,si,si,si}(m:chd,&(a(3:a(640:ss))))"
.globl init.nstackwords
.globl init.maxthreads
.globl init.maxtimers
.globl init.maxchanends
.globl init.maxsync
.type  init, @function
.linkset init.locnoside, 0
.linkset init.locnochandec, 1
.linkset init.nstackwords, 1
init:
          entsp     0x1 
.L6:
          stw       r4, sp[0x0] 
          ldc       r2, 0x0
          ldc       r4, 0x280
.L3:
.xtaloop 640
.L7:
          lsu       r3, r2, r4
          ecallf    r3
.L11:
          int       r11, res[r0] 
          shl       r11, r11, 0x8
          int       r3, res[r0] 
          or        r11, r11, r3
          st16      r11, r1[r2] 
          add       r2, r2, 0x1
.L8:
          lss       r3, r2, r4
          bt        r3, .L3 
.L9:
          mkmsk     r0, 0x1
          ldc       r1, 0x2
          mkmsk     r2, 0x2
          ldc       r3, 0x0
          ldw       r4, sp[0x0] 
          retsp     0x1 
.cc_bottom init.function
.linkset init.maxchanends, 0
.linkset init.maxtimers, 0
.linkset init.maxthreads, 1
.cc_top producer_handler.function,producer_handler
          .align    4
.globl producer_handler, "f{si,si,si}(si,si,si)"
.globl producer_handler.nstackwords
.globl producer_handler.maxthreads
.globl producer_handler.maxtimers
.globl producer_handler.maxchanends
.globl producer_handler.maxsync
.type  producer_handler, @function
.linkset producer_handler.locnoside, 1
.linkset producer_handler.locnochandec, 1
.linkset producer_handler.nstackwords, 0
producer_handler:
.L15:
          bt        r2, .L13 
.L17:
          mov       r2, r0
          mov       r0, r1
          ldc       r1, 0x0
.L12:
.L18:
          retsp     0x0 
.L13:
.L16:
          mov       r3, r2
          mov       r2, r0
          mov       r0, r3
          bu        .L12 
.cc_bottom producer_handler.function
.linkset producer_handler.maxchanends, 0
.linkset producer_handler.maxtimers, 0
.linkset producer_handler.maxthreads, 1
.cc_top consumer_handler.function,consumer_handler
          .align    4
.globl consumer_handler, "f{si,si,si}(si,si,si)"
.globl consumer_handler.nstackwords
.globl consumer_handler.maxthreads
.globl consumer_handler.maxtimers
.globl consumer_handler.maxchanends
.globl consumer_handler.maxsync
.type  consumer_handler, @function
.linkset consumer_handler.locnoside, 1
.linkset consumer_handler.locnochandec, 1
.linkset consumer_handler.nstackwords, 0
consumer_handler:
.L21:
          bt        r2, .L20 
.L19:
.L23:
          retsp     0x0 
.L20:
.L22:
          mov       r1, r0
          mov       r0, r2
          ldc       r2, 0x0
          bu        .L19 
.cc_bottom consumer_handler.function
.linkset consumer_handler.maxchanends, 0
.linkset consumer_handler.maxtimers, 0
.linkset consumer_handler.maxthreads, 1
.cc_top line_buffer.function,line_buffer
          .align    4
.call line_buffer, producer_handler
.globl line_buffer, "f{0}(m:chd,m:chd)"
.globl line_buffer.nstackwords
.globl line_buffer.maxthreads
.globl line_buffer.maxtimers
.globl line_buffer.maxchanends
.globl line_buffer.maxsync
.type  line_buffer, @function
.linkset line_buffer.locnoside, 0
.linkset line_buffer.locnochandec, 1
.linkset .LLNK1, producer_handler.nstackwords $M producer_handler.nstackwords
.linkset .LLNK0, .LLNK1 + 961
.linkset line_buffer.nstackwords, .LLNK0
line_buffer:
          entsp     0x3c1 
.L52:
          mov       r8, r0
          mov       r7, r1
          ldaw      r1, sp[0x1] 
          ldc       r0, 0x0
.L27:
.xtaloop 640
.L53:
          ldc       r2, 0x280
          lsu       r2, r0, r2
          ecallf    r2
.L70:
          int       r3, res[r8] 
          shl       r3, r3, 0x8
          int       r2, res[r8] 
          or        r3, r3, r2
          st16      r3, r1[r0] 
          add       r0, r0, 0x1
.L54:
          ldc       r2, 0x280
          lss       r2, r0, r2
          bt        r2, .L27 
.L55:
.L56:
          mkmsk     r6, 0x1
          ldc       r0, 0x2
          mkmsk     r1, 0x2
          ldc       r2, 0x0
          ldc       r10, 0x0
          ldc       r5, 0x0
          ldap      r11, .L39
          setv      res[r7], r11
          ldap      r11, .L47
          setv      res[r8], r11
          ldaw      r4, sp[0x1] 
.L34:
.L58:
.L57:
          clre      
          eeu       res[r7]
          eeu       res[r8]
.xtabranch .L47,.L39
          waiteu    
.L39:
.L59:
.L71:
          in        r3, res[r7] 
          sub       r9, r6, 0x1
          ldc       r3, 0x500
          mul       r3, r9, r3
          add       r3, r4, r3
          ld16s     r3, r3[r5] 
.L72:
          out       res[r7], r3 
          add       r3, r5, 0x1
          mov       r5, r3
          ldc       r9, 0x280
          eq        r3, r3, r9
          bt        r3, .L43 
.L35:
.L66:
          bu        .L34 
.L43:
.L60:
          mov       r3, r1
          mov       r1, r6
          bt        r2, .L45 
.L62:
.L46:
.L63:
          mov       r6, r1
          mov       r1, r3
          ldc       r5, 0x0
          bu        .L35 
.L45:
.L61:
          mov       r3, r6
          mov       r1, r2
          ldc       r2, 0x0
          bu        .L46 
.L47:
.L64:
.L73:
          in        r11, res[r8] 
          sub       r9, r0, 0x1
          ldc       r3, 0x500
          mul       r3, r9, r3
          add       r3, r4, r3
          st16      r11, r3[r10] 
          add       r3, r10, 0x1
          mov       r10, r3
          ldc       r9, 0x280
          eq        r3, r3, r9
          bf        r3, .L35 
.L65:
.L74:
          bl        producer_handler 
          ldc       r10, 0x0
          bu        .L35 
.L36:
.cc_bottom line_buffer.function
.linkset line_buffer.maxchanends, producer_handler.maxchanends
.linkset line_buffer.maxtimers, producer_handler.maxtimers
.linkset .LLNK4, producer_handler.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset line_buffer.maxthreads, .LLNK2
# Thread names for recovering thread graph in linker
.extern __builtin_getid, "f{si}(0)"
          .section .xtaendpointtable,       "", @progbits
.L75:
          .int      .L76-.L75
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000067
          .long    .L73
.cc_bottom line_buffer.function
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000005e
          .long    .L72
.cc_bottom line_buffer.function
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000005d
          .long    .L71
.cc_bottom line_buffer.function
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001a
          .long    .L70
.cc_bottom line_buffer.function
.cc_top init.function, init
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001a
          .long    .L11
.cc_bottom init.function
.L76:
          .section .xtacalltable,       "", @progbits
.L77:
          .int      .L78-.L77
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000006d
          .long    .L74
.cc_bottom line_buffer.function
.L78:
          .section .xtalabeltable,       "", @progbits
.L79:
          .int      .L80-.L79
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top line_buffer.function, line_buffer
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L66
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000006d
          .int      0x0000006e
          .long    .L65
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000067
          .int      0x0000006a
          .long    .L64
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000063
          .int      0x00000064
          .long    .L63
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L62
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000040
          .int      0x00000042
          .long    .L61
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000063
          .int      0x0000003e
          .long    .L60
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000005d
          .int      0x00000060
          .long    .L59
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000005b
          .int      0x0000005b
          .long    .L58
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000005b
          .int      0x0000005b
          .long    .L57
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000054
          .int      0x00000072
          .long    .L56
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000020
          .int      0x00000020
          .long    .L55
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001a
          .int      0x0000001a
          .long    .L54
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001c
          .int      0x0000001a
          .long    .L53
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000004d
          .int      0x0000001a
          .long    .L52
.cc_bottom line_buffer.function
.cc_top consumer_handler.function, consumer_handler
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L23
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000040
          .int      0x00000042
          .long    .L22
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000003d
          .int      0x0000003e
          .long    .L21
.cc_bottom consumer_handler.function
.cc_top producer_handler.function, producer_handler
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000036
          .int      0x00000036
          .long    .L18
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000031
          .int      0x00000033
          .long    .L17
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000002b
          .int      0x0000002d
          .long    .L16
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000027
          .int      0x00000028
          .long    .L15
.cc_bottom producer_handler.function
.cc_top init.function, init
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000020
          .int      0x00000020
          .long    .L9
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001a
          .int      0x0000001a
          .long    .L8
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x0000001c
          .int      0x0000001a
          .long    .L7
          .asciiz  "../../src/buffer/line_buf.xc"
          .int      0x00000018
          .int      0x0000001a
          .long    .L6
.cc_bottom init.function
.L80:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
