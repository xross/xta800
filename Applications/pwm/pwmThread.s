#########################################################
# XMOS Compiled Assembly File                           #
#########################################################
#       generated: Mon May 11 2009, 16:34               #
#         product: XS1-040                              #
#        compiler: XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build 1327)#
#           input: C:\\DOCUME~1\\kris\\LOCALS~1\\Temp\\ccozbaaa.xi#
#          output: pwmThread.s                          #
#########################################################
          .file     "pwmThread.xc"
          .text
          .align    2

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
.extern set_port_pull_up, "f{0}(w:p)"
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
.extern rawSampleThread, "f{0}(chd)"
.extern interpolateStage1Thread, "f{0}(chd,chd)"
.extern interpolateStage2Thread, "f{0}(chd,chd)"
.extern pwmThread, "f{0}(chd)"
.globl speaker, "o:p"
.type  speaker, @object
          .section .dp.data,       "adw", @progbits
.cc_top speaker.data, speaker
          .align    4
speaker:
          .int      0x00080300
.cc_bottom speaker.data
          .align    4
          .section .globcode,      "ax",  @progbits
          .align    2
.LC0_init:
          ldw       r0, dp[speaker] 
          setc      res[r0], 0x8     # CTRL_INUSE_ON
          ldc       r1, 0x6
          setclk    res[r0], r1
          retsp     0x0 
          .section .ctors,         "aw",  @progbits
          .align    4
          .int      .LC0_init
          .section .globcode,      "ax",  @progbits
.LC1_init:
          ldw       r0, dp[speaker] 
          setc      res[r0], 0x0     # CTRL_INUSE_OFF
          retsp     0x0 
          .section .dtors,         "aw",  @progbits
          .align    4
          .int      .LC1_init
          .text
.cc_top pwmThread.function,pwmThread
          .align    4
.LDBG4:
.globwrite pwmThread, speaker, 0, 4, "pwmThread.xc:47: error: previously used here (bytes 0..4)"
.globwrite pwmThread, speaker, 0, 4, "pwmThread.xc:100: error: previously used here (bytes 0..4)"
.call pwmThread, __builtin_macs
.globwrite pwmThread, speaker, 0, 4, "pwmThread.xc:132: error: previously used here (bytes 0..4)"
.call pwmThread, __builtin_macs
.globwrite pwmThread, speaker, 0, 4, "pwmThread.xc:190: error: previously used here (bytes 0..4)"
.set __builtin_macs, 0
.linkset __builtin_macs.locnoside, 1
.linkset __builtin_macs.locnochandec, 1
.globl pwmThread, "f{0}(chd)"
.globl pwmThread.nstackwords
.globl pwmThread.maxthreads
.globl pwmThread.maxtimers
.globl pwmThread.maxchanends
.globl pwmThread.maxsync
.type  pwmThread, @function
.linkset pwmThread.locnoside, 0
.linkset pwmThread.locnochandec, 1
.linkset pwmThread.nstackwords, 3
.LDBG68:
.LDBG11:

################
# Entry to function `pwmThread' (Implied thread 0)
################
# STACK
# -------|-workspace------
# -----2-| cycleDurationZ
# -----1-| cycleDuration
# -----0-| downFracError
# ------------------------
# r0 = [f:s2SampleChan, t:0, t:1, t:2, t:3, t:4, t:5, t:6, t:7, l:offsetHi, l:offsetLo, l:s2SampleConditionedHi, l:s2SampleConditionedLo, l:cycleDurationFracZ, l:maxFrac, t:8, t:9, t:10, t:11, t:12, t:13, t:14, t:15, t:16, t:17, t:18, t:19, t:20, t:23, t:25, t:26, t:27, t:28, t:30, t:33, t:34, t:35, t:36, t:37, t:38, t:39, t:40, t:41, t:42, t:43, t:46, t:48, t:49, t:50, t:51, t:54, t:56, t:57, t:58, t:59, t:60, t:63, t:64]
# r1 = [l:s2Sample, t:93]
# r2 = [l:downTimeStepWhole, t:47, t:55, t:68, t:_cse72, t:84, t:89, t:101, t:102, t:103, t:104, t:105, t:106, t:111, t:112, t:116, t:117, t:118]
# r3 = [l:s2SampleTime]
# r4 = [t:_cse74]
# r5 = [t:_cse71]
# r6 = [t:_cse70]
# r7 = [l:portTime]
# r8 = [l:pwmTime, t:32, t:61]
# r9 = [l:upTimeStep, l:upStepDividend, t:21, t:24, t:65, t:85, t:86, t:90, t:91, t:94, t:96, t:107, t:108, t:109, t:113, t:114]
# r10 = [l:portTimeInc, l:s2SampleConditioned, l:cycleDurationFrac, l:cycleDurationFracD, l:upStepDivisor, t:29, t:31, t:44, t:62, t:66, t:67, t:87, t:88, t:92, t:98, t:100]
# r11 = [l:cycleDurationD, t:22, t:45, t:52, t:53, t:_cse73, t:95, t:97, t:99, t:110, t:115]
# Code
pwmThread:
          extsp     0x3              # extend sp (leaf function) 3 words
.LDBG69:
.L22_bb_begin:
.LDBG12:
.LDBG70:                             # line (30)

# pwmThread.xc, line 30:
.LDBG9:
.LDBG71:                             # line (32)

# pwmThread.xc, line 32:
          ldc       r8, 0x0
.LDBG13:
.LDBG72:                             # line (35)

# pwmThread.xc, line 35:
.LDBG73:                             # line (38)

# pwmThread.xc, line 38:
          ldc       r1, 0x0
          stw       r1, sp[0x0] 
.LDBG74:                             # line (41)

# pwmThread.xc, line 41:
.L39_EndPoint:
          chkct     res[r0], 0x1 
          outct     res[r0], 0x3     # output ctrl token on `s2SampleChan'
          in        r1, res[r0]      # input on `s2SampleChan'
.LDBG14:
          outct     res[r0], 0x1     # output ctrl token on `s2SampleChan'
          chkct     res[r0], 0x1 
.LDBG75:                             # line (42)

# pwmThread.xc, line 42:
          ldc       r3, 0x0
.LDBG15:
          ldw       r6, dp[speaker] 
          ldc       r5, 0xf0
.LDBG76:                             # line (47)

# pwmThread.xc, line 47:
.L40_EndPoint:
          out       res[r6], r5      # output on `_cse70'
          syncr     res[r6] 
          getts     r7, res[r6]      # input timestamp from `_cse70'
.LDBG16:
.LDBG77:                             # line (49)

# pwmThread.xc, line 49:
          ldc       r4, 0x823
          ldw       r11, cp[.LC2_pool]
          .section .cp.const4,     "acM", @progbits, 4
.LC2_pool:
          .align    4
          .int      0x00080000
          .text
          ldw       r11, cp[.LC3_pool]
          .section .cp.const4,     "acM", @progbits, 4
.LC3_pool:
          .align    4
          .int      0x00800000
          .text
          ldw       r11, cp[.LC4_pool]
          .section .cp.const4,     "acM", @progbits, 4
.LC4_pool:
          .align    4
          .int      0x00400000
          .text
          ldw       r11, cp[.LC5_pool]
          .section .cp.const4,     "acM", @progbits, 4
.LC5_pool:
          .align    4
          .int      0x00010000
          .text
.LDBG17:
.LDBG18:
.L3_DoBegin:
.L23_bb_begin:
.LDBG7:
.LDBG78:                             # line (52)

# pwmThread.xc, line 52:
.LDBG79:                             # line (53)

# pwmThread.xc, line 53:
.LDBG80:                             # line (69)

# pwmThread.xc, line 69:
.LDBG81:                             # line (77)

# pwmThread.xc, line 77:
          add       r8, r8, r5
          ldc       r2, 0x5
          mul       r11, r8, r2
.LDBG82:                             # line (80)

# pwmThread.xc, line 80:
          add       r2, r3, r4
.LDBG83:                             # line (80)

# pwmThread.xc, line 80:
          lss       r9, r11, r2
          bf        r9, .L6_IfExpT 
.LDBG19:
.LDBG20:
.L5_IfNext:
.L26_bb_begin:
.LDBG84:                             # line (91)

# pwmThread.xc, line 91:
          ldc       r9, 0x0
          lss       r9, r1, r9
          bt        r9, .L11_IfExpT 
.LDBG21:
.LDBG22:
.xtalabel pwmThreadHiDuration2, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 103
.L28_bb_begin:
.LDBG5:
.LDBG85:                             # line (118)

# pwmThread.xc, line 118:
.LDBG86:                             # line (118)

# pwmThread.xc, line 118:
          ldw       r11, cp[.LC2_pool]
          mov       r9, r11          # move r9 <-- r11
          ldc       r10, 0x0
          ldw       r11, cp[.LC3_pool]
          maccs     r9, r10, r1, r11
.LDBG87:                             # line (120)

# pwmThread.xc, line 120:
          shl       r11, r9, 0x2
          ldc       r9, 0x1e
          shr       r9, r10, r9
          add       r10, r11, r9
.LDBG23:
.LDBG88:                             # line (123)

# pwmThread.xc, line 123:
          mul       r9, r5, r10
.LDBG24:
.LDBG89:                             # line (124)

# pwmThread.xc, line 124:
          ldw       r11, cp[.LC4_pool]
          sub       r10, r11, r10
.LDBG25:
.LDBG90:                             # line (125)

# pwmThread.xc, line 125:
          divu      r9, r9, r10
.LDBG26:
.LDBG91:                             # line (128)

# pwmThread.xc, line 128:
          shr       r10, r9, 0x2
.LDBG27:
.LDBG92:                             # line (129)

# pwmThread.xc, line 129:
          add       r7, r7, r10
.LDBG93:                             # line (132)

# pwmThread.xc, line 132:
          setpt     res[r6], r7
          ldc       r10, 0x0
.xtaendpoint pwmThreadLow2, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 131
.L41_EndPoint:
          out       res[r6], r10     # output on `_cse70'
.LDBG94:                             # line (137)

# pwmThread.xc, line 137:
          sub       r10, r9, r5
          add       r8, r8, r10
          ldc       r10, 0x5
          mul       r11, r8, r10
.LDBG95:                             # line (139)

# pwmThread.xc, line 139:
.LDBG96:                             # line (139)

# pwmThread.xc, line 139:
          lss       r10, r11, r2
          bf        r10, .L14_IfExpT 
.LDBG28:
.LDBG29:
.L13_IfNext:
.LDBG6:
.LDBG30:
.LDBG31:
.L10_IfNext:
.L31_bb_begin:
.LDBG97:                             # line (155)

# pwmThread.xc, line 155:
.LDBG98:                             # line (155)

# pwmThread.xc, line 155:
          ldw       r11, cp[.LC2_pool]
          mov       r10, r11         # move r10 <-- r11
          ldc       r2, 0x0
          ldw       r11, cp[.LC3_pool]
          maccs     r10, r2, r1, r11
.LDBG99:                             # line (157)

# pwmThread.xc, line 157:
          shl       r11, r10, 0x2
          ldc       r10, 0x1e
          shr       r2, r2, r10
          add       r10, r11, r2
.LDBG32:
.LDBG100:                            # line (159)

# pwmThread.xc, line 159:
          ldc       r2, 0x14
          shl       r2, r9, r2
          stw       r2, sp[0x2] 
.LDBG101:                            # line (160)

# pwmThread.xc, line 160:
          shr       r11, r10, 0x2
.LDBG33:
.LDBG102:                            # line (161)

# pwmThread.xc, line 161:
          ldw       r2, sp[0x2] 
          divu      r2, r2, r11
          stw       r2, sp[0x1] 
.LDBG103:                            # line (163)

# pwmThread.xc, line 163:
          ldw       r2, sp[0x1] 
          mul       r11, r2, r11
          ldw       r2, sp[0x2] 
          sub       r11, r2, r11
.LDBG104:                            # line (164)

# pwmThread.xc, line 164:
          shr       r10, r10, 0x7
.LDBG34:
.LDBG105:                            # line (165)

# pwmThread.xc, line 165:
          ldc       r2, 0xb
          shl       r2, r11, r2
          divu      r10, r2, r10
.LDBG35:
.LDBG106:                            # line (168)

# pwmThread.xc, line 168:
          ldc       r2, 0x0
          lsu       r2, r2, r10
          bt        r2, .L19_IfExpT 
.LDBG36:
.LDBG37:
.L18_IfNext:
.L33_bb_begin:
.LDBG107:                            # line (171)

# pwmThread.xc, line 171:
          ldw       r2, sp[0x1] 
          sub       r2, r2, r9
.LDBG38:
.LDBG108:                            # line (172)

# pwmThread.xc, line 172:
          ldw       r9, sp[0x0] 
          add       r9, r9, r10
          stw       r9, sp[0x0] 
.LDBG109:                            # line (175)

# pwmThread.xc, line 175:
          ldw       r9, sp[0x0] 
          ldw       r11, cp[.LC5_pool]
          lsu       r9, r9, r11
          bf        r9, .L21_IfExpT 
.LDBG39:
.LDBG40:
.L20_IfNext:
.L35_bb_begin:
.LDBG110:                            # line (183)

# pwmThread.xc, line 183:
          add       r8, r8, r2
.LDBG111:                            # line (185)

# pwmThread.xc, line 185:
          shr       r10, r2, 0x2
.LDBG41:
.LDBG112:                            # line (186)

# pwmThread.xc, line 186:
          add       r7, r7, r10
.LDBG113:                            # line (190)

# pwmThread.xc, line 190:
          setpt     res[r6], r7
.xtaendpoint pwmThreadHigh, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 189
.L42_EndPoint:
          out       res[r6], r5      # output on `_cse70'
.LDBG8:
.L36_bb_begin:
.LDBG114:                            # line (49)

# pwmThread.xc, line 49:
          bu        .L3_DoBegin 
.LDBG42:
.LDBG43:
.L6_IfExpT:
.LDBG44:
.LDBG45:
.L8_DoBegin:
.xtalabel pwmThreadLoop1, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 81
.L24_bb_begin:
.LDBG115:                            # line (83)

# pwmThread.xc, line 83:
.L43_EndPoint:
          chkct     res[r0], 0x1 
          outct     res[r0], 0x3     # output ctrl token on `s2SampleChan'
          in        r1, res[r0]      # input on `s2SampleChan'
.LDBG46:
          outct     res[r0], 0x1     # output ctrl token on `s2SampleChan'
          chkct     res[r0], 0x1 
.LDBG116:                            # line (85)

# pwmThread.xc, line 85:
          mov       r3, r2           # move r3 <-- r2
.LDBG47:
.L25_bb_begin:
          add       r2, r2, r4
.LDBG117:                            # line (80)

# pwmThread.xc, line 80:
          lss       r9, r11, r2
          bf        r9, .L8_DoBegin 
.LDBG48:
.LDBG49:
          bu        .L5_IfNext 
.LDBG50:
.LDBG51:
.L11_IfExpT:
.xtalabel pwmThreadHiDuration1, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 92
.L27_bb_begin:
.LDBG118:                            # line (95)

# pwmThread.xc, line 95:
          mov       r9, r5           # move r9 <-- r5
.LDBG52:
.LDBG119:                            # line (96)

# pwmThread.xc, line 96:
          ldc       r2, 0x3c
          add       r7, r7, r2
.LDBG120:                            # line (100)

# pwmThread.xc, line 100:
          setpt     res[r6], r7
          ldc       r2, 0x0
.xtaendpoint pwmThreadLow1, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 99
.L44_EndPoint:
          out       res[r6], r2      # output on `_cse70'
          bu        .L10_IfNext 
.LDBG53:
.LDBG54:
.L14_IfExpT:
.LDBG55:
.LDBG56:
.L16_DoBegin:
.xtalabel pwmThreadLoop2, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 140
.L29_bb_begin:
.LDBG121:                            # line (142)

# pwmThread.xc, line 142:
.L45_EndPoint:
          chkct     res[r0], 0x1 
          outct     res[r0], 0x3     # output ctrl token on `s2SampleChan'
          in        r1, res[r0]      # input on `s2SampleChan'
.LDBG57:
          outct     res[r0], 0x1     # output ctrl token on `s2SampleChan'
          chkct     res[r0], 0x1 
.LDBG122:                            # line (144)

# pwmThread.xc, line 144:
          mov       r3, r2           # move r3 <-- r2
.LDBG58:
.xtalabel pwmThreadHiDuration2, "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm", "pwmThread.xc", 103
.L30_bb_begin:
          add       r2, r2, r4
.LDBG123:                            # line (139)

# pwmThread.xc, line 139:
          lss       r10, r11, r2
          bf        r10, .L16_DoBegin 
.LDBG59:
.LDBG60:
          bu        .L13_IfNext 
.LDBG61:
.LDBG62:
.L19_IfExpT:
.L32_bb_begin:
.LDBG124:                            # line (169)

# pwmThread.xc, line 169:
          bu        .L18_IfNext 
.LDBG63:
.LDBG64:
.L21_IfExpT:
.L34_bb_begin:
.LDBG125:                            # line (177)

# pwmThread.xc, line 177:
          ldw       r9, sp[0x0] 
          ldw       r11, cp[.LC5_pool]
          sub       r9, r9, r11
          stw       r9, sp[0x0] 
.LDBG126:                            # line (178)

# pwmThread.xc, line 178:
          add       r2, r2, 0x1
          bu        .L20_IfNext 
.LDBG65:
.LDBG66:
.LDBG10:
.LDBG127:                            # line (218)

# pwmThread.xc, line 218:
.LDBG67:
.LDBG128:
.cc_bottom pwmThread.function
          .section  .debug_frame, "",     @progbits
.cc_top pwmThread.function,pwmThread
          .align    4
          .int      .LDBG130-.LDBG129
.LDBG129:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG68
          .int      .LDBG128-.LDBG68
          .byte     0x01
          .int      .LDBG69
          .byte     0x0e
          .uleb128  0xc
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .align    4, 0
.LDBG130:
.cc_bottom pwmThread.function

# PROFILE
.linkset pwmThread.maxchanends, 0
.linkset pwmThread.maxtimers, 0
.linkset pwmThread.maxthreads, 1
###############

          .text
.LDBG131:
# Thread names for recovering thread graph in linker
.LDBG132:
.extern __builtin_getid, "f{si}(0)"
.extern stdcore, "a(*:cr)"
          .section  .debug_info,   "",    @progbits
.LDBG135:
          .int      .LDBG137-.LDBG136
.LDBG136:
          .short    0x0003
          .long     .LDBG133         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG132         # high address
          .asciiz   "pwmThread.xc"
          .asciiz   "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG134         # offset in .debug_lineprog
.LDBG138:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG139:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG140:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG141:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG142:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG143:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG144:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG145:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG146:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG147:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG148:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG149:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG150:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG151:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG152:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG153:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG154:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top speaker.data,speaker
          .uleb128  0x3
          .asciiz   "speaker"
          .byte     0x01
          .short    0x000b
          .int      .LDBG149-.LDBG135
          .short    .LDBG156-.LDBG155
.LDBG155:
          .byte     0x03
          .int      speaker
.LDBG156:
.cc_bottom speaker.data
.cc_top pwmThread.function,pwmThread
.LDBG157:
          .uleb128  0x4
          .asciiz   "pwmThread"
          .byte     0x01
          .byte     0x1e
          .byte     0x01
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG131         # high address
          .uleb128  0x5
          .asciiz   "s2SampleChan"
          .byte     0x01
          .short    0x001e
          .int      .LDBG146-.LDBG135
          .int      .LDBG158
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG158:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG160-.LDBG159
.LDBG159:
          .byte     0x50
.LDBG160:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG162-.LDBG161
.LDBG161:
          .byte     0x50
.LDBG162:
          .int      .LDBG43-.LDBG0
          .int      .LDBG44-.LDBG0
          .short    .LDBG164-.LDBG163
.LDBG163:
          .byte     0x50
.LDBG164:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG166-.LDBG165
.LDBG165:
          .byte     0x50
.LDBG166:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG168-.LDBG167
.LDBG167:
          .byte     0x50
.LDBG168:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG170-.LDBG169
.LDBG169:
          .byte     0x50
.LDBG170:
          .int      .LDBG56-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG172-.LDBG171
.LDBG171:
          .byte     0x50
.LDBG172:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG174-.LDBG173
.LDBG173:
          .byte     0x50
.LDBG174:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG176-.LDBG175
.LDBG175:
          .byte     0x50
.LDBG176:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG178-.LDBG177
.LDBG177:
          .byte     0x50
.LDBG178:
          .int      .LDBG22-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG180-.LDBG179
.LDBG179:
          .byte     0x50
.LDBG180:
          .int      .LDBG12-.LDBG0
          .int      .LDBG17-.LDBG0
          .short    .LDBG182-.LDBG181
.LDBG181:
          .byte     0x50
.LDBG182:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG184-.LDBG183
.LDBG183:
          .byte     0x50
.LDBG184:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG186-.LDBG185
.LDBG185:
          .byte     0x50
.LDBG186:
          .int      .LDBG45-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG188-.LDBG187
.LDBG187:
          .byte     0x50
.LDBG188:
          .int      .LDBG51-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG190-.LDBG189
.LDBG189:
          .byte     0x50
.LDBG190:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG192-.LDBG191
.LDBG191:
          .byte     0x50
.LDBG192:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "pwmTime"
          .byte     0x01
          .short    0x0020
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG193
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG193:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG195-.LDBG194
.LDBG194:
          .byte     0x58
.LDBG195:
          .int      .LDBG22-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG197-.LDBG196
.LDBG196:
          .byte     0x58
.LDBG197:
          .int      .LDBG45-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG199-.LDBG198
.LDBG198:
          .byte     0x58
.LDBG199:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG201-.LDBG200
.LDBG200:
          .byte     0x58
.LDBG201:
          .int      .LDBG13-.LDBG0
          .int      .LDBG17-.LDBG0
          .short    .LDBG203-.LDBG202
.LDBG202:
          .byte     0x58
.LDBG203:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG205-.LDBG204
.LDBG204:
          .byte     0x58
.LDBG205:
          .int      .LDBG56-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG207-.LDBG206
.LDBG206:
          .byte     0x58
.LDBG207:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG209-.LDBG208
.LDBG208:
          .byte     0x58
.LDBG209:
          .int      .LDBG43-.LDBG0
          .int      .LDBG44-.LDBG0
          .short    .LDBG211-.LDBG210
.LDBG210:
          .byte     0x58
.LDBG211:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG213-.LDBG212
.LDBG212:
          .byte     0x58
.LDBG213:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG215-.LDBG214
.LDBG214:
          .byte     0x58
.LDBG215:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG217-.LDBG216
.LDBG216:
          .byte     0x58
.LDBG217:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG219-.LDBG218
.LDBG218:
          .byte     0x58
.LDBG219:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG221-.LDBG220
.LDBG220:
          .byte     0x58
.LDBG221:
          .int      .LDBG51-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG223-.LDBG222
.LDBG222:
          .byte     0x58
.LDBG223:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG225-.LDBG224
.LDBG224:
          .byte     0x58
.LDBG225:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG227-.LDBG226
.LDBG226:
          .byte     0x58
.LDBG227:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s2Sample"
          .byte     0x01
          .short    0x0021
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG228
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG228:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG230-.LDBG229
.LDBG229:
          .byte     0x51
.LDBG230:
          .int      .LDBG22-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG232-.LDBG231
.LDBG231:
          .byte     0x51
.LDBG232:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG234-.LDBG233
.LDBG233:
          .byte     0x51
.LDBG234:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG236-.LDBG235
.LDBG235:
          .byte     0x51
.LDBG236:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG238-.LDBG237
.LDBG237:
          .byte     0x51
.LDBG238:
          .int      .LDBG57-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG240-.LDBG239
.LDBG239:
          .byte     0x51
.LDBG240:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG242-.LDBG241
.LDBG241:
          .byte     0x51
.LDBG242:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG244-.LDBG243
.LDBG243:
          .byte     0x51
.LDBG244:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG246-.LDBG245
.LDBG245:
          .byte     0x51
.LDBG246:
          .int      .LDBG14-.LDBG0
          .int      .LDBG17-.LDBG0
          .short    .LDBG248-.LDBG247
.LDBG247:
          .byte     0x51
.LDBG248:
          .int      .LDBG46-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG250-.LDBG249
.LDBG249:
          .byte     0x51
.LDBG250:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG252-.LDBG251
.LDBG251:
          .byte     0x51
.LDBG252:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG254-.LDBG253
.LDBG253:
          .byte     0x51
.LDBG254:
          .int      .LDBG51-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG256-.LDBG255
.LDBG255:
          .byte     0x51
.LDBG256:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG258-.LDBG257
.LDBG257:
          .byte     0x51
.LDBG258:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s2SampleTime"
          .byte     0x01
          .short    0x0023
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG259
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG259:
          .int      .LDBG47-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG261-.LDBG260
.LDBG260:
          .byte     0x53
.LDBG261:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG263-.LDBG262
.LDBG262:
          .byte     0x53
.LDBG263:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG265-.LDBG264
.LDBG264:
          .byte     0x53
.LDBG265:
          .int      .LDBG58-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG267-.LDBG266
.LDBG266:
          .byte     0x53
.LDBG267:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG269-.LDBG268
.LDBG268:
          .byte     0x53
.LDBG269:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG271-.LDBG270
.LDBG270:
          .byte     0x53
.LDBG271:
          .int      .LDBG22-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG273-.LDBG272
.LDBG272:
          .byte     0x53
.LDBG273:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG275-.LDBG274
.LDBG274:
          .byte     0x53
.LDBG275:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG277-.LDBG276
.LDBG276:
          .byte     0x53
.LDBG277:
          .int      .LDBG15-.LDBG0
          .int      .LDBG17-.LDBG0
          .short    .LDBG279-.LDBG278
.LDBG278:
          .byte     0x53
.LDBG279:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG281-.LDBG280
.LDBG280:
          .byte     0x53
.LDBG281:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG283-.LDBG282
.LDBG282:
          .byte     0x53
.LDBG283:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG285-.LDBG284
.LDBG284:
          .byte     0x53
.LDBG285:
          .int      .LDBG51-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG287-.LDBG286
.LDBG286:
          .byte     0x53
.LDBG287:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG289-.LDBG288
.LDBG288:
          .byte     0x53
.LDBG289:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "portTime"
          .byte     0x01
          .short    0x0024
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG290
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG290:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG292-.LDBG291
.LDBG291:
          .byte     0x57
.LDBG292:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG294-.LDBG293
.LDBG293:
          .byte     0x57
.LDBG294:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG296-.LDBG295
.LDBG295:
          .byte     0x57
.LDBG296:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG298-.LDBG297
.LDBG297:
          .byte     0x57
.LDBG298:
          .int      .LDBG56-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG300-.LDBG299
.LDBG299:
          .byte     0x57
.LDBG300:
          .int      .LDBG43-.LDBG0
          .int      .LDBG44-.LDBG0
          .short    .LDBG302-.LDBG301
.LDBG301:
          .byte     0x57
.LDBG302:
          .int      .LDBG45-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG304-.LDBG303
.LDBG303:
          .byte     0x57
.LDBG304:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG306-.LDBG305
.LDBG305:
          .byte     0x57
.LDBG306:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG308-.LDBG307
.LDBG307:
          .byte     0x57
.LDBG308:
          .int      .LDBG16-.LDBG0
          .int      .LDBG17-.LDBG0
          .short    .LDBG310-.LDBG309
.LDBG309:
          .byte     0x57
.LDBG310:
          .int      .LDBG22-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG312-.LDBG311
.LDBG311:
          .byte     0x57
.LDBG312:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG314-.LDBG313
.LDBG313:
          .byte     0x57
.LDBG314:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG316-.LDBG315
.LDBG315:
          .byte     0x57
.LDBG316:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG318-.LDBG317
.LDBG317:
          .byte     0x57
.LDBG318:
          .int      .LDBG51-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG320-.LDBG319
.LDBG319:
          .byte     0x57
.LDBG320:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG322-.LDBG321
.LDBG321:
          .byte     0x57
.LDBG322:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG324-.LDBG323
.LDBG323:
          .byte     0x57
.LDBG324:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "portTimeInc"
          .byte     0x01
          .short    0x0025
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG325
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG325:
          .int      .LDBG41-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG327-.LDBG326
.LDBG326:
          .byte     0x5a
.LDBG327:
          .int      .LDBG27-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG329-.LDBG328
.LDBG328:
          .byte     0x5a
.LDBG329:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "downFracError"
          .byte     0x01
          .short    0x0026
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG330
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG330:
          .int      .LDBG9-.LDBG0
          .int      .LDBG10-.LDBG0
          .short    .LDBG332-.LDBG331
.LDBG331:
          .byte     0x7e
          .sleb128  0x0
.LDBG332:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
.LDBG333:
          .uleb128  0x7
          .long     .LDBG7           # low address
          .long     .LDBG8           # high address
          .uleb128  0x6
          .asciiz   "offsetHi"
          .byte     0x01
          .short    0x0034
          .short    .LDBG334-.LDBG333
          .int      .LDBG140-.LDBG135
          .int      .LDBG335
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG335:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
.LDBG334:
          .uleb128  0x6
          .asciiz   "offsetLo"
          .byte     0x01
          .short    0x0035
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG336
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG336:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s2SampleConditionedHi"
          .byte     0x01
          .short    0x0036
          .short    0x0000
          .int      .LDBG140-.LDBG135
          .int      .LDBG337
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG337:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s2SampleConditionedLo"
          .byte     0x01
          .short    0x0037
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG338
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG338:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s2SampleConditioned"
          .byte     0x01
          .short    0x0038
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG339
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG339:
          .int      .LDBG32-.LDBG0
          .int      .LDBG34-.LDBG0
          .short    .LDBG341-.LDBG340
.LDBG340:
          .byte     0x5a
.LDBG341:
          .int      .LDBG23-.LDBG0
          .int      .LDBG25-.LDBG0
          .short    .LDBG343-.LDBG342
.LDBG342:
          .byte     0x5a
.LDBG343:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "upTimeStep"
          .byte     0x01
          .short    0x003a
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG344
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG344:
          .int      .LDBG26-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG346-.LDBG345
.LDBG345:
          .byte     0x59
.LDBG346:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG348-.LDBG347
.LDBG347:
          .byte     0x59
.LDBG348:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG350-.LDBG349
.LDBG349:
          .byte     0x59
.LDBG350:
          .int      .LDBG56-.LDBG0
          .int      .LDBG59-.LDBG0
          .short    .LDBG352-.LDBG351
.LDBG351:
          .byte     0x59
.LDBG352:
          .int      .LDBG31-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG354-.LDBG353
.LDBG353:
          .byte     0x59
.LDBG354:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG356-.LDBG355
.LDBG355:
          .byte     0x59
.LDBG356:
          .int      .LDBG60-.LDBG0
          .int      .LDBG61-.LDBG0
          .short    .LDBG358-.LDBG357
.LDBG357:
          .byte     0x59
.LDBG358:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG360-.LDBG359
.LDBG359:
          .byte     0x59
.LDBG360:
          .int      .LDBG52-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG362-.LDBG361
.LDBG361:
          .byte     0x59
.LDBG362:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "downTimeStepWhole"
          .byte     0x01
          .short    0x003b
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG363
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG363:
          .int      .LDBG40-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG365-.LDBG364
.LDBG364:
          .byte     0x52
.LDBG365:
          .int      .LDBG38-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG367-.LDBG366
.LDBG366:
          .byte     0x52
.LDBG367:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG369-.LDBG368
.LDBG368:
          .byte     0x52
.LDBG369:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDuration"
          .byte     0x01
          .short    0x003c
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG370
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG370:
          .int      .LDBG7-.LDBG0
          .int      .LDBG8-.LDBG0
          .short    .LDBG372-.LDBG371
.LDBG371:
          .byte     0x7e
          .sleb128  0x4
.LDBG372:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDurationZ"
          .byte     0x01
          .short    0x003e
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG373
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG373:
          .int      .LDBG7-.LDBG0
          .int      .LDBG8-.LDBG0
          .short    .LDBG375-.LDBG374
.LDBG374:
          .byte     0x7e
          .sleb128  0x8
.LDBG375:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDurationD"
          .byte     0x01
          .short    0x003f
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG376
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG376:
          .int      .LDBG33-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG378-.LDBG377
.LDBG377:
          .byte     0x5b
.LDBG378:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDurationFrac"
          .byte     0x01
          .short    0x0041
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG379
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG379:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG381-.LDBG380
.LDBG380:
          .byte     0x5a
.LDBG381:
          .int      .LDBG35-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG383-.LDBG382
.LDBG382:
          .byte     0x5a
.LDBG383:
          .int      .LDBG62-.LDBG0
          .int      .LDBG63-.LDBG0
          .short    .LDBG385-.LDBG384
.LDBG384:
          .byte     0x5a
.LDBG385:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDurationFracZ"
          .byte     0x01
          .short    0x0042
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG386
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG386:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "cycleDurationFracD"
          .byte     0x01
          .short    0x0043
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG387
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG387:
          .int      .LDBG34-.LDBG0
          .int      .LDBG35-.LDBG0
          .short    .LDBG389-.LDBG388
.LDBG388:
          .byte     0x5a
.LDBG389:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "maxFrac"
          .byte     0x01
          .short    0x0045
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG390
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG390:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "downStepWhole"
          .byte     0x01
          .short    0x0046
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG391
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG391:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
.LDBG392:
          .uleb128  0x7
          .long     .LDBG5           # low address
          .long     .LDBG6           # high address
          .uleb128  0x6
          .asciiz   "upStepDividend"
          .byte     0x01
          .short    0x006b
          .short    .LDBG393-.LDBG392
          .int      .LDBG141-.LDBG135
          .int      .LDBG394
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG394:
          .int      .LDBG24-.LDBG0
          .int      .LDBG26-.LDBG0
          .short    .LDBG396-.LDBG395
.LDBG395:
          .byte     0x59
.LDBG396:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
.LDBG393:
          .uleb128  0x6
          .asciiz   "upStepDivisor"
          .byte     0x01
          .short    0x006c
          .short    0x0000
          .int      .LDBG141-.LDBG135
          .int      .LDBG397
          .section  .debug_loc,    "",    @progbits
.cc_top pwmThread.function,pwmThread
.LDBG397:
          .int      .LDBG25-.LDBG0
          .int      .LDBG27-.LDBG0
          .short    .LDBG399-.LDBG398
.LDBG398:
          .byte     0x5a
.LDBG399:
          .int      0x00000000
          .int      0x00000000
.cc_bottom pwmThread.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
          .byte     0x00
          .byte     0x00
.cc_bottom pwmThread.function
          .byte     0x00
.LDBG137:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG401-.LDBG400
.LDBG400:
          .short    0x0002
          .long     .LDBG135         # offset in .debug_info
          .int      .LDBG137-.LDBG135
.cc_top pwmThread.function,pwmThread
          .int      .LDBG157-.LDBG135
          .asciiz   "pwmThread"
.cc_bottom pwmThread.function
          .int      0x00000000
.LDBG401:
          .section  .debug_line,   "",    @progbits
.LDBG134:
          .int      .LDBG403-.LDBG402
.LDBG402:
          .short    0x0003
          .int      .LDBG405-.LDBG404
.LDBG404:
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
          .asciiz   "pwmThread.xc"
          .uleb128  0x0
          .uleb128  0x4a084491
          .uleb128  0x1f93
          .byte     0x00
.LDBG405:
.cc_top pwmThread.function, pwmThread
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG70
          .byte     0x03
          .sleb128  0x1d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG71
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG72
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG73
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG74
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG75
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG76
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG77
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG78
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG79
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG80
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG81
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG82
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG84
          .byte     0x03
          .sleb128  0xb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG85
          .byte     0x03
          .sleb128  0x1b
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG87
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG88
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG89
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG90
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG91
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG92
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG93
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG94
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG95
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG97
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG99
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG100
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG101
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG102
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG103
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG104
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG105
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG106
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG107
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG108
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG109
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG110
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG111
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG112
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG113
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG114
          .byte     0x03
          .sleb128  0xffffff73
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG115
          .byte     0x03
          .sleb128  0x22
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG116
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG117
          .byte     0x03
          .sleb128  0xfffffffb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG118
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG119
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG120
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG121
          .byte     0x03
          .sleb128  0x2a
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG122
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG123
          .byte     0x03
          .sleb128  0xfffffffb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG124
          .byte     0x03
          .sleb128  0x1e
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG125
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG126
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG127
          .byte     0x03
          .sleb128  0x28
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG128
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom pwmThread.function
.LDBG403:
          .section  .debug_abbrev, "",    @progbits
.LDBG133:
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
          .uleb128  0x5
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
          .uleb128  0x6
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
          .uleb128  0x7
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
.L46_xta_begin:
          .int      .L47_xta_end-.L46_xta_begin
          .int      0x00000000
          .asciiz   "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm"
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x0000008e
          .long    .L45_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x00000064
          .long    .L44_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x00000053
          .long    .L43_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x000000be
          .long    .L42_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x00000084
          .long    .L41_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x0000002f
          .long    .L40_EndPoint
.cc_bottom pwmThread.function
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x00000029
          .long    .L39_EndPoint
.cc_bottom pwmThread.function
.L47_xta_end:
          .section .xtalabeltable,       "", @progbits
.L48_xta_begin:
          .int      .L49_xta_end-.L48_xta_begin
          .int      0x00000000
          .asciiz   "H:\\Views\\Tools\\verif_tests_sw\\Tools\\xta\\Applications\\pwm"
.cc_top pwmThread.function, pwmThread
          .asciiz  "pwmThread.xc"
          .int      0x00000031
          .int      0x00000031
          .long    .L36_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x000000b7
          .int      0x000000be
          .long    .L35_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x000000b1
          .int      0x000000b2
          .long    .L34_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x000000ab
          .int      0x000000af
          .long    .L33_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x000000a9
          .int      0x000000a9
          .long    .L32_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000009b
          .int      0x000000a8
          .long    .L31_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000008b
          .int      0x0000008b
          .long    .L30_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000008e
          .int      0x00000090
          .long    .L29_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x00000076
          .int      0x0000008b
          .long    .L28_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000005f
          .int      0x00000064
          .long    .L27_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000005b
          .int      0x0000005b
          .long    .L26_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x00000050
          .int      0x00000050
          .long    .L25_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x00000053
          .int      0x00000055
          .long    .L24_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x00000034
          .int      0x00000050
          .long    .L23_bb_begin
          .asciiz  "pwmThread.xc"
          .int      0x0000001e
          .int      0x00000031
          .long    .L22_bb_begin
.cc_bottom pwmThread.function
.L49_xta_end:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build 1327)"
          .core     "XS1"
          .corerev  "REVB"

# memory access instructions: 15
# total instructions: 137
########################################
