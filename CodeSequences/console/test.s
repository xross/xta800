          .file     "mii.xc"
          .text
          .align    2

.LDBG0:
          .section  .debug_line,   "",    @progbits
.LDBG1:
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
          .align    4
          .section  .debug_frame, "",     @progbits
          .align    4
.LDBG2:
          .int      .LDBG4-.LDBG3
.LDBG3:
          .int      0xffffffff
          .byte     0x03
          .byte     0x00
          .uleb128  0x2
          .sleb128  0xfffffffc
          .uleb128  0xf
          .byte     0x0c
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x07
          .uleb128  0x0
          .byte     0x07
          .uleb128  0x1
          .byte     0x07
          .uleb128  0x2
          .byte     0x07
          .uleb128  0x3
          .byte     0x08
          .uleb128  0x4
          .byte     0x08
          .uleb128  0x5
          .byte     0x08
          .uleb128  0x6
          .byte     0x08
          .uleb128  0x7
          .byte     0x08
          .uleb128  0x8
          .byte     0x08
          .uleb128  0x9
          .byte     0x08
          .uleb128  0xa
          .byte     0x07
          .uleb128  0xb
          .byte     0x08
          .uleb128  0xc
          .byte     0x08
          .uleb128  0xd
          .byte     0x08
          .uleb128  0xe
          .byte     0x08
          .uleb128  0xf
          .align    4, 0
.LDBG4:
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
          .text
.cc_top main.function,main
          .align    4
.LDBG5:
.call main, thread.anon.0
.set main.consumer.inline.0, 0 # unreal
.call main, main.consumer.inline.0
.linkset main.consumer.inline.0.locnoside, 1
.linkset main.consumer.inline.0.locnochandec, 1
.call main, thread.anon.3
.call thread.anon.3, mii_rx_pins
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 0
.linkset main.locnochandec, 0
.linkset .LLNK2, 1 + mii_rx_pins.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 3
.linkset main.nstackwords, .LLNK0
.LDBG41:
.LDBG18:
main:
          entsp     0x3 
.LDBG42:
          stw       r4, sp[0x0] 
          .file     1 "mii.xc"
          .loc      1 37 0

.LDBG16:
          getr      r4, 0x2
          getr      r3, 0x2
          setd      res[r4], r3
          setd      res[r3], r4
          .loc      1 39 0

          getr      r2, 0x3
          getst     r0, res[r2]
          ldap      r11, .L79
          ldaw      r11, sp[0x0] 
          init      t[r0]:sp, r11
          ldap      r11, .L79
          init      t[r0]:pc, r11
          set       t[r0]:r4, r3
          msync     res[r2]
.LDBG19:
.LDBG20:
.L101:
.LDBG14:
          .loc      1 40 0

.L76:
.L74:
.LDBG12:
          .loc      1 40 0

.L11:
.L13:
.LDBG10:
          .loc      1 45 0

.LDBG8:
          .loc      1 47 0

.L26:
.L28:
          ldc       r1, 0x0
.LDBG21:
          .loc      1 48 0

.L71:
          .loc      1 48 0

.L36:
          mkmsk     r0, 0x1
.LDBG6:
.LDBG22:
.LDBG23:
.L45:
          .loc      1 50 0

          chkct     res[r4], 0x1 
          outct     res[r4], 0x1 
.L104:
          in        r11, res[r4] 
.LDBG24:
          chkct     res[r4], 0x1 
          outct     res[r4], 0x1 
          .loc      1 52 0

.L62:
          .loc      1 52 0

.L50:
          bt        r11, .L59 
.LDBG25:
.LDBG26:
.L40:
.L70:
          .loc      1 48 0

.L67:
          bf        r1, .L45 
.LDBG27:
.LDBG28:
.LDBG9:
.LDBG13:
.LDBG15:
.LDBG11:
          msync     res[r2]
          mjoin     res[r2]
.LDBG29:
.LDBG30:
.L78:
          freer     res[r2]
.LDBG17:
          .loc      1 43 0

          freer     res[r4]
          freer     res[r3]
.LDBG43:
          ldw       r4, sp[0x0] 
.LDBG44:
.L103:
          retsp     0x3 
.LDBG31:
.LDBG45:
.LDBG32:
          .loc      1 53 0

          .loc      1 53 0

.LDBG33:
.LDBG34:
.L59:
          mov       r1, r0
.LDBG35:
          bu        .L40 
.LDBG36:
.LDBG37:
.LDBG7:
.LDBG38:
.LDBG39:
.xtathreadstart
          .loc      1 41 0

.L79:
          extsp     0x1 
          .loc      1 41 0

.L98:
.L96:
          .loc      1 41 0

.L90:
          mov       r0, r4
          .loc      1 41 0

.L105:
          bl        mii_rx_pins 
          ssync     
.xtathreadstop
          ssync     
.LDBG40:
.LDBG46:
.cc_bottom main.function
          .section  .debug_frame, "",     @progbits
.cc_top main.function,main
          .align    4
          .int      .LDBG48-.LDBG47
.LDBG47:
          .long     .LDBG2           # offset in .debug_frame
          .int      .LDBG41
          .int      .LDBG46-.LDBG41
          .byte     0x01
          .int      .LDBG42
          .byte     0x0e
          .uleb128  0xc
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG43
          .byte     0x0a
          .byte     0x01
          .int      .LDBG44
          .byte     0xc4
          .byte     0x01
          .int      .LDBG45
          .byte     0x0b
          .align    4, 0
.LDBG48:
.cc_bottom main.function
.linkset .LLNK4, 2 + mii_rx_pins.maxchanends
.linkset .LLNK3, 2 $M .LLNK4
.linkset main.maxchanends, .LLNK3
.linkset main.maxtimers, mii_rx_pins.maxtimers
.linkset .LLNK9, mii_rx_pins.maxthreads - 1
.linkset .LLNK8, 1 + .LLNK9
.linkset .LLNK7, 1 $M .LLNK8
.linkset .LLNK6, 1 + .LLNK7
.linkset .LLNK5, 1 $M .LLNK6
.linkset main.maxthreads, .LLNK5
          .text
.LDBG49:
.cc_top consumer.function,consumer
          .align    4
.LDBG50:
.globl consumer, "f{0}(chd)"
.globl consumer.nstackwords
.globl consumer.maxthreads
.globl consumer.maxtimers
.globl consumer.maxchanends
.globl consumer.maxsync
.type  consumer, @function
.linkset consumer.locnoside, 0
.linkset consumer.locnochandec, 1
.linkset consumer.nstackwords, 0
.LDBG73:
.LDBG55:
consumer:
.LDBG74:
.LDBG56:
          .loc      1 46 0

.LDBG53:
          .loc      1 47 0

.L108:
.L110:
          ldc       r2, 0x0
.LDBG57:
          .loc      1 48 0

.L153:
          .loc      1 48 0

.L118:
          mkmsk     r1, 0x1
.LDBG51:
.LDBG58:
.LDBG59:
.L127:
          .loc      1 50 0

          chkct     res[r0], 0x1 
          outct     res[r0], 0x1 
.L156:
          in        r3, res[r0] 
.LDBG60:
          chkct     res[r0], 0x1 
          outct     res[r0], 0x1 
          .loc      1 52 0

.L144:
          .loc      1 52 0

.L132:
          bt        r3, .L141 
.LDBG61:
.LDBG62:
.L122:
.L152:
          .loc      1 48 0

.L149:
          bf        r2, .L127 
.LDBG63:
.LDBG64:
.LDBG54:
          .loc      1 56 0

.LDBG75:
.L155:
          retsp     0x0 
.LDBG65:
.LDBG76:
.LDBG66:
          .loc      1 53 0

          .loc      1 53 0

.LDBG67:
.LDBG68:
.L141:
          mov       r2, r1
.LDBG69:
          bu        .L122 
.LDBG70:
.LDBG71:
.LDBG52:
.LDBG72:
.LDBG77:
.cc_bottom consumer.function
          .section  .debug_frame, "",     @progbits
.cc_top consumer.function,consumer
          .align    4
          .int      .LDBG79-.LDBG78
.LDBG78:
          .long     .LDBG2           # offset in .debug_frame
          .int      .LDBG73
          .int      .LDBG77-.LDBG73
          .byte     0x01
          .int      .LDBG74
          .byte     0x01
          .int      .LDBG75
          .byte     0x0a
          .byte     0x01
          .int      .LDBG76
          .byte     0x0b
          .align    4, 0
.LDBG79:
.cc_bottom consumer.function
.linkset consumer.maxchanends, 0
.linkset consumer.maxtimers, 0
.linkset consumer.maxthreads, 1
          .text
.LDBG80:
.cc_top mii_rx_pins.function,mii_rx_pins
          .align    4
.LDBG81:
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
.linkset mii_rx_pins.nstackwords, 2
.LDBG177:
.LDBG91:
mii_rx_pins:
          entsp     0x2 
.LDBG178:
.LDBG92:
          .loc      1 61 0

.LDBG89:
          .loc      1 62 0

.L159:
.L161:
          getr      r1, 0x1
          stw       r1, sp[0x0] 
.L165:
          .loc      1 65 0

.L163:
          ldc       r3, 0x0
          ldw       r4, dp[p_mii_rxdv] 
          .loc      1 65 0

          setd      res[r4], r3
          setc      res[r4], 0x11
.L1008:
          in        r1, res[r4] 
.LDBG93:
          mkmsk     r1, 0x1
          stw       r1, sp[0x1] 
          ldc       r1, 0xd
          ldw       r2, dp[p_mii_rxd] 
          setd      res[r2], r1
          ldw       r1, sp[0x0] 
          setc      res[r1], 0x1
          ldap      r11, .L350
          setv      res[r2], r11
          ldap      r11, .L426
          setv      res[r4], r11
.LDBG94:
.LDBG95:
.L167:
.LDBG87:
          .loc      1 68 0

.L172:
.L174:
          mov       r10, r3
.LDBG96:
          .loc      1 69 0

.L175:
.L177:
          .loc      1 70 0

.L178:
.L180:
          ldw       r11, cp[.LC4]
          .section .cp.const4,     "acM", @progbits, 4
.LC4:
          .align    4
          .int      0x9226f562

          .text
          mov       r8, r11
.LDBG97:
.L184:
          .loc      1 75 0

.L182:
          .loc      1 75 0

          ldw       r1, sp[0x1] 
          setd      res[r4], r1
          setc      res[r4], 0x11
.xtaendpoint "packet_start", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 74
.L1009:
          in        r1, res[r4] 
.LDBG98:
.L188:
          .loc      1 76 0

.L186:
          .loc      1 76 0

          setc      res[r2], 0x11
.L1010:
          in        r1, res[r2] 
.LDBG99:
.L190:
          .loc      1 78 0

          ldw       r1, sp[0x0] 
.L1011:
          in        r7, res[r1] 
.LDBG100:
.L192:
          .loc      1 80 0

.L1012:
          in        r1, res[r2] 
.LDBG101:
          .loc      1 81 0

.L212:
.L210:
          .loc      1 81 0

.L202:
.L208:
          .loc      1 81 0

.L1013:
          out       res[r0], r3 
          .loc      1 82 0

.L233:
.L231:
          .loc      1 82 0

.L223:
.L229:
          .loc      1 82 0

.L1014:
          out       res[r0], r1 
          .loc      1 83 0

.L244:
          .loc      1 83 0

.L242:
          .loc      1 84 0

.L265:
.L263:
          .loc      1 84 0

.L253:
.L259:
.L261:
          .loc      1 84 0

          ldw       r11, cp[.LC5]
          .section .cp.const4,     "acM", @progbits, 4
.LC5:
          .align    4
          .int      0xedb88320

          .text
          crc32     r8, r1, r11
.L268:
          .loc      1 85 0

.L1015:
          in        r1, res[r2] 
          .loc      1 86 0

.L288:
.L286:
          .loc      1 86 0

.L278:
.L284:
          .loc      1 86 0

.L1016:
          out       res[r0], r3 
          .loc      1 87 0

.L309:
.L307:
          .loc      1 87 0

.L299:
.L305:
          .loc      1 87 0

.L1017:
          out       res[r0], r1 
          .loc      1 88 0

.L320:
          .loc      1 88 0

.L318:
          ldc       r9, 0x8
.LDBG102:
          .loc      1 89 0

.L341:
.L339:
          .loc      1 89 0

.L329:
.L335:
.L337:
          .loc      1 89 0

          ldw       r11, cp[.LC5]
          crc32     r8, r1, r11
          clre      
          setc      res[r2], 0x1
          eeu       res[r2]
          setd      res[r4], r3
          setc      res[r4], 0x11
          eeu       res[r4]
.LDBG103:
.LDBG104:
.L344:
          .loc      1 94 0

          .loc      1 106 0

.L431:
          .loc      1 106 0

.xtabranch .L426,.L350
          waiteu    
.LDBG105:
.LDBG106:
.L350:
          .loc      1 97 0

.xtaendpoint "receive_loop", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 96
.L1018:
          in        r1, res[r2] 
.LDBG107:
          .loc      1 99 0

.L372:
.L370:
          .loc      1 99 0

.L362:
.L368:
          .loc      1 99 0

.L1019:
          out       res[r0], r3 
          .loc      1 100 0

.L393:
.L391:
          .loc      1 100 0

.L383:
.L389:
          .loc      1 100 0

.L1020:
          out       res[r0], r1 
          .loc      1 101 0

.L404:
          .loc      1 101 0

.L402:
          add       r9, r9, 0x4
          .loc      1 102 0

.L424:
.L422:
          .loc      1 102 0

.L412:
.L418:
.L420:
          .loc      1 102 0

          ldw       r11, cp[.LC5]
          crc32     r8, r1, r11
.LDBG108:
.LDBG109:
.L345:
.L998:
          .loc      1 174 0

.L995:
.LDBG110:
.LDBG111:
.L996:
          bf        r10, .L344 
.LDBG112:
.LDBG113:
          bu        .L167 
.LDBG114:
.LDBG115:
.L426:
.LDBG82:
          .loc      1 106 0

.xtaendpoint "eop_detected", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 105
.L1021:
          in        r1, res[r4] 
.LDBG116:
.LDBG85:
.LDBG83:
          .loc      1 112 0

.L444:
          .loc      1 112 0

.L442:
          mkmsk     r10, 0x1
.LDBG117:
          .loc      1 113 0

.L465:
.L453:
          .loc      1 113 0

.L459:
          .loc      1 113 0

.L1022:
          endin     r1, res[r2] 
          mov       r5, r1
.LDBG118:
.L468:
          .loc      1 114 0

.L1023:
          in        r6, res[r2] 
.LDBG119:
          .loc      1 116 0

.L486:
          .loc      1 116 0

.L476:
.L478:
.L480:
.L482:
.L484:
          ldc       r1, 0x20
          sub       r1, r1, r5
          shr       r6, r6, r1
          .loc      1 117 0

.L501:
          .loc      1 117 0

.L495:
.L497:
.L499:
          .loc      1 119 0

.L522:
.L520:
          .loc      1 119 0

.L512:
.L518:
          .loc      1 119 0

.L1024:
          out       res[r0], r3 
          .loc      1 120 0

.L543:
.L541:
          .loc      1 120 0

.L533:
.L539:
          .loc      1 120 0

.L1025:
          out       res[r0], r6 
          .loc      1 122 0

.L564:
.L562:
          .loc      1 122 0

.L554:
.L560:
          .loc      1 122 0

.L1026:
          out       res[r0], r3 
          .loc      1 123 0

.L585:
.L583:
          .loc      1 123 0

.L575:
.L581:
          .loc      1 123 0

.L1027:
          out       res[r0], r7 
          .loc      1 125 0

.L591:
          ashr      r1, r5, 0x3
          mov       r11, r1
          mkmsk     r1, 0x2
          lsu       r1, r1, r11
          bt        r1, .L1006 
.LDBG120:
.xtabranch .L1007 + 6,.L1007 + 4,.L1007 + 2,.L1007
          bru       r11 
.L1007:
.LDBG121:
.LDBG123:
.LDBG125:
.LDBG127:
.jmptable           .L604, .L620, .L663, .L733
.LDBG128:
.LDBG126:
.LDBG124:
.LDBG122:
.LDBG129:
.L1006:
          .loc      1 146 0

.L825:
.L821:
.xtalabel "error_overflow", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 145
.L822:
          ldw       r1, dp[mii_counter_rxeroverflow] 
          add       r1, r1, 0x1
          stw       r1, dp[mii_counter_rxeroverflow] 
          .loc      1 147 0

.L840:
.L838:
          .loc      1 147 0

.L832:
          .loc      1 147 0

          setc      res[r2], 0x17
          .loc      1 148 0

.L843:
          .loc      1 152 0

.L863:
.L861:
          .loc      1 152 0

.L853:
.L859:
.LDBG130:
.LDBG131:
.L860:
          .loc      1 152 0

          mkmsk     r1, 0x1
.xtaendpoint "end_frame", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 151
.L1028:
          out       res[r0], r1 
          .loc      1 153 0

.L884:
.L882:
          .loc      1 153 0

.L874:
.L880:
          .loc      1 153 0

.L1029:
          out       res[r0], r9 
          .loc      1 171 0

.L891:
.L893:
          not       r1, r8
          bt        r1, .L897 
.LDBG132:
.LDBG133:
          .loc      1 159 0

.L921:
.L923:
.L925:
.L927:
          mkmsk     r1, 0x6
          not       r1, r1
          and       r1, r9, r1
          bt        r1, .L931 
.LDBG134:
.LDBG135:
          .loc      1 163 0

.L955:
.L957:
.L959:
          mkmsk     r1, 0x3
          and       r1, r5, r1
          bt        r1, .L963 
.LDBG136:
.LDBG137:
          .loc      1 168 0

.L986:
.L982:
          ldw       r1, dp[mii_counter_rxgood] 
          add       r1, r1, 0x1
          stw       r1, dp[mii_counter_rxgood] 
.LDBG138:
.LDBG139:
.L916:
.LDBG86:
          bu        .L345 
.LDBG140:
.LDBG141:
          .loc      1 127 0

          .loc      1 127 0

.LDBG142:
.LDBG143:
.L604:
          ldw       r11, cp[.LC6]
          .section .cp.const4,     "acM", @progbits, 4
.LC6:
          .align    4
          .int      0xfffffffc

          .text
          add       r9, r9, r11
          .loc      1 128 0

.L608:
          bu        .L860 
.LDBG144:
.LDBG145:
          .loc      1 130 0

          .loc      1 130 0

.LDBG146:
.LDBG147:
.L620:
          ldw       r11, cp[.LC7]
          .section .cp.const4,     "acM", @progbits, 4
.LC7:
          .align    4
          .int      0xfffffffd

          .text
          add       r9, r9, r11
          .loc      1 131 0

.L648:
.L630:
          .loc      1 131 0

.L638:
.L644:
.L646:
          .loc      1 131 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r6, r11
          .loc      1 132 0

.L651:
          bu        .L860 
.LDBG148:
.LDBG149:
          .loc      1 134 0

          .loc      1 134 0

.LDBG150:
.LDBG151:
.L663:
          ldw       r11, cp[.LC8]
          .section .cp.const4,     "acM", @progbits, 4
.LC8:
          .align    4
          .int      0xfffffffe

          .text
          add       r9, r9, r11
          .loc      1 135 0

.L691:
.L673:
          .loc      1 135 0

.L681:
.L687:
.L689:
          .loc      1 135 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r6, r11
          .loc      1 136 0

.L718:
.L700:
          .loc      1 136 0

.L708:
.L714:
.L716:
          .loc      1 136 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r1, r11
          .loc      1 137 0

.L721:
          bu        .L860 
.LDBG152:
.LDBG153:
          .loc      1 139 0

          .loc      1 139 0

.LDBG154:
.LDBG155:
.L733:
          mkmsk     r1, 0x20
          add       r9, r9, r1
          .loc      1 140 0

.L761:
.L743:
          .loc      1 140 0

.L751:
.L757:
.L759:
          .loc      1 140 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r6, r11
          .loc      1 141 0

.L788:
.L770:
          .loc      1 141 0

.L778:
.L784:
.L786:
          .loc      1 141 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r1, r11
          .loc      1 142 0

.L815:
.L797:
          .loc      1 142 0

.L805:
.L811:
.L813:
          .loc      1 142 0

          ldw       r11, cp[.LC5]
          crc8      r8, r1, r1, r11
          .loc      1 143 0

.L818:
          bu        .L860 
.LDBG156:
.LDBG157:
          .loc      1 157 0

.LDBG158:
.LDBG159:
.xtalabel "error_crc", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 156
.L897:
          ldw       r1, dp[mii_counter_rxercrc] 
          add       r1, r1, 0x1
          stw       r1, dp[mii_counter_rxercrc] 
          .loc      1 158 0

.L914:
.L912:
          .loc      1 158 0

.L906:
          .loc      1 158 0

          setc      res[r2], 0x17
.L990:
          bu        .L996 
.LDBG160:
.LDBG161:
          .loc      1 161 0

.LDBG162:
.LDBG163:
.xtalabel "error_len", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 160
.L931:
          ldw       r1, dp[mii_counter_rxerlen] 
          add       r1, r1, 0x1
          stw       r1, dp[mii_counter_rxerlen] 
          .loc      1 162 0

.L948:
.L946:
          .loc      1 162 0

.L940:
          .loc      1 162 0

          setc      res[r2], 0x17
.L989:
          bu        .L916 
.LDBG164:
.LDBG165:
          .loc      1 165 0

.LDBG166:
.LDBG167:
.xtalabel "error_nibble", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console", "mii.xc", 164
.L963:
          ldw       r1, dp[mii_counter_rxeralgn] 
          add       r1, r1, 0x1
          stw       r1, dp[mii_counter_rxeralgn] 
          .loc      1 166 0

.L980:
.L978:
          .loc      1 166 0

.L972:
          .loc      1 166 0

          setc      res[r2], 0x17
.L988:
          bu        .L916 
.LDBG168:
.LDBG169:
.LDBG84:
.LDBG170:
.LDBG171:
.L347:
.LDBG172:
.LDBG173:
.LDBG174:
.LDBG175:
.LDBG88:
          .loc      1 175 0

.LDBG90:
          .loc      1 177 0

.LDBG176:
.LDBG179:
.cc_bottom mii_rx_pins.function
          .section  .debug_frame, "",     @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
          .align    4
          .int      .LDBG181-.LDBG180
.LDBG180:
          .long     .LDBG2           # offset in .debug_frame
          .int      .LDBG177
          .int      .LDBG179-.LDBG177
          .byte     0x01
          .int      .LDBG178
          .byte     0x0e
          .uleb128  0x8
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .align    4, 0
.LDBG181:
.cc_bottom mii_rx_pins.function
.linkset mii_rx_pins.maxchanends, 0
.linkset mii_rx_pins.maxtimers, 1
.linkset mii_rx_pins.maxthreads, 1
          .text
.LDBG182:
.par mii_rx_pins, consumer, "mii.xc:39: error: use of `%s' violates parallel usage rules"
# Thread names for recovering thread graph in linker
.set thread.anon.0, 0  #unreal
.set thread.anon.1, 0  #unreal
.set thread.anon.2, 0  #unreal
.set thread.anon.3, 0  #unreal
.set thread.anon.4, 0  #unreal
.LDBG183:
.extern __builtin_getid, "f{si}(0)"
.extern __builtin_getid, "f{si}(0)"
          .section  .debug_info,   "",    @progbits
.LDBG185:
          .int      .LDBG187-.LDBG186
.LDBG186:
          .short    0x0003
          .long     .LDBG184         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG183         # high address
          .asciiz   "mii.xc"
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG1           # offset in .debug_lineprog
.LDBG188:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG189:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG190:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG191:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG192:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG193:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG194:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG195:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG196:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG197:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG198:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG199:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG200:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG201:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG202:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG203:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG204:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top p_mii_rxd.data,p_mii_rxd
          .uleb128  0x3
          .asciiz   "p_mii_rxd"
          .byte     0x01
          .short    0x0013
          .int      .LDBG204-.LDBG185
          .short    .LDBG206-.LDBG205
.LDBG205:
          .byte     0x03
          .int      p_mii_rxd
.LDBG206:
.cc_bottom p_mii_rxd.data
.cc_top p_mii_rxdv.data,p_mii_rxdv
          .uleb128  0x3
          .asciiz   "p_mii_rxdv"
          .byte     0x01
          .short    0x0014
          .int      .LDBG199-.LDBG185
          .short    .LDBG208-.LDBG207
.LDBG207:
          .byte     0x03
          .int      p_mii_rxdv
.LDBG208:
.cc_bottom p_mii_rxdv.data
.cc_top mii_counter_rxgood.data,mii_counter_rxgood
          .uleb128  0x3
          .asciiz   "mii_counter_rxgood"
          .byte     0x01
          .short    0x0019
          .int      .LDBG190-.LDBG185
          .short    .LDBG210-.LDBG209
.LDBG209:
          .byte     0x03
          .int      mii_counter_rxgood
.LDBG210:
.cc_bottom mii_counter_rxgood.data
.cc_top mii_counter_rxeroverflow.data,mii_counter_rxeroverflow
          .uleb128  0x3
          .asciiz   "mii_counter_rxeroverflow"
          .byte     0x01
          .short    0x001a
          .int      .LDBG190-.LDBG185
          .short    .LDBG212-.LDBG211
.LDBG211:
          .byte     0x03
          .int      mii_counter_rxeroverflow
.LDBG212:
.cc_bottom mii_counter_rxeroverflow.data
.cc_top mii_counter_rxercrc.data,mii_counter_rxercrc
          .uleb128  0x3
          .asciiz   "mii_counter_rxercrc"
          .byte     0x01
          .short    0x001b
          .int      .LDBG190-.LDBG185
          .short    .LDBG214-.LDBG213
.LDBG213:
          .byte     0x03
          .int      mii_counter_rxercrc
.LDBG214:
.cc_bottom mii_counter_rxercrc.data
.cc_top mii_counter_rxerlen.data,mii_counter_rxerlen
          .uleb128  0x3
          .asciiz   "mii_counter_rxerlen"
          .byte     0x01
          .short    0x001c
          .int      .LDBG190-.LDBG185
          .short    .LDBG216-.LDBG215
.LDBG215:
          .byte     0x03
          .int      mii_counter_rxerlen
.LDBG216:
.cc_bottom mii_counter_rxerlen.data
.cc_top mii_counter_rxeralgn.data,mii_counter_rxeralgn
          .uleb128  0x3
          .asciiz   "mii_counter_rxeralgn"
          .byte     0x01
          .short    0x001d
          .int      .LDBG190-.LDBG185
          .short    .LDBG218-.LDBG217
.LDBG217:
          .byte     0x03
          .int      mii_counter_rxeralgn
.LDBG218:
.cc_bottom mii_counter_rxeralgn.data
.cc_top main.function,main
.LDBG219:
          .uleb128  0x4
          .asciiz   "main"
          .byte     0x01
          .byte     0x25
          .byte     0x01
          .int      .LDBG190-.LDBG185
          .byte     0x01
          .long     .LDBG5           # low address
          .long     .LDBG49          # high address
.LDBG220:
          .uleb128  0x5
          .long     .LDBG14          # low address
          .long     .LDBG15          # high address
          .byte     0x00
          .byte     0x00
.cc_bottom main.function
.cc_top consumer.function,consumer
.LDBG221:
          .uleb128  0x6
          .asciiz   "consumer"
          .byte     0x01
          .byte     0x2e
          .byte     0x01
          .byte     0x01
          .long     .LDBG50          # low address
          .long     .LDBG80          # high address
          .uleb128  0x7
          .asciiz   "input"
          .byte     0x01
          .short    0x002d
          .int      .LDBG196-.LDBG185
          .int      .LDBG222
          .section  .debug_loc,    "",    @progbits
.cc_top consumer.function,consumer
.LDBG222:
          .int      .LDBG59-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG224-.LDBG223
.LDBG223:
          .byte     0x50
.LDBG224:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG226-.LDBG225
.LDBG225:
          .byte     0x50
.LDBG226:
          .int      .LDBG56-.LDBG0
          .int      .LDBG58-.LDBG0
          .short    .LDBG228-.LDBG227
.LDBG227:
          .byte     0x50
.LDBG228:
          .int      .LDBG68-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG230-.LDBG229
.LDBG229:
          .byte     0x50
.LDBG230:
          .int      .LDBG66-.LDBG0
          .int      .LDBG67-.LDBG0
          .short    .LDBG232-.LDBG231
.LDBG231:
          .byte     0x50
.LDBG232:
          .int      0x00000000
          .int      0x00000000
.cc_bottom consumer.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "done"
          .byte     0x01
          .short    0x002f
          .short    0x0000
          .int      .LDBG190-.LDBG185
          .int      .LDBG233
          .section  .debug_loc,    "",    @progbits
.cc_top consumer.function,consumer
.LDBG233:
          .int      .LDBG59-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG235-.LDBG234
.LDBG234:
          .byte     0x52
.LDBG235:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG237-.LDBG236
.LDBG236:
          .byte     0x52
.LDBG237:
          .int      .LDBG57-.LDBG0
          .int      .LDBG58-.LDBG0
          .short    .LDBG239-.LDBG238
.LDBG238:
          .byte     0x52
.LDBG239:
          .int      .LDBG69-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG241-.LDBG240
.LDBG240:
          .byte     0x52
.LDBG241:
          .int      0x00000000
          .int      0x00000000
.cc_bottom consumer.function
          .section  .debug_info,   "",    @progbits
.LDBG242:
          .uleb128  0x5
          .long     .LDBG51          # low address
          .long     .LDBG52          # high address
          .uleb128  0x8
          .asciiz   "x"
          .byte     0x01
          .short    0x0031
          .short    .LDBG243-.LDBG242
          .int      .LDBG190-.LDBG185
          .int      .LDBG244
          .section  .debug_loc,    "",    @progbits
.cc_top consumer.function,consumer
.LDBG244:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG246-.LDBG245
.LDBG245:
          .byte     0x53
.LDBG246:
          .int      0x00000000
          .int      0x00000000
.cc_bottom consumer.function
          .section  .debug_info,   "",    @progbits
.LDBG243:
          .byte     0x00
          .byte     0x00
.cc_bottom consumer.function
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG247:
          .uleb128  0x6
          .asciiz   "mii_rx_pins"
          .byte     0x01
          .byte     0x3d
          .byte     0x01
          .byte     0x01
          .long     .LDBG81          # low address
          .long     .LDBG182         # high address
          .uleb128  0x7
          .asciiz   "c_mii_rx"
          .byte     0x01
          .short    0x003c
          .int      .LDBG196-.LDBG185
          .int      .LDBG248
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG248:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG250-.LDBG249
.LDBG249:
          .byte     0x50
.LDBG250:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG252-.LDBG251
.LDBG251:
          .byte     0x50
.LDBG252:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG254-.LDBG253
.LDBG253:
          .byte     0x50
.LDBG254:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG256-.LDBG255
.LDBG255:
          .byte     0x50
.LDBG256:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG258-.LDBG257
.LDBG257:
          .byte     0x50
.LDBG258:
          .int      .LDBG106-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG260-.LDBG259
.LDBG259:
          .byte     0x50
.LDBG260:
          .int      .LDBG115-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG262-.LDBG261
.LDBG261:
          .byte     0x50
.LDBG262:
          .int      .LDBG104-.LDBG0
          .int      .LDBG105-.LDBG0
          .short    .LDBG264-.LDBG263
.LDBG263:
          .byte     0x50
.LDBG264:
          .int      .LDBG95-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG266-.LDBG265
.LDBG265:
          .byte     0x50
.LDBG266:
          .int      .LDBG113-.LDBG0
          .int      .LDBG114-.LDBG0
          .short    .LDBG268-.LDBG267
.LDBG267:
          .byte     0x50
.LDBG268:
          .int      .LDBG92-.LDBG0
          .int      .LDBG94-.LDBG0
          .short    .LDBG270-.LDBG269
.LDBG269:
          .byte     0x50
.LDBG270:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG272-.LDBG271
.LDBG271:
          .byte     0x50
.LDBG272:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG274-.LDBG273
.LDBG273:
          .byte     0x50
.LDBG274:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG276-.LDBG275
.LDBG275:
          .byte     0x50
.LDBG276:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG278-.LDBG277
.LDBG277:
          .byte     0x50
.LDBG278:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG280-.LDBG279
.LDBG279:
          .byte     0x50
.LDBG280:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG282-.LDBG281
.LDBG281:
          .byte     0x50
.LDBG282:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG284-.LDBG283
.LDBG283:
          .byte     0x50
.LDBG284:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG286-.LDBG285
.LDBG285:
          .byte     0x50
.LDBG286:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG288-.LDBG287
.LDBG287:
          .byte     0x50
.LDBG288:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG290-.LDBG289
.LDBG289:
          .byte     0x50
.LDBG290:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG292-.LDBG291
.LDBG291:
          .byte     0x50
.LDBG292:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG294-.LDBG293
.LDBG293:
          .byte     0x50
.LDBG294:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG296-.LDBG295
.LDBG295:
          .byte     0x50
.LDBG296:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG298-.LDBG297
.LDBG297:
          .byte     0x50
.LDBG298:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG300-.LDBG299
.LDBG299:
          .byte     0x50
.LDBG300:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG302-.LDBG301
.LDBG301:
          .byte     0x50
.LDBG302:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG304-.LDBG303
.LDBG303:
          .byte     0x50
.LDBG304:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG306-.LDBG305
.LDBG305:
          .byte     0x50
.LDBG306:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG308-.LDBG307
.LDBG307:
          .byte     0x50
.LDBG308:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG310-.LDBG309
.LDBG309:
          .byte     0x50
.LDBG310:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG312-.LDBG311
.LDBG311:
          .byte     0x50
.LDBG312:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG314-.LDBG313
.LDBG313:
          .byte     0x50
.LDBG314:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG316-.LDBG315
.LDBG315:
          .byte     0x50
.LDBG316:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG318-.LDBG317
.LDBG317:
          .byte     0x50
.LDBG318:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG319:
          .uleb128  0x9
          .int      .LDBG191-.LDBG185
          .uleb128  0x8
          .asciiz   "poly"
          .byte     0x01
          .short    0x003e
          .short    0x0000
          .int      .LDBG319-.LDBG185
          .int      .LDBG320
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG320:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "tmr"
          .byte     0x01
          .short    0x003f
          .short    0x0000
          .int      .LDBG197-.LDBG185
          .int      .LDBG321
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG321:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG323-.LDBG322
.LDBG322:
          .byte     0x7e
          .sleb128  0x0
.LDBG323:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG324:
          .uleb128  0x5
          .long     .LDBG87          # low address
          .long     .LDBG88          # high address
          .uleb128  0x8
          .asciiz   "eop"
          .byte     0x01
          .short    0x0044
          .short    .LDBG325-.LDBG324
          .int      .LDBG190-.LDBG185
          .int      .LDBG326
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG326:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG328-.LDBG327
.LDBG327:
          .byte     0x5a
.LDBG328:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG330-.LDBG329
.LDBG329:
          .byte     0x5a
.LDBG330:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG332-.LDBG331
.LDBG331:
          .byte     0x5a
.LDBG332:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG334-.LDBG333
.LDBG333:
          .byte     0x5a
.LDBG334:
          .int      .LDBG106-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG336-.LDBG335
.LDBG335:
          .byte     0x5a
.LDBG336:
          .int      .LDBG117-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG338-.LDBG337
.LDBG337:
          .byte     0x5a
.LDBG338:
          .int      .LDBG104-.LDBG0
          .int      .LDBG105-.LDBG0
          .short    .LDBG340-.LDBG339
.LDBG339:
          .byte     0x5a
.LDBG340:
          .int      .LDBG96-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG342-.LDBG341
.LDBG341:
          .byte     0x5a
.LDBG342:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG344-.LDBG343
.LDBG343:
          .byte     0x5a
.LDBG344:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG346-.LDBG345
.LDBG345:
          .byte     0x5a
.LDBG346:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG348-.LDBG347
.LDBG347:
          .byte     0x5a
.LDBG348:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG350-.LDBG349
.LDBG349:
          .byte     0x5a
.LDBG350:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG352-.LDBG351
.LDBG351:
          .byte     0x5a
.LDBG352:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG354-.LDBG353
.LDBG353:
          .byte     0x5a
.LDBG354:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG356-.LDBG355
.LDBG355:
          .byte     0x5a
.LDBG356:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG358-.LDBG357
.LDBG357:
          .byte     0x5a
.LDBG358:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG360-.LDBG359
.LDBG359:
          .byte     0x5a
.LDBG360:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG362-.LDBG361
.LDBG361:
          .byte     0x5a
.LDBG362:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG364-.LDBG363
.LDBG363:
          .byte     0x5a
.LDBG364:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG366-.LDBG365
.LDBG365:
          .byte     0x5a
.LDBG366:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG368-.LDBG367
.LDBG367:
          .byte     0x5a
.LDBG368:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG370-.LDBG369
.LDBG369:
          .byte     0x5a
.LDBG370:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG372-.LDBG371
.LDBG371:
          .byte     0x5a
.LDBG372:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG374-.LDBG373
.LDBG373:
          .byte     0x5a
.LDBG374:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG376-.LDBG375
.LDBG375:
          .byte     0x5a
.LDBG376:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG378-.LDBG377
.LDBG377:
          .byte     0x5a
.LDBG378:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG380-.LDBG379
.LDBG379:
          .byte     0x5a
.LDBG380:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG382-.LDBG381
.LDBG381:
          .byte     0x5a
.LDBG382:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG384-.LDBG383
.LDBG383:
          .byte     0x5a
.LDBG384:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG386-.LDBG385
.LDBG385:
          .byte     0x5a
.LDBG386:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG388-.LDBG387
.LDBG387:
          .byte     0x5a
.LDBG388:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG390-.LDBG389
.LDBG389:
          .byte     0x5a
.LDBG390:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG325:
          .uleb128  0x8
          .asciiz   "length"
          .byte     0x01
          .short    0x0045
          .short    0x0000
          .int      .LDBG190-.LDBG185
          .int      .LDBG391
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG391:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG393-.LDBG392
.LDBG392:
          .byte     0x59
.LDBG393:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG395-.LDBG394
.LDBG394:
          .byte     0x59
.LDBG395:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG397-.LDBG396
.LDBG396:
          .byte     0x59
.LDBG397:
          .int      .LDBG115-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG399-.LDBG398
.LDBG398:
          .byte     0x59
.LDBG399:
          .int      .LDBG106-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG401-.LDBG400
.LDBG400:
          .byte     0x59
.LDBG401:
          .int      .LDBG104-.LDBG0
          .int      .LDBG105-.LDBG0
          .short    .LDBG403-.LDBG402
.LDBG402:
          .byte     0x59
.LDBG403:
          .int      .LDBG102-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG405-.LDBG404
.LDBG404:
          .byte     0x59
.LDBG405:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG407-.LDBG406
.LDBG406:
          .byte     0x59
.LDBG407:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG409-.LDBG408
.LDBG408:
          .byte     0x59
.LDBG409:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG411-.LDBG410
.LDBG410:
          .byte     0x59
.LDBG411:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG413-.LDBG412
.LDBG412:
          .byte     0x59
.LDBG413:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG415-.LDBG414
.LDBG414:
          .byte     0x59
.LDBG415:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG417-.LDBG416
.LDBG416:
          .byte     0x59
.LDBG417:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG419-.LDBG418
.LDBG418:
          .byte     0x59
.LDBG419:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG421-.LDBG420
.LDBG420:
          .byte     0x59
.LDBG421:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG423-.LDBG422
.LDBG422:
          .byte     0x59
.LDBG423:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG425-.LDBG424
.LDBG424:
          .byte     0x59
.LDBG425:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG427-.LDBG426
.LDBG426:
          .byte     0x59
.LDBG427:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG429-.LDBG428
.LDBG428:
          .byte     0x59
.LDBG429:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG431-.LDBG430
.LDBG430:
          .byte     0x59
.LDBG431:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG433-.LDBG432
.LDBG432:
          .byte     0x59
.LDBG433:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG435-.LDBG434
.LDBG434:
          .byte     0x59
.LDBG435:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG437-.LDBG436
.LDBG436:
          .byte     0x59
.LDBG437:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG439-.LDBG438
.LDBG438:
          .byte     0x59
.LDBG439:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG441-.LDBG440
.LDBG440:
          .byte     0x59
.LDBG441:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG443-.LDBG442
.LDBG442:
          .byte     0x59
.LDBG443:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG445-.LDBG444
.LDBG444:
          .byte     0x59
.LDBG445:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG447-.LDBG446
.LDBG446:
          .byte     0x59
.LDBG447:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG449-.LDBG448
.LDBG448:
          .byte     0x59
.LDBG449:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG451-.LDBG450
.LDBG450:
          .byte     0x59
.LDBG451:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG453-.LDBG452
.LDBG452:
          .byte     0x59
.LDBG453:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG455-.LDBG454
.LDBG454:
          .byte     0x59
.LDBG455:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG457-.LDBG456
.LDBG456:
          .byte     0x59
.LDBG457:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "crc"
          .byte     0x01
          .short    0x0046
          .short    0x0000
          .int      .LDBG191-.LDBG185
          .int      .LDBG458
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG458:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG460-.LDBG459
.LDBG459:
          .byte     0x58
.LDBG460:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG462-.LDBG461
.LDBG461:
          .byte     0x58
.LDBG462:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG464-.LDBG463
.LDBG463:
          .byte     0x58
.LDBG464:
          .int      .LDBG106-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG466-.LDBG465
.LDBG465:
          .byte     0x58
.LDBG466:
          .int      .LDBG115-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG468-.LDBG467
.LDBG467:
          .byte     0x58
.LDBG468:
          .int      .LDBG104-.LDBG0
          .int      .LDBG105-.LDBG0
          .short    .LDBG470-.LDBG469
.LDBG469:
          .byte     0x58
.LDBG470:
          .int      .LDBG97-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG472-.LDBG471
.LDBG471:
          .byte     0x58
.LDBG472:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG474-.LDBG473
.LDBG473:
          .byte     0x58
.LDBG474:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG476-.LDBG475
.LDBG475:
          .byte     0x58
.LDBG476:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG478-.LDBG477
.LDBG477:
          .byte     0x58
.LDBG478:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG480-.LDBG479
.LDBG479:
          .byte     0x58
.LDBG480:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG482-.LDBG481
.LDBG481:
          .byte     0x58
.LDBG482:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG484-.LDBG483
.LDBG483:
          .byte     0x58
.LDBG484:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG486-.LDBG485
.LDBG485:
          .byte     0x58
.LDBG486:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG488-.LDBG487
.LDBG487:
          .byte     0x58
.LDBG488:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG490-.LDBG489
.LDBG489:
          .byte     0x58
.LDBG490:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG492-.LDBG491
.LDBG491:
          .byte     0x58
.LDBG492:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG494-.LDBG493
.LDBG493:
          .byte     0x58
.LDBG494:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG496-.LDBG495
.LDBG495:
          .byte     0x58
.LDBG496:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG498-.LDBG497
.LDBG497:
          .byte     0x58
.LDBG498:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG500-.LDBG499
.LDBG499:
          .byte     0x58
.LDBG500:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG502-.LDBG501
.LDBG501:
          .byte     0x58
.LDBG502:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG504-.LDBG503
.LDBG503:
          .byte     0x58
.LDBG504:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG506-.LDBG505
.LDBG505:
          .byte     0x58
.LDBG506:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG508-.LDBG507
.LDBG507:
          .byte     0x58
.LDBG508:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG510-.LDBG509
.LDBG509:
          .byte     0x58
.LDBG510:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG512-.LDBG511
.LDBG511:
          .byte     0x58
.LDBG512:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG514-.LDBG513
.LDBG513:
          .byte     0x58
.LDBG514:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG516-.LDBG515
.LDBG515:
          .byte     0x58
.LDBG516:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG518-.LDBG517
.LDBG517:
          .byte     0x58
.LDBG518:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG520-.LDBG519
.LDBG519:
          .byte     0x58
.LDBG520:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG522-.LDBG521
.LDBG521:
          .byte     0x58
.LDBG522:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG524-.LDBG523
.LDBG523:
          .byte     0x58
.LDBG524:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "time"
          .byte     0x01
          .short    0x0047
          .short    0x0000
          .int      .LDBG191-.LDBG185
          .int      .LDBG525
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG525:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG527-.LDBG526
.LDBG526:
          .byte     0x57
.LDBG527:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG529-.LDBG528
.LDBG528:
          .byte     0x57
.LDBG529:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG531-.LDBG530
.LDBG530:
          .byte     0x57
.LDBG531:
          .int      .LDBG115-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG533-.LDBG532
.LDBG532:
          .byte     0x57
.LDBG533:
          .int      .LDBG106-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG535-.LDBG534
.LDBG534:
          .byte     0x57
.LDBG535:
          .int      .LDBG104-.LDBG0
          .int      .LDBG105-.LDBG0
          .short    .LDBG537-.LDBG536
.LDBG536:
          .byte     0x57
.LDBG537:
          .int      .LDBG100-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG539-.LDBG538
.LDBG538:
          .byte     0x57
.LDBG539:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG541-.LDBG540
.LDBG540:
          .byte     0x57
.LDBG541:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG543-.LDBG542
.LDBG542:
          .byte     0x57
.LDBG543:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG545-.LDBG544
.LDBG544:
          .byte     0x57
.LDBG545:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG547-.LDBG546
.LDBG546:
          .byte     0x57
.LDBG547:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG549-.LDBG548
.LDBG548:
          .byte     0x57
.LDBG549:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG551-.LDBG550
.LDBG550:
          .byte     0x57
.LDBG551:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG553-.LDBG552
.LDBG552:
          .byte     0x57
.LDBG553:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG555-.LDBG554
.LDBG554:
          .byte     0x57
.LDBG555:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG557-.LDBG556
.LDBG556:
          .byte     0x57
.LDBG557:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG559-.LDBG558
.LDBG558:
          .byte     0x57
.LDBG559:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG561-.LDBG560
.LDBG560:
          .byte     0x57
.LDBG561:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG563-.LDBG562
.LDBG562:
          .byte     0x57
.LDBG563:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG565-.LDBG564
.LDBG564:
          .byte     0x57
.LDBG565:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG567-.LDBG566
.LDBG566:
          .byte     0x57
.LDBG567:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG569-.LDBG568
.LDBG568:
          .byte     0x57
.LDBG569:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG571-.LDBG570
.LDBG570:
          .byte     0x57
.LDBG571:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG573-.LDBG572
.LDBG572:
          .byte     0x57
.LDBG573:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG575-.LDBG574
.LDBG574:
          .byte     0x57
.LDBG575:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG577-.LDBG576
.LDBG576:
          .byte     0x57
.LDBG577:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG579-.LDBG578
.LDBG578:
          .byte     0x57
.LDBG579:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG581-.LDBG580
.LDBG580:
          .byte     0x57
.LDBG581:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG583-.LDBG582
.LDBG582:
          .byte     0x57
.LDBG583:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG585-.LDBG584
.LDBG584:
          .byte     0x57
.LDBG585:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG587-.LDBG586
.LDBG586:
          .byte     0x57
.LDBG587:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG589-.LDBG588
.LDBG588:
          .byte     0x57
.LDBG589:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG591-.LDBG590
.LDBG590:
          .byte     0x57
.LDBG591:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "word"
          .byte     0x01
          .short    0x0048
          .short    0x0000
          .int      .LDBG191-.LDBG185
          .int      .LDBG592
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG592:
          .int      .LDBG107-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG594-.LDBG593
.LDBG593:
          .byte     0x51
.LDBG594:
          .int      .LDBG101-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG596-.LDBG595
.LDBG595:
          .byte     0x51
.LDBG596:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG597:
          .uleb128  0x5
          .long     .LDBG85          # low address (select)
          .long     .LDBG86          # high address (select)
          .uleb128  0x8
          .asciiz   "lo"
          .byte     0x01
          .short    0x006a
          .short    0x0000
          .int      .LDBG190-.LDBG185
          .int      .LDBG598
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG598:
          .int      .LDBG116-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG600-.LDBG599
.LDBG599:
          .byte     0x51
.LDBG600:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG601:
          .uleb128  0x5
          .long     .LDBG85          # low address
          .long     .LDBG86          # high address
          .uleb128  0x8
          .asciiz   "tail"
          .byte     0x01
          .short    0x006c
          .short    .LDBG602-.LDBG601
          .int      .LDBG191-.LDBG185
          .int      .LDBG603
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG603:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG605-.LDBG604
.LDBG604:
          .byte     0x56
.LDBG605:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG607-.LDBG606
.LDBG606:
          .byte     0x56
.LDBG607:
          .int      .LDBG119-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG609-.LDBG608
.LDBG608:
          .byte     0x56
.LDBG609:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG611-.LDBG610
.LDBG610:
          .byte     0x56
.LDBG611:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG613-.LDBG612
.LDBG612:
          .byte     0x56
.LDBG613:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG615-.LDBG614
.LDBG614:
          .byte     0x56
.LDBG615:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG617-.LDBG616
.LDBG616:
          .byte     0x56
.LDBG617:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG619-.LDBG618
.LDBG618:
          .byte     0x56
.LDBG619:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG621-.LDBG620
.LDBG620:
          .byte     0x56
.LDBG621:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG623-.LDBG622
.LDBG622:
          .byte     0x56
.LDBG623:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG625-.LDBG624
.LDBG624:
          .byte     0x56
.LDBG625:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
.LDBG602:
          .uleb128  0x8
          .asciiz   "taillen"
          .byte     0x01
          .short    0x006d
          .short    0x0000
          .int      .LDBG190-.LDBG185
          .int      .LDBG626
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG626:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG628-.LDBG627
.LDBG627:
          .byte     0x55
.LDBG628:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG630-.LDBG629
.LDBG629:
          .byte     0x55
.LDBG630:
          .int      .LDBG118-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG632-.LDBG631
.LDBG631:
          .byte     0x55
.LDBG632:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG634-.LDBG633
.LDBG633:
          .byte     0x55
.LDBG634:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG636-.LDBG635
.LDBG635:
          .byte     0x55
.LDBG636:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG638-.LDBG637
.LDBG637:
          .byte     0x55
.LDBG638:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG640-.LDBG639
.LDBG639:
          .byte     0x55
.LDBG640:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG642-.LDBG641
.LDBG641:
          .byte     0x55
.LDBG642:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG644-.LDBG643
.LDBG643:
          .byte     0x55
.LDBG644:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG646-.LDBG645
.LDBG645:
          .byte     0x55
.LDBG646:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG648-.LDBG647
.LDBG647:
          .byte     0x55
.LDBG648:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG650-.LDBG649
.LDBG649:
          .byte     0x55
.LDBG650:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG652-.LDBG651
.LDBG651:
          .byte     0x55
.LDBG652:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG654-.LDBG653
.LDBG653:
          .byte     0x55
.LDBG654:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG656-.LDBG655
.LDBG655:
          .byte     0x55
.LDBG656:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG658-.LDBG657
.LDBG657:
          .byte     0x55
.LDBG658:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG660-.LDBG659
.LDBG659:
          .byte     0x55
.LDBG660:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x8
          .asciiz   "endbytes"
          .byte     0x01
          .short    0x006e
          .short    0x0000
          .int      .LDBG190-.LDBG185
          .int      .LDBG661
          .section  .debug_loc,    "",    @progbits
.cc_top mii_rx_pins.function,mii_rx_pins
.LDBG661:
          .int      0x00000000
          .int      0x00000000
.cc_bottom mii_rx_pins.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
.cc_bottom mii_rx_pins.function
          .byte     0x00
.LDBG187:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG663-.LDBG662
.LDBG662:
          .short    0x0002
          .long     .LDBG185         # offset in .debug_info
          .int      .LDBG187-.LDBG185
.cc_top main.function,main
          .int      .LDBG219-.LDBG185
          .asciiz   "main"
.cc_bottom main.function
.cc_top consumer.function,consumer
          .int      .LDBG221-.LDBG185
          .asciiz   "consumer"
.cc_bottom consumer.function
.cc_top mii_rx_pins.function,mii_rx_pins
          .int      .LDBG247-.LDBG185
          .asciiz   "mii_rx_pins"
.cc_bottom mii_rx_pins.function
          .int      0x00000000
.LDBG663:
          .section  .debug_abbrev, "",    @progbits
.LDBG184:
          .uleb128  0x1
          .byte     0x11
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x03
          .byte     0x08
          .byte     0x1b
          .byte     0x08
          .byte     0x13
          .byte     0x05
          .byte     0x25
          .byte     0x08
          .byte     0x10
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x2
          .byte     0x24
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0x3e
          .byte     0x0b
          .byte     0x0b
          .byte     0x0b
          .byte     0x00
          .byte     0x00
          .uleb128  0x6
          .byte     0x2e
          .byte     0x01
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x0b
          .byte     0x3f
          .byte     0x0c
          .byte     0x27
          .byte     0x0c
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0x4
          .byte     0x2e
          .byte     0x01
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x0b
          .byte     0x3f
          .byte     0x0c
          .byte     0x49
          .byte     0x13
          .byte     0x27
          .byte     0x0c
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0x3
          .byte     0x34
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x05
          .byte     0x49
          .byte     0x13
          .byte     0x02
          .byte     0x03
          .byte     0x00
          .byte     0x00
          .uleb128  0x7
          .byte     0x05
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x05
          .byte     0x49
          .byte     0x13
          .byte     0x02
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x8
          .byte     0x34
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x05
          .byte     0x2c
          .byte     0x05
          .byte     0x49
          .byte     0x13
          .byte     0x02
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x5
          .byte     0x0b
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0x9
          .byte     0x26
          .byte     0x00
          .byte     0x49
          .byte     0x13
          .byte     0x00
          .byte     0x00

          .byte     0x00
          .section .xtaendpointtable,       "", @progbits
.L1030:
          .int      .L1031-.L1030
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console"
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000099
          .long    .L1029
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000098
          .long    .L1028
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000007b
          .long    .L1027
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000007a
          .long    .L1026
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000078
          .long    .L1025
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000077
          .long    .L1024
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000072
          .long    .L1023
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000071
          .long    .L1022
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000006a
          .long    .L1021
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000064
          .long    .L1020
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000063
          .long    .L1019
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000061
          .long    .L1018
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000057
          .long    .L1017
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000056
          .long    .L1016
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000055
          .long    .L1015
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000052
          .long    .L1014
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000051
          .long    .L1013
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000050
          .long    .L1012
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004e
          .long    .L1011
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004c
          .long    .L1010
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x0000004b
          .long    .L1009
.cc_bottom mii_rx_pins.function
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x00000041
          .long    .L1008
.cc_bottom mii_rx_pins.function
.cc_top consumer.function, consumer
          .asciiz  "mii.xc"
          .int      0x00000032
          .long    .L156
.cc_bottom consumer.function
.cc_top main.function, main
          .asciiz  "mii.xc"
          .int      0x00000032
          .long    .L104
.cc_bottom main.function
.L1031:
          .section .xtacalltable,       "", @progbits
.L1032:
          .int      .L1033-.L1032
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console"
.cc_top main.function, main
          .asciiz  "mii.xc"
          .int      0x00000029
          .long    .L105
.cc_bottom main.function
.L1033:
          .section .xtalabeltable,       "", @progbits
.L1034:
          .int      .L1035-.L1034
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/console"
.cc_top mii_rx_pins.function, mii_rx_pins
          .asciiz  "mii.xc"
          .int      0x000000a6
          .int      0x000000a6
          .long    .L972
          .asciiz  "mii.xc"
          .int      0x000000a6
          .int      0x000000a6
          .long    .L978
          .asciiz  "mii.xc"
          .int      0x000000a6
          .int      0x000000a6
          .long    .L980
          .asciiz  "mii.xc"
          .int      0x000000a2
          .int      0x000000a2
          .long    .L940
          .asciiz  "mii.xc"
          .int      0x000000a2
          .int      0x000000a2
          .long    .L946
          .asciiz  "mii.xc"
          .int      0x000000a2
          .int      0x000000a2
          .long    .L948
          .asciiz  "mii.xc"
          .int      0x0000009e
          .int      0x0000009e
          .long    .L906
          .asciiz  "mii.xc"
          .int      0x0000009e
          .int      0x0000009e
          .long    .L912
          .asciiz  "mii.xc"
          .int      0x0000009e
          .int      0x0000009e
          .long    .L914
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L813
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L811
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L805
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L797
          .asciiz  "mii.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L815
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L786
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L784
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L778
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L770
          .asciiz  "mii.xc"
          .int      0x0000008d
          .int      0x0000008d
          .long    .L788
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L759
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L757
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L751
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L743
          .asciiz  "mii.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L761
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L716
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L714
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L708
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L700
          .asciiz  "mii.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L718
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L689
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L687
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L681
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L673
          .asciiz  "mii.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L691
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L646
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L644
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L638
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L630
          .asciiz  "mii.xc"
          .int      0x00000083
          .int      0x00000083
          .long    .L648
          .asciiz  "mii.xc"
          .int      0x000000a8
          .int      0x000000a8
          .long    .L982
          .asciiz  "mii.xc"
          .int      0x000000a8
          .int      0x000000a8
          .long    .L986
          .asciiz  "mii.xc"
          .int      0x000000a3
          .int      0x000000a3
          .long    .L959
          .asciiz  "mii.xc"
          .int      0x000000a3
          .int      0x000000a3
          .long    .L957
          .asciiz  "mii.xc"
          .int      0x000000a3
          .int      0x000000a3
          .long    .L955
          .asciiz  "mii.xc"
          .int      0x0000009f
          .int      0x0000009f
          .long    .L927
          .asciiz  "mii.xc"
          .int      0x0000009f
          .int      0x0000009f
          .long    .L925
          .asciiz  "mii.xc"
          .int      0x0000009f
          .int      0x0000009f
          .long    .L923
          .asciiz  "mii.xc"
          .int      0x0000009f
          .int      0x0000009f
          .long    .L921
          .asciiz  "mii.xc"
          .int      0x0000009b
          .int      0x0000009b
          .long    .L893
          .asciiz  "mii.xc"
          .int      0x0000009b
          .int      0x0000009b
          .long    .L891
          .asciiz  "mii.xc"
          .int      0x00000099
          .int      0x00000099
          .long    .L880
          .asciiz  "mii.xc"
          .int      0x00000099
          .int      0x00000099
          .long    .L874
          .asciiz  "mii.xc"
          .int      0x00000099
          .int      0x00000099
          .long    .L882
          .asciiz  "mii.xc"
          .int      0x00000099
          .int      0x00000099
          .long    .L884
          .asciiz  "mii.xc"
          .int      0x00000098
          .int      0x00000098
          .long    .L859
          .asciiz  "mii.xc"
          .int      0x00000098
          .int      0x00000098
          .long    .L853
          .asciiz  "mii.xc"
          .int      0x00000098
          .int      0x00000098
          .long    .L861
          .asciiz  "mii.xc"
          .int      0x00000098
          .int      0x00000098
          .long    .L863
          .asciiz  "mii.xc"
          .int      0x00000093
          .int      0x00000093
          .long    .L832
          .asciiz  "mii.xc"
          .int      0x00000093
          .int      0x00000093
          .long    .L838
          .asciiz  "mii.xc"
          .int      0x00000093
          .int      0x00000093
          .long    .L840
          .asciiz  "mii.xc"
          .int      0x00000092
          .int      0x00000092
          .long    .L821
          .asciiz  "mii.xc"
          .int      0x00000092
          .int      0x00000092
          .long    .L825
          .asciiz  "mii.xc"
          .int      0x0000007d
          .int      0x0000007d
          .long    .L591
          .asciiz  "mii.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L581
          .asciiz  "mii.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L575
          .asciiz  "mii.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L583
          .asciiz  "mii.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L585
          .asciiz  "mii.xc"
          .int      0x0000007a
          .int      0x0000007a
          .long    .L560
          .asciiz  "mii.xc"
          .int      0x0000007a
          .int      0x0000007a
          .long    .L554
          .asciiz  "mii.xc"
          .int      0x0000007a
          .int      0x0000007a
          .long    .L562
          .asciiz  "mii.xc"
          .int      0x0000007a
          .int      0x0000007a
          .long    .L564
          .asciiz  "mii.xc"
          .int      0x00000078
          .int      0x00000078
          .long    .L539
          .asciiz  "mii.xc"
          .int      0x00000078
          .int      0x00000078
          .long    .L533
          .asciiz  "mii.xc"
          .int      0x00000078
          .int      0x00000078
          .long    .L541
          .asciiz  "mii.xc"
          .int      0x00000078
          .int      0x00000078
          .long    .L543
          .asciiz  "mii.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L518
          .asciiz  "mii.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L512
          .asciiz  "mii.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L520
          .asciiz  "mii.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L522
          .asciiz  "mii.xc"
          .int      0x00000075
          .int      0x00000075
          .long    .L499
          .asciiz  "mii.xc"
          .int      0x00000075
          .int      0x00000075
          .long    .L497
          .asciiz  "mii.xc"
          .int      0x00000075
          .int      0x00000075
          .long    .L495
          .asciiz  "mii.xc"
          .int      0x00000075
          .int      0x00000075
          .long    .L501
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L484
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L482
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L480
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L478
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L476
          .asciiz  "mii.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L486
          .asciiz  "mii.xc"
          .int      0x00000072
          .int      0x00000072
          .long    .L468
          .asciiz  "mii.xc"
          .int      0x00000071
          .int      0x00000071
          .long    .L459
          .asciiz  "mii.xc"
          .int      0x00000071
          .int      0x00000071
          .long    .L453
          .asciiz  "mii.xc"
          .int      0x00000071
          .int      0x00000071
          .long    .L465
          .asciiz  "mii.xc"
          .int      0x00000070
          .int      0x00000070
          .long    .L442
          .asciiz  "mii.xc"
          .int      0x00000070
          .int      0x00000070
          .long    .L444
          .asciiz  "mii.xc"
          .int      0x000000ae
          .int      0x000000ae
          .long    .L995
          .asciiz  "mii.xc"
          .int      0x000000ae
          .int      0x000000ae
          .long    .L998
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L420
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L418
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L412
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L422
          .asciiz  "mii.xc"
          .int      0x00000066
          .int      0x00000066
          .long    .L424
          .asciiz  "mii.xc"
          .int      0x00000065
          .int      0x00000065
          .long    .L402
          .asciiz  "mii.xc"
          .int      0x00000065
          .int      0x00000065
          .long    .L404
          .asciiz  "mii.xc"
          .int      0x00000064
          .int      0x00000064
          .long    .L389
          .asciiz  "mii.xc"
          .int      0x00000064
          .int      0x00000064
          .long    .L383
          .asciiz  "mii.xc"
          .int      0x00000064
          .int      0x00000064
          .long    .L391
          .asciiz  "mii.xc"
          .int      0x00000064
          .int      0x00000064
          .long    .L393
          .asciiz  "mii.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L368
          .asciiz  "mii.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L362
          .asciiz  "mii.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L370
          .asciiz  "mii.xc"
          .int      0x00000063
          .int      0x00000063
          .long    .L372
          .asciiz  "mii.xc"
          .int      0x0000006a
          .int      0x0000006a
          .long    .L431
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L337
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L335
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L329
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L339
          .asciiz  "mii.xc"
          .int      0x00000059
          .int      0x00000059
          .long    .L341
          .asciiz  "mii.xc"
          .int      0x00000058
          .int      0x00000058
          .long    .L318
          .asciiz  "mii.xc"
          .int      0x00000058
          .int      0x00000058
          .long    .L320
          .asciiz  "mii.xc"
          .int      0x00000057
          .int      0x00000057
          .long    .L305
          .asciiz  "mii.xc"
          .int      0x00000057
          .int      0x00000057
          .long    .L299
          .asciiz  "mii.xc"
          .int      0x00000057
          .int      0x00000057
          .long    .L307
          .asciiz  "mii.xc"
          .int      0x00000057
          .int      0x00000057
          .long    .L309
          .asciiz  "mii.xc"
          .int      0x00000056
          .int      0x00000056
          .long    .L284
          .asciiz  "mii.xc"
          .int      0x00000056
          .int      0x00000056
          .long    .L278
          .asciiz  "mii.xc"
          .int      0x00000056
          .int      0x00000056
          .long    .L286
          .asciiz  "mii.xc"
          .int      0x00000056
          .int      0x00000056
          .long    .L288
          .asciiz  "mii.xc"
          .int      0x00000055
          .int      0x00000055
          .long    .L268
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L261
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L259
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L253
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L263
          .asciiz  "mii.xc"
          .int      0x00000054
          .int      0x00000054
          .long    .L265
          .asciiz  "mii.xc"
          .int      0x00000053
          .int      0x00000053
          .long    .L242
          .asciiz  "mii.xc"
          .int      0x00000053
          .int      0x00000053
          .long    .L244
          .asciiz  "mii.xc"
          .int      0x00000052
          .int      0x00000052
          .long    .L229
          .asciiz  "mii.xc"
          .int      0x00000052
          .int      0x00000052
          .long    .L223
          .asciiz  "mii.xc"
          .int      0x00000052
          .int      0x00000052
          .long    .L231
          .asciiz  "mii.xc"
          .int      0x00000052
          .int      0x00000052
          .long    .L233
          .asciiz  "mii.xc"
          .int      0x00000051
          .int      0x00000051
          .long    .L208
          .asciiz  "mii.xc"
          .int      0x00000051
          .int      0x00000051
          .long    .L202
          .asciiz  "mii.xc"
          .int      0x00000051
          .int      0x00000051
          .long    .L210
          .asciiz  "mii.xc"
          .int      0x00000051
          .int      0x00000051
          .long    .L212
          .asciiz  "mii.xc"
          .int      0x00000050
          .int      0x00000050
          .long    .L192
          .asciiz  "mii.xc"
          .int      0x0000004e
          .int      0x0000004e
          .long    .L190
          .asciiz  "mii.xc"
          .int      0x0000004c
          .int      0x0000004c
          .long    .L186
          .asciiz  "mii.xc"
          .int      0x0000004c
          .int      0x0000004c
          .long    .L188
          .asciiz  "mii.xc"
          .int      0x0000004b
          .int      0x0000004b
          .long    .L182
          .asciiz  "mii.xc"
          .int      0x0000004b
          .int      0x0000004b
          .long    .L184
          .asciiz  "mii.xc"
          .int      0x00000046
          .int      0x00000046
          .long    .L180
          .asciiz  "mii.xc"
          .int      0x00000046
          .int      0x00000046
          .long    .L178
          .asciiz  "mii.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L177
          .asciiz  "mii.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L175
          .asciiz  "mii.xc"
          .int      0x00000044
          .int      0x00000044
          .long    .L174
          .asciiz  "mii.xc"
          .int      0x00000044
          .int      0x00000044
          .long    .L172
          .asciiz  "mii.xc"
          .int      0x00000041
          .int      0x00000041
          .long    .L163
          .asciiz  "mii.xc"
          .int      0x00000041
          .int      0x00000041
          .long    .L165
          .asciiz  "mii.xc"
          .int      0x0000003e
          .int      0x0000003e
          .long    .L161
          .asciiz  "mii.xc"
          .int      0x0000003e
          .int      0x0000003e
          .long    .L159
.cc_bottom mii_rx_pins.function
.cc_top consumer.function, consumer
          .asciiz  "mii.xc"
          .int      0x00000038
          .int      0x00000038
          .long    .L155
          .asciiz  "mii.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L127
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L149
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L152
          .asciiz  "mii.xc"
          .int      0x00000034
          .int      0x00000034
          .long    .L132
          .asciiz  "mii.xc"
          .int      0x00000034
          .int      0x00000034
          .long    .L144
          .asciiz  "mii.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L127
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L118
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L153
          .asciiz  "mii.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L110
          .asciiz  "mii.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L108
.cc_bottom consumer.function
.cc_top main.function, main
          .asciiz  "mii.xc"
          .int      0x0000002b
          .int      0x0000002b
          .long    .L103
          .asciiz  "mii.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L45
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L67
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L70
          .asciiz  "mii.xc"
          .int      0x00000034
          .int      0x00000034
          .long    .L50
          .asciiz  "mii.xc"
          .int      0x00000034
          .int      0x00000034
          .long    .L62
          .asciiz  "mii.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L45
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L36
          .asciiz  "mii.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L71
          .asciiz  "mii.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L28
          .asciiz  "mii.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L26
          .asciiz  "mii.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L13
          .asciiz  "mii.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L11
          .asciiz  "mii.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L74
          .asciiz  "mii.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L76
.cc_bottom main.function
.L1035:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
