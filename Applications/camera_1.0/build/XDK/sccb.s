          .file     "../../src/camera/sccb.xc"
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
.extern bitrev, "f{ui}(ui)"
.extern byterev, "f{ui}(ui)"
.extern clz, "f{si}(ui)"
          .text
          .align    2
.cc_top iic_phy_wait.function,iic_phy_wait
          .align    4
.globwrite iic_phy_wait, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_wait, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_wait, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_wait' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_wait, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_wait, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
.globl iic_phy_wait, "f{0}(ui)"
.globl iic_phy_wait.nstackwords
.globl iic_phy_wait.maxthreads
.globl iic_phy_wait.maxtimers
.globl iic_phy_wait.maxchanends
.globl iic_phy_wait.maxsync
.type  iic_phy_wait, @function
.linkset iic_phy_wait.locnoside, 0
.linkset iic_phy_wait.locnochandec, 1
.linkset iic_phy_wait.nstackwords, 0
iic_phy_wait:
.L0:
          ldw       r2, dp[IIC_timer] 
          setc      res[r2], 0x1
.L1:
          in        r1, res[r2] 
          add       r0, r1, r0
          setd      res[r2], r0
          setc      res[r2], 0x9
.L2:
          in        r0, res[r2] 
          retsp     0x0 
.cc_bottom iic_phy_wait.function
.linkset iic_phy_wait.maxchanends, 0
.linkset iic_phy_wait.maxtimers, 0
.linkset iic_phy_wait.maxthreads, 1
.cc_top iic_phy_receiveBit.function,iic_phy_receiveBit
          .align    4
.globwrite iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:58: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:59: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:62: error: previously used here (bytes 0..4)"
.call iic_phy_receiveBit, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:62: error: `iic_phy_receiveBit' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:62: error: previously used here (bytes 0..4)"
.globread iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:62: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:63: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:65: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:68: error: previously used here (bytes 0..4)"
.call iic_phy_receiveBit, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:68: error: `iic_phy_receiveBit' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:68: error: previously used here (bytes 0..4)"
.globread iic_phy_receiveBit, IIC_timer, 0, 4, "../../src/camera/sccb.xc:68: error: previously used here (bytes 0..4)"
.globwrite iic_phy_receiveBit, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:69: error: previously used here (bytes 0..4)"
.globl iic_phy_receiveBit, "f{ui}(0)"
.globl iic_phy_receiveBit.nstackwords
.globl iic_phy_receiveBit.maxthreads
.globl iic_phy_receiveBit.maxtimers
.globl iic_phy_receiveBit.maxchanends
.globl iic_phy_receiveBit.maxsync
.type  iic_phy_receiveBit, @function
.linkset iic_phy_receiveBit.locnoside, 0
.linkset iic_phy_receiveBit.locnochandec, 1
.linkset iic_phy_receiveBit.nstackwords, 0
iic_phy_receiveBit:
.L3:
          ldw       r11, dp[IIC_timer] 
          mkmsk     r2, 0x1
          setc      res[r11], r2
.L4:
          in        r1, res[r11] 
          ldw       r0, dp[p_iic_sda] 
          setc      res[r0], r2
.L5:
          in        r3, res[r0] 
          ldc       r3, 0x514
          add       r1, r1, r3
          setd      res[r11], r1
          setc      res[r11], 0x9
.L6:
          in        r1, res[r11] 
          ldw       r3, dp[p_iic_scl] 
.L7:
          out       res[r3], r2 
.L8:
          in        r0, res[r0] 
          ldc       r2, 0x1770
          add       r1, r1, r2
          setd      res[r11], r1
.L9:
          in        r1, res[r11] 
          ldc       r1, 0x0
.L10:
          out       res[r3], r1 
          retsp     0x0 
.cc_bottom iic_phy_receiveBit.function
.linkset iic_phy_receiveBit.maxchanends, 0
.linkset iic_phy_receiveBit.maxtimers, 0
.linkset iic_phy_receiveBit.maxthreads, 1
.cc_top iic_phy_master_start.function,iic_phy_master_start
          .align    4
.globwrite iic_phy_master_start, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:80: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:81: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_master_start, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_master_start' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:84: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_master_start, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_master_start' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_master_start, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_start, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:87: error: previously used here (bytes 0..4)"
.globl iic_phy_master_start, "f{0}(0)"
.globl iic_phy_master_start.nstackwords
.globl iic_phy_master_start.maxthreads
.globl iic_phy_master_start.maxtimers
.globl iic_phy_master_start.maxchanends
.globl iic_phy_master_start.maxsync
.type  iic_phy_master_start, @function
.linkset iic_phy_master_start.locnoside, 0
.linkset iic_phy_master_start.locnochandec, 1
.linkset iic_phy_master_start.nstackwords, 2
iic_phy_master_start:
          entsp     0x2 
.L13:
          stw       r4, sp[0x0] 
          stw       r5, sp[0x1] 
          ldw       r2, dp[p_iic_scl] 
          mkmsk     r11, 0x1
.L14:
          out       res[r2], r11 
          ldw       r0, dp[p_iic_sda] 
.L15:
          out       res[r0], r11 
          ldw       r5, dp[IIC_timer] 
          setc      res[r5], r11
.L16:
          in        r1, res[r5] 
          ldc       r4, 0x258
          add       r1, r1, r4
          setd      res[r5], r1
          ldc       r3, 0x9
          setc      res[r5], r3
.L17:
          in        r1, res[r5] 
          ldc       r1, 0x0
.L18:
          out       res[r0], r1 
          setc      res[r5], r11
.L19:
          in        r0, res[r5] 
          add       r0, r0, r4
          setd      res[r5], r0
          setc      res[r5], r3
.L20:
          in        r0, res[r5] 
.L21:
          out       res[r2], r1 
          ldw       r4, sp[0x0] 
          ldw       r5, sp[0x1] 
          retsp     0x2 
.cc_bottom iic_phy_master_start.function
.linkset iic_phy_master_start.maxchanends, 0
.linkset iic_phy_master_start.maxtimers, 0
.linkset iic_phy_master_start.maxthreads, 1
.cc_top iic_phy_master_stop.function,iic_phy_master_stop
          .align    4
.globwrite iic_phy_master_stop, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:95: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_master_stop, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_master_stop' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:97: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_master_stop, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_master_stop' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:100: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:45: error: previously used here (bytes 0..4)"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.call iic_phy_master_stop, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:46: error: `iic_phy_master_stop' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globread iic_phy_master_stop, IIC_timer, 0, 4, "../../src/camera/sccb.xc:46: error: previously used here (bytes 0..4)"
.globl iic_phy_master_stop, "f{0}(0)"
.globl iic_phy_master_stop.nstackwords
.globl iic_phy_master_stop.maxthreads
.globl iic_phy_master_stop.maxtimers
.globl iic_phy_master_stop.maxchanends
.globl iic_phy_master_stop.maxsync
.type  iic_phy_master_stop, @function
.linkset iic_phy_master_stop.locnoside, 0
.linkset iic_phy_master_stop.locnochandec, 1
.linkset iic_phy_master_stop.nstackwords, 1
iic_phy_master_stop:
          entsp     0x1 
.L25:
          stw       r4, sp[0x0] 
          ldw       r1, dp[p_iic_sda] 
          ldc       r0, 0x0
.L26:
          out       res[r1], r0 
          ldw       r2, dp[IIC_timer] 
          mkmsk     r4, 0x1
          setc      res[r2], r4
.L27:
          in        r0, res[r2] 
          ldc       r3, 0x514
          add       r0, r0, r3
          setd      res[r2], r0
          ldc       r3, 0x9
          setc      res[r2], r3
.L28:
          in        r0, res[r2] 
          ldw       r0, dp[p_iic_scl] 
.L29:
          out       res[r0], r4 
          setc      res[r2], r4
.L30:
          in        r0, res[r2] 
          ldc       r11, 0x1770
          add       r0, r0, r11
          setd      res[r2], r0
          setc      res[r2], r3
.L31:
          in        r0, res[r2] 
.L32:
          out       res[r1], r4 
          setc      res[r2], r4
.L33:
          in        r0, res[r2] 
          ldc       r1, 0x258
          add       r0, r0, r1
          setd      res[r2], r0
          setc      res[r2], r3
.L34:
          in        r0, res[r2] 
          ldw       r4, sp[0x0] 
          retsp     0x1 
.cc_bottom iic_phy_master_stop.function
.linkset iic_phy_master_stop.maxchanends, 0
.linkset iic_phy_master_stop.maxtimers, 0
.linkset iic_phy_master_stop.maxthreads, 1
.cc_top iic_phy_sendByte.function,iic_phy_sendByte
          .align    4
.call iic_phy_sendByte, __builtin_bitrev
.globwrite iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:121: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:127: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:130: error: previously used here (bytes 0..4)"
.call iic_phy_sendByte, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:130: error: `iic_phy_sendByte' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:130: error: previously used here (bytes 0..4)"
.globread iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:130: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:131: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:137: error: previously used here (bytes 0..4)"
.call iic_phy_sendByte, __builtin_timer_after
.globpassesref __builtin_timer_after, IIC_timer, "../../src/camera/sccb.xc:137: error: `iic_phy_sendByte' makes alias of global 'IIC_timer' in call to `__builtin_timer_after'"
.globwrite iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:137: error: previously used here (bytes 0..4)"
.globread iic_phy_sendByte, IIC_timer, 0, 4, "../../src/camera/sccb.xc:137: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:138: error: previously used here (bytes 0..4)"
.globwrite iic_phy_sendByte, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:141: error: previously used here (bytes 0..4)"
.call iic_phy_sendByte, iic_phy_receiveBit
.set __builtin_bitrev, 0
.linkset __builtin_bitrev.locnoside, 1
.linkset __builtin_bitrev.locnochandec, 1
.globl iic_phy_sendByte, "f{e(){m(XMOS_ACK){5},m(XMOS_FAIL){1},m(XMOS_FRAMING_ERROR){7},m(XMOS_INVALID_PARA){2},m(XMOS_NACK){6},m(XMOS_OVERFLOW){9},m(XMOS_RES_UNAVB){4},m(XMOS_SUCCESS){0},m(XMOS_TIME_OUT){3},m(XMOS_UNDERFLOW){8}}}(ui,ui)"
.globl iic_phy_sendByte.nstackwords
.globl iic_phy_sendByte.maxthreads
.globl iic_phy_sendByte.maxtimers
.globl iic_phy_sendByte.maxchanends
.globl iic_phy_sendByte.maxsync
.type  iic_phy_sendByte, @function
.linkset iic_phy_sendByte.locnoside, 0
.linkset iic_phy_sendByte.locnochandec, 1
.linkset .LLNK1, iic_phy_receiveBit.nstackwords $M iic_phy_receiveBit.nstackwords
.linkset .LLNK0, .LLNK1 + 9
.linkset iic_phy_sendByte.nstackwords, .LLNK0
iic_phy_sendByte:
          entsp     0x9 
.L42:
          stw       r4, sp[0x2] 
          stw       r5, sp[0x3] 
          stw       r6, sp[0x4] 
          stw       r7, sp[0x5] 
          stw       r8, sp[0x6] 
          stw       r9, sp[0x7] 
          stw       r10, sp[0x8] 
          stw       r1, sp[0x1] 
          ldc       r4, 0x0
          mov       r10, r4
          bitrev    r0, r0
          shr       r9, r0, 0x18
          ldw       r7, dp[IIC_timer] 
          setc      res[r7], 0x1
.L49:
          in        r8, res[r7] 
          ldc       r11, 0x80
          ldw       r5, dp[p_iic_sda] 
          ldc       r3, 0x514
          setc      res[r7], 0x9
          ldw       r6, dp[p_iic_scl] 
          ldc       r2, 0x1770
          ldc       r1, 0x8
.L38:
.xtaloop 8
.L43:
          and       r0, r9, r11
          shr       r0, r0, 0x7
.L50:
          out       res[r5], r0 
          add       r8, r8, r3
          setd      res[r7], r8
.L51:
          in        r8, res[r7] 
          mkmsk     r0, 0x1
.L52:
          out       res[r6], r0 
          shl       r9, r9, 0x1
          add       r10, r10, 0x1
          add       r8, r8, r2
          setd      res[r7], r8
.L53:
          in        r8, res[r7] 
.L54:
          out       res[r6], r4 
.L44:
          lsu       r0, r10, r1
          bt        r0, .L38 
.L45:
          setc      res[r5], 0x1
.L55:
          in        r0, res[r5] 
.L56:
          bl        iic_phy_receiveBit 
          ldw       r1, sp[0x1] 
          eq        r0, r0, r1
          bf        r0, .L41 
.L40:
.L47:
          mov       r0, r4
          ldw       r4, sp[0x2] 
          ldw       r5, sp[0x3] 
          ldw       r6, sp[0x4] 
          ldw       r7, sp[0x5] 
          ldw       r8, sp[0x6] 
          ldw       r9, sp[0x7] 
          ldw       r10, sp[0x8] 
          retsp     0x9 
.L41:
.L46:
          mkmsk     r4, 0x1
          bu        .L40 
.cc_bottom iic_phy_sendByte.function
.linkset iic_phy_sendByte.maxchanends, iic_phy_receiveBit.maxchanends
.linkset iic_phy_sendByte.maxtimers, iic_phy_receiveBit.maxtimers
.linkset .LLNK4, iic_phy_receiveBit.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset iic_phy_sendByte.maxthreads, .LLNK2
.cc_top WaitUntil.function,WaitUntil
          .align    4
.call WaitUntil, __builtin_timer_after
.globl WaitUntil, "f{0}(t,ui)"
.globl WaitUntil.nstackwords
.globl WaitUntil.maxthreads
.globl WaitUntil.maxtimers
.globl WaitUntil.maxchanends
.globl WaitUntil.maxsync
.type  WaitUntil, @function
.linkset WaitUntil.locnoside, 0
.linkset WaitUntil.locnochandec, 1
.linkset WaitUntil.nstackwords, 0
WaitUntil:
.L57:
          setd      res[r0], r1
          setc      res[r0], 0x9
.L58:
          in        r0, res[r0] 
          retsp     0x0 
.cc_bottom WaitUntil.function
.linkset WaitUntil.maxchanends, 0
.linkset WaitUntil.maxtimers, 0
.linkset WaitUntil.maxthreads, 1
.cc_top PeekUntil.function,PeekUntil
          .align    4
.call PeekUntil, __builtin_pins_eq
.set __builtin_pins_eq, 0
.linkset __builtin_pins_eq.locnoside, 0
.linkset __builtin_pins_eq.locnochandec, 1
.globl PeekUntil, "f{0}(p,si)"
.globl PeekUntil.nstackwords
.globl PeekUntil.maxthreads
.globl PeekUntil.maxtimers
.globl PeekUntil.maxchanends
.globl PeekUntil.maxsync
.type  PeekUntil, @function
.linkset PeekUntil.locnoside, 0
.linkset PeekUntil.locnochandec, 1
.linkset PeekUntil.nstackwords, 0
PeekUntil:
.L59:
          setd      res[r0], r1
          setc      res[r0], 0x11
.L60:
          in        r0, res[r0] 
          retsp     0x0 
.cc_bottom PeekUntil.function
.linkset PeekUntil.maxchanends, 0
.linkset PeekUntil.maxtimers, 0
.linkset PeekUntil.maxthreads, 1
.cc_top MasterWaitForAck.function,MasterWaitForAck
          .align    4
.call MasterWaitForAck, __builtin_timer_after
.globl MasterWaitForAck, "f{si}(t,o:p,p)"
.globl MasterWaitForAck.nstackwords
.globl MasterWaitForAck.maxthreads
.globl MasterWaitForAck.maxtimers
.globl MasterWaitForAck.maxchanends
.globl MasterWaitForAck.maxsync
.type  MasterWaitForAck, @function
.linkset MasterWaitForAck.locnoside, 0
.linkset MasterWaitForAck.locnochandec, 1
.linkset MasterWaitForAck.nstackwords, 2
MasterWaitForAck:
          entsp     0x2 
.L62:
          stw       r4, sp[0x0] 
          stw       r5, sp[0x1] 
          mkmsk     r3, 0x1
          setc      res[r0], r3
.L63:
          in        r4, res[r0] 
          ldw       r11, cp[.LC0]
          .section .cp.const4,     "acM", @progbits, 4
.LC0:
          .align    4
          .int      0x000249f0
          .text
          add       r4, r4, r11
          setc      res[r2], r3
.L64:
          in        r5, res[r2] 
          add       r4, r4, r11
          setpt     res[r1], r4
.L65:
          out       res[r1], r3 
          setpt     res[r2], r4
.L66:
          in        r3, res[r2] 
          ldc       r5, 0x7530
          add       r4, r4, r5
          setpt     res[r1], r4
          ldc       r5, 0x0
.L67:
          out       res[r1], r5 
          setd      res[r0], r4
          setc      res[r0], 0x9
.L68:
          in        r0, res[r0] 
.L69:
          out       res[r2], r5 
          mov       r0, r3
          ldw       r4, sp[0x0] 
          ldw       r5, sp[0x1] 
          retsp     0x2 
.cc_bottom MasterWaitForAck.function
.linkset MasterWaitForAck.maxchanends, 0
.linkset MasterWaitForAck.maxtimers, 0
.linkset MasterWaitForAck.maxthreads, 1
.cc_top MasterWrite.function,MasterWrite
          .align    4
.call MasterWrite, __builtin_bitrev
.call MasterWrite, __builtin_timer_after
.globwrite MasterWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:206: error: previously used here (bytes 0..4)"
.call MasterWrite, __builtin_timer_after
.globwrite MasterWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:212: error: previously used here (bytes 0..4)"
.call MasterWrite, __builtin_timer_after
.globwrite MasterWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:216: error: previously used here (bytes 0..4)"
.globwrite MasterWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:220: error: previously used here (bytes 0..4)"
.globl MasterWrite, "f{si}(t,ui)"
.globl MasterWrite.nstackwords
.globl MasterWrite.maxthreads
.globl MasterWrite.maxtimers
.globl MasterWrite.maxchanends
.globl MasterWrite.maxsync
.type  MasterWrite, @function
.linkset MasterWrite.locnoside, 0
.linkset MasterWrite.locnochandec, 1
.linkset MasterWrite.nstackwords, 7
MasterWrite:
          entsp     0x7 
.L76:
          stw       r4, sp[0x0] 
          stw       r5, sp[0x1] 
          stw       r6, sp[0x2] 
          stw       r7, sp[0x3] 
          stw       r8, sp[0x4] 
          stw       r9, sp[0x5] 
          stw       r10, sp[0x6] 
          bitrev    r1, r1
          shr       r1, r1, 0x18
          ldc       r3, 0x0
          mov       r4, r3
          mkmsk     r9, 0x1
          ldw       r11, cp[.LC0]
          ldc       r10, 0x9
          ldw       r7, dp[p_iic_sda] 
          ldw       r8, dp[p_iic_scl] 
          ldc       r6, 0x7530
          ldc       r5, 0x8
.L73:
.xtaloop 8
.L77:
          setc      res[r0], r9
.L81:
          in        r2, res[r0] 
          add       r2, r2, r11
          setd      res[r0], r2
          setc      res[r0], r10
.L82:
          in        r2, res[r0] 
.L83:
          outshr    res[r7], r1 
          setc      res[r0], r9
.L84:
          in        r2, res[r0] 
          add       r2, r2, r11
          setd      res[r0], r2
          setc      res[r0], r10
.L85:
          in        r2, res[r0] 
.L86:
          out       res[r8], r9 
          setc      res[r0], r9
.L87:
          in        r2, res[r0] 
          add       r2, r2, r6
          setd      res[r0], r2
          setc      res[r0], r10
.L88:
          in        r2, res[r0] 
.L89:
          out       res[r8], r3 
          add       r4, r4, 0x1
.L78:
          lss       r2, r4, r5
          bt        r2, .L73 
.L79:
          setc      res[r7], r9
.L90:
          in        r0, res[r7] 
          mkmsk     r0, 0x1
          ldw       r4, sp[0x0] 
          ldw       r5, sp[0x1] 
          ldw       r6, sp[0x2] 
          ldw       r7, sp[0x3] 
          ldw       r8, sp[0x4] 
          ldw       r9, sp[0x5] 
          ldw       r10, sp[0x6] 
          retsp     0x7 
.cc_bottom MasterWrite.function
.linkset MasterWrite.maxchanends, 0
.linkset MasterWrite.maxtimers, 0
.linkset MasterWrite.maxthreads, 1
.cc_top sccbRegWritedd.function,sccbRegWritedd
          .align    4
.call sccbRegWritedd, iic_phy_master_start
.call sccbRegWritedd, iic_phy_sendByte
.call sccbRegWritedd, iic_phy_sendByte
.call sccbRegWritedd, iic_phy_sendByte
.call sccbRegWritedd, iic_phy_master_stop
.globl sccbRegWritedd, "f{ui}(ui,ui)"
.globl sccbRegWritedd.nstackwords
.globl sccbRegWritedd.maxthreads
.globl sccbRegWritedd.maxtimers
.globl sccbRegWritedd.maxchanends
.globl sccbRegWritedd.maxsync
.type  sccbRegWritedd, @function
.linkset sccbRegWritedd.locnoside, 1
.linkset sccbRegWritedd.locnochandec, 1
.linkset .LLNK10, iic_phy_master_start.nstackwords $M iic_phy_sendByte.nstackwords
.linkset .LLNK9, .LLNK10 $M iic_phy_sendByte.nstackwords
.linkset .LLNK8, .LLNK9 $M iic_phy_sendByte.nstackwords
.linkset .LLNK7, .LLNK8 $M iic_phy_master_stop.nstackwords
.linkset .LLNK6, .LLNK7 $M .LLNK7
.linkset .LLNK5, .LLNK6 + 3
.linkset sccbRegWritedd.nstackwords, .LLNK5
sccbRegWritedd:
          entsp     0x3 
.L91:
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          mov       r5, r0
          mov       r4, r1
.L92:
          bl        iic_phy_master_start 
          ldc       r0, 0x42
          ldc       r1, 0x0
.L93:
          bl        iic_phy_sendByte 
          mov       r0, r5
          ldc       r1, 0x0
.L94:
          bl        iic_phy_sendByte 
          mov       r0, r4
          ldc       r1, 0x0
.L95:
          bl        iic_phy_sendByte 
          mov       r4, r0
.L96:
          bl        iic_phy_master_stop 
          mov       r0, r4
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          retsp     0x3 
.cc_bottom sccbRegWritedd.function
.linkset .LLNK14, iic_phy_master_start.maxchanends $M iic_phy_sendByte.maxchanends
.linkset .LLNK13, .LLNK14 $M iic_phy_sendByte.maxchanends
.linkset .LLNK12, .LLNK13 $M iic_phy_sendByte.maxchanends
.linkset .LLNK11, .LLNK12 $M iic_phy_master_stop.maxchanends
.linkset sccbRegWritedd.maxchanends, .LLNK11
.linkset .LLNK18, iic_phy_master_start.maxtimers $M iic_phy_sendByte.maxtimers
.linkset .LLNK17, .LLNK18 $M iic_phy_sendByte.maxtimers
.linkset .LLNK16, .LLNK17 $M iic_phy_sendByte.maxtimers
.linkset .LLNK15, .LLNK16 $M iic_phy_master_stop.maxtimers
.linkset sccbRegWritedd.maxtimers, .LLNK15
.linkset .LLNK25, iic_phy_master_start.maxthreads - 1
.linkset .LLNK24, 1 + .LLNK25
.linkset .LLNK23, 1 $M .LLNK24
.linkset .LLNK27, iic_phy_sendByte.maxthreads - 1
.linkset .LLNK26, 1 + .LLNK27
.linkset .LLNK22, .LLNK23 $M .LLNK26
.linkset .LLNK29, iic_phy_sendByte.maxthreads - 1
.linkset .LLNK28, 1 + .LLNK29
.linkset .LLNK21, .LLNK22 $M .LLNK28
.linkset .LLNK31, iic_phy_sendByte.maxthreads - 1
.linkset .LLNK30, 1 + .LLNK31
.linkset .LLNK20, .LLNK21 $M .LLNK30
.linkset .LLNK33, iic_phy_master_stop.maxthreads - 1
.linkset .LLNK32, 1 + .LLNK33
.linkset .LLNK19, .LLNK20 $M .LLNK32
.linkset sccbRegWritedd.maxthreads, .LLNK19
.cc_top sccbRegWrite.function,sccbRegWrite
          .align    4
.call sccbRegWrite, iic_phy_master_start
.call sccbRegWrite, MasterWrite
.call sccbRegWrite, MasterWaitForAck
.globpassesref MasterWaitForAck, p_iic_scl, "../../src/camera/sccb.xc:272: error: `sccbRegWrite' makes alias of global 'p_iic_scl' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:272: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:272: error: previously used here (bytes 0..4)"
.globpassesref MasterWaitForAck, p_iic_sda, "../../src/camera/sccb.xc:272: error: `sccbRegWrite' makes alias of global 'p_iic_sda' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:272: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:272: error: previously used here (bytes 0..4)"
.call sccbRegWrite, MasterWrite
.call sccbRegWrite, MasterWaitForAck
.globpassesref MasterWaitForAck, p_iic_scl, "../../src/camera/sccb.xc:275: error: `sccbRegWrite' makes alias of global 'p_iic_scl' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:275: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:275: error: previously used here (bytes 0..4)"
.globpassesref MasterWaitForAck, p_iic_sda, "../../src/camera/sccb.xc:275: error: `sccbRegWrite' makes alias of global 'p_iic_sda' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:275: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:275: error: previously used here (bytes 0..4)"
.call sccbRegWrite, MasterWrite
.call sccbRegWrite, MasterWaitForAck
.globpassesref MasterWaitForAck, p_iic_scl, "../../src/camera/sccb.xc:282: error: `sccbRegWrite' makes alias of global 'p_iic_scl' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:282: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:282: error: previously used here (bytes 0..4)"
.globpassesref MasterWaitForAck, p_iic_sda, "../../src/camera/sccb.xc:282: error: `sccbRegWrite' makes alias of global 'p_iic_sda' in call to `MasterWaitForAck'"
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:282: error: previously used here (bytes 0..4)"
.globread sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:282: error: previously used here (bytes 0..4)"
.call sccbRegWrite, __builtin_timer_after
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:288: error: previously used here (bytes 0..4)"
.call sccbRegWrite, __builtin_timer_after
.globwrite sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:293: error: previously used here (bytes 0..4)"
.call sccbRegWrite, __builtin_timer_after
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:298: error: previously used here (bytes 0..4)"
.call sccbRegWrite, __builtin_timer_after
.globwrite sccbRegWrite, p_iic_scl, 0, 4, "../../src/camera/sccb.xc:305: error: previously used here (bytes 0..4)"
.globwrite sccbRegWrite, p_iic_sda, 0, 4, "../../src/camera/sccb.xc:306: error: previously used here (bytes 0..4)"
.globl sccbRegWrite, "f{ui}(ui,ui)"
.globl sccbRegWrite.nstackwords
.globl sccbRegWrite.maxthreads
.globl sccbRegWrite.maxtimers
.globl sccbRegWrite.maxchanends
.globl sccbRegWrite.maxsync
.type  sccbRegWrite, @function
.linkset sccbRegWrite.locnoside, 0
.linkset sccbRegWrite.locnochandec, 1
.linkset .LLNK41, iic_phy_master_start.nstackwords $M MasterWrite.nstackwords
.linkset .LLNK40, .LLNK41 $M MasterWaitForAck.nstackwords
.linkset .LLNK39, .LLNK40 $M MasterWrite.nstackwords
.linkset .LLNK38, .LLNK39 $M MasterWaitForAck.nstackwords
.linkset .LLNK37, .LLNK38 $M MasterWrite.nstackwords
.linkset .LLNK36, .LLNK37 $M MasterWaitForAck.nstackwords
.linkset .LLNK35, .LLNK36 $M .LLNK36
.linkset .LLNK34, .LLNK35 + 5
.linkset sccbRegWrite.nstackwords, .LLNK34
sccbRegWrite:
          entsp     0x5 
.L97:
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          stw       r6, sp[0x3] 
          stw       r7, sp[0x4] 
          mov       r7, r0
          mov       r4, r1
          getr      r5, 0x1
.L98:
          bl        iic_phy_master_start 
          mov       r0, r5
          ldc       r1, 0x42
.L99:
          bl        MasterWrite 
          mov       r0, r5
          ldw       r1, dp[p_iic_scl] 
          ldw       r2, dp[p_iic_sda] 
.L100:
          bl        MasterWaitForAck 
          mov       r6, r0
          mov       r0, r5
          mov       r1, r7
.L101:
          bl        MasterWrite 
          mov       r0, r5
          ldw       r1, dp[p_iic_scl] 
          ldw       r2, dp[p_iic_sda] 
.L102:
          bl        MasterWaitForAck 
          add       r6, r6, r0
          mov       r0, r5
          mov       r1, r4
.L103:
          bl        MasterWrite 
          mov       r0, r5
          ldw       r1, dp[p_iic_scl] 
          ldw       r2, dp[p_iic_sda] 
.L104:
          bl        MasterWaitForAck 
          add       r6, r6, r0
          mkmsk     r4, 0x1
          setc      res[r5], r4
.L105:
          in        r0, res[r5] 
          ldw       r11, cp[.LC0]
          add       r0, r0, r11
          setd      res[r5], r0
          ldc       r3, 0x9
          setc      res[r5], r3
.L106:
          in        r0, res[r5] 
          ldw       r2, dp[p_iic_sda] 
          ldc       r0, 0x0
.L107:
          out       res[r2], r0 
          setc      res[r5], r4
.L108:
          in        r0, res[r5] 
          add       r0, r0, r11
          setd      res[r5], r0
          setc      res[r5], r3
.L109:
          in        r0, res[r5] 
          ldw       r1, dp[p_iic_scl] 
.L110:
          out       res[r1], r4 
          setc      res[r5], r4
.L111:
          in        r0, res[r5] 
          ldc       r7, 0x7530
          add       r0, r0, r7
          setd      res[r5], r0
          setc      res[r5], r3
.L112:
          in        r0, res[r5] 
.L113:
          out       res[r2], r4 
          setc      res[r5], r4
.L114:
          in        r0, res[r5] 
          ldw       r11, cp[.LC1]
          .section .cp.const4,     "acM", @progbits, 4
.LC1:
          .align    4
          .int      0x000186a0
          .text
          add       r0, r0, r11
          setd      res[r5], r0
          setc      res[r5], r3
.L115:
          in        r0, res[r5] 
.L116:
          out       res[r1], r4 
.L117:
          out       res[r2], r4 
          mov       r0, r6
          freer     res[r5]
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          ldw       r6, sp[0x3] 
          ldw       r7, sp[0x4] 
          retsp     0x5 
.cc_bottom sccbRegWrite.function
.linkset .LLNK47, iic_phy_master_start.maxchanends $M MasterWrite.maxchanends
.linkset .LLNK46, .LLNK47 $M MasterWaitForAck.maxchanends
.linkset .LLNK45, .LLNK46 $M MasterWrite.maxchanends
.linkset .LLNK44, .LLNK45 $M MasterWaitForAck.maxchanends
.linkset .LLNK43, .LLNK44 $M MasterWrite.maxchanends
.linkset .LLNK42, .LLNK43 $M MasterWaitForAck.maxchanends
.linkset sccbRegWrite.maxchanends, .LLNK42
.linkset .LLNK55, 1 + iic_phy_master_start.maxtimers
.linkset .LLNK54, 1 $M .LLNK55
.linkset .LLNK56, 1 + MasterWrite.maxtimers
.linkset .LLNK53, .LLNK54 $M .LLNK56
.linkset .LLNK57, 1 + MasterWaitForAck.maxtimers
.linkset .LLNK52, .LLNK53 $M .LLNK57
.linkset .LLNK58, 1 + MasterWrite.maxtimers
.linkset .LLNK51, .LLNK52 $M .LLNK58
.linkset .LLNK59, 1 + MasterWaitForAck.maxtimers
.linkset .LLNK50, .LLNK51 $M .LLNK59
.linkset .LLNK60, 1 + MasterWrite.maxtimers
.linkset .LLNK49, .LLNK50 $M .LLNK60
.linkset .LLNK61, 1 + MasterWaitForAck.maxtimers
.linkset .LLNK48, .LLNK49 $M .LLNK61
.linkset sccbRegWrite.maxtimers, .LLNK48
.linkset .LLNK70, iic_phy_master_start.maxthreads - 1
.linkset .LLNK69, 1 + .LLNK70
.linkset .LLNK68, 1 $M .LLNK69
.linkset .LLNK72, MasterWrite.maxthreads - 1
.linkset .LLNK71, 1 + .LLNK72
.linkset .LLNK67, .LLNK68 $M .LLNK71
.linkset .LLNK74, MasterWaitForAck.maxthreads - 1
.linkset .LLNK73, 1 + .LLNK74
.linkset .LLNK66, .LLNK67 $M .LLNK73
.linkset .LLNK76, MasterWrite.maxthreads - 1
.linkset .LLNK75, 1 + .LLNK76
.linkset .LLNK65, .LLNK66 $M .LLNK75
.linkset .LLNK78, MasterWaitForAck.maxthreads - 1
.linkset .LLNK77, 1 + .LLNK78
.linkset .LLNK64, .LLNK65 $M .LLNK77
.linkset .LLNK80, MasterWrite.maxthreads - 1
.linkset .LLNK79, 1 + .LLNK80
.linkset .LLNK63, .LLNK64 $M .LLNK79
.linkset .LLNK82, MasterWaitForAck.maxthreads - 1
.linkset .LLNK81, 1 + .LLNK82
.linkset .LLNK62, .LLNK63 $M .LLNK81
.linkset sccbRegWrite.maxthreads, .LLNK62
.cc_top MasterRead.function,MasterRead
          .align    4
.call MasterRead, __builtin_timer_after
.globl MasterRead, "f{ui}(t,o:p,p,si)"
.globl MasterRead.nstackwords
.globl MasterRead.maxthreads
.globl MasterRead.maxtimers
.globl MasterRead.maxchanends
.globl MasterRead.maxsync
.type  MasterRead, @function
.linkset MasterRead.locnoside, 0
.linkset MasterRead.locnochandec, 1
.linkset MasterRead.nstackwords, 7
MasterRead:
          entsp     0x7 
.L125:
          stw       r4, sp[0x0] 
          stw       r5, sp[0x1] 
          stw       r6, sp[0x2] 
          stw       r7, sp[0x3] 
          stw       r8, sp[0x4] 
          stw       r9, sp[0x5] 
          stw       r10, sp[0x6] 
          ldc       r4, 0x0
          mov       r3, r4
          mkmsk     r9, 0x1
          setc      res[r0], r9
.L130:
          in        r6, res[r0] 
          ldw       r11, cp[.LC0]
          add       r6, r6, r11
          mov       r5, r4
          setc      res[r2], r9
          ldc       r10, 0x7530
          setc      res[r0], 0x9
          ldc       r8, 0x8
.L121:
.xtaloop 8
.L126:
          add       r6, r6, r11
          setpt     res[r1], r6
.L131:
          out       res[r1], r9 
          setpt     res[r2], r6
.L132:
          inshr     r3, res[r2] 
          add       r6, r6, r10
          setpt     res[r1], r6
.L133:
          out       res[r1], r4 
          setd      res[r0], r6
.L134:
          in        r7, res[r0] 
          add       r5, r5, 0x1
.L127:
          lss       r7, r5, r8
          bt        r7, .L121 
.L128:
.L135:
          out       res[r2], r4 
          mov       r0, r3
          ldw       r4, sp[0x0] 
          ldw       r5, sp[0x1] 
          ldw       r6, sp[0x2] 
          ldw       r7, sp[0x3] 
          ldw       r8, sp[0x4] 
          ldw       r9, sp[0x5] 
          ldw       r10, sp[0x6] 
          retsp     0x7 
.cc_bottom MasterRead.function
.linkset MasterRead.maxchanends, 0
.linkset MasterRead.maxtimers, 0
.linkset MasterRead.maxthreads, 1
# Thread names for recovering thread graph in linker
.extern p_iic_scl, "p"
.extern p_iic_sda, "p"
.globl IIC_timer, "t"
.type  IIC_timer, @object
          .section .dp.bss,        "adw", @nobits
.cc_top IIC_timer.data, IIC_timer
          .align    4
IIC_timer:
          .int      0x00000000
.cc_bottom IIC_timer.data
          .align    4
          .section .globcode,      "ax",  @progbits
          .align    2
.LC2:
          getr      r0, 0x1
          stw       r0, dp[IIC_timer] 
retsp     0x0 
          .section .ctors,         "aw",  @progbits
          .align    4
          .int      .LC2
          .section .globcode,      "ax",  @progbits
.LC3:
          ldw       r0, dp[IIC_timer] 
          freer     res[r0]
retsp     0x0 
          .section .dtors,         "aw",  @progbits
          .align    4
          .int      .LC3
.extern __builtin_getid, "f{si}(0)"
          .section .xtaendpointtable,       "", @progbits
.L136:
          .int      .L137-.L136
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000014e
          .long    .L135
.cc_bottom MasterRead.function
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000009d
          .long    .L134
.cc_bottom MasterRead.function
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000149
          .long    .L133
.cc_bottom MasterRead.function
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000146
          .long    .L132
.cc_bottom MasterRead.function
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000144
          .long    .L131
.cc_bottom MasterRead.function
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000013c
          .long    .L130
.cc_bottom MasterRead.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000132
          .long    .L117
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000131
          .long    .L116
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000012d
          .long    .L115
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000012c
          .long    .L114
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000012a
          .long    .L113
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000129
          .long    .L112
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000127
          .long    .L111
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000125
          .long    .L110
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000124
          .long    .L109
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000122
          .long    .L108
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000120
          .long    .L107
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000011f
          .long    .L106
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000011d
          .long    .L105
.cc_bottom sccbRegWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000dc
          .long    .L90
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d8
          .long    .L89
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d7
          .long    .L88
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d6
          .long    .L87
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d4
          .long    .L86
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d3
          .long    .L85
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000d1
          .long    .L84
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ce
          .long    .L83
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000cc
          .long    .L82
.cc_bottom MasterWrite.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ca
          .long    .L81
.cc_bottom MasterWrite.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000bb
          .long    .L69
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000009d
          .long    .L68
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000b7
          .long    .L67
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000b4
          .long    .L66
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000b2
          .long    .L65
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ae
          .long    .L64
.cc_bottom MasterWaitForAck.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ac
          .long    .L63
.cc_bottom MasterWaitForAck.function
.cc_top PeekUntil.function, PeekUntil
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000a4
          .long    .L60
.cc_bottom PeekUntil.function
.cc_top WaitUntil.function, WaitUntil
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000009d
          .long    .L58
.cc_bottom WaitUntil.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000008d
          .long    .L55
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000008a
          .long    .L54
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000089
          .long    .L53
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000083
          .long    .L52
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000082
          .long    .L51
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000007f
          .long    .L50
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000079
          .long    .L49
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L34
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L33
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000064
          .long    .L32
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L31
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L30
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000061
          .long    .L29
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L28
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L27
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000005f
          .long    .L26
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000057
          .long    .L21
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L20
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L19
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000054
          .long    .L18
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L17
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L16
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000051
          .long    .L15
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000050
          .long    .L14
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000045
          .long    .L10
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000044
          .long    .L9
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000041
          .long    .L8
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000003f
          .long    .L7
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000003e
          .long    .L6
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000003b
          .long    .L5
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000003a
          .long    .L4
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_wait.function, iic_phy_wait
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002e
          .long    .L2
.cc_bottom iic_phy_wait.function
.cc_top iic_phy_wait.function, iic_phy_wait
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002d
          .long    .L1
.cc_bottom iic_phy_wait.function
.L137:
          .section .xtacalltable,       "", @progbits
.L138:
          .int      .L139-.L138
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000011a
          .long    .L104
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000119
          .long    .L103
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000113
          .long    .L102
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000112
          .long    .L101
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000110
          .long    .L100
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000010f
          .long    .L99
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000f9
          .long    .L98
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ee
          .long    .L96
.cc_bottom sccbRegWritedd.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ec
          .long    .L95
.cc_bottom sccbRegWritedd.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ea
          .long    .L94
.cc_bottom sccbRegWritedd.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000e8
          .long    .L93
.cc_bottom sccbRegWritedd.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000e6
          .long    .L92
.cc_bottom sccbRegWritedd.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000008f
          .long    .L56
.cc_bottom iic_phy_sendByte.function
.L139:
          .section .xtalabeltable,       "", @progbits
.L140:
          .int      .L141-.L140
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox/verif_tests_sw/Tools/xta/Applications/camera_1.0/build/XDK"
.cc_top MasterRead.function, MasterRead
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000014e
          .int      0x00000150
          .long    .L128
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000141
          .int      0x00000141
          .long    .L127
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000143
          .int      0x00000141
          .long    .L126
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000138
          .int      0x00000141
          .long    .L125
.cc_bottom MasterRead.function
.cc_top sccbRegWrite.function, sccbRegWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000f4
          .int      0x00000134
          .long    .L97
.cc_bottom sccbRegWrite.function
.cc_top sccbRegWritedd.function, sccbRegWritedd
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000e3
          .int      0x000000f0
          .long    .L91
.cc_bottom sccbRegWritedd.function
.cc_top MasterWrite.function, MasterWrite
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000dc
          .int      0x000000df
          .long    .L79
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000c8
          .int      0x000000c8
          .long    .L78
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000ca
          .int      0x000000c8
          .long    .L77
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000c1
          .int      0x000000c8
          .long    .L76
.cc_bottom MasterWrite.function
.cc_top MasterWaitForAck.function, MasterWaitForAck
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000a9
          .int      0x000000bd
          .long    .L62
.cc_bottom MasterWaitForAck.function
.cc_top PeekUntil.function, PeekUntil
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x000000a1
          .int      0x000000a6
          .long    .L59
.cc_bottom PeekUntil.function
.cc_top WaitUntil.function, WaitUntil
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000009b
          .int      0x0000009e
          .long    .L57
.cc_bottom WaitUntil.function
.cc_top iic_phy_sendByte.function, iic_phy_sendByte
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000095
          .int      0x00000095
          .long    .L47
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000092
          .int      0x00000092
          .long    .L46
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000008d
          .int      0x00000090
          .long    .L45
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L44
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000007d
          .int      0x0000008a
          .long    .L43
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000006c
          .int      0x0000008b
          .long    .L42
.cc_bottom iic_phy_sendByte.function
.cc_top iic_phy_master_stop.function, iic_phy_master_stop
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000005e
          .int      0x00000066
          .long    .L25
.cc_bottom iic_phy_master_stop.function
.cc_top iic_phy_master_start.function, iic_phy_master_start
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000004f
          .int      0x00000058
          .long    .L13
.cc_bottom iic_phy_master_start.function
.cc_top iic_phy_receiveBit.function, iic_phy_receiveBit
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x00000036
          .int      0x00000047
          .long    .L3
.cc_bottom iic_phy_receiveBit.function
.cc_top iic_phy_wait.function, iic_phy_wait
          .asciiz  "../../src/camera/sccb.xc"
          .int      0x0000002b
          .int      0x0000002f
          .long    .L0
.cc_bottom iic_phy_wait.function
.L141:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
