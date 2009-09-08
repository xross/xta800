          .file     "../../src/camera/camera.xc"
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
.extern lb_lineDone, "f{0}(chd)"
.extern lb_getPix, "f{ui}(chd)"
.extern line_buffer, "f{0}(m:chd,m:chd)"
.extern doCamera, "f{0}(m:chd,m:chd,ck,ck,i:p,i:p,o:p,i:p,i:p,o:p)"
.extern sccbRegWrite, "f{ui}(ui,ui)"
          .text
          .align    2
.cc_top initCamInterface.function,initCamInterface
          .align    4
.call initCamInterface, __builtin_set_thread_fast
.call initCamInterface, __builtin_set_clk_src
.call initCamInterface, __builtin_set_port_clk
.call initCamInterface, __builtin_set_port_clk
.call initCamInterface, __builtin_set_port_clk
.call initCamInterface, configure_clock_rate
.call initCamInterface, configure_port_clock_output
.call initCamInterface, __builtin_start_clock
.set __builtin_set_thread_fast, 0
.linkset __builtin_set_thread_fast.locnoside, 0
.linkset __builtin_set_thread_fast.locnochandec, 1
.set __builtin_set_clk_src, 0
.linkset __builtin_set_clk_src.locnoside, 0
.linkset __builtin_set_clk_src.locnochandec, 1
.set __builtin_set_port_clk, 0
.linkset __builtin_set_port_clk.locnoside, 0
.linkset __builtin_set_port_clk.locnochandec, 1
.set __builtin_start_clock, 0
.linkset __builtin_start_clock.locnoside, 0
.linkset __builtin_start_clock.locnochandec, 1
.globl initCamInterface, "f{0}(ck,ck,i:p,o:p,i:p,i:p,i:p)"
.globl initCamInterface.nstackwords
.globl initCamInterface.maxthreads
.globl initCamInterface.maxtimers
.globl initCamInterface.maxchanends
.globl initCamInterface.maxsync
.type  initCamInterface, @function
.linkset initCamInterface.locnoside, 1
.linkset initCamInterface.locnochandec, 1
.linkset .LLNK2, configure_clock_rate.nstackwords $M configure_port_clock_output.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 4
.linkset initCamInterface.nstackwords, .LLNK0
initCamInterface:
          entsp     0x4 
.L0:
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          stw       r6, sp[0x3] 
          mov       r6, r0
          mov       r5, r1
          mov       r4, r3
          ldw       r11, sp[0x5] 
          ldw       r3, sp[0x6] 
          ldw       r1, sp[0x7] 
          setsr     0x80
          ldc       r0, 0x0
          setd      res[r6], r0
          setclk    res[r6], r11
          setclk    res[r3], r6
          setclk    res[r1], r6
          setclk    res[r2], r6
          mov       r0, r5
          ldc       r1, 0x64
          ldc       r2, 0x4
.L1:
          bl        configure_clock_rate 
          mov       r0, r4
          mov       r1, r5
.L2:
          bl        configure_port_clock_output 
          setc      res[r6], 0xf
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          ldw       r6, sp[0x3] 
          retsp     0x4 
.cc_bottom initCamInterface.function
.linkset .LLNK3, configure_clock_rate.maxchanends $M configure_port_clock_output.maxchanends
.linkset initCamInterface.maxchanends, .LLNK3
.linkset .LLNK4, configure_clock_rate.maxtimers $M configure_port_clock_output.maxtimers
.linkset initCamInterface.maxtimers, .LLNK4
.linkset .LLNK8, configure_clock_rate.maxthreads - 1
.linkset .LLNK7, 1 + .LLNK8
.linkset .LLNK6, 1 $M .LLNK7
.linkset .LLNK10, configure_port_clock_output.maxthreads - 1
.linkset .LLNK9, 1 + .LLNK10
.linkset .LLNK5, .LLNK6 $M .LLNK9
.linkset initCamInterface.maxthreads, .LLNK5
.cc_top configCamera.function,configCamera
          .align    4
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.call configCamera, sccbRegWrite
.globl configCamera, "f{ui}(0)"
.globl configCamera.nstackwords
.globl configCamera.maxthreads
.globl configCamera.maxtimers
.globl configCamera.maxchanends
.globl configCamera.maxsync
.type  configCamera, @function
.linkset configCamera.locnoside, 1
.linkset configCamera.locnochandec, 1
.linkset .LLNK18, sccbRegWrite.nstackwords $M sccbRegWrite.nstackwords
.linkset .LLNK17, .LLNK18 $M sccbRegWrite.nstackwords
.linkset .LLNK16, .LLNK17 $M sccbRegWrite.nstackwords
.linkset .LLNK15, .LLNK16 $M sccbRegWrite.nstackwords
.linkset .LLNK14, .LLNK15 $M sccbRegWrite.nstackwords
.linkset .LLNK13, .LLNK14 $M sccbRegWrite.nstackwords
.linkset .LLNK12, .LLNK13 $M .LLNK13
.linkset .LLNK11, .LLNK12 + 1
.linkset configCamera.nstackwords, .LLNK11
configCamera:
          entsp     0x1 
.L3:
          ldc       r0, 0x11
          mkmsk     r1, 0x4
.L4:
          bl        sccbRegWrite 
          ldc       r0, 0x11
          ldc       r1, 0x0
.L5:
          bl        sccbRegWrite 
          ldc       r0, 0x11
          ldc       r1, 0x0
.L6:
          bl        sccbRegWrite 
          ldc       r0, 0x11
          mkmsk     r1, 0x1
.L7:
          bl        sccbRegWrite 
          ldc       r0, 0x14
          ldc       r1, 0x20
.L8:
          bl        sccbRegWrite 
          ldc       r0, 0x28
          ldc       r1, 0x24
.L9:
          bl        sccbRegWrite 
          ldc       r0, 0x12
          ldc       r1, 0x6c
.L10:
          bl        sccbRegWrite 
          retsp     0x1 
.cc_bottom configCamera.function
.linkset .LLNK24, sccbRegWrite.maxchanends $M sccbRegWrite.maxchanends
.linkset .LLNK23, .LLNK24 $M sccbRegWrite.maxchanends
.linkset .LLNK22, .LLNK23 $M sccbRegWrite.maxchanends
.linkset .LLNK21, .LLNK22 $M sccbRegWrite.maxchanends
.linkset .LLNK20, .LLNK21 $M sccbRegWrite.maxchanends
.linkset .LLNK19, .LLNK20 $M sccbRegWrite.maxchanends
.linkset configCamera.maxchanends, .LLNK19
.linkset .LLNK30, sccbRegWrite.maxtimers $M sccbRegWrite.maxtimers
.linkset .LLNK29, .LLNK30 $M sccbRegWrite.maxtimers
.linkset .LLNK28, .LLNK29 $M sccbRegWrite.maxtimers
.linkset .LLNK27, .LLNK28 $M sccbRegWrite.maxtimers
.linkset .LLNK26, .LLNK27 $M sccbRegWrite.maxtimers
.linkset .LLNK25, .LLNK26 $M sccbRegWrite.maxtimers
.linkset configCamera.maxtimers, .LLNK25
.linkset .LLNK39, sccbRegWrite.maxthreads - 1
.linkset .LLNK38, 1 + .LLNK39
.linkset .LLNK37, 1 $M .LLNK38
.linkset .LLNK41, sccbRegWrite.maxthreads - 1
.linkset .LLNK40, 1 + .LLNK41
.linkset .LLNK36, .LLNK37 $M .LLNK40
.linkset .LLNK43, sccbRegWrite.maxthreads - 1
.linkset .LLNK42, 1 + .LLNK43
.linkset .LLNK35, .LLNK36 $M .LLNK42
.linkset .LLNK45, sccbRegWrite.maxthreads - 1
.linkset .LLNK44, 1 + .LLNK45
.linkset .LLNK34, .LLNK35 $M .LLNK44
.linkset .LLNK47, sccbRegWrite.maxthreads - 1
.linkset .LLNK46, 1 + .LLNK47
.linkset .LLNK33, .LLNK34 $M .LLNK46
.linkset .LLNK49, sccbRegWrite.maxthreads - 1
.linkset .LLNK48, 1 + .LLNK49
.linkset .LLNK32, .LLNK33 $M .LLNK48
.linkset .LLNK51, sccbRegWrite.maxthreads - 1
.linkset .LLNK50, 1 + .LLNK51
.linkset .LLNK31, .LLNK32 $M .LLNK50
.linkset configCamera.maxthreads, .LLNK31
.cc_top camLoop.function,camLoop
          .align    4
.call camLoop, __builtin_set_thread_fast
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.call camLoop, __builtin_pins_eq
.set __builtin_pins_eq, 0
.linkset __builtin_pins_eq.locnoside, 0
.linkset __builtin_pins_eq.locnochandec, 1
.globl camLoop, "f{0}(m:chd,m:chd,i:p,i:p,i:p,i:p)"
.globl camLoop.nstackwords
.globl camLoop.maxthreads
.globl camLoop.maxtimers
.globl camLoop.maxchanends
.globl camLoop.maxsync
.type  camLoop, @function
.linkset camLoop.locnoside, 0
.linkset camLoop.locnochandec, 1
.linkset camLoop.nstackwords, 7
camLoop:
          entsp     0x7 
.L31:
          stw       r0, sp[0x0] 
          stw       r1, sp[0x1] 
          mkmsk     r4, 0x1
          setsr     0x80
          ldc       r5, 0x0
          ldw       r1, sp[0x9] 
          setd      res[r1], r5
          ldc       r5, 0x11
          ldw       r1, sp[0x9] 
          setc      res[r1], r5
          ldw       r1, sp[0x9] 
.L47:
          in        r6, res[r1] 
          stw       r4, sp[0x3] 
          stw       r4, sp[0x4] 
          ldw       r1, sp[0x8] 
          setc      res[r1], r5
          ldc       r1, 0x0
          stw       r1, sp[0x5] 
          mov       r1, r4
          setc      res[r3], r5
          mov       r5, r4
          mov       r7, r4
          setc      res[r2], r4
          ldc       r6, 0x0
          stw       r6, sp[0x6] 
.L14:
.L32:
          ldw       r8, sp[0x9] 
          ldw       r6, sp[0x3] 
          setd      res[r8], r6
          ldw       r6, sp[0x9] 
.L48:
          in        r6, res[r6] 
          ldc       r6, 0x0
          stw       r6, sp[0x2] 
.L19:
.xtaloop 240
.L33:
          ldc       r8, 0x0
          ldw       r9, sp[0x8] 
          ldw       r6, sp[0x4] 
          setd      res[r9], r6
          ldw       r6, sp[0x8] 
.L49:
          in        r6, res[r6] 
          ldw       r9, sp[0x1] 
          mkmsk     r6, 0x8
.L50:
          out       res[r9], r6 
          ldc       r9, 0x0
.L25:
.xtaloop 320
.L34:
          setd      res[r3], r1
.L51:
          in        r6, res[r3] 
          setd      res[r3], r5
.L52:
          in        r6, res[r3] 
          setd      res[r3], r7
.L53:
          in        r6, res[r3] 
.L54:
          in        r6, res[r2] 
          bt        r4, .L29 
.L36:
          ldc       r10, 0xb
          ashr      r8, r6, r10
.L28:
.L37:
          mkmsk     r11, 0x8
          and       r6, r6, r11
          ashr      r6, r6, 0x2
          shl       r10, r0, r10
          shl       r6, r6, 0x5
          or        r6, r10, r6
          or        r6, r6, r8
          eq        r4, r4, 0x0
          ldw       r10, sp[0x0] 
.L55:
          out       res[r10], r6 
          add       r9, r9, 0x1
.L38:
          ldc       r6, 0x140
          lss       r6, r9, r6
          bt        r6, .L25 
.L39:
          ldw       r8, sp[0x8] 
          ldw       r6, sp[0x5] 
          setd      res[r8], r6
          ldw       r6, sp[0x8] 
.L56:
          in        r6, res[r6] 
          ldw       r6, sp[0x2] 
          add       r6, r6, 0x1
          stw       r6, sp[0x2] 
.L40:
          mov       r8, r6
          ldc       r6, 0xf0
          lss       r6, r8, r6
          bt        r6, .L19 
.L41:
          ldw       r8, sp[0x9] 
          ldw       r6, sp[0x6] 
          setd      res[r8], r6
          ldw       r6, sp[0x9] 
.L57:
          in        r6, res[r6] 
.L42:
          bu        .L14 
.L29:
.L35:
          ldc       r10, 0xb
          ashr      r0, r6, r10
          bu        .L28 
.cc_bottom camLoop.function
.linkset camLoop.maxchanends, 0
.linkset camLoop.maxtimers, 0
.linkset camLoop.maxthreads, 1
.cc_top doCamera.function,doCamera
          .align    4
.call doCamera, printstrln
          .section .cp.string,     "ac", @progbits
.cc_top .LC0.string, .LC0
          .align    4
.LC0:
          .ascii    "XMOS Camera...\0"
.cc_bottom .LC0.string
.call doCamera, printstr
.cc_top .LC1.string, .LC1
          .align    4
.LC1:
          .ascii    "Initialising interface...\0"
.cc_bottom .LC1.string
.call doCamera, initCamInterface
.call doCamera, printstrln
.cc_top .LC2.string, .LC2
          .align    4
.LC2:
          .ascii    "DONE\0"
.cc_bottom .LC2.string
.call doCamera, __builtin_timer_after
.call doCamera, configCamera
.call doCamera, printstr
.cc_top .LC3.string, .LC3
          .align    4
.LC3:
          .ascii    "Config Returned: \0"
.cc_bottom .LC3.string
.call doCamera, printhexln
.call doCamera, printstr
.cc_top .LC4.string, .LC4
          .align    4
.LC4:
          .ascii    "Checking clock...\0"
.cc_bottom .LC4.string
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, printstrln
.cc_top .LC5.string, .LC5
          .align    4
.LC5:
          .ascii    "OK\0"
.cc_bottom .LC5.string
.call doCamera, printstr
.cc_top .LC6.string, .LC6
          .align    4
.LC6:
          .ascii    "Checking HREF...\0"
.cc_bottom .LC6.string
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, printstrln
.cc_top .LC7.string, .LC7
          .align    4
.LC7:
          .ascii    "OK\0"
.cc_bottom .LC7.string
.call doCamera, printstr
.cc_top .LC8.string, .LC8
          .align    4
.LC8:
          .ascii    "Checking clock...\0"
.cc_bottom .LC8.string
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, __builtin_pins_eq
.call doCamera, printstrln
.cc_top .LC9.string, .LC9
          .align    4
.LC9:
          .ascii    "OK\0"
.cc_bottom .LC9.string
.call doCamera, printstrln
.cc_top .LC10.string, .LC10
          .align    4
.LC10:
          .ascii    "Camera running...OK\0"
.cc_bottom .LC10.string
.call doCamera, camLoop
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
          .text
.globl doCamera, "f{0}(m:chd,m:chd,ck,ck,i:p,i:p,o:p,i:p,i:p,o:p)"
.globl doCamera.nstackwords
.globl doCamera.maxthreads
.globl doCamera.maxtimers
.globl doCamera.maxchanends
.globl doCamera.maxsync
.type  doCamera, @function
.linkset doCamera.locnoside, 0
.linkset doCamera.locnochandec, 1
.linkset .LLNK67, printstrln.nstackwords $M printstr.nstackwords
.linkset .LLNK66, .LLNK67 $M initCamInterface.nstackwords
.linkset .LLNK65, .LLNK66 $M printstrln.nstackwords
.linkset .LLNK64, .LLNK65 $M configCamera.nstackwords
.linkset .LLNK63, .LLNK64 $M printstr.nstackwords
.linkset .LLNK62, .LLNK63 $M printhexln.nstackwords
.linkset .LLNK61, .LLNK62 $M printstr.nstackwords
.linkset .LLNK60, .LLNK61 $M printstrln.nstackwords
.linkset .LLNK59, .LLNK60 $M printstr.nstackwords
.linkset .LLNK58, .LLNK59 $M printstrln.nstackwords
.linkset .LLNK57, .LLNK58 $M printstr.nstackwords
.linkset .LLNK56, .LLNK57 $M printstrln.nstackwords
.linkset .LLNK55, .LLNK56 $M printstrln.nstackwords
.linkset .LLNK54, .LLNK55 $M camLoop.nstackwords
.linkset .LLNK53, .LLNK54 $M .LLNK54
.linkset .LLNK52, .LLNK53 + 14
.linkset doCamera.nstackwords, .LLNK52
doCamera:
          entsp     0xe 
.L58:
          stw       r4, sp[0x7] 
          stw       r5, sp[0x8] 
          stw       r6, sp[0x9] 
          stw       r7, sp[0xa] 
          stw       r8, sp[0xb] 
          stw       r9, sp[0xc] 
          stw       r10, sp[0xd] 
          stw       r0, sp[0x4] 
          stw       r1, sp[0x5] 
          stw       r2, sp[0x6] 
          mov       r5, r3
          ldw       r9, sp[0x10] 
          ldw       r4, sp[0x11] 
          ldw       r8, sp[0x12] 
          ldw       r7, sp[0x13] 
          ldw       r10, sp[0x14] 
          getr      r6, 0x1
          ldaw      r11, cp[.LC0] 
          mov       r0, r11
          mkmsk     r1, 0x4
.L59:
          bl        printstrln 
          ldaw      r11, cp[.LC1] 
          mov       r0, r11
          ldc       r1, 0x1a
.L60:
          bl        printstr 
          ldw       r0, sp[0x6] 
          mov       r1, r5
          ldw       r2, sp[0xf] 
          mov       r3, r4
          stw       r9, sp[0x1] 
          stw       r8, sp[0x2] 
          stw       r7, sp[0x3] 
.L61:
          bl        initCamInterface 
          ldaw      r11, cp[.LC2] 
          mov       r0, r11
          ldc       r1, 0x5
.L62:
          bl        printstrln 
          mkmsk     r4, 0x1
.L63:
          out       res[r10], r4 
          setc      res[r6], r4
.L64:
          in        r0, res[r6] 
          ldw       r11, cp[.LC11]
          .section .cp.const4,     "acM", @progbits, 4
.LC11:
          .align    4
          .int      0x00989680
          .text
          add       r0, r0, r11
          setd      res[r6], r0
          setc      res[r6], 0x9
.L65:
          in        r0, res[r6] 
          ldc       r5, 0x0
.L66:
          out       res[r10], r5 
.L67:
          bl        configCamera 
          mov       r10, r0
          ldaw      r11, cp[.LC3] 
          mov       r0, r11
          ldc       r1, 0x12
.L68:
          bl        printstr 
          mov       r0, r10
.L69:
          bl        printhexln 
          ldaw      r11, cp[.LC4] 
          mov       r0, r11
          ldc       r1, 0x12
.L70:
          bl        printstr 
          setd      res[r9], r5
          ldc       r10, 0x11
          setc      res[r9], r10
.L71:
          in        r0, res[r9] 
          setd      res[r9], r4
.L72:
          in        r0, res[r9] 
          setd      res[r9], r5
.L73:
          in        r0, res[r9] 
          setd      res[r9], r4
.L74:
          in        r0, res[r9] 
          setd      res[r9], r5
.L75:
          in        r0, res[r9] 
          ldaw      r11, cp[.LC5] 
          mov       r0, r11
          mkmsk     r1, 0x2
.L76:
          bl        printstrln 
          ldaw      r11, cp[.LC6] 
          mov       r0, r11
          ldc       r1, 0x11
.L77:
          bl        printstr 
          setd      res[r8], r5
          setc      res[r8], r10
.L78:
          in        r0, res[r8] 
          setd      res[r8], r4
.L79:
          in        r0, res[r8] 
          setd      res[r8], r5
.L80:
          in        r0, res[r8] 
          setd      res[r8], r4
.L81:
          in        r0, res[r8] 
          setd      res[r8], r5
.L82:
          in        r0, res[r8] 
          ldaw      r11, cp[.LC7] 
          mov       r0, r11
          mkmsk     r1, 0x2
.L83:
          bl        printstrln 
          ldaw      r11, cp[.LC8] 
          mov       r0, r11
          ldc       r1, 0x12
.L84:
          bl        printstr 
          setd      res[r7], r5
          setc      res[r7], r10
.L85:
          in        r0, res[r7] 
          setd      res[r7], r4
.L86:
          in        r0, res[r7] 
          setd      res[r7], r5
.L87:
          in        r0, res[r7] 
          setd      res[r7], r4
.L88:
          in        r0, res[r7] 
          setd      res[r7], r5
.L89:
          in        r0, res[r7] 
          ldaw      r11, cp[.LC9] 
          mov       r0, r11
          mkmsk     r1, 0x2
.L90:
          bl        printstrln 
          ldaw      r11, cp[.LC10] 
          mov       r0, r11
          ldc       r1, 0x14
.L91:
          bl        printstrln 
          ldw       r0, sp[0x4] 
          ldw       r1, sp[0x5] 
          ldw       r2, sp[0xf] 
          mov       r3, r9
          stw       r8, sp[0x1] 
          stw       r7, sp[0x2] 
.L92:
          bl        camLoop 
          freer     res[r6]
          ldw       r4, sp[0x7] 
          ldw       r5, sp[0x8] 
          ldw       r6, sp[0x9] 
          ldw       r7, sp[0xa] 
          ldw       r8, sp[0xb] 
          ldw       r9, sp[0xc] 
          ldw       r10, sp[0xd] 
          retsp     0xe 
.cc_bottom doCamera.function
.linkset .LLNK81, printstrln.maxchanends $M printstr.maxchanends
.linkset .LLNK80, .LLNK81 $M initCamInterface.maxchanends
.linkset .LLNK79, .LLNK80 $M printstrln.maxchanends
.linkset .LLNK78, .LLNK79 $M configCamera.maxchanends
.linkset .LLNK77, .LLNK78 $M printstr.maxchanends
.linkset .LLNK76, .LLNK77 $M printhexln.maxchanends
.linkset .LLNK75, .LLNK76 $M printstr.maxchanends
.linkset .LLNK74, .LLNK75 $M printstrln.maxchanends
.linkset .LLNK73, .LLNK74 $M printstr.maxchanends
.linkset .LLNK72, .LLNK73 $M printstrln.maxchanends
.linkset .LLNK71, .LLNK72 $M printstr.maxchanends
.linkset .LLNK70, .LLNK71 $M printstrln.maxchanends
.linkset .LLNK69, .LLNK70 $M printstrln.maxchanends
.linkset .LLNK68, .LLNK69 $M camLoop.maxchanends
.linkset doCamera.maxchanends, .LLNK68
.linkset .LLNK97, 1 + printstrln.maxtimers
.linkset .LLNK96, 1 $M .LLNK97
.linkset .LLNK98, 1 + printstr.maxtimers
.linkset .LLNK95, .LLNK96 $M .LLNK98
.linkset .LLNK99, 1 + initCamInterface.maxtimers
.linkset .LLNK94, .LLNK95 $M .LLNK99
.linkset .LLNK100, 1 + printstrln.maxtimers
.linkset .LLNK93, .LLNK94 $M .LLNK100
.linkset .LLNK101, 1 + configCamera.maxtimers
.linkset .LLNK92, .LLNK93 $M .LLNK101
.linkset .LLNK102, 1 + printstr.maxtimers
.linkset .LLNK91, .LLNK92 $M .LLNK102
.linkset .LLNK103, 1 + printhexln.maxtimers
.linkset .LLNK90, .LLNK91 $M .LLNK103
.linkset .LLNK104, 1 + printstr.maxtimers
.linkset .LLNK89, .LLNK90 $M .LLNK104
.linkset .LLNK105, 1 + printstrln.maxtimers
.linkset .LLNK88, .LLNK89 $M .LLNK105
.linkset .LLNK106, 1 + printstr.maxtimers
.linkset .LLNK87, .LLNK88 $M .LLNK106
.linkset .LLNK107, 1 + printstrln.maxtimers
.linkset .LLNK86, .LLNK87 $M .LLNK107
.linkset .LLNK108, 1 + printstr.maxtimers
.linkset .LLNK85, .LLNK86 $M .LLNK108
.linkset .LLNK109, 1 + printstrln.maxtimers
.linkset .LLNK84, .LLNK85 $M .LLNK109
.linkset .LLNK110, 1 + printstrln.maxtimers
.linkset .LLNK83, .LLNK84 $M .LLNK110
.linkset .LLNK111, 1 + camLoop.maxtimers
.linkset .LLNK82, .LLNK83 $M .LLNK111
.linkset doCamera.maxtimers, .LLNK82
.linkset .LLNK128, printstrln.maxthreads - 1
.linkset .LLNK127, 1 + .LLNK128
.linkset .LLNK126, 1 $M .LLNK127
.linkset .LLNK130, printstr.maxthreads - 1
.linkset .LLNK129, 1 + .LLNK130
.linkset .LLNK125, .LLNK126 $M .LLNK129
.linkset .LLNK132, initCamInterface.maxthreads - 1
.linkset .LLNK131, 1 + .LLNK132
.linkset .LLNK124, .LLNK125 $M .LLNK131
.linkset .LLNK134, printstrln.maxthreads - 1
.linkset .LLNK133, 1 + .LLNK134
.linkset .LLNK123, .LLNK124 $M .LLNK133
.linkset .LLNK136, configCamera.maxthreads - 1
.linkset .LLNK135, 1 + .LLNK136
.linkset .LLNK122, .LLNK123 $M .LLNK135
.linkset .LLNK138, printstr.maxthreads - 1
.linkset .LLNK137, 1 + .LLNK138
.linkset .LLNK121, .LLNK122 $M .LLNK137
.linkset .LLNK140, printhexln.maxthreads - 1
.linkset .LLNK139, 1 + .LLNK140
.linkset .LLNK120, .LLNK121 $M .LLNK139
.linkset .LLNK142, printstr.maxthreads - 1
.linkset .LLNK141, 1 + .LLNK142
.linkset .LLNK119, .LLNK120 $M .LLNK141
.linkset .LLNK144, printstrln.maxthreads - 1
.linkset .LLNK143, 1 + .LLNK144
.linkset .LLNK118, .LLNK119 $M .LLNK143
.linkset .LLNK146, printstr.maxthreads - 1
.linkset .LLNK145, 1 + .LLNK146
.linkset .LLNK117, .LLNK118 $M .LLNK145
.linkset .LLNK148, printstrln.maxthreads - 1
.linkset .LLNK147, 1 + .LLNK148
.linkset .LLNK116, .LLNK117 $M .LLNK147
.linkset .LLNK150, printstr.maxthreads - 1
.linkset .LLNK149, 1 + .LLNK150
.linkset .LLNK115, .LLNK116 $M .LLNK149
.linkset .LLNK152, printstrln.maxthreads - 1
.linkset .LLNK151, 1 + .LLNK152
.linkset .LLNK114, .LLNK115 $M .LLNK151
.linkset .LLNK154, printstrln.maxthreads - 1
.linkset .LLNK153, 1 + .LLNK154
.linkset .LLNK113, .LLNK114 $M .LLNK153
.linkset .LLNK156, camLoop.maxthreads - 1
.linkset .LLNK155, 1 + .LLNK156
.linkset .LLNK112, .LLNK113 $M .LLNK155
.linkset doCamera.maxthreads, .LLNK112
# Thread names for recovering thread graph in linker
.extern __builtin_getid, "f{si}(0)"
          .section .xtaendpointtable,       "", @progbits
.L93:
          .int      .L94-.L93
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b9
          .long    .L89
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b8
          .long    .L88
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b7
          .long    .L87
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b6
          .long    .L86
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b5
          .long    .L85
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b1
          .long    .L82
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b0
          .long    .L81
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000af
          .long    .L80
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000ae
          .long    .L79
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000ad
          .long    .L78
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a9
          .long    .L75
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a8
          .long    .L74
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a7
          .long    .L73
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a6
          .long    .L72
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a5
          .long    .L71
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000009c
          .long    .L66
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000009b
          .long    .L65
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000009a
          .long    .L64
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000099
          .long    .L63
.cc_bottom doCamera.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000087
          .long    .L57
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000083
          .long    .L56
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000080
          .long    .L55
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000006b
          .long    .L54
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000006a
          .long    .L53
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000069
          .long    .L52
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000068
          .long    .L51
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000062
          .long    .L50
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000061
          .long    .L49
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000005d
          .long    .L48
.cc_bottom camLoop.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000059
          .long    .L47
.cc_bottom camLoop.function
.L94:
          .section .xtacalltable,       "", @progbits
.L95:
          .int      .L96-.L95
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000be
          .long    .L92
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000bc
          .long    .L91
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000ba
          .long    .L90
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b4
          .long    .L84
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000b2
          .long    .L83
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000ac
          .long    .L77
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000aa
          .long    .L76
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a4
          .long    .L70
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x000000a0
          .long    .L69
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000009f
          .long    .L68
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000009e
          .long    .L67
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000096
          .long    .L62
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000095
          .long    .L61
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000094
          .long    .L60
.cc_bottom doCamera.function
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000093
          .long    .L59
.cc_bottom doCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000048
          .long    .L10
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000043
          .long    .L9
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000038
          .long    .L8
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000033
          .long    .L7
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000032
          .long    .L6
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000031
          .long    .L5
.cc_bottom configCamera.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000030
          .long    .L4
.cc_bottom configCamera.function
.cc_top initCamInterface.function, initCamInterface
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000023
          .long    .L2
.cc_bottom initCamInterface.function
.cc_top initCamInterface.function, initCamInterface
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000022
          .long    .L1
.cc_bottom initCamInterface.function
.L96:
          .section .xtalabeltable,       "", @progbits
.L97:
          .int      .L98-.L97
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top doCamera.function, doCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000008e
          .int      0x000000c0
          .long    .L58
.cc_bottom doCamera.function
.cc_top camLoop.function, camLoop
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000005a
          .int      0x0000005a
          .long    .L42
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L41
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000005e
          .int      0x0000005e
          .long    .L40
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000083
          .int      0x0000005e
          .long    .L39
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L38
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000077
          .int      0x00000063
          .long    .L37
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L36
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000072
          .int      0x00000072
          .long    .L35
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000068
          .int      0x00000070
          .long    .L34
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000060
          .int      0x00000063
          .long    .L33
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000005d
          .int      0x0000005e
          .long    .L32
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000050
          .int      0x00000089
          .long    .L31
.cc_bottom camLoop.function
.cc_top configCamera.function, configCamera
          .asciiz  "../../src/camera/camera.xc"
          .int      0x0000002b
          .int      0x0000004b
          .long    .L3
.cc_bottom configCamera.function
.cc_top initCamInterface.function, initCamInterface
          .asciiz  "../../src/camera/camera.xc"
          .int      0x00000016
          .int      0x00000028
          .long    .L0
.cc_bottom initCamInterface.function
.L98:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
