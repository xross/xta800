          .file     "./tests/audio/src/audio_init_audioBoard.xc"
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
.extern configure_in_port_strobed_master, "f{0}(w:p,o:p,ck)"
.extern configure_out_port_strobed_master, "f{0}(w:p,o:p,ck,ui)"
.extern configure_in_port_strobed_slave, "f{0}(w:p,i:p,ck)"
.extern configure_out_port_strobed_slave, "f{0}(w:p,i:p,ck,ui)"
.extern configure_in_port_no_ready, "f{0}(w:p,ck)"
.extern configure_out_port_no_ready, "f{0}(w:p,ck,ui)"
.extern configure_port_clock_output, "f{0}(w:p,ck)"
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
.extern set_port_clock, "f{0}(w:p,ck)"
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
.extern crc32, "f{0}(&(ui),ui,ui)"
.extern crc8shr, "f{ui}(&(ui),ui,ui)"
.extern lmul, "f{ui,ui}(ui,ui,ui,ui)"
.extern mac, "f{ui,ui}(ui,ui,ui,ui)"
.extern macs, "f{si,ui}(si,si,si,ui)"
.extern pinseq, "f{0}(ui)"
.extern pinsneq, "f{0}(ui)"
.extern timerafter, "f{0}(ui)"
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
          .text
.LDBG4:
.call swc_audio_codec_ctl_reg_wr, __builtin_sync
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.call swc_audio_codec_ctl_reg_wr, __builtin_timer_after
.set __builtin_sync, 0
.linkset __builtin_sync.locnoside, 0
.linkset __builtin_sync.locnochandec, 1
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
.globl swc_audio_codec_ctl_reg_wr, "f{si}(si,si,si,p,p)"
.globl swc_audio_codec_ctl_reg_wr.nstackwords
.globl swc_audio_codec_ctl_reg_wr.maxthreads
.globl swc_audio_codec_ctl_reg_wr.maxtimers
.globl swc_audio_codec_ctl_reg_wr.maxchanends
.globl swc_audio_codec_ctl_reg_wr.maxsync
.type  swc_audio_codec_ctl_reg_wr, @function
.linkset swc_audio_codec_ctl_reg_wr.locnoside, 0
.linkset swc_audio_codec_ctl_reg_wr.locnochandec, 1
.linkset swc_audio_codec_ctl_reg_wr.nstackwords, 11
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG72:
.LDBG73:                             # line (17)
          .align    4
swc_audio_codec_ctl_reg_wr:
          extsp     0xb 
.LDBG74:
.L26:
          stw       r4, sp[0x1] 
.LDBG75:
          stw       r5, sp[0x2] 
.LDBG76:
          stw       r6, sp[0x3] 
.LDBG77:
          stw       r7, sp[0x4] 
.LDBG78:
          stw       r8, sp[0x5] 
.LDBG79:
          stw       r9, sp[0x6] 
.LDBG80:
          stw       r10, sp[0x7] 
.LDBG81:
.LDBG7:
.LDBG8:
          stw       r2, sp[0x0] 
.LDBG9:
          ldw       r11, sp[0xc] 
.LDBG10:
.LDBG82:                             # line (17)
.LDBG5:
          getr      r7, 0x1
.LDBG11:
          ldc       r10, 0x1
.LDBG83:                             # line (26)
.L41:
          out       res[r3], r10 
.LDBG84:                             # line (27)
.L42:
          out       res[r11], r10 
.LDBG85:                             # line (28)
.LDBG86:                             # line (28)
          syncr     res[r11] 
.LDBG87:                             # line (30)
          setc      res[r7], r10
.L43:
          in        r4, res[r7] 
.LDBG12:
.LDBG88:                             # line (31)
          ldc       r2, 0x7d0
          add       r4, r4, r2
.LDBG89:                             # line (32)
.LDBG90:                             # line (32)
          setd      res[r7], r4
          ldc       r9, 0x9
          setc      res[r7], r9
.L44:
          in        r2, res[r7] 
.LDBG13:
.LDBG91:                             # line (35)
.L45:
          out       res[r3], r10 
          ldc       r8, 0x0
.LDBG92:                             # line (36)
.L46:
          out       res[r11], r8 
.LDBG93:                             # line (37)
          setc      res[r7], r10
.L47:
          in        r4, res[r7] 
.LDBG94:                             # line (38)
          ldc       r6, 0x3e8
          add       r4, r4, r6
.LDBG95:                             # line (39)
.LDBG96:                             # line (39)
          setd      res[r7], r4
          setc      res[r7], r9
.L48:
          in        r2, res[r7] 
.LDBG14:
.LDBG97:                             # line (40)
.L49:
          out       res[r3], r8 
.LDBG98:                             # line (44)
          ldc       r2, 0x0
.LDBG15:
.LDBG99:                             # line (44)
.LDBG16:
.LDBG17:
.L3:
.L27:
.LDBG100:                            # line (46)
          ldc       r4, 0x7
          sub       r4, r4, r2
          ashr      r5, r0, r4
          ldc       r4, 0x1
          and       r4, r5, r4
.LDBG18:
.LDBG101:                            # line (47)
.L50:
          out       res[r11], r4 
.LDBG102:                            # line (48)
          setc      res[r7], r10
.L51:
          in        r4, res[r7] 
.LDBG19:
.LDBG103:                            # line (49)
          add       r4, r4, r6
.LDBG104:                            # line (50)
.LDBG105:                            # line (50)
          setd      res[r7], r4
          setc      res[r7], r9
.L52:
          in        r4, res[r7] 
.LDBG20:
.LDBG106:                            # line (51)
.L53:
          out       res[r3], r10 
.LDBG107:                            # line (52)
          setc      res[r7], r10
.L54:
          in        r4, res[r7] 
.LDBG21:
.LDBG108:                            # line (53)
          add       r4, r4, r6
.LDBG109:                            # line (54)
.LDBG110:                            # line (54)
          setd      res[r7], r4
          setc      res[r7], r9
.L55:
          in        r4, res[r7] 
.LDBG22:
.LDBG111:                            # line (55)
.L56:
          out       res[r3], r8 
.LDBG112:                            # line (44)
          add       r2, r2, 0x1
.L28:
          ldc       r5, 0x8
.LDBG113:                            # line (44)
          lss       r4, r2, r5
          bt        r4, .L3 
.LDBG23:
.LDBG24:
.L29:
.LDBG114:                            # line (59)
          setc      res[r11], r10
.L57:
          in        r4, res[r11] 
.LDBG25:
.LDBG115:                            # line (60)
          setc      res[r7], r10
.L58:
          in        r4, res[r7] 
.LDBG26:
.LDBG116:                            # line (61)
          add       r4, r4, r6
.LDBG117:                            # line (62)
.LDBG118:                            # line (62)
          setd      res[r7], r4
          setc      res[r7], r9
.L59:
          in        r0, res[r7] 
.LDBG27:
.LDBG119:                            # line (63)
.L60:
          out       res[r3], r10 
          ldaw      r2, sp[0x8] 
          in        r0, res[r11] 
          stw       r0, r2[0x0] 
.LDBG120:                            # line (67)
          setc      res[r7], r10
.L61:
          in        r4, res[r7] 
.LDBG121:                            # line (68)
          add       r4, r4, r6
.LDBG122:                            # line (69)
.LDBG123:                            # line (69)
          setd      res[r7], r4
          setc      res[r7], r9
.L62:
          in        r0, res[r7] 
.LDBG28:
.LDBG124:                            # line (70)
.L63:
          out       res[r3], r8 
.LDBG125:                            # line (72)
          ldc       r0, 0xff
          and       r0, r1, r0
.LDBG29:
.LDBG126:                            # line (75)
          ldc       r2, 0x0
.LDBG30:
.LDBG127:                            # line (75)
.LDBG31:
.LDBG32:
.L9:
.L30:
.LDBG128:                            # line (77)
          ldc       r1, 0x7
          sub       r1, r1, r2
          ashr      r4, r0, r1
          ldc       r1, 0x1
          and       r4, r4, r1
.LDBG33:
.LDBG129:                            # line (78)
.L64:
          out       res[r11], r4 
.LDBG130:                            # line (79)
          setc      res[r7], r10
.L65:
          in        r4, res[r7] 
.LDBG34:
.LDBG131:                            # line (80)
          add       r4, r4, r6
.LDBG132:                            # line (81)
.LDBG133:                            # line (81)
          setd      res[r7], r4
          setc      res[r7], r9
.L66:
          in        r1, res[r7] 
.LDBG35:
.LDBG134:                            # line (82)
.L67:
          out       res[r3], r10 
.LDBG135:                            # line (83)
          setc      res[r7], r10
.L68:
          in        r4, res[r7] 
.LDBG136:                            # line (84)
          add       r4, r4, r6
.LDBG137:                            # line (85)
.LDBG138:                            # line (85)
          setd      res[r7], r4
          setc      res[r7], r9
.L69:
          in        r1, res[r7] 
.LDBG36:
.LDBG139:                            # line (86)
.L70:
          out       res[r3], r8 
.LDBG140:                            # line (75)
          add       r2, r2, 0x1
.L31:
.LDBG141:                            # line (75)
          lss       r1, r2, r5
          bt        r1, .L9 
.LDBG37:
.LDBG38:
.L32:
.L71:
          in        r4, res[r11] 
.LDBG39:
.LDBG142:                            # line (90)
          setc      res[r7], r10
.L72:
          in        r4, res[r7] 
.LDBG40:
.LDBG143:                            # line (91)
          add       r4, r4, r6
.LDBG144:                            # line (92)
.LDBG145:                            # line (92)
          setd      res[r7], r4
          setc      res[r7], r9
.L73:
          in        r0, res[r7] 
.LDBG41:
.LDBG146:                            # line (93)
.L74:
          out       res[r3], r10 
          ldaw      r1, sp[0x8] 
          in        r0, res[r11] 
          stw       r0, r1[0x1] 
.LDBG147:                            # line (96)
          setc      res[r7], r10
.L75:
          in        r4, res[r7] 
.LDBG148:                            # line (97)
          add       r4, r4, r6
.LDBG149:                            # line (98)
.LDBG150:                            # line (98)
          setd      res[r7], r4
          setc      res[r7], r9
.L76:
          in        r0, res[r7] 
.LDBG42:
.LDBG151:                            # line (99)
.L77:
          out       res[r3], r8 
.LDBG152:                            # line (101)
          ldw       r1, sp[0x0] 
          ldc       r0, 0xff
          and       r0, r1, r0
.LDBG43:
.LDBG153:                            # line (103)
          ldc       r2, 0x0
.LDBG44:
.LDBG154:                            # line (103)
.LDBG45:
.LDBG46:
.L15:
.L33:
.LDBG155:                            # line (105)
          ldc       r1, 0x7
          sub       r1, r1, r2
          ashr      r4, r0, r1
          ldc       r1, 0x1
          and       r4, r4, r1
.LDBG47:
.LDBG156:                            # line (106)
.L78:
          out       res[r11], r4 
.LDBG157:                            # line (107)
          setc      res[r7], r10
.L79:
          in        r4, res[r7] 
.LDBG48:
.LDBG158:                            # line (108)
          add       r4, r4, r6
.LDBG159:                            # line (109)
.LDBG160:                            # line (109)
          setd      res[r7], r4
          setc      res[r7], r9
.L80:
          in        r1, res[r7] 
.LDBG49:
.LDBG161:                            # line (110)
.L81:
          out       res[r3], r10 
.LDBG162:                            # line (111)
          setc      res[r7], r10
.L82:
          in        r4, res[r7] 
.LDBG163:                            # line (112)
          add       r4, r4, r6
.LDBG164:                            # line (113)
.LDBG165:                            # line (113)
          setd      res[r7], r4
          setc      res[r7], r9
.L83:
          in        r1, res[r7] 
.LDBG50:
.LDBG166:                            # line (114)
.L84:
          out       res[r3], r8 
.LDBG167:                            # line (103)
          add       r2, r2, 0x1
.L34:
.LDBG168:                            # line (103)
          lss       r1, r2, r5
          bt        r1, .L15 
.LDBG51:
.LDBG52:
.L35:
.L85:
          in        r4, res[r11] 
.LDBG53:
.LDBG169:                            # line (118)
          setc      res[r7], r10
.L86:
          in        r4, res[r7] 
.LDBG54:
.LDBG170:                            # line (119)
          add       r4, r4, r6
.LDBG171:                            # line (120)
.LDBG172:                            # line (120)
          setd      res[r7], r4
          setc      res[r7], r9
.L87:
          in        r0, res[r7] 
.LDBG55:
.LDBG173:                            # line (121)
.L88:
          out       res[r3], r10 
          ldaw      r1, sp[0x8] 
          in        r0, res[r11] 
          stw       r0, r1[0x2] 
.LDBG174:                            # line (124)
          setc      res[r7], r10
.L89:
          in        r4, res[r7] 
.LDBG175:                            # line (125)
          add       r4, r4, r6
.LDBG176:                            # line (126)
.LDBG177:                            # line (126)
          setd      res[r7], r4
          setc      res[r7], r9
.L90:
          in        r0, res[r7] 
.LDBG56:
.LDBG178:                            # line (127)
.L91:
          out       res[r3], r8 
.LDBG179:                            # line (128)
          setc      res[r7], r10
.L92:
          in        r4, res[r7] 
.LDBG180:                            # line (129)
          add       r4, r4, r6
.LDBG181:                            # line (130)
.LDBG182:                            # line (130)
          setd      res[r7], r4
          setc      res[r7], r9
.L93:
          in        r0, res[r7] 
.LDBG57:
.LDBG183:                            # line (131)
.L94:
          out       res[r3], r10 
.LDBG184:                            # line (133)
.L95:
          out       res[r11], r10 
.LDBG185:                            # line (135)
          ldc       r0, 0x0
.LDBG58:
.LDBG186:                            # line (136)
          ldc       r2, 0x0
.LDBG59:
.LDBG187:                            # line (136)
.LDBG60:
.LDBG61:
.L21:
.L36:
.LDBG188:                            # line (138)
          ldaw      r3, sp[0x8] 
          ldc       r1, 0x3
          lsu       r1, r2, r1
          ecallf    r1
          ldw       r3, r3[r2] 
          ldc       r1, 0x1
          and       r1, r3, r1
.LDBG189:                            # line (138)
          bt        r1, .L25 
.LDBG62:
.LDBG63:
.L24:
.L38:
.LDBG190:                            # line (136)
          add       r2, r2, 0x1
.L39:
.LDBG191:                            # line (136)
          ldc       r1, 0x3
          lss       r1, r2, r1
          bt        r1, .L21 
.LDBG64:
.LDBG65:
.L40:
.LDBG192:                            # line (144)
          freer     res[r7]
.LDBG193:
          ldw       r4, sp[0x1] 
.LDBG194:
          ldw       r5, sp[0x2] 
.LDBG195:
          ldw       r6, sp[0x3] 
.LDBG196:
          ldw       r7, sp[0x4] 
.LDBG197:
          ldw       r8, sp[0x5] 
.LDBG198:
          ldw       r9, sp[0x6] 
.LDBG199:
          ldw       r10, sp[0x7] 
.LDBG200:
          ldaw      sp, sp[0xb] 
          retsp     0x0 
.LDBG6:
.LDBG66:
.LDBG201:
.LDBG67:
.LDBG68:
.LDBG69:
.L25:
.L37:
.LDBG202:                            # line (140)
          ldc       r1, 0x1
          shl       r1, r1, r2
          or        r0, r0, r1
          bu        .L24 
.LDBG70:
.LDBG71:
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.LDBG203:
          .section  .debug_frame, "",     @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
          .align    4
          .int      .LDBG205-.LDBG204
.LDBG204:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG72
          .int      .LDBG203-.LDBG72
          .byte     0x01
          .int      .LDBG74
          .byte     0x0e
          .uleb128  0x2c
          .byte     0x01
          .int      .LDBG75
          .byte     0x84
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG76
          .byte     0x85
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG77
          .byte     0x86
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG78
          .byte     0x87
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG79
          .byte     0x88
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG80
          .byte     0x89
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG81
          .byte     0x8a
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG193
          .byte     0x0a
          .byte     0x01
          .int      .LDBG194
          .byte     0xc4
          .byte     0x01
          .int      .LDBG195
          .byte     0xc5
          .byte     0x01
          .int      .LDBG196
          .byte     0xc6
          .byte     0x01
          .int      .LDBG197
          .byte     0xc7
          .byte     0x01
          .int      .LDBG198
          .byte     0xc8
          .byte     0x01
          .int      .LDBG199
          .byte     0xc9
          .byte     0x01
          .int      .LDBG200
          .byte     0xca
          .byte     0x01
          .int      .LDBG201
          .byte     0x0b
          .align    4, 0
.LDBG205:
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.linkset swc_audio_codec_ctl_reg_wr.maxchanends, 0
.linkset swc_audio_codec_ctl_reg_wr.maxtimers, 1
.linkset swc_audio_codec_ctl_reg_wr.maxthreads, 1
          .text
.LDBG206:
.LDBG207:
.call swc_audio_codec_ctl_reg_rd, __builtin_sync
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_sync
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.call swc_audio_codec_ctl_reg_rd, __builtin_timer_after
.globl swc_audio_codec_ctl_reg_rd, "f{si}(si,si,p,p)"
.globl swc_audio_codec_ctl_reg_rd.nstackwords
.globl swc_audio_codec_ctl_reg_rd.maxthreads
.globl swc_audio_codec_ctl_reg_rd.maxtimers
.globl swc_audio_codec_ctl_reg_rd.maxchanends
.globl swc_audio_codec_ctl_reg_rd.maxsync
.type  swc_audio_codec_ctl_reg_rd, @function
.linkset swc_audio_codec_ctl_reg_rd.locnoside, 0
.linkset swc_audio_codec_ctl_reg_rd.locnochandec, 1
.linkset swc_audio_codec_ctl_reg_rd.nstackwords, 12
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG301:
.LDBG302:                            # line (151)
          .align    4
swc_audio_codec_ctl_reg_rd:
          extsp     0xc 
.LDBG303:
.L128:
          stw       r4, sp[0x1] 
.LDBG304:
          stw       r5, sp[0x2] 
.LDBG305:
          stw       r6, sp[0x3] 
.LDBG306:
          stw       r7, sp[0x4] 
.LDBG307:
          stw       r8, sp[0x5] 
.LDBG308:
          stw       r9, sp[0x6] 
.LDBG309:
          stw       r10, sp[0x7] 
.LDBG310:
.LDBG212:
          stw       r1, sp[0x0] 
.LDBG213:
.LDBG214:
.LDBG311:                            # line (151)
.LDBG210:
          getr      r6, 0x1
.LDBG215:
          ldc       r10, 0x1
.LDBG312:                            # line (161)
.L146:
          out       res[r2], r10 
.LDBG313:                            # line (162)
.L147:
          out       res[r3], r10 
.LDBG314:                            # line (163)
.LDBG315:                            # line (163)
          syncr     res[r3] 
.LDBG316:                            # line (164)
          setc      res[r6], r10
.L148:
          in        r1, res[r6] 
.LDBG216:
.LDBG317:                            # line (165)
          ldc       r4, 0x7d0
          add       r1, r1, r4
.LDBG318:                            # line (166)
.LDBG319:                            # line (166)
          setd      res[r6], r1
          ldc       r9, 0x9
          setc      res[r6], r9
.L149:
          in        r1, res[r6] 
.LDBG217:
.LDBG320:                            # line (168)
.L150:
          out       res[r2], r10 
          ldc       r8, 0x0
.LDBG321:                            # line (169)
.L151:
          out       res[r3], r8 
.LDBG322:                            # line (170)
          setc      res[r6], r10
.L152:
          in        r1, res[r6] 
.LDBG218:
.LDBG323:                            # line (171)
          ldc       r7, 0x3e8
          add       r1, r1, r7
.LDBG324:                            # line (172)
.LDBG325:                            # line (172)
          setd      res[r6], r1
          setc      res[r6], r9
.L153:
          in        r1, res[r6] 
.LDBG219:
.LDBG326:                            # line (173)
.L154:
          out       res[r2], r8 
.LDBG327:                            # line (176)
          ldc       r4, 0x0
.LDBG220:
.LDBG328:                            # line (176)
.LDBG221:
.LDBG222:
.L99:
.L129:
.LDBG329:                            # line (178)
          ldc       r1, 0x7
          sub       r1, r1, r4
          ashr      r5, r0, r1
          ldc       r1, 0x1
          and       r1, r5, r1
.LDBG223:
.LDBG330:                            # line (179)
.L155:
          out       res[r3], r1 
.LDBG331:                            # line (180)
          setc      res[r6], r10
.L156:
          in        r1, res[r6] 
.LDBG224:
.LDBG332:                            # line (181)
          add       r1, r1, r7
.LDBG333:                            # line (182)
.LDBG334:                            # line (182)
          setd      res[r6], r1
          setc      res[r6], r9
.L157:
          in        r1, res[r6] 
.LDBG225:
.LDBG335:                            # line (183)
.L158:
          out       res[r2], r10 
.LDBG336:                            # line (184)
          setc      res[r6], r10
.L159:
          in        r1, res[r6] 
.LDBG226:
.LDBG337:                            # line (185)
          add       r1, r1, r7
.LDBG338:                            # line (186)
.LDBG339:                            # line (186)
          setd      res[r6], r1
          setc      res[r6], r9
.L160:
          in        r1, res[r6] 
.LDBG227:
.LDBG340:                            # line (187)
.L161:
          out       res[r2], r8 
.LDBG341:                            # line (176)
          add       r4, r4, 0x1
.L130:
          ldc       r5, 0x8
.LDBG342:                            # line (176)
          lss       r1, r4, r5
          bt        r1, .L99 
.LDBG228:
.LDBG229:
.L131:
.LDBG343:                            # line (190)
          setc      res[r3], r10
.L162:
          in        r1, res[r3] 
.LDBG230:
.LDBG344:                            # line (191)
          setc      res[r6], r10
.L163:
          in        r1, res[r6] 
.LDBG231:
.LDBG345:                            # line (192)
          add       r1, r1, r7
.LDBG346:                            # line (193)
.LDBG347:                            # line (193)
          setd      res[r6], r1
          setc      res[r6], r9
.L164:
          in        r1, res[r6] 
.LDBG232:
.LDBG348:                            # line (194)
.L165:
          out       res[r2], r10 
          ldaw      r4, sp[0x8] 
          in        r1, res[r3] 
          stw       r1, r4[0x0] 
.LDBG349:                            # line (197)
          setc      res[r6], r10
.L166:
          in        r1, res[r6] 
.LDBG233:
.LDBG350:                            # line (198)
          add       r1, r1, r7
.LDBG351:                            # line (199)
.LDBG352:                            # line (199)
          setd      res[r6], r1
          setc      res[r6], r9
.L167:
          in        r1, res[r6] 
.LDBG234:
.LDBG353:                            # line (200)
.L168:
          out       res[r2], r8 
.LDBG354:                            # line (202)
          ldc       r4, 0xff
          ldw       r1, sp[0x0] 
          and       r1, r1, r4
          stw       r1, sp[0xb] 
.LDBG355:                            # line (204)
          ldc       r4, 0x0
.LDBG235:
.LDBG356:                            # line (204)
.LDBG236:
.LDBG237:
.L105:
.L132:
.LDBG357:                            # line (206)
          ldc       r1, 0x7
          sub       r11, r1, r4
          ldw       r1, sp[0xb] 
          ashr      r11, r1, r11
          ldc       r1, 0x1
          and       r1, r11, r1
.LDBG238:
.LDBG358:                            # line (207)
.L169:
          out       res[r3], r1 
.LDBG359:                            # line (208)
          setc      res[r6], r10
.L170:
          in        r1, res[r6] 
.LDBG239:
.LDBG360:                            # line (209)
          add       r1, r1, r7
.LDBG361:                            # line (210)
.LDBG362:                            # line (210)
          setd      res[r6], r1
          setc      res[r6], r9
.L171:
          in        r1, res[r6] 
.LDBG240:
.LDBG363:                            # line (211)
.L172:
          out       res[r2], r10 
.LDBG364:                            # line (212)
          setc      res[r6], r10
.L173:
          in        r1, res[r6] 
.LDBG241:
.LDBG365:                            # line (213)
          add       r1, r1, r7
.LDBG366:                            # line (214)
.LDBG367:                            # line (214)
          setd      res[r6], r1
          setc      res[r6], r9
.L174:
          in        r1, res[r6] 
.LDBG242:
.LDBG368:                            # line (215)
.L175:
          out       res[r2], r8 
.LDBG369:                            # line (204)
          add       r4, r4, 0x1
.L133:
.LDBG370:                            # line (204)
          lss       r1, r4, r5
          bt        r1, .L105 
.LDBG243:
.LDBG244:
.L134:
.L176:
          in        r1, res[r3] 
.LDBG245:
.LDBG371:                            # line (219)
          setc      res[r6], r10
.L177:
          in        r1, res[r6] 
.LDBG246:
.LDBG372:                            # line (220)
          add       r1, r1, r7
.LDBG373:                            # line (221)
.LDBG374:                            # line (221)
          setd      res[r6], r1
          setc      res[r6], r9
.L178:
          in        r1, res[r6] 
.LDBG247:
.LDBG375:                            # line (222)
.L179:
          out       res[r2], r10 
          ldaw      r4, sp[0x8] 
          in        r1, res[r3] 
          stw       r1, r4[0x1] 
.LDBG376:                            # line (225)
          setc      res[r6], r10
.L180:
          in        r1, res[r6] 
.LDBG248:
.LDBG377:                            # line (226)
          add       r1, r1, r7
.LDBG378:                            # line (227)
.LDBG379:                            # line (227)
          setd      res[r6], r1
          setc      res[r6], r9
.L181:
          in        r1, res[r6] 
.LDBG249:
.LDBG380:                            # line (228)
.L182:
          out       res[r2], r8 
.LDBG381:                            # line (232)
          setc      res[r6], r10
.L183:
          in        r1, res[r6] 
.LDBG250:
.LDBG382:                            # line (233)
          ldc       r4, 0x7d0
          add       r1, r1, r4
.LDBG383:                            # line (234)
.LDBG384:                            # line (234)
          setd      res[r6], r1
          setc      res[r6], r9
.L184:
          in        r4, res[r6] 
.LDBG251:
.LDBG385:                            # line (236)
.L185:
          out       res[r2], r10 
.LDBG386:                            # line (237)
.L186:
          out       res[r3], r10 
.LDBG387:                            # line (238)
          ldc       r4, 0x7d0
          add       r1, r1, r4
.LDBG388:                            # line (239)
.LDBG389:                            # line (239)
          setd      res[r6], r1
.L187:
          in        r4, res[r6] 
.LDBG252:
.LDBG390:                            # line (240)
.L188:
          out       res[r2], r8 
.LDBG391:                            # line (241)
          ldc       r4, 0x7d0
          add       r1, r1, r4
.LDBG392:                            # line (242)
.LDBG393:                            # line (242)
          setd      res[r6], r1
.L189:
          in        r1, res[r6] 
.LDBG253:
.LDBG394:                            # line (246)
.L190:
          out       res[r2], r10 
.LDBG395:                            # line (247)
.L191:
          out       res[r3], r10 
.LDBG396:                            # line (248)
.LDBG397:                            # line (248)
          syncr     res[r3] 
.LDBG398:                            # line (249)
          setc      res[r6], r10
.L192:
          in        r1, res[r6] 
.LDBG254:
.LDBG399:                            # line (250)
          ldc       r4, 0x7d0
          add       r1, r1, r4
.LDBG400:                            # line (251)
.LDBG401:                            # line (251)
          setd      res[r6], r1
          setc      res[r6], r9
.L193:
          in        r1, res[r6] 
.LDBG255:
.LDBG402:                            # line (253)
.L194:
          out       res[r2], r10 
.LDBG403:                            # line (254)
.L195:
          out       res[r3], r8 
.LDBG404:                            # line (255)
          setc      res[r6], r10
.L196:
          in        r1, res[r6] 
.LDBG256:
.LDBG405:                            # line (256)
          add       r1, r1, r7
.LDBG406:                            # line (257)
.LDBG407:                            # line (257)
          setd      res[r6], r1
          setc      res[r6], r9
.L197:
          in        r1, res[r6] 
.LDBG257:
.LDBG408:                            # line (258)
.L198:
          out       res[r2], r8 
.LDBG409:                            # line (261)
          ldc       r4, 0x0
.LDBG258:
.LDBG410:                            # line (261)
.LDBG259:
.LDBG260:
.L111:
.L135:
.LDBG208:
.LDBG411:                            # line (263)
          ldc       r1, 0x1
          or        r11, r0, r1
.LDBG412:                            # line (264)
          ldc       r1, 0x7
          sub       r1, r1, r4
          ashr      r11, r11, r1
          ldc       r1, 0x1
          and       r1, r11, r1
.LDBG261:
.LDBG413:                            # line (265)
.L199:
          out       res[r3], r1 
.LDBG414:                            # line (266)
          setc      res[r6], r10
.L200:
          in        r1, res[r6] 
.LDBG262:
.LDBG415:                            # line (267)
          add       r1, r1, r7
.LDBG416:                            # line (268)
.LDBG417:                            # line (268)
          setd      res[r6], r1
          setc      res[r6], r9
.L201:
          in        r1, res[r6] 
.LDBG263:
.LDBG418:                            # line (269)
.L202:
          out       res[r2], r10 
.LDBG419:                            # line (270)
          setc      res[r6], r10
.L203:
          in        r1, res[r6] 
.LDBG264:
.LDBG420:                            # line (271)
          add       r1, r1, r7
.LDBG421:                            # line (272)
.LDBG422:                            # line (272)
          setd      res[r6], r1
          setc      res[r6], r9
.L204:
          in        r1, res[r6] 
.LDBG265:
.LDBG423:                            # line (273)
.L205:
          out       res[r2], r8 
.LDBG209:
.LDBG424:                            # line (261)
          add       r4, r4, 0x1
.L136:
.LDBG425:                            # line (261)
          lss       r1, r4, r5
          bt        r1, .L111 
.LDBG266:
.LDBG267:
.L137:
.L206:
          in        r1, res[r3] 
.LDBG268:
.LDBG426:                            # line (277)
          setc      res[r6], r10
.L207:
          in        r1, res[r6] 
.LDBG269:
.LDBG427:                            # line (278)
          add       r1, r1, r7
.LDBG428:                            # line (279)
.LDBG429:                            # line (279)
          setd      res[r6], r1
          setc      res[r6], r9
.L208:
          in        r0, res[r6] 
.LDBG270:
.LDBG430:                            # line (280)
.L209:
          out       res[r2], r10 
          ldaw      r1, sp[0x8] 
          in        r0, res[r3] 
          stw       r0, r1[0x0] 
.LDBG431:                            # line (283)
          setc      res[r6], r10
.L210:
          in        r1, res[r6] 
.LDBG432:                            # line (284)
          add       r1, r1, r7
.LDBG433:                            # line (285)
.LDBG434:                            # line (285)
          setd      res[r6], r1
          setc      res[r6], r9
.L211:
          in        r0, res[r6] 
.LDBG271:
.LDBG435:                            # line (286)
.L212:
          out       res[r2], r8 
.LDBG436:                            # line (289)
.LDBG437:                            # line (291)
          ldc       r4, 0x0
.LDBG272:
.LDBG438:                            # line (291)
.LDBG273:
.LDBG274:
.L117:
.L138:
.LDBG439:                            # line (294)
          setc      res[r6], r10
.L213:
          in        r1, res[r6] 
.LDBG275:
.LDBG440:                            # line (295)
          add       r1, r1, r7
.LDBG441:                            # line (296)
.LDBG442:                            # line (296)
          setd      res[r6], r1
          setc      res[r6], r9
.L214:
          in        r0, res[r6] 
.LDBG276:
.LDBG443:                            # line (297)
.L215:
          out       res[r2], r10 
          in        r1, res[r3] 
.LDBG277:
.LDBG444:                            # line (300)
.LDBG445:                            # line (302)
          setc      res[r6], r10
.L216:
          in        r1, res[r6] 
.LDBG278:
.LDBG446:                            # line (303)
          add       r1, r1, r7
.LDBG447:                            # line (304)
.LDBG448:                            # line (304)
          setd      res[r6], r1
          setc      res[r6], r9
.L217:
          in        r0, res[r6] 
.LDBG279:
.LDBG449:                            # line (305)
.L218:
          out       res[r2], r8 
.LDBG450:                            # line (291)
          add       r4, r4, 0x1
.L139:
.LDBG451:                            # line (291)
          lss       r0, r4, r5
          bt        r0, .L117 
.LDBG280:
.LDBG281:
.L140:
.L219:
          in        r1, res[r3] 
.LDBG282:
.LDBG452:                            # line (310)
          setc      res[r6], r10
.L220:
          in        r1, res[r6] 
.LDBG283:
.LDBG453:                            # line (311)
          add       r1, r1, r7
.LDBG454:                            # line (312)
.LDBG455:                            # line (312)
          setd      res[r6], r1
          setc      res[r6], r9
.L221:
          in        r0, res[r6] 
.LDBG284:
.LDBG456:                            # line (313)
.L222:
          out       res[r2], r10 
          ldaw      r1, sp[0x8] 
          in        r0, res[r3] 
          stw       r0, r1[0x2] 
.LDBG457:                            # line (316)
          setc      res[r6], r10
.L223:
          in        r1, res[r6] 
.LDBG458:                            # line (317)
          add       r1, r1, r7
.LDBG459:                            # line (318)
.LDBG460:                            # line (318)
          setd      res[r6], r1
          setc      res[r6], r9
.L224:
          in        r0, res[r6] 
.LDBG285:
.LDBG461:                            # line (319)
.L225:
          out       res[r2], r8 
.LDBG462:                            # line (320)
          setc      res[r6], r10
.L226:
          in        r1, res[r6] 
.LDBG463:                            # line (321)
          add       r1, r1, r7
.LDBG464:                            # line (322)
.LDBG465:                            # line (322)
          setd      res[r6], r1
          setc      res[r6], r9
.L227:
          in        r0, res[r6] 
.LDBG286:
.LDBG466:                            # line (323)
.L228:
          out       res[r2], r10 
.LDBG467:                            # line (325)
.L229:
          out       res[r3], r10 
.LDBG468:                            # line (327)
          ldc       r0, 0x0
.LDBG287:
.LDBG469:                            # line (328)
          ldc       r4, 0x0
.LDBG288:
.LDBG470:                            # line (328)
.LDBG289:
.LDBG290:
.L123:
.L141:
.LDBG471:                            # line (330)
          ldaw      r2, sp[0x8] 
          ldc       r1, 0x3
          lsu       r1, r4, r1
          ecallf    r1
          ldw       r2, r2[r4] 
          ldc       r1, 0x1
          and       r1, r2, r1
.LDBG472:                            # line (330)
          bt        r1, .L127 
.LDBG291:
.LDBG292:
.L126:
.L143:
.LDBG473:                            # line (328)
          add       r4, r4, 0x1
.L144:
.LDBG474:                            # line (328)
          ldc       r1, 0x3
          lss       r1, r4, r1
          bt        r1, .L123 
.LDBG293:
.LDBG294:
.L145:
.LDBG475:                            # line (336)
          freer     res[r6]
.LDBG476:
          ldw       r4, sp[0x1] 
.LDBG477:
          ldw       r5, sp[0x2] 
.LDBG478:
          ldw       r6, sp[0x3] 
.LDBG479:
          ldw       r7, sp[0x4] 
.LDBG480:
          ldw       r8, sp[0x5] 
.LDBG481:
          ldw       r9, sp[0x6] 
.LDBG482:
          ldw       r10, sp[0x7] 
.LDBG483:
          ldaw      sp, sp[0xc] 
          retsp     0x0 
.LDBG211:
.LDBG295:
.LDBG484:
.LDBG296:
.LDBG297:
.LDBG298:
.L127:
.L142:
.LDBG485:                            # line (332)
          ldc       r1, 0x1
          shl       r1, r1, r4
          or        r0, r0, r1
          bu        .L126 
.LDBG299:
.LDBG300:
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.LDBG486:
          .section  .debug_frame, "",     @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
          .align    4
          .int      .LDBG488-.LDBG487
.LDBG487:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG301
          .int      .LDBG486-.LDBG301
          .byte     0x01
          .int      .LDBG303
          .byte     0x0e
          .uleb128  0x30
          .byte     0x01
          .int      .LDBG304
          .byte     0x84
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG305
          .byte     0x85
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG306
          .byte     0x86
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG307
          .byte     0x87
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG308
          .byte     0x88
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG309
          .byte     0x89
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG310
          .byte     0x8a
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG476
          .byte     0x0a
          .byte     0x01
          .int      .LDBG477
          .byte     0xc4
          .byte     0x01
          .int      .LDBG478
          .byte     0xc5
          .byte     0x01
          .int      .LDBG479
          .byte     0xc6
          .byte     0x01
          .int      .LDBG480
          .byte     0xc7
          .byte     0x01
          .int      .LDBG481
          .byte     0xc8
          .byte     0x01
          .int      .LDBG482
          .byte     0xc9
          .byte     0x01
          .int      .LDBG483
          .byte     0xca
          .byte     0x01
          .int      .LDBG484
          .byte     0x0b
          .align    4, 0
.LDBG488:
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.linkset swc_audio_codec_ctl_reg_rd.maxchanends, 0
.linkset swc_audio_codec_ctl_reg_rd.maxtimers, 1
.linkset swc_audio_codec_ctl_reg_rd.maxthreads, 1
          .text
.LDBG489:
.LDBG490:
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.call audio_init, swc_audio_codec_ctl_reg_wr
.globl audio_init, "f{0}(o:p,p,p,p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,i:p,ck)"
.globl audio_init.nstackwords
.globl audio_init.maxthreads
.globl audio_init.maxtimers
.globl audio_init.maxchanends
.globl audio_init.maxsync
.type  audio_init, @function
.linkset audio_init.locnoside, 1
.linkset audio_init.locnochandec, 1
.linkset .LLNK10, swc_audio_codec_ctl_reg_wr.nstackwords $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK9, .LLNK10 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK8, .LLNK9 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK7, .LLNK8 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK6, .LLNK7 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK5, .LLNK6 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK4, .LLNK5 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK3, .LLNK4 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK2, .LLNK3 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 6
.linkset audio_init.nstackwords, .LLNK0
.cc_top audio_init.function,audio_init
.LDBG507:
.LDBG508:                            # line (357)
          .align    4
audio_init:
          entsp     0x6 
.LDBG509:
.L230:
          stw       r4, sp[0x2] 
.LDBG510:
          stw       r5, sp[0x3] 
.LDBG511:
          stw       r6, sp[0x4] 
.LDBG512:
          stw       r7, sp[0x5] 
.LDBG513:
.LDBG495:
.LDBG496:
          mov       r7, r2
.LDBG497:
          mov       r6, r3
.LDBG498:
          ldw       r0, sp[0x7] 
.LDBG499:
          ldw       r0, sp[0x8] 
.LDBG500:
          ldw       r0, sp[0x9] 
.LDBG501:
          ldw       r0, sp[0xa] 
.LDBG502:
          ldw       r0, sp[0xb] 
.LDBG503:
          ldw       r0, sp[0xc] 
.LDBG504:
.LDBG514:                            # line (357)
.LDBG493:
          getr      r5, 0x1
.LDBG505:
.LDBG515:                            # line (370)
          ldc       r4, 0x90
.LDBG516:                            # line (371)
          ldc       r1, 0x3
          ldc       r2, 0x58
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG517:                            # line (371)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG518:                            # line (374)
.LDBG519:                            # line (375)
          ldc       r1, 0x4
          ldc       r2, 0x11
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG520:                            # line (375)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG521:                            # line (379)
.LDBG522:                            # line (380)
          ldc       r1, 0x5
          ldc       r2, 0x1c
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG523:                            # line (380)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG491:
.LDBG524:                            # line (386)
          ldc       r4, 0x9c
.LDBG525:                            # line (387)
          ldc       r1, 0x3
          ldc       r2, 0x1
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG526:                            # line (387)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG527:                            # line (388)
          ldc       r1, 0x5
          ldc       r2, 0x1
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG528:                            # line (388)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG529:                            # line (402)
          ldc       r1, 0x6
          ldc       r2, 0x18
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG530:                            # line (402)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG531:                            # line (403)
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG532:                            # line (403)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG533:                            # line (404)
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG534:                            # line (404)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG535:                            # line (405)
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG536:                            # line (405)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG537:                            # line (411)
          ldc       r1, 0x16
          ldc       r2, 0x10
          mov       r0, r4
          mov       r3, r7
          stw       r6, sp[0x1] 
.LDBG538:                            # line (411)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG494:
.LDBG492:
.LDBG539:                            # line (413)
          freer     res[r5]
.LDBG540:
          ldw       r4, sp[0x2] 
.LDBG541:
          ldw       r5, sp[0x3] 
.LDBG542:
          ldw       r6, sp[0x4] 
.LDBG543:
          ldw       r7, sp[0x5] 
.LDBG544:
          retsp     0x6 
.LDBG506:
.LDBG545:
.cc_bottom audio_init.function
.LDBG546:
          .section  .debug_frame, "",     @progbits
.cc_top audio_init.function,audio_init
          .align    4
          .int      .LDBG548-.LDBG547
.LDBG547:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG507
          .int      .LDBG546-.LDBG507
          .byte     0x01
          .int      .LDBG509
          .byte     0x0e
          .uleb128  0x18
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG510
          .byte     0x84
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG511
          .byte     0x85
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG512
          .byte     0x86
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG513
          .byte     0x87
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG540
          .byte     0x0a
          .byte     0x01
          .int      .LDBG541
          .byte     0xc4
          .byte     0x01
          .int      .LDBG542
          .byte     0xc5
          .byte     0x01
          .int      .LDBG543
          .byte     0xc6
          .byte     0x01
          .int      .LDBG544
          .byte     0xc7
          .byte     0x01
          .int      .LDBG545
          .byte     0x0b
          .align    4, 0
.LDBG548:
.cc_bottom audio_init.function
.linkset .LLNK19, swc_audio_codec_ctl_reg_wr.maxchanends $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK18, .LLNK19 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK17, .LLNK18 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK16, .LLNK17 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK15, .LLNK16 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK14, .LLNK15 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK13, .LLNK14 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK12, .LLNK13 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK11, .LLNK12 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset audio_init.maxchanends, .LLNK11
.linkset .LLNK30, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK29, 1 $M .LLNK30
.linkset .LLNK31, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK28, .LLNK29 $M .LLNK31
.linkset .LLNK32, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK27, .LLNK28 $M .LLNK32
.linkset .LLNK33, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK26, .LLNK27 $M .LLNK33
.linkset .LLNK34, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK25, .LLNK26 $M .LLNK34
.linkset .LLNK35, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK24, .LLNK25 $M .LLNK35
.linkset .LLNK36, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK23, .LLNK24 $M .LLNK36
.linkset .LLNK37, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK22, .LLNK23 $M .LLNK37
.linkset .LLNK38, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK21, .LLNK22 $M .LLNK38
.linkset .LLNK39, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK20, .LLNK21 $M .LLNK39
.linkset audio_init.maxtimers, .LLNK20
.linkset .LLNK51, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK50, 1 + .LLNK51
.linkset .LLNK49, 1 $M .LLNK50
.linkset .LLNK53, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK52, 1 + .LLNK53
.linkset .LLNK48, .LLNK49 $M .LLNK52
.linkset .LLNK55, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK54, 1 + .LLNK55
.linkset .LLNK47, .LLNK48 $M .LLNK54
.linkset .LLNK57, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK56, 1 + .LLNK57
.linkset .LLNK46, .LLNK47 $M .LLNK56
.linkset .LLNK59, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK58, 1 + .LLNK59
.linkset .LLNK45, .LLNK46 $M .LLNK58
.linkset .LLNK61, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK60, 1 + .LLNK61
.linkset .LLNK44, .LLNK45 $M .LLNK60
.linkset .LLNK63, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK62, 1 + .LLNK63
.linkset .LLNK43, .LLNK44 $M .LLNK62
.linkset .LLNK65, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK64, 1 + .LLNK65
.linkset .LLNK42, .LLNK43 $M .LLNK64
.linkset .LLNK67, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK66, 1 + .LLNK67
.linkset .LLNK41, .LLNK42 $M .LLNK66
.linkset .LLNK69, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK68, 1 + .LLNK69
.linkset .LLNK40, .LLNK41 $M .LLNK68
.linkset audio_init.maxthreads, .LLNK40
          .text
.LDBG549:
# Thread names for recovering thread graph in linker
.LDBG550:
.globl samp, "ui"
.type  samp, @object
          .section .dp.bss,        "adw", @nobits
.cc_top samp.data, samp
          .align    4
samp:
          .int      0x00000000
.cc_bottom samp.data
          .align    4
          .section  .debug_info,   "",    @progbits
.LDBG553:
          .int      .LDBG555-.LDBG554
.LDBG554:
          .short    0x0003
          .long     .LDBG551         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG550         # high address
          .asciiz   "./tests/audio/src/audio_init_audioBoard.xc"
          .asciiz   "/home/andrew-local/software/sandbox2/verif_tests_sw/Tools/xta/Applications/swc_usb"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG552         # offset in .debug_lineprog
.LDBG556:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG557:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG558:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG559:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG560:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG561:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG562:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG563:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG564:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG565:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG566:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG567:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG568:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG569:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG570:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG571:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG572:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top samp.data,samp
          .uleb128  0x3
          .asciiz   "samp"
          .byte     0x01
          .short    0x000c
          .int      .LDBG559-.LDBG553
          .short    .LDBG574-.LDBG573
.LDBG573:
          .byte     0x03
          .int      samp
.LDBG574:
.cc_bottom samp.data
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG575:
          .uleb128  0x4
          .asciiz   "swc_audio_codec_ctl_reg_wr"
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .int      .LDBG558-.LDBG553
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG206         # high address
          .uleb128  0x5
          .asciiz   "deviceAdrs"
          .byte     0x01
          .short    0x0010
          .int      .LDBG558-.LDBG553
          .int      .LDBG576
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG576:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG578-.LDBG577
.LDBG577:
          .byte     0x50
.LDBG578:
          .int      .LDBG7-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG580-.LDBG579
.LDBG579:
          .byte     0x50
.LDBG580:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "Adrs"
          .byte     0x01
          .short    0x0010
          .int      .LDBG558-.LDBG553
          .int      .LDBG581
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG581:
          .int      .LDBG24-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG583-.LDBG582
.LDBG582:
          .byte     0x51
.LDBG583:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG585-.LDBG584
.LDBG584:
          .byte     0x51
.LDBG585:
          .int      .LDBG8-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG587-.LDBG586
.LDBG586:
          .byte     0x51
.LDBG587:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "WrData"
          .byte     0x01
          .short    0x0010
          .int      .LDBG558-.LDBG553
          .int      .LDBG588
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG588:
          .int      .LDBG4-.LDBG0
          .int      .LDBG206-.LDBG0
          .short    .LDBG590-.LDBG589
.LDBG589:
          .byte     0x7e
          .sleb128  0x0
.LDBG590:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SCLK"
          .byte     0x01
          .short    0x0010
          .int      .LDBG567-.LDBG553
          .int      .LDBG591
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG591:
          .int      .LDBG32-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG593-.LDBG592
.LDBG592:
          .byte     0x53
.LDBG593:
          .int      .LDBG24-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG595-.LDBG594
.LDBG594:
          .byte     0x53
.LDBG595:
          .int      .LDBG52-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG597-.LDBG596
.LDBG596:
          .byte     0x53
.LDBG597:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG599-.LDBG598
.LDBG598:
          .byte     0x53
.LDBG599:
          .int      .LDBG46-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG601-.LDBG600
.LDBG600:
          .byte     0x53
.LDBG601:
          .int      .LDBG38-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG603-.LDBG602
.LDBG602:
          .byte     0x53
.LDBG603:
          .int      .LDBG9-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG605-.LDBG604
.LDBG604:
          .byte     0x53
.LDBG605:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SDIN"
          .byte     0x01
          .short    0x0010
          .int      .LDBG567-.LDBG553
          .int      .LDBG606
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG606:
          .int      .LDBG52-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG608-.LDBG607
.LDBG607:
          .byte     0x5b
.LDBG608:
          .int      .LDBG32-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG610-.LDBG609
.LDBG609:
          .byte     0x5b
.LDBG610:
          .int      .LDBG24-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG612-.LDBG611
.LDBG611:
          .byte     0x5b
.LDBG612:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG614-.LDBG613
.LDBG613:
          .byte     0x5b
.LDBG614:
          .int      .LDBG46-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG616-.LDBG615
.LDBG615:
          .byte     0x5b
.LDBG616:
          .int      .LDBG38-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG618-.LDBG617
.LDBG617:
          .byte     0x5b
.LDBG618:
          .int      .LDBG10-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG620-.LDBG619
.LDBG619:
          .byte     0x5b
.LDBG620:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "Result"
          .byte     0x01
          .short    0x0012
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG621
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG621:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG623-.LDBG622
.LDBG622:
          .byte     0x50
.LDBG623:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG625-.LDBG624
.LDBG624:
          .byte     0x50
.LDBG625:
          .int      .LDBG65-.LDBG0
          .int      .LDBG66-.LDBG0
          .short    .LDBG627-.LDBG626
.LDBG626:
          .byte     0x50
.LDBG627:
          .int      .LDBG58-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG629-.LDBG628
.LDBG628:
          .byte     0x50
.LDBG629:
          .int      .LDBG69-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG631-.LDBG630
.LDBG630:
          .byte     0x50
.LDBG631:
          .int      .LDBG67-.LDBG0
          .int      .LDBG68-.LDBG0
          .short    .LDBG633-.LDBG632
.LDBG632:
          .byte     0x50
.LDBG633:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "gt"
          .byte     0x01
          .short    0x0013
          .short    0x0000
          .int      .LDBG565-.LDBG553
          .int      .LDBG634
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG634:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG636-.LDBG635
.LDBG635:
          .byte     0x57
.LDBG636:
          .int      .LDBG52-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG638-.LDBG637
.LDBG637:
          .byte     0x57
.LDBG638:
          .int      .LDBG32-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG640-.LDBG639
.LDBG639:
          .byte     0x57
.LDBG640:
          .int      .LDBG24-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG642-.LDBG641
.LDBG641:
          .byte     0x57
.LDBG642:
          .int      .LDBG65-.LDBG0
          .int      .LDBG66-.LDBG0
          .short    .LDBG644-.LDBG643
.LDBG643:
          .byte     0x57
.LDBG644:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG646-.LDBG645
.LDBG645:
          .byte     0x57
.LDBG646:
          .int      .LDBG69-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG648-.LDBG647
.LDBG647:
          .byte     0x57
.LDBG648:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG650-.LDBG649
.LDBG649:
          .byte     0x57
.LDBG650:
          .int      .LDBG46-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG652-.LDBG651
.LDBG651:
          .byte     0x57
.LDBG652:
          .int      .LDBG38-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG654-.LDBG653
.LDBG653:
          .byte     0x57
.LDBG654:
          .int      .LDBG11-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG656-.LDBG655
.LDBG655:
          .byte     0x57
.LDBG656:
          .int      .LDBG67-.LDBG0
          .int      .LDBG68-.LDBG0
          .short    .LDBG658-.LDBG657
.LDBG657:
          .byte     0x57
.LDBG658:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x0014
          .short    0x0000
          .int      .LDBG559-.LDBG553
          .int      .LDBG659
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG659:
          .int      .LDBG34-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG661-.LDBG660
.LDBG660:
          .byte     0x54
.LDBG661:
          .int      .LDBG54-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG663-.LDBG662
.LDBG662:
          .byte     0x54
.LDBG663:
          .int      .LDBG26-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG665-.LDBG664
.LDBG664:
          .byte     0x54
.LDBG665:
          .int      .LDBG48-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG667-.LDBG666
.LDBG666:
          .byte     0x54
.LDBG667:
          .int      .LDBG21-.LDBG0
          .int      .LDBG22-.LDBG0
          .short    .LDBG669-.LDBG668
.LDBG668:
          .byte     0x54
.LDBG669:
          .int      .LDBG19-.LDBG0
          .int      .LDBG20-.LDBG0
          .short    .LDBG671-.LDBG670
.LDBG670:
          .byte     0x54
.LDBG671:
          .int      .LDBG40-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG673-.LDBG672
.LDBG672:
          .byte     0x54
.LDBG673:
          .int      .LDBG12-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG675-.LDBG674
.LDBG674:
          .byte     0x54
.LDBG675:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "Temp"
          .byte     0x01
          .short    0x0015
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG676
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG676:
          .int      .LDBG39-.LDBG0
          .int      .LDBG40-.LDBG0
          .short    .LDBG678-.LDBG677
.LDBG677:
          .byte     0x54
.LDBG678:
          .int      .LDBG33-.LDBG0
          .int      .LDBG34-.LDBG0
          .short    .LDBG680-.LDBG679
.LDBG679:
          .byte     0x54
.LDBG680:
          .int      .LDBG53-.LDBG0
          .int      .LDBG54-.LDBG0
          .short    .LDBG682-.LDBG681
.LDBG681:
          .byte     0x54
.LDBG682:
          .int      .LDBG25-.LDBG0
          .int      .LDBG26-.LDBG0
          .short    .LDBG684-.LDBG683
.LDBG683:
          .byte     0x54
.LDBG684:
          .int      .LDBG47-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG686-.LDBG685
.LDBG685:
          .byte     0x54
.LDBG686:
          .int      .LDBG18-.LDBG0
          .int      .LDBG19-.LDBG0
          .short    .LDBG688-.LDBG687
.LDBG687:
          .byte     0x54
.LDBG688:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "CtlAdrsData"
          .byte     0x01
          .short    0x0015
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG689
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG689:
          .int      .LDBG32-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG691-.LDBG690
.LDBG690:
          .byte     0x50
.LDBG691:
          .int      .LDBG29-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG693-.LDBG692
.LDBG692:
          .byte     0x50
.LDBG693:
          .int      .LDBG46-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG695-.LDBG694
.LDBG694:
          .byte     0x50
.LDBG695:
          .int      .LDBG43-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG697-.LDBG696
.LDBG696:
          .byte     0x50
.LDBG697:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i"
          .byte     0x01
          .short    0x0015
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG698
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG698:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG700-.LDBG699
.LDBG699:
          .byte     0x52
.LDBG700:
          .int      .LDBG32-.LDBG0
          .int      .LDBG37-.LDBG0
          .short    .LDBG702-.LDBG701
.LDBG701:
          .byte     0x52
.LDBG702:
          .int      .LDBG59-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG704-.LDBG703
.LDBG703:
          .byte     0x52
.LDBG704:
          .int      .LDBG69-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG706-.LDBG705
.LDBG705:
          .byte     0x52
.LDBG706:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG708-.LDBG707
.LDBG707:
          .byte     0x52
.LDBG708:
          .int      .LDBG30-.LDBG0
          .int      .LDBG31-.LDBG0
          .short    .LDBG710-.LDBG709
.LDBG709:
          .byte     0x52
.LDBG710:
          .int      .LDBG46-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG712-.LDBG711
.LDBG711:
          .byte     0x52
.LDBG712:
          .int      .LDBG17-.LDBG0
          .int      .LDBG23-.LDBG0
          .short    .LDBG714-.LDBG713
.LDBG713:
          .byte     0x52
.LDBG714:
          .int      .LDBG44-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG716-.LDBG715
.LDBG715:
          .byte     0x52
.LDBG716:
          .int      .LDBG15-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG718-.LDBG717
.LDBG717:
          .byte     0x52
.LDBG718:
          .int      .LDBG67-.LDBG0
          .int      .LDBG68-.LDBG0
          .short    .LDBG720-.LDBG719
.LDBG719:
          .byte     0x52
.LDBG720:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
.LDBG721:
          .uleb128  0x7
          .int      .LDBG558-.LDBG553
          .int      0x0000000c
          .uleb128  0x8
          .byte     0x00
          .int      0x00000002
          .byte     0x00
          .uleb128  0x6
          .asciiz   "DeviceACK"
          .byte     0x01
          .short    0x0017
          .short    0x0000
          .int      .LDBG721-.LDBG553
          .int      .LDBG722
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
.LDBG722:
          .int      .LDBG5-.LDBG0
          .int      .LDBG6-.LDBG0
          .short    .LDBG724-.LDBG723
.LDBG723:
          .byte     0x7e
          .sleb128  0x20
.LDBG724:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_wr.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG725:
          .uleb128  0x4
          .asciiz   "swc_audio_codec_ctl_reg_rd"
          .byte     0x01
          .byte     0x97
          .byte     0x01
          .int      .LDBG558-.LDBG553
          .byte     0x01
          .long     .LDBG207         # low address
          .long     .LDBG489         # high address
          .uleb128  0x5
          .asciiz   "deviceAdrs"
          .byte     0x01
          .short    0x0096
          .int      .LDBG558-.LDBG553
          .int      .LDBG726
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG726:
          .int      .LDBG244-.LDBG0
          .int      .LDBG259-.LDBG0
          .short    .LDBG728-.LDBG727
.LDBG727:
          .byte     0x50
.LDBG728:
          .int      .LDBG222-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG730-.LDBG729
.LDBG729:
          .byte     0x50
.LDBG730:
          .int      .LDBG212-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG732-.LDBG731
.LDBG731:
          .byte     0x50
.LDBG732:
          .int      .LDBG237-.LDBG0
          .int      .LDBG243-.LDBG0
          .short    .LDBG734-.LDBG733
.LDBG733:
          .byte     0x50
.LDBG734:
          .int      .LDBG229-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG736-.LDBG735
.LDBG735:
          .byte     0x50
.LDBG736:
          .int      .LDBG260-.LDBG0
          .int      .LDBG266-.LDBG0
          .short    .LDBG738-.LDBG737
.LDBG737:
          .byte     0x50
.LDBG738:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "Adrs"
          .byte     0x01
          .short    0x0096
          .int      .LDBG558-.LDBG553
          .int      .LDBG739
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG739:
          .int      .LDBG207-.LDBG0
          .int      .LDBG489-.LDBG0
          .short    .LDBG741-.LDBG740
.LDBG740:
          .byte     0x7e
          .sleb128  0x0
.LDBG741:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SCLK"
          .byte     0x01
          .short    0x0096
          .int      .LDBG567-.LDBG553
          .int      .LDBG742
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG742:
          .int      .LDBG244-.LDBG0
          .int      .LDBG259-.LDBG0
          .short    .LDBG744-.LDBG743
.LDBG743:
          .byte     0x52
.LDBG744:
          .int      .LDBG213-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG746-.LDBG745
.LDBG745:
          .byte     0x52
.LDBG746:
          .int      .LDBG274-.LDBG0
          .int      .LDBG280-.LDBG0
          .short    .LDBG748-.LDBG747
.LDBG747:
          .byte     0x52
.LDBG748:
          .int      .LDBG281-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG750-.LDBG749
.LDBG749:
          .byte     0x52
.LDBG750:
          .int      .LDBG267-.LDBG0
          .int      .LDBG273-.LDBG0
          .short    .LDBG752-.LDBG751
.LDBG751:
          .byte     0x52
.LDBG752:
          .int      .LDBG237-.LDBG0
          .int      .LDBG243-.LDBG0
          .short    .LDBG754-.LDBG753
.LDBG753:
          .byte     0x52
.LDBG754:
          .int      .LDBG229-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG756-.LDBG755
.LDBG755:
          .byte     0x52
.LDBG756:
          .int      .LDBG260-.LDBG0
          .int      .LDBG266-.LDBG0
          .short    .LDBG758-.LDBG757
.LDBG757:
          .byte     0x52
.LDBG758:
          .int      .LDBG222-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG760-.LDBG759
.LDBG759:
          .byte     0x52
.LDBG760:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SDIN"
          .byte     0x01
          .short    0x0096
          .int      .LDBG567-.LDBG553
          .int      .LDBG761
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG761:
          .int      .LDBG222-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG763-.LDBG762
.LDBG762:
          .byte     0x53
.LDBG763:
          .int      .LDBG244-.LDBG0
          .int      .LDBG259-.LDBG0
          .short    .LDBG765-.LDBG764
.LDBG764:
          .byte     0x53
.LDBG765:
          .int      .LDBG214-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG767-.LDBG766
.LDBG766:
          .byte     0x53
.LDBG767:
          .int      .LDBG274-.LDBG0
          .int      .LDBG280-.LDBG0
          .short    .LDBG769-.LDBG768
.LDBG768:
          .byte     0x53
.LDBG769:
          .int      .LDBG281-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG771-.LDBG770
.LDBG770:
          .byte     0x53
.LDBG771:
          .int      .LDBG267-.LDBG0
          .int      .LDBG273-.LDBG0
          .short    .LDBG773-.LDBG772
.LDBG772:
          .byte     0x53
.LDBG773:
          .int      .LDBG237-.LDBG0
          .int      .LDBG243-.LDBG0
          .short    .LDBG775-.LDBG774
.LDBG774:
          .byte     0x53
.LDBG775:
          .int      .LDBG260-.LDBG0
          .int      .LDBG266-.LDBG0
          .short    .LDBG777-.LDBG776
.LDBG776:
          .byte     0x53
.LDBG777:
          .int      .LDBG229-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG779-.LDBG778
.LDBG778:
          .byte     0x53
.LDBG779:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "Result"
          .byte     0x01
          .short    0x0098
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG780
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG780:
          .int      .LDBG287-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG782-.LDBG781
.LDBG781:
          .byte     0x50
.LDBG782:
          .int      .LDBG290-.LDBG0
          .int      .LDBG291-.LDBG0
          .short    .LDBG784-.LDBG783
.LDBG783:
          .byte     0x50
.LDBG784:
          .int      .LDBG292-.LDBG0
          .int      .LDBG293-.LDBG0
          .short    .LDBG786-.LDBG785
.LDBG785:
          .byte     0x50
.LDBG786:
          .int      .LDBG294-.LDBG0
          .int      .LDBG295-.LDBG0
          .short    .LDBG788-.LDBG787
.LDBG787:
          .byte     0x50
.LDBG788:
          .int      .LDBG296-.LDBG0
          .int      .LDBG297-.LDBG0
          .short    .LDBG790-.LDBG789
.LDBG789:
          .byte     0x50
.LDBG790:
          .int      .LDBG298-.LDBG0
          .int      .LDBG299-.LDBG0
          .short    .LDBG792-.LDBG791
.LDBG791:
          .byte     0x50
.LDBG792:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "gt"
          .byte     0x01
          .short    0x0099
          .short    0x0000
          .int      .LDBG565-.LDBG553
          .int      .LDBG793
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG793:
          .int      .LDBG222-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG795-.LDBG794
.LDBG794:
          .byte     0x56
.LDBG795:
          .int      .LDBG244-.LDBG0
          .int      .LDBG259-.LDBG0
          .short    .LDBG797-.LDBG796
.LDBG796:
          .byte     0x56
.LDBG797:
          .int      .LDBG215-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG799-.LDBG798
.LDBG798:
          .byte     0x56
.LDBG799:
          .int      .LDBG274-.LDBG0
          .int      .LDBG280-.LDBG0
          .short    .LDBG801-.LDBG800
.LDBG800:
          .byte     0x56
.LDBG801:
          .int      .LDBG281-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG803-.LDBG802
.LDBG802:
          .byte     0x56
.LDBG803:
          .int      .LDBG290-.LDBG0
          .int      .LDBG291-.LDBG0
          .short    .LDBG805-.LDBG804
.LDBG804:
          .byte     0x56
.LDBG805:
          .int      .LDBG292-.LDBG0
          .int      .LDBG293-.LDBG0
          .short    .LDBG807-.LDBG806
.LDBG806:
          .byte     0x56
.LDBG807:
          .int      .LDBG294-.LDBG0
          .int      .LDBG295-.LDBG0
          .short    .LDBG809-.LDBG808
.LDBG808:
          .byte     0x56
.LDBG809:
          .int      .LDBG296-.LDBG0
          .int      .LDBG297-.LDBG0
          .short    .LDBG811-.LDBG810
.LDBG810:
          .byte     0x56
.LDBG811:
          .int      .LDBG298-.LDBG0
          .int      .LDBG299-.LDBG0
          .short    .LDBG813-.LDBG812
.LDBG812:
          .byte     0x56
.LDBG813:
          .int      .LDBG267-.LDBG0
          .int      .LDBG273-.LDBG0
          .short    .LDBG815-.LDBG814
.LDBG814:
          .byte     0x56
.LDBG815:
          .int      .LDBG237-.LDBG0
          .int      .LDBG243-.LDBG0
          .short    .LDBG817-.LDBG816
.LDBG816:
          .byte     0x56
.LDBG817:
          .int      .LDBG229-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG819-.LDBG818
.LDBG818:
          .byte     0x56
.LDBG819:
          .int      .LDBG260-.LDBG0
          .int      .LDBG266-.LDBG0
          .short    .LDBG821-.LDBG820
.LDBG820:
          .byte     0x56
.LDBG821:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x009a
          .short    0x0000
          .int      .LDBG559-.LDBG553
          .int      .LDBG822
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG822:
          .int      .LDBG262-.LDBG0
          .int      .LDBG263-.LDBG0
          .short    .LDBG824-.LDBG823
.LDBG823:
          .byte     0x51
.LDBG824:
          .int      .LDBG226-.LDBG0
          .int      .LDBG227-.LDBG0
          .short    .LDBG826-.LDBG825
.LDBG825:
          .byte     0x51
.LDBG826:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG828-.LDBG827
.LDBG827:
          .byte     0x51
.LDBG828:
          .int      .LDBG256-.LDBG0
          .int      .LDBG257-.LDBG0
          .short    .LDBG830-.LDBG829
.LDBG829:
          .byte     0x51
.LDBG830:
          .int      .LDBG254-.LDBG0
          .int      .LDBG255-.LDBG0
          .short    .LDBG832-.LDBG831
.LDBG831:
          .byte     0x51
.LDBG832:
          .int      .LDBG218-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG834-.LDBG833
.LDBG833:
          .byte     0x51
.LDBG834:
          .int      .LDBG250-.LDBG0
          .int      .LDBG253-.LDBG0
          .short    .LDBG836-.LDBG835
.LDBG835:
          .byte     0x51
.LDBG836:
          .int      .LDBG216-.LDBG0
          .int      .LDBG217-.LDBG0
          .short    .LDBG838-.LDBG837
.LDBG837:
          .byte     0x51
.LDBG838:
          .int      .LDBG248-.LDBG0
          .int      .LDBG249-.LDBG0
          .short    .LDBG840-.LDBG839
.LDBG839:
          .byte     0x51
.LDBG840:
          .int      .LDBG278-.LDBG0
          .int      .LDBG280-.LDBG0
          .short    .LDBG842-.LDBG841
.LDBG841:
          .byte     0x51
.LDBG842:
          .int      .LDBG283-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG844-.LDBG843
.LDBG843:
          .byte     0x51
.LDBG844:
          .int      .LDBG275-.LDBG0
          .int      .LDBG277-.LDBG0
          .short    .LDBG846-.LDBG845
.LDBG845:
          .byte     0x51
.LDBG846:
          .int      .LDBG269-.LDBG0
          .int      .LDBG273-.LDBG0
          .short    .LDBG848-.LDBG847
.LDBG847:
          .byte     0x51
.LDBG848:
          .int      .LDBG246-.LDBG0
          .int      .LDBG247-.LDBG0
          .short    .LDBG850-.LDBG849
.LDBG849:
          .byte     0x51
.LDBG850:
          .int      .LDBG241-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG852-.LDBG851
.LDBG851:
          .byte     0x51
.LDBG852:
          .int      .LDBG239-.LDBG0
          .int      .LDBG240-.LDBG0
          .short    .LDBG854-.LDBG853
.LDBG853:
          .byte     0x51
.LDBG854:
          .int      .LDBG264-.LDBG0
          .int      .LDBG265-.LDBG0
          .short    .LDBG856-.LDBG855
.LDBG855:
          .byte     0x51
.LDBG856:
          .int      .LDBG233-.LDBG0
          .int      .LDBG234-.LDBG0
          .short    .LDBG858-.LDBG857
.LDBG857:
          .byte     0x51
.LDBG858:
          .int      .LDBG231-.LDBG0
          .int      .LDBG232-.LDBG0
          .short    .LDBG860-.LDBG859
.LDBG859:
          .byte     0x51
.LDBG860:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "Temp"
          .byte     0x01
          .short    0x009b
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG861
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG861:
          .int      .LDBG261-.LDBG0
          .int      .LDBG262-.LDBG0
          .short    .LDBG863-.LDBG862
.LDBG862:
          .byte     0x51
.LDBG863:
          .int      .LDBG223-.LDBG0
          .int      .LDBG224-.LDBG0
          .short    .LDBG865-.LDBG864
.LDBG864:
          .byte     0x51
.LDBG865:
          .int      .LDBG277-.LDBG0
          .int      .LDBG278-.LDBG0
          .short    .LDBG867-.LDBG866
.LDBG866:
          .byte     0x51
.LDBG867:
          .int      .LDBG282-.LDBG0
          .int      .LDBG283-.LDBG0
          .short    .LDBG869-.LDBG868
.LDBG868:
          .byte     0x51
.LDBG869:
          .int      .LDBG245-.LDBG0
          .int      .LDBG246-.LDBG0
          .short    .LDBG871-.LDBG870
.LDBG870:
          .byte     0x51
.LDBG871:
          .int      .LDBG238-.LDBG0
          .int      .LDBG239-.LDBG0
          .short    .LDBG873-.LDBG872
.LDBG872:
          .byte     0x51
.LDBG873:
          .int      .LDBG268-.LDBG0
          .int      .LDBG269-.LDBG0
          .short    .LDBG875-.LDBG874
.LDBG874:
          .byte     0x51
.LDBG875:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG877-.LDBG876
.LDBG876:
          .byte     0x51
.LDBG877:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "CtlAdrsData"
          .byte     0x01
          .short    0x009b
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG878
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG878:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i"
          .byte     0x01
          .short    0x009b
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG879
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG879:
          .int      .LDBG222-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG881-.LDBG880
.LDBG880:
          .byte     0x54
.LDBG881:
          .int      .LDBG258-.LDBG0
          .int      .LDBG259-.LDBG0
          .short    .LDBG883-.LDBG882
.LDBG882:
          .byte     0x54
.LDBG883:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG885-.LDBG884
.LDBG884:
          .byte     0x54
.LDBG885:
          .int      .LDBG274-.LDBG0
          .int      .LDBG280-.LDBG0
          .short    .LDBG887-.LDBG886
.LDBG886:
          .byte     0x54
.LDBG887:
          .int      .LDBG288-.LDBG0
          .int      .LDBG289-.LDBG0
          .short    .LDBG889-.LDBG888
.LDBG888:
          .byte     0x54
.LDBG889:
          .int      .LDBG290-.LDBG0
          .int      .LDBG291-.LDBG0
          .short    .LDBG891-.LDBG890
.LDBG890:
          .byte     0x54
.LDBG891:
          .int      .LDBG292-.LDBG0
          .int      .LDBG293-.LDBG0
          .short    .LDBG893-.LDBG892
.LDBG892:
          .byte     0x54
.LDBG893:
          .int      .LDBG296-.LDBG0
          .int      .LDBG297-.LDBG0
          .short    .LDBG895-.LDBG894
.LDBG894:
          .byte     0x54
.LDBG895:
          .int      .LDBG298-.LDBG0
          .int      .LDBG299-.LDBG0
          .short    .LDBG897-.LDBG896
.LDBG896:
          .byte     0x54
.LDBG897:
          .int      .LDBG272-.LDBG0
          .int      .LDBG273-.LDBG0
          .short    .LDBG899-.LDBG898
.LDBG898:
          .byte     0x54
.LDBG899:
          .int      .LDBG237-.LDBG0
          .int      .LDBG243-.LDBG0
          .short    .LDBG901-.LDBG900
.LDBG900:
          .byte     0x54
.LDBG901:
          .int      .LDBG235-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG903-.LDBG902
.LDBG902:
          .byte     0x54
.LDBG903:
          .int      .LDBG260-.LDBG0
          .int      .LDBG266-.LDBG0
          .short    .LDBG905-.LDBG904
.LDBG904:
          .byte     0x54
.LDBG905:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
.LDBG906:
          .uleb128  0x7
          .int      .LDBG558-.LDBG553
          .int      0x0000000c
          .uleb128  0x8
          .byte     0x00
          .int      0x00000002
          .byte     0x00
          .uleb128  0x6
          .asciiz   "DeviceACK"
          .byte     0x01
          .short    0x009d
          .short    0x0000
          .int      .LDBG906-.LDBG553
          .int      .LDBG907
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG907:
          .int      .LDBG210-.LDBG0
          .int      .LDBG211-.LDBG0
          .short    .LDBG909-.LDBG908
.LDBG908:
          .byte     0x7e
          .sleb128  0x20
.LDBG909:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "rdData"
          .byte     0x01
          .short    0x009e
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG910
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG910:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
.LDBG911:
          .uleb128  0x9
          .long     .LDBG208         # low address
          .long     .LDBG209         # high address
          .uleb128  0x6
          .asciiz   "deviceAddr"
          .byte     0x01
          .short    0x0107
          .short    .LDBG912-.LDBG911
          .int      .LDBG558-.LDBG553
          .int      .LDBG913
          .section  .debug_loc,    "",    @progbits
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
.LDBG913:
          .int      0x00000000
          .int      0x00000000
.cc_bottom swc_audio_codec_ctl_reg_rd.function
          .section  .debug_info,   "",    @progbits
.LDBG912:
          .byte     0x00
          .byte     0x00
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top audio_init.function,audio_init
.LDBG914:
          .uleb128  0xa
          .asciiz   "audio_init"
          .byte     0x01
          .byte     0x65
          .byte     0x01
          .byte     0x01
          .long     .LDBG490         # low address
          .long     .LDBG549         # high address
          .uleb128  0x5
          .asciiz   "AUD_RESET_N"
          .byte     0x01
          .short    0x015b
          .int      .LDBG567-.LDBG553
          .int      .LDBG915
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG915:
          .int      .LDBG495-.LDBG0
          .int      .LDBG499-.LDBG0
          .short    .LDBG917-.LDBG916
.LDBG916:
          .byte     0x50
.LDBG917:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_BCLK"
          .byte     0x01
          .short    0x015c
          .int      .LDBG567-.LDBG553
          .int      .LDBG918
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG918:
          .int      .LDBG496-.LDBG0
          .int      .LDBG506-.LDBG0
          .short    .LDBG920-.LDBG919
.LDBG919:
          .byte     0x51
.LDBG920:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SCLK"
          .byte     0x01
          .short    0x015d
          .int      .LDBG567-.LDBG553
          .int      .LDBG921
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG921:
          .int      .LDBG497-.LDBG0
          .int      .LDBG506-.LDBG0
          .short    .LDBG923-.LDBG922
.LDBG922:
          .byte     0x57
.LDBG923:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SDIN"
          .byte     0x01
          .short    0x015e
          .int      .LDBG567-.LDBG553
          .int      .LDBG924
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG924:
          .int      .LDBG498-.LDBG0
          .int      .LDBG506-.LDBG0
          .short    .LDBG926-.LDBG925
.LDBG925:
          .byte     0x56
.LDBG926:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT0"
          .byte     0x01
          .short    0x015f
          .int      .LDBG572-.LDBG553
          .int      .LDBG927
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG927:
          .int      .LDBG499-.LDBG0
          .int      .LDBG500-.LDBG0
          .short    .LDBG929-.LDBG928
.LDBG928:
          .byte     0x50
.LDBG929:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT1"
          .byte     0x01
          .short    0x0160
          .int      .LDBG572-.LDBG553
          .int      .LDBG930
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG930:
          .int      .LDBG500-.LDBG0
          .int      .LDBG501-.LDBG0
          .short    .LDBG932-.LDBG931
.LDBG931:
          .byte     0x50
.LDBG932:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT2"
          .byte     0x01
          .short    0x0161
          .int      .LDBG572-.LDBG553
          .int      .LDBG933
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG933:
          .int      .LDBG501-.LDBG0
          .int      .LDBG502-.LDBG0
          .short    .LDBG935-.LDBG934
.LDBG934:
          .byte     0x50
.LDBG935:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT3"
          .byte     0x01
          .short    0x0162
          .int      .LDBG572-.LDBG553
          .int      .LDBG936
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG936:
          .int      .LDBG502-.LDBG0
          .int      .LDBG503-.LDBG0
          .short    .LDBG938-.LDBG937
.LDBG937:
          .byte     0x50
.LDBG938:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_LRCLK"
          .byte     0x01
          .short    0x0163
          .int      .LDBG567-.LDBG553
          .int      .LDBG939
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG939:
          .int      .LDBG503-.LDBG0
          .int      .LDBG504-.LDBG0
          .short    .LDBG941-.LDBG940
.LDBG940:
          .byte     0x50
.LDBG941:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_BIT_CLOCK"
          .byte     0x01
          .short    0x0164
          .int      .LDBG566-.LDBG553
          .int      .LDBG942
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG942:
          .int      .LDBG504-.LDBG0
          .int      .LDBG506-.LDBG0
          .short    .LDBG944-.LDBG943
.LDBG943:
          .byte     0x50
.LDBG944:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "WrData"
          .byte     0x01
          .short    0x0166
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG945
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG945:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "t"
          .byte     0x01
          .short    0x0167
          .short    0x0000
          .int      .LDBG565-.LDBG553
          .int      .LDBG946
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG946:
          .int      .LDBG505-.LDBG0
          .int      .LDBG506-.LDBG0
          .short    .LDBG948-.LDBG947
.LDBG947:
          .byte     0x55
.LDBG948:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x0168
          .short    0x0000
          .int      .LDBG559-.LDBG553
          .int      .LDBG949
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG949:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "value"
          .byte     0x01
          .short    0x0169
          .short    0x0000
          .int      .LDBG558-.LDBG553
          .int      .LDBG950
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG950:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
.LDBG951:
          .uleb128  0x9
          .long     .LDBG491         # low address
          .long     .LDBG492         # high address
          .uleb128  0x6
          .asciiz   "deviceAddr"
          .byte     0x01
          .short    0x0182
          .short    .LDBG952-.LDBG951
          .int      .LDBG558-.LDBG553
          .int      .LDBG953
          .section  .debug_loc,    "",    @progbits
.cc_top audio_init.function,audio_init
.LDBG953:
          .int      0x00000000
          .int      0x00000000
.cc_bottom audio_init.function
          .section  .debug_info,   "",    @progbits
.LDBG952:
          .byte     0x00
          .byte     0x00
.cc_bottom audio_init.function
          .byte     0x00
.LDBG555:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG955-.LDBG954
.LDBG954:
          .short    0x0002
          .long     .LDBG553         # offset in .debug_info
          .int      .LDBG555-.LDBG553
.cc_top swc_audio_codec_ctl_reg_wr.function,swc_audio_codec_ctl_reg_wr
          .int      .LDBG575-.LDBG553
          .asciiz   "swc_audio_codec_ctl_reg_wr"
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_rd.function,swc_audio_codec_ctl_reg_rd
          .int      .LDBG725-.LDBG553
          .asciiz   "swc_audio_codec_ctl_reg_rd"
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top audio_init.function,audio_init
          .int      .LDBG914-.LDBG553
          .asciiz   "audio_init"
.cc_bottom audio_init.function
          .int      0x00000000
.LDBG955:
          .section  .debug_line,   "",    @progbits
.LDBG552:
          .int      .LDBG957-.LDBG956
.LDBG956:
          .short    0x0003
          .int      .LDBG959-.LDBG958
.LDBG958:
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
          .asciiz   "./tests/audio/src/audio_init_audioBoard.xc"
          .uleb128  0x0
          .uleb128  0x49b91aba
          .uleb128  0x2d23
          .byte     0x00
.LDBG959:
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG73
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG83
          .byte     0x03
          .sleb128  0x9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG84
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG85
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG87
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG88
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG89
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG94
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG95
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG97
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG98
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG104
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG106
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG107
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG108
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG109
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG111
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG112
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG114
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG115
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG116
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG117
          .byte     0x12             # special opcode (line + 1)
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG122
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG124
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG125
          .byte     0x13             # special opcode (line + 2)
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
          .int      .LDBG128
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG129
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG130
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG131
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG132
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG134
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG135
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG136
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG137
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG139
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG140
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG142
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG143
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG144
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG146
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG147
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG148
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG149
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG151
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG152
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG153
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG155
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG156
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG157
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG158
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG159
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG161
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG162
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG163
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG164
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG166
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG167
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG169
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG170
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG171
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG173
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG174
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG175
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG176
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG178
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG179
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG180
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG181
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG183
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG184
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG185
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG186
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG188
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG190
          .byte     0x03
          .sleb128  0xfffffffe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG192
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG202
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG302
          .byte     0x03
          .sleb128  0x96
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG312
          .byte     0x03
          .sleb128  0xa
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG313
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG314
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG316
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG317
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG318
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG320
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG321
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG322
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG323
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG324
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG326
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG327
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG329
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG330
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG331
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG332
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG333
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG335
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG336
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG337
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG338
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG340
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG341
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG343
          .byte     0x03
          .sleb128  0xe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG344
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG345
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG346
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG348
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG349
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG350
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG351
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG353
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG354
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG355
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG357
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG358
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG359
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG360
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG361
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG363
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG364
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG365
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG366
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG368
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG369
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG371
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG372
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG373
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG375
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG376
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG377
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG378
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG380
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG381
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG382
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG383
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG385
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG386
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG387
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG388
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG390
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG391
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG392
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG394
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG395
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG396
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG398
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG399
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG400
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG402
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG403
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG404
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG405
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG406
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG408
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG409
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG411
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG412
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG413
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG414
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG415
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG416
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG418
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG419
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG420
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG421
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG423
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG424
          .byte     0x03
          .sleb128  0xfffffff4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG426
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG427
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG428
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG430
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG431
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG432
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG433
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG435
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG436
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG437
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG439
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG440
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG441
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG443
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG444
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG445
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG446
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG447
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG449
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG450
          .byte     0x03
          .sleb128  0xfffffff2
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG452
          .byte     0x03
          .sleb128  0x13
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG453
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG454
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG456
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG457
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG458
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG459
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG461
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG462
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG463
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG464
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG466
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG467
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG468
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG469
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG471
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG473
          .byte     0x03
          .sleb128  0xfffffffe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG475
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG485
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top audio_init.function, audio_init
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG508
          .byte     0x03
          .sleb128  0x164
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG515
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG516
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG518
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG519
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG521
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG522
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG524
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG525
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG527
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG529
          .byte     0x03
          .sleb128  0xe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG531
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG533
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG535
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG537
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG539
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom audio_init.function
.LDBG957:
          .section  .debug_abbrev, "",    @progbits
.LDBG551:
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
          .uleb128  0xa
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
          .uleb128  0x9
          .byte     0x0b
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0x7
          .byte     0x01
          .byte     0x01
          .byte     0x49
          .byte     0x13
          .byte     0x0b
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x8
          .byte     0x21
          .byte     0x00
          .byte     0x22
          .byte     0x0b
          .byte     0x2f
          .byte     0x06
          .byte     0x00
          .byte     0x00

          .byte     0x00
          .section .xtaendpointtable,       "", @progbits
.L231:
          .int      .L232-.L231
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/verif_tests_sw/Tools/xta/Applications/swc_usb"
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000145
          .long    .L229
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000143
          .long    .L228
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000142
          .long    .L227
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000140
          .long    .L226
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000013f
          .long    .L225
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000013e
          .long    .L224
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000013c
          .long    .L223
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000139
          .long    .L222
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000138
          .long    .L221
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000136
          .long    .L220
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000123
          .long    .L219
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000131
          .long    .L218
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000130
          .long    .L217
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000012e
          .long    .L216
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000129
          .long    .L215
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000128
          .long    .L214
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000126
          .long    .L213
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000011e
          .long    .L212
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000011d
          .long    .L211
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000011b
          .long    .L210
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000118
          .long    .L209
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000117
          .long    .L208
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000115
          .long    .L207
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000105
          .long    .L206
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000111
          .long    .L205
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000110
          .long    .L204
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000010e
          .long    .L203
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000010d
          .long    .L202
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000010c
          .long    .L201
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000010a
          .long    .L200
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000109
          .long    .L199
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000102
          .long    .L198
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000101
          .long    .L197
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ff
          .long    .L196
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000fe
          .long    .L195
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000fd
          .long    .L194
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000fb
          .long    .L193
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000f9
          .long    .L192
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000f7
          .long    .L191
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000f6
          .long    .L190
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000f2
          .long    .L189
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000f0
          .long    .L188
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ef
          .long    .L187
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ed
          .long    .L186
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ec
          .long    .L185
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ea
          .long    .L184
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000e8
          .long    .L183
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000e4
          .long    .L182
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000e3
          .long    .L181
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000e1
          .long    .L180
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000de
          .long    .L179
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000dd
          .long    .L178
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000db
          .long    .L177
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000cc
          .long    .L176
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d7
          .long    .L175
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d6
          .long    .L174
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d4
          .long    .L173
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d3
          .long    .L172
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d2
          .long    .L171
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000d0
          .long    .L170
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000cf
          .long    .L169
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000c8
          .long    .L168
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000c7
          .long    .L167
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000c5
          .long    .L166
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000c2
          .long    .L165
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000c1
          .long    .L164
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000bf
          .long    .L163
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000be
          .long    .L162
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000bb
          .long    .L161
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ba
          .long    .L160
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b8
          .long    .L159
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b7
          .long    .L158
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b6
          .long    .L157
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b4
          .long    .L156
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b3
          .long    .L155
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ad
          .long    .L154
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ac
          .long    .L153
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000aa
          .long    .L152
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a9
          .long    .L151
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a8
          .long    .L150
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a6
          .long    .L149
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a4
          .long    .L148
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a2
          .long    .L147
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000a1
          .long    .L146
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000085
          .long    .L95
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000083
          .long    .L94
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000082
          .long    .L93
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000080
          .long    .L92
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000007f
          .long    .L91
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000007e
          .long    .L90
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000007c
          .long    .L89
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000079
          .long    .L88
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000078
          .long    .L87
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000076
          .long    .L86
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000067
          .long    .L85
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000072
          .long    .L84
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000071
          .long    .L83
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000006f
          .long    .L82
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000006e
          .long    .L81
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000006d
          .long    .L80
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000006b
          .long    .L79
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000006a
          .long    .L78
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000063
          .long    .L77
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000062
          .long    .L76
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000060
          .long    .L75
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000005d
          .long    .L74
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000005c
          .long    .L73
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000005a
          .long    .L72
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000004b
          .long    .L71
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000056
          .long    .L70
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000055
          .long    .L69
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000053
          .long    .L68
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000052
          .long    .L67
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000051
          .long    .L66
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000004f
          .long    .L65
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000004e
          .long    .L64
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000046
          .long    .L63
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000045
          .long    .L62
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000043
          .long    .L61
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000003f
          .long    .L60
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000003e
          .long    .L59
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000003c
          .long    .L58
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000003b
          .long    .L57
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000037
          .long    .L56
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000036
          .long    .L55
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000034
          .long    .L54
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000033
          .long    .L53
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000032
          .long    .L52
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000030
          .long    .L51
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000002f
          .long    .L50
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000028
          .long    .L49
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000027
          .long    .L48
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000025
          .long    .L47
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000024
          .long    .L46
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000023
          .long    .L45
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000020
          .long    .L44
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000001e
          .long    .L43
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000001b
          .long    .L42
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000001a
          .long    .L41
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.L232:
          .section .xtalabeltable,       "", @progbits
.L233:
          .int      .L234-.L233
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/verif_tests_sw/Tools/xta/Applications/swc_usb"
.cc_top audio_init.function, audio_init
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000165
          .int      0x0000019d
          .long    .L230
.cc_bottom audio_init.function
.cc_top swc_audio_codec_ctl_reg_rd.function, swc_audio_codec_ctl_reg_rd
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000150
          .int      0x00000150
          .long    .L145
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000148
          .int      0x00000148
          .long    .L144
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000148
          .int      0x00000148
          .long    .L143
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000014c
          .int      0x0000014c
          .long    .L142
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000014a
          .int      0x0000014a
          .long    .L141
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000135
          .int      0x00000148
          .long    .L140
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000123
          .int      0x00000123
          .long    .L139
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000126
          .int      0x00000123
          .long    .L138
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000114
          .int      0x00000123
          .long    .L137
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000105
          .int      0x00000105
          .long    .L136
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000107
          .int      0x00000105
          .long    .L135
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000da
          .int      0x00000105
          .long    .L134
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000cc
          .int      0x000000cc
          .long    .L133
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000ce
          .int      0x000000cc
          .long    .L132
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000be
          .int      0x000000cc
          .long    .L131
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b0
          .int      0x000000b0
          .long    .L130
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x000000b2
          .int      0x000000b0
          .long    .L129
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000097
          .int      0x000000b0
          .long    .L128
.cc_bottom swc_audio_codec_ctl_reg_rd.function
.cc_top swc_audio_codec_ctl_reg_wr.function, swc_audio_codec_ctl_reg_wr
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000090
          .int      0x00000090
          .long    .L40
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L39
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000088
          .int      0x00000088
          .long    .L38
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000008c
          .int      0x0000008c
          .long    .L37
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000008a
          .int      0x0000008a
          .long    .L36
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000075
          .int      0x00000088
          .long    .L35
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000067
          .int      0x00000067
          .long    .L34
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000069
          .int      0x00000067
          .long    .L33
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000059
          .int      0x00000067
          .long    .L32
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000004b
          .int      0x0000004b
          .long    .L31
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000004d
          .int      0x0000004b
          .long    .L30
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000003b
          .int      0x0000004b
          .long    .L29
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000002c
          .int      0x0000002c
          .long    .L28
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x0000002e
          .int      0x0000002c
          .long    .L27
          .asciiz  "./tests/audio/src/audio_init_audioBoard.xc"
          .int      0x00000011
          .int      0x0000002c
          .long    .L26
.cc_bottom swc_audio_codec_ctl_reg_wr.function
.L234:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
