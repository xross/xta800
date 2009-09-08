          .file     "../../src/./main.xc"
          .section .netinfo,       "", @netinfo
          .int      0x1eaba15c
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
.extern bitrev, "f{ui}(ui)"
.extern byterev, "f{ui}(ui)"
.extern clz, "f{si}(ui)"
.extern lcd_physical, "f{0}(m:chd,m:chd,o:p,o:p,bo:p:32,ck)"
.globl p_lcd_dtm.info, "o:p"
p_lcd_dtm.info:
          .int      0x00010000
          .long stdcore + 12
.globl p_lcd_clk.info, "o:p"
p_lcd_clk.info:
          .int      0x00010200
          .long stdcore + 12
.globl p_lcd_rgb.info, "bo:p:32"
p_lcd_rgb.info:
          .int      0x00200000
          .long stdcore + 12
.globl clk_lcd.info, "ck"
clk_lcd.info:
          .int      0x00000106
          .long stdcore + 12
.globl p_cam_rst.info, "o:p"
p_cam_rst.info:
          .int      0x00010000
          .long stdcore + 4
.globl HREF_port.info, "i:p"
HREF_port.info:
          .int      0x00010400
          .long stdcore + 4
.globl VSYNC_port.info, "i:p"
VSYNC_port.info:
          .int      0x00010700
          .long stdcore + 4
.globl PCLK_port.info, "i:p"
PCLK_port.info:
          .int      0x00010500
          .long stdcore + 4
.globl EXCLK_port.info, "o:p"
EXCLK_port.info:
          .int      0x00010a00
          .long stdcore + 4
.globl Y_UV_port.info, "i:p"
Y_UV_port.info:
          .int      0x00100000
          .long stdcore + 4
.globl p_iic_sda.info, "p"
p_iic_sda.info:
          .int      0x00010100
          .long stdcore + 4
.globl p_iic_scl.info, "p"
p_iic_scl.info:
          .int      0x00010600
          .long stdcore + 4
.globl clk.info, "ck"
clk.info:
          .int      0x00000306
          .long stdcore + 4
.globl clk2.info, "ck"
clk2.info:
          .int      0x00000206
          .long stdcore + 4
main.parinfo.debugstring0:
.asciiz ""
main.parinfo.debugstring1:
.asciiz ""
.cc_top main.parinfo.cc, main.parinfo
.globl main.parinfo, "pi"
.type  main.parinfo, @object
main.parinfo:
          .int      0x00000004
          .long __main_default_network
          .long main.parinfo.debugstring0
          .long main.parinfo.debugstring1
          .int      0x00000003
          .int      0x00000000
          .int      $N __main_xm_0
          .long stdcore + 4
          .int      0x00000002
          .int      0x00000000
          .int      0x00000001
          .int      $N __main_xm_1
          .long stdcore + 4
          .int      0x00000002
          .int      0x00000002
          .int      0x00000001
          .int      $N __main_xm_2
          .long stdcore + 12
          .int      0x00000002
          .int      0x00000000
          .int      0x00000002
.cc_bottom main.parinfo.cc
          .text
          .align    2
.cc_top __main_xm_2.function,__main_xm_2
          .align    4
.call __main_xm_2, lcd_physical
.globpassesref lcd_physical, p_lcd_dtm, "../../src/./main.xc:69: error: `__main_xm_2' makes alias of global 'p_lcd_dtm' in call to `lcd_physical'"
.globwrite __main_xm_2, p_lcd_dtm, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globread __main_xm_2, p_lcd_dtm, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globpassesref lcd_physical, p_lcd_clk, "../../src/./main.xc:69: error: `__main_xm_2' makes alias of global 'p_lcd_clk' in call to `lcd_physical'"
.globwrite __main_xm_2, p_lcd_clk, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globread __main_xm_2, p_lcd_clk, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globpassesref lcd_physical, p_lcd_rgb, "../../src/./main.xc:69: error: `__main_xm_2' makes alias of global 'p_lcd_rgb' in call to `lcd_physical'"
.globwrite __main_xm_2, p_lcd_rgb, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globread __main_xm_2, p_lcd_rgb, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globpassesref lcd_physical, clk_lcd, "../../src/./main.xc:69: error: `__main_xm_2' makes alias of global 'clk_lcd' in call to `lcd_physical'"
.globwrite __main_xm_2, clk_lcd, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globread __main_xm_2, clk_lcd, 0, 4, "../../src/./main.xc:69: error: previously used here (bytes 0..4)"
.globl __main_xm_2, "f{0}(m:chd,m:chd)"
.globl __main_xm_2.nstackwords
.globl __main_xm_2.maxthreads
.globl __main_xm_2.maxtimers
.globl __main_xm_2.maxchanends
.globl __main_xm_2.maxsync
.type  __main_xm_2, @function
.linkset __main_xm_2.locnoside, 1
.linkset __main_xm_2.locnochandec, 1
.linkset .LLNK1, lcd_physical.nstackwords $M lcd_physical.nstackwords
.linkset .LLNK0, .LLNK1 + 3
.linkset __main_xm_2.nstackwords, .LLNK0
__main_xm_2:
          entsp     0x3 
.L0:
          mov       r2, r0
          mov       r0, r1
          mov       r1, r2
          ldw       r2, dp[p_lcd_dtm] 
          ldw       r3, dp[p_lcd_clk] 
          ldw       r11, dp[p_lcd_rgb] 
          stw       r11, sp[0x1] 
          ldw       r11, dp[clk_lcd] 
          stw       r11, sp[0x2] 
.L1:
          bl        lcd_physical 
          retsp     0x3 
.cc_bottom __main_xm_2.function
.linkset __main_xm_2.maxchanends, lcd_physical.maxchanends
.linkset __main_xm_2.maxtimers, lcd_physical.maxtimers
.linkset .LLNK4, lcd_physical.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset __main_xm_2.maxthreads, .LLNK2
.cc_top __main_xm_1.function,__main_xm_1
          .align    4
.call __main_xm_1, line_buffer
.globl __main_xm_1, "f{0}(m:chd,m:chd)"
.globl __main_xm_1.nstackwords
.globl __main_xm_1.maxthreads
.globl __main_xm_1.maxtimers
.globl __main_xm_1.maxchanends
.globl __main_xm_1.maxsync
.type  __main_xm_1, @function
.linkset __main_xm_1.locnoside, 1
.linkset __main_xm_1.locnochandec, 1
.linkset .LLNK6, line_buffer.nstackwords $M line_buffer.nstackwords
.linkset .LLNK5, .LLNK6 + 1
.linkset __main_xm_1.nstackwords, .LLNK5
__main_xm_1:
          entsp     0x1 
.L2:
          mov       r2, r0
          mov       r0, r1
          mov       r1, r2
.L3:
          bl        line_buffer 
          retsp     0x1 
.cc_bottom __main_xm_1.function
.linkset __main_xm_1.maxchanends, line_buffer.maxchanends
.linkset __main_xm_1.maxtimers, line_buffer.maxtimers
.linkset .LLNK9, line_buffer.maxthreads - 1
.linkset .LLNK8, 1 + .LLNK9
.linkset .LLNK7, 1 $M .LLNK8
.linkset __main_xm_1.maxthreads, .LLNK7
.cc_top __main_xm_0.function,__main_xm_0
          .align    4
.call __main_xm_0, doCamera
.globpassesref doCamera, clk, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'clk' in call to `doCamera'"
.globwrite __main_xm_0, clk, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, clk, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, clk2, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'clk2' in call to `doCamera'"
.globwrite __main_xm_0, clk2, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, clk2, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, Y_UV_port, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'Y_UV_port' in call to `doCamera'"
.globwrite __main_xm_0, Y_UV_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, Y_UV_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, PCLK_port, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'PCLK_port' in call to `doCamera'"
.globwrite __main_xm_0, PCLK_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, PCLK_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, EXCLK_port, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'EXCLK_port' in call to `doCamera'"
.globwrite __main_xm_0, EXCLK_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, EXCLK_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, HREF_port, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'HREF_port' in call to `doCamera'"
.globwrite __main_xm_0, HREF_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, HREF_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, VSYNC_port, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'VSYNC_port' in call to `doCamera'"
.globwrite __main_xm_0, VSYNC_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, VSYNC_port, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globpassesref doCamera, p_cam_rst, "../../src/./main.xc:63: error: `__main_xm_0' makes alias of global 'p_cam_rst' in call to `doCamera'"
.globwrite __main_xm_0, p_cam_rst, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globread __main_xm_0, p_cam_rst, 0, 4, "../../src/./main.xc:63: error: previously used here (bytes 0..4)"
.globl __main_xm_0, "f{0}(m:chd,m:chd)"
.globl __main_xm_0.nstackwords
.globl __main_xm_0.maxthreads
.globl __main_xm_0.maxtimers
.globl __main_xm_0.maxchanends
.globl __main_xm_0.maxsync
.type  __main_xm_0, @function
.linkset __main_xm_0.locnoside, 1
.linkset __main_xm_0.locnochandec, 1
.linkset .LLNK11, doCamera.nstackwords $M doCamera.nstackwords
.linkset .LLNK10, .LLNK11 + 7
.linkset __main_xm_0.nstackwords, .LLNK10
__main_xm_0:
          entsp     0x7 
.L4:
          mov       r2, r0
          mov       r0, r1
          mov       r1, r2
          ldw       r2, dp[clk] 
          ldw       r3, dp[clk2] 
          ldw       r11, dp[Y_UV_port] 
          stw       r11, sp[0x1] 
          ldw       r11, dp[PCLK_port] 
          stw       r11, sp[0x2] 
          ldw       r11, dp[EXCLK_port] 
          stw       r11, sp[0x3] 
          ldw       r11, dp[HREF_port] 
          stw       r11, sp[0x4] 
          ldw       r11, dp[VSYNC_port] 
          stw       r11, sp[0x5] 
          ldw       r11, dp[p_cam_rst] 
          stw       r11, sp[0x6] 
.L5:
          bl        doCamera 
          retsp     0x7 
.cc_bottom __main_xm_0.function
.linkset __main_xm_0.maxchanends, doCamera.maxchanends
.linkset __main_xm_0.maxtimers, doCamera.maxtimers
.linkset .LLNK14, doCamera.maxthreads - 1
.linkset .LLNK13, 1 + .LLNK14
.linkset .LLNK12, 1 $M .LLNK13
.linkset __main_xm_0.maxthreads, .LLNK12
.par doCamera, line_buffer, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par thread.anon.1, line_buffer, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par line_buffer, lcd_physical, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par line_buffer, thread.anon.5, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par line_buffer, thread.anon.6, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par doCamera, thread.anon.3, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par doCamera, lcd_physical, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par doCamera, thread.anon.5, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par doCamera, thread.anon.6, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par thread.anon.1, lcd_physical, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par thread.anon.3, lcd_physical, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
.par lcd_physical, thread.anon.6, "../../src/./main.xc:57: error: use of `%s' violates parallel usage rules"
# Thread names for recovering thread graph in linker
.set thread.anon.0, 0  #unreal
.set thread.anon.1, 0  #unreal
.set thread.anon.2, 0  #unreal
.set thread.anon.3, 0  #unreal
.set thread.anon.4, 0  #unreal
.set thread.anon.5, 0  #unreal
.set thread.anon.6, 0  #unreal
.extern stdcore, "a(4:cr)"
.extern __builtin_getid, "f{si}(0)"
          .section .xtacalltable,       "", @progbits
.L6:
          .int      .L7-.L6
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top __main_xm_0.function, __main_xm_0
          .asciiz  "../../src/./main.xc"
          .int      0x0000003f
          .long    .L5
.cc_bottom __main_xm_0.function
.cc_top __main_xm_1.function, __main_xm_1
          .asciiz  "../../src/./main.xc"
          .int      0x00000042
          .long    .L3
.cc_bottom __main_xm_1.function
.cc_top __main_xm_2.function, __main_xm_2
          .asciiz  "../../src/./main.xc"
          .int      0x00000045
          .long    .L1
.cc_bottom __main_xm_2.function
.L7:
          .section .xtalabeltable,       "", @progbits
.L8:
          .int      .L9-.L8
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top __main_xm_0.function, __main_xm_0
          .asciiz  "../../src/./main.xc"
          .int      0x00000036
          .int      0x0000004a
          .long    .L4
.cc_bottom __main_xm_0.function
.cc_top __main_xm_1.function, __main_xm_1
          .asciiz  "../../src/./main.xc"
          .int      0x00000036
          .int      0x0000004a
          .long    .L2
.cc_bottom __main_xm_1.function
.cc_top __main_xm_2.function, __main_xm_2
          .asciiz  "../../src/./main.xc"
          .int      0x00000036
          .int      0x0000004a
          .long    .L0
.cc_bottom __main_xm_2.function
.L9:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
