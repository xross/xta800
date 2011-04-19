          .file     "test2.xc"
          .text
          .align    2

.align 2
.globl _start.nstackwords
.globl _start.maxthreads
.globl _start.maxtimers
.globl _start.maxchanends
.linkset _start.nstackwords, main.nstackwords
.linkset _start.maxthreads, main.maxthreads
.linkset _start.maxtimers, main.maxtimers
.linkset _start.maxchanends, main.maxchanends
.globl _start
_start:
.xtathreadstart
  bl main
  clre
.xtathreadstop
_stop:
.xtabranch _stop
  waiteu


.LDBG0:
          .section  .debug_frame, "",     @progbits
          .align    4
.LDBG1:
          .int      .LDBG3-.LDBG2
.LDBG2:
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
.LDBG3:
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
          .text
.cc_top read.function,read
          .align    4
.LDBG4:
.globl read, "f{si}(0)"
.globl read.nstackwords
.globl read.maxthreads
.globl read.maxtimers
.globl read.maxchanends
.globl read.maxsync
.type  read, @function
.linkset read.locnoside, 0
.linkset read.locnochandec, 1
.linkset read.nstackwords, 0
.LDBG14:
.LDBG7:
read:
.LDBG15:
.LDBG16:                             # line (3)
.LDBG5:
          getr      r1, 0x1
.LDBG8:
.L0:
.LDBG17:                             # line (8)
          setc      res[r1], 0x1
.xtaendpoint "start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L4:
          in        r2, res[r1] 
.LDBG9:
.L1:
.LDBG18:                             # line (11)
.xtaendpoint "stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L5:
          in        r0, res[r1] 
.LDBG10:
.LDBG19:                             # line (13)
.L2:
          sub       r0, r0, r2
          freer     res[r1]
.LDBG20:
          retsp     0x0 
.LDBG11:
.LDBG21:
.LDBG12:
.LDBG6:
.LDBG13:
.LDBG22:
.cc_bottom read.function
          .section  .debug_frame, "",     @progbits
.cc_top read.function,read
          .align    4
          .int      .LDBG24-.LDBG23
.LDBG23:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG14
          .int      .LDBG22-.LDBG14
          .byte     0x01
          .int      .LDBG15
          .byte     0x01
          .int      .LDBG20
          .byte     0x0a
          .byte     0x01
          .int      .LDBG21
          .byte     0x0b
          .align    4, 0
.LDBG24:
.cc_bottom read.function
.linkset read.maxchanends, 0
.linkset read.maxtimers, 1
.linkset read.maxthreads, 1
          .text
.LDBG25:
.cc_top func1.function,func1
          .align    4
.LDBG26:
.globl func1, "f{si}(0)"
.globl func1.nstackwords
.globl func1.maxthreads
.globl func1.maxtimers
.globl func1.maxchanends
.globl func1.maxsync
.type  func1, @function
.linkset func1.locnoside, 0
.linkset func1.locnochandec, 1
.linkset func1.nstackwords, 0
.LDBG41:
.LDBG33:
func1:
.LDBG42:
.LDBG43:                             # line (16)
.LDBG31:
.LDBG29:
.LDBG27:
.LDBG44:                             # line (18)
          getr      r2, 0x1
.LDBG34:
.L6:
.LDBG45:                             # line (8)
          setc      res[r2], 0x1
.xtaendpoint "read1,start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L13:
          in        r1, res[r2] 
.LDBG35:
.L7:
.LDBG46:                             # line (11)
.xtaendpoint "read1,stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L14:
          in        r0, res[r2] 
.LDBG36:
.LDBG47:                             # line (13)
.L8:

          sub       r0, r0, r1
.LDBG37:
          freer     res[r2]
.L10:
.LDBG48:                             # line (20)
.L11:
.LDBG49:
          retsp     0x0 
.LDBG38:
.LDBG50:
.LDBG39:
.LDBG28:
.LDBG32:
.LDBG30:
.LDBG40:
.LDBG51:
.cc_bottom func1.function
          .section  .debug_frame, "",     @progbits
.cc_top func1.function,func1
          .align    4
          .int      .LDBG53-.LDBG52
.LDBG52:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG41
          .int      .LDBG51-.LDBG41
          .byte     0x01
          .int      .LDBG42
          .byte     0x01
          .int      .LDBG49
          .byte     0x0a
          .byte     0x01
          .int      .LDBG50
          .byte     0x0b
          .align    4, 0
.LDBG53:
.cc_bottom func1.function
.linkset func1.maxchanends, 0
.linkset func1.maxtimers, 1
.linkset func1.maxthreads, 1
          .text
.LDBG54:
.cc_top func2.function,func2
          .align    4
.LDBG55:
.globl func2, "f{si}(0)"
.globl func2.nstackwords
.globl func2.maxthreads
.globl func2.maxtimers
.globl func2.maxchanends
.globl func2.maxsync
.type  func2, @function
.linkset func2.locnoside, 0
.linkset func2.locnochandec, 1
.linkset func2.nstackwords, 0
.LDBG70:
.LDBG62:
func2:
.LDBG71:
.LDBG72:                             # line (23)
.LDBG60:
.LDBG58:
.LDBG56:
.LDBG73:                             # line (25)
          getr      r2, 0x1
.LDBG63:
.L15:
.LDBG74:                             # line (8)
          setc      res[r2], 0x1
.xtaendpoint "read2,start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L22:
          in        r1, res[r2] 
.LDBG64:
.L16:
.LDBG75:                             # line (11)
.xtaendpoint "read2,stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L23:
          in        r0, res[r2] 
.LDBG65:
.LDBG76:                             # line (13)
.L17:

          sub       r0, r0, r1
.LDBG66:
          freer     res[r2]
.L19:
.LDBG77:                             # line (27)
.L20:
.LDBG78:
          retsp     0x0 
.LDBG67:
.LDBG79:
.LDBG68:
.LDBG57:
.LDBG61:
.LDBG59:
.LDBG69:
.LDBG80:
.cc_bottom func2.function
          .section  .debug_frame, "",     @progbits
.cc_top func2.function,func2
          .align    4
          .int      .LDBG82-.LDBG81
.LDBG81:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG70
          .int      .LDBG80-.LDBG70
          .byte     0x01
          .int      .LDBG71
          .byte     0x01
          .int      .LDBG78
          .byte     0x0a
          .byte     0x01
          .int      .LDBG79
          .byte     0x0b
          .align    4, 0
.LDBG82:
.cc_bottom func2.function
.linkset func2.maxchanends, 0
.linkset func2.maxtimers, 1
.linkset func2.maxthreads, 1
          .text
.LDBG83:
.cc_top main.function,main
          .align    4
.LDBG84:
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 0
.linkset main.locnochandec, 1
.linkset main.nstackwords, 0
.LDBG120:
.LDBG107:
main:
.LDBG121:
.LDBG122:                            # line (31)
.LDBG105:
.LDBG103:
.LDBG89:
.LDBG123:                            # line (32)
.LDBG87:
.LDBG85:
.LDBG124:                            # line (18)
          getr      r1, 0x1
.LDBG108:
.L24:
.LDBG125:                            # line (8)
          setc      res[r1], 0x1
.xtaendpoint "func1,read1,start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L45:
          in        r0, res[r1] 
.LDBG109:
.L25:
.LDBG126:                            # line (11)
.xtaendpoint "func1,read1,stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L46:
          in        r0, res[r1] 
.LDBG110:
.LDBG127:                            # line (13)
.L26:

          freer     res[r1]
.L28:
.LDBG128:                            # line (20)
.L29:

.LDBG90:
.LDBG88:
.L31:
.LDBG97:
.LDBG129:                            # line (33)
.L39:
.LDBG95:
.LDBG130:                            # line (33)
.LDBG93:
.LDBG91:
.LDBG131:                            # line (25)
          getr      r1, 0x1
.LDBG111:
.L32:
.LDBG132:                            # line (8)
          setc      res[r1], 0x1
.xtaendpoint "func2,read2,start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L47:
          in        r0, res[r1] 
.LDBG112:
.L33:
.LDBG133:                            # line (11)
.xtaendpoint "func2,read2,stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L48:
          in        r0, res[r1] 
.LDBG113:
.LDBG134:                            # line (13)
.L34:

          freer     res[r1]
.L36:
.LDBG135:                            # line (27)
.L37:

.LDBG96:
.LDBG94:
.LDBG98:
.LDBG101:
.LDBG136:                            # line (34)
.L44:
.LDBG99:
.LDBG137:                            # line (34)
          getr      r1, 0x1
.LDBG114:
.L40:
.LDBG138:                            # line (8)
          setc      res[r1], 0x1
.xtaendpoint "read3,start", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 7
.L49:
          in        r0, res[r1] 
.LDBG115:
.L41:
.LDBG139:                            # line (11)
.xtaendpoint "read3,stop", "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail", "test2.xc", 10
.L50:
          in        r0, res[r1] 
.LDBG116:
.LDBG140:                            # line (13)
.L42:

          freer     res[r1]
.LDBG104:
.LDBG106:
.LDBG102:
.LDBG141:                            # line (35)
.LDBG142:
          retsp     0x0 
.LDBG117:
.LDBG143:
.LDBG118:
.LDBG86:
.LDBG92:
.LDBG100:
.LDBG119:
.LDBG144:
.cc_bottom main.function
          .section  .debug_frame, "",     @progbits
.cc_top main.function,main
          .align    4
          .int      .LDBG146-.LDBG145
.LDBG145:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG120
          .int      .LDBG144-.LDBG120
          .byte     0x01
          .int      .LDBG121
          .byte     0x01
          .int      .LDBG142
          .byte     0x0a
          .byte     0x01
          .int      .LDBG143
          .byte     0x0b
          .align    4, 0
.LDBG146:
.cc_bottom main.function
.linkset main.maxchanends, 0
.linkset main.maxtimers, 1
.linkset main.maxthreads, 1
          .text
.LDBG147:
# Thread names for recovering thread graph in linker
.LDBG148:
.extern __builtin_getid, "f{si}(0)"
.extern __builtin_getid, "f{si}(0)"
          .section  .debug_info,   "",    @progbits
.LDBG151:
          .int      .LDBG153-.LDBG152
.LDBG152:
          .short    0x0003
          .long     .LDBG149         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG148         # high address
          .asciiz   "test2.xc"
          .asciiz   "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG150         # offset in .debug_lineprog
.LDBG154:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG155:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG156:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG157:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG158:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG159:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG160:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG161:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG162:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG163:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG164:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG165:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG166:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG167:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG168:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG169:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG170:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top read.function,read
.LDBG171:
          .uleb128  0x3
          .asciiz   "read"
          .byte     0x01
          .byte     0x03
          .byte     0x01
          .int      .LDBG156-.LDBG151
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG25          # high address
          .uleb128  0x4
          .asciiz   "t"
          .byte     0x01
          .short    0x0004
          .short    0x0000
          .int      .LDBG163-.LDBG151
          .int      .LDBG172
          .section  .debug_loc,    "",    @progbits
.cc_top read.function,read
.LDBG172:
          .int      .LDBG8-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG174-.LDBG173
.LDBG173:
          .byte     0x51
.LDBG174:
          .int      0x00000000
          .int      0x00000000
.cc_bottom read.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x4
          .asciiz   "x"
          .byte     0x01
          .short    0x0005
          .short    0x0000
          .int      .LDBG157-.LDBG151
          .int      .LDBG175
          .section  .debug_loc,    "",    @progbits
.cc_top read.function,read
.LDBG175:
          .int      .LDBG9-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG177-.LDBG176
.LDBG176:
          .byte     0x52
.LDBG177:
          .int      0x00000000
          .int      0x00000000
.cc_bottom read.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x4
          .asciiz   "y"
          .byte     0x01
          .short    0x0005
          .short    0x0000
          .int      .LDBG157-.LDBG151
          .int      .LDBG178
          .section  .debug_loc,    "",    @progbits
.cc_top read.function,read
.LDBG178:
          .int      .LDBG10-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG180-.LDBG179
.LDBG179:
          .byte     0x50
.LDBG180:
          .int      0x00000000
          .int      0x00000000
.cc_bottom read.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom read.function
.cc_top func1.function,func1
.LDBG181:
          .uleb128  0x3
          .asciiz   "func1"
          .byte     0x01
          .byte     0x10
          .byte     0x01
          .int      .LDBG156-.LDBG151
          .byte     0x01
          .long     .LDBG26          # low address
          .long     .LDBG54          # high address
.LDBG182:
          .uleb128  0x5
          .long     .LDBG31          # low address
          .long     .LDBG32          # high address
          .uleb128  0x4
          .asciiz   "x"
          .byte     0x01
          .short    0x0012
          .short    0x0000
          .int      .LDBG156-.LDBG151
          .int      .LDBG183
          .section  .debug_loc,    "",    @progbits
.cc_top func1.function,func1
.LDBG183:
          .int      0x00000000
          .int      0x00000000
.cc_bottom func1.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
          .byte     0x00
.cc_bottom func1.function
.cc_top func2.function,func2
.LDBG184:
          .uleb128  0x3
          .asciiz   "func2"
          .byte     0x01
          .byte     0x17
          .byte     0x01
          .int      .LDBG156-.LDBG151
          .byte     0x01
          .long     .LDBG55          # low address
          .long     .LDBG83          # high address
.LDBG185:
          .uleb128  0x5
          .long     .LDBG60          # low address
          .long     .LDBG61          # high address
          .uleb128  0x4
          .asciiz   "x"
          .byte     0x01
          .short    0x0019
          .short    0x0000
          .int      .LDBG156-.LDBG151
          .int      .LDBG186
          .section  .debug_loc,    "",    @progbits
.cc_top func2.function,func2
.LDBG186:
          .int      0x00000000
          .int      0x00000000
.cc_bottom func2.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
          .byte     0x00
.cc_bottom func2.function
.cc_top main.function,main
.LDBG187:
          .uleb128  0x3
          .asciiz   "main"
          .byte     0x01
          .byte     0x1f
          .byte     0x01
          .int      .LDBG156-.LDBG151
          .byte     0x01
          .long     .LDBG84          # low address
          .long     .LDBG147         # high address
.LDBG188:
          .uleb128  0x5
          .long     .LDBG105         # low address
          .long     .LDBG106         # high address
          .uleb128  0x4
          .asciiz   "x"
          .byte     0x01
          .short    0x0020
          .short    0x0000
          .int      .LDBG156-.LDBG151
          .int      .LDBG189
          .section  .debug_loc,    "",    @progbits
.cc_top main.function,main
.LDBG189:
          .int      0x00000000
          .int      0x00000000
.cc_bottom main.function
          .section  .debug_info,   "",    @progbits
.LDBG190:
          .uleb128  0x5
          .long     .LDBG97          # low address
          .long     .LDBG98          # high address
          .byte     0x00
.LDBG191:
          .uleb128  0x5
          .long     .LDBG101         # low address
          .long     .LDBG102         # high address
          .byte     0x00
          .byte     0x00
          .byte     0x00
.cc_bottom main.function
          .byte     0x00
.LDBG153:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG193-.LDBG192
.LDBG192:
          .short    0x0002
          .long     .LDBG151         # offset in .debug_info
          .int      .LDBG153-.LDBG151
.cc_top read.function,read
          .int      .LDBG171-.LDBG151
          .asciiz   "read"
.cc_bottom read.function
.cc_top func1.function,func1
          .int      .LDBG181-.LDBG151
          .asciiz   "func1"
.cc_bottom func1.function
.cc_top func2.function,func2
          .int      .LDBG184-.LDBG151
          .asciiz   "func2"
.cc_bottom func2.function
.cc_top main.function,main
          .int      .LDBG187-.LDBG151
          .asciiz   "main"
.cc_bottom main.function
          .int      0x00000000
.LDBG193:
          .section  .debug_line,   "",    @progbits
.LDBG150:
          .int      .LDBG195-.LDBG194
.LDBG194:
          .short    0x0003
          .int      .LDBG197-.LDBG196
.LDBG196:
          .byte     0x01
          .byte     0x01
          .byte     0xff
          .byte     0x04
          .byte     0x10
          .byte     0x00
          .byte     0x01
          .byte     0x01
          .byte     0x01
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .asciiz   "test2.xc"
          .uleb128  0x0
          .uleb128  0x4b1f7c7c
          .uleb128  0x184
          .byte     0x00
.LDBG197:
.cc_top read.function, read
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG16
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG17
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG18
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG19
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG22
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom read.function
.cc_top func1.function, func1
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG43
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG44
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG45
          .byte     0x03
          .sleb128  0xfffffff6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG46
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG47
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG48
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG51
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom func1.function
.cc_top func2.function, func2
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG72
          .byte     0x03
          .sleb128  0x16
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG73
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG74
          .byte     0x03
          .sleb128  0xffffffef
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG75
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG76
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG77
          .byte     0x03
          .sleb128  0xe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG80
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom func2.function
.cc_top main.function, main
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG122
          .byte     0x03
          .sleb128  0x1e
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG123
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG124
          .byte     0x03
          .sleb128  0xfffffff2
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG125
          .byte     0x03
          .sleb128  0xfffffff6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG126
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG127
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG128
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG129
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG131
          .byte     0x03
          .sleb128  0xfffffff8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG132
          .byte     0x03
          .sleb128  0xffffffef
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG133
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG134
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG135
          .byte     0x03
          .sleb128  0xe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG136
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG138
          .byte     0x03
          .sleb128  0xffffffe6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG139
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG140
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG141
          .byte     0x03
          .sleb128  0x16
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG144
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom main.function
.LDBG195:
          .section  .debug_abbrev, "",    @progbits
.LDBG149:
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
          .uleb128  0x3
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
          .uleb128  0x4
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

          .byte     0x00
          .section .xtaendpointtable,       "", @progbits
.L51:
          .int      .L52-.L51
          .int      0x00000000
          .asciiz   "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail"
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L50
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L49
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L48
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L47
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L46
.cc_bottom main.function
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L45
.cc_bottom main.function
.cc_top func2.function, func2
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L23
.cc_bottom func2.function
.cc_top func2.function, func2
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L22
.cc_bottom func2.function
.cc_top func1.function, func1
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L14
.cc_bottom func1.function
.cc_top func1.function, func1
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L13
.cc_bottom func1.function
.cc_top read.function, read
          .asciiz  "test2.xc"
          .int      0x0000000b
          .long    .L5
.cc_bottom read.function
.cc_top read.function, read
          .asciiz  "test2.xc"
          .int      0x00000008
          .long    .L4
.cc_bottom read.function
.L52:
          .section .xtalabeltable,       "", @progbits
.L53:
          .int      .L54-.L53
          .int      0x00000000
          .asciiz   "C:\\peter\\git\\software\\verif_tests_sw\\Tools\\xta\\Pragmas\\xtaendpoint_backtrail"
.cc_top main.function, main
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L42
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L41
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L40
          .asciiz  "test2.xc"
          .int      0x00000022
          .int      0x00000022
          .long    .L44
          .asciiz  "test2.xc"
          .int      0x0000001b
          .int      0x0000001b
          .long    .L37
          .asciiz  "test2.xc"
          .int      0x00000019
          .int      0x00000019
          .long    .L36
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L34
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L33
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L32
          .asciiz  "test2.xc"
          .int      0x00000021
          .int      0x00000021
          .long    .L39
          .asciiz  "test2.xc"
          .int      0x00000020
          .int      0x00000020
          .long    .L31
          .asciiz  "test2.xc"
          .int      0x00000014
          .int      0x00000014
          .long    .L29
          .asciiz  "test2.xc"
          .int      0x00000012
          .int      0x00000012
          .long    .L28
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L26
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L25
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L24
.cc_bottom main.function
.cc_top func2.function, func2
          .asciiz  "test2.xc"
          .int      0x0000001b
          .int      0x0000001b
          .long    .L20
          .asciiz  "test2.xc"
          .int      0x00000019
          .int      0x00000019
          .long    .L19
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L17
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L16
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L15
.cc_bottom func2.function
.cc_top func1.function, func1
          .asciiz  "test2.xc"
          .int      0x00000014
          .int      0x00000014
          .long    .L11
          .asciiz  "test2.xc"
          .int      0x00000012
          .int      0x00000012
          .long    .L10
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L8
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L7
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L6
.cc_bottom func1.function
.cc_top read.function, read
          .asciiz  "test2.xc"
          .int      0x0000000d
          .int      0x0000000d
          .long    .L2
          .asciiz  "test2.xc"
          .int      0x0000000b
          .int      0x0000000b
          .long    .L1
          .asciiz  "test2.xc"
          .int      0x00000008
          .int      0x00000008
          .long    .L0
.cc_bottom read.function
.L54:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
