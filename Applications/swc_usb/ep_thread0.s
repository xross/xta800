          .file     "./tests/audio/src/ep_thread0.xc"
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
.extern safestrcpy, "f{0}(&(a(:uc)),&(a(:c:uc)))"
.extern safestrncpy, "f{0}(&(a(:uc)),&(a(:c:uc)),ui)"
.extern safestrcat, "f{0}(&(a(:uc)),&(a(:c:uc)))"
.extern safestrncat, "f{0}(&(a(:uc)),&(a(:c:uc)),ui)"
.extern safestrcmp, "f{si}(&(a(:c:uc)),&(a(:c:uc)))"
.extern safestrncmp, "f{si}(&(a(:c:uc)),&(a(:c:uc)),ui)"
.extern safestrlen, "f{si}(&(a(:c:uc)))"
.extern safestrchr, "f{si}(&(a(:c:uc)),si)"
.extern safestrrchr, "f{si}(&(a(:c:uc)),si)"
.extern safestrspn, "f{ui}(&(a(:c:uc)),&(a(:c:uc)))"
.extern safestrcspn, "f{ui}(&(a(:c:uc)),&(a(:c:uc)))"
.extern safestrpbrk, "f{si}(&(a(:c:uc)),&(a(:c:uc)))"
.extern safestrstr, "f{si}(&(a(:c:uc)),&(a(:c:uc)))"
.extern safememcpy, "f{0}(&(a(:uc)),&(a(:c:uc)),ui)"
.extern safememset, "f{0}(&(a(:uc)),si,ui)"
.extern safememcmp, "f{si}(&(a(:c:uc)),&(a(:c:uc)),ui)"
.extern safememchr, "f{si}(&(a(:c:uc)),si,ui)"
.extern XUD_Sup_GetLock, "f{ui}(0)"
.extern XUD_Sup_ClaimLock, "f{0}(ui)"
.extern XUD_Sup_FreeLock, "f{0}(ui)"
.extern XUD_Sup_FreerLock, "f{0}(ui)"
.extern XUD_Sup_Delay, "f{0}(ui)"
.extern XUD_Sup_EnableThreadInterrupts, "f{0}(0)"
.extern XUD_Sup_DisableThreadInterrupts, "f{0}(0)"
.extern XUD_Sup_Outpw8, "f{0}(o:p,ui)"
.extern XUD_Sup_Outpw16, "f{0}(o:p,ui)"
.extern XUD_Sup_Outpw24, "f{0}(o:p,ui)"
.extern XUD_Sup_GetResourceId, "f{ui}(chd)"
.extern XUD_Sup_Inpw24, "f{ui}(i:p)"
.extern XUD_Sup_Inpw8, "f{ui}(i:p)"
.extern XUD_Sup_inuint, "f{ui}(ui)"
.extern XUD_Sup_inct, "f{uc}(ui)"
.extern XUD_Sup_outuint, "f{0}(ui,ui)"
.extern XUD_Sup_outct, "f{0}(ui,uc)"
.extern XUD_Sup_stop_streaming_master, "f{0}(ui)"
.extern GetArrayAddress, "f{ui}(&(a(:uc)))"
.extern XUD_Manager, "f{si}(chd,chd,chd,chd,chd,o:p,o:p)"
.extern XUD_ParseSetupPacket, "f{0}(&(a(:uc)),&(s(setupPacket){m(bmRequestType){s(bmRequestType){m(Recipient){uc},m(Type){uc},m(Direction){uc}}},m(bRequest){uc},m(wValue){us},m(wIndex){us},m(wLength){us}}))"
.extern XUD_PrintSetupPacket, "f{0}(s(setupPacket){m(bmRequestType){s(bmRequestType){m(Recipient){uc},m(Type){uc},m(Direction){uc}}},m(bRequest){uc},m(wValue){us},m(wIndex){us},m(wLength){us}})"
.extern XUD_DoEnumReqs, "f{si}(chd,&(a(:uc)),si,&(a(:uc)),si,&(a(:a(40:uc))),&(s(setupPacket){m(bmRequestType){s(bmRequestType){m(Recipient){uc},m(Type){uc},m(Direction){uc}}},m(bRequest){uc},m(wValue){us},m(wIndex){us},m(wLength){us}}))"
.extern XUD_Error, "f{0}(&(a(:uc)))"
.extern XUD_Error_hex, "f{0}(&(a(:uc)),si)"
.extern XUD_GetSetupBuffer, "f{si}(chd,chd,ui,&(a(:uc)))"
.extern XUD_GetSetupBuffer_, "f{si}(chd,ui,&(a(:uc)))"
.extern XUD_GetBuffer, "f{si}(chd,chd,ui,&(a(:uc)))"
.extern XUD_GetBuffer_, "f{si}(chd,ui,&(a(:uc)))"
.extern XUD_GetBuffer_int, "f{si}(chd,ui,&(a(:si)))"
.extern XUD_SetBuffer, "f{si}(chd,chd,ui,&(a(:uc)),ui)"
.extern XUD_SetBuffer_, "f{si}(chd,ui,&(a(:uc)),ui)"
.extern XUD_SetBuffer_ResetPid, "f{si}(chd,chd,ui,&(a(:uc)),ui,uc)"
.extern XUD_SetBuffer_ResetPid_, "f{si}(chd,ui,&(a(:uc)),ui,uc)"
.extern XUD_SetBuffer_ResetPid_EpMax, "f{si}(chd,ui,&(a(:uc)),ui,ui,uc)"
.extern XUD_DoGetRequest, "f{si}(chd,ui,&(a(:uc)),ui)"
.extern XUD_DoSetRequestStatus, "f{si}(chd,ui)"
.extern XUD_SetDevAddr, "f{0}(chd,ui)"
.extern XUD_UIFM_PortConfig, "f{0}(i:p,o:p,i:p,i:p,i:p,i:p,o:p,i:p)"
.extern XUD_UIFM_Enable, "f{ui}(ui)"
.extern XUD_UIFM_RegWrite, "f{0}(o:p,ui,ui)"
.extern XUD_UIFM_RegRead, "f{ui}(o:p,i:p,ui)"
.extern XUD_UIFM_RegWrite_Locked, "f{0}(o:p,ui,ui,ui)"
.extern XUD_UIFM_RegRead_Locked, "f{ui}(o:p,i:p,ui,ui)"
.extern WriteEpBuffToPktBuff, "f{0}(o:p,ui,ui)"
.extern ReadPktBuffToEpBuff, "f{ui}(o:p,i:p,ui,ui)"
.extern ReadSetupBuffToEpBuff, "f{ui}(o:p,i:p,ui)"
.globl params, "a(28:ui)"
.type  params, @object
          .section .dp.data,       "adw", @progbits
.cc_top params.data, params
          .align    4
params:
.globl params.globound
          .set      params.globound,28
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00002000
          .int      0x00017700
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
          .int      0x00000000
.cc_bottom params.data
          .align    4
          .text
.LDBG4:
.globread updateMasterVol, params, "./tests/audio/src/ep_thread0.xc:25: error: previously used here"
.globread updateMasterVol, params, "./tests/audio/src/ep_thread0.xc:25: error: previously used here"
.globread updateMasterVol, params, "./tests/audio/src/ep_thread0.xc:29: error: previously used here"
.globread updateMasterVol, params, "./tests/audio/src/ep_thread0.xc:29: error: previously used here"
.globwrite updateMasterVol, params, "./tests/audio/src/ep_thread0.xc:29: error: previously used here"
.globl updateMasterVol, "f{0}(0)"
.globl updateMasterVol.nstackwords
.globl updateMasterVol.maxthreads
.globl updateMasterVol.maxtimers
.globl updateMasterVol.maxchanends
.globl updateMasterVol.maxsync
.type  updateMasterVol, @function
.linkset updateMasterVol.locnoside, 0
.linkset updateMasterVol.locnochandec, 1
.linkset updateMasterVol.nstackwords, 2
.cc_top updateMasterVol.function,updateMasterVol
.LDBG16:
.LDBG17:                             # line (24)
          .align    4
updateMasterVol:
          extsp     0x2 
.LDBG18:
.L6:
          stw       r4, sp[0x0] 
.LDBG19:
          stw       r5, sp[0x1] 
.LDBG20:
.LDBG21:                             # line (24)
          ldaw      r2, dp[params] 
.LDBG7:
.LDBG22:                             # line (25)
          ldw       r1, r2[0x0] 
          ldw       r0, r2[0xa] 
          eq        r0, r0, 0x0
          mul       r4, r1, r0
.LDBG9:
.LDBG5:
.LDBG23:                             # line (27)
          ldc       r3, 0x1
.LDBG10:
.LDBG24:                             # line (27)
.LDBG11:
.LDBG12:
.L3:
.L7:
.LDBG25:                             # line (29)
          ldc       r1, 0x1c
          lsu       r0, r3, r1
          ecallf    r0
          ldw       r0, r2[r3] 
          mul       r5, r4, r0
          add       r0, r3, 0xa
          lsu       r11, r0, r1
          ecallf    r11
          ldw       r0, r2[r0] 
          eq        r0, r0, 0x0
          mul       r0, r5, r0
          shr       r5, r0, 0x8
          ldc       r0, 0x12
          add       r0, r3, r0
          lsu       r1, r0, r1
          ecallf    r1
          stw       r5, r2[r0] 
.LDBG26:                             # line (27)
          add       r3, r3, 0x1
.L8:
.LDBG27:                             # line (27)
          ldc       r0, 0x9
          lss       r0, r3, r0
          bt        r0, .L3 
.LDBG13:
.LDBG8:
.LDBG6:
.L9:
.LDBG28:                             # line (31)
.LDBG29:
          ldw       r4, sp[0x0] 
.LDBG30:
          ldw       r5, sp[0x1] 
.LDBG31:
          ldaw      sp, sp[0x2] 
          retsp     0x0 
.LDBG14:
.LDBG32:
.LDBG15:
.cc_bottom updateMasterVol.function
.LDBG33:
          .section  .debug_frame, "",     @progbits
.cc_top updateMasterVol.function,updateMasterVol
          .align    4
          .int      .LDBG35-.LDBG34
.LDBG34:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG16
          .int      .LDBG33-.LDBG16
          .byte     0x01
          .int      .LDBG18
          .byte     0x0e
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG19
          .byte     0x84
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG20
          .byte     0x85
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG29
          .byte     0x0a
          .byte     0x01
          .int      .LDBG30
          .byte     0xc4
          .byte     0x01
          .int      .LDBG31
          .byte     0xc5
          .byte     0x01
          .int      .LDBG32
          .byte     0x0b
          .align    4, 0
.LDBG35:
.cc_bottom updateMasterVol.function
.linkset updateMasterVol.maxchanends, 0
.linkset updateMasterVol.maxtimers, 0
.linkset updateMasterVol.maxthreads, 1
          .text
.LDBG36:
.LDBG37:
.call updateVol, updateMasterVol
.globread updateVol, params, "./tests/audio/src/ep_thread0.xc:39: error: previously used here"
.globread updateVol, params, "./tests/audio/src/ep_thread0.xc:39: error: previously used here"
.globread updateVol, params, "./tests/audio/src/ep_thread0.xc:39: error: previously used here"
.globread updateVol, params, "./tests/audio/src/ep_thread0.xc:39: error: previously used here"
.globwrite updateVol, params, "./tests/audio/src/ep_thread0.xc:39: error: previously used here"
.globl updateVol, "f{0}(si)"
.globl updateVol.nstackwords
.globl updateVol.maxthreads
.globl updateVol.maxtimers
.globl updateVol.maxchanends
.globl updateVol.maxsync
.type  updateVol, @function
.linkset updateVol.locnoside, 0
.linkset updateVol.locnochandec, 1
.linkset .LLNK1, updateMasterVol.nstackwords $M updateMasterVol.nstackwords
.linkset .LLNK0, .LLNK1 + 2
.linkset updateVol.nstackwords, .LLNK0
.cc_top updateVol.function,updateVol
.LDBG45:
.LDBG46:                             # line (34)
          .align    4
updateVol:
          entsp     0x2 
.LDBG47:
.L13:
          stw       r4, sp[0x1] 
.LDBG48:
.LDBG38:
.LDBG49:                             # line (34)
.LDBG50:                             # line (35)
          bf        r0, .L11 
.LDBG39:
.LDBG40:
.L15:
          ldaw      r2, dp[params] 
.LDBG51:                             # line (39)
          ldw       r3, r2[0x0] 
          ldw       r1, r2[0xa] 
          eq        r1, r1, 0x0
          mul       r4, r3, r1
          ldc       r1, 0x1c
          lsu       r3, r0, r1
          ecallf    r3
          ldw       r3, r2[r0] 
          mul       r4, r4, r3
          add       r3, r0, 0xa
          lsu       r11, r3, r1
          ecallf    r11
          ldw       r3, r2[r3] 
          eq        r3, r3, 0x0
          mul       r3, r4, r3
          shr       r3, r3, 0x8
          ldc       r4, 0x12
          add       r0, r0, r4
          lsu       r1, r0, r1
          ecallf    r1
          stw       r3, r2[r0] 
.LDBG41:
.L10:
.L16:
.LDBG52:                             # line (40)
.LDBG53:
          ldw       r4, sp[0x1] 
.LDBG54:
          retsp     0x2 
.LDBG42:
.LDBG55:
.LDBG43:
.L11:
.L14:
.LDBG56:                             # line (36)
          bl        updateMasterVol 
          bu        .L10 
.LDBG44:
.cc_bottom updateVol.function
.LDBG57:
          .section  .debug_frame, "",     @progbits
.cc_top updateVol.function,updateVol
          .align    4
          .int      .LDBG59-.LDBG58
.LDBG58:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG45
          .int      .LDBG57-.LDBG45
          .byte     0x01
          .int      .LDBG47
          .byte     0x0e
          .uleb128  0x8
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG48
          .byte     0x84
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG53
          .byte     0x0a
          .byte     0x01
          .int      .LDBG54
          .byte     0xc4
          .byte     0x01
          .int      .LDBG55
          .byte     0x0b
          .align    4, 0
.LDBG59:
.cc_bottom updateVol.function
.linkset updateVol.maxchanends, updateMasterVol.maxchanends
.linkset updateVol.maxtimers, updateMasterVol.maxtimers
.linkset .LLNK4, updateMasterVol.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset updateVol.maxthreads, .LLNK2
          .text
.LDBG60:
.locl devDesc, "a(18:uc)"
          .section .dp.data,       "adw", @progbits
.cc_top devDesc.data, devDesc
          .align    4
devDesc:
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x02
          .byte     0xef
          .byte     0x02
          .byte     0x01
          .byte     0x40
          .byte     0x00
          .byte     0x00
          .byte     0x30
          .byte     0x70
          .byte     0x10
          .byte     0x00
          .byte     0x01
          .byte     0x02
          .byte     0x00
          .byte     0x01
.cc_bottom devDesc.data
.locl cfgDesc, "a(194:uc)"
.cc_top cfgDesc.data, cfgDesc
          .align    4
cfgDesc:
          .byte     0x09
          .byte     0x02
          .byte     0xc2
          .byte     0x00
          .byte     0x04
          .byte     0x01
          .byte     0x00
          .byte     0xc0
          .byte     0x32
          .byte     0x08
          .byte     0x0b
          .byte     0x00
          .byte     0x02
          .byte     0x01
          .byte     0x00
          .byte     0x20
          .byte     0x00
          .byte     0x09
          .byte     0x04
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x01
          .byte     0x01
          .byte     0x20
          .byte     0x04
          .byte     0x09
          .byte     0x24
          .byte     0x01
          .byte     0x00
          .byte     0x02
          .byte     0x01
          .byte     0x58
          .byte     0x00
          .byte     0x00
          .byte     0x08
          .byte     0x24
          .byte     0x0a
          .byte     0x03
          .byte     0x03
          .byte     0x03
          .byte     0x00
          .byte     0x06
          .byte     0x11
          .byte     0x24
          .byte     0x02
          .byte     0x06
          .byte     0x01
          .byte     0x01
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0xff
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x07
          .byte     0x0c
          .byte     0x24
          .byte     0x03
          .byte     0x07
          .byte     0x01
          .byte     0x03
          .byte     0x00
          .byte     0x0a
          .byte     0x03
          .byte     0x00
          .byte     0x00
          .byte     0x08
          .byte     0x2a
          .byte     0x24
          .byte     0x06
          .byte     0x0a
          .byte     0x06
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x0f
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x05
          .byte     0x09
          .byte     0x04
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x01
          .byte     0x02
          .byte     0x20
          .byte     0x03
          .byte     0x09
          .byte     0x04
          .byte     0x01
          .byte     0x01
          .byte     0x01
          .byte     0x01
          .byte     0x02
          .byte     0x20
          .byte     0x03
          .byte     0x10
          .byte     0x24
          .byte     0x01
          .byte     0x06
          .byte     0x00
          .byte     0x01
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x08
          .byte     0xff
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x06
          .byte     0x24
          .byte     0x02
          .byte     0x01
          .byte     0x04
          .byte     0x20
          .byte     0x07
          .byte     0x05
          .byte     0x01
          .byte     0x0d
          .byte     0xc8
          .byte     0x03
          .byte     0x01
          .byte     0x08
          .byte     0x25
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .byte     0x02
          .byte     0x05
          .byte     0x00
          .byte     0x09
          .byte     0x04
          .byte     0x03
          .byte     0x00
          .byte     0x01
          .byte     0x03
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x09
          .byte     0x21
          .byte     0x00
          .byte     0x01
          .byte     0x00
          .byte     0x01
          .byte     0x22
          .byte     0x32
          .byte     0x00
          .byte     0x07
          .byte     0x05
          .byte     0x82
          .byte     0x03
          .byte     0x04
          .byte     0x00
          .byte     0x08
.cc_bottom cfgDesc.data
.locl strDesc_langIDs, "a(5:uc)"
.cc_top strDesc_langIDs.data, strDesc_langIDs
          .align    4
strDesc_langIDs:
          .byte     0x04
          .byte     0x03
          .byte     0x09
          .byte     0x04
          .byte     0x00
.cc_bottom strDesc_langIDs.data
.locl strDescs, "a(9:a(40:uc))"
.cc_top strDescs.data, strDescs
          .align    4
strDescs:
          .ascii    "string0\0"
          .space    32
          .ascii    "XMOS\0"
          .space    35
          .ascii    "XMOS USB Audio\0"
          .space    25
          .ascii    "XMOS USB 2.0 Audio Stream OUT\0"
          .space    10
          .ascii    "XMOS USB 2.0 Audio Control\0"
          .space    13
          .ascii    "XMOS Feature Unit\0"
          .space    22
          .ascii    "XMOS Clock\0"
          .space    29
          .ascii    "Input term\0"
          .space    29
          .ascii    "Output term\0"
          .space    28
.cc_bottom strDescs.data
.locl hidReportDesc, "a(50:uc)"
.cc_top hidReportDesc.data, hidReportDesc
          .align    4
hidReportDesc:
          .byte     0x05
          .byte     0x0c
          .byte     0x09
          .byte     0x01
          .byte     0xa1
          .byte     0x01
          .byte     0x15
          .byte     0x00
          .byte     0x25
          .byte     0x01
          .byte     0x09
          .byte     0xe9
          .byte     0x09
          .byte     0xea
          .byte     0x75
          .byte     0x01
          .byte     0x95
          .byte     0x02
          .byte     0x81
          .byte     0x02
          .byte     0x09
          .byte     0xe2
          .byte     0x09
          .byte     0x00
          .byte     0x81
          .byte     0x06
          .byte     0x09
          .byte     0x00
          .byte     0x95
          .byte     0x04
          .byte     0x81
          .byte     0x02
          .byte     0x26
          .byte     0xff
          .byte     0x00
          .byte     0x09
          .byte     0x00
          .byte     0x75
          .byte     0x08
          .byte     0x95
          .byte     0x03
          .byte     0x81
          .byte     0x02
          .byte     0x09
          .byte     0x00
          .byte     0x95
          .byte     0x04
          .byte     0x91
          .byte     0x02
          .byte     0xc0
.cc_bottom hidReportDesc.data
          .align    4
          .text
.LDBG61:
.globl storeInt, "f{0}(&(a(:uc)),si,si)"
.globl storeInt.nstackwords
.globl storeInt.maxthreads
.globl storeInt.maxtimers
.globl storeInt.maxchanends
.globl storeInt.maxsync
.type  storeInt, @function
.linkset storeInt.locnoside, 0
.linkset storeInt.locnochandec, 1
.linkset storeInt.nstackwords, 2
.cc_top storeInt.function,storeInt
.LDBG66:
.LDBG67:                             # line (242)
          .align    4
storeInt:
          extsp     0x2 
.LDBG68:
.L17:
          stw       r4, sp[0x0] 
.LDBG69:
          stw       r5, sp[0x1] 
.LDBG70:
.LDBG62:
.LDBG63:
.LDBG64:
.LDBG71:                             # line (242)
.LDBG72:                             # line (243)
          ashr      r5, r2, 0x18
          add       r4, r1, 0x3
          lsu       r11, r4, r3
          ecallf    r11
          st8       r5, r0[r4] 
.LDBG73:                             # line (244)
          ashr      r5, r2, 0x10
          add       r4, r1, 0x2
          lsu       r11, r4, r3
          ecallf    r11
          st8       r5, r0[r4] 
.LDBG74:                             # line (245)
          ashr      r5, r2, 0x8
          add       r4, r1, 0x1
          lsu       r11, r4, r3
          ecallf    r11
          st8       r5, r0[r4] 
.LDBG75:                             # line (246)
          lsu       r3, r1, r3
          ecallf    r3
          st8       r2, r0[r1] 
.LDBG76:                             # line (247)
.LDBG77:
          ldw       r4, sp[0x0] 
.LDBG78:
          ldw       r5, sp[0x1] 
.LDBG79:
          ldaw      sp, sp[0x2] 
          retsp     0x0 
.LDBG65:
.LDBG80:
.cc_bottom storeInt.function
.LDBG81:
          .section  .debug_frame, "",     @progbits
.cc_top storeInt.function,storeInt
          .align    4
          .int      .LDBG83-.LDBG82
.LDBG82:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG66
          .int      .LDBG81-.LDBG66
          .byte     0x01
          .int      .LDBG68
          .byte     0x0e
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG69
          .byte     0x84
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG70
          .byte     0x85
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG77
          .byte     0x0a
          .byte     0x01
          .int      .LDBG78
          .byte     0xc4
          .byte     0x01
          .int      .LDBG79
          .byte     0xc5
          .byte     0x01
          .int      .LDBG80
          .byte     0x0b
          .align    4, 0
.LDBG83:
.cc_bottom storeInt.function
.linkset storeInt.maxchanends, 0
.linkset storeInt.maxtimers, 0
.linkset storeInt.maxthreads, 1
          .text
.LDBG84:
.LDBG85:
.globl storeShort, "f{0}(&(a(:uc)),si,ss)"
.globl storeShort.nstackwords
.globl storeShort.maxthreads
.globl storeShort.maxtimers
.globl storeShort.maxchanends
.globl storeShort.maxsync
.type  storeShort, @function
.linkset storeShort.locnoside, 0
.linkset storeShort.locnochandec, 1
.linkset storeShort.nstackwords, 2
.cc_top storeShort.function,storeShort
.LDBG90:
.LDBG91:                             # line (250)
          .align    4
storeShort:
          extsp     0x2 
.LDBG92:
.L18:
          stw       r4, sp[0x0] 
.LDBG93:
          stw       r5, sp[0x1] 
.LDBG94:
.LDBG86:
.LDBG87:
.LDBG88:
.LDBG95:                             # line (250)
.LDBG96:                             # line (251)
          ashr      r5, r2, 0x8
          add       r4, r1, 0x1
          lsu       r11, r4, r3
          ecallf    r11
          st8       r5, r0[r4] 
.LDBG97:                             # line (252)
          lsu       r3, r1, r3
          ecallf    r3
          st8       r2, r0[r1] 
.LDBG98:                             # line (253)
.LDBG99:
          ldw       r4, sp[0x0] 
.LDBG100:
          ldw       r5, sp[0x1] 
.LDBG101:
          ldaw      sp, sp[0x2] 
          retsp     0x0 
.LDBG89:
.LDBG102:
.cc_bottom storeShort.function
.LDBG103:
          .section  .debug_frame, "",     @progbits
.cc_top storeShort.function,storeShort
          .align    4
          .int      .LDBG105-.LDBG104
.LDBG104:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG90
          .int      .LDBG103-.LDBG90
          .byte     0x01
          .int      .LDBG92
          .byte     0x0e
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG93
          .byte     0x84
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG94
          .byte     0x85
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG99
          .byte     0x0a
          .byte     0x01
          .int      .LDBG100
          .byte     0xc4
          .byte     0x01
          .int      .LDBG101
          .byte     0xc5
          .byte     0x01
          .int      .LDBG102
          .byte     0x0b
          .align    4, 0
.LDBG105:
.cc_bottom storeShort.function
.linkset storeShort.maxchanends, 0
.linkset storeShort.maxtimers, 0
.linkset storeShort.maxthreads, 1
          .text
.LDBG106:
.LDBG107:
.call ep0_thread, safememcpy
.globpassesref safememcpy, strDescs, "./tests/audio/src/ep_thread0.xc:267: error: `ep0_thread' makes alias of global 'strDescs' in call to `safememcpy'"
.globwrite ep0_thread, strDescs, "./tests/audio/src/ep_thread0.xc:267: error: previously used here"
.globwrite ep0_thread, strDescs, "./tests/audio/src/ep_thread0.xc:267: error: previously used here"
.globpassesref safememcpy, strDesc_langIDs, "./tests/audio/src/ep_thread0.xc:267: error: `ep0_thread' makes alias of global 'strDesc_langIDs' in call to `safememcpy'"
.call ep0_thread, XUD_DoEnumReqs
.globpassesref XUD_DoEnumReqs, devDesc, "./tests/audio/src/ep_thread0.xc:272: error: `ep0_thread' makes alias of global 'devDesc' in call to `XUD_DoEnumReqs'"
.globwrite ep0_thread, devDesc, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.globwrite ep0_thread, devDesc, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.globpassesref XUD_DoEnumReqs, cfgDesc, "./tests/audio/src/ep_thread0.xc:272: error: `ep0_thread' makes alias of global 'cfgDesc' in call to `XUD_DoEnumReqs'"
.globwrite ep0_thread, cfgDesc, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.globwrite ep0_thread, cfgDesc, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.globpassesref XUD_DoEnumReqs, strDescs, "./tests/audio/src/ep_thread0.xc:272: error: `ep0_thread' makes alias of global 'strDescs' in call to `XUD_DoEnumReqs'"
.globwrite ep0_thread, strDescs, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.globwrite ep0_thread, strDescs, "./tests/audio/src/ep_thread0.xc:272: error: previously used here"
.call ep0_thread, XUD_DoSetRequestStatus
.call ep0_thread, XUD_DoGetRequest
.globpassesref XUD_DoGetRequest, hidReportDesc, "./tests/audio/src/ep_thread0.xc:308: error: `ep0_thread' makes alias of global 'hidReportDesc' in call to `XUD_DoGetRequest'"
.globwrite ep0_thread, hidReportDesc, "./tests/audio/src/ep_thread0.xc:308: error: previously used here"
.globwrite ep0_thread, hidReportDesc, "./tests/audio/src/ep_thread0.xc:308: error: previously used here"
.call ep0_thread, XUD_Error
          .section .cp.string,     "ac", @progbits
          .align    4
s.LC0:
          .ascii    "Unknown descriptor request\n\0"
.call ep0_thread, XUD_DoSetRequestStatus
.call ep0_thread, XUD_Error
          .align    4
s.LC1:
          .ascii    "Unknown device request\0"
.call ep0_thread, XUD_Error
          .align    4
s.LC2:
          .ascii    "Unknown Recipient\0"
.call ep0_thread, XUD_GetBuffer_
.call ep0_thread, XUD_Error
          .align    4
s.LC3:
          .ascii    "Bad datalength for sample rate \0"
.globwrite ep0_thread, params, "./tests/audio/src/ep_thread0.xc:392: error: previously used here"
.call ep0_thread, XUD_SetBuffer_ResetPid
.globread ep0_thread, params, "./tests/audio/src/ep_thread0.xc:409: error: previously used here"
.call ep0_thread, XUD_DoGetRequest
.call ep0_thread, XUD_SetBuffer_ResetPid
.call ep0_thread, XUD_GetBuffer
.call ep0_thread, XUD_Error
          .align    4
s.LC4:
          .ascii    "Unknown control selector\0"
.call ep0_thread, XUD_GetBuffer
.globwrite ep0_thread, params, "./tests/audio/src/ep_thread0.xc:450: error: previously used here"
.call ep0_thread, updateVol
.call ep0_thread, XUD_SetBuffer_ResetPid
.globread ep0_thread, params, "./tests/audio/src/ep_thread0.xc:458: error: previously used here"
.globread ep0_thread, params, "./tests/audio/src/ep_thread0.xc:459: error: previously used here"
.call ep0_thread, XUD_SetBuffer_ResetPid
.call ep0_thread, XUD_GetBuffer
.call ep0_thread, XUD_GetBuffer
.globwrite ep0_thread, params, "./tests/audio/src/ep_thread0.xc:472: error: previously used here"
.call ep0_thread, updateVol
.call ep0_thread, XUD_SetBuffer_ResetPid
.globread ep0_thread, params, "./tests/audio/src/ep_thread0.xc:480: error: previously used here"
.call ep0_thread, XUD_SetBuffer_ResetPid
.call ep0_thread, XUD_GetBuffer
.call ep0_thread, XUD_Error
          .align    4
s.LC5:
          .ascii    " Unknown control selector\0"
.call ep0_thread, XUD_Error_hex
          .align    4
s.LC6:
          .ascii    "ERR: Unknown unit: \0"
.call ep0_thread, storeShort
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, storeInt
.call ep0_thread, XUD_SetBuffer_ResetPid
.call ep0_thread, XUD_GetBuffer
.call ep0_thread, XUD_Error_hex
          .align    4
s.LC7:
          .ascii    "Unknown control selector: \0"
.call ep0_thread, storeShort
.call ep0_thread, storeShort
.call ep0_thread, storeShort
.call ep0_thread, storeShort
.call ep0_thread, XUD_SetBuffer_ResetPid
.call ep0_thread, XUD_GetBuffer
.call ep0_thread, printstrln
          .align    4
s.LC8:
          .ascii    "unknown cont rol selector: \0"
.call ep0_thread, printhexln
.call ep0_thread, printstrln
          .align    4
s.LC9:
          .ascii    "unknown unit: \0"
.call ep0_thread, printhexln
.call ep0_thread, printstr
          .align    4
s.LC10:
          .ascii    "Err bad req\0"
.call ep0_thread, printhexln
.call ep0_thread, printstrln
          .align    4
s.LC11:
          .ascii    "Err...\0"
.call ep0_thread, XUD_PrintSetupPacket
.call ep0_thread, printstr
          .align    4
s.LC12:
          .ascii    "ERR: Unknown request: \0"
.call ep0_thread, XUD_PrintSetupPacket
.call ep0_thread, __builtin_inct
.call ep0_thread, __builtin_inct
.set __builtin_inct, 0
.linkset __builtin_inct.locnoside, 0
.linkset __builtin_inct.locnochandec, 1
          .text
.globl ep0_thread, "f{0}(chd,chd)"
.globl ep0_thread.nstackwords
.globl ep0_thread.maxthreads
.globl ep0_thread.maxtimers
.globl ep0_thread.maxchanends
.globl ep0_thread.maxsync
.type  ep0_thread, @function
.linkset ep0_thread.locnoside, 0
.linkset ep0_thread.locnochandec, 1
.linkset .LLNK64, safememcpy.nstackwords $M XUD_DoEnumReqs.nstackwords
.linkset .LLNK63, .LLNK64 $M printstr.nstackwords
.linkset .LLNK62, .LLNK63 $M XUD_PrintSetupPacket.nstackwords
.linkset .LLNK61, .LLNK62 $M XUD_Error.nstackwords
.linkset .LLNK60, .LLNK61 $M XUD_DoSetRequestStatus.nstackwords
.linkset .LLNK59, .LLNK60 $M XUD_Error.nstackwords
.linkset .LLNK58, .LLNK59 $M XUD_DoGetRequest.nstackwords
.linkset .LLNK57, .LLNK58 $M XUD_Error.nstackwords
.linkset .LLNK56, .LLNK57 $M XUD_DoSetRequestStatus.nstackwords
.linkset .LLNK55, .LLNK56 $M printstrln.nstackwords
.linkset .LLNK54, .LLNK55 $M XUD_PrintSetupPacket.nstackwords
.linkset .LLNK53, .LLNK54 $M printstr.nstackwords
.linkset .LLNK52, .LLNK53 $M printhexln.nstackwords
.linkset .LLNK51, .LLNK52 $M XUD_Error_hex.nstackwords
.linkset .LLNK50, .LLNK51 $M XUD_Error.nstackwords
.linkset .LLNK49, .LLNK50 $M XUD_DoGetRequest.nstackwords
.linkset .LLNK48, .LLNK49 $M XUD_GetBuffer_.nstackwords
.linkset .LLNK47, .LLNK48 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK46, .LLNK47 $M XUD_Error.nstackwords
.linkset .LLNK45, .LLNK46 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK44, .LLNK45 $M XUD_GetBuffer.nstackwords
.linkset .LLNK43, .LLNK44 $M XUD_Error.nstackwords
.linkset .LLNK42, .LLNK43 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK41, .LLNK42 $M XUD_GetBuffer.nstackwords
.linkset .LLNK40, .LLNK41 $M XUD_GetBuffer.nstackwords
.linkset .LLNK39, .LLNK40 $M updateVol.nstackwords
.linkset .LLNK38, .LLNK39 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK37, .LLNK38 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK36, .LLNK37 $M XUD_GetBuffer.nstackwords
.linkset .LLNK35, .LLNK36 $M XUD_GetBuffer.nstackwords
.linkset .LLNK34, .LLNK35 $M updateVol.nstackwords
.linkset .LLNK33, .LLNK34 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK32, .LLNK33 $M printstrln.nstackwords
.linkset .LLNK31, .LLNK32 $M printhexln.nstackwords
.linkset .LLNK30, .LLNK31 $M XUD_Error_hex.nstackwords
.linkset .LLNK29, .LLNK30 $M storeShort.nstackwords
.linkset .LLNK28, .LLNK29 $M storeInt.nstackwords
.linkset .LLNK27, .LLNK28 $M storeInt.nstackwords
.linkset .LLNK26, .LLNK27 $M storeInt.nstackwords
.linkset .LLNK25, .LLNK26 $M storeInt.nstackwords
.linkset .LLNK24, .LLNK25 $M storeInt.nstackwords
.linkset .LLNK23, .LLNK24 $M storeInt.nstackwords
.linkset .LLNK22, .LLNK23 $M storeInt.nstackwords
.linkset .LLNK21, .LLNK22 $M storeInt.nstackwords
.linkset .LLNK20, .LLNK21 $M storeInt.nstackwords
.linkset .LLNK19, .LLNK20 $M storeInt.nstackwords
.linkset .LLNK18, .LLNK19 $M storeInt.nstackwords
.linkset .LLNK17, .LLNK18 $M storeInt.nstackwords
.linkset .LLNK16, .LLNK17 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK15, .LLNK16 $M XUD_GetBuffer.nstackwords
.linkset .LLNK14, .LLNK15 $M printstrln.nstackwords
.linkset .LLNK13, .LLNK14 $M printhexln.nstackwords
.linkset .LLNK12, .LLNK13 $M storeShort.nstackwords
.linkset .LLNK11, .LLNK12 $M storeShort.nstackwords
.linkset .LLNK10, .LLNK11 $M storeShort.nstackwords
.linkset .LLNK9, .LLNK10 $M storeShort.nstackwords
.linkset .LLNK8, .LLNK9 $M XUD_SetBuffer_ResetPid.nstackwords
.linkset .LLNK7, .LLNK8 $M XUD_GetBuffer.nstackwords
.linkset .LLNK6, .LLNK7 $M .LLNK7
.linkset .LLNK5, .LLNK6 + 266
.linkset ep0_thread.nstackwords, .LLNK5
.cc_top ep0_thread.function,ep0_thread
.LDBG254:
.LDBG255:                            # line (256)
          .align    4
ep0_thread:
          entsp     0x10a 
.LDBG256:
.L98:
          mov       r8, r0
.LDBG112:
          mov       r7, r1
.LDBG113:
.LDBG257:                            # line (256)
.LDBG110:
.LDBG258:                            # line (261)
.LDBG259:                            # line (267)
          ldaw      r0, dp[strDescs] 
          ldaw      r1, dp[strDesc_langIDs] 
          ldc       r2, 0x5
          ldc       r3, 0x28
          ldc       r4, 0x5
          stw       r4, sp[0x1] 
.LDBG260:                            # line (267)
          bl        safememcpy 
.LDBG261:                            # line (269)
.LDBG114:
.LDBG115:
.L22:
.L99:
.LDBG108:
.LDBG262:                            # line (272)
          ldaw      r1, dp[devDesc] 
          ldc       r9, 0x12
          mov       r2, r9
          ldaw      r3, dp[cfgDesc] 
          ldc       r5, 0xc2
          ldaw      r4, dp[strDescs] 
          ldaw      r6, sp[0x107] 
          mov       r0, r8
          stw       r5, sp[0x1] 
          stw       r4, sp[0x2] 
          stw       r6, sp[0x3] 
          ldc       r4, 0x12
          stw       r4, sp[0x4] 
          ldc       r4, 0xc2
          stw       r4, sp[0x5] 
          ldc       r4, 0x9
          stw       r4, sp[0x6] 
.LDBG263:                            # line (272)
          bl        XUD_DoEnumReqs 
.LDBG264:                            # line (273)
.LDBG265:                            # line (273)
          ldw       r11, cp[s.LC13]
          .section .cp.const4,     "acM", @progbits, 4
s.LC13:
          .align    4
          .int      0xffffffff
          .text
          eq        r1, r0, r11
          bt        r1, .L25 
.LDBG116:
.LDBG117:
.L101:
.LDBG266:                            # line (276)
          bf        r0, .L27 
.LDBG118:
.LDBG119:
.L23:
.L144:
.LDBG267:                            # line (269)
          bu        .L22 
.LDBG120:
.LDBG121:
.LDBG122:
.LDBG123:
.L25:
.L100:
.LDBG268:                            # line (274)
          bu        .L23 
.LDBG124:
.LDBG125:
.LDBG126:
.LDBG127:
.L27:
.L102:
          ldaw      r6, sp[0x107] 
.LDBG269:                            # line (279)
          ldc       r0, 0x1
          ld8u      r0, r6[r0] 
          bf        r0, .L30 
.LDBG128:
.LDBG129:
          eq        r0, r0, 0x1
          bt        r0, .L48 
.LDBG130:
.LDBG131:
.L143:
.LDBG270:                            # line (667)
          ldaw      r11, cp[s.LC12] 
          mov       r0, r11
          ldc       r1, 0x17
.LDBG271:                            # line (667)
          bl        printstr 
.LDBG272:                            # line (668)
          mov       r0, r6
.LDBG273:                            # line (668)
          bl        XUD_PrintSetupPacket 
.LDBG132:
.LDBG133:
.L29:
          bu        .L23 
.LDBG134:
.LDBG135:
.L30:
.L103:
.LDBG274:                            # line (283)
          ldc       r0, 0x0
          ld8u      r0, r6[r0] 
          bf        r0, .L42 
.LDBG136:
.LDBG137:
          eq        r0, r0, 0x1
          bt        r0, .L33 
.LDBG138:
.LDBG139:
.L112:
.LDBG275:                            # line (343)
          ldaw      r11, cp[s.LC2] 
          mov       r0, r11
          ldc       r1, 0x12
.LDBG276:                            # line (343)
          bl        XUD_Error 
          bu        .L29 
.LDBG140:
.LDBG141:
.L33:
.L104:
.LDBG277:                            # line (287)
          ldc       r0, 0x3
          ld8u      r0, r6[r0] 
          eq        r1, r0, 0x6
          bt        r1, .L37 
.LDBG142:
.LDBG143:
          eq        r0, r0, 0xb
          bt        r0, .L36 
.LDBG144:
.LDBG145:
          bu        .L29 
.LDBG146:
.LDBG147:
.L36:
.L105:
          ldc       r5, 0x0
.LDBG278:                            # line (295)
          mov       r1, r5
          mov       r0, r8
.LDBG279:                            # line (295)
          bl        XUD_DoSetRequestStatus 
          bu        .L29 
.LDBG148:
.LDBG149:
.L37:
.L106:
.LDBG280:                            # line (302)
          ldc       r0, 0x2
          ld16s     r1, r6[r0] 
          zext      r1, 0x10 
          ldc       r0, 0xff00
          and       r0, r1, r0
          ldc       r1, 0x2200
          eq        r0, r0, r1
          bt        r0, .L40 
.LDBG150:
.LDBG151:
.L108:
.LDBG281:                            # line (312)
          ldaw      r11, cp[s.LC0] 
          mov       r0, r11
          ldc       r1, 0x1c
.LDBG282:                            # line (312)
          bl        XUD_Error 
          bu        .L29 
.LDBG152:
.LDBG153:
.L40:
.L107:
          ldc       r5, 0x0
.LDBG283:                            # line (308)
          ldaw      r2, dp[hidReportDesc] 
          ldc       r3, 0x32
          mov       r0, r8
          mov       r1, r5
          ldc       r4, 0x32
          stw       r4, sp[0x1] 
.LDBG284:                            # line (308)
          bl        XUD_DoGetRequest 
          bu        .L29 
.LDBG154:
.LDBG155:
.LDBG156:
.LDBG157:
.L42:
.L109:
.LDBG285:                            # line (323)
          ldc       r0, 0x3
          ld8u      r0, r6[r0] 
          eq        r0, r0, 0x9
          bt        r0, .L45 
.LDBG158:
.LDBG159:
.L111:
.LDBG286:                            # line (335)
          ldaw      r11, cp[s.LC1] 
          mov       r0, r11
          ldc       r1, 0x17
.LDBG287:                            # line (335)
          bl        XUD_Error 
          bu        .L29 
.LDBG160:
.LDBG161:
.L45:
.L110:
          ldc       r5, 0x0
.LDBG288:                            # line (331)
          mov       r1, r5
          mov       r0, r8
.LDBG289:                            # line (331)
          bl        XUD_DoSetRequestStatus 
          bu        .L29 
.LDBG162:
.LDBG163:
.LDBG164:
.LDBG165:
.L48:
.L113:
.LDBG290:                            # line (352)
          ldc       r0, 0x0
          ld8u      r0, r6[r0] 
          eq        r0, r0, 0x1
          bt        r0, .L51 
.LDBG166:
.LDBG167:
.L142:
.LDBG291:                            # line (658)
          ldaw      r11, cp[s.LC11] 
          mov       r0, r11
          ldc       r1, 0x7
.LDBG292:                            # line (658)
          bl        printstrln 
.LDBG293:                            # line (659)
          mov       r0, r6
.LDBG294:                            # line (659)
          bl        XUD_PrintSetupPacket 
          bu        .L29 
.LDBG168:
.LDBG169:
.L51:
.L114:
.LDBG295:                            # line (358)
          ldc       r0, 0x3
          ld8u      r0, r6[r0] 
          eq        r1, r0, 0x1
          bt        r1, .L54 
.LDBG170:
.LDBG171:
          eq        r0, r0, 0x2
          bt        r0, .L81 
.LDBG172:
.LDBG173:
.L141:
.LDBG296:                            # line (651)
          ldaw      r11, cp[s.LC10] 
          mov       r0, r11
          ldc       r1, 0xc
.LDBG297:                            # line (651)
          bl        printstr 
.LDBG298:                            # line (652)
          ldc       r0, 0x3
          ld8u      r0, r6[r0] 
.LDBG299:                            # line (652)
          bl        printhexln 
          bu        .L29 
.LDBG174:
.LDBG175:
.L54:
.L115:
.LDBG300:                            # line (364)
          ldc       r0, 0x3
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r1, r0, 0x3
          bt        r1, .L57 
.LDBG176:
.LDBG177:
          eq        r0, r0, 0xa
          bt        r0, .L68 
.LDBG178:
.LDBG179:
.L132:
.LDBG301:                            # line (499)
          ldaw      r11, cp[s.LC6] 
          mov       r0, r11
          ldc       r1, 0x3
          ld16s     r1, r6[r1] 
          zext      r1, 0x10 
          ldc       r2, 0x14
.LDBG302:                            # line (499)
          bl        XUD_Error_hex 
          bu        .L29 
.LDBG180:
.LDBG181:
.L57:
.L116:
.LDBG303:                            # line (370)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r1, r0, 0x1
          bt        r1, .L60 
.LDBG182:
.LDBG183:
          eq        r0, r0, 0x2
          bt        r0, .L66 
.LDBG184:
.LDBG185:
.L123:
.LDBG304:                            # line (432)
          ldaw      r11, cp[s.LC4] 
          mov       r0, r11
          ldc       r1, 0x19
.LDBG305:                            # line (432)
          bl        XUD_Error 
          bu        .L29 
.LDBG186:
.LDBG187:
.L60:
.L117:
.LDBG306:                            # line (376)
          ldc       r0, 0x2
          ld8u      r0, r6[r0] 
.LDBG307:                            # line (376)
          bf        r0, .L62 
.LDBG188:
.LDBG189:
.L121:
          ldaw      r2, dp[params] 
.LDBG308:                            # line (409)
          ldw       r0, r2[0x9] 
          ldaw      r4, sp[0x7] 
          stw       r0, r4[0x0] 
          ldc       r5, 0x0
.LDBG309:                            # line (412)
          ldaw      r4, sp[0x7] 
          mov       r2, r4
          ldc       r0, 0x4
          ld16s     r3, r6[r0] 
          zext      r3, 0x10 
          mov       r0, r8
          mov       r1, r5
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG310:                            # line (412)
          bl        XUD_DoGetRequest 
          bu        .L29 
.LDBG190:
.LDBG191:
.LDBG192:
.LDBG193:
.L62:
.L118:
          ldc       r5, 0x0
.LDBG311:                            # line (379)
          mov       r1, r5
          ldaw      r4, sp[0x7] 
          mov       r2, r4
          mov       r0, r8
          ldc       r3, 0x400
.LDBG312:                            # line (379)
          bl        XUD_GetBuffer_ 
.LDBG313:                            # line (381)
          eq        r0, r0, 0x4
          bf        r0, .L65 
.LDBG194:
.LDBG195:
.L64:
.L120:
          ldaw      r4, sp[0x7] 
.LDBG314:                            # line (387)
          ldc       r0, 0x0
          ld8u      r1, r4[r0] 
          ldc       r0, 0x1
          ld8u      r0, r4[r0] 
          shl       r0, r0, 0x8
          or        r1, r1, r0
          ldc       r0, 0x2
          ld8u      r0, r4[r0] 
          shl       r0, r0, 0x10
          or        r1, r1, r0
          ldc       r0, 0x3
          ld8u      r0, r4[r0] 
          shl       r0, r0, 0x18
          or        r0, r1, r0
          ldaw      r2, dp[params] 
.LDBG315:                            # line (392)
          stw       r0, r2[0x9] 
.LDBG316:                            # line (395)
          mov       r2, r5
          mov       r3, r4
          ldc       r4, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r5, sp[0x1] 
          stw       r4, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG317:                            # line (395)
          bl        XUD_SetBuffer_ResetPid 
          bu        .L29 
.LDBG196:
.LDBG197:
.LDBG198:
.LDBG199:
.L65:
.L119:
.LDBG318:                            # line (383)
          ldaw      r11, cp[s.LC3] 
          mov       r0, r11
          ldc       r1, 0x20
.LDBG319:                            # line (383)
          bl        XUD_Error 
          bu        .L64 
.LDBG200:
.LDBG201:
.LDBG202:
.LDBG203:
.L66:
.L122:
          ldaw      r4, sp[0x7] 
.LDBG320:                            # line (419)
          ldc       r1, 0x0
          ldc       r0, 0x1
          st8       r0, r4[r1] 
          ldc       r5, 0x0
.LDBG321:                            # line (421)
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          ldc       r0, 0x4
          ld16s     r4, r6[r0] 
          zext      r4, 0x10 
          ldc       r6, 0x4b
          mov       r0, r8
          mov       r1, r7
          mov       r2, r5
          stw       r4, sp[0x1] 
          stw       r6, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG322:                            # line (421)
          bl        XUD_SetBuffer_ResetPid 
.LDBG323:                            # line (425)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG324:                            # line (425)
          bl        XUD_GetBuffer 
          bu        .L29 
.LDBG204:
.LDBG205:
.LDBG206:
.LDBG207:
.L68:
.L124:
.LDBG325:                            # line (442)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r1, r0, 0x1
          bt        r1, .L75 
.LDBG208:
.LDBG209:
          eq        r0, r0, 0x2
          bt        r0, .L71 
.LDBG210:
.LDBG211:
.L131:
.LDBG326:                            # line (489)
          ldaw      r11, cp[s.LC5] 
          mov       r0, r11
          ldc       r1, 0x1a
.LDBG327:                            # line (489)
          bl        XUD_Error 
          bu        .L29 
.LDBG212:
.LDBG213:
.L71:
.L125:
.LDBG328:                            # line (446)
          ldc       r0, 0x2
          ld8u      r0, r6[r0] 
.LDBG329:                            # line (446)
          bf        r0, .L73 
.LDBG214:
.LDBG215:
.L127:
          ldaw      r2, dp[params] 
.LDBG330:                            # line (458)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ldc       r3, 0xff
          and       r0, r0, r3
          ldc       r1, 0x1c
          lsu       r4, r0, r1
          ecallf    r4
          ldw       r5, r2[r0] 
          ldaw      r4, sp[0x7] 
          ldc       r0, 0x0
          st8       r5, r4[r0] 
.LDBG331:                            # line (459)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          and       r0, r0, r3
          lsu       r1, r0, r1
          ecallf    r1
          ldw       r0, r2[r0] 
          shr       r1, r0, 0x8
          ldaw      r4, sp[0x7] 
          ldc       r0, 0x1
          st8       r1, r4[r0] 
          ldc       r5, 0x0
.LDBG332:                            # line (460)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          ldc       r0, 0x4
          ld16s     r4, r6[r0] 
          zext      r4, 0x10 
          ldc       r6, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r4, sp[0x1] 
          stw       r6, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG333:                            # line (460)
          bl        XUD_SetBuffer_ResetPid 
.LDBG334:                            # line (462)
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          mov       r2, r5
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG335:                            # line (462)
          bl        XUD_GetBuffer 
          bu        .L29 
.LDBG216:
.LDBG217:
.LDBG218:
.LDBG219:
.L73:
.L126:
          ldc       r5, 0x0
.LDBG336:                            # line (449)
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          mov       r2, r5
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG337:                            # line (449)
          bl        XUD_GetBuffer 
          ldaw      r4, sp[0x7] 
.LDBG338:                            # line (450)
          ldc       r0, 0x0
          ld8u      r1, r4[r0] 
          ldc       r0, 0x1
          ld8u      r0, r4[r0] 
          shl       r0, r0, 0x8
          or        r9, r1, r0
          ldaw      r2, dp[params] 
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ldc       r3, 0xff
          and       r0, r0, r3
          ldc       r1, 0x1c
          lsu       r1, r0, r1
          ecallf    r1
          stw       r9, r2[r0] 
.LDBG339:                            # line (451)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          and       r0, r0, r3
.LDBG340:                            # line (451)
          bl        updateVol 
.LDBG341:                            # line (454)
          mov       r2, r5
          mov       r3, r4
          ldc       r4, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r5, sp[0x1] 
          stw       r4, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG342:                            # line (454)
          bl        XUD_SetBuffer_ResetPid 
          bu        .L29 
.LDBG220:
.LDBG221:
.LDBG222:
.LDBG223:
.L75:
.L128:
.LDBG343:                            # line (468)
          ldc       r0, 0x2
          ld8u      r0, r6[r0] 
.LDBG344:                            # line (468)
          bf        r0, .L77 
.LDBG224:
.LDBG225:
.L130:
          ldaw      r2, dp[params] 
.LDBG345:                            # line (480)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ldc       r3, 0xff
          and       r0, r0, r3
          add       r0, r0, 0xa
          ldc       r1, 0x1c
          lsu       r1, r0, r1
          ecallf    r1
          ldw       r1, r2[r0] 
          ldaw      r4, sp[0x7] 
          ldc       r0, 0x0
          st8       r1, r4[r0] 
          ldc       r5, 0x0
.LDBG346:                            # line (481)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          ldc       r0, 0x4
          ld16s     r4, r6[r0] 
          zext      r4, 0x10 
          ldc       r6, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r4, sp[0x1] 
          stw       r6, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG347:                            # line (481)
          bl        XUD_SetBuffer_ResetPid 
.LDBG348:                            # line (483)
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          mov       r2, r5
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG349:                            # line (483)
          bl        XUD_GetBuffer 
          bu        .L29 
.LDBG226:
.LDBG227:
.LDBG228:
.LDBG229:
.L77:
.L129:
          ldc       r5, 0x0
.LDBG350:                            # line (471)
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          mov       r2, r5
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG351:                            # line (471)
          bl        XUD_GetBuffer 
          ldaw      r4, sp[0x7] 
.LDBG352:                            # line (472)
          ldc       r0, 0x0
          ld8u      r9, r4[r0] 
          ldaw      r2, dp[params] 
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ldc       r3, 0xff
          and       r0, r0, r3
          add       r0, r0, 0xa
          ldc       r1, 0x1c
          lsu       r1, r0, r1
          ecallf    r1
          stw       r9, r2[r0] 
.LDBG353:                            # line (473)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          and       r0, r0, r3
.LDBG354:                            # line (473)
          bl        updateVol 
.LDBG355:                            # line (476)
          mov       r2, r5
          mov       r3, r4
          ldc       r4, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r5, sp[0x1] 
          stw       r4, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG356:                            # line (476)
          bl        XUD_SetBuffer_ResetPid 
          bu        .L29 
.LDBG230:
.LDBG231:
.LDBG232:
.LDBG233:
.L81:
.L133:
.LDBG357:                            # line (509)
          ldc       r0, 0x3
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r1, r0, 0x3
          bt        r1, .L84 
.LDBG234:
.LDBG235:
          eq        r0, r0, 0xa
          bt        r0, .L89 
.LDBG236:
.LDBG237:
.L140:
.LDBG358:                            # line (571)
          ldaw      r11, cp[s.LC9] 
          mov       r0, r11
          ldc       r1, 0xf
.LDBG359:                            # line (571)
          bl        printstrln 
.LDBG360:                            # line (572)
          ldc       r0, 0x3
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
.LDBG361:                            # line (572)
          bl        printhexln 
          bu        .L29 
.LDBG238:
.LDBG239:
.L84:
.L134:
.LDBG362:                            # line (514)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r0, r0, 0x1
          bt        r0, .L87 
.LDBG240:
.LDBG241:
.L136:
.LDBG363:                            # line (540)
          ldaw      r11, cp[s.LC7] 
          ldc       r0, 0x2
          ld16s     r1, r6[r0] 
          zext      r1, 0x10 
          mov       r0, r11
          ldc       r2, 0x1b
.LDBG364:                            # line (540)
          bl        XUD_Error_hex 
          bu        .L29 
.LDBG242:
.LDBG243:
.L87:
.L135:
          ldaw      r4, sp[0x7] 
.LDBG365:                            # line (518)
          mov       r0, r4
          ldc       r1, 0x0
          ldc       r2, 0x4
          ldc       r3, 0x400
.LDBG366:                            # line (518)
          bl        storeShort 
          ldaw      r4, sp[0x7] 
.LDBG367:                            # line (519)
          ldc       r1, 0x2
          ldc       r5, 0xac44
          mov       r2, r5
          mov       r0, r4
          ldc       r3, 0x400
.LDBG368:                            # line (519)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG369:                            # line (520)
          mov       r0, r4
          ldc       r1, 0x6
          mov       r2, r5
          ldc       r3, 0x400
.LDBG370:                            # line (520)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG371:                            # line (521)
          mov       r0, r4
          ldc       r1, 0xa
          ldc       r2, 0x0
          ldc       r3, 0x400
.LDBG372:                            # line (521)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG373:                            # line (522)
          mov       r0, r4
          ldc       r1, 0xe
          ldc       r5, 0xbb80
          mov       r2, r5
          ldc       r3, 0x400
.LDBG374:                            # line (522)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG375:                            # line (523)
          mov       r0, r4
          mov       r1, r9
          mov       r2, r5
          ldc       r3, 0x400
.LDBG376:                            # line (523)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG377:                            # line (524)
          mov       r0, r4
          ldc       r1, 0x16
          ldc       r2, 0x0
          ldc       r3, 0x400
.LDBG378:                            # line (524)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG379:                            # line (525)
          mov       r0, r4
          ldc       r1, 0x1a
          ldw       r11, cp[s.LC14]
          .section .cp.const4,     "acM", @progbits, 4
s.LC14:
          .align    4
          .int      0x00017700
          .text
          mov       r5, r11
          mov       r2, r5
          ldc       r3, 0x400
.LDBG380:                            # line (525)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG381:                            # line (526)
          mov       r0, r4
          ldc       r1, 0x1e
          mov       r2, r5
          ldc       r3, 0x400
.LDBG382:                            # line (526)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG383:                            # line (527)
          mov       r0, r4
          ldc       r1, 0x22
          ldc       r2, 0x0
          ldc       r3, 0x400
.LDBG384:                            # line (527)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG385:                            # line (528)
          mov       r0, r4
          ldc       r1, 0x26
          ldw       r11, cp[s.LC15]
          .section .cp.const4,     "acM", @progbits, 4
s.LC15:
          .align    4
          .int      0x0002ee00
          .text
          mov       r5, r11
          mov       r2, r5
          ldc       r3, 0x400
.LDBG386:                            # line (528)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG387:                            # line (529)
          mov       r0, r4
          ldc       r1, 0x2a
          mov       r2, r5
          ldc       r3, 0x400
.LDBG388:                            # line (529)
          bl        storeInt 
          ldaw      r4, sp[0x7] 
.LDBG389:                            # line (530)
          mov       r0, r4
          ldc       r1, 0x2e
          ldc       r2, 0x0
          ldc       r3, 0x400
.LDBG390:                            # line (530)
          bl        storeInt 
          ldc       r5, 0x0
.LDBG391:                            # line (532)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          ldc       r0, 0x4
          ld16s     r4, r6[r0] 
          zext      r4, 0x10 
          ldc       r6, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r4, sp[0x1] 
          stw       r6, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG392:                            # line (532)
          bl        XUD_SetBuffer_ResetPid 
.LDBG393:                            # line (534)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG394:                            # line (534)
          bl        XUD_GetBuffer 
          bu        .L29 
.LDBG244:
.LDBG245:
.LDBG246:
.LDBG247:
.L89:
.L137:
.LDBG395:                            # line (546)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
          ashr      r0, r0, 0x8
          eq        r0, r0, 0x2
          bt        r0, .L92 
.LDBG248:
.LDBG249:
.L139:
.LDBG396:                            # line (562)
          ldaw      r11, cp[s.LC8] 
          mov       r0, r11
          ldc       r1, 0x1c
.LDBG397:                            # line (562)
          bl        printstrln 
.LDBG398:                            # line (563)
          ldc       r0, 0x2
          ld16s     r0, r6[r0] 
          zext      r0, 0x10 
.LDBG399:                            # line (563)
          bl        printhexln 
          bu        .L29 
.LDBG250:
.LDBG251:
.L92:
.L138:
          ldaw      r4, sp[0x7] 
.LDBG400:                            # line (550)
          mov       r0, r4
          ldc       r1, 0x0
          ldc       r5, 0x1
          mov       r2, r5
          ldc       r3, 0x400
.LDBG401:                            # line (550)
          bl        storeShort 
          ldaw      r4, sp[0x7] 
.LDBG402:                            # line (551)
          mov       r0, r4
          ldc       r1, 0x2
          ldc       r2, 0x0
          ldc       r3, 0x400
.LDBG403:                            # line (551)
          bl        storeShort 
          ldaw      r4, sp[0x7] 
.LDBG404:                            # line (552)
          mov       r0, r4
          ldc       r1, 0x4
          ldc       r2, 0x7fff
          ldc       r3, 0x400
.LDBG405:                            # line (552)
          bl        storeShort 
          ldaw      r4, sp[0x7] 
.LDBG406:                            # line (553)
          mov       r0, r4
          ldc       r1, 0x6
          mov       r2, r5
          ldc       r3, 0x400
.LDBG407:                            # line (553)
          bl        storeShort 
          ldc       r5, 0x0
.LDBG408:                            # line (555)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          ldc       r0, 0x4
          ld16s     r4, r6[r0] 
          zext      r4, 0x10 
          ldc       r6, 0x4b
          mov       r0, r8
          mov       r1, r7
          stw       r4, sp[0x1] 
          stw       r6, sp[0x2] 
          ldc       r4, 0x400
          stw       r4, sp[0x3] 
.LDBG409:                            # line (555)
          bl        XUD_SetBuffer_ResetPid 
.LDBG410:                            # line (557)
          mov       r2, r5
          ldaw      r4, sp[0x7] 
          mov       r3, r4
          mov       r0, r8
          mov       r1, r7
          ldc       r4, 0x400
          stw       r4, sp[0x1] 
.LDBG411:                            # line (557)
          bl        XUD_GetBuffer 
          bu        .L29 
.LDBG252:
.LDBG109:
.L145:
.LDBG412:                            # line (679)
.LDBG413:                            # line (679)
.LDBG414:                            # line (680)
.LDBG415:                            # line (680)
.LDBG111:
.LDBG416:                            # line (687)
.LDBG253:
.cc_bottom ep0_thread.function
.LDBG417:
          .section  .debug_frame, "",     @progbits
.cc_top ep0_thread.function,ep0_thread
          .align    4
          .int      .LDBG419-.LDBG418
.LDBG418:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG254
          .int      .LDBG417-.LDBG254
          .byte     0x01
          .int      .LDBG256
          .byte     0x0e
          .uleb128  0x428
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .align    4, 0
.LDBG419:
.cc_bottom ep0_thread.function
.linkset .LLNK122, safememcpy.maxchanends $M XUD_DoEnumReqs.maxchanends
.linkset .LLNK121, .LLNK122 $M printstr.maxchanends
.linkset .LLNK120, .LLNK121 $M XUD_PrintSetupPacket.maxchanends
.linkset .LLNK119, .LLNK120 $M printstrln.maxchanends
.linkset .LLNK118, .LLNK119 $M XUD_PrintSetupPacket.maxchanends
.linkset .LLNK117, .LLNK118 $M printstr.maxchanends
.linkset .LLNK116, .LLNK117 $M printhexln.maxchanends
.linkset .LLNK115, .LLNK116 $M printstrln.maxchanends
.linkset .LLNK114, .LLNK115 $M printhexln.maxchanends
.linkset .LLNK113, .LLNK114 $M printstrln.maxchanends
.linkset .LLNK112, .LLNK113 $M printhexln.maxchanends
.linkset .LLNK111, .LLNK112 $M storeShort.maxchanends
.linkset .LLNK110, .LLNK111 $M storeShort.maxchanends
.linkset .LLNK109, .LLNK110 $M storeShort.maxchanends
.linkset .LLNK108, .LLNK109 $M storeShort.maxchanends
.linkset .LLNK107, .LLNK108 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK106, .LLNK107 $M XUD_GetBuffer.maxchanends
.linkset .LLNK105, .LLNK106 $M XUD_Error_hex.maxchanends
.linkset .LLNK104, .LLNK105 $M storeShort.maxchanends
.linkset .LLNK103, .LLNK104 $M storeInt.maxchanends
.linkset .LLNK102, .LLNK103 $M storeInt.maxchanends
.linkset .LLNK101, .LLNK102 $M storeInt.maxchanends
.linkset .LLNK100, .LLNK101 $M storeInt.maxchanends
.linkset .LLNK99, .LLNK100 $M storeInt.maxchanends
.linkset .LLNK98, .LLNK99 $M storeInt.maxchanends
.linkset .LLNK97, .LLNK98 $M storeInt.maxchanends
.linkset .LLNK96, .LLNK97 $M storeInt.maxchanends
.linkset .LLNK95, .LLNK96 $M storeInt.maxchanends
.linkset .LLNK94, .LLNK95 $M storeInt.maxchanends
.linkset .LLNK93, .LLNK94 $M storeInt.maxchanends
.linkset .LLNK92, .LLNK93 $M storeInt.maxchanends
.linkset .LLNK91, .LLNK92 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK90, .LLNK91 $M XUD_GetBuffer.maxchanends
.linkset .LLNK89, .LLNK90 $M XUD_Error_hex.maxchanends
.linkset .LLNK88, .LLNK89 $M XUD_Error.maxchanends
.linkset .LLNK87, .LLNK88 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK86, .LLNK87 $M XUD_GetBuffer.maxchanends
.linkset .LLNK85, .LLNK86 $M XUD_GetBuffer.maxchanends
.linkset .LLNK84, .LLNK85 $M updateVol.maxchanends
.linkset .LLNK83, .LLNK84 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK82, .LLNK83 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK81, .LLNK82 $M XUD_GetBuffer.maxchanends
.linkset .LLNK80, .LLNK81 $M XUD_GetBuffer.maxchanends
.linkset .LLNK79, .LLNK80 $M updateVol.maxchanends
.linkset .LLNK78, .LLNK79 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK77, .LLNK78 $M XUD_Error.maxchanends
.linkset .LLNK76, .LLNK77 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK75, .LLNK76 $M XUD_GetBuffer.maxchanends
.linkset .LLNK74, .LLNK75 $M XUD_DoGetRequest.maxchanends
.linkset .LLNK73, .LLNK74 $M XUD_GetBuffer_.maxchanends
.linkset .LLNK72, .LLNK73 $M XUD_SetBuffer_ResetPid.maxchanends
.linkset .LLNK71, .LLNK72 $M XUD_Error.maxchanends
.linkset .LLNK70, .LLNK71 $M XUD_Error.maxchanends
.linkset .LLNK69, .LLNK70 $M XUD_DoSetRequestStatus.maxchanends
.linkset .LLNK68, .LLNK69 $M XUD_Error.maxchanends
.linkset .LLNK67, .LLNK68 $M XUD_DoGetRequest.maxchanends
.linkset .LLNK66, .LLNK67 $M XUD_Error.maxchanends
.linkset .LLNK65, .LLNK66 $M XUD_DoSetRequestStatus.maxchanends
.linkset ep0_thread.maxchanends, .LLNK65
.linkset .LLNK180, safememcpy.maxtimers $M XUD_DoEnumReqs.maxtimers
.linkset .LLNK179, .LLNK180 $M printstr.maxtimers
.linkset .LLNK178, .LLNK179 $M XUD_PrintSetupPacket.maxtimers
.linkset .LLNK177, .LLNK178 $M printstrln.maxtimers
.linkset .LLNK176, .LLNK177 $M XUD_PrintSetupPacket.maxtimers
.linkset .LLNK175, .LLNK176 $M printstr.maxtimers
.linkset .LLNK174, .LLNK175 $M printhexln.maxtimers
.linkset .LLNK173, .LLNK174 $M printstrln.maxtimers
.linkset .LLNK172, .LLNK173 $M printhexln.maxtimers
.linkset .LLNK171, .LLNK172 $M printstrln.maxtimers
.linkset .LLNK170, .LLNK171 $M printhexln.maxtimers
.linkset .LLNK169, .LLNK170 $M storeShort.maxtimers
.linkset .LLNK168, .LLNK169 $M storeShort.maxtimers
.linkset .LLNK167, .LLNK168 $M storeShort.maxtimers
.linkset .LLNK166, .LLNK167 $M storeShort.maxtimers
.linkset .LLNK165, .LLNK166 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK164, .LLNK165 $M XUD_GetBuffer.maxtimers
.linkset .LLNK163, .LLNK164 $M XUD_Error_hex.maxtimers
.linkset .LLNK162, .LLNK163 $M storeShort.maxtimers
.linkset .LLNK161, .LLNK162 $M storeInt.maxtimers
.linkset .LLNK160, .LLNK161 $M storeInt.maxtimers
.linkset .LLNK159, .LLNK160 $M storeInt.maxtimers
.linkset .LLNK158, .LLNK159 $M storeInt.maxtimers
.linkset .LLNK157, .LLNK158 $M storeInt.maxtimers
.linkset .LLNK156, .LLNK157 $M storeInt.maxtimers
.linkset .LLNK155, .LLNK156 $M storeInt.maxtimers
.linkset .LLNK154, .LLNK155 $M storeInt.maxtimers
.linkset .LLNK153, .LLNK154 $M storeInt.maxtimers
.linkset .LLNK152, .LLNK153 $M storeInt.maxtimers
.linkset .LLNK151, .LLNK152 $M storeInt.maxtimers
.linkset .LLNK150, .LLNK151 $M storeInt.maxtimers
.linkset .LLNK149, .LLNK150 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK148, .LLNK149 $M XUD_GetBuffer.maxtimers
.linkset .LLNK147, .LLNK148 $M XUD_Error_hex.maxtimers
.linkset .LLNK146, .LLNK147 $M XUD_Error.maxtimers
.linkset .LLNK145, .LLNK146 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK144, .LLNK145 $M XUD_GetBuffer.maxtimers
.linkset .LLNK143, .LLNK144 $M XUD_GetBuffer.maxtimers
.linkset .LLNK142, .LLNK143 $M updateVol.maxtimers
.linkset .LLNK141, .LLNK142 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK140, .LLNK141 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK139, .LLNK140 $M XUD_GetBuffer.maxtimers
.linkset .LLNK138, .LLNK139 $M XUD_GetBuffer.maxtimers
.linkset .LLNK137, .LLNK138 $M updateVol.maxtimers
.linkset .LLNK136, .LLNK137 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK135, .LLNK136 $M XUD_Error.maxtimers
.linkset .LLNK134, .LLNK135 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK133, .LLNK134 $M XUD_GetBuffer.maxtimers
.linkset .LLNK132, .LLNK133 $M XUD_DoGetRequest.maxtimers
.linkset .LLNK131, .LLNK132 $M XUD_GetBuffer_.maxtimers
.linkset .LLNK130, .LLNK131 $M XUD_SetBuffer_ResetPid.maxtimers
.linkset .LLNK129, .LLNK130 $M XUD_Error.maxtimers
.linkset .LLNK128, .LLNK129 $M XUD_Error.maxtimers
.linkset .LLNK127, .LLNK128 $M XUD_DoSetRequestStatus.maxtimers
.linkset .LLNK126, .LLNK127 $M XUD_Error.maxtimers
.linkset .LLNK125, .LLNK126 $M XUD_DoGetRequest.maxtimers
.linkset .LLNK124, .LLNK125 $M XUD_Error.maxtimers
.linkset .LLNK123, .LLNK124 $M XUD_DoSetRequestStatus.maxtimers
.linkset ep0_thread.maxtimers, .LLNK123
.linkset .LLNK241, safememcpy.maxthreads - 1
.linkset .LLNK240, 1 + .LLNK241
.linkset .LLNK239, 1 $M .LLNK240
.linkset .LLNK243, XUD_DoEnumReqs.maxthreads - 1
.linkset .LLNK242, 1 + .LLNK243
.linkset .LLNK238, .LLNK239 $M .LLNK242
.linkset .LLNK245, printstr.maxthreads - 1
.linkset .LLNK244, 1 + .LLNK245
.linkset .LLNK237, .LLNK238 $M .LLNK244
.linkset .LLNK247, XUD_PrintSetupPacket.maxthreads - 1
.linkset .LLNK246, 1 + .LLNK247
.linkset .LLNK236, .LLNK237 $M .LLNK246
.linkset .LLNK249, printstrln.maxthreads - 1
.linkset .LLNK248, 1 + .LLNK249
.linkset .LLNK235, .LLNK236 $M .LLNK248
.linkset .LLNK251, XUD_PrintSetupPacket.maxthreads - 1
.linkset .LLNK250, 1 + .LLNK251
.linkset .LLNK234, .LLNK235 $M .LLNK250
.linkset .LLNK253, printstr.maxthreads - 1
.linkset .LLNK252, 1 + .LLNK253
.linkset .LLNK233, .LLNK234 $M .LLNK252
.linkset .LLNK255, printhexln.maxthreads - 1
.linkset .LLNK254, 1 + .LLNK255
.linkset .LLNK232, .LLNK233 $M .LLNK254
.linkset .LLNK257, printstrln.maxthreads - 1
.linkset .LLNK256, 1 + .LLNK257
.linkset .LLNK231, .LLNK232 $M .LLNK256
.linkset .LLNK259, printhexln.maxthreads - 1
.linkset .LLNK258, 1 + .LLNK259
.linkset .LLNK230, .LLNK231 $M .LLNK258
.linkset .LLNK261, printstrln.maxthreads - 1
.linkset .LLNK260, 1 + .LLNK261
.linkset .LLNK229, .LLNK230 $M .LLNK260
.linkset .LLNK263, printhexln.maxthreads - 1
.linkset .LLNK262, 1 + .LLNK263
.linkset .LLNK228, .LLNK229 $M .LLNK262
.linkset .LLNK265, storeShort.maxthreads - 1
.linkset .LLNK264, 1 + .LLNK265
.linkset .LLNK227, .LLNK228 $M .LLNK264
.linkset .LLNK267, storeShort.maxthreads - 1
.linkset .LLNK266, 1 + .LLNK267
.linkset .LLNK226, .LLNK227 $M .LLNK266
.linkset .LLNK269, storeShort.maxthreads - 1
.linkset .LLNK268, 1 + .LLNK269
.linkset .LLNK225, .LLNK226 $M .LLNK268
.linkset .LLNK271, storeShort.maxthreads - 1
.linkset .LLNK270, 1 + .LLNK271
.linkset .LLNK224, .LLNK225 $M .LLNK270
.linkset .LLNK273, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK272, 1 + .LLNK273
.linkset .LLNK223, .LLNK224 $M .LLNK272
.linkset .LLNK275, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK274, 1 + .LLNK275
.linkset .LLNK222, .LLNK223 $M .LLNK274
.linkset .LLNK277, XUD_Error_hex.maxthreads - 1
.linkset .LLNK276, 1 + .LLNK277
.linkset .LLNK221, .LLNK222 $M .LLNK276
.linkset .LLNK279, storeShort.maxthreads - 1
.linkset .LLNK278, 1 + .LLNK279
.linkset .LLNK220, .LLNK221 $M .LLNK278
.linkset .LLNK281, storeInt.maxthreads - 1
.linkset .LLNK280, 1 + .LLNK281
.linkset .LLNK219, .LLNK220 $M .LLNK280
.linkset .LLNK283, storeInt.maxthreads - 1
.linkset .LLNK282, 1 + .LLNK283
.linkset .LLNK218, .LLNK219 $M .LLNK282
.linkset .LLNK285, storeInt.maxthreads - 1
.linkset .LLNK284, 1 + .LLNK285
.linkset .LLNK217, .LLNK218 $M .LLNK284
.linkset .LLNK287, storeInt.maxthreads - 1
.linkset .LLNK286, 1 + .LLNK287
.linkset .LLNK216, .LLNK217 $M .LLNK286
.linkset .LLNK289, storeInt.maxthreads - 1
.linkset .LLNK288, 1 + .LLNK289
.linkset .LLNK215, .LLNK216 $M .LLNK288
.linkset .LLNK291, storeInt.maxthreads - 1
.linkset .LLNK290, 1 + .LLNK291
.linkset .LLNK214, .LLNK215 $M .LLNK290
.linkset .LLNK293, storeInt.maxthreads - 1
.linkset .LLNK292, 1 + .LLNK293
.linkset .LLNK213, .LLNK214 $M .LLNK292
.linkset .LLNK295, storeInt.maxthreads - 1
.linkset .LLNK294, 1 + .LLNK295
.linkset .LLNK212, .LLNK213 $M .LLNK294
.linkset .LLNK297, storeInt.maxthreads - 1
.linkset .LLNK296, 1 + .LLNK297
.linkset .LLNK211, .LLNK212 $M .LLNK296
.linkset .LLNK299, storeInt.maxthreads - 1
.linkset .LLNK298, 1 + .LLNK299
.linkset .LLNK210, .LLNK211 $M .LLNK298
.linkset .LLNK301, storeInt.maxthreads - 1
.linkset .LLNK300, 1 + .LLNK301
.linkset .LLNK209, .LLNK210 $M .LLNK300
.linkset .LLNK303, storeInt.maxthreads - 1
.linkset .LLNK302, 1 + .LLNK303
.linkset .LLNK208, .LLNK209 $M .LLNK302
.linkset .LLNK305, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK304, 1 + .LLNK305
.linkset .LLNK207, .LLNK208 $M .LLNK304
.linkset .LLNK307, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK306, 1 + .LLNK307
.linkset .LLNK206, .LLNK207 $M .LLNK306
.linkset .LLNK309, XUD_Error_hex.maxthreads - 1
.linkset .LLNK308, 1 + .LLNK309
.linkset .LLNK205, .LLNK206 $M .LLNK308
.linkset .LLNK311, XUD_Error.maxthreads - 1
.linkset .LLNK310, 1 + .LLNK311
.linkset .LLNK204, .LLNK205 $M .LLNK310
.linkset .LLNK313, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK312, 1 + .LLNK313
.linkset .LLNK203, .LLNK204 $M .LLNK312
.linkset .LLNK315, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK314, 1 + .LLNK315
.linkset .LLNK202, .LLNK203 $M .LLNK314
.linkset .LLNK317, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK316, 1 + .LLNK317
.linkset .LLNK201, .LLNK202 $M .LLNK316
.linkset .LLNK319, updateVol.maxthreads - 1
.linkset .LLNK318, 1 + .LLNK319
.linkset .LLNK200, .LLNK201 $M .LLNK318
.linkset .LLNK321, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK320, 1 + .LLNK321
.linkset .LLNK199, .LLNK200 $M .LLNK320
.linkset .LLNK323, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK322, 1 + .LLNK323
.linkset .LLNK198, .LLNK199 $M .LLNK322
.linkset .LLNK325, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK324, 1 + .LLNK325
.linkset .LLNK197, .LLNK198 $M .LLNK324
.linkset .LLNK327, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK326, 1 + .LLNK327
.linkset .LLNK196, .LLNK197 $M .LLNK326
.linkset .LLNK329, updateVol.maxthreads - 1
.linkset .LLNK328, 1 + .LLNK329
.linkset .LLNK195, .LLNK196 $M .LLNK328
.linkset .LLNK331, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK330, 1 + .LLNK331
.linkset .LLNK194, .LLNK195 $M .LLNK330
.linkset .LLNK333, XUD_Error.maxthreads - 1
.linkset .LLNK332, 1 + .LLNK333
.linkset .LLNK193, .LLNK194 $M .LLNK332
.linkset .LLNK335, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK334, 1 + .LLNK335
.linkset .LLNK192, .LLNK193 $M .LLNK334
.linkset .LLNK337, XUD_GetBuffer.maxthreads - 1
.linkset .LLNK336, 1 + .LLNK337
.linkset .LLNK191, .LLNK192 $M .LLNK336
.linkset .LLNK339, XUD_DoGetRequest.maxthreads - 1
.linkset .LLNK338, 1 + .LLNK339
.linkset .LLNK190, .LLNK191 $M .LLNK338
.linkset .LLNK341, XUD_GetBuffer_.maxthreads - 1
.linkset .LLNK340, 1 + .LLNK341
.linkset .LLNK189, .LLNK190 $M .LLNK340
.linkset .LLNK343, XUD_SetBuffer_ResetPid.maxthreads - 1
.linkset .LLNK342, 1 + .LLNK343
.linkset .LLNK188, .LLNK189 $M .LLNK342
.linkset .LLNK345, XUD_Error.maxthreads - 1
.linkset .LLNK344, 1 + .LLNK345
.linkset .LLNK187, .LLNK188 $M .LLNK344
.linkset .LLNK347, XUD_Error.maxthreads - 1
.linkset .LLNK346, 1 + .LLNK347
.linkset .LLNK186, .LLNK187 $M .LLNK346
.linkset .LLNK349, XUD_DoSetRequestStatus.maxthreads - 1
.linkset .LLNK348, 1 + .LLNK349
.linkset .LLNK185, .LLNK186 $M .LLNK348
.linkset .LLNK351, XUD_Error.maxthreads - 1
.linkset .LLNK350, 1 + .LLNK351
.linkset .LLNK184, .LLNK185 $M .LLNK350
.linkset .LLNK353, XUD_DoGetRequest.maxthreads - 1
.linkset .LLNK352, 1 + .LLNK353
.linkset .LLNK183, .LLNK184 $M .LLNK352
.linkset .LLNK355, XUD_Error.maxthreads - 1
.linkset .LLNK354, 1 + .LLNK355
.linkset .LLNK182, .LLNK183 $M .LLNK354
.linkset .LLNK357, XUD_DoSetRequestStatus.maxthreads - 1
.linkset .LLNK356, 1 + .LLNK357
.linkset .LLNK181, .LLNK182 $M .LLNK356
.linkset ep0_thread.maxthreads, .LLNK181
          .text
.LDBG420:
# Thread names for recovering thread graph in linker
.LDBG421:
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC16:
          .int      50
          .set      hidReportDesc.locbound,s.LC16
          .align    4
s.LC17:
          .int      9
          .set      strDescs.locbound,s.LC17
          .align    4
s.LC18:
          .int      5
          .set      strDesc_langIDs.locbound,s.LC18
          .align    4
s.LC19:
          .int      194
          .set      cfgDesc.locbound,s.LC19
          .align    4
s.LC20:
          .int      18
          .set      devDesc.locbound,s.LC20
          .align    4
s.LC21:
          .int      28
          .set      params.locbound,s.LC21
          .section  .debug_info,   "",    @progbits
.LDBG424:
          .int      .LDBG426-.LDBG425
.LDBG425:
          .short    0x0003
          .long     .LDBG422         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG421         # high address
          .asciiz   "./tests/audio/src/ep_thread0.xc"
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG423         # offset in .debug_lineprog
.LDBG427:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG428:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG429:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG430:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG431:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG432:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG433:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG434:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG435:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG436:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG437:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG438:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG439:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG440:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG441:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG442:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG443:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top params.data,params
.LDBG444:
          .uleb128  0x3
          .int      .LDBG430-.LDBG424
          .int      0x00000070
          .uleb128  0x4
          .byte     0x00
          .int      0x0000001b
          .byte     0x00
          .uleb128  0x5
          .asciiz   "params"
          .byte     0x01
          .short    0x0012
          .int      .LDBG444-.LDBG424
          .short    .LDBG446-.LDBG445
.LDBG445:
          .byte     0x03
          .int      params
.LDBG446:
.cc_bottom params.data
.cc_top updateMasterVol.function,updateMasterVol
.LDBG447:
          .uleb128  0x6
          .asciiz   "updateMasterVol"
          .byte     0x01
          .byte     0x18
          .byte     0x01
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG36          # high address
          .uleb128  0x7
          .asciiz   "tmp"
          .byte     0x01
          .short    0x0019
          .short    0x0000
          .int      .LDBG429-.LDBG424
          .int      .LDBG448
          .section  .debug_loc,    "",    @progbits
.cc_top updateMasterVol.function,updateMasterVol
.LDBG448:
          .int      .LDBG12-.LDBG0
          .int      .LDBG13-.LDBG0
          .short    .LDBG450-.LDBG449
.LDBG449:
          .byte     0x54
.LDBG450:
          .int      .LDBG9-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG452-.LDBG451
.LDBG451:
          .byte     0x54
.LDBG452:
          .int      0x00000000
          .int      0x00000000
.cc_bottom updateMasterVol.function
          .section  .debug_info,   "",    @progbits
.LDBG453:
          .uleb128  0x8
          .long     .LDBG5           # low address
          .long     .LDBG6           # high address
          .uleb128  0x7
          .asciiz   "i"
          .byte     0x01
          .short    0x001b
          .short    .LDBG454-.LDBG453
          .int      .LDBG429-.LDBG424
          .int      .LDBG455
          .section  .debug_loc,    "",    @progbits
.cc_top updateMasterVol.function,updateMasterVol
.LDBG455:
          .int      .LDBG12-.LDBG0
          .int      .LDBG13-.LDBG0
          .short    .LDBG457-.LDBG456
.LDBG456:
          .byte     0x53
.LDBG457:
          .int      .LDBG10-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG459-.LDBG458
.LDBG458:
          .byte     0x53
.LDBG459:
          .int      0x00000000
          .int      0x00000000
.cc_bottom updateMasterVol.function
          .section  .debug_info,   "",    @progbits
.LDBG454:
          .byte     0x00
          .byte     0x00
.cc_bottom updateMasterVol.function
.cc_top updateVol.function,updateVol
.LDBG460:
          .uleb128  0x6
          .asciiz   "updateVol"
          .byte     0x01
          .byte     0x22
          .byte     0x01
          .byte     0x01
          .long     .LDBG37          # low address
          .long     .LDBG60          # high address
          .uleb128  0x9
          .asciiz   "channel"
          .byte     0x01
          .short    0x0021
          .int      .LDBG429-.LDBG424
          .int      .LDBG461
          .section  .debug_loc,    "",    @progbits
.cc_top updateVol.function,updateVol
.LDBG461:
          .int      .LDBG40-.LDBG0
          .int      .LDBG41-.LDBG0
          .short    .LDBG463-.LDBG462
.LDBG462:
          .byte     0x50
.LDBG463:
          .int      .LDBG38-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG465-.LDBG464
.LDBG464:
          .byte     0x50
.LDBG465:
          .int      0x00000000
          .int      0x00000000
.cc_bottom updateVol.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom updateVol.function
.cc_top devDesc.data,devDesc
.LDBG466:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000012
          .uleb128  0x4
          .byte     0x00
          .int      0x00000011
          .byte     0x00
          .uleb128  0x5
          .asciiz   "devDesc"
          .byte     0x01
          .short    0x00d0
          .int      .LDBG466-.LDBG424
          .short    .LDBG468-.LDBG467
.LDBG467:
          .byte     0x03
          .int      devDesc
.LDBG468:
.cc_bottom devDesc.data
.cc_top cfgDesc.data,cfgDesc
.LDBG469:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x000000c2
          .uleb128  0x4
          .byte     0x00
          .int      0x000000c1
          .byte     0x00
          .uleb128  0x5
          .asciiz   "cfgDesc"
          .byte     0x01
          .short    0x00d1
          .int      .LDBG469-.LDBG424
          .short    .LDBG471-.LDBG470
.LDBG470:
          .byte     0x03
          .int      cfgDesc
.LDBG471:
.cc_bottom cfgDesc.data
.cc_top strDesc_langIDs.data,strDesc_langIDs
.LDBG472:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000005
          .uleb128  0x4
          .byte     0x00
          .int      0x00000004
          .byte     0x00
          .uleb128  0x5
          .asciiz   "strDesc_langIDs"
          .byte     0x01
          .short    0x00d4
          .int      .LDBG472-.LDBG424
          .short    .LDBG474-.LDBG473
.LDBG473:
          .byte     0x03
          .int      strDesc_langIDs
.LDBG474:
.cc_bottom strDesc_langIDs.data
.cc_top strDescs.data,strDescs
.LDBG475:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000028
          .uleb128  0x4
          .byte     0x00
          .int      0x00000027
          .byte     0x00
.LDBG476:
          .uleb128  0x3
          .int      .LDBG475-.LDBG424
          .int      0x00000168
          .uleb128  0x4
          .byte     0x00
          .int      0x00000008
          .byte     0x00
          .uleb128  0x5
          .asciiz   "strDescs"
          .byte     0x01
          .short    0x00d8
          .int      .LDBG476-.LDBG424
          .short    .LDBG478-.LDBG477
.LDBG477:
          .byte     0x03
          .int      strDescs
.LDBG478:
.cc_bottom strDescs.data
.cc_top hidReportDesc.data,hidReportDesc
.LDBG479:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000032
          .uleb128  0x4
          .byte     0x00
          .int      0x00000031
          .byte     0x00
          .uleb128  0x5
          .asciiz   "hidReportDesc"
          .byte     0x01
          .short    0x00e7
          .int      .LDBG479-.LDBG424
          .short    .LDBG481-.LDBG480
.LDBG480:
          .byte     0x03
          .int      hidReportDesc
.LDBG481:
.cc_bottom hidReportDesc.data
.cc_top storeInt.function,storeInt
.LDBG482:
          .uleb128  0x6
          .asciiz   "storeInt"
          .byte     0x01
          .byte     0xf2
          .byte     0x01
          .byte     0x01
          .long     .LDBG61          # low address
          .long     .LDBG84          # high address
.LDBG483:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000000
          .byte     0x00
.LDBG484:
          .uleb128  0xa
          .int      .LDBG483-.LDBG424
          .uleb128  0x9
          .asciiz   "buffer"
          .byte     0x01
          .short    0x00f1
          .int      .LDBG484-.LDBG424
          .int      .LDBG485
          .section  .debug_loc,    "",    @progbits
.cc_top storeInt.function,storeInt
.LDBG485:
          .int      .LDBG62-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG487-.LDBG486
.LDBG486:
          .byte     0x50
.LDBG487:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeInt.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "index"
          .byte     0x01
          .short    0x00f1
          .int      .LDBG429-.LDBG424
          .int      .LDBG488
          .section  .debug_loc,    "",    @progbits
.cc_top storeInt.function,storeInt
.LDBG488:
          .int      .LDBG63-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG490-.LDBG489
.LDBG489:
          .byte     0x51
.LDBG490:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeInt.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "val"
          .byte     0x01
          .short    0x00f1
          .int      .LDBG429-.LDBG424
          .int      .LDBG491
          .section  .debug_loc,    "",    @progbits
.cc_top storeInt.function,storeInt
.LDBG491:
          .int      .LDBG64-.LDBG0
          .int      .LDBG65-.LDBG0
          .short    .LDBG493-.LDBG492
.LDBG492:
          .byte     0x52
.LDBG493:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeInt.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom storeInt.function
.cc_top storeShort.function,storeShort
.LDBG494:
          .uleb128  0x6
          .asciiz   "storeShort"
          .byte     0x01
          .byte     0xfa
          .byte     0x01
          .byte     0x01
          .long     .LDBG85          # low address
          .long     .LDBG106         # high address
.LDBG495:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000000
          .byte     0x00
.LDBG496:
          .uleb128  0xa
          .int      .LDBG495-.LDBG424
          .uleb128  0x9
          .asciiz   "buffer"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG496-.LDBG424
          .int      .LDBG497
          .section  .debug_loc,    "",    @progbits
.cc_top storeShort.function,storeShort
.LDBG497:
          .int      .LDBG86-.LDBG0
          .int      .LDBG89-.LDBG0
          .short    .LDBG499-.LDBG498
.LDBG498:
          .byte     0x50
.LDBG499:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeShort.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "index"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG429-.LDBG424
          .int      .LDBG500
          .section  .debug_loc,    "",    @progbits
.cc_top storeShort.function,storeShort
.LDBG500:
          .int      .LDBG87-.LDBG0
          .int      .LDBG89-.LDBG0
          .short    .LDBG502-.LDBG501
.LDBG501:
          .byte     0x51
.LDBG502:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeShort.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "val"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG431-.LDBG424
          .int      .LDBG503
          .section  .debug_loc,    "",    @progbits
.cc_top storeShort.function,storeShort
.LDBG503:
          .int      .LDBG88-.LDBG0
          .int      .LDBG89-.LDBG0
          .short    .LDBG505-.LDBG504
.LDBG504:
          .byte     0x52
.LDBG505:
          .int      0x00000000
          .int      0x00000000
.cc_bottom storeShort.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom storeShort.function
.cc_top ep0_thread.function,ep0_thread
.LDBG506:
          .uleb128  0x6
          .asciiz   "ep0_thread"
          .byte     0x01
          .byte     0x00
          .byte     0x01
          .byte     0x01
          .long     .LDBG107         # low address
          .long     .LDBG420         # high address
          .uleb128  0x9
          .asciiz   "c_ep0"
          .byte     0x01
          .short    0x00ff
          .int      .LDBG435-.LDBG424
          .int      .LDBG507
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG507:
          .int      .LDBG227-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG509-.LDBG508
.LDBG508:
          .byte     0x58
.LDBG509:
          .int      .LDBG225-.LDBG0
          .int      .LDBG226-.LDBG0
          .short    .LDBG511-.LDBG510
.LDBG510:
          .byte     0x58
.LDBG511:
          .int      .LDBG221-.LDBG0
          .int      .LDBG222-.LDBG0
          .short    .LDBG513-.LDBG512
.LDBG512:
          .byte     0x58
.LDBG513:
          .int      .LDBG217-.LDBG0
          .int      .LDBG218-.LDBG0
          .short    .LDBG515-.LDBG514
.LDBG514:
          .byte     0x58
.LDBG515:
          .int      .LDBG219-.LDBG0
          .int      .LDBG220-.LDBG0
          .short    .LDBG517-.LDBG516
.LDBG516:
          .byte     0x58
.LDBG517:
          .int      .LDBG207-.LDBG0
          .int      .LDBG208-.LDBG0
          .short    .LDBG519-.LDBG518
.LDBG518:
          .byte     0x58
.LDBG519:
          .int      .LDBG213-.LDBG0
          .int      .LDBG214-.LDBG0
          .short    .LDBG521-.LDBG520
.LDBG520:
          .byte     0x58
.LDBG521:
          .int      .LDBG205-.LDBG0
          .int      .LDBG206-.LDBG0
          .short    .LDBG523-.LDBG522
.LDBG522:
          .byte     0x58
.LDBG523:
          .int      .LDBG215-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG525-.LDBG524
.LDBG524:
          .byte     0x58
.LDBG525:
          .int      .LDBG211-.LDBG0
          .int      .LDBG212-.LDBG0
          .short    .LDBG527-.LDBG526
.LDBG526:
          .byte     0x58
.LDBG527:
          .int      .LDBG209-.LDBG0
          .int      .LDBG210-.LDBG0
          .short    .LDBG529-.LDBG528
.LDBG528:
          .byte     0x58
.LDBG529:
          .int      .LDBG203-.LDBG0
          .int      .LDBG204-.LDBG0
          .short    .LDBG531-.LDBG530
.LDBG530:
          .byte     0x58
.LDBG531:
          .int      .LDBG201-.LDBG0
          .int      .LDBG202-.LDBG0
          .short    .LDBG533-.LDBG532
.LDBG532:
          .byte     0x58
.LDBG533:
          .int      .LDBG195-.LDBG0
          .int      .LDBG196-.LDBG0
          .short    .LDBG535-.LDBG534
.LDBG534:
          .byte     0x58
.LDBG535:
          .int      .LDBG199-.LDBG0
          .int      .LDBG200-.LDBG0
          .short    .LDBG537-.LDBG536
.LDBG536:
          .byte     0x58
.LDBG537:
          .int      .LDBG193-.LDBG0
          .int      .LDBG194-.LDBG0
          .short    .LDBG539-.LDBG538
.LDBG538:
          .byte     0x58
.LDBG539:
          .int      .LDBG197-.LDBG0
          .int      .LDBG198-.LDBG0
          .short    .LDBG541-.LDBG540
.LDBG540:
          .byte     0x58
.LDBG541:
          .int      .LDBG183-.LDBG0
          .int      .LDBG184-.LDBG0
          .short    .LDBG543-.LDBG542
.LDBG542:
          .byte     0x58
.LDBG543:
          .int      .LDBG189-.LDBG0
          .int      .LDBG190-.LDBG0
          .short    .LDBG545-.LDBG544
.LDBG544:
          .byte     0x58
.LDBG545:
          .int      .LDBG187-.LDBG0
          .int      .LDBG188-.LDBG0
          .short    .LDBG547-.LDBG546
.LDBG546:
          .byte     0x58
.LDBG547:
          .int      .LDBG191-.LDBG0
          .int      .LDBG192-.LDBG0
          .short    .LDBG549-.LDBG548
.LDBG548:
          .byte     0x58
.LDBG549:
          .int      .LDBG185-.LDBG0
          .int      .LDBG186-.LDBG0
          .short    .LDBG551-.LDBG550
.LDBG550:
          .byte     0x58
.LDBG551:
          .int      .LDBG181-.LDBG0
          .int      .LDBG182-.LDBG0
          .short    .LDBG553-.LDBG552
.LDBG552:
          .byte     0x58
.LDBG553:
          .int      .LDBG179-.LDBG0
          .int      .LDBG180-.LDBG0
          .short    .LDBG555-.LDBG554
.LDBG554:
          .byte     0x58
.LDBG555:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG557-.LDBG556
.LDBG556:
          .byte     0x58
.LDBG557:
          .int      .LDBG177-.LDBG0
          .int      .LDBG178-.LDBG0
          .short    .LDBG559-.LDBG558
.LDBG558:
          .byte     0x58
.LDBG559:
          .int      .LDBG175-.LDBG0
          .int      .LDBG176-.LDBG0
          .short    .LDBG561-.LDBG560
.LDBG560:
          .byte     0x58
.LDBG561:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG563-.LDBG562
.LDBG562:
          .byte     0x58
.LDBG563:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG565-.LDBG564
.LDBG564:
          .byte     0x58
.LDBG565:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG567-.LDBG566
.LDBG566:
          .byte     0x58
.LDBG567:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG569-.LDBG568
.LDBG568:
          .byte     0x58
.LDBG569:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG571-.LDBG570
.LDBG570:
          .byte     0x58
.LDBG571:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG573-.LDBG572
.LDBG572:
          .byte     0x58
.LDBG573:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG575-.LDBG574
.LDBG574:
          .byte     0x58
.LDBG575:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG577-.LDBG576
.LDBG576:
          .byte     0x58
.LDBG577:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG579-.LDBG578
.LDBG578:
          .byte     0x58
.LDBG579:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG581-.LDBG580
.LDBG580:
          .byte     0x58
.LDBG581:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG583-.LDBG582
.LDBG582:
          .byte     0x58
.LDBG583:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG585-.LDBG584
.LDBG584:
          .byte     0x58
.LDBG585:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG587-.LDBG586
.LDBG586:
          .byte     0x58
.LDBG587:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG589-.LDBG588
.LDBG588:
          .byte     0x58
.LDBG589:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG591-.LDBG590
.LDBG590:
          .byte     0x58
.LDBG591:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG593-.LDBG592
.LDBG592:
          .byte     0x58
.LDBG593:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG595-.LDBG594
.LDBG594:
          .byte     0x58
.LDBG595:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG597-.LDBG596
.LDBG596:
          .byte     0x58
.LDBG597:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG599-.LDBG598
.LDBG598:
          .byte     0x58
.LDBG599:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG601-.LDBG600
.LDBG600:
          .byte     0x58
.LDBG601:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG603-.LDBG602
.LDBG602:
          .byte     0x58
.LDBG603:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG605-.LDBG604
.LDBG604:
          .byte     0x58
.LDBG605:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG607-.LDBG606
.LDBG606:
          .byte     0x58
.LDBG607:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG609-.LDBG608
.LDBG608:
          .byte     0x58
.LDBG609:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG611-.LDBG610
.LDBG610:
          .byte     0x58
.LDBG611:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG613-.LDBG612
.LDBG612:
          .byte     0x58
.LDBG613:
          .int      .LDBG112-.LDBG0
          .int      .LDBG114-.LDBG0
          .short    .LDBG615-.LDBG614
.LDBG614:
          .byte     0x58
.LDBG615:
          .int      .LDBG119-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG617-.LDBG616
.LDBG616:
          .byte     0x58
.LDBG617:
          .int      .LDBG117-.LDBG0
          .int      .LDBG118-.LDBG0
          .short    .LDBG619-.LDBG618
.LDBG618:
          .byte     0x58
.LDBG619:
          .int      .LDBG115-.LDBG0
          .int      .LDBG116-.LDBG0
          .short    .LDBG621-.LDBG620
.LDBG620:
          .byte     0x58
.LDBG621:
          .int      .LDBG251-.LDBG0
          .int      .LDBG252-.LDBG0
          .short    .LDBG623-.LDBG622
.LDBG622:
          .byte     0x58
.LDBG623:
          .int      .LDBG247-.LDBG0
          .int      .LDBG248-.LDBG0
          .short    .LDBG625-.LDBG624
.LDBG624:
          .byte     0x58
.LDBG625:
          .int      .LDBG245-.LDBG0
          .int      .LDBG246-.LDBG0
          .short    .LDBG627-.LDBG626
.LDBG626:
          .byte     0x58
.LDBG627:
          .int      .LDBG249-.LDBG0
          .int      .LDBG250-.LDBG0
          .short    .LDBG629-.LDBG628
.LDBG628:
          .byte     0x58
.LDBG629:
          .int      .LDBG241-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG631-.LDBG630
.LDBG630:
          .byte     0x58
.LDBG631:
          .int      .LDBG233-.LDBG0
          .int      .LDBG234-.LDBG0
          .short    .LDBG633-.LDBG632
.LDBG632:
          .byte     0x58
.LDBG633:
          .int      .LDBG243-.LDBG0
          .int      .LDBG244-.LDBG0
          .short    .LDBG635-.LDBG634
.LDBG634:
          .byte     0x58
.LDBG635:
          .int      .LDBG237-.LDBG0
          .int      .LDBG238-.LDBG0
          .short    .LDBG637-.LDBG636
.LDBG636:
          .byte     0x58
.LDBG637:
          .int      .LDBG235-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG639-.LDBG638
.LDBG638:
          .byte     0x58
.LDBG639:
          .int      .LDBG239-.LDBG0
          .int      .LDBG240-.LDBG0
          .short    .LDBG641-.LDBG640
.LDBG640:
          .byte     0x58
.LDBG641:
          .int      .LDBG229-.LDBG0
          .int      .LDBG230-.LDBG0
          .short    .LDBG643-.LDBG642
.LDBG642:
          .byte     0x58
.LDBG643:
          .int      .LDBG231-.LDBG0
          .int      .LDBG232-.LDBG0
          .short    .LDBG645-.LDBG644
.LDBG644:
          .byte     0x58
.LDBG645:
          .int      .LDBG223-.LDBG0
          .int      .LDBG224-.LDBG0
          .short    .LDBG647-.LDBG646
.LDBG646:
          .byte     0x58
.LDBG647:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "c_ep0_con"
          .byte     0x01
          .short    0x00ff
          .int      .LDBG435-.LDBG424
          .int      .LDBG648
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG648:
          .int      .LDBG229-.LDBG0
          .int      .LDBG230-.LDBG0
          .short    .LDBG650-.LDBG649
.LDBG649:
          .byte     0x57
.LDBG650:
          .int      .LDBG223-.LDBG0
          .int      .LDBG224-.LDBG0
          .short    .LDBG652-.LDBG651
.LDBG651:
          .byte     0x57
.LDBG652:
          .int      .LDBG225-.LDBG0
          .int      .LDBG226-.LDBG0
          .short    .LDBG654-.LDBG653
.LDBG653:
          .byte     0x57
.LDBG654:
          .int      .LDBG221-.LDBG0
          .int      .LDBG222-.LDBG0
          .short    .LDBG656-.LDBG655
.LDBG655:
          .byte     0x57
.LDBG656:
          .int      .LDBG219-.LDBG0
          .int      .LDBG220-.LDBG0
          .short    .LDBG658-.LDBG657
.LDBG657:
          .byte     0x57
.LDBG658:
          .int      .LDBG207-.LDBG0
          .int      .LDBG208-.LDBG0
          .short    .LDBG660-.LDBG659
.LDBG659:
          .byte     0x57
.LDBG660:
          .int      .LDBG203-.LDBG0
          .int      .LDBG204-.LDBG0
          .short    .LDBG662-.LDBG661
.LDBG661:
          .byte     0x57
.LDBG662:
          .int      .LDBG215-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG664-.LDBG663
.LDBG663:
          .byte     0x57
.LDBG664:
          .int      .LDBG213-.LDBG0
          .int      .LDBG214-.LDBG0
          .short    .LDBG666-.LDBG665
.LDBG665:
          .byte     0x57
.LDBG666:
          .int      .LDBG211-.LDBG0
          .int      .LDBG212-.LDBG0
          .short    .LDBG668-.LDBG667
.LDBG667:
          .byte     0x57
.LDBG668:
          .int      .LDBG217-.LDBG0
          .int      .LDBG218-.LDBG0
          .short    .LDBG670-.LDBG669
.LDBG669:
          .byte     0x57
.LDBG670:
          .int      .LDBG205-.LDBG0
          .int      .LDBG206-.LDBG0
          .short    .LDBG672-.LDBG671
.LDBG671:
          .byte     0x57
.LDBG672:
          .int      .LDBG209-.LDBG0
          .int      .LDBG210-.LDBG0
          .short    .LDBG674-.LDBG673
.LDBG673:
          .byte     0x57
.LDBG674:
          .int      .LDBG195-.LDBG0
          .int      .LDBG196-.LDBG0
          .short    .LDBG676-.LDBG675
.LDBG675:
          .byte     0x57
.LDBG676:
          .int      .LDBG193-.LDBG0
          .int      .LDBG194-.LDBG0
          .short    .LDBG678-.LDBG677
.LDBG677:
          .byte     0x57
.LDBG678:
          .int      .LDBG201-.LDBG0
          .int      .LDBG202-.LDBG0
          .short    .LDBG680-.LDBG679
.LDBG679:
          .byte     0x57
.LDBG680:
          .int      .LDBG199-.LDBG0
          .int      .LDBG200-.LDBG0
          .short    .LDBG682-.LDBG681
.LDBG681:
          .byte     0x57
.LDBG682:
          .int      .LDBG197-.LDBG0
          .int      .LDBG198-.LDBG0
          .short    .LDBG684-.LDBG683
.LDBG683:
          .byte     0x57
.LDBG684:
          .int      .LDBG189-.LDBG0
          .int      .LDBG190-.LDBG0
          .short    .LDBG686-.LDBG685
.LDBG685:
          .byte     0x57
.LDBG686:
          .int      .LDBG185-.LDBG0
          .int      .LDBG186-.LDBG0
          .short    .LDBG688-.LDBG687
.LDBG687:
          .byte     0x57
.LDBG688:
          .int      .LDBG183-.LDBG0
          .int      .LDBG184-.LDBG0
          .short    .LDBG690-.LDBG689
.LDBG689:
          .byte     0x57
.LDBG690:
          .int      .LDBG187-.LDBG0
          .int      .LDBG188-.LDBG0
          .short    .LDBG692-.LDBG691
.LDBG691:
          .byte     0x57
.LDBG692:
          .int      .LDBG191-.LDBG0
          .int      .LDBG192-.LDBG0
          .short    .LDBG694-.LDBG693
.LDBG693:
          .byte     0x57
.LDBG694:
          .int      .LDBG181-.LDBG0
          .int      .LDBG182-.LDBG0
          .short    .LDBG696-.LDBG695
.LDBG695:
          .byte     0x57
.LDBG696:
          .int      .LDBG173-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG698-.LDBG697
.LDBG697:
          .byte     0x57
.LDBG698:
          .int      .LDBG171-.LDBG0
          .int      .LDBG172-.LDBG0
          .short    .LDBG700-.LDBG699
.LDBG699:
          .byte     0x57
.LDBG700:
          .int      .LDBG179-.LDBG0
          .int      .LDBG180-.LDBG0
          .short    .LDBG702-.LDBG701
.LDBG701:
          .byte     0x57
.LDBG702:
          .int      .LDBG177-.LDBG0
          .int      .LDBG178-.LDBG0
          .short    .LDBG704-.LDBG703
.LDBG703:
          .byte     0x57
.LDBG704:
          .int      .LDBG175-.LDBG0
          .int      .LDBG176-.LDBG0
          .short    .LDBG706-.LDBG705
.LDBG705:
          .byte     0x57
.LDBG706:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG708-.LDBG707
.LDBG707:
          .byte     0x57
.LDBG708:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG710-.LDBG709
.LDBG709:
          .byte     0x57
.LDBG710:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG712-.LDBG711
.LDBG711:
          .byte     0x57
.LDBG712:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG714-.LDBG713
.LDBG713:
          .byte     0x57
.LDBG714:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG716-.LDBG715
.LDBG715:
          .byte     0x57
.LDBG716:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG718-.LDBG717
.LDBG717:
          .byte     0x57
.LDBG718:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG720-.LDBG719
.LDBG719:
          .byte     0x57
.LDBG720:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG722-.LDBG721
.LDBG721:
          .byte     0x57
.LDBG722:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG724-.LDBG723
.LDBG723:
          .byte     0x57
.LDBG724:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG726-.LDBG725
.LDBG725:
          .byte     0x57
.LDBG726:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG728-.LDBG727
.LDBG727:
          .byte     0x57
.LDBG728:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG730-.LDBG729
.LDBG729:
          .byte     0x57
.LDBG730:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG732-.LDBG731
.LDBG731:
          .byte     0x57
.LDBG732:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG734-.LDBG733
.LDBG733:
          .byte     0x57
.LDBG734:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG736-.LDBG735
.LDBG735:
          .byte     0x57
.LDBG736:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG738-.LDBG737
.LDBG737:
          .byte     0x57
.LDBG738:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG740-.LDBG739
.LDBG739:
          .byte     0x57
.LDBG740:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG742-.LDBG741
.LDBG741:
          .byte     0x57
.LDBG742:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG744-.LDBG743
.LDBG743:
          .byte     0x57
.LDBG744:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG746-.LDBG745
.LDBG745:
          .byte     0x57
.LDBG746:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG748-.LDBG747
.LDBG747:
          .byte     0x57
.LDBG748:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG750-.LDBG749
.LDBG749:
          .byte     0x57
.LDBG750:
          .int      .LDBG115-.LDBG0
          .int      .LDBG116-.LDBG0
          .short    .LDBG752-.LDBG751
.LDBG751:
          .byte     0x57
.LDBG752:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG754-.LDBG753
.LDBG753:
          .byte     0x57
.LDBG754:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG756-.LDBG755
.LDBG755:
          .byte     0x57
.LDBG756:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG758-.LDBG757
.LDBG757:
          .byte     0x57
.LDBG758:
          .int      .LDBG119-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG760-.LDBG759
.LDBG759:
          .byte     0x57
.LDBG760:
          .int      .LDBG113-.LDBG0
          .int      .LDBG114-.LDBG0
          .short    .LDBG762-.LDBG761
.LDBG761:
          .byte     0x57
.LDBG762:
          .int      .LDBG117-.LDBG0
          .int      .LDBG118-.LDBG0
          .short    .LDBG764-.LDBG763
.LDBG763:
          .byte     0x57
.LDBG764:
          .int      .LDBG251-.LDBG0
          .int      .LDBG252-.LDBG0
          .short    .LDBG766-.LDBG765
.LDBG765:
          .byte     0x57
.LDBG766:
          .int      .LDBG243-.LDBG0
          .int      .LDBG244-.LDBG0
          .short    .LDBG768-.LDBG767
.LDBG767:
          .byte     0x57
.LDBG768:
          .int      .LDBG247-.LDBG0
          .int      .LDBG248-.LDBG0
          .short    .LDBG770-.LDBG769
.LDBG769:
          .byte     0x57
.LDBG770:
          .int      .LDBG249-.LDBG0
          .int      .LDBG250-.LDBG0
          .short    .LDBG772-.LDBG771
.LDBG771:
          .byte     0x57
.LDBG772:
          .int      .LDBG245-.LDBG0
          .int      .LDBG246-.LDBG0
          .short    .LDBG774-.LDBG773
.LDBG773:
          .byte     0x57
.LDBG774:
          .int      .LDBG239-.LDBG0
          .int      .LDBG240-.LDBG0
          .short    .LDBG776-.LDBG775
.LDBG775:
          .byte     0x57
.LDBG776:
          .int      .LDBG233-.LDBG0
          .int      .LDBG234-.LDBG0
          .short    .LDBG778-.LDBG777
.LDBG777:
          .byte     0x57
.LDBG778:
          .int      .LDBG237-.LDBG0
          .int      .LDBG238-.LDBG0
          .short    .LDBG780-.LDBG779
.LDBG779:
          .byte     0x57
.LDBG780:
          .int      .LDBG235-.LDBG0
          .int      .LDBG236-.LDBG0
          .short    .LDBG782-.LDBG781
.LDBG781:
          .byte     0x57
.LDBG782:
          .int      .LDBG241-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG784-.LDBG783
.LDBG783:
          .byte     0x57
.LDBG784:
          .int      .LDBG231-.LDBG0
          .int      .LDBG232-.LDBG0
          .short    .LDBG786-.LDBG785
.LDBG785:
          .byte     0x57
.LDBG786:
          .int      .LDBG227-.LDBG0
          .int      .LDBG228-.LDBG0
          .short    .LDBG788-.LDBG787
.LDBG787:
          .byte     0x57
.LDBG788:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x7
          .asciiz   "datalength"
          .byte     0x01
          .short    0x0101
          .short    0x0000
          .int      .LDBG429-.LDBG424
          .int      .LDBG789
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG789:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x7
          .asciiz   "j"
          .byte     0x01
          .short    0x0102
          .short    0x0000
          .int      .LDBG429-.LDBG424
          .int      .LDBG790
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG790:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
.LDBG791:
          .uleb128  0x3
          .int      .LDBG434-.LDBG424
          .int      0x00000400
          .uleb128  0x4
          .byte     0x00
          .int      0x000003ff
          .byte     0x00
          .uleb128  0x7
          .asciiz   "buffer"
          .byte     0x01
          .short    0x0103
          .short    0x0000
          .int      .LDBG791-.LDBG424
          .int      .LDBG792
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG792:
          .int      .LDBG110-.LDBG0
          .int      .LDBG111-.LDBG0
          .short    .LDBG794-.LDBG793
.LDBG793:
          .byte     0x7e
          .sleb128  0x1c
.LDBG794:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
.LDBG795:
          .uleb128  0xb
          .asciiz   "setupPacket"
          .int      0x0000000a
.LDBG796:
          .uleb128  0xb
          .asciiz   "bmRequestType"
          .int      0x00000003
          .uleb128  0xc
          .asciiz   "Recipient"
          .byte     0x00
          .short    0x0018
          .int      .LDBG434-.LDBG424
          .uleb128  0x0
          .uleb128  0xc
          .asciiz   "Type"
          .byte     0x00
          .short    0x001d
          .int      .LDBG434-.LDBG424
          .uleb128  0x1
          .uleb128  0xc
          .asciiz   "Direction"
          .byte     0x00
          .short    0x0020
          .int      .LDBG434-.LDBG424
          .uleb128  0x2
          .byte     0x00
          .uleb128  0xc
          .asciiz   "bmRequestType"
          .byte     0x00
          .short    0x002a
          .int      .LDBG796-.LDBG424
          .uleb128  0x0
          .uleb128  0xc
          .asciiz   "bRequest"
          .byte     0x00
          .short    0x002b
          .int      .LDBG434-.LDBG424
          .uleb128  0x3
          .uleb128  0xc
          .asciiz   "wValue"
          .byte     0x00
          .short    0x002c
          .int      .LDBG432-.LDBG424
          .uleb128  0x4
          .uleb128  0xc
          .asciiz   "wIndex"
          .byte     0x00
          .short    0x002d
          .int      .LDBG432-.LDBG424
          .uleb128  0x6
          .uleb128  0xc
          .asciiz   "wLength"
          .byte     0x00
          .short    0x002e
          .int      .LDBG432-.LDBG424
          .uleb128  0x8
          .byte     0x00
          .uleb128  0x7
          .asciiz   "sp"
          .byte     0x01
          .short    0x0104
          .short    0x0000
          .int      .LDBG795-.LDBG424
          .int      .LDBG797
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG797:
          .int      .LDBG110-.LDBG0
          .int      .LDBG111-.LDBG0
          .short    .LDBG799-.LDBG798
.LDBG798:
          .byte     0x7e
          .sleb128  0x41c
.LDBG799:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x7
          .asciiz   "loop"
          .byte     0x01
          .short    0x0105
          .short    0x0000
          .int      .LDBG429-.LDBG424
          .int      .LDBG800
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG800:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x7
          .asciiz   "pid"
          .byte     0x01
          .short    0x0106
          .short    0x0000
          .int      .LDBG434-.LDBG424
          .int      .LDBG801
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG801:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x7
          .asciiz   "i_tmp"
          .byte     0x01
          .short    0x0107
          .short    0x0000
          .int      .LDBG429-.LDBG424
          .int      .LDBG802
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG802:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
.LDBG803:
          .uleb128  0x8
          .long     .LDBG108         # low address
          .long     .LDBG109         # high address
          .uleb128  0x7
          .asciiz   "retVal"
          .byte     0x01
          .short    0x0110
          .short    .LDBG804-.LDBG803
          .int      .LDBG429-.LDBG424
          .int      .LDBG805
          .section  .debug_loc,    "",    @progbits
.cc_top ep0_thread.function,ep0_thread
.LDBG805:
          .int      0x00000000
          .int      0x00000000
.cc_bottom ep0_thread.function
          .section  .debug_info,   "",    @progbits
.LDBG804:
          .byte     0x00
          .byte     0x00
.cc_bottom ep0_thread.function
          .byte     0x00
.LDBG426:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG807-.LDBG806
.LDBG806:
          .short    0x0002
          .long     .LDBG424         # offset in .debug_info
          .int      .LDBG426-.LDBG424
.cc_top updateMasterVol.function,updateMasterVol
          .int      .LDBG447-.LDBG424
          .asciiz   "updateMasterVol"
.cc_bottom updateMasterVol.function
.cc_top updateVol.function,updateVol
          .int      .LDBG460-.LDBG424
          .asciiz   "updateVol"
.cc_bottom updateVol.function
.cc_top storeInt.function,storeInt
          .int      .LDBG482-.LDBG424
          .asciiz   "storeInt"
.cc_bottom storeInt.function
.cc_top storeShort.function,storeShort
          .int      .LDBG494-.LDBG424
          .asciiz   "storeShort"
.cc_bottom storeShort.function
.cc_top ep0_thread.function,ep0_thread
          .int      .LDBG506-.LDBG424
          .asciiz   "ep0_thread"
.cc_bottom ep0_thread.function
          .int      0x00000000
.LDBG807:
          .section  .debug_line,   "",    @progbits
.LDBG423:
          .int      .LDBG809-.LDBG808
.LDBG808:
          .short    0x0003
          .int      .LDBG811-.LDBG810
.LDBG810:
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
          .asciiz   "./tests/audio/src/ep_thread0.xc"
          .uleb128  0x0
          .uleb128  0x49b91aba
          .uleb128  0x8227
          .byte     0x00
.LDBG811:
.cc_top updateMasterVol.function, updateMasterVol
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG17
          .byte     0x03
          .sleb128  0x17
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG22
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG23
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG25
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG26
          .byte     0x03
          .sleb128  0xfffffffe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG28
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom updateMasterVol.function
.cc_top updateVol.function, updateVol
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG46
          .byte     0x03
          .sleb128  0x21
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG50
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG51
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG52
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG56
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom updateVol.function
.cc_top storeInt.function, storeInt
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG67
          .byte     0x03
          .sleb128  0xf1
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG72
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG73
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG74
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG75
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG76
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom storeInt.function
.cc_top storeShort.function, storeShort
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG91
          .byte     0x03
          .sleb128  0xf9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG96
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom storeShort.function
.cc_top ep0_thread.function, ep0_thread
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG255
          .byte     0x03
          .sleb128  0xff
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG258
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG259
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG261
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG262
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG264
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG266
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG267
          .byte     0x03
          .sleb128  0xfffffff9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG268
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG269
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG270
          .byte     0x03
          .sleb128  0x184
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG272
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG274
          .byte     0x03
          .sleb128  0xfffffe7f
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG275
          .byte     0x03
          .sleb128  0x3c
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG277
          .byte     0x03
          .sleb128  0xffffffc8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG278
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG280
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG281
          .byte     0x03
          .sleb128  0xa
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG283
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG285
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG286
          .byte     0x03
          .sleb128  0xc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG288
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG290
          .byte     0x03
          .sleb128  0x15
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG291
          .byte     0x03
          .sleb128  0x132
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG293
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG295
          .byte     0x03
          .sleb128  0xfffffed3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG296
          .byte     0x03
          .sleb128  0x125
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG298
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG300
          .byte     0x03
          .sleb128  0xfffffee0
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG301
          .byte     0x03
          .sleb128  0x87
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG303
          .byte     0x03
          .sleb128  0xffffff7f
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG304
          .byte     0x03
          .sleb128  0x3e
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG306
          .byte     0x03
          .sleb128  0xffffffc8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG308
          .byte     0x03
          .sleb128  0x21
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG309
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG311
          .byte     0x03
          .sleb128  0xffffffdf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG313
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG314
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG315
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG316
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG318
          .byte     0x03
          .sleb128  0xfffffff4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG320
          .byte     0x03
          .sleb128  0x24
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG321
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG323
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG325
          .byte     0x03
          .sleb128  0x11
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG326
          .byte     0x03
          .sleb128  0x2f
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG328
          .byte     0x03
          .sleb128  0xffffffd5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG330
          .byte     0x03
          .sleb128  0xc
          .byte     0x01
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
          .int      .LDBG334
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG336
          .byte     0x03
          .sleb128  0xfffffff3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG338
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG339
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG341
          .byte     0x03
          .sleb128  0x3
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
          .int      .LDBG345
          .byte     0x03
          .sleb128  0xc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG346
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG348
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG350
          .byte     0x03
          .sleb128  0xfffffff4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG352
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG353
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG355
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG357
          .byte     0x03
          .sleb128  0x21
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG358
          .byte     0x03
          .sleb128  0x3e
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG360
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG362
          .byte     0x03
          .sleb128  0xffffffc6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG363
          .byte     0x03
          .sleb128  0x1a
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG365
          .byte     0x03
          .sleb128  0xffffffea
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG367
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG369
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG371
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
          .int      .LDBG377
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG379
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG381
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG387
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG389
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG391
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG393
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG395
          .byte     0x03
          .sleb128  0xc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG396
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG398
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG400
          .byte     0x03
          .sleb128  0xfffffff3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG402
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG404
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
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG410
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG412
          .byte     0x03
          .sleb128  0x7a
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG414
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG416
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom ep0_thread.function
.LDBG809:
          .section  .debug_abbrev, "",    @progbits
.LDBG422:
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
          .uleb128  0x5
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
          .uleb128  0x9
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
          .uleb128  0x7
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
          .uleb128  0x8
          .byte     0x0b
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0xa
          .byte     0x10
          .byte     0x00
          .byte     0x49
          .byte     0x13
          .byte     0x00
          .byte     0x00
          .uleb128  0x3
          .byte     0x01
          .byte     0x01
          .byte     0x49
          .byte     0x13
          .byte     0x0b
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x4
          .byte     0x21
          .byte     0x00
          .byte     0x22
          .byte     0x0b
          .byte     0x2f
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0xb
          .byte     0x13
          .byte     0x01
          .byte     0x03
          .byte     0x08
          .byte     0x0b
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0xc
          .byte     0x0d
          .byte     0x00
          .byte     0x03
          .byte     0x08
          .byte     0x3a
          .byte     0x0b
          .byte     0x3b
          .byte     0x05
          .byte     0x49
          .byte     0x13
          .byte     0x38
          .byte     0x0f
          .byte     0x00
          .byte     0x00

          .byte     0x00
          .section .xtalabeltable,       "", @progbits
.L159:
          .int      .L160-.L159
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
.cc_top ep0_thread.function, ep0_thread
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000002a7
          .int      0x000002af
          .long    .L145
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000010d
          .int      0x0000010d
          .long    .L144
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000029b
          .int      0x0000029c
          .long    .L143
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000292
          .int      0x00000293
          .long    .L142
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000028b
          .int      0x0000028c
          .long    .L141
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000023b
          .int      0x0000023c
          .long    .L140
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000232
          .int      0x00000233
          .long    .L139
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000226
          .int      0x0000022d
          .long    .L138
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000222
          .int      0x00000222
          .long    .L137
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000021c
          .int      0x0000021c
          .long    .L136
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000206
          .int      0x00000216
          .long    .L135
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000202
          .int      0x00000202
          .long    .L134
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001fd
          .int      0x000001fd
          .long    .L133
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001f3
          .int      0x000001f3
          .long    .L132
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001e9
          .int      0x000001e9
          .long    .L131
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001e0
          .int      0x000001e3
          .long    .L130
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001d7
          .int      0x000001dc
          .long    .L129
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001d4
          .int      0x000001d4
          .long    .L128
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001ca
          .int      0x000001ce
          .long    .L127
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001c1
          .int      0x000001c6
          .long    .L126
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001be
          .int      0x000001be
          .long    .L125
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001ba
          .int      0x000001ba
          .long    .L124
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001b0
          .int      0x000001b0
          .long    .L123
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000001a3
          .int      0x000001a9
          .long    .L122
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000199
          .int      0x0000019c
          .long    .L121
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000183
          .int      0x0000018b
          .long    .L120
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000017f
          .int      0x0000017f
          .long    .L119
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000017b
          .int      0x0000017d
          .long    .L118
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000178
          .int      0x00000178
          .long    .L117
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000172
          .int      0x00000172
          .long    .L116
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000016c
          .int      0x0000016c
          .long    .L115
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000166
          .int      0x00000166
          .long    .L114
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000160
          .int      0x00000160
          .long    .L113
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000157
          .int      0x00000157
          .long    .L112
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000014f
          .int      0x0000014f
          .long    .L111
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000014b
          .int      0x0000014b
          .long    .L110
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000143
          .int      0x00000143
          .long    .L109
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000138
          .int      0x00000138
          .long    .L108
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000134
          .int      0x00000134
          .long    .L107
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000012e
          .int      0x0000012e
          .long    .L106
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000127
          .int      0x00000127
          .long    .L105
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000011f
          .int      0x0000011f
          .long    .L104
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000011b
          .int      0x0000011b
          .long    .L103
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000117
          .int      0x00000117
          .long    .L102
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000114
          .int      0x00000114
          .long    .L101
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000112
          .int      0x00000112
          .long    .L100
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000110
          .int      0x00000111
          .long    .L99
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000100
          .int      0x0000010d
          .long    .L98
.cc_bottom ep0_thread.function
.cc_top storeShort.function, storeShort
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000000fa
          .int      0x000000fd
          .long    .L18
.cc_bottom storeShort.function
.cc_top storeInt.function, storeInt
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x000000f2
          .int      0x000000f7
          .long    .L17
.cc_bottom storeInt.function
.cc_top updateVol.function, updateVol
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L16
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000027
          .int      0x00000027
          .long    .L15
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000024
          .int      0x00000024
          .long    .L14
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000022
          .int      0x00000023
          .long    .L13
.cc_bottom updateVol.function
.cc_top updateMasterVol.function, updateMasterVol
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000001f
          .int      0x0000001f
          .long    .L9
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000001b
          .int      0x0000001b
          .long    .L8
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x0000001d
          .int      0x0000001b
          .long    .L7
          .asciiz  "./tests/audio/src/ep_thread0.xc"
          .int      0x00000018
          .int      0x0000001b
          .long    .L6
.cc_bottom updateMasterVol.function
.L160:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
