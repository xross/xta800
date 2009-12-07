          .file     "i2sOutput_spdifOutput.xc"
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
.extern audioWaitForInput, "f{0}(m:chd,m:chd)"
.extern audioManager, "f{0}(m:chd,m:chd,m:chd,&(s(){m(pSPDIF){bo:p:32},m(p_sdout){bo:p:32},m(p_bclk){bo:p:32},m(p_mclk){p},m(p_lrclk){bo:p:32},m(clk_audio_mclk){ck},m(clk_audio_bclk){ck}}))"
.extern audioPlayerInit, "f{0}(o:p,&(s(){m(pSPDIF){bo:p:32},m(p_sdout){bo:p:32},m(p_bclk){bo:p:32},m(p_mclk){p},m(p_lrclk){bo:p:32},m(clk_audio_mclk){ck},m(clk_audio_bclk){ck}}))"
          .text
          .align    2
.cc_top checkForData.function,checkForData
          .align    4
.type  checkForData, @function
.linkset checkForData.locnoside, 0
.linkset checkForData.locnochandec, 1
.linkset checkForData.nstackwords, 3
checkForData:
          entsp     0x3 
.L9:
          stw       r4, sp[0x0] 
          stw       r5, sp[0x1] 
          stw       r6, sp[0x2] 
          ldw       r6, sp[0x4] 
          ldw       r5, sp[0x5] 
          ldw       r4, sp[0x6] 
          clre      
          ldap      r11, .L3
          setv      res[r0], r11
          eeu       res[r0]
.xtabranch .L3
          setsr     0x1
          clrsr     0x1
.L2:
.L12:
          ldw       r4, sp[0x0] 
          ldw       r5, sp[0x1] 
          ldw       r6, sp[0x2] 
          retsp     0x3 
.L3:
.L10:
.L13:
          in        r4, res[r0] 
          mul       r4, r4, r5
          ashr      r11, r4, 0x8
          ldw       r4, r2[0x0] 
          st16      r11, r1[r4] 
.L14:
          in        r4, res[r0] 
          mul       r0, r4, r5
          ashr      r4, r0, 0x8
          ldw       r0, r2[0x0] 
          add       r0, r0, 0x1
          st16      r4, r1[r0] 
          ldc       r0, 0x100
          lss       r1, r0, r6
          ldw       r0, r3[0x0] 
          or        r0, r0, r1
          stw       r0, r3[0x0] 
          ldc       r0, 0x1fe
          lss       r0, r0, r6
          bt        r0, .L12 
.L11:
          ldw       r0, r2[0x0] 
          add       r1, r0, 0x2
          ldc       r0, 0x1ff
          and       r0, r1, r0
          stw       r0, r2[0x0] 
          bu        .L2 
.L0:
.cc_bottom checkForData.function
.linkset checkForData.maxchanends, 0
.linkset checkForData.maxtimers, 0
.linkset checkForData.maxthreads, 1
.cc_top mysext.function,mysext
          .align    4
.type  mysext, @function
.linkset mysext.locnoside, 1
.linkset mysext.locnochandec, 1
.linkset mysext.nstackwords, 0
mysext:
.L15:
          sext r0, r1
          retsp     0x0 
.cc_bottom mysext.function
.linkset mysext.maxchanends, 0
.linkset mysext.maxtimers, 0
.linkset mysext.maxthreads, 1
.cc_top parity32.function,parity32
          .align    4
.call parity32, __builtin_crc32
.set __builtin_crc32, 0
.linkset __builtin_crc32.locnoside, 0
.linkset __builtin_crc32.locnochandec, 1
.type  parity32, @function
.linkset parity32.locnoside, 1
.linkset parity32.locnochandec, 1
.linkset parity32.nstackwords, 0
parity32:
.L16:
          mkmsk     r3, 0x1
          ldc       r2, 0x0
          crc32     r0, r2, r3
          and       r0, r0, r3
          retsp     0x0 
.cc_bottom parity32.function
.linkset parity32.maxchanends, 0
.linkset parity32.maxtimers, 0
.linkset parity32.maxthreads, 1
.globl data_words_48, "a(32:uc)"
.type  data_words_48, @object
          .section .dp.data,       "adw", @progbits
.cc_top data_words_48.data, data_words_48
          .align    4
data_words_48:
.globl data_words_48.globound
          .set      data_words_48.globound,32
          .byte     0x0f
          .byte     0x0f
          .byte     0xf0
          .byte     0xf3
          .byte     0xf0
          .byte     0xcf
          .byte     0x0f
          .byte     0x33
          .byte     0xf3
          .byte     0x0f
          .byte     0x0c
          .byte     0xf3
          .byte     0x0c
          .byte     0xcf
          .byte     0xf3
          .byte     0x33
          .byte     0xcf
          .byte     0x0f
          .byte     0x30
          .byte     0xf3
          .byte     0x30
          .byte     0xcf
          .byte     0xcf
          .byte     0x33
          .byte     0x33
          .byte     0x0f
          .byte     0xcc
          .byte     0xf3
          .byte     0xcc
          .byte     0xcf
          .byte     0x33
          .byte     0x33
.cc_bottom data_words_48.data
.globl preamble_words_48, "a(6:uc)"
.type  preamble_words_48, @object
.cc_top preamble_words_48.data, preamble_words_48
          .align    4
preamble_words_48:
.globl preamble_words_48.globound
          .set      preamble_words_48.globound,6
          .byte     0x30
          .byte     0x3f
          .byte     0x0c
          .byte     0x3f
          .byte     0x03
          .byte     0x3f
.cc_bottom preamble_words_48.data
.locl audiovolume, "si"
          .align    4
          .section .dp.bss,        "adw", @nobits
.cc_top audiovolume.data, audiovolume
          .align    4
audiovolume:
          .int      0x00000000
.cc_bottom audiovolume.data
.locl count, "si"
.cc_top count.data, count
          .align    4
count:
          .int      0x00000000
.cc_bottom count.data
          .align    4
          .text
.cc_top audioManager_48kHz.function,audioManager_48kHz
          .align    4
.globread audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:118: error: previously used here"
.call audioManager_48kHz, __builtin_crc32
.call audioManager_48kHz, __builtin_bitrev
.call audioManager_48kHz, __builtin_bitrev
.globread audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:132: error: previously used here"
.globread audioManager_48kHz, preamble_words_48, "i2sOutput_spdifOutput.xc:136: error: previously used here"
.globread audioManager_48kHz, preamble_words_48, "i2sOutput_spdifOutput.xc:142: error: previously used here"
.globread audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:149: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:150: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:152: error: previously used here"
.call audioManager_48kHz, __builtin_crc32
.call audioManager_48kHz, __builtin_bitrev
.globwrite audioManager_48kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:158: error: previously used here (bytes 0..4)"
.globread audioManager_48kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:159: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:169: error: previously used here (bytes 0..4)"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:176: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:180: error: previously used here"
.globread audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:197: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:197: error: previously used here"
.globread audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:199: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:199: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:220: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:221: error: previously used here"
.call audioManager_48kHz, __builtin_bitrev
.globread audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:230: error: previously used here"
.globread audioManager_48kHz, preamble_words_48, "i2sOutput_spdifOutput.xc:236: error: previously used here"
.globread audioManager_48kHz, preamble_words_48, "i2sOutput_spdifOutput.xc:240: error: previously used here"
.globread audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:272: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:275: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:277: error: previously used here"
.call audioManager_48kHz, __builtin_crc32
.call audioManager_48kHz, __builtin_bitrev
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:287: error: previously used here"
.globwrite audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:315: error: previously used here"
.call audioManager_48kHz, audioWaitForInput
.globread audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:330: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:331: error: previously used here (bytes 0..4)"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:342: error: previously used here"
.globread audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:359: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:359: error: previously used here"
.globread audioManager_48kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:361: error: previously used here (bytes 0..4)"
.globwrite audioManager_48kHz, audiodata, "i2sOutput_spdifOutput.xc:361: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:381: error: previously used here"
.globread audioManager_48kHz, data_words_48, "i2sOutput_spdifOutput.xc:382: error: previously used here"
.set __builtin_bitrev, 0
.linkset __builtin_bitrev.locnoside, 1
.linkset __builtin_bitrev.locnochandec, 1
.globl audioManager_48kHz, "f{0}(m:chd,m:chd,m:chd,&(s(){m(pSPDIF){bo:p:32},m(p_sdout){bo:p:32},m(p_bclk){bo:p:32},m(p_mclk){p},m(p_lrclk){bo:p:32},m(clk_audio_mclk){ck},m(clk_audio_bclk){ck}}),si)"
.globl audioManager_48kHz.nstackwords
.globl audioManager_48kHz.maxthreads
.globl audioManager_48kHz.maxtimers
.globl audioManager_48kHz.maxchanends
.globl audioManager_48kHz.maxsync
.type  audioManager_48kHz, @function
.linkset audioManager_48kHz.locnoside, 0
.linkset audioManager_48kHz.locnochandec, 1
.linkset .LLNK1, audioWaitForInput.nstackwords $M audioWaitForInput.nstackwords
.linkset .LLNK0, .LLNK1 + 24
.linkset audioManager_48kHz.nstackwords, .LLNK0
audioManager_48kHz:
          entsp     0x18 
.L110:
          stw       r4, sp[0x4] 
          stw       r5, sp[0x5] 
          stw       r6, sp[0x6] 
          stw       r7, sp[0x7] 
          stw       r8, sp[0x8] 
          stw       r9, sp[0x9] 
          stw       r10, sp[0xa] 
          stw       r0, sp[0x1] 
          stw       r1, sp[0x2] 
          stw       r2, sp[0x3] 
          ldc       r0, 0x0
          stw       r0, sp[0xb] 
          ldc       r8, 0x0
          ldw       r11, cp[.LC0]
          .section .cp.const4,     "acM", @progbits, 4
.LC0:
          .align    4
          .int      0x00200008

          .text
          stw       r11, sp[0xc] 
          ldw       r11, cp[.LC1]
          .section .cp.const4,     "acM", @progbits, 4
.LC1:
          .align    4
          .int      0x00400008

          .text
          stw       r11, sp[0xd] 
          ldc       r0, 0x0
          stw       r0, sp[0xe] 
          mkmsk     r0, 0x1
          stw       r0, sp[0xf] 
          ldc       r0, 0x0
          stw       r0, sp[0x10] 
          ldc       r6, 0x0
          ldaw      r0, dp[audiodata] 
          ldc       r1, 0x0
          ld16s     r1, r0[r1] 
          ldc       r0, 0xc
          shl       r0, r1, r0
          ldw       r11, cp[.LC2]
          .section .cp.const4,     "acM", @progbits, 4
.LC2:
          .align    4
          .int      0x0ffffff0

          .text
          and       r0, r0, r11
          mov       r10, r0
          mov       r0, r10
          ldc       r2, 0x0
          mkmsk     r1, 0x1
          crc32     r0, r2, r1
          mkmsk     r1, 0x1
          and       r2, r0, r1
.L111:
          bitrev    r1, r2
          ldc       r0, 0x2
          or        r0, r1, r0
          or        r4, r0, r10
.L112:
          ldc       r0, 0xc0
          stw       r0, sp[0x11] 
.L113:
          ldw       r2, r3[0x1] 
          ldaw      r0, dp[audiodata] 
          ldw       r1, sp[0xb] 
          ld16s     r1, r0[r1] 
          shl       r1, r1, 0x10
          bitrev    r1, r1
.L175:
          out       res[r2], r1 
          ldw       r1, r3[0x4] 
          ldw       r11, cp[.LC3]
          .section .cp.const4,     "acM", @progbits, 4
.LC3:
          .align    4
          .int      0x80000000

          .text
.L176:
          out       res[r1], r11 
          mkmsk     r1, 0x4
          and       r2, r4, r1
          shl       r2, r2, 0x1
          add       r5, r2, 0x1
          ldaw      r1, dp[preamble_words_48] 
          ld8u      r1, r1[r5] 
          xor       r5, r1, r6
          ldw       r1, r3[0x2] 
          ldw       r11, cp[.LC4]
          .section .cp.const4,     "acM", @progbits, 4
.LC4:
          .align    4
          .int      0xcccccccc

          .text
.xtaendpoint audio_48_left_o0, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 139
.L177:
          out       res[r1], r11 
          ldw       r1, r3[0x0] 
.L178:
          outpw     res[r1], r5, 0x8 
          ldaw      r1, dp[preamble_words_48] 
          ld8u      r1, r1[r2] 
          xor       r5, r1, r6
          ldw       r1, r3[0x0] 
.xtaendpoint audio_48_left_o1, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 143
.L179:
          outpw     res[r1], r5, 0x8 
          ldc       r1, 0x8
          sext r1, r1
.L114:
          ashr      r1, r1, 0x8
          mov       r6, r1
          shr       r7, r4, 0x4
          ldw       r1, sp[0xc] 
          ashr      r1, r1, 0x1
          stw       r1, sp[0xc] 
          ldw       r2, sp[0xd] 
          ldc       r1, 0x2
          and       r1, r2, r1
          mov       r4, r1
          ldw       r1, sp[0xb] 
          add       r1, r1, 0x1
          stw       r1, sp[0x14] 
          ldw       r1, sp[0x14] 
          ld16s     r2, r0[r1] 
          ldc       r1, 0xc
          shl       r1, r2, r1
          ldw       r11, cp[.LC2]
          and       r1, r1, r11
          mov       r10, r1
          ldaw      r1, dp[data_words_48] 
          mkmsk     r2, 0x4
          and       r2, r7, r2
          shl       r2, r2, 0x1
          ld8u      r5, r1[r2] 
          xor       r5, r5, r6
          ldw       r9, r3[0x0] 
          add       r2, r2, 0x1
          ld8u      r2, r1[r2] 
          xor       r2, r2, r6
.xtaendpoint audio_48_left_o2, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 151
.L180:
          outpw     res[r9], r2, 0x8 
          or        r2, r10, r4
          ldc       r9, 0x0
          mkmsk     r6, 0x1
          crc32     r2, r9, r6
          mkmsk     r6, 0x1
          and       r2, r2, r6
.L115:
          or        r2, r2, r4
          bitrev    r2, r2
          stw       r2, sp[0x12] 
          ldw       r2, r3[0x0] 
.xtaendpoint audio_48_left_o3, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 155
.L181:
          outpw     res[r2], r5, 0x8 
          ldw       r5, dp[count] 
          ldc       r2, 0xa00
          add       r2, r5, r2
          stw       r2, dp[count] 
          ldw       r5, dp[count] 
          ldw       r2, sp[0x19] 
          lss       r2, r2, r5
          bt        r2, .L116 
.L30:
.L120:
          ldc       r2, 0x8
          sext r2, r2
.L121:
          ashr      r2, r2, 0x8
          mov       r6, r2
          ldc       r4, 0x0
          clre      
          ldap      r11, .L49
          ldw       r2, sp[0x1] 
          setv      res[r2], r11
          ldw       r2, sp[0x1] 
          eeu       res[r2]
          ldw       r2, dp[audiovolume] 
          stw       r2, sp[0x15] 
.L122:
          shr       r7, r7, 0x4
          mkmsk     r2, 0x4
          and       r2, r7, r2
          shl       r2, r2, 0x1
          add       r5, r2, 0x1
          ld8u      r5, r1[r5] 
          xor       r5, r5, r6
          ldw       r9, r3[0x2] 
          ldw       r11, cp[.LC4]
.xtaendpoint audio_48_left_o4, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 177
.L182:
          out       res[r9], r11 
          ldw       r9, r3[0x0] 
.L183:
          outpw     res[r9], r5, 0x8 
          ld8u      r2, r1[r2] 
          xor       r5, r2, r6
          ldc       r2, 0x8
          sext r2, r2
.L123:
          ashr      r2, r2, 0x8
          mov       r6, r2
          shr       r7, r7, 0x4
          ldw       r2, r3[0x0] 
.xtaendpoint audio_48_left_o5, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 183
.L184:
          outpw     res[r2], r5, 0x8 
.xtabranch .L49
          setsr     0x1
          clrsr     0x1
.L48:
.L126:
          ldw       r2, sp[0x12] 
          or        r5, r10, r2
          mkmsk     r2, 0x1
          or        r10, r5, r2
          ldw       r5, r3[0x0] 
          mkmsk     r2, 0x4
          and       r2, r7, r2
          shl       r2, r2, 0x1
          add       r9, r2, 0x1
          ld8u      r9, r1[r9] 
          xor       r9, r9, r6
.xtaendpoint audio_48_left_o6, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 219
.L185:
          outpw     res[r5], r9, 0x8 
          ld8u      r2, r1[r2] 
          xor       r5, r2, r6
          ldw       r2, r3[0x0] 
.xtaendpoint audio_48_left_o7, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 222
.L186:
          outpw     res[r2], r5, 0x8 
          ldc       r2, 0x8
          sext r2, r2
.L127:
          ashr      r2, r2, 0x8
          mov       r6, r2
          add       r4, r4, 0x1
.L128:
          mkmsk     r2, 0x2
          lss       r2, r4, r2
          bt        r2, .L122 
.L129:
          ldw       r4, r3[0x1] 
          ldw       r2, sp[0x14] 
          ld16s     r2, r0[r2] 
          shl       r2, r2, 0x10
          bitrev    r2, r2
.L187:
          out       res[r4], r2 
          ldw       r2, r3[0x4] 
          ldw       r11, cp[.LC5]
          .section .cp.const4,     "acM", @progbits, 4
.LC5:
          .align    4
          .int      0x7fffffff

          .text
.L188:
          out       res[r2], r11 
          mkmsk     r2, 0x4
          and       r2, r10, r2
          shl       r2, r2, 0x1
          add       r5, r2, 0x1
          ldaw      r4, dp[preamble_words_48] 
          ld8u      r4, r4[r5] 
          xor       r5, r4, r6
          ldw       r4, r3[0x2] 
          ldw       r11, cp[.LC4]
.xtaendpoint audio_48_right_o0, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 237
.L189:
          out       res[r4], r11 
          ldw       r4, r3[0x0] 
.L190:
          outpw     res[r4], r5, 0x8 
          ldaw      r4, dp[preamble_words_48] 
          ld8u      r2, r4[r2] 
          xor       r5, r2, r6
          ldc       r2, 0x8
          sext r2, r2
.L130:
          ashr      r2, r2, 0x8
          mov       r6, r2
          shr       r7, r10, 0x4
          ldw       r2, sp[0x10] 
          bt        r2, .L131 
.L59:
.L133:
          ldc       r2, 0x0
          stw       r2, sp[0x10] 
.L134:
          ldw       r2, r3[0x0] 
.xtaendpoint audio_48_right_o1, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 258
.L191:
          outpw     res[r2], r5, 0x8 
          ldw       r2, sp[0x11] 
          sub       r2, r2, 0x1
          stw       r2, sp[0x11] 
          ldw       r2, sp[0x11] 
          bf        r2, .L135 
.L136:
          ldw       r2, sp[0xd] 
          ashr      r2, r2, 0x1
          stw       r2, sp[0xd] 
          ldc       r9, 0x0
.L137:
          ldw       r2, sp[0xb] 
          ld16s     r2, r0[r2] 
          ldc       r0, 0xc
          shl       r0, r2, r0
          ldw       r11, cp[.LC2]
          and       r10, r0, r11
          ldw       r4, r3[0x0] 
          mkmsk     r0, 0x4
          and       r2, r7, r0
          shl       r2, r2, 0x1
          add       r0, r2, 0x1
          ld8u      r0, r1[r0] 
          xor       r0, r0, r6
.xtaendpoint audio_48_right_o2, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 274
.L192:
          outpw     res[r4], r0, 0x8 
          ldw       r4, sp[0xc] 
          ldc       r0, 0x2
          and       r0, r4, r0
          mov       r4, r0
          ld8u      r0, r1[r2] 
          xor       r5, r0, r6
          ldw       r0, r3[0x0] 
.xtaendpoint audio_48_right_o3, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 278
.L193:
          outpw     res[r0], r5, 0x8 
          or        r0, r10, r4
          ldc       r5, 0x0
          mkmsk     r2, 0x1
          crc32     r0, r5, r2
          mkmsk     r2, 0x1
          and       r2, r0, r2
.L138:
          or        r0, r4, r2
          bitrev    r0, r0
          or        r0, r0, r9
          or        r4, r0, r10
          ldc       r0, 0x8
          sext r0, r0
.L139:
          ashr      r0, r0, 0x8
          mov       r6, r0
          ldc       r0, 0x0
          stw       r0, sp[0x13] 
          ldap      r11, .L103
          ldw       r0, sp[0x1] 
          setv      res[r0], r11
.L140:
          shr       r7, r7, 0x4
          mkmsk     r0, 0x4
          and       r2, r7, r0
          shl       r2, r2, 0x1
          add       r0, r2, 0x1
          ld8u      r0, r1[r0] 
          xor       r5, r0, r6
          ldw       r0, sp[0xf] 
          bt        r0, .L141 
.L72:
.L157:
          ldw       r0, r3[0x2] 
          ldw       r11, cp[.LC4]
.L194:
          out       res[r0], r11 
          ldw       r0, r3[0x0] 
.xtaendpoint audio_48_right_o4, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 340
.L195:
          outpw     res[r0], r5, 0x8 
          ldaw      r1, dp[data_words_48] 
          ld8u      r0, r1[r2] 
          xor       r5, r0, r6
          ldc       r0, 0x8
          sext r0, r0
.L158:
          ashr      r0, r0, 0x8
          mov       r6, r0
          shr       r7, r7, 0x4
          ldw       r0, r3[0x0] 
.xtaendpoint audio_48_right_o5, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 345
.L196:
          outpw     res[r0], r5, 0x8 
          clre      
          ldw       r0, sp[0x1] 
          eeu       res[r0]
.xtabranch .L103
          setsr     0x1
          clrsr     0x1
.L102:
.L161:
          ldw       r5, r3[0x0] 
          mkmsk     r0, 0x4
          and       r2, r7, r0
          shl       r2, r2, 0x1
          add       r0, r2, 0x1
          ld8u      r0, r1[r0] 
          xor       r0, r0, r6
.xtaendpoint audio_48_right_o6, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 380
.L197:
          outpw     res[r5], r0, 0x8 
          ld8u      r0, r1[r2] 
          xor       r5, r0, r6
          ldw       r0, r3[0x0] 
.xtaendpoint audio_48_right_o7, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 383
.L198:
          outpw     res[r0], r5, 0x8 
          ldc       r0, 0x8
          sext r0, r0
.L162:
          ashr      r0, r0, 0x8
          mov       r6, r0
          ldw       r0, sp[0x13] 
          add       r0, r0, 0x1
          stw       r0, sp[0x13] 
.L163:
          ldw       r2, sp[0x13] 
          mkmsk     r0, 0x2
          lss       r0, r2, r0
          bt        r0, .L140 
.L164:
          ldw       r0, sp[0x11] 
          bt        r0, .L113 
.L165:
          ldw       r0, sp[0xf] 
          bt        r0, .L112 
.xtalabel audio_48_exit, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 389
.L166:
          ldw       r4, sp[0x4] 
          ldw       r5, sp[0x5] 
          ldw       r6, sp[0x6] 
          ldw       r7, sp[0x7] 
          ldw       r8, sp[0x8] 
          ldw       r9, sp[0x9] 
          ldw       r10, sp[0xa] 
          retsp     0x18 
.L116:
          clre      
          ldap      r11, .L35
          ldw       r2, sp[0x2] 
          setv      res[r2], r11
          ldw       r2, sp[0x2] 
          eeu       res[r2]
.xtabranch .L35
          setsr     0x1
          clrsr     0x1
.L119:
          ldw       r2, sp[0x19] 
          sub       r2, r5, r2
          stw       r2, dp[count] 
          bu        .L30 
.L35:
.L118:
          ldw       r2, sp[0x2] 
.L199:
          in        r2, res[r2] 
          ldw       r4, sp[0x2] 
          mkmsk     r2, 0x1
.L200:
          out       res[r4], r2 
          bu        .L119 
.L32:
.L49:
.L124:
          ldw       r2, sp[0x1] 
.L201:
          in        r2, res[r2] 
          ldw       r9, sp[0x15] 
          mul       r2, r2, r9
          ashr      r2, r2, 0x8
          st16      r2, r0[r8] 
          ldw       r2, sp[0x1] 
.L202:
          in        r2, res[r2] 
          mul       r2, r2, r9
          ashr      r5, r2, 0x8
          add       r2, r8, 0x1
          st16      r5, r0[r2] 
          ldc       r5, 0x100
          ldw       r2, sp[0xe] 
          lss       r5, r5, r2
          ldw       r2, sp[0x10] 
          or        r2, r2, r5
          stw       r2, sp[0x10] 
          ldc       r5, 0x1fe
          ldw       r2, sp[0xe] 
          lss       r2, r5, r2
          bt        r2, .L126 
.L125:
          add       r5, r8, 0x2
          ldc       r2, 0x1ff
          and       r8, r5, r2
          ldw       r2, sp[0xe] 
          add       r2, r2, 0x2
          stw       r2, sp[0xe] 
          bu        .L48 
.L46:
.L131:
          ldc       r4, 0x0
          ldw       r2, sp[0xe] 
          lss       r2, r4, r2
          bf        r2, .L59 
.L132:
          ldw       r2, sp[0xb] 
          add       r4, r2, 0x2
          ldc       r2, 0x1ff
          and       r2, r4, r2
          stw       r2, sp[0xb] 
          ldw       r2, sp[0xe] 
          sub       r2, r2, 0x2
          stw       r2, sp[0xe] 
          bu        .L134 
.L135:
          ldw       r11, cp[.LC0]
          stw       r11, sp[0xc] 
          ldw       r11, cp[.LC1]
          stw       r11, sp[0xd] 
          ldc       r9, 0x2
          bu        .L137 
.L141:
          clre      
          ldap      r11, .L77
          ldw       r0, sp[0x3] 
          setv      res[r0], r11
          ldw       r0, sp[0x3] 
          eeu       res[r0]
.xtabranch .L77
          setsr     0x1
          clrsr     0x1
          bu        .L72 
.L77:
.xtalabel audio_48_freqadjust, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 293
.L143:
          ldw       r0, sp[0x3] 
.L203:
          in        r0, res[r0] 
          ldc       r1, 0x4
          lsu       r1, r1, r0
          bt        r1, .L172 
.xtabranch .L173 + 8,.L173 + 6,.L173 + 4,.L173 + 2,.L173
          bru       r0 
.L173:
.jmptable           .L144, .L148, .L152, .L153, .L154
.L172:
          bu        .L72 
.L144:
          ldw       r0, sp[0x3] 
.L204:
          in        r0, res[r0] 
          bf        r0, .L145 
          eq        r0, r0, 0x1
          bt        r0, .L146 
          bu        .L72 
.L145:
          ldw       r11, cp[.LC6]
          .section .cp.const4,     "acM", @progbits, 4
.LC6:
          .align    4
          .int      0x0001b900

          .text
          stw       r11, sp[0x19] 
          bu        .L157 
.L146:
          ldw       r11, cp[.LC7]
          .section .cp.const4,     "acM", @progbits, 4
.LC7:
          .align    4
          .int      0x0001e000

          .text
          stw       r11, sp[0x19] 
          bu        .L157 
.xtalabel audio_48_wipebuf, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 313
.L148:
          ldc       r9, 0x0
          ldaw      r0, dp[audiodata] 
.L149:
          ldc       r1, 0x0
          st16      r1, r0[r9] 
          add       r9, r9, 0x1
.L150:
          ldc       r1, 0x200
          lss       r1, r9, r1
          bt        r1, .L149 
          bu        .L72 
.L152:
          ldc       r0, 0x0
          stw       r0, sp[0xf] 
          bu        .L157 
.xtalabel audio_48_waitforinput, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 324
.L153:
          ldw       r0, sp[0x2] 
          ldw       r1, sp[0x3] 
          stw       r3, sp[0x16] 
          stw       r2, sp[0x17] 
.L205:
          bl        audioWaitForInput 
          ldw       r3, sp[0x16] 
          ldw       r2, sp[0x17] 
          bu        .L157 
.xtalabel audio_48_voladjust, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 328
.L154:
          ldw       r0, sp[0x3] 
.L206:
          in        r0, res[r0] 
          stw       r0, dp[audiovolume] 
          ldw       r9, dp[audiovolume] 
          stw       r9, dp[audiovolume] 
          mkmsk     r0, 0x8
          eq        r0, r9, r0
          bf        r0, .L72 
.L155:
          ldc       r0, 0x100
          stw       r0, dp[audiovolume] 
          bu        .L72 
.L74:
.L103:
.L159:
          ldw       r0, sp[0x1] 
.L207:
          in        r2, res[r0] 
          ldw       r9, dp[audiovolume] 
          mul       r0, r2, r9
          ashr      r2, r0, 0x8
          ldaw      r0, dp[audiodata] 
          st16      r2, r0[r8] 
          ldw       r2, sp[0x1] 
.L208:
          in        r2, res[r2] 
          mul       r2, r2, r9
          ashr      r5, r2, 0x8
          add       r2, r8, 0x1
          st16      r5, r0[r2] 
          ldc       r2, 0x100
          ldw       r0, sp[0xe] 
          lss       r2, r2, r0
          ldw       r0, sp[0x10] 
          or        r0, r0, r2
          stw       r0, sp[0x10] 
          ldc       r2, 0x1fe
          ldw       r0, sp[0xe] 
          lss       r0, r2, r0
          bt        r0, .L161 
.L160:
          add       r2, r8, 0x2
          ldc       r0, 0x1ff
          and       r8, r2, r0
          ldw       r0, sp[0xe] 
          add       r0, r0, 0x2
          stw       r0, sp[0xe] 
          bu        .L102 
.L100:
.cc_bottom audioManager_48kHz.function
.linkset audioManager_48kHz.maxchanends, audioWaitForInput.maxchanends
.linkset audioManager_48kHz.maxtimers, audioWaitForInput.maxtimers
.linkset .LLNK4, audioWaitForInput.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset audioManager_48kHz.maxthreads, .LLNK2
.globl data_words_32, "a(32:us)"
.type  data_words_32, @object
          .section .dp.data,       "adw", @progbits
.cc_top data_words_32.data, data_words_32
          .align    4
data_words_32:
.globl data_words_32.globound
          .set      data_words_32.globound,32
          .short    0x003f
          .short    0x003f
          .short    0x0fc0
          .short    0x0fc7
          .short    0x0fc0
          .short    0x0e3f
          .short    0x003f
          .short    0x01c7
          .short    0x0fc7
          .short    0x003f
          .short    0x0038
          .short    0x0fc7
          .short    0x0038
          .short    0x0e3f
          .short    0x0fc7
          .short    0x01c7
          .short    0x0e3f
          .short    0x003f
          .short    0x01c0
          .short    0x0fc7
          .short    0x01c0
          .short    0x0e3f
          .short    0x0e3f
          .short    0x01c7
          .short    0x01c7
          .short    0x003f
          .short    0x0e38
          .short    0x0fc7
          .short    0x0e38
          .short    0x0e3f
          .short    0x01c7
          .short    0x01c7
.cc_bottom data_words_32.data
.globl preamble_words_32, "a(6:us)"
.type  preamble_words_32, @object
.cc_top preamble_words_32.data, preamble_words_32
          .align    4
preamble_words_32:
.globl preamble_words_32.globound
          .set      preamble_words_32.globound,6
          .short    0x01c0
          .short    0x01ff
          .short    0x0038
          .short    0x01ff
          .short    0x0007
          .short    0x01ff
.cc_bottom preamble_words_32.data
          .align    4
          .text
.cc_top audioManager_32kHz.function,audioManager_32kHz
          .align    4
.globread audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:439: error: previously used here"
.call audioManager_32kHz, __builtin_crc32
.call audioManager_32kHz, __builtin_bitrev
.call audioManager_32kHz, __builtin_bitrev
.globread audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:453: error: previously used here"
.globread audioManager_32kHz, preamble_words_32, "i2sOutput_spdifOutput.xc:457: error: previously used here"
.globread audioManager_32kHz, preamble_words_32, "i2sOutput_spdifOutput.xc:465: error: previously used here"
.globread audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:472: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:473: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:475: error: previously used here"
.call audioManager_32kHz, __builtin_crc32
.call audioManager_32kHz, __builtin_bitrev
.globwrite audioManager_32kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:481: error: previously used here (bytes 0..4)"
.globread audioManager_32kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:482: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, count, 0, 4, "i2sOutput_spdifOutput.xc:492: error: previously used here (bytes 0..4)"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:499: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:504: error: previously used here"
.globread audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:521: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:521: error: previously used here"
.globread audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:523: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:523: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:544: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:545: error: previously used here"
.call audioManager_32kHz, __builtin_bitrev
.globread audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:554: error: previously used here"
.globread audioManager_32kHz, preamble_words_32, "i2sOutput_spdifOutput.xc:560: error: previously used here"
.globread audioManager_32kHz, preamble_words_32, "i2sOutput_spdifOutput.xc:565: error: previously used here"
.globread audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:595: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:598: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:600: error: previously used here"
.call audioManager_32kHz, __builtin_crc32
.call audioManager_32kHz, __builtin_bitrev
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:610: error: previously used here"
.globwrite audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:638: error: previously used here"
.call audioManager_32kHz, audioWaitForInput
.globread audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:652: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:653: error: previously used here (bytes 0..4)"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:665: error: previously used here"
.globread audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:682: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:682: error: previously used here"
.globread audioManager_32kHz, audiovolume, 0, 4, "i2sOutput_spdifOutput.xc:684: error: previously used here (bytes 0..4)"
.globwrite audioManager_32kHz, audiodata, "i2sOutput_spdifOutput.xc:684: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:704: error: previously used here"
.globread audioManager_32kHz, data_words_32, "i2sOutput_spdifOutput.xc:705: error: previously used here"
.globl audioManager_32kHz, "f{0}(m:chd,m:chd,m:chd,&(s(){m(pSPDIF){bo:p:32},m(p_sdout){bo:p:32},m(p_bclk){bo:p:32},m(p_mclk){p},m(p_lrclk){bo:p:32},m(clk_audio_mclk){ck},m(clk_audio_bclk){ck}}),si)"
.globl audioManager_32kHz.nstackwords
.globl audioManager_32kHz.maxthreads
.globl audioManager_32kHz.maxtimers
.globl audioManager_32kHz.maxchanends
.globl audioManager_32kHz.maxsync
.type  audioManager_32kHz, @function
.linkset audioManager_32kHz.locnoside, 0
.linkset audioManager_32kHz.locnochandec, 1
.linkset .LLNK6, audioWaitForInput.nstackwords $M audioWaitForInput.nstackwords
.linkset .LLNK5, .LLNK6 + 23
.linkset audioManager_32kHz.nstackwords, .LLNK5
audioManager_32kHz:
          entsp     0x17 
.L302:
          stw       r4, sp[0x4] 
          stw       r5, sp[0x5] 
          stw       r6, sp[0x6] 
          stw       r7, sp[0x7] 
          stw       r8, sp[0x8] 
          stw       r9, sp[0x9] 
          stw       r10, sp[0xa] 
          stw       r0, sp[0x1] 
          stw       r1, sp[0x2] 
          stw       r2, sp[0x3] 
          ldc       r0, 0x0
          stw       r0, sp[0xb] 
          ldc       r9, 0x0
          ldw       r11, cp[.LC0]
          stw       r11, sp[0xc] 
          ldw       r11, cp[.LC1]
          stw       r11, sp[0xd] 
          ldc       r0, 0x0
          stw       r0, sp[0xe] 
          mkmsk     r0, 0x1
          stw       r0, sp[0xf] 
          ldc       r0, 0x0
          stw       r0, sp[0x10] 
          ldc       r7, 0x0
          ldaw      r1, dp[audiodata] 
          ldc       r0, 0x0
          ld16s     r1, r1[r0] 
          ldc       r0, 0xc
          shl       r0, r1, r0
          ldw       r11, cp[.LC2]
          and       r0, r0, r11
          mov       r10, r0
          mov       r0, r10
          ldc       r2, 0x0
          mkmsk     r1, 0x1
          crc32     r0, r2, r1
          mkmsk     r1, 0x1
          and       r0, r0, r1
.L303:
          bitrev    r1, r0
          ldc       r0, 0x2
          or        r0, r1, r0
          or        r4, r0, r10
.L304:
          ldc       r0, 0xc0
          stw       r0, sp[0x11] 
.L305:
          ldw       r2, r3[0x1] 
          ldaw      r1, dp[audiodata] 
          ldw       r0, sp[0xb] 
          ld16s     r0, r1[r0] 
          shl       r0, r0, 0x10
          bitrev    r0, r0
.L367:
          out       res[r2], r0 
          ldw       r0, r3[0x4] 
          ldw       r11, cp[.LC3]
.L368:
          out       res[r0], r11 
          mkmsk     r0, 0x4
          and       r0, r4, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ldaw      r1, dp[preamble_words_32] 
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r0, r3[0x2] 
          ldw       r11, cp[.LC8]
          .section .cp.const4,     "acM", @progbits, 4
.LC8:
          .align    4
          .int      0xc71c71c7

          .text
.xtaendpoint audio_32_left_o0, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 460
.L369:
          out       res[r0], r11 
          ldw       r1, r3[0x2] 
          ldc       r0, 0x1c71
.L370:
          outpw     res[r1], r0, 0x10 
          ldw       r1, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r1], r0
.L371:
          out       res[r1], r5 
          ldaw      r0, dp[preamble_words_32] 
          ld16s     r0, r0[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r1, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r1], r0
.xtaendpoint audio_32_left_o1, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 466
.L372:
          out       res[r1], r5 
          ldc       r0, 0x8
          sext r0, r0
.L306:
          ashr      r0, r0, 0x8
          mov       r7, r0
          shr       r8, r4, 0x4
          ldw       r0, sp[0xc] 
          ashr      r0, r0, 0x1
          stw       r0, sp[0xc] 
          ldw       r1, sp[0xd] 
          ldc       r0, 0x2
          and       r0, r1, r0
          mov       r4, r0
          ldw       r0, sp[0xb] 
          add       r0, r0, 0x1
          stw       r0, sp[0x13] 
          ldaw      r1, dp[audiodata] 
          ldw       r0, sp[0x13] 
          ld16s     r1, r1[r0] 
          ldc       r0, 0xc
          shl       r0, r1, r0
          ldw       r11, cp[.LC2]
          and       r0, r0, r11
          mov       r10, r0
          ldaw      r1, dp[data_words_32] 
          mkmsk     r0, 0x4
          and       r0, r8, r0
          shl       r2, r0, 0x1
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r6, r3[0x0] 
          add       r0, r2, 0x1
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r2, r0, r7
          ldc       r0, 0xc
          setpsc    res[r6], r0
.xtaendpoint audio_32_left_o2, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 474
.L373:
          out       res[r6], r2 
          or        r0, r10, r4
          ldc       r6, 0x0
          mkmsk     r2, 0x1
          crc32     r0, r6, r2
          mkmsk     r2, 0x1
          and       r0, r0, r2
.L307:
          or        r0, r0, r4
          bitrev    r0, r0
          stw       r0, sp[0x12] 
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_left_o3, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 478
.L374:
          out       res[r2], r5 
          ldw       r4, dp[count] 
          ldc       r0, 0xf00
          add       r0, r4, r0
          stw       r0, dp[count] 
          ldw       r4, dp[count] 
          ldw       r0, sp[0x18] 
          lss       r0, r0, r4
          bt        r0, .L308 
.L222:
.L312:
          ldc       r0, 0x8
          sext r0, r0
.L313:
          ashr      r0, r0, 0x8
          mov       r7, r0
          ldc       r4, 0x0
          clre      
          ldap      r11, .L241
          ldw       r0, sp[0x1] 
          setv      res[r0], r11
          ldw       r0, sp[0x1] 
          eeu       res[r0]
          ldw       r0, dp[audiovolume] 
          stw       r0, sp[0x14] 
.L314:
          shr       r8, r8, 0x4
          mkmsk     r0, 0x4
          and       r0, r8, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r0, r3[0x2] 
          ldw       r11, cp[.LC8]
.xtaendpoint audio_32_left_o4, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 500
.L375:
          out       res[r0], r11 
          ldw       r6, r3[0x2] 
          ldc       r0, 0x1c71
.L376:
          outpw     res[r6], r0, 0x10 
          ldw       r6, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r6], r0
.L377:
          out       res[r6], r5 
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldc       r0, 0x8
          sext r0, r0
.L315:
          ashr      r0, r0, 0x8
          mov       r7, r0
          shr       r8, r8, 0x4
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_left_o5, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 507
.L378:
          out       res[r2], r5 
.xtabranch .L241
          setsr     0x1
          clrsr     0x1
.L240:
.L318:
          ldw       r0, sp[0x12] 
          or        r2, r10, r0
          mkmsk     r0, 0x1
          or        r10, r2, r0
          ldw       r5, r3[0x0] 
          mkmsk     r0, 0x4
          and       r0, r8, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r6, r0, r7
          ldc       r0, 0xc
          setpsc    res[r5], r0
.xtaendpoint audio_32_left_o6, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 543
.L379:
          out       res[r5], r6 
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_left_o7, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 546
.L380:
          out       res[r2], r5 
          ldc       r0, 0x8
          sext r0, r0
.L319:
          ashr      r0, r0, 0x8
          mov       r7, r0
          add       r4, r4, 0x1
.L320:
          mkmsk     r0, 0x2
          lss       r0, r4, r0
          bt        r0, .L314 
.L321:
          mov       r8, r10
          ldw       r4, r3[0x1] 
          ldaw      r2, dp[audiodata] 
          ldw       r0, sp[0x13] 
          ld16s     r0, r2[r0] 
          shl       r0, r0, 0x10
          bitrev    r0, r0
.L381:
          out       res[r4], r0 
          ldw       r0, r3[0x4] 
          ldw       r11, cp[.LC5]
.L382:
          out       res[r0], r11 
          mkmsk     r0, 0x4
          and       r0, r10, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ldaw      r4, dp[preamble_words_32] 
          ld16s     r4, r4[r0] 
          zext      r4, 0x10 
          xor       r5, r4, r7
          ldw       r4, r3[0x2] 
          ldw       r11, cp[.LC8]
.xtaendpoint audio_32_right_o0, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 561
.L383:
          out       res[r4], r11 
          ldw       r6, r3[0x2] 
          ldc       r4, 0x1c71
.L384:
          outpw     res[r6], r4, 0x10 
          ldw       r6, r3[0x0] 
          ldc       r4, 0xc
          setpsc    res[r6], r4
.L385:
          out       res[r6], r5 
          ldaw      r4, dp[preamble_words_32] 
          ld16s     r4, r4[r2] 
          zext      r4, 0x10 
          xor       r5, r4, r7
          ldc       r4, 0x8
          sext r4, r4
.L322:
          ashr      r4, r4, 0x8
          mov       r7, r4
          ldw       r4, sp[0x10] 
          bt        r4, .L323 
.L251:
.L325:
          ldc       r4, 0x0
          stw       r4, sp[0x10] 
.L326:
          ldw       r6, r3[0x0] 
          ldc       r4, 0xc
          setpsc    res[r6], r4
.xtaendpoint audio_32_right_o1, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 581
.L386:
          out       res[r6], r5 
          ldw       r4, sp[0x11] 
          sub       r4, r4, 0x1
          stw       r4, sp[0x11] 
          ldw       r4, sp[0x11] 
          bf        r4, .L327 
.L328:
          ldw       r4, sp[0xd] 
          ashr      r4, r4, 0x1
          stw       r4, sp[0xd] 
          ldc       r6, 0x0
.L329:
          ldaw      r5, dp[audiodata] 
          ldw       r4, sp[0xb] 
          ld16s     r5, r5[r4] 
          ldc       r4, 0xc
          shl       r4, r5, r4
          ldw       r11, cp[.LC2]
          and       r10, r4, r11
          ldw       r5, r3[0x0] 
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r4, r0, r7
          ldc       r0, 0xc
          setpsc    res[r5], r0
.xtaendpoint audio_32_right_o2, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 597
.L387:
          out       res[r5], r4 
          ldw       r4, sp[0xc] 
          ldc       r0, 0x2
          and       r0, r4, r0
          mov       r4, r0
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_right_o3, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 601
.L388:
          out       res[r2], r5 
          or        r0, r10, r4
          ldc       r5, 0x0
          mkmsk     r2, 0x1
          crc32     r0, r5, r2
          mkmsk     r2, 0x1
          and       r0, r0, r2
.L330:
          or        r0, r4, r0
          bitrev    r0, r0
          or        r0, r0, r6
          or        r4, r0, r10
          ldc       r0, 0x8
          sext r0, r0
.L331:
          ashr      r0, r0, 0x8
          mov       r7, r0
          ldc       r6, 0x0
          ldap      r11, .L295
          ldw       r0, sp[0x1] 
          setv      res[r0], r11
.L332:
          shr       r8, r8, 0x4
          mkmsk     r0, 0x4
          and       r0, r8, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r0, sp[0xf] 
          bt        r0, .L333 
.L264:
.L349:
          ldw       r0, r3[0x2] 
          ldw       r11, cp[.LC8]
.L389:
          out       res[r0], r11 
          ldw       r1, r3[0x2] 
          ldc       r0, 0x1c71
.L390:
          outpw     res[r1], r0, 0x10 
          ldw       r1, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r1], r0
.xtaendpoint audio_32_right_o4, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 663
.L391:
          out       res[r1], r5 
          ldaw      r1, dp[data_words_32] 
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldc       r0, 0x8
          sext r0, r0
.L350:
          ashr      r0, r0, 0x8
          mov       r7, r0
          shr       r8, r8, 0x4
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_right_o5, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 668
.L392:
          out       res[r2], r5 
          clre      
          ldw       r0, sp[0x1] 
          eeu       res[r0]
.xtabranch .L295
          setsr     0x1
          clrsr     0x1
.L294:
.L353:
          ldw       r5, r3[0x0] 
          mkmsk     r0, 0x4
          and       r0, r8, r0
          shl       r2, r0, 0x1
          add       r0, r2, 0x1
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          xor       r10, r0, r7
          ldc       r0, 0xc
          setpsc    res[r5], r0
.xtaendpoint audio_32_right_o6, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 703
.L393:
          out       res[r5], r10 
          ld16s     r0, r1[r2] 
          zext      r0, 0x10 
          xor       r5, r0, r7
          ldw       r2, r3[0x0] 
          ldc       r0, 0xc
          setpsc    res[r2], r0
.xtaendpoint audio_32_right_o7, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 706
.L394:
          out       res[r2], r5 
          ldc       r0, 0x8
          sext r0, r0
.L354:
          ashr      r0, r0, 0x8
          mov       r7, r0
          add       r6, r6, 0x1
.L355:
          mkmsk     r0, 0x2
          lss       r0, r6, r0
          bt        r0, .L332 
.L356:
          ldw       r0, sp[0x11] 
          bt        r0, .L305 
.L357:
          ldw       r0, sp[0xf] 
          bt        r0, .L304 
.L358:
          ldw       r4, sp[0x4] 
          ldw       r5, sp[0x5] 
          ldw       r6, sp[0x6] 
          ldw       r7, sp[0x7] 
          ldw       r8, sp[0x8] 
          ldw       r9, sp[0x9] 
          ldw       r10, sp[0xa] 
          retsp     0x17 
.L308:
          clre      
          ldap      r11, .L227
          ldw       r0, sp[0x2] 
          setv      res[r0], r11
          ldw       r0, sp[0x2] 
          eeu       res[r0]
.xtabranch .L227
          setsr     0x1
          clrsr     0x1
.L311:
          ldw       r0, sp[0x18] 
          sub       r0, r4, r0
          stw       r0, dp[count] 
          bu        .L222 
.L227:
.L310:
          ldw       r0, sp[0x2] 
.L395:
          in        r0, res[r0] 
          ldw       r2, sp[0x2] 
          mkmsk     r0, 0x1
.L396:
          out       res[r2], r0 
          bu        .L311 
.L224:
.L241:
.L316:
          ldw       r0, sp[0x1] 
.L397:
          in        r2, res[r0] 
          ldw       r0, sp[0x14] 
          mul       r2, r2, r0
          ashr      r5, r2, 0x8
          ldaw      r2, dp[audiodata] 
          st16      r5, r2[r9] 
          ldw       r2, sp[0x1] 
.L398:
          in        r2, res[r2] 
          mul       r0, r2, r0
          ashr      r5, r0, 0x8
          add       r2, r9, 0x1
          ldaw      r0, dp[audiodata] 
          st16      r5, r0[r2] 
          ldc       r2, 0x100
          ldw       r0, sp[0xe] 
          lss       r2, r2, r0
          ldw       r0, sp[0x10] 
          or        r0, r0, r2
          stw       r0, sp[0x10] 
          ldc       r2, 0x1fe
          ldw       r0, sp[0xe] 
          lss       r0, r2, r0
          bt        r0, .L318 
.L317:
          add       r2, r9, 0x2
          ldc       r0, 0x1ff
          and       r9, r2, r0
          ldw       r0, sp[0xe] 
          add       r0, r0, 0x2
          stw       r0, sp[0xe] 
          bu        .L240 
.L238:
.L323:
          ldc       r6, 0x0
          ldw       r4, sp[0xe] 
          lss       r4, r6, r4
          bf        r4, .L251 
.L324:
          ldw       r4, sp[0xb] 
          add       r6, r4, 0x2
          ldc       r4, 0x1ff
          and       r4, r6, r4
          stw       r4, sp[0xb] 
          ldw       r4, sp[0xe] 
          sub       r4, r4, 0x2
          stw       r4, sp[0xe] 
          bu        .L326 
.L327:
          ldw       r11, cp[.LC0]
          stw       r11, sp[0xc] 
          ldw       r11, cp[.LC1]
          stw       r11, sp[0xd] 
          ldc       r6, 0x2
          bu        .L329 
.L333:
          clre      
          ldap      r11, .L269
          ldw       r0, sp[0x3] 
          setv      res[r0], r11
          ldw       r0, sp[0x3] 
          eeu       res[r0]
.xtabranch .L269
          setsr     0x1
          clrsr     0x1
          bu        .L264 
.L269:
.xtalabel audio_32_freqadjust, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 616
.L335:
          ldw       r0, sp[0x3] 
.L399:
          in        r0, res[r0] 
          ldc       r1, 0x4
          lsu       r1, r1, r0
          bt        r1, .L364 
.xtabranch .L365 + 8,.L365 + 6,.L365 + 4,.L365 + 2,.L365
          bru       r0 
.L365:
.jmptable           .L336, .L340, .L344, .L345, .L346
.L364:
          bu        .L264 
.L336:
          ldw       r0, sp[0x3] 
.L400:
          in        r0, res[r0] 
          bf        r0, .L337 
          eq        r0, r0, 0x1
          bt        r0, .L338 
          bu        .L264 
.L337:
          ldw       r11, cp[.LC6]
          stw       r11, sp[0x18] 
          bu        .L349 
.L338:
          ldw       r11, cp[.LC7]
          stw       r11, sp[0x18] 
          bu        .L349 
.xtalabel audio_32_wipebuf, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 636
.L340:
          ldc       r10, 0x0
.L341:
          ldaw      r1, dp[audiodata] 
          ldc       r0, 0x0
          st16      r0, r1[r10] 
          add       r10, r10, 0x1
.L342:
          ldc       r0, 0x200
          lss       r0, r10, r0
          bt        r0, .L341 
          bu        .L264 
.L344:
          ldc       r0, 0x0
          stw       r0, sp[0xf] 
          bu        .L349 
.L345:
          ldw       r0, sp[0x2] 
          ldw       r1, sp[0x3] 
          stw       r3, sp[0x15] 
          stw       r2, sp[0x16] 
.L401:
          bl        audioWaitForInput 
          ldw       r3, sp[0x15] 
          ldw       r2, sp[0x16] 
          bu        .L349 
.xtalabel audio_32_voladjust, "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test", "i2sOutput_spdifOutput.xc", 650
.L346:
          ldw       r0, sp[0x3] 
.L402:
          in        r0, res[r0] 
          stw       r0, dp[audiovolume] 
          ldw       r0, dp[audiovolume] 
          stw       r0, dp[audiovolume] 
          mkmsk     r1, 0x8
          eq        r0, r0, r1
          bf        r0, .L264 
.L347:
          ldc       r0, 0x100
          stw       r0, dp[audiovolume] 
          bu        .L264 
.L266:
.L295:
.L351:
          ldw       r0, sp[0x1] 
.L403:
          in        r2, res[r0] 
          ldw       r0, dp[audiovolume] 
          mul       r2, r2, r0
          ashr      r5, r2, 0x8
          ldaw      r2, dp[audiodata] 
          st16      r5, r2[r9] 
          ldw       r2, sp[0x1] 
.L404:
          in        r2, res[r2] 
          mul       r0, r2, r0
          ashr      r5, r0, 0x8
          add       r2, r9, 0x1
          ldaw      r0, dp[audiodata] 
          st16      r5, r0[r2] 
          ldc       r2, 0x100
          ldw       r0, sp[0xe] 
          lss       r2, r2, r0
          ldw       r0, sp[0x10] 
          or        r0, r0, r2
          stw       r0, sp[0x10] 
          ldc       r2, 0x1fe
          ldw       r0, sp[0xe] 
          lss       r0, r2, r0
          bt        r0, .L353 
.L352:
          add       r2, r9, 0x2
          ldc       r0, 0x1ff
          and       r9, r2, r0
          ldw       r0, sp[0xe] 
          add       r0, r0, 0x2
          stw       r0, sp[0xe] 
          bu        .L294 
.L292:
.cc_bottom audioManager_32kHz.function
.linkset audioManager_32kHz.maxchanends, audioWaitForInput.maxchanends
.linkset audioManager_32kHz.maxtimers, audioWaitForInput.maxtimers
.linkset .LLNK9, audioWaitForInput.maxthreads - 1
.linkset .LLNK8, 1 + .LLNK9
.linkset .LLNK7, 1 $M .LLNK8
.linkset audioManager_32kHz.maxthreads, .LLNK7
.cc_top audioManager.function,audioManager
          .align    4
.call audioManager, __builtin_set_thread_fast
.globwrite audioManager, audiodata, "i2sOutput_spdifOutput.xc:732: error: previously used here"
.call audioManager, audioManager_48kHz
.call audioManager, audioManager_48kHz
.call audioManager, audioManager_32kHz
.globwrite audioManager, count, 0, 4, "i2sOutput_spdifOutput.xc:772: error: previously used here (bytes 0..4)"
.globread audioManager, count, 0, 4, "i2sOutput_spdifOutput.xc:773: error: previously used here (bytes 0..4)"
.globwrite audioManager, count, 0, 4, "i2sOutput_spdifOutput.xc:783: error: previously used here (bytes 0..4)"
.call audioManager, __builtin_timer_after
.set __builtin_set_thread_fast, 0
.linkset __builtin_set_thread_fast.locnoside, 0
.linkset __builtin_set_thread_fast.locnochandec, 1
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
.globl audioManager, "f{0}(m:chd,m:chd,m:chd,&(s(){m(pSPDIF){bo:p:32},m(p_sdout){bo:p:32},m(p_bclk){bo:p:32},m(p_mclk){p},m(p_lrclk){bo:p:32},m(clk_audio_mclk){ck},m(clk_audio_bclk){ck}}))"
.globl audioManager.nstackwords
.globl audioManager.maxthreads
.globl audioManager.maxtimers
.globl audioManager.maxchanends
.globl audioManager.maxsync
.type  audioManager, @function
.linkset audioManager.locnoside, 0
.linkset audioManager.locnochandec, 1
.linkset .LLNK13, audioManager_48kHz.nstackwords $M audioManager_48kHz.nstackwords
.linkset .LLNK12, .LLNK13 $M audioManager_32kHz.nstackwords
.linkset .LLNK11, .LLNK12 $M .LLNK12
.linkset .LLNK10, .LLNK11 + 4
.linkset audioManager.nstackwords, .LLNK10
audioManager:
          entsp     0x4 
.L453:
          stw       r0, sp[0x2] 
          mov       r5, r1
          stw       r2, sp[0x3] 
          mov       r4, r3
          ldc       r1, 0xbb80
          getr      r7, 0x1
          setsr     0x80
          ldc       r6, 0x0
          ldw       r0, sp[0x2] 
.L483:
          out       res[r0], r6 
          mov       r0, r6
          ldaw      r8, dp[audiodata] 
          ldc       r3, 0x200
.L454:
          st16      r6, r8[r0] 
          add       r0, r0, 0x1
.L455:
          lss       r2, r0, r3
          bt        r2, .L454 
.L456:
          mkmsk     r0, 0x1
.L484:
          out       res[r5], r0 
.L416:
          ldc       r0, 0xac44
          eq        r0, r1, r0
          bt        r0, .L458 
          ldc       r0, 0xbb80
          eq        r0, r1, r0
          bt        r0, .L459 
          ldc       r0, 0x7d00
          eq        r0, r1, r0
          bt        r0, .L460 
.L462:
          bu        .L462 
.L458:
          ldw       r0, sp[0x2] 
          mov       r1, r5
          ldw       r2, sp[0x3] 
          mov       r3, r4
          ldw       r11, cp[.LC6]
          stw       r11, sp[0x1] 
.L485:
          bl        audioManager_48kHz 
.L464:
          ldw       r0, sp[0x3] 
.L486:
          in        r1, res[r0] 
          ldw       r11, cp[.LC3]
          mov       r2, r11
          mov       r3, r6
          ldw       r11, cp[.LC4]
          mov       r0, r11
          ldw       r11, cp[.LC7]
          mov       r8, r11
          ldc       r9, 0x100
.L465:
          ldw       r10, r4[0x1] 
.L487:
          out       res[r10], r6 
          ldw       r10, r4[0x4] 
.L488:
          out       res[r10], r2 
          not       r2, r2
          ldw       r10, r4[0x2] 
.L489:
          out       res[r10], r0 
          ldw       r10, r4[0x2] 
.L490:
          out       res[r10], r0 
          ldw       r10, r4[0x2] 
.L491:
          out       res[r10], r0 
          ldw       r10, r4[0x2] 
.L492:
          out       res[r10], r0 
          ldw       r10, dp[count] 
          ldc       r11, 0xa00
          add       r10, r10, r11
          stw       r10, dp[count] 
          ldw       r10, dp[count] 
          lss       r11, r8, r10
          bt        r11, .L466 
.L433:
.L470:
          add       r3, r3, 0x1
.L471:
          lss       r10, r3, r9
          bt        r10, .L465 
.L472:
          ldw       r0, sp[0x3] 
.L493:
          out       res[r0], r6 
          ldw       r0, r4[0x2] 
.L494:
          outpw     res[r0], r6, 0x1 
          setc      res[r7], 0x1
.L495:
          in        r0, res[r7] 
          mov       r2, r6
          setc      res[r7], 0x9
          clre      
          ldap      r11, .L450
          setv      res[r5], r11
          eeu       res[r5]
.L473:
          ldw       r11, cp[.LC9]
          .section .cp.const4,     "acM", @progbits, 4
.LC9:
          .align    4
          .int      0x000186a0

          .text
          add       r0, r0, r11
          setd      res[r7], r0
.L496:
          in        r0, res[r7] 
.xtabranch .L450
          setsr     0x1
          clrsr     0x1
.L475:
.L446:
          add       r2, r2, 0x1
.L476:
          mkmsk     r3, 0x1
          lss       r3, r2, r3
          bt        r3, .L473 
          bu        .L416 
.L459:
          ldw       r0, sp[0x2] 
          mov       r1, r5
          ldw       r2, sp[0x3] 
          mov       r3, r4
          ldw       r11, cp[.LC7]
          stw       r11, sp[0x1] 
.L497:
          bl        audioManager_48kHz 
          bu        .L464 
.L460:
          ldw       r0, sp[0x2] 
          mov       r1, r5
          ldw       r2, sp[0x3] 
          mov       r3, r4
          ldw       r11, cp[.LC7]
          stw       r11, sp[0x1] 
.L498:
          bl        audioManager_32kHz 
          bu        .L464 
.L466:
          clre      
          ldap      r11, .L438
          setv      res[r5], r11
          eeu       res[r5]
.xtabranch .L438
          setsr     0x1
          clrsr     0x1
.L469:
          sub       r10, r10, r8
          stw       r10, dp[count] 
          bu        .L433 
.L438:
.L468:
.L499:
          in        r11, res[r5] 
          mkmsk     r11, 0x1
.L500:
          out       res[r5], r11 
          bu        .L469 
.L435:
.L450:
.L474:
.L501:
          in        r3, res[r5] 
          mkmsk     r3, 0x1
.L502:
          out       res[r5], r3 
          bu        .L446 
.L447:
.cc_bottom audioManager.function
.linkset .LLNK15, audioManager_32kHz.maxchanends $M audioManager_48kHz.maxchanends
.linkset .LLNK14, .LLNK15 $M audioManager_48kHz.maxchanends
.linkset audioManager.maxchanends, .LLNK14
.linkset .LLNK19, 1 + audioManager_32kHz.maxtimers
.linkset .LLNK18, 1 $M .LLNK19
.linkset .LLNK20, 1 + audioManager_48kHz.maxtimers
.linkset .LLNK17, .LLNK18 $M .LLNK20
.linkset .LLNK21, 1 + audioManager_48kHz.maxtimers
.linkset .LLNK16, .LLNK17 $M .LLNK21
.linkset audioManager.maxtimers, .LLNK16
.linkset .LLNK26, audioManager_32kHz.maxthreads - 1
.linkset .LLNK25, 1 + .LLNK26
.linkset .LLNK24, 1 $M .LLNK25
.linkset .LLNK28, audioManager_48kHz.maxthreads - 1
.linkset .LLNK27, 1 + .LLNK28
.linkset .LLNK23, .LLNK24 $M .LLNK27
.linkset .LLNK30, audioManager_48kHz.maxthreads - 1
.linkset .LLNK29, 1 + .LLNK30
.linkset .LLNK22, .LLNK23 $M .LLNK29
.linkset audioManager.maxthreads, .LLNK22
# Thread names for recovering thread graph in linker
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
.LC10:
          .int      6
          .set      preamble_words_32.locbound,.LC10
          .align    4
.LC11:
          .int      32
          .set      data_words_32.locbound,.LC11
.globl audiodata, "a(512:ss)"
.type  audiodata, @object
          .section .dp.bss,        "adw", @nobits
.cc_top audiodata.data, audiodata
          .align    4
audiodata:
.globl audiodata.globound
          .set      audiodata.globound,512
          .space    1024
.cc_bottom audiodata.data
          .align    4
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
.LC12:
          .int      512
          .set      audiodata.locbound,.LC12
          .align    4
.LC13:
          .int      6
          .set      preamble_words_48.locbound,.LC13
          .align    4
.LC14:
          .int      32
          .set      data_words_48.locbound,.LC14
.extern __builtin_getid, "f{si}(0)"
          .section .xtacommand,       "", @progbits
.L503:
          .int      .L504-.L503
          .int      0x00000000
          .asciiz   "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test"
          .asciiz  "add exclusion audio_48_exit"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  813
          .asciiz  "add exclusion audio_48_waitforinput"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  814
          .asciiz  "add exclusion audio_48_wipebuf"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  815
          .asciiz  "add exclusion audio_48_freqadjust"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  816
          .asciiz  "config freq 500"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  817
          .asciiz  "analyse path audio_48_left_o0 audio_48_left_o2"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  818
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  819
          .asciiz  "analyse path audio_48_left_o1 audio_48_left_o3"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  820
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  821
          .asciiz  "analyse path audio_48_left_o2 audio_48_left_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  822
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  823
          .asciiz  "analyse path audio_48_left_o3 audio_48_left_o5"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  824
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  825
          .asciiz  "analyse path audio_48_left_o4 audio_48_left_o6"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  826
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  827
          .asciiz  "analyse path audio_48_left_o5 audio_48_left_o7"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  828
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  829
          .asciiz  "add exclusion audio_48_right_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  830
          .asciiz  "analyse path audio_48_left_o6 audio_48_left_o4  "
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  831
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  832
          .asciiz  "remove exclusion audio_48_right_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  833
          .asciiz  "add exclusion audio_48_left_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  834
          .asciiz  "analyse path audio_48_left_o6 audio_48_right_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  835
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  836
          .asciiz  "analyse path audio_48_left_o7 audio_48_right_o1"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  837
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  838
          .asciiz  "remove exclusion audio_48_left_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  839
          .asciiz  "analyse path audio_48_right_o0 audio_48_right_o2"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  840
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  841
          .asciiz  "analyse path audio_48_right_o1 audio_48_right_o3"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  842
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  843
          .asciiz  "analyse path audio_48_right_o2 audio_48_right_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  844
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  845
          .asciiz  "analyse path audio_48_right_o3 audio_48_right_o5"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  846
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  847
          .asciiz  "analyse path audio_48_right_o4 audio_48_right_o6"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  848
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  849
          .asciiz  "analyse path audio_48_right_o5 audio_48_right_o7"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  850
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  851
          .asciiz  "add exclusion audio_48_left_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  852
          .asciiz  "analyse path audio_48_right_o6 audio_48_right_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  853
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  854
          .asciiz  "analyse path audio_48_right_o7 audio_48_right_o5"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  855
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  856
          .asciiz  "remove exclusion audio_48_left_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  857
          .asciiz  "add exclusion audio_48_right_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  858
          .asciiz  "analyse path audio_48_right_o6 audio_48_left_o0"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  859
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  860
          .asciiz  "analyse path audio_48_right_o7 audio_48_left_o1"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  861
          .asciiz  "set required - 1300 ns"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  862
          .asciiz  "remove exclusion audio_48_right_o4"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  863
          .asciiz  "remove exclusion audio_48_wipebuf"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  864
          .asciiz  "remove exclusion audio_48_freqadjust"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  865
          .asciiz  "remove exclusion audio_48_exit"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  866
          .asciiz  "remove exclusion audio_48_waitforinput"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  867
          .asciiz  "print summary"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  868
          .asciiz  "exit"
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int  869
.L504:
          .section .xtaendpointtable,       "", @progbits
.L505:
          .int      .L506-.L505
          .int      0x00000000
          .asciiz   "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test"
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000320
          .long    .L502
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031f
          .long    .L501
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000030a
          .long    .L500
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000309
          .long    .L499
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031c
          .long    .L496
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000319
          .long    .L495
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000318
          .long    .L494
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000315
          .long    .L493
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000302
          .long    .L492
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000301
          .long    .L491
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000300
          .long    .L490
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002ff
          .long    .L489
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002fc
          .long    .L488
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002fa
          .long    .L487
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002f3
          .long    .L486
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002de
          .long    .L484
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002d8
          .long    .L483
.cc_bottom audioManager.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002ab
          .long    .L404
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002a6
          .long    .L403
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000028b
          .long    .L402
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000026e
          .long    .L400
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000267
          .long    .L399
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000020a
          .long    .L398
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000205
          .long    .L397
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001e7
          .long    .L396
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001e6
          .long    .L395
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002c3
          .long    .L394
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002c0
          .long    .L393
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000029d
          .long    .L392
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000298
          .long    .L391
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000296
          .long    .L390
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000295
          .long    .L389
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000025a
          .long    .L388
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000256
          .long    .L387
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000246
          .long    .L386
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000234
          .long    .L385
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000233
          .long    .L384
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000232
          .long    .L383
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000022c
          .long    .L382
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000022a
          .long    .L381
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000223
          .long    .L380
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000220
          .long    .L379
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001fc
          .long    .L378
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f7
          .long    .L377
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f6
          .long    .L376
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f5
          .long    .L375
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001df
          .long    .L374
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001db
          .long    .L373
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001d3
          .long    .L372
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001d0
          .long    .L371
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001ce
          .long    .L370
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001cd
          .long    .L369
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001c7
          .long    .L368
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001c5
          .long    .L367
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000168
          .long    .L208
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000163
          .long    .L207
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000149
          .long    .L206
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000012b
          .long    .L204
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000124
          .long    .L203
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000c6
          .long    .L202
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000c1
          .long    .L201
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000a4
          .long    .L200
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000a3
          .long    .L199
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000180
          .long    .L198
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000017d
          .long    .L197
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000015a
          .long    .L196
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000155
          .long    .L195
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000153
          .long    .L194
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000117
          .long    .L193
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000113
          .long    .L192
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000103
          .long    .L191
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ef
          .long    .L190
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ee
          .long    .L189
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000e8
          .long    .L188
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000e6
          .long    .L187
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000df
          .long    .L186
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000dc
          .long    .L185
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000b8
          .long    .L184
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000b3
          .long    .L183
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000b2
          .long    .L182
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000009c
          .long    .L181
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000098
          .long    .L180
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000090
          .long    .L179
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000008d
          .long    .L178
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000008c
          .long    .L177
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000086
          .long    .L176
.cc_bottom audioManager_48kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000084
          .long    .L175
.cc_bottom audioManager_48kHz.function
.cc_top checkForData.function, checkForData
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000001d
          .long    .L14
.cc_bottom checkForData.function
.cc_top checkForData.function, checkForData
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000018
          .long    .L13
.cc_bottom checkForData.function
.L506:
          .section .xtacalltable,       "", @progbits
.L507:
          .int      .L508-.L507
          .int      0x00000000
          .asciiz   "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test"
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002eb
          .long    .L498
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002e8
          .long    .L497
.cc_bottom audioManager.function
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002e5
          .long    .L485
.cc_bottom audioManager.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000287
          .long    .L401
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000145
          .long    .L205
.cc_bottom audioManager_48kHz.function
.L508:
          .section .xtalabeltable,       "", @progbits
.L509:
          .int      .L510-.L509
          .int      0x00000000
          .asciiz   "C:\\Dev\\SandboxRevA\\apps_ipod\\src\\libs\\audioManager\\test"
.cc_top audioManager.function, audioManager
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031a
          .int      0x0000031a
          .long    .L476
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031a
          .int      0x0000031a
          .long    .L475
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031f
          .int      0x00000320
          .long    .L474
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000031c
          .int      0x0000031d
          .long    .L473
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000315
          .int      0x0000031a
          .long    .L472
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002f8
          .int      0x000002f8
          .long    .L471
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002f8
          .int      0x000002f8
          .long    .L470
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000030f
          .int      0x0000030f
          .long    .L469
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000309
          .int      0x0000030a
          .long    .L468
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000307
          .int      0x00000307
          .long    .L466
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002fa
          .int      0x00000305
          .long    .L465
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002f3
          .int      0x000002f8
          .long    .L464
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002f0
          .int      0x000002f0
          .long    .L462
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002eb
          .int      0x000002ec
          .long    .L460
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002e8
          .int      0x000002e9
          .long    .L459
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002e5
          .int      0x000002e6
          .long    .L458
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002de
          .int      0x00000328
          .long    .L456
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002db
          .int      0x000002db
          .long    .L455
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002dc
          .int      0x000002db
          .long    .L454
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002d0
          .int      0x000002db
          .long    .L453
.cc_bottom audioManager.function
.cc_top audioManager_32kHz.function, audioManager_32kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002c9
          .int      0x000002c9
          .long    .L358
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001bc
          .int      0x000001bc
          .long    .L357
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001c0
          .int      0x000001c0
          .long    .L356
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000025f
          .int      0x0000025f
          .long    .L355
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000025f
          .int      0x0000025f
          .long    .L354
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002c0
          .int      0x00000034
          .long    .L353
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002b6
          .int      0x000002b6
          .long    .L352
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000002a6
          .int      0x000002ae
          .long    .L351
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000029b
          .int      0x000002a3
          .long    .L350
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000295
          .int      0x00000034
          .long    .L349
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000028d
          .int      0x0000028d
          .long    .L347
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000028b
          .int      0x0000028c
          .long    .L346
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000287
          .int      0x00000288
          .long    .L345
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000283
          .int      0x00000285
          .long    .L344
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000027d
          .int      0x0000027d
          .long    .L342
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000027e
          .int      0x0000027d
          .long    .L341
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000027d
          .int      0x0000027d
          .long    .L340
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000275
          .int      0x00000276
          .long    .L338
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000272
          .int      0x00000273
          .long    .L337
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000026e
          .int      0x0000026f
          .long    .L336
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000267
          .int      0x00000269
          .long    .L335
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000265
          .int      0x00000265
          .long    .L333
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000261
          .int      0x00000263
          .long    .L332
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000025f
          .int      0x0000025f
          .long    .L331
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000025c
          .int      0x00000034
          .long    .L330
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000253
          .int      0x0000003b
          .long    .L329
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000250
          .int      0x00000251
          .long    .L328
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000024a
          .int      0x0000024c
          .long    .L327
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000246
          .int      0x00000248
          .long    .L326
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000243
          .int      0x00000243
          .long    .L325
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000239
          .int      0x00000239
          .long    .L324
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000237
          .int      0x00000237
          .long    .L323
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000237
          .int      0x00000237
          .long    .L322
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000229
          .int      0x00000034
          .long    .L321
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f0
          .int      0x000001f0
          .long    .L320
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f0
          .int      0x000001f0
          .long    .L319
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000021e
          .int      0x00000034
          .long    .L318
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000215
          .int      0x00000215
          .long    .L317
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000205
          .int      0x0000020d
          .long    .L316
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001fa
          .int      0x00000202
          .long    .L315
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f2
          .int      0x00000034
          .long    .L314
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001f0
          .int      0x000001f0
          .long    .L313
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001ee
          .int      0x00000034
          .long    .L312
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001ec
          .int      0x000001ec
          .long    .L311
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001e6
          .int      0x000001e7
          .long    .L310
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001e4
          .int      0x000001e4
          .long    .L308
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001dd
          .int      0x000001e2
          .long    .L307
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001d5
          .int      0x0000003b
          .long    .L306
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001c4
          .int      0x00000034
          .long    .L305
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001be
          .int      0x000002c6
          .long    .L304
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001ba
          .int      0x000002c7
          .long    .L303
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000001a5
          .int      0x0000003b
          .long    .L302
.cc_bottom audioManager_32kHz.function
.cc_top audioManager_48kHz.function, audioManager_48kHz
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000186
          .int      0x00000186
          .long    .L166
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L165
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000007f
          .int      0x0000007f
          .long    .L164
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000011c
          .int      0x0000011c
          .long    .L163
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000011c
          .int      0x0000011c
          .long    .L162
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000017d
          .int      0x00000034
          .long    .L161
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000173
          .int      0x00000173
          .long    .L160
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000163
          .int      0x0000016b
          .long    .L159
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000158
          .int      0x00000160
          .long    .L158
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000153
          .int      0x00000034
          .long    .L157
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000014b
          .int      0x0000014b
          .long    .L155
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000149
          .int      0x0000014a
          .long    .L154
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000145
          .int      0x00000146
          .long    .L153
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000140
          .int      0x00000142
          .long    .L152
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000013a
          .int      0x0000013a
          .long    .L150
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000013b
          .int      0x0000013a
          .long    .L149
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000013a
          .int      0x0000013a
          .long    .L148
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000132
          .int      0x00000133
          .long    .L146
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000012f
          .int      0x00000130
          .long    .L145
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000012b
          .int      0x0000012c
          .long    .L144
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000124
          .int      0x00000126
          .long    .L143
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000122
          .int      0x00000122
          .long    .L141
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000011e
          .int      0x00000120
          .long    .L140
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000011c
          .int      0x0000011c
          .long    .L139
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000119
          .int      0x00000034
          .long    .L138
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000110
          .int      0x0000003b
          .long    .L137
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000010d
          .int      0x0000010e
          .long    .L136
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000107
          .int      0x00000109
          .long    .L135
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000103
          .int      0x00000105
          .long    .L134
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000100
          .int      0x00000100
          .long    .L133
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000f5
          .int      0x000000f5
          .long    .L132
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000f3
          .int      0x000000f3
          .long    .L131
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000f2
          .int      0x000000f3
          .long    .L130
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000e5
          .int      0x00000034
          .long    .L129
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ad
          .int      0x000000ad
          .long    .L128
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ad
          .int      0x000000ad
          .long    .L127
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000da
          .int      0x00000034
          .long    .L126
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000d1
          .int      0x000000d1
          .long    .L125
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000c1
          .int      0x000000c9
          .long    .L124
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000b6
          .int      0x000000be
          .long    .L123
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000af
          .int      0x00000034
          .long    .L122
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ad
          .int      0x000000ad
          .long    .L121
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000ab
          .int      0x00000034
          .long    .L120
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000a9
          .int      0x000000a9
          .long    .L119
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000a3
          .int      0x000000a4
          .long    .L118
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x000000a1
          .int      0x000000a1
          .long    .L116
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000009a
          .int      0x0000009f
          .long    .L115
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000092
          .int      0x0000003b
          .long    .L114
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000083
          .int      0x00000034
          .long    .L113
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000007d
          .int      0x00000183
          .long    .L112
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000079
          .int      0x00000184
          .long    .L111
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000064
          .int      0x0000003b
          .long    .L110
.cc_bottom audioManager_48kHz.function
.cc_top parity32.function, parity32
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000039
          .int      0x0000003b
          .long    .L16
.cc_bottom parity32.function
.cc_top mysext.function, mysext
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000032
          .int      0x00000034
          .long    .L15
.cc_bottom mysext.function
.cc_top checkForData.function, checkForData
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x0000002f
          .int      0x0000002f
          .long    .L12
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L11
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000018
          .int      0x00000020
          .long    .L10
          .asciiz  "i2sOutput_spdifOutput.xc"
          .int      0x00000013
          .int      0x00000015
          .long    .L9
.cc_bottom checkForData.function
.L510:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler '20091110' (build 1495)"
          .core     "XS1"
          .corerev  "REVB"
