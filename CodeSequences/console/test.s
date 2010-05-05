          .file     "mii.xc"
.globl p_mii_rxd, "bi:p:32"
.type  p_mii_rxd, @object
          .section .dp.data,       "adw", @progbits
.cc_top p_mii_rxd.data, p_mii_rxd
          .align    4
p_mii_rxd:
          .int      0x00040000
.cc_bottom p_mii_rxd.data
          .align    4
          .section .globcode,      "ax",  @progbits
          .align    2
.cc_top p_mii_rxd.globcode.init, p_mii_rxd
.LC0:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x8
          ldc       r1, 0x6
          setclk    res[r0], r1
          setc      res[r0], 0x200f
          ldc       r1, 0x20
          settw     res[r0], r1
          retsp     0x0 
.cc_bottom p_mii_rxd.globcode.init
          .section .ctors,         "aw",  @progbits
          .align    4
          .int      .LC0
          .section .globcode,      "ax",  @progbits
.cc_top p_mii_rxd.globcode.fini, p_mii_rxd
.LC1:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x0
          retsp     0x0 
.cc_bottom p_mii_rxd.globcode.fini
          .section .dtors,         "aw",  @progbits
          .align    4
          .int      .LC1
.globl p_mii_rxdv, "i:p"
.type  p_mii_rxdv, @object
          .section .dp.data,       "adw", @progbits
.cc_top p_mii_rxdv.data, p_mii_rxdv
          .align    4
p_mii_rxdv:
          .int      0x00010a00
.cc_bottom p_mii_rxdv.data
          .align    4
          .section .globcode,      "ax",  @progbits
.cc_top p_mii_rxdv.globcode.init, p_mii_rxdv
.LC2:
          ldw       r0, dp[p_mii_rxdv] 
          setc      res[r0], 0x8
          ldc       r1, 0x6
          setclk    res[r0], r1
          retsp     0x0 
.cc_bottom p_mii_rxdv.globcode.init
          .section .ctors,         "aw",  @progbits
          .int      .LC2
          .section .globcode,      "ax",  @progbits
.cc_top p_mii_rxdv.globcode.fini, p_mii_rxdv
.LC3:
          ldw       r0, dp[p_mii_rxdv] 
          setc      res[r0], 0x0
          retsp     0x0 
.cc_bottom p_mii_rxdv.globcode.fini
          .section .dtors,         "aw",  @progbits
          .int      .LC3
.globl mii_counter_rxgood, "si"
.type  mii_counter_rxgood, @object
          .section .dp.bss,        "adw", @nobits
.cc_top mii_counter_rxgood.data, mii_counter_rxgood
          .align    4
mii_counter_rxgood:
          .int      0x00000000
.cc_bottom mii_counter_rxgood.data
.globl mii_counter_rxeroverflow, "si"
.type  mii_counter_rxeroverflow, @object
.cc_top mii_counter_rxeroverflow.data, mii_counter_rxeroverflow
          .align    4
mii_counter_rxeroverflow:
          .int      0x00000000
.cc_bottom mii_counter_rxeroverflow.data
.globl mii_counter_rxercrc, "si"
.type  mii_counter_rxercrc, @object
.cc_top mii_counter_rxercrc.data, mii_counter_rxercrc
          .align    4
mii_counter_rxercrc:
          .int      0x00000000
.cc_bottom mii_counter_rxercrc.data
.globl mii_counter_rxerlen, "si"
.type  mii_counter_rxerlen, @object
.cc_top mii_counter_rxerlen.data, mii_counter_rxerlen
          .align    4
mii_counter_rxerlen:
          .int      0x00000000
.cc_bottom mii_counter_rxerlen.data
.globl mii_counter_rxeralgn, "si"
.type  mii_counter_rxeralgn, @object
.cc_top mii_counter_rxeralgn.data, mii_counter_rxeralgn
          .align    4
mii_counter_rxeralgn:
          .int      0x00000000
.cc_bottom mii_counter_rxeralgn.data
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
.extern soutct, "f{0}(m:chd,uc)"
.extern schkct, "f{0}(m:chd,uc)"
.extern sinct, "f{uc}(m:chd)"
.extern sinct_byref, "f{0}(m:chd,&(ui))"
.extern stestct, "f{si}(m:chd)"
.extern stestwct, "f{si}(m:chd)"
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
.extern _exit, "f{0}(si)"
.extern _done, "f{0}(0)"
.extern _open, "f{si}(&(a(:c:uc)),si,ui)"
.extern _close, "f{si}(si)"
.extern _read, "f{si}(si,&(a(:uc)),ui)"
.extern _write, "f{si}(si,&(a(:c:uc)),ui)"
.extern _lseek, "f{si}(si,sl,si)"
.extern _remove, "f{si}(&(a(:c:uc)))"
.extern _rename, "f{si}(&(a(:c:uc)),&(a(:c:uc)))"
.extern _system, "f{si}(&(a(:c:uc)))"
.extern _exception, "f{0}(ui,ui)"
.extern _plugins, "f{0}(si,ui,ui)"
.extern bitrev, "f{ui}(ui)"
.extern byterev, "f{ui}(ui)"
.extern clz, "f{si}(ui)"
.extern consumer, "f{0}(chd)"
.extern mii_rx_pins, "f{0}(chd)"
          .align    4
          .text
          .align    2
.cc_top main.function,main
          .align    4
.call main, thread.anon.0
.call main, thread.anon.1
.call thread.anon.1, consumer
.call main, thread.anon.3
.call thread.anon.3, mii_rx_pins
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 0
.linkset .LLNK5, consumer.nstackwords + 1
.linkset .LLNK4, .LLNK5 + mii_rx_pins.nstackwords
.linkset .LLNK3, .LLNK4 $M .LLNK4
.linkset .LLNK2, consumer.nstackwords $M .LLNK3
.linkset .LLNK1, consumer.nstackwords $M .LLNK2
.linkset .LLNK0, .LLNK1 + 4
.linkset main.nstackwords, .LLNK0
main:
          entsp     0x4 
          stw       r4, sp[0x1] 
          stw       r5, sp[0x2] 
          stw       r6, sp[0x3] 
          getr      r6, 0x2
          getr      r5, 0x2
          setd      res[r6], r5
          setd      res[r5], r6
          getr      r4, 0x3
          getst     r0, res[r4]
          ldap      r11, .L2
          ldaw      r11, sp[0x0] 
          init      t[r0]:sp, r11
          ldap      r11, .L2
          init      t[r0]:pc, r11
          set       t[r0]:r4, r5
          msync     res[r4]
.L4:
.L0:
          mov       r0, r6
          bl        consumer 
          msync     res[r4]
          mjoin     res[r4]
.L1:
          freer     res[r4]
          freer     res[r6]
          freer     res[r5]
          ldw       r4, sp[0x1] 
          ldw       r5, sp[0x2] 
          ldw       r6, sp[0x3] 
.L5:
          retsp     0x4 
.xtathreadstart
          .file     1 "mii.xc"
          .loc      1 41 0

.L2:
.linkset .LLNK8, consumer.nstackwords - consumer.nstackwords
.linkset .LLNK7, .LLNK8 + consumer.nstackwords
.linkset .LLNK6, .LLNK7 + 1
          extsp     .LLNK6 
.L3:
          mov       r0, r4
          bl        mii_rx_pins 
          ssync     
.xtathreadstop
          ssync     
.cc_bottom main.function
.linkset .LLNK11, consumer.maxchanends + mii_rx_pins.maxchanends
.linkset .LLNK10, 2 + .LLNK11
.linkset .LLNK9, 2 $M .LLNK10
.linkset main.maxchanends, .LLNK9
.linkset .LLNK12, consumer.maxtimers + mii_rx_pins.maxtimers
.linkset main.maxtimers, .LLNK12
.linkset .LLNK17, consumer.maxthreads - 1
.linkset .LLNK16, 1 + .LLNK17
.linkset .LLNK15, 1 $M .LLNK16
.linkset .LLNK20, mii_rx_pins.maxthreads - 1
.linkset .LLNK19, 1 + .LLNK20
.linkset .LLNK18, 1 $M .LLNK19
.linkset .LLNK14, .LLNK15 + .LLNK18
.linkset .LLNK13, 1 $M .LLNK14
.linkset main.maxthreads, .LLNK13
.cc_top consumer.function,consumer
          .align    4
.globl consumer, "f{0}(chd)"
.globl consumer.nstackwords
.globl consumer.maxthreads
.globl consumer.maxtimers
.globl consumer.maxchanends
.globl consumer.maxsync
.type  consumer, @function
.linkset consumer.locnoside, 0
.linkset consumer.locnochandec, 1
.linkset consumer.nstackwords, 3
consumer:
          entsp     0x3 
          stw       r0, sp[0x0] 
.L6:
          ldc       r0, 0x0
          stw       r0, sp[0x1] 
.L8:
.L15:
          ldw       r0, sp[0x1] 
          bf        r0, .L9 
          bu        .L7 
.L9:
.L10:
          ldw       r0, sp[0x0] 
          chkct     res[r0], 0x1 
          ldw       r0, sp[0x0] 
          outct     res[r0], 0x1 
          ldw       r0, sp[0x0] 
#endpoint
.L18:
          in        r0, res[r0] 
          stw       r0, sp[0x2] 
          ldw       r0, sp[0x2] 
          stw       r0, sp[0x2] 
          ldw       r0, sp[0x0] 
          chkct     res[r0], 0x1 
          ldw       r0, sp[0x0] 
          outct     res[r0], 0x1 
.L14:
          ldw       r0, sp[0x2] 
          bt        r0, .L12 
          bu        .L11 
.L12:
.L13:
          mkmsk     r0, 0x1
          stw       r0, sp[0x1] 
.L11:
.L16:
          bu        .L8 
.L7:
.L17:
          retsp     0x3 
.cc_bottom consumer.function
.linkset consumer.maxchanends, 0
.linkset consumer.maxtimers, 0
.linkset consumer.maxthreads, 1
.cc_top mii_rx_pins.function,mii_rx_pins
          .align    4
.globwrite mii_rx_pins, p_mii_rxdv, 0, 4, "mii.xc:65: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_pins_eq
.globwrite mii_rx_pins, p_mii_rxdv, 0, 4, "mii.xc:75: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_pins_eq
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:76: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_pins_eq
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:80: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_crc32
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:85: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_crc32
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:97: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_crc32
.call mii_rx_pins, __builtin_pins_eq
.globwrite mii_rx_pins, p_mii_rxdv, 0, 4, "mii.xc:106: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_endin
.globpassesref __builtin_endin, p_mii_rxd, "mii.xc:113: error: `mii_rx_pins' makes alias of global 'p_mii_rxd' in call to `__builtin_endin'"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:113: error: previously used here (bytes 0..4)"
.globread mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:113: error: previously used here (bytes 0..4)"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:114: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_crc8shr
.call mii_rx_pins, __builtin_crc8shr
.call mii_rx_pins, __builtin_crc8shr
.call mii_rx_pins, __builtin_crc8shr
.call mii_rx_pins, __builtin_crc8shr
.call mii_rx_pins, __builtin_crc8shr
.globwrite mii_rx_pins, mii_counter_rxeroverflow, 0, 4, "mii.xc:146: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_clear_buff
.globpassesref __builtin_clear_buff, p_mii_rxd, "mii.xc:147: error: `mii_rx_pins' makes alias of global 'p_mii_rxd' in call to `__builtin_clear_buff'"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:147: error: previously used here (bytes 0..4)"
.globread mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:147: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_out_uint
.call mii_rx_pins, __builtin_out_uint
.globwrite mii_rx_pins, mii_counter_rxercrc, 0, 4, "mii.xc:157: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_clear_buff
.globpassesref __builtin_clear_buff, p_mii_rxd, "mii.xc:158: error: `mii_rx_pins' makes alias of global 'p_mii_rxd' in call to `__builtin_clear_buff'"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:158: error: previously used here (bytes 0..4)"
.globread mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:158: error: previously used here (bytes 0..4)"
.globwrite mii_rx_pins, mii_counter_rxerlen, 0, 4, "mii.xc:161: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_clear_buff
.globpassesref __builtin_clear_buff, p_mii_rxd, "mii.xc:162: error: `mii_rx_pins' makes alias of global 'p_mii_rxd' in call to `__builtin_clear_buff'"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:162: error: previously used here (bytes 0..4)"
.globread mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:162: error: previously used here (bytes 0..4)"
.globwrite mii_rx_pins, mii_counter_rxeralgn, 0, 4, "mii.xc:165: error: previously used here (bytes 0..4)"
.call mii_rx_pins, __builtin_clear_buff
.globpassesref __builtin_clear_buff, p_mii_rxd, "mii.xc:166: error: `mii_rx_pins' makes alias of global 'p_mii_rxd' in call to `__builtin_clear_buff'"
.globwrite mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:166: error: previously used here (bytes 0..4)"
.globread mii_rx_pins, p_mii_rxd, 0, 4, "mii.xc:166: error: previously used here (bytes 0..4)"
.globwrite mii_rx_pins, mii_counter_rxgood, 0, 4, "mii.xc:168: error: previously used here (bytes 0..4)"
.set __builtin_pins_eq, 0
.linkset __builtin_pins_eq.locnoside, 0
.linkset __builtin_pins_eq.locnochandec, 1
.set __builtin_out_uint, 0
.linkset __builtin_out_uint.locnoside, 0
.linkset __builtin_out_uint.locnochandec, 1
.set __builtin_crc32, 0
.linkset __builtin_crc32.locnoside, 0
.linkset __builtin_crc32.locnochandec, 1
.set __builtin_endin, 0
.linkset __builtin_endin.locnoside, 0
.linkset __builtin_endin.locnochandec, 1
.set __builtin_crc8shr, 0
.linkset __builtin_crc8shr.locnoside, 0
.linkset __builtin_crc8shr.locnochandec, 1
.set __builtin_clear_buff, 0
.linkset __builtin_clear_buff.locnoside, 0
.linkset __builtin_clear_buff.locnochandec, 1
.globl mii_rx_pins, "f{0}(chd)"
.globl mii_rx_pins.nstackwords
.globl mii_rx_pins.maxthreads
.globl mii_rx_pins.maxtimers
.globl mii_rx_pins.maxchanends
.globl mii_rx_pins.maxsync
.type  mii_rx_pins, @function
.linkset mii_rx_pins.locnoside, 0
.linkset mii_rx_pins.locnochandec, 1
.linkset mii_rx_pins.nstackwords, 11
mii_rx_pins:
          entsp     0xb 
          stw       r0, sp[0x0] 
.L19:
          ldw       r11, cp[.LC4]
          .section .cp.const4,     "acM", @progbits, 4
.LC4:
          .align    4
          .int      0xedb88320

          .text
          stw       r11, sp[0x1] 
          getr      r0, 0x1
          stw       r0, sp[0x2] 
.L20:
          ldc       r1, 0x0
          ldw       r0, dp[p_mii_rxdv] 
          setd      res[r0], r1
          ldw       r0, dp[p_mii_rxdv] 
          setc      res[r0], 0x11
          ldw       r0, dp[p_mii_rxdv] 
#endpoint
.L119:
          in        r0, res[r0] 
.L22:
.L24:
          ldc       r0, 0x0
          stw       r0, sp[0x3] 
.L25:
          ldc       r0, 0x0
          stw       r0, sp[0x4] 
.L26:
          ldw       r11, cp[.LC5]
          .section .cp.const4,     "acM", @progbits, 4
.LC5:
          .align    4
          .int      0x9226f562

          .text
          stw       r11, sp[0x5] 
.L27:
          mkmsk     r1, 0x1
          ldw       r0, dp[p_mii_rxdv] 
          setd      res[r0], r1
          ldw       r0, dp[p_mii_rxdv] 
          setc      res[r0], 0x11
          ldw       r0, dp[p_mii_rxdv] 
.xtaendpoint "packet_start", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 74
.L120:
          in        r0, res[r0] 
.L28:
          ldc       r1, 0xd
          ldw       r0, dp[p_mii_rxd] 
          setd      res[r0], r1
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x11
          ldw       r0, dp[p_mii_rxd] 
#endpoint
.L121:
          in        r0, res[r0] 
.L29:
          ldw       r0, sp[0x2] 
          setc      res[r0], 0x1
          ldw       r0, sp[0x2] 
#endpoint
.L122:
          in        r0, res[r0] 
          stw       r0, sp[0x6] 
          ldw       r0, sp[0x6] 
          stw       r0, sp[0x6] 
.L30:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x1
          ldw       r0, dp[p_mii_rxd] 
#endpoint
.L123:
          in        r0, res[r0] 
          stw       r0, sp[0x7] 
          ldw       r0, sp[0x7] 
          stw       r0, sp[0x7] 
.L31:
          ldc       r1, 0x0
          ldw       r0, sp[0x0] 
#endpoint
.L124:
          out       res[r0], r1 
.L32:
          ldw       r1, sp[0x7] 
          ldw       r0, sp[0x0] 
#endpoint
.L125:
          out       res[r0], r1 
.L33:
          ldw       r0, sp[0x4] 
          add       r0, r0, 0x4
          stw       r0, sp[0x4] 
.L34:
          ldw       r2, sp[0x7] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc32     r0, r2, r1
          stw       r0, sp[0x5] 
.L35:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x1
          ldw       r0, dp[p_mii_rxd] 
#endpoint
.L126:
          in        r0, res[r0] 
          stw       r0, sp[0x7] 
          ldw       r0, sp[0x7] 
          stw       r0, sp[0x7] 
.L36:
          ldc       r1, 0x0
          ldw       r0, sp[0x0] 
#endpoint
.L127:
          out       res[r0], r1 
.L37:
          ldw       r1, sp[0x7] 
          ldw       r0, sp[0x0] 
#endpoint
.L128:
          out       res[r0], r1 
.L38:
          ldw       r0, sp[0x4] 
          add       r0, r0, 0x4
          stw       r0, sp[0x4] 
.L39:
          ldw       r2, sp[0x7] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc32     r0, r2, r1
          stw       r0, sp[0x5] 
.L41:
          clre      
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x1
          ldw       r0, dp[p_mii_rxd] 
          ldap      r11, .L46
          setv      res[r0], r11
          ldw       r0, dp[p_mii_rxd] 
          eeu       res[r0]
          ldc       r1, 0x0
          ldw       r0, dp[p_mii_rxdv] 
          setd      res[r0], r1
          ldw       r0, dp[p_mii_rxdv] 
          setc      res[r0], 0x11
          ldw       r0, dp[p_mii_rxdv] 
          ldap      r11, .L53
          setv      res[r0], r11
          ldw       r0, dp[p_mii_rxdv] 
          eeu       res[r0]
.xtabranch .L53,.L46
          waiteu    
.L46:
          ldw       r0, dp[p_mii_rxd] 
.xtaendpoint "receive_loop", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 96
.L129:
          in        r0, res[r0] 
          stw       r0, sp[0x7] 
          ldw       r0, sp[0x7] 
          stw       r0, sp[0x7] 
.L49:
          ldc       r1, 0x0
          ldw       r0, sp[0x0] 
#endpoint
.L130:
          out       res[r0], r1 
.L50:
          ldw       r1, sp[0x7] 
          ldw       r0, sp[0x0] 
#endpoint
.L131:
          out       res[r0], r1 
.L51:
          ldw       r0, sp[0x4] 
          add       r0, r0, 0x4
          stw       r0, sp[0x4] 
.L52:
          ldw       r2, sp[0x7] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc32     r0, r2, r1
          stw       r0, sp[0x5] 
          bu        .L45 
.L53:
          ldw       r0, dp[p_mii_rxdv] 
.xtaendpoint "eop_detected", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 105
.L132:
          in        r0, res[r0] 
.L56:
          mkmsk     r0, 0x1
          stw       r0, sp[0x3] 
.L57:
#endpoint
.L133:
          ldw       r0, dp[p_mii_rxd] 
          endin     r0, res[r0] 
          stw       r0, sp[0x9] 
.L58:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x1
          ldw       r0, dp[p_mii_rxd] 
#endpoint
.L134:
          in        r0, res[r0] 
          stw       r0, sp[0x8] 
          ldw       r0, sp[0x8] 
          stw       r0, sp[0x8] 
.L59:
          ldw       r1, sp[0x9] 
          ldc       r0, 0x20
          sub       r1, r0, r1
          ldw       r0, sp[0x8] 
          shr       r0, r0, r1
          stw       r0, sp[0x8] 
.L60:
          ldw       r0, sp[0x9] 
          ashr      r0, r0, 0x3
          stw       r0, sp[0xa] 
.L61:
          ldc       r1, 0x0
          ldw       r0, sp[0x0] 
#endpoint
.L135:
          out       res[r0], r1 
.L62:
          ldw       r1, sp[0x8] 
          ldw       r0, sp[0x0] 
#endpoint
.L136:
          out       res[r0], r1 
.L63:
          ldc       r1, 0x0
          ldw       r0, sp[0x0] 
#endpoint
.L137:
          out       res[r0], r1 
.L64:
          ldw       r1, sp[0x6] 
          ldw       r0, sp[0x0] 
#endpoint
.L138:
          out       res[r0], r1 
          ldw       r1, sp[0xa] 
          bu        .L65 
.L67:
.L68:
          ldw       r0, sp[0x4] 
          ldw       r11, cp[.LC6]
          .section .cp.const4,     "acM", @progbits, 4
.LC6:
          .align    4
          .int      0xfffffffc

          .text
          add       r0, r0, r11
          stw       r0, sp[0x4] 
.L69:
          bu        .L66 
          bu        .L70 
.L70:
.L71:
          ldw       r0, sp[0x4] 
          ldw       r11, cp[.LC7]
          .section .cp.const4,     "acM", @progbits, 4
.LC7:
          .align    4
          .int      0xfffffffd

          .text
          add       r0, r0, r11
          stw       r0, sp[0x4] 
.L72:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L73:
          bu        .L66 
          bu        .L74 
.L74:
.L75:
          ldw       r0, sp[0x4] 
          ldw       r11, cp[.LC8]
          .section .cp.const4,     "acM", @progbits, 4
.LC8:
          .align    4
          .int      0xfffffffe

          .text
          add       r0, r0, r11
          stw       r0, sp[0x4] 
.L76:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L77:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L78:
          bu        .L66 
          bu        .L79 
.L79:
.L80:
          ldw       r1, sp[0x4] 
          mkmsk     r0, 0x20
          add       r0, r1, r0
          stw       r0, sp[0x4] 
.L81:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L82:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L83:
          ldw       r2, sp[0x8] 
          ldw       r1, sp[0x1] 
          ldw       r0, sp[0x5] 
          crc8      r0, r1, r2, r1
          stw       r0, sp[0x5] 
          stw       r1, sp[0x8] 
.L84:
          bu        .L66 
          bu        .L85 
.L85:
.L87:
.xtalabel "error_overflow", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 145
.L86:
          ldw       r0, dp[mii_counter_rxeroverflow] 
          add       r0, r0, 0x1
          stw       r0, dp[mii_counter_rxeroverflow] 
.L88:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x17
.L89:
          bu        .L66 
          bu        .L66 
.L65:
          bf        r1, .L67 
          eq        r0, r1, 0x1
          bt        r0, .L70 
          eq        r0, r1, 0x2
          bt        r0, .L74 
          eq        r0, r1, 0x3
          bt        r0, .L79 
          bu        .L85 
.L66:
.L90:
          mkmsk     r1, 0x1
          ldw       r0, sp[0x0] 
.xtaendpoint "end_frame", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 151
.L139:
          out       res[r0], r1 
.L91:
          ldw       r0, sp[0x4] 
          ldw       r1, sp[0x0] 
#endpoint
.L140:
          out       res[r1], r0 
          ldw       r0, sp[0x5] 
          not       r0, r0
          bt        r0, .L93 
          bu        .L94 
.L93:
.L96:
.xtalabel "error_crc", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 156
.L95:
          ldw       r0, dp[mii_counter_rxercrc] 
          add       r0, r0, 0x1
          stw       r0, dp[mii_counter_rxercrc] 
.L97:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x17
.L113:
          bu        .L92 
.L94:
          mkmsk     r0, 0x6
          not       r1, r0
          ldw       r0, sp[0x4] 
          and       r0, r0, r1
          bt        r0, .L99 
          bu        .L100 
.L99:
.L102:
.xtalabel "error_len", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 160
.L101:
          ldw       r0, dp[mii_counter_rxerlen] 
          add       r0, r0, 0x1
          stw       r0, dp[mii_counter_rxerlen] 
.L103:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x17
.L112:
          bu        .L98 
.L100:
          ldw       r1, sp[0x9] 
          mkmsk     r0, 0x3
          and       r0, r1, r0
          bt        r0, .L105 
          bu        .L106 
.L105:
.L108:
.xtalabel "error_nibble", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console", "mii.xc", 164
.L107:
          ldw       r0, dp[mii_counter_rxeralgn] 
          add       r0, r0, 0x1
          stw       r0, dp[mii_counter_rxeralgn] 
.L109:
          ldw       r0, dp[p_mii_rxd] 
          setc      res[r0], 0x17
.L111:
          bu        .L104 
.L106:
.L110:
          ldw       r0, dp[mii_counter_rxgood] 
          add       r0, r0, 0x1
          stw       r0, dp[mii_counter_rxgood] 
.L104:
.L98:
.L92:
          bu        .L45 
.L43:
.L45:
.L114:
          bu        .L42 
.L42:
.L115:
          ldw       r0, sp[0x3] 
          bf        r0, .L41 
          bu        .L40 
.L40:
.L116:
          bu        .L23 
.L23:
.L117:
          mkmsk     r0, 0x1
          bt        r0, .L22 
          bu        .L21 
.L21:
          ldw       r0, sp[0x2] 
          freer     res[r0]
.L118:
          retsp     0xb 
.cc_bottom mii_rx_pins.function
.linkset mii_rx_pins.maxchanends, 0
.linkset mii_rx_pins.maxtimers, 1
.linkset mii_rx_pins.maxthreads, 1
.par mii_rx_pins, consumer, "mii.xc:39: error: use of `%s' violates parallel usage rules"
# Thread names for recovering thread graph in linker
.set thread.anon.0, 0  #unreal
.set thread.anon.1, 0  #unreal
.set thread.anon.2, 0  #unreal
.set thread.anon.3, 0  #unreal
.set thread.anon.4, 0  #unreal
.extern __builtin_getid, "f{si}(0)"
.extern __builtin_getid, "f{si}(0)"
          .section .xtaendpointtable,       "", @progbits
.L141:
          .int      .L142-.L141
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console"
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000099
          .long    .L140
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000098
          .long    .L139
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000007b
          .long    .L138
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000007a
          .long    .L137
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000078
          .long    .L136
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000077
          .long    .L135
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000072
          .long    .L134
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000071
          .long    .L133
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000006a
          .long    .L132
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000064
          .long    .L131
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000063
          .long    .L130
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000061
          .long    .L129
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000057
          .long    .L128
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000056
          .long    .L127
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000055
          .long    .L126
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000052
          .long    .L125
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000051
          .long    .L124
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000050
          .long    .L123
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004e
          .long    .L122
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004c
          .long    .L121
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004b
          .long    .L120
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000041
          .long    .L119
.cc_bottom mii_rx_pins.function
.cc_top consumer.function, consumer
          .asciiz  "mii.xc"
          .int      0x00000032
          .long    .L18
.cc_bottom consumer.function
.L142:
          .section .xtalabeltable,       "", @progbits
.L143:
          .int      .L144-.L143
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/Tutorial/console"
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x000000b1
          .int      0x000000b1
          .long    .L118
          .asciiz  "mii.xc"
          .int      0x000000af
          .int      0x000000af
          .long    .L117
          .asciiz  "mii.xc"
          .int      0x000000ae
          .int      0x000000ae
          .long    .L115
          .asciiz  "mii.xc"
          .int      0x000000a8
          .int      0x000000a8
          .long    .L110
          .asciiz  "mii.xc"
          .int      0x000000a6
          .int      0x000000a6
          .long    .L109
          .asciiz  "mii.xc"
          .int      0x000000a5
          .int      0x000000a5
          .long    .L108
          .asciiz  "mii.xc"
          .int      0x000000a2
          .int      0x000000a2
          .long    .L103
          .asciiz  "mii.xc"
          .int      0x000000a1
          .int      0x000000a1
          .long    .L102
          .asciiz  "mii.xc"
          .int      0x0000009e
          .int      0x0000009e
          .long    .L97
          .asciiz  "mii.xc"
          .int      0x0000009d
          .int      0x0000009d
          .long    .L96
          .asciiz  "mii.xc"
          .int      0x00000099
          .int      0x00000099
          .long    .L91
          .asciiz  "mii.xc"
          .int      0x00000098
          .int      0x00000098
          .long    .L90
          .asciiz  "mii.xc"
          .int      0x00000093
          .int      0x00000093
          .long    .L88
          .asciiz  "mii.xc"
          .int      0x00000092
          .int      0x00000092
          .long    .L87
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L83
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L82
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L81
          .asciiz  "mii.xc"
          .int      0x0000008b
          .int      0x0000008b
          .long    .L80
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L77
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L76
          .asciiz  "mii.xc"
          .int      0x00000086
          .int      0x00000086
          .long    .L75
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L72
          .asciiz  "mii.xc"
          .int      0x00000082
          .int      0x00000082
          .long    .L71
          .asciiz  "mii.xc"
          .int      0x0000007f
          .int      0x0000007f
          .long    .L68
          .asciiz  "mii.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L64
          .asciiz  "mii.xc"
          .int      0x0000007a
          .int      0x0000007a
          .long    .L63
          .asciiz  "mii.xc"
          .int      0x00000078
          .int      0x00000078
          .long    .L62
          .asciiz  "mii.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L61
          .asciiz  "mii.xc"
          .int      0x00000075
          .int      0x00000075
          .long    .L60
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L59
          .asciiz  "mii.xc"
          .int      0x00000072
          .int      0x00000072
          .long    .L58
          .asciiz  "mii.xc"
          .int      0x00000071
          .int      0x00000071
          .long    .L57
          .asciiz  "mii.xc"
          .int      0x00000070
          .int      0x00000070
          .long    .L56
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L52
          .asciiz  "mii.xc"
          .int      0x00000065
          .int      0x00000065
          .long    .L51
          .asciiz  "mii.xc"
          .int      0x00000064
          .int      0x00000064
          .long    .L50
          .asciiz  "mii.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L49
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L39
          .asciiz  "mii.xc"
          .int      0x00000058
          .int      0x00000058
          .long    .L38
          .asciiz  "mii.xc"
          .int      0x00000057
          .int      0x00000057
          .long    .L37
          .asciiz  "mii.xc"
          .int      0x00000056
          .int      0x00000056
          .long    .L36
          .asciiz  "mii.xc"
          .int      0x00000055
          .int      0x00000055
          .long    .L35
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L34
          .asciiz  "mii.xc"
          .int      0x00000053
          .int      0x00000053
          .long    .L33
          .asciiz  "mii.xc"
          .int      0x00000052
          .int      0x00000052
          .long    .L32
          .asciiz  "mii.xc"
          .int      0x00000051
          .int      0x00000051
          .long    .L31
          .asciiz  "mii.xc"
          .int      0x00000050
          .int      0x00000050
          .long    .L30
          .asciiz  "mii.xc"
          .int      0x0000004e
          .int      0x0000004e
          .long    .L29
          .asciiz  "mii.xc"
          .int      0x0000004c
          .int      0x0000004c
          .long    .L28
          .asciiz  "mii.xc"
          .int      0x0000004b
          .int      0x0000004b
          .long    .L27
          .asciiz  "mii.xc"
          .int      0x00000046
          .int      0x00000046
          .long    .L26
          .asciiz  "mii.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L25
          .asciiz  "mii.xc"
          .int      0x00000044
          .int      0x00000044
          .long    .L24
          .asciiz  "mii.xc"
          .int      0x00000041
          .int      0x00000041
          .long    .L20
          .asciiz  "mii.xc"
          .int      0x0000003e
          .int      0x0000003e
          .long    .L19
.cc_bottom mii_rx_pins.function
.cc_top consumer.function, consumer
          .asciiz  "mii.xc"
          .int      0x00000038
          .int      0x00000038
          .long    .L17
          .asciiz  "mii.xc"
          .int      0x00000035
          .int      0x00000035
          .long    .L13
          .asciiz  "mii.xc"
          .int      0x00000034
          .int      0x00000034
          .long    .L14
          .asciiz  "mii.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L10
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L15
          .asciiz  "mii.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L6
.cc_bottom consumer.function
.cc_top main.function, main
          .asciiz  "mii.xc"
          .int      0x0000002b
          .int      0x0000002b
          .long    .L5
          .asciiz  "mii.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L0
.cc_bottom main.function
.L144:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
