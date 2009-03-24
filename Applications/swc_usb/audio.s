          .file     "./tests/audio/src/audio.xc"
          .section .netinfo,       "", @netinfo
          .int      0x1eaba15c
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
.extern GetParam, "f{si}(si)"
.extern GetBufferAddress, "f{ui}(&(a(:si)))"
.extern Endpoint0Wrapper, "f{si}(chd,chd)"
.extern ep1_thread, "f{0}(0)"
          .text
.LDBG4:
.globl ep3dead, "f{0}(0)"
.globl ep3dead.nstackwords
.globl ep3dead.maxthreads
.globl ep3dead.maxtimers
.globl ep3dead.maxchanends
.globl ep3dead.maxsync
.type  ep3dead, @function
.linkset ep3dead.locnoside, 1
.linkset ep3dead.locnochandec, 1
.linkset ep3dead.nstackwords, 0
.cc_top ep3dead.function,ep3dead
.LDBG8:
.LDBG9:                              # line (30)
          .align    4
ep3dead:
.LDBG10:
.L5:
.LDBG11:                             # line (30)
.LDBG12:                             # line (31)
.LDBG5:
.L4:
.L6:
.LDBG13:                             # line (31)
          bu        .L4 
.LDBG6:
.L7:
.LDBG14:                             # line (32)
.LDBG7:
.cc_bottom ep3dead.function
.LDBG15:
          .section  .debug_frame, "",     @progbits
.cc_top ep3dead.function,ep3dead
          .align    4
          .int      .LDBG17-.LDBG16
.LDBG16:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG8
          .int      .LDBG15-.LDBG8
          .byte     0x01
          .int      .LDBG10
          .align    4, 0
.LDBG17:
.cc_bottom ep3dead.function
.linkset ep3dead.maxchanends, 0
.linkset ep3dead.maxtimers, 0
.linkset ep3dead.maxthreads, 1
          .text
.LDBG18:
.LDBG19:
          .section .dp.data,       "adw", @progbits
.cc_top s.LC0.data, s.LC0
          .align    4
s.LC0:
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
          .byte     0x00
.cc_bottom s.LC0.data
.call XUD_Endpoint3, XUD_SetBuffer_
          .align    4
          .text
.globl XUD_Endpoint3, "f{si}(chd,i:p)"
.globl XUD_Endpoint3.nstackwords
.globl XUD_Endpoint3.maxthreads
.globl XUD_Endpoint3.maxtimers
.globl XUD_Endpoint3.maxchanends
.globl XUD_Endpoint3.maxsync
.type  XUD_Endpoint3, @function
.linkset XUD_Endpoint3.locnoside, 0
.linkset XUD_Endpoint3.locnochandec, 1
.linkset .LLNK2, __crt_memcpy.nstackwords $M XUD_SetBuffer_.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 4
.linkset XUD_Endpoint3.nstackwords, .LLNK0
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG34:
.LDBG35:                             # line (38)
          .align    4
XUD_Endpoint3:
          entsp     0x4 
.LDBG36:
.L18:
          mov       r4, r0
.LDBG22:
          mov       r5, r1
.LDBG23:
.LDBG37:                             # line (38)
.LDBG20:
.LDBG38:                             # line (39)
.LDBG39:                             # line (44)
          ldaw      r0, sp[0x2] 
          ldaw      r1, dp[s.LC0] 
          ldc       r2, 0x8
          bl        __crt_memcpy 
.LDBG40:                             # line (46)
          setc      res[r5], 0x1
.L24:
          in        r0, res[r5] 
.LDBG24:
.LDBG41:                             # line (48)
.LDBG25:
.LDBG26:
.L11:
.L19:
.LDBG42:                             # line (50)
.LDBG43:                             # line (51)
.LDBG44:                             # line (51)
.LDBG27:
.LDBG28:
.L16:
.L20:
.L25:
          in        r0, res[r5] 
.LDBG29:
.LDBG45:                             # line (55)
          not       r1, r0
          ldc       r0, 0x7
          and       r2, r1, r0
          ldaw      r1, sp[0x2] 
          ldc       r0, 0x0
          st8       r2, r1[r0] 
.LDBG46:                             # line (57)
          ldc       r1, 0x2
          ldaw      r2, sp[0x2] 
          ldc       r3, 0x4
          mov       r0, r4
          ldc       r6, 0x8
          stw       r6, sp[0x1] 
.LDBG47:                             # line (57)
          bl        XUD_SetBuffer_ 
.L21:
.LDBG48:                             # line (51)
          ldw       r11, cp[s.LC1]
          .section .cp.const4,     "acM", @progbits, 4
s.LC1:
          .align    4
          .int      0xffffffff
          .text
          eq        r0, r0, r11
          bf        r0, .L16 
.LDBG30:
.LDBG31:
.L22:
.LDBG49:                             # line (48)
          bu        .L11 
.LDBG32:
.L23:
.LDBG50:                             # line (61)
.LDBG21:
.LDBG33:
.cc_bottom XUD_Endpoint3.function
.LDBG51:
          .section  .debug_frame, "",     @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
          .align    4
          .int      .LDBG53-.LDBG52
.LDBG52:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG34
          .int      .LDBG51-.LDBG34
          .byte     0x01
          .int      .LDBG36
          .byte     0x0e
          .uleb128  0x10
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .align    4, 0
.LDBG53:
.cc_bottom XUD_Endpoint3.function
.linkset .LLNK3, __crt_memcpy.maxchanends $M XUD_SetBuffer_.maxchanends
.linkset XUD_Endpoint3.maxchanends, .LLNK3
.linkset .LLNK4, __crt_memcpy.maxtimers $M XUD_SetBuffer_.maxtimers
.linkset XUD_Endpoint3.maxtimers, .LLNK4
.linkset .LLNK8, __crt_memcpy.maxthreads - 1
.linkset .LLNK7, 1 + .LLNK8
.linkset .LLNK6, 1 $M .LLNK7
.linkset .LLNK10, XUD_SetBuffer_.maxthreads - 1
.linkset .LLNK9, 1 + .LLNK10
.linkset .LLNK5, .LLNK6 $M .LLNK9
.linkset XUD_Endpoint3.maxthreads, .LLNK5
          .text
.LDBG54:
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC2:
          .int      8
          .set      buffer.locbound,s.LC2
.extern lrand48, "f{sl}(0)"
          .text
.LDBG55:
.call dummyEP, __builtin_inct
.call dummyEP, __builtin_stop_streaming_slave
.call dummyEP, __builtin_stop_streaming_slave
.set __builtin_inct, 0
.linkset __builtin_inct.locnoside, 0
.linkset __builtin_inct.locnochandec, 1
.set __builtin_stop_streaming_slave, 0
.linkset __builtin_stop_streaming_slave.locnoside, 0
.linkset __builtin_stop_streaming_slave.locnochandec, 1
.globl dummyEP, "f{si}(chd,chd)"
.globl dummyEP.nstackwords
.globl dummyEP.maxthreads
.globl dummyEP.maxtimers
.globl dummyEP.maxchanends
.globl dummyEP.maxsync
.type  dummyEP, @function
.linkset dummyEP.locnoside, 1
.linkset dummyEP.locnochandec, 1
.linkset dummyEP.nstackwords, 0
.cc_top dummyEP.function,dummyEP
.LDBG62:
.LDBG63:                             # line (69)
          .align    4
dummyEP:
.LDBG64:
.L31:
.LDBG56:
.LDBG57:
.LDBG65:                             # line (69)
.LDBG66:                             # line (70)
.LDBG58:
.LDBG59:
.L29:
.L32:
.LDBG67:                             # line (72)
.LDBG68:                             # line (72)
.L35:
          inct      r2, res[r1] 
.LDBG69:                             # line (73)
.LDBG70:                             # line (73)
.L36:
          outct     res[r0], 0x1 
          chkct     res[r0], 0x1 
.LDBG71:                             # line (74)
.LDBG72:                             # line (74)
.L37:
          outct     res[r1], 0x1 
          chkct     res[r1], 0x1 
.L33:
.LDBG73:                             # line (70)
          bu        .L29 
.LDBG60:
.L34:
.LDBG74:                             # line (76)
.LDBG61:
.cc_bottom dummyEP.function
.LDBG75:
          .section  .debug_frame, "",     @progbits
.cc_top dummyEP.function,dummyEP
          .align    4
          .int      .LDBG77-.LDBG76
.LDBG76:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG62
          .int      .LDBG75-.LDBG62
          .byte     0x01
          .int      .LDBG64
          .align    4, 0
.LDBG77:
.cc_bottom dummyEP.function
.linkset dummyEP.maxchanends, 0
.linkset dummyEP.maxtimers, 0
.linkset dummyEP.maxthreads, 1
          .text
.LDBG78:
.LDBG79:
.call doClock, __builtin_testct
.call doClock, __builtin_inct
.call doClock, __builtin_in_uint_byref
.set __builtin_testct, 0
.linkset __builtin_testct.locnoside, 0
.linkset __builtin_testct.locnochandec, 1
.set __builtin_in_uint_byref, 0
.linkset __builtin_in_uint_byref.locnoside, 0
.linkset __builtin_in_uint_byref.locnochandec, 1
.globl doClock, "f{0}(chd,o:p)"
.globl doClock.nstackwords
.globl doClock.maxthreads
.globl doClock.maxtimers
.globl doClock.maxchanends
.globl doClock.maxsync
.type  doClock, @function
.linkset doClock.locnoside, 0
.linkset doClock.locnochandec, 1
.linkset doClock.nstackwords, 0
.cc_top doClock.function,doClock
.LDBG108:
.LDBG109:                            # line (81)
          .align    4
doClock:
.LDBG110:
.L48:
.LDBG82:
.LDBG83:
.LDBG111:                            # line (81)
.LDBG80:
.LDBG112:                            # line (82)
          ldc       r4, 0x0
.LDBG84:
          getr      r3, 0x1
.LDBG85:
.LDBG113:                            # line (85)
          ldc       r2, 0x0
.LDBG86:
.LDBG114:                            # line (88)
.L55:
          out       res[r1], r4 
.LDBG115:                            # line (89)
          setc      res[r3], 0x1
.L56:
          in        r3, res[r3] 
.LDBG87:
.LDBG116:                            # line (91)
.LDBG88:
.LDBG89:
.L41:
.L49:
.LDBG117:                            # line (93)
.LDBG118:                            # line (93)
          testct    r3, res[r0] 
          bt        r3, .L44 
.LDBG90:
.LDBG91:
.L51:
.LDBG119:                            # line (96)
.LDBG120:                            # line (96)
.L57:
          in        r3, res[r0] 
.LDBG92:
          add       r2, r2, 0x1
.LDBG121:                            # line (97)
.LDBG122:                            # line (98)
          eq        r3, r2, 0x5
          bt        r3, .L47 
.LDBG93:
.LDBG94:
.LDBG95:
.LDBG96:
.L42:
.L53:
.LDBG123:                            # line (91)
          bu        .L41 
.LDBG97:
.LDBG98:
.LDBG99:
.LDBG100:
.L44:
.L50:
.LDBG124:                            # line (94)
.LDBG125:                            # line (94)
.L58:
          inct      r3, res[r0] 
          bu        .L42 
.LDBG101:
.LDBG102:
.LDBG103:
.LDBG104:
.L47:
.L52:
.LDBG126:                            # line (100)
          eq        r4, r4, 0x0
.LDBG127:                            # line (101)
.L59:
          out       res[r1], r4 
.LDBG128:                            # line (102)
          ldc       r2, 0x0
.LDBG105:
          bu        .L42 
.LDBG106:
.LDBG81:
.L54:
.LDBG129:                            # line (107)
.LDBG107:
.cc_bottom doClock.function
.LDBG130:
          .section  .debug_frame, "",     @progbits
.cc_top doClock.function,doClock
          .align    4
          .int      .LDBG132-.LDBG131
.LDBG131:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG108
          .int      .LDBG130-.LDBG108
          .byte     0x01
          .int      .LDBG110
          .align    4, 0
.LDBG132:
.cc_bottom doClock.function
.linkset doClock.maxchanends, 0
.linkset doClock.maxtimers, 1
.linkset doClock.maxthreads, 1
          .text
.LDBG133:
.extern GetInt, "f{si}(ui,si)"
.globl data, "si"
.type  data, @object
          .section .dp.bss,        "adw", @nobits
.cc_top data.data, data
          .align    4
data:
          .int      0x00000000
.cc_bottom data.data
          .align    4
          .text
.LDBG134:
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "./tests/audio/src/audio.xc:124: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "./tests/audio/src/audio.xc:130: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "./tests/audio/src/audio.xc:136: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.set __builtin_out_uint, 0
.linkset __builtin_out_uint.locnoside, 0
.linkset __builtin_out_uint.locnochandec, 1
.globl readData, "f{si}(chd,&(a(:si)),&(a(:si)),&(a(:si)),chd)"
.globl readData.nstackwords
.globl readData.maxthreads
.globl readData.maxtimers
.globl readData.maxchanends
.globl readData.maxsync
.type  readData, @function
.linkset readData.locnoside, 0
.linkset readData.locnochandec, 1
.linkset .LLNK14, XUD_GetBuffer_int.nstackwords $M XUD_GetBuffer_int.nstackwords
.linkset .LLNK13, .LLNK14 $M XUD_GetBuffer_int.nstackwords
.linkset .LLNK12, .LLNK13 $M .LLNK13
.linkset .LLNK11, .LLNK12 + 4
.linkset readData.nstackwords, .LLNK11
.cc_top readData.function,readData
.LDBG165:
.LDBG166:                            # line (116)
          .align    4
readData:
          entsp     0x4 
.LDBG167:
.L76:
          mov       r10, r0
.LDBG137:
          stw       r1, sp[0x1] 
          stw       r2, sp[0x2] 
          stw       r3, sp[0x3] 
          ldw       r9, sp[0x5] 
.LDBG138:
          ldw       r8, sp[0x6] 
          ldw       r7, sp[0x7] 
          ldw       r4, sp[0x8] 
.LDBG168:                            # line (116)
.LDBG135:
.LDBG169:                            # line (118)
.LDBG139:
.LDBG140:
.L63:
.L77:
.LDBG170:                            # line (119)
.LDBG141:
.LDBG142:
.L68:
.L78:
          ldc       r6, 0x1
.LDBG171:                            # line (122)
          mov       r1, r6
          ldw       r2, sp[0x1] 
          mov       r0, r10
          mov       r3, r8
.LDBG172:                            # line (122)
          bl        XUD_GetBuffer_int 
.LDBG173:                            # line (123)
          ldw       r11, cp[s.LC1]
          mov       r5, r11
.LDBG174:                            # line (123)
          eq        r1, r0, r5
          bt        r1, .L71 
.LDBG143:
.LDBG144:
.L80:
.LDBG175:                            # line (124)
          ldc       r1, 0x0

          stw       r1, dp[data] 
.LDBG176:                            # line (125)
          ashr      r0, r0, 0x2
.LDBG177:                            # line (125)
.L88:
          out       res[r9], r0 
.LDBG178:                            # line (126)
          ldc       r0, 0x0
.LDBG179:                            # line (126)
.L89:
          out       res[r9], r0 
.LDBG180:                            # line (128)
          ldw       r2, sp[0x2] 
          mov       r0, r10
          mov       r1, r6
          mov       r3, r7
.LDBG181:                            # line (128)
          bl        XUD_GetBuffer_int 
.LDBG182:                            # line (129)
.LDBG183:                            # line (129)
          eq        r1, r0, r5
          bt        r1, .L73 
.LDBG145:
.LDBG146:
.L82:
.LDBG184:                            # line (130)
          ldc       r1, 0x1

          stw       r1, dp[data] 
.LDBG185:                            # line (131)
          ashr      r0, r0, 0x2
.LDBG186:                            # line (131)
.L90:
          out       res[r9], r0 
.LDBG187:                            # line (132)
.LDBG188:                            # line (132)
.L91:
          out       res[r9], r6 
.LDBG189:                            # line (134)
          ldw       r2, sp[0x3] 
          mov       r0, r10
          mov       r1, r6
          mov       r3, r4
.LDBG190:                            # line (134)
          bl        XUD_GetBuffer_int 
.LDBG191:                            # line (135)
.LDBG192:                            # line (135)
          eq        r1, r0, r5
          bt        r1, .L75 
.LDBG147:
.LDBG148:
.L84:
.LDBG193:                            # line (136)
          ldc       r1, 0x2

          stw       r1, dp[data] 
.LDBG194:                            # line (137)
          ashr      r0, r0, 0x2
.LDBG195:                            # line (137)
.L92:
          out       res[r9], r0 
.LDBG196:                            # line (138)
          ldc       r0, 0x2
.LDBG197:                            # line (138)
.L93:
          out       res[r9], r0 
.L85:
.LDBG198:                            # line (119)
          bu        .L68 
.LDBG149:
.LDBG150:
.LDBG151:
.LDBG152:
.L71:
.L79:
.LDBG199:                            # line (123)
.LDBG153:
.LDBG154:
.L67:
.L86:
.LDBG200:                            # line (118)
          bu        .L63 
.LDBG155:
.LDBG156:
.LDBG157:
.LDBG158:
.L73:
.L81:
.LDBG201:                            # line (129)
          bu        .L67 
.LDBG159:
.LDBG160:
.LDBG161:
.LDBG162:
.L75:
.L83:
.LDBG202:                            # line (135)
          bu        .L67 
.LDBG163:
.LDBG136:
.L87:
.LDBG203:                            # line (142)
.LDBG164:
.cc_bottom readData.function
.LDBG204:
          .section  .debug_frame, "",     @progbits
.cc_top readData.function,readData
          .align    4
          .int      .LDBG206-.LDBG205
.LDBG205:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG165
          .int      .LDBG204-.LDBG165
          .byte     0x01
          .int      .LDBG167
          .byte     0x0e
          .uleb128  0x10
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .align    4, 0
.LDBG206:
.cc_bottom readData.function
.linkset .LLNK16, XUD_GetBuffer_int.maxchanends $M XUD_GetBuffer_int.maxchanends
.linkset .LLNK15, .LLNK16 $M XUD_GetBuffer_int.maxchanends
.linkset readData.maxchanends, .LLNK15
.linkset .LLNK18, XUD_GetBuffer_int.maxtimers $M XUD_GetBuffer_int.maxtimers
.linkset .LLNK17, .LLNK18 $M XUD_GetBuffer_int.maxtimers
.linkset readData.maxtimers, .LLNK17
.linkset .LLNK23, XUD_GetBuffer_int.maxthreads - 1
.linkset .LLNK22, 1 + .LLNK23
.linkset .LLNK21, 1 $M .LLNK22
.linkset .LLNK25, XUD_GetBuffer_int.maxthreads - 1
.linkset .LLNK24, 1 + .LLNK25
.linkset .LLNK20, .LLNK21 $M .LLNK24
.linkset .LLNK27, XUD_GetBuffer_int.maxthreads - 1
.linkset .LLNK26, 1 + .LLNK27
.linkset .LLNK19, .LLNK20 $M .LLNK26
.linkset readData.maxthreads, .LLNK19
          .text
.LDBG207:
.extern swc_audio_codec_ctl_reg_wr, "f{si}(si,si,si,p,p)"
.locl lrtime, "ss"
          .section .dp.bss,        "adw", @nobits
.cc_top lrtime.data, lrtime
          .align    4
lrtime:
          .short    0x0000
.cc_bottom lrtime.data
.extern GetData, "f{si}(0)"
          .align    4
          .text
.LDBG208:
.locl value, "ui"
          .section .dp.data,       "adw", @progbits
.cc_top value.stat.0.data, value.stat.0
          .align    4
value.stat.0:
          .int      0x00000000
.cc_bottom value.stat.0.data
.locl value2, "ui"
.cc_top value2.stat.1.data, value2.stat.1
          .align    4
value2.stat.1:
          .int      0x00000000
.cc_bottom value2.stat.1.data
.locl value3, "ui"
.cc_top value3.stat.2.data, value3.stat.2
          .align    4
value3.stat.2:
          .int      0x00000000
.cc_bottom value3.stat.2.data
.locl value4, "ui"
.cc_top value4.stat.3.data, value4.stat.3
          .align    4
value4.stat.3:
          .int      0x00000000
.cc_bottom value4.stat.3.data
.locl value5, "ui"
.cc_top value5.stat.4.data, value5.stat.4
          .align    4
value5.stat.4:
          .int      0x00000000
.cc_bottom value5.stat.4.data
.locl value6, "ui"
.cc_top value6.stat.5.data, value6.stat.5
          .align    4
value6.stat.5:
          .int      0x00000000
.cc_bottom value6.stat.5.data
.locl value7, "ui"
.cc_top value7.stat.6.data, value7.stat.6
          .align    4
value7.stat.6:
          .int      0x00000000
.cc_bottom value7.stat.6.data
.locl value8, "ui"
.cc_top value8.stat.7.data, value8.stat.7
          .align    4
value8.stat.7:
          .int      0x00000000
.cc_bottom value8.stat.7.data
.call deliver, GetData
.call deliver, __builtin_pins_ne
.globread deliver, isLeft, "./tests/audio/src/audio.xc:175: error: previously used here"
.globread deliver, lrtime, "./tests/audio/src/audio.xc:177: error: previously used here"
.globwrite deliver, lrtime, "./tests/audio/src/audio.xc:181: error: previously used here"
.globread deliver, isLeft, "./tests/audio/src/audio.xc:183: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value.stat.0, "./tests/audio/src/audio.xc:187: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value3.stat.2, "./tests/audio/src/audio.xc:189: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value5.stat.4, "./tests/audio/src/audio.xc:191: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value7.stat.6, "./tests/audio/src/audio.xc:193: error: previously used here"
.globread deliver, value.stat.0, "./tests/audio/src/audio.xc:197: error: previously used here"
.globread deliver, value3.stat.2, "./tests/audio/src/audio.xc:198: error: previously used here"
.globread deliver, value5.stat.4, "./tests/audio/src/audio.xc:199: error: previously used here"
.globread deliver, value7.stat.6, "./tests/audio/src/audio.xc:200: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value2.stat.1, "./tests/audio/src/audio.xc:203: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value4.stat.3, "./tests/audio/src/audio.xc:205: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value6.stat.5, "./tests/audio/src/audio.xc:207: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value8.stat.7, "./tests/audio/src/audio.xc:209: error: previously used here"
.call deliver, GetData
.call deliver, GetParam
.globread deliver, value2.stat.1, "./tests/audio/src/audio.xc:235: error: previously used here"
.globread deliver, value4.stat.3, "./tests/audio/src/audio.xc:236: error: previously used here"
.globread deliver, value6.stat.5, "./tests/audio/src/audio.xc:237: error: previously used here"
.globread deliver, value8.stat.7, "./tests/audio/src/audio.xc:238: error: previously used here"
.set __builtin_pins_ne, 0
.linkset __builtin_pins_ne.locnoside, 0
.linkset __builtin_pins_ne.locnochandec, 1
.set __builtin_macs, 0
.linkset __builtin_macs.locnoside, 1
.linkset __builtin_macs.locnochandec, 1
.set __builtin_bitrev, 0
.linkset __builtin_bitrev.locnoside, 1
.linkset __builtin_bitrev.locnochandec, 1
          .align    4
          .text
.type  deliver, @function
.linkset deliver.locnoside, 0
.linkset deliver.locnochandec, 1
.linkset .LLNK47, GetData.nstackwords $M GetInt.nstackwords
.linkset .LLNK46, .LLNK47 $M GetParam.nstackwords
.linkset .LLNK45, .LLNK46 $M GetInt.nstackwords
.linkset .LLNK44, .LLNK45 $M GetParam.nstackwords
.linkset .LLNK43, .LLNK44 $M GetInt.nstackwords
.linkset .LLNK42, .LLNK43 $M GetParam.nstackwords
.linkset .LLNK41, .LLNK42 $M GetInt.nstackwords
.linkset .LLNK40, .LLNK41 $M GetParam.nstackwords
.linkset .LLNK39, .LLNK40 $M GetInt.nstackwords
.linkset .LLNK38, .LLNK39 $M GetParam.nstackwords
.linkset .LLNK37, .LLNK38 $M GetInt.nstackwords
.linkset .LLNK36, .LLNK37 $M GetParam.nstackwords
.linkset .LLNK35, .LLNK36 $M GetInt.nstackwords
.linkset .LLNK34, .LLNK35 $M GetParam.nstackwords
.linkset .LLNK33, .LLNK34 $M GetInt.nstackwords
.linkset .LLNK32, .LLNK33 $M GetParam.nstackwords
.linkset .LLNK31, .LLNK32 $M GetData.nstackwords
.linkset .LLNK30, .LLNK31 $M GetParam.nstackwords
.linkset .LLNK29, .LLNK30 $M .LLNK30
.linkset .LLNK28, .LLNK29 + 15
.linkset deliver.nstackwords, .LLNK28
.cc_top deliver.function,deliver
.LDBG248:
.LDBG249:                            # line (163)
          .align    4
deliver:
          entsp     0xf 
.LDBG250:
.L111:
          stw       r4, sp[0x3] 
.LDBG251:
          stw       r5, sp[0x4] 
.LDBG252:
          stw       r6, sp[0x5] 
.LDBG253:
          stw       r7, sp[0x6] 
.LDBG254:
          stw       r8, sp[0x7] 
.LDBG255:
          stw       r9, sp[0x8] 
.LDBG256:
          stw       r10, sp[0x9] 
.LDBG257:
          stw       r0, sp[0x1] 
          mov       r5, r1
.LDBG211:
          mov       r9, r2
.LDBG212:
          stw       r3, sp[0x2] 
          ldw       r8, sp[0x11] 
.LDBG213:
          ldw       r7, sp[0x12] 
.LDBG214:
.LDBG258:                            # line (163)
          ldc       r4, 0x0
.LDBG209:
.LDBG259:                            # line (165)
          mov       r6, r4
.LDBG215:
.LDBG260:                            # line (169)
          bl        GetData 
          stw       r0, sp[0xa] 
.LDBG261:                            # line (173)
.LDBG216:
.LDBG217:
.L97:
.L112:
.LDBG262:                            # line (175)
          ldw       r0, dp[isLeft] 
.LDBG263:                            # line (175)
          setd      res[r9], r0
          ldc       r10, 0x19
          setc      res[r9], r10
.L124:
          in        r0, res[r9] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          getts     r1, res[r9] 
.LDBG218:
.LDBG264:                            # line (177)
          ldw       r2, dp[lrtime] 
          sext      r2, 0x10 
          ldc       r0, 0x40
          add       r0, r2, r0
          sext      r0, 0x10 
          sub       r0, r0, r1
          sext      r0, 0x10 
.LDBG265:                            # line (177)
          ldc       r2, 0x0
          lss       r0, r0, r2
          bt        r0, .L100 
.LDBG219:
.LDBG220:
.L114:
.LDBG266:                            # line (181)
          sext      r1, 0x10 

          stw       r1, dp[lrtime] 
.LDBG267:                            # line (183)
          ldw       r0, dp[isLeft] 
          bt        r0, .L102 
.LDBG221:
.LDBG222:
.L121:
.LDBG268:                            # line (235)
          ldw       r1, dp[value2.stat.1] 
          ldw       r0, sp[0x2] 
.L125:
          out       res[r0], r1 
.LDBG269:                            # line (236)
          ldw       r1, dp[value4.stat.3] 
          ldw       r0, sp[0x10] 
.L126:
          out       res[r0], r1 
.LDBG270:                            # line (237)
          ldw       r0, dp[value6.stat.5] 
.L127:
          out       res[r8], r0 
.LDBG271:                            # line (238)
          ldw       r0, dp[value8.stat.7] 
.L128:
          out       res[r7], r0 
.LDBG223:
.LDBG224:
.L98:
.L122:
.LDBG272:                            # line (173)
          bu        .L97 
.LDBG225:
.LDBG226:
.L100:
.L113:
.LDBG273:                            # line (179)
          ldw       r11, cp[s.LC1]
          mov       r0, r11
.LDBG274:
          ldw       r4, sp[0x3] 
.LDBG275:
          ldw       r5, sp[0x4] 
.LDBG276:
          ldw       r6, sp[0x5] 
.LDBG277:
          ldw       r7, sp[0x6] 
.LDBG278:
          ldw       r8, sp[0x7] 
.LDBG279:
          ldw       r9, sp[0x8] 
.LDBG280:
          ldw       r10, sp[0x9] 
.LDBG281:
          retsp     0xf 
.LDBG227:
.LDBG282:
.LDBG228:
.LDBG229:
.LDBG230:
.L102:
.L115:
.LDBG283:                            # line (186)
          mov       r0, r5
          mov       r1, r6
.LDBG284:                            # line (186)
          bl        GetInt 
          stw       r0, sp[0xb] 
.LDBG285:                            # line (186)
          ldc       r0, 0x13
.LDBG286:                            # line (186)
          bl        GetParam 
.LDBG287:                            # line (186)
          ldc       r1, 0x0
          mov       r2, r4
          ldw       r3, sp[0xb] 
          maccs     r1, r2, r3, r0

          stw       r1, dp[value.stat.0] 
.LDBG288:                            # line (187)
.LDBG289:                            # line (187)
          ldw       r0, dp[value.stat.0] 
          bitrev    r0, r0

          stw       r0, dp[value.stat.0] 
.LDBG290:                            # line (188)
          mov       r0, r5
          add       r1, r6, 0x2
.LDBG291:                            # line (188)
          bl        GetInt 
          stw       r0, sp[0xc] 
.LDBG292:                            # line (188)
          ldc       r0, 0x15
.LDBG293:                            # line (188)
          bl        GetParam 
.LDBG294:                            # line (188)
          ldc       r1, 0x0
          mov       r2, r4
          ldw       r3, sp[0xc] 
          maccs     r1, r2, r3, r0

          stw       r1, dp[value3.stat.2] 
.LDBG295:                            # line (189)
.LDBG296:                            # line (189)
          ldw       r0, dp[value3.stat.2] 
          bitrev    r0, r0

          stw       r0, dp[value3.stat.2] 
.LDBG297:                            # line (190)
          add       r1, r6, 0x4
          mov       r0, r5
.LDBG298:                            # line (190)
          bl        GetInt 
          stw       r0, sp[0xd] 
.LDBG299:                            # line (190)
          ldc       r0, 0x17
.LDBG300:                            # line (190)
          bl        GetParam 
.LDBG301:                            # line (190)
          ldc       r2, 0x0
          mov       r1, r4
          ldw       r3, sp[0xd] 
          maccs     r2, r1, r3, r0

          stw       r2, dp[value5.stat.4] 
.LDBG302:                            # line (191)
.LDBG303:                            # line (191)
          ldw       r0, dp[value5.stat.4] 
          bitrev    r0, r0

          stw       r0, dp[value5.stat.4] 
.LDBG304:                            # line (192)
          mov       r0, r5
          add       r1, r6, 0x6
.LDBG305:                            # line (192)
          bl        GetInt 
          stw       r0, sp[0xe] 
.LDBG306:                            # line (192)
          mov       r0, r10
.LDBG307:                            # line (192)
          bl        GetParam 
.LDBG308:                            # line (192)
          ldc       r1, 0x0
          mov       r2, r4
          ldw       r3, sp[0xe] 
          maccs     r1, r2, r3, r0

          stw       r1, dp[value7.stat.6] 
.LDBG309:                            # line (193)
.LDBG310:                            # line (193)
          ldw       r0, dp[value7.stat.6] 
          bitrev    r0, r0

          stw       r0, dp[value7.stat.6] 
.LDBG311:                            # line (197)
          ldw       r1, dp[value.stat.0] 
          ldw       r0, sp[0x2] 
.L129:
          out       res[r0], r1 
.LDBG312:                            # line (198)
          ldw       r1, dp[value3.stat.2] 
          ldw       r0, sp[0x10] 
.L130:
          out       res[r0], r1 
.LDBG313:                            # line (199)
          ldw       r0, dp[value5.stat.4] 
.L131:
          out       res[r8], r0 
.LDBG314:                            # line (200)
          ldw       r0, dp[value7.stat.6] 
.L132:
          out       res[r7], r0 
.LDBG315:                            # line (202)
          mov       r0, r5
          add       r1, r6, 0x1
.LDBG316:                            # line (202)
          bl        GetInt 
          mov       r10, r0
.LDBG317:                            # line (202)
          ldc       r0, 0x14
.LDBG318:                            # line (202)
          bl        GetParam 
.LDBG319:                            # line (202)
          ldc       r2, 0x0
          mov       r1, r4
          maccs     r2, r1, r10, r0

          stw       r2, dp[value2.stat.1] 
.LDBG320:                            # line (203)
.LDBG321:                            # line (203)
          ldw       r0, dp[value2.stat.1] 
          bitrev    r0, r0

          stw       r0, dp[value2.stat.1] 
.LDBG322:                            # line (204)
          mov       r0, r5
          add       r1, r6, 0x3
.LDBG323:                            # line (204)
          bl        GetInt 
          mov       r10, r0
.LDBG324:                            # line (204)
          ldc       r0, 0x16
.LDBG325:                            # line (204)
          bl        GetParam 
.LDBG326:                            # line (204)
          ldc       r1, 0x0
          mov       r2, r4
          maccs     r1, r2, r10, r0

          stw       r1, dp[value4.stat.3] 
.LDBG327:                            # line (205)
.LDBG328:                            # line (205)
          ldw       r0, dp[value4.stat.3] 
          bitrev    r0, r0

          stw       r0, dp[value4.stat.3] 
.LDBG329:                            # line (206)
          mov       r0, r5
          add       r1, r6, 0x5
.LDBG330:                            # line (206)
          bl        GetInt 
          mov       r10, r0
.LDBG331:                            # line (206)
          ldc       r0, 0x18
.LDBG332:                            # line (206)
          bl        GetParam 
.LDBG333:                            # line (206)
          ldc       r1, 0x0
          mov       r2, r4
          maccs     r1, r2, r10, r0

          stw       r1, dp[value6.stat.5] 
.LDBG334:                            # line (207)
.LDBG335:                            # line (207)
          ldw       r0, dp[value6.stat.5] 
          bitrev    r0, r0

          stw       r0, dp[value6.stat.5] 
.LDBG336:                            # line (208)
          mov       r0, r5
          add       r1, r6, 0x7
.LDBG337:                            # line (208)
          bl        GetInt 
          mov       r10, r0
.LDBG338:                            # line (208)
          ldc       r0, 0x1a
.LDBG339:                            # line (208)
          bl        GetParam 
.LDBG340:                            # line (208)
          ldc       r2, 0x0
          mov       r1, r4
          maccs     r2, r1, r10, r0

          stw       r2, dp[value8.stat.7] 
.LDBG341:                            # line (209)
.LDBG342:                            # line (209)
          ldw       r0, dp[value8.stat.7] 
          bitrev    r0, r0

          stw       r0, dp[value8.stat.7] 
          add       r0, r6, 0x8
.LDBG343:                            # line (217)
.LDBG344:                            # line (217)
          ldw       r1, sp[0x1] 
          lsu       r1, r0, r1
          bt        r1, .L105 
.LDBG231:
.LDBG232:
.L117:
.LDBG345:                            # line (224)
          bl        GetData 
.LDBG346:                            # line (224)
          ldw       r1, sp[0xa] 
          eq        r0, r0, r1
          bf        r0, .L108 
.LDBG233:
.LDBG234:
.L119:
.LDBG347:                            # line (226)
          ldc       r0, 0x9
.LDBG348:                            # line (226)
          bl        GetParam 
.LDBG349:                            # line (226)
          ldw       r1, sp[0x13] 
          eq        r0, r0, r1
          bf        r0, .L110 
.LDBG235:
.LDBG236:
          bu        .L98 
.LDBG237:
.LDBG238:
.LDBG239:
.LDBG240:
.L105:
.L116:
.LDBG350:                            # line (219)
          mov       r6, r0
.LDBG241:
          bu        .L98 
.LDBG242:
.LDBG243:
.L108:
.L118:
.LDBG351:                            # line (225)
          ldc       r0, 0x1
.LDBG352:
          ldw       r4, sp[0x3] 
.LDBG353:
          ldw       r5, sp[0x4] 
.LDBG354:
          ldw       r6, sp[0x5] 
.LDBG355:
          ldw       r7, sp[0x6] 
.LDBG356:
          ldw       r8, sp[0x7] 
.LDBG357:
          ldw       r9, sp[0x8] 
.LDBG358:
          ldw       r10, sp[0x9] 
.LDBG359:
          retsp     0xf 
.LDBG244:
.LDBG360:
.LDBG245:
.L110:
.L120:
.LDBG361:                            # line (227)
          ldw       r11, cp[s.LC1]
          mov       r0, r11
.LDBG362:
          ldw       r4, sp[0x3] 
.LDBG363:
          ldw       r5, sp[0x4] 
.LDBG364:
          ldw       r6, sp[0x5] 
.LDBG365:
          ldw       r7, sp[0x6] 
.LDBG366:
          ldw       r8, sp[0x7] 
.LDBG367:
          ldw       r9, sp[0x8] 
.LDBG368:
          ldw       r10, sp[0x9] 
.LDBG369:
          retsp     0xf 
.LDBG246:
.LDBG370:
.LDBG210:
.L123:
.LDBG371:                            # line (242)
.LDBG247:
.cc_bottom deliver.function
.LDBG372:
          .section  .debug_frame, "",     @progbits
.cc_top deliver.function,deliver
          .align    4
          .int      .LDBG374-.LDBG373
.LDBG373:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG248
          .int      .LDBG372-.LDBG248
          .byte     0x01
          .int      .LDBG250
          .byte     0x0e
          .uleb128  0x3c
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG251
          .byte     0x84
          .uleb128  0x3
          .byte     0x01
          .int      .LDBG252
          .byte     0x85
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG253
          .byte     0x86
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG254
          .byte     0x87
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG255
          .byte     0x88
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG256
          .byte     0x89
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG257
          .byte     0x8a
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG274
          .byte     0x0a
          .byte     0x01
          .int      .LDBG275
          .byte     0xc4
          .byte     0x01
          .int      .LDBG276
          .byte     0xc5
          .byte     0x01
          .int      .LDBG277
          .byte     0xc6
          .byte     0x01
          .int      .LDBG278
          .byte     0xc7
          .byte     0x01
          .int      .LDBG279
          .byte     0xc8
          .byte     0x01
          .int      .LDBG280
          .byte     0xc9
          .byte     0x01
          .int      .LDBG281
          .byte     0xca
          .byte     0x01
          .int      .LDBG282
          .byte     0x0b
          .byte     0x01
          .int      .LDBG352
          .byte     0x0a
          .byte     0x01
          .int      .LDBG353
          .byte     0xc4
          .byte     0x01
          .int      .LDBG354
          .byte     0xc5
          .byte     0x01
          .int      .LDBG355
          .byte     0xc6
          .byte     0x01
          .int      .LDBG356
          .byte     0xc7
          .byte     0x01
          .int      .LDBG357
          .byte     0xc8
          .byte     0x01
          .int      .LDBG358
          .byte     0xc9
          .byte     0x01
          .int      .LDBG359
          .byte     0xca
          .byte     0x01
          .int      .LDBG360
          .byte     0x0b
          .byte     0x01
          .int      .LDBG362
          .byte     0x0a
          .byte     0x01
          .int      .LDBG363
          .byte     0xc4
          .byte     0x01
          .int      .LDBG364
          .byte     0xc5
          .byte     0x01
          .int      .LDBG365
          .byte     0xc6
          .byte     0x01
          .int      .LDBG366
          .byte     0xc7
          .byte     0x01
          .int      .LDBG367
          .byte     0xc8
          .byte     0x01
          .int      .LDBG368
          .byte     0xc9
          .byte     0x01
          .int      .LDBG369
          .byte     0xca
          .byte     0x01
          .int      .LDBG370
          .byte     0x0b
          .align    4, 0
.LDBG374:
.cc_bottom deliver.function
.linkset .LLNK65, GetData.maxchanends $M GetInt.maxchanends
.linkset .LLNK64, .LLNK65 $M GetParam.maxchanends
.linkset .LLNK63, .LLNK64 $M GetInt.maxchanends
.linkset .LLNK62, .LLNK63 $M GetParam.maxchanends
.linkset .LLNK61, .LLNK62 $M GetInt.maxchanends
.linkset .LLNK60, .LLNK61 $M GetParam.maxchanends
.linkset .LLNK59, .LLNK60 $M GetInt.maxchanends
.linkset .LLNK58, .LLNK59 $M GetParam.maxchanends
.linkset .LLNK57, .LLNK58 $M GetInt.maxchanends
.linkset .LLNK56, .LLNK57 $M GetParam.maxchanends
.linkset .LLNK55, .LLNK56 $M GetInt.maxchanends
.linkset .LLNK54, .LLNK55 $M GetParam.maxchanends
.linkset .LLNK53, .LLNK54 $M GetInt.maxchanends
.linkset .LLNK52, .LLNK53 $M GetParam.maxchanends
.linkset .LLNK51, .LLNK52 $M GetInt.maxchanends
.linkset .LLNK50, .LLNK51 $M GetParam.maxchanends
.linkset .LLNK49, .LLNK50 $M GetData.maxchanends
.linkset .LLNK48, .LLNK49 $M GetParam.maxchanends
.linkset deliver.maxchanends, .LLNK48
.linkset .LLNK83, GetData.maxtimers $M GetInt.maxtimers
.linkset .LLNK82, .LLNK83 $M GetParam.maxtimers
.linkset .LLNK81, .LLNK82 $M GetInt.maxtimers
.linkset .LLNK80, .LLNK81 $M GetParam.maxtimers
.linkset .LLNK79, .LLNK80 $M GetInt.maxtimers
.linkset .LLNK78, .LLNK79 $M GetParam.maxtimers
.linkset .LLNK77, .LLNK78 $M GetInt.maxtimers
.linkset .LLNK76, .LLNK77 $M GetParam.maxtimers
.linkset .LLNK75, .LLNK76 $M GetInt.maxtimers
.linkset .LLNK74, .LLNK75 $M GetParam.maxtimers
.linkset .LLNK73, .LLNK74 $M GetInt.maxtimers
.linkset .LLNK72, .LLNK73 $M GetParam.maxtimers
.linkset .LLNK71, .LLNK72 $M GetInt.maxtimers
.linkset .LLNK70, .LLNK71 $M GetParam.maxtimers
.linkset .LLNK69, .LLNK70 $M GetInt.maxtimers
.linkset .LLNK68, .LLNK69 $M GetParam.maxtimers
.linkset .LLNK67, .LLNK68 $M GetData.maxtimers
.linkset .LLNK66, .LLNK67 $M GetParam.maxtimers
.linkset deliver.maxtimers, .LLNK66
.linkset .LLNK104, GetData.maxthreads - 1
.linkset .LLNK103, 1 + .LLNK104
.linkset .LLNK102, 1 $M .LLNK103
.linkset .LLNK106, GetInt.maxthreads - 1
.linkset .LLNK105, 1 + .LLNK106
.linkset .LLNK101, .LLNK102 $M .LLNK105
.linkset .LLNK108, GetParam.maxthreads - 1
.linkset .LLNK107, 1 + .LLNK108
.linkset .LLNK100, .LLNK101 $M .LLNK107
.linkset .LLNK110, GetInt.maxthreads - 1
.linkset .LLNK109, 1 + .LLNK110
.linkset .LLNK99, .LLNK100 $M .LLNK109
.linkset .LLNK112, GetParam.maxthreads - 1
.linkset .LLNK111, 1 + .LLNK112
.linkset .LLNK98, .LLNK99 $M .LLNK111
.linkset .LLNK114, GetInt.maxthreads - 1
.linkset .LLNK113, 1 + .LLNK114
.linkset .LLNK97, .LLNK98 $M .LLNK113
.linkset .LLNK116, GetParam.maxthreads - 1
.linkset .LLNK115, 1 + .LLNK116
.linkset .LLNK96, .LLNK97 $M .LLNK115
.linkset .LLNK118, GetInt.maxthreads - 1
.linkset .LLNK117, 1 + .LLNK118
.linkset .LLNK95, .LLNK96 $M .LLNK117
.linkset .LLNK120, GetParam.maxthreads - 1
.linkset .LLNK119, 1 + .LLNK120
.linkset .LLNK94, .LLNK95 $M .LLNK119
.linkset .LLNK122, GetInt.maxthreads - 1
.linkset .LLNK121, 1 + .LLNK122
.linkset .LLNK93, .LLNK94 $M .LLNK121
.linkset .LLNK124, GetParam.maxthreads - 1
.linkset .LLNK123, 1 + .LLNK124
.linkset .LLNK92, .LLNK93 $M .LLNK123
.linkset .LLNK126, GetInt.maxthreads - 1
.linkset .LLNK125, 1 + .LLNK126
.linkset .LLNK91, .LLNK92 $M .LLNK125
.linkset .LLNK128, GetParam.maxthreads - 1
.linkset .LLNK127, 1 + .LLNK128
.linkset .LLNK90, .LLNK91 $M .LLNK127
.linkset .LLNK130, GetInt.maxthreads - 1
.linkset .LLNK129, 1 + .LLNK130
.linkset .LLNK89, .LLNK90 $M .LLNK129
.linkset .LLNK132, GetParam.maxthreads - 1
.linkset .LLNK131, 1 + .LLNK132
.linkset .LLNK88, .LLNK89 $M .LLNK131
.linkset .LLNK134, GetInt.maxthreads - 1
.linkset .LLNK133, 1 + .LLNK134
.linkset .LLNK87, .LLNK88 $M .LLNK133
.linkset .LLNK136, GetParam.maxthreads - 1
.linkset .LLNK135, 1 + .LLNK136
.linkset .LLNK86, .LLNK87 $M .LLNK135
.linkset .LLNK138, GetData.maxthreads - 1
.linkset .LLNK137, 1 + .LLNK138
.linkset .LLNK85, .LLNK86 $M .LLNK137
.linkset .LLNK140, GetParam.maxthreads - 1
.linkset .LLNK139, 1 + .LLNK140
.linkset .LLNK84, .LLNK85 $M .LLNK139
.linkset deliver.maxthreads, .LLNK84
          .text
.LDBG375:
.extern audio_init, "f{0}(o:p,p,p,p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,i:p,ck)"
.LDBG376:
.call physical, GetParam
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferA, "./tests/audio/src/audio.xc:263: error: `physical' makes alias of global 'bufferA' in call to `GetBufferAddress'"
.globwrite physical, bufferA, "./tests/audio/src/audio.xc:263: error: previously used here"
.globwrite physical, bufferA, "./tests/audio/src/audio.xc:263: error: previously used here"
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferB, "./tests/audio/src/audio.xc:264: error: `physical' makes alias of global 'bufferB' in call to `GetBufferAddress'"
.globwrite physical, bufferB, "./tests/audio/src/audio.xc:264: error: previously used here"
.globwrite physical, bufferB, "./tests/audio/src/audio.xc:264: error: previously used here"
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferC, "./tests/audio/src/audio.xc:265: error: `physical' makes alias of global 'bufferC' in call to `GetBufferAddress'"
.globwrite physical, bufferC, "./tests/audio/src/audio.xc:265: error: previously used here"
.globwrite physical, bufferC, "./tests/audio/src/audio.xc:265: error: previously used here"
.call physical, __builtin_timer_after
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, GetParam
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, swc_audio_codec_ctl_reg_wr
.call physical, XUD_Error
          .section .cp.string,     "ac", @progbits
          .align    4
s.LC3:
          .ascii    "Unknown samp freq\0"
.call physical, __builtin_in_uint_byref
.call physical, __builtin_in_uint
.call physical, GetParam
.call physical, __builtin_clear_buff
.call physical, __builtin_clear_buff
.call physical, __builtin_clear_buff
.call physical, __builtin_clear_buff
.call physical, __builtin_pins_ne
.call physical, __builtin_pins_ne
.call physical, __builtin_pins_ne
.call physical, __builtin_pins_ne
.globwrite physical, lrtime, "./tests/audio/src/audio.xc:156: error: previously used here"
.globwrite physical, lrtime, "./tests/audio/src/audio.xc:410: error: previously used here"
.globread physical, lrtime, "./tests/audio/src/audio.xc:411: error: previously used here"
.globread physical, lrtime, "./tests/audio/src/audio.xc:412: error: previously used here"
.globread physical, lrtime, "./tests/audio/src/audio.xc:413: error: previously used here"
.globread physical, lrtime, "./tests/audio/src/audio.xc:414: error: previously used here"
.call physical, deliver
.call physical, __builtin_in_uint_byref
.call physical, __builtin_in_uint
.set __builtin_timer_after, 0
.linkset __builtin_timer_after.locnoside, 0
.linkset __builtin_timer_after.locnochandec, 1
.set __builtin_in_uint, 0
.linkset __builtin_in_uint.locnoside, 0
.linkset __builtin_in_uint.locnochandec, 1
.set __builtin_clear_buff, 0
.linkset __builtin_clear_buff.locnoside, 0
.linkset __builtin_clear_buff.locnochandec, 1
          .text
.globl physical, "f{0}(chd,o:p,p,p,p,i:p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,ck)"
.globl physical.nstackwords
.globl physical.maxthreads
.globl physical.maxtimers
.globl physical.maxchanends
.globl physical.maxsync
.type  physical, @function
.linkset physical.locnoside, 0
.linkset physical.locnochandec, 1
.linkset .LLNK175, GetParam.nstackwords $M GetBufferAddress.nstackwords
.linkset .LLNK174, .LLNK175 $M GetBufferAddress.nstackwords
.linkset .LLNK173, .LLNK174 $M GetBufferAddress.nstackwords
.linkset .LLNK172, .LLNK173 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK171, .LLNK172 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK170, .LLNK171 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK169, .LLNK170 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK168, .LLNK169 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK167, .LLNK168 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK166, .LLNK167 $M GetParam.nstackwords
.linkset .LLNK165, .LLNK166 $M XUD_Error.nstackwords
.linkset .LLNK164, .LLNK165 $M GetParam.nstackwords
.linkset .LLNK163, .LLNK164 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK162, .LLNK163 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK161, .LLNK162 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK160, .LLNK161 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK159, .LLNK160 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK158, .LLNK159 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK157, .LLNK158 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK156, .LLNK157 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK155, .LLNK156 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK154, .LLNK155 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK153, .LLNK154 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK152, .LLNK153 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK151, .LLNK152 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK150, .LLNK151 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK149, .LLNK150 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK148, .LLNK149 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK147, .LLNK148 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK146, .LLNK147 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK145, .LLNK146 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK144, .LLNK145 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK143, .LLNK144 $M deliver.nstackwords
.linkset .LLNK142, .LLNK143 $M .LLNK143
.linkset .LLNK141, .LLNK142 + 14
.linkset physical.nstackwords, .LLNK141
.cc_top physical.function,physical
.LDBG446:
.LDBG447:                            # line (250)
          .align    4
physical:
          entsp     0xe 
.LDBG448:
.L167:
          mov       r10, r0
.LDBG379:
          mov       r6, r1
.LDBG380:
.LDBG381:
          mov       r9, r3
.LDBG382:
          ldw       r8, sp[0xf] 
.LDBG383:
          ldw       r7, sp[0x10] 
.LDBG384:
          ldw       r0, sp[0x15] 
.LDBG385:
.LDBG449:                            # line (250)
.LDBG377:
          getr      r5, 0x1
.LDBG386:
.LDBG450:                            # line (253)
.LDBG451:                            # line (255)
          ldc       r0, 0x9
.LDBG452:                            # line (255)
          bl        GetParam 
.LDBG453:                            # line (258)
.LDBG454:                            # line (263)
          ldaw      r0, dp[bufferA] 
          ldc       r1, 0x300
.LDBG455:                            # line (263)
          bl        GetBufferAddress 
          ldaw      r1, sp[0x7] 
          stw       r0, r1[0x0] 
.LDBG456:                            # line (264)
          ldaw      r0, dp[bufferB] 
          ldc       r1, 0x300
.LDBG457:                            # line (264)
          bl        GetBufferAddress 
          ldaw      r1, sp[0x7] 
          stw       r0, r1[0x1] 
.LDBG458:                            # line (265)
          ldaw      r0, dp[bufferC] 
          ldc       r1, 0x300
.LDBG459:                            # line (265)
          bl        GetBufferAddress 
          ldaw      r1, sp[0x7] 
          stw       r0, r1[0x2] 
          ldc       r4, 0x0
.LDBG460:                            # line (268)
.L188:
          out       res[r6], r4 
          ldc       r1, 0x1
.LDBG461:                            # line (269)
          setc      res[r5], r1
.L189:
          in        r0, res[r5] 
.LDBG387:
.LDBG462:                            # line (270)
          ldw       r11, cp[s.LC4]
          .section .cp.const4,     "acM", @progbits, 4
s.LC4:
          .align    4
          .int      0x000186a0
          .text
          add       r0, r0, r11
.LDBG463:                            # line (270)
          setd      res[r5], r0
          setc      res[r5], 0x9
.L190:
          in        r0, res[r5] 
.LDBG388:
.LDBG464:                            # line (271)
.L191:
          out       res[r6], r1 
.LDBG465:                            # line (280)
          ldc       r5, 0x90
.LDBG466:                            # line (281)
          mov       r0, r5
          ldc       r1, 0x4
          ldc       r2, 0x11
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG467:                            # line (281)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG468:                            # line (292)
.LDBG469:                            # line (293)
          mov       r0, r5
          ldc       r1, 0x5
          ldc       r2, 0x1c
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG470:                            # line (293)
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r6, 0x9c
.LDBG471:                            # line (300)
          ldc       r1, 0x3
          ldc       r2, 0x1
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG472:                            # line (300)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG473:                            # line (301)
          ldc       r1, 0x5
          ldc       r2, 0x1
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG474:                            # line (301)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG475:                            # line (303)
          ldc       r1, 0x16
          ldc       r2, 0x10
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG476:                            # line (303)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG477:                            # line (305)
          ldc       r1, 0x17
          ldc       r2, 0x0
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG478:                            # line (305)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG479:                            # line (309)
.LDBG389:
.LDBG390:
.L136:
.L168:
.LDBG480:                            # line (317)
          ldc       r0, 0x9
.LDBG481:                            # line (317)
          bl        GetParam 
          stw       r0, sp[0xc] 
          ldw       r1, sp[0xc] 
.LDBG391:
.LDBG482:                            # line (318)
.LDBG483:                            # line (319)
          ldc       r0, 0xac44
          eq        r0, r1, r0
          bt        r0, .L140 
.LDBG392:
.LDBG393:
          ldc       r0, 0xbb80
          eq        r0, r1, r0
          bt        r0, .L141 
.LDBG394:
.LDBG395:
          ldw       r11, cp[s.LC5]
          .section .cp.const4,     "acM", @progbits, 4
s.LC5:
          .align    4
          .int      0x00017700
          .text
          eq        r0, r1, r11
          bt        r0, .L142 
.LDBG396:
.LDBG397:
          ldw       r11, cp[s.LC6]
          .section .cp.const4,     "acM", @progbits, 4
s.LC6:
          .align    4
          .int      0x0002ee00
          .text
          eq        r0, r1, r11
          bt        r0, .L143 
.LDBG398:
.LDBG399:
.L173:
.LDBG484:                            # line (367)
          ldaw      r11, cp[s.LC3] 
          mov       r0, r11
          ldc       r1, 0x12
.LDBG485:                            # line (367)
          bl        XUD_Error 
.LDBG400:
.LDBG401:
.L139:
.L174:
.LDBG486:                            # line (372)
          ldc       r0, 0x1
          stw       r0, sp[0xb] 
.LDBG487:                            # line (373)
          ldc       r0, 0x1
          stw       r0, sp[0x6] 
.LDBG488:                            # line (376)
.LDBG402:
.LDBG403:
.L148:
.L175:
.LDBG489:                            # line (378)
          clre      
          ldap      r11, .L153
          setv      res[r10], r11
          eeu       res[r10]
.xtabranch physical, .L153
          setsr     0x1
          clrsr     0x1
.LDBG404:
.LDBG405:
.L176:
.LDBG490:                            # line (387)
          ldc       r0, 0x9
.LDBG491:                            # line (387)
          bl        GetParam 
.LDBG492:                            # line (387)
          ldw       r1, sp[0xc] 
          eq        r0, r1, r0
          bf        r0, .L157 
.LDBG406:
.LDBG407:
.L149:
.L179:
.LDBG493:                            # line (376)
          ldw       r0, sp[0x6] 
          bt        r0, .L148 
.LDBG408:
.LDBG409:
.L180:
.LDBG494:                            # line (397)
          ldw       r0, sp[0xb] 
          bt        r0, .L159 
.LDBG410:
.LDBG411:
.L137:
.L186:
.LDBG495:                            # line (309)
          bu        .L136 
.LDBG412:
.LDBG413:
.LDBG414:
.LDBG415:
.L140:
.L169:
.LDBG496:                            # line (323)
          mov       r0, r6
          ldc       r1, 0x6
          ldc       r2, 0x6e
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG497:                            # line (323)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG498:                            # line (324)
          mov       r0, r6
          ldc       r1, 0x7
          ldc       r2, 0x40
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG499:                            # line (324)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG500:                            # line (325)
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG501:                            # line (325)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG502:                            # line (326)
          mov       r0, r6
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG503:                            # line (326)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG504:                            # line (328)
.LDBG505:                            # line (329)
          mov       r0, r5
          ldc       r1, 0x3
          ldc       r2, 0x8
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG506:                            # line (329)
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L139 
.LDBG416:
.LDBG417:
.L141:
.L170:
.LDBG507:                            # line (335)
          mov       r0, r6
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG508:                            # line (335)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG509:                            # line (336)
          mov       r0, r6
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG510:                            # line (336)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG511:                            # line (337)
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG512:                            # line (337)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG513:                            # line (338)
          mov       r0, r6
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG514:                            # line (338)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG515:                            # line (340)
.LDBG516:                            # line (341)
          mov       r0, r5
          ldc       r1, 0x3
          ldc       r2, 0x8
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG517:                            # line (341)
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L139 
.LDBG418:
.LDBG419:
.L142:
.L171:
.LDBG518:                            # line (345)
          mov       r0, r6
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG519:                            # line (345)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG520:                            # line (346)
          mov       r0, r6
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG521:                            # line (346)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG522:                            # line (347)
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG523:                            # line (347)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG524:                            # line (348)
          mov       r0, r6
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG525:                            # line (348)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG526:                            # line (350)
.LDBG527:                            # line (351)
          ldc       r1, 0x3
          ldc       r2, 0x58
          mov       r0, r5
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG528:                            # line (351)
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L139 
.LDBG420:
.LDBG421:
.L143:
.L172:
.LDBG529:                            # line (355)
          mov       r0, r6
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG530:                            # line (355)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG531:                            # line (356)
          mov       r0, r6
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG532:                            # line (356)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG533:                            # line (357)
          mov       r0, r6
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG534:                            # line (357)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG535:                            # line (358)
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r0, r6
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG536:                            # line (358)
          bl        swc_audio_codec_ctl_reg_wr 
.LDBG537:                            # line (359)
.LDBG538:                            # line (362)
          ldc       r1, 0x3
          ldc       r2, 0xa8
          mov       r0, r5
          mov       r3, r9
          stw       r8, sp[0x1] 
.LDBG539:                            # line (362)
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L139 
.LDBG422:
.LDBG423:
.LDBG424:
.LDBG425:
.L157:
.L177:
.LDBG540:                            # line (389)
          ldc       r0, 0x0
          stw       r0, sp[0xb] 
.LDBG541:                            # line (390)
          ldc       r0, 0x0
          stw       r0, sp[0x6] 
          bu        .L149 
.LDBG426:
.LDBG427:
.LDBG428:
.LDBG429:
.L153:
.L178:
.LDBG542:                            # line (380)
.LDBG543:                            # line (380)
.L192:
          in        r0, res[r10] 
          stw       r0, sp[0x5] 
.LDBG544:                            # line (381)
.LDBG545:                            # line (381)
.L193:
          in        r2, res[r10] 
.LDBG546:                            # line (382)
          ldaw      r1, sp[0x7] 
          ldc       r0, 0x3
          lsu       r0, r2, r0
          ecallf    r0
          ldw       r0, r1[r2] 
          stw       r0, sp[0xa] 
.LDBG547:                            # line (383)
          ldc       r0, 0x0
          stw       r0, sp[0x6] 
          bu        .L149 
.LDBG430:
.LDBG431:
.L150:
.LDBG432:
.LDBG433:
.LDBG434:
.LDBG435:
.L159:
.L181:
.LDBG548:                            # line (399)
.LDBG549:                            # line (399)
          ldw       r0, sp[0x11] 
          setc      res[r0], 0x17
.LDBG550:                            # line (400)
.LDBG551:                            # line (400)
          ldw       r0, sp[0x12] 
          setc      res[r0], 0x17
.LDBG552:                            # line (401)
.LDBG553:                            # line (401)
          ldw       r0, sp[0x13] 
          setc      res[r0], 0x17
.LDBG554:                            # line (402)
.LDBG555:                            # line (402)
          ldw       r0, sp[0x14] 
          setc      res[r0], 0x17
          ldc       r1, 0x1
.LDBG556:                            # line (404)
.LDBG557:                            # line (404)
          setd      res[r7], r1
          setc      res[r7], 0x19
.L194:
          in        r0, res[r7] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          ldc       r2, 0x0
.LDBG558:                            # line (405)
.LDBG559:                            # line (405)
          setd      res[r7], r2
.L195:
          in        r0, res[r7] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
.LDBG560:                            # line (406)
.LDBG561:                            # line (406)
          setd      res[r7], r1
.L196:
          in        r0, res[r7] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
.LDBG562:                            # line (408)
.LDBG563:                            # line (408)
          setd      res[r7], r2
.L197:
          in        r0, res[r7] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          getts     r0, res[r7] 

          stw       r0, dp[lrtime] 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
.LDBG564:                            # line (410)
          ldc       r0, 0x40
          add       r0, r1, r0
          sext      r0, 0x10 

          stw       r0, dp[lrtime] 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
.LDBG565:                            # line (411)
          ldw       r0, sp[0x11] 
          setpt     res[r0], r1
          ldw       r0, sp[0x11] 
.L198:
          out       res[r0], r4 
.LDBG566:                            # line (412)
          ldw       r0, sp[0x12] 
          setpt     res[r0], r1
          ldw       r0, sp[0x12] 
.L199:
          out       res[r0], r4 
.LDBG567:                            # line (413)
          ldw       r0, sp[0x13] 
          setpt     res[r0], r1
          ldw       r0, sp[0x13] 
.L200:
          out       res[r0], r4 
.LDBG568:                            # line (414)
          ldw       r0, sp[0x14] 
          setpt     res[r0], r1
          ldw       r0, sp[0x14] 
.L201:
          out       res[r0], r4 
.LDBG569:                            # line (420)
.LDBG436:
.LDBG437:
.L163:
.L182:
.LDBG570:                            # line (422)
          ldw       r0, sp[0x5] 
          ldw       r1, sp[0xa] 
          ldw       r2, sp[0xc] 
          stw       r2, sp[0xd] 
          mov       r2, r7
          ldw       r3, sp[0x11] 
          ldw       r11, sp[0x12] 
          stw       r11, sp[0x1] 
          ldw       r11, sp[0x13] 
          stw       r11, sp[0x2] 
          ldw       r11, sp[0x14] 
          stw       r11, sp[0x3] 
          ldw       r11, sp[0xd] 
          stw       r11, sp[0x4] 
.LDBG571:                            # line (422)
          bl        deliver 
.LDBG572:                            # line (422)
          ldw       r11, cp[s.LC1]
          eq        r0, r0, r11
          bt        r0, .L166 
.LDBG438:
.LDBG439:
.L184:
.LDBG573:                            # line (425)
.LDBG574:                            # line (425)
.L202:
          in        r0, res[r10] 
          stw       r0, sp[0x5] 
.LDBG575:                            # line (426)
.LDBG576:                            # line (426)
.L203:
          in        r2, res[r10] 
.LDBG577:                            # line (427)
          ldaw      r1, sp[0x7] 
          ldc       r0, 0x3
          lsu       r0, r2, r0
          ecallf    r0
          ldw       r0, r1[r2] 
          stw       r0, sp[0xa] 
.L185:
.LDBG578:                            # line (420)
          bu        .L163 
.LDBG440:
.LDBG441:
.LDBG442:
.LDBG443:
.L166:
.L183:
.LDBG579:                            # line (423)
          bu        .L137 
.LDBG444:
.LDBG378:
.L187:
.LDBG580:                            # line (431)
.LDBG445:
.cc_bottom physical.function
.LDBG581:
          .section  .debug_frame, "",     @progbits
.cc_top physical.function,physical
          .align    4
          .int      .LDBG583-.LDBG582
.LDBG582:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG446
          .int      .LDBG581-.LDBG446
          .byte     0x01
          .int      .LDBG448
          .byte     0x0e
          .uleb128  0x38
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .align    4, 0
.LDBG583:
.cc_bottom physical.function
.linkset .LLNK208, GetParam.maxchanends $M GetBufferAddress.maxchanends
.linkset .LLNK207, .LLNK208 $M GetBufferAddress.maxchanends
.linkset .LLNK206, .LLNK207 $M GetBufferAddress.maxchanends
.linkset .LLNK205, .LLNK206 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK204, .LLNK205 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK203, .LLNK204 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK202, .LLNK203 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK201, .LLNK202 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK200, .LLNK201 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK199, .LLNK200 $M GetParam.maxchanends
.linkset .LLNK198, .LLNK199 $M XUD_Error.maxchanends
.linkset .LLNK197, .LLNK198 $M GetParam.maxchanends
.linkset .LLNK196, .LLNK197 $M deliver.maxchanends
.linkset .LLNK195, .LLNK196 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK194, .LLNK195 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK193, .LLNK194 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK192, .LLNK193 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK191, .LLNK192 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK190, .LLNK191 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK189, .LLNK190 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK188, .LLNK189 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK187, .LLNK188 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK186, .LLNK187 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK185, .LLNK186 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK184, .LLNK185 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK183, .LLNK184 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK182, .LLNK183 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK181, .LLNK182 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK180, .LLNK181 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK179, .LLNK180 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK178, .LLNK179 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK177, .LLNK178 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK176, .LLNK177 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset physical.maxchanends, .LLNK176
.linkset .LLNK243, 1 + GetParam.maxtimers
.linkset .LLNK242, 1 $M .LLNK243
.linkset .LLNK244, 1 + GetBufferAddress.maxtimers
.linkset .LLNK241, .LLNK242 $M .LLNK244
.linkset .LLNK245, 1 + GetBufferAddress.maxtimers
.linkset .LLNK240, .LLNK241 $M .LLNK245
.linkset .LLNK246, 1 + GetBufferAddress.maxtimers
.linkset .LLNK239, .LLNK240 $M .LLNK246
.linkset .LLNK247, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK238, .LLNK239 $M .LLNK247
.linkset .LLNK248, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK237, .LLNK238 $M .LLNK248
.linkset .LLNK249, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK236, .LLNK237 $M .LLNK249
.linkset .LLNK250, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK235, .LLNK236 $M .LLNK250
.linkset .LLNK251, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK234, .LLNK235 $M .LLNK251
.linkset .LLNK252, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK233, .LLNK234 $M .LLNK252
.linkset .LLNK253, 1 + GetParam.maxtimers
.linkset .LLNK232, .LLNK233 $M .LLNK253
.linkset .LLNK254, 1 + XUD_Error.maxtimers
.linkset .LLNK231, .LLNK232 $M .LLNK254
.linkset .LLNK255, 1 + GetParam.maxtimers
.linkset .LLNK230, .LLNK231 $M .LLNK255
.linkset .LLNK256, 1 + deliver.maxtimers
.linkset .LLNK229, .LLNK230 $M .LLNK256
.linkset .LLNK257, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK228, .LLNK229 $M .LLNK257
.linkset .LLNK258, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK227, .LLNK228 $M .LLNK258
.linkset .LLNK259, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK226, .LLNK227 $M .LLNK259
.linkset .LLNK260, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK225, .LLNK226 $M .LLNK260
.linkset .LLNK261, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK224, .LLNK225 $M .LLNK261
.linkset .LLNK262, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK223, .LLNK224 $M .LLNK262
.linkset .LLNK263, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK222, .LLNK223 $M .LLNK263
.linkset .LLNK264, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK221, .LLNK222 $M .LLNK264
.linkset .LLNK265, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK220, .LLNK221 $M .LLNK265
.linkset .LLNK266, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK219, .LLNK220 $M .LLNK266
.linkset .LLNK267, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK218, .LLNK219 $M .LLNK267
.linkset .LLNK268, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK217, .LLNK218 $M .LLNK268
.linkset .LLNK269, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK216, .LLNK217 $M .LLNK269
.linkset .LLNK270, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK215, .LLNK216 $M .LLNK270
.linkset .LLNK271, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK214, .LLNK215 $M .LLNK271
.linkset .LLNK272, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK213, .LLNK214 $M .LLNK272
.linkset .LLNK273, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK212, .LLNK213 $M .LLNK273
.linkset .LLNK274, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK211, .LLNK212 $M .LLNK274
.linkset .LLNK275, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK210, .LLNK211 $M .LLNK275
.linkset .LLNK276, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK209, .LLNK210 $M .LLNK276
.linkset physical.maxtimers, .LLNK209
.linkset .LLNK312, GetParam.maxthreads - 1
.linkset .LLNK311, 1 + .LLNK312
.linkset .LLNK310, 1 $M .LLNK311
.linkset .LLNK314, GetBufferAddress.maxthreads - 1
.linkset .LLNK313, 1 + .LLNK314
.linkset .LLNK309, .LLNK310 $M .LLNK313
.linkset .LLNK316, GetBufferAddress.maxthreads - 1
.linkset .LLNK315, 1 + .LLNK316
.linkset .LLNK308, .LLNK309 $M .LLNK315
.linkset .LLNK318, GetBufferAddress.maxthreads - 1
.linkset .LLNK317, 1 + .LLNK318
.linkset .LLNK307, .LLNK308 $M .LLNK317
.linkset .LLNK320, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK319, 1 + .LLNK320
.linkset .LLNK306, .LLNK307 $M .LLNK319
.linkset .LLNK322, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK321, 1 + .LLNK322
.linkset .LLNK305, .LLNK306 $M .LLNK321
.linkset .LLNK324, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK323, 1 + .LLNK324
.linkset .LLNK304, .LLNK305 $M .LLNK323
.linkset .LLNK326, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK325, 1 + .LLNK326
.linkset .LLNK303, .LLNK304 $M .LLNK325
.linkset .LLNK328, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK327, 1 + .LLNK328
.linkset .LLNK302, .LLNK303 $M .LLNK327
.linkset .LLNK330, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK329, 1 + .LLNK330
.linkset .LLNK301, .LLNK302 $M .LLNK329
.linkset .LLNK332, GetParam.maxthreads - 1
.linkset .LLNK331, 1 + .LLNK332
.linkset .LLNK300, .LLNK301 $M .LLNK331
.linkset .LLNK334, XUD_Error.maxthreads - 1
.linkset .LLNK333, 1 + .LLNK334
.linkset .LLNK299, .LLNK300 $M .LLNK333
.linkset .LLNK336, GetParam.maxthreads - 1
.linkset .LLNK335, 1 + .LLNK336
.linkset .LLNK298, .LLNK299 $M .LLNK335
.linkset .LLNK338, deliver.maxthreads - 1
.linkset .LLNK337, 1 + .LLNK338
.linkset .LLNK297, .LLNK298 $M .LLNK337
.linkset .LLNK340, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK339, 1 + .LLNK340
.linkset .LLNK296, .LLNK297 $M .LLNK339
.linkset .LLNK342, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK341, 1 + .LLNK342
.linkset .LLNK295, .LLNK296 $M .LLNK341
.linkset .LLNK344, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK343, 1 + .LLNK344
.linkset .LLNK294, .LLNK295 $M .LLNK343
.linkset .LLNK346, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK345, 1 + .LLNK346
.linkset .LLNK293, .LLNK294 $M .LLNK345
.linkset .LLNK348, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK347, 1 + .LLNK348
.linkset .LLNK292, .LLNK293 $M .LLNK347
.linkset .LLNK350, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK349, 1 + .LLNK350
.linkset .LLNK291, .LLNK292 $M .LLNK349
.linkset .LLNK352, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK351, 1 + .LLNK352
.linkset .LLNK290, .LLNK291 $M .LLNK351
.linkset .LLNK354, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK353, 1 + .LLNK354
.linkset .LLNK289, .LLNK290 $M .LLNK353
.linkset .LLNK356, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK355, 1 + .LLNK356
.linkset .LLNK288, .LLNK289 $M .LLNK355
.linkset .LLNK358, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK357, 1 + .LLNK358
.linkset .LLNK287, .LLNK288 $M .LLNK357
.linkset .LLNK360, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK359, 1 + .LLNK360
.linkset .LLNK286, .LLNK287 $M .LLNK359
.linkset .LLNK362, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK361, 1 + .LLNK362
.linkset .LLNK285, .LLNK286 $M .LLNK361
.linkset .LLNK364, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK363, 1 + .LLNK364
.linkset .LLNK284, .LLNK285 $M .LLNK363
.linkset .LLNK366, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK365, 1 + .LLNK366
.linkset .LLNK283, .LLNK284 $M .LLNK365
.linkset .LLNK368, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK367, 1 + .LLNK368
.linkset .LLNK282, .LLNK283 $M .LLNK367
.linkset .LLNK370, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK369, 1 + .LLNK370
.linkset .LLNK281, .LLNK282 $M .LLNK369
.linkset .LLNK372, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK371, 1 + .LLNK372
.linkset .LLNK280, .LLNK281 $M .LLNK371
.linkset .LLNK374, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK373, 1 + .LLNK374
.linkset .LLNK279, .LLNK280 $M .LLNK373
.linkset .LLNK376, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK375, 1 + .LLNK376
.linkset .LLNK278, .LLNK279 $M .LLNK375
.linkset .LLNK378, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK377, 1 + .LLNK378
.linkset .LLNK277, .LLNK278 $M .LLNK377
.linkset physical.maxthreads, .LLNK277
          .text
.LDBG584:
.LDBG585:
.call XUD_Endpoint1, GetParam
.call XUD_Endpoint1, configure_clock_src
.call XUD_Endpoint1, configure_out_port_no_ready
.call XUD_Endpoint1, configure_out_port_no_ready
.call XUD_Endpoint1, configure_out_port_no_ready
.call XUD_Endpoint1, configure_out_port_no_ready
.call XUD_Endpoint1, configure_out_port_no_ready
.call XUD_Endpoint1, __builtin_start_clock
.call XUD_Endpoint1, thread.anon.0
.call thread.anon.0, readData
.globpassesref readData, bufferA, "./tests/audio/src/audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferA' in call to `readData'"
.globwrite thread.anon.0, bufferA, "./tests/audio/src/audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferA, "./tests/audio/src/audio.xc:467: error: previously used here"
.globpassesref readData, bufferB, "./tests/audio/src/audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferB' in call to `readData'"
.globwrite thread.anon.0, bufferB, "./tests/audio/src/audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferB, "./tests/audio/src/audio.xc:467: error: previously used here"
.globpassesref readData, bufferC, "./tests/audio/src/audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferC' in call to `readData'"
.globwrite thread.anon.0, bufferC, "./tests/audio/src/audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferC, "./tests/audio/src/audio.xc:467: error: previously used here"
.call XUD_Endpoint1, thread.anon.3
.call thread.anon.3, physical
.set __builtin_start_clock, 0
.linkset __builtin_start_clock.locnoside, 0
.linkset __builtin_start_clock.locnochandec, 1
.globl XUD_Endpoint1, "f{0}(chd,o:p,p,p,p,i:p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,ck)"
.globl XUD_Endpoint1.nstackwords
.globl XUD_Endpoint1.maxthreads
.globl XUD_Endpoint1.maxtimers
.globl XUD_Endpoint1.maxchanends
.globl XUD_Endpoint1.maxsync
.type  XUD_Endpoint1, @function
.linkset XUD_Endpoint1.locnoside, 0
.linkset XUD_Endpoint1.locnochandec, 0
.linkset .LLNK387, GetParam.nstackwords $M configure_clock_src.nstackwords
.linkset .LLNK386, .LLNK387 $M configure_out_port_no_ready.nstackwords
.linkset .LLNK385, .LLNK386 $M configure_out_port_no_ready.nstackwords
.linkset .LLNK384, .LLNK385 $M configure_out_port_no_ready.nstackwords
.linkset .LLNK383, .LLNK384 $M configure_out_port_no_ready.nstackwords
.linkset .LLNK382, .LLNK383 $M configure_out_port_no_ready.nstackwords
.linkset .LLNK381, .LLNK382 $M readData.nstackwords
.linkset .LLNK391, .LLNK381 + 8
.linkset .LLNK390, .LLNK391 + physical.nstackwords
.linkset .LLNK389, .LLNK390 $M .LLNK390
.linkset .LLNK388, .LLNK381 $M .LLNK389
.linkset .LLNK380, .LLNK381 $M .LLNK388
.linkset .LLNK379, .LLNK380 + 19
.linkset XUD_Endpoint1.nstackwords, .LLNK379
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG601:
.LDBG602:                            # line (435)
          .align    4
XUD_Endpoint1:
          entsp     0x13 
.LDBG603:
.L207:
          stw       r4, sp[0x9] 
.LDBG604:
          stw       r5, sp[0xa] 
.LDBG605:
          stw       r6, sp[0xb] 
.LDBG606:
          stw       r7, sp[0xc] 
.LDBG607:
          stw       r8, sp[0xd] 
.LDBG608:
          stw       r9, sp[0xe] 
.LDBG609:
          stw       r10, sp[0xf] 
.LDBG610:
          stw       r0, sp[0x5] 
          stw       r1, sp[0x6] 
          stw       r2, sp[0x7] 
          stw       r3, sp[0x8] 
          ldw       r9, sp[0x18] 
.LDBG588:
          ldw       r8, sp[0x19] 
.LDBG589:
          ldw       r7, sp[0x1a] 
.LDBG590:
.LDBG611:                            # line (435)
.LDBG586:
          getr      r6, 0x2
          getr      r5, 0x2
          setd      res[r6], r5
          setd      res[r5], r6
          getr      r4, 0x1
.LDBG591:
.LDBG612:                            # line (442)
          ldc       r0, 0x9
.LDBG613:                            # line (442)
          bl        GetParam 
          ldc       r1, 0x1
.LDBG614:                            # line (448)
          ldw       r0, sp[0x8] 
.L211:
          out       res[r0], r1 
.LDBG615:                            # line (449)
          ldw       r0, sp[0x14] 
.L212:
          out       res[r0], r1 
.LDBG616:                            # line (452)
          mov       r0, r7
          ldw       r1, sp[0x7] 
.LDBG617:                            # line (452)
          bl        configure_clock_src 
          ldc       r10, 0x0
.LDBG618:                            # line (456)
          ldw       r0, sp[0x16] 
          mov       r1, r7
          mov       r2, r10
.LDBG619:                            # line (456)
          bl        configure_out_port_no_ready 
.LDBG620:                            # line (457)
          ldw       r0, sp[0x17] 
          mov       r1, r7
          mov       r2, r10
.LDBG621:                            # line (457)
          bl        configure_out_port_no_ready 
.LDBG622:                            # line (458)
          mov       r0, r9
          mov       r1, r7
          mov       r2, r10
.LDBG623:                            # line (458)
          bl        configure_out_port_no_ready 
.LDBG624:                            # line (459)
          mov       r0, r8
          mov       r1, r7
          mov       r2, r10
.LDBG625:                            # line (459)
          bl        configure_out_port_no_ready 
.LDBG626:                            # line (460)
          ldw       r0, sp[0x15] 
          mov       r1, r7
          mov       r2, r10
.LDBG627:                            # line (460)
          bl        configure_out_port_no_ready 
.LDBG628:                            # line (463)
.LDBG629:                            # line (463)
          setc      res[r7], 0xf
.LDBG630:                            # line (465)
          getr      r10, 0x3
          getst     r0, res[r10]
          ldap      r11, .L205
          ldaw      r11, sp[0x0] 
          init      t[r0]:sp, r11
          ldap      r11, .L205
          init      t[r0]:pc, r11
          set       t[r0]:r7, r9
          set       t[r0]:r6, r8
          set       t[r0]:r5, r7
          set       t[r0]:r4, r5
          msync     res[r10]
.LDBG592:
.LDBG593:
.L206:
.L208:
.LDBG631:                            # line (467)
          ldaw      r1, dp[bufferA] 
          ldaw      r2, dp[bufferB] 
          ldaw      r3, dp[bufferC] 
          ldw       r0, sp[0x5] 
          stw       r6, sp[0x1] 
          ldc       r7, 0x300
          stw       r7, sp[0x2] 
          ldc       r7, 0x300
          stw       r7, sp[0x3] 
          ldc       r7, 0x300
          stw       r7, sp[0x4] 
.LDBG632:                            # line (467)
          bl        readData 
          msync     res[r10]
          mjoin     res[r10]
.LDBG594:
.LDBG595:
.L204:
.L209:
          freer     res[r10]
.LDBG587:
.LDBG633:                            # line (471)
          freer     res[r6]
          freer     res[r5]
          freer     res[r4]
.LDBG634:
          ldw       r4, sp[0x9] 
.LDBG635:
          ldw       r5, sp[0xa] 
.LDBG636:
          ldw       r6, sp[0xb] 
.LDBG637:
          ldw       r7, sp[0xc] 
.LDBG638:
          ldw       r8, sp[0xd] 
.LDBG639:
          ldw       r9, sp[0xe] 
.LDBG640:
          ldw       r10, sp[0xf] 
.LDBG641:
          retsp     0x13 
.LDBG596:
.LDBG642:
.LDBG597:
.L210:
.LDBG598:
.LDBG599:
.LDBG643:                            # line (469)
.L205:
.linkset .LLNK394, .LLNK381 - .LLNK381
.linkset .LLNK393, .LLNK394 + .LLNK381
.linkset .LLNK392, .LLNK393 + 8
          extsp     .LLNK392 
          .linkset .LLNK395, .LLNK391 + 6
          ldw       r1, sp[.LLNK395] 
          .linkset .LLNK396, .LLNK391 + 7
          ldw       r2, sp[.LLNK396] 
          .linkset .LLNK397, .LLNK391 + 8
          ldw       r3, sp[.LLNK397] 
          .linkset .LLNK398, .LLNK391 + 20
          ldw       r11, sp[.LLNK398] 
          .linkset .LLNK399, .LLNK391 + 21
          ldw       r10, sp[.LLNK399] 
          .linkset .LLNK400, .LLNK391 + 22
          ldw       r9, sp[.LLNK400] 
          .linkset .LLNK401, .LLNK391 + 23
          ldw       r8, sp[.LLNK401] 
.LDBG644:                            # line (469)
          mov       r0, r4
          stw       r11, sp[0x1] 
          stw       r10, sp[0x2] 
          stw       r9, sp[0x3] 
          stw       r8, sp[0x4] 
          stw       r7, sp[0x5] 
          stw       r6, sp[0x6] 
          stw       r5, sp[0x7] 
.LDBG645:                            # line (468)
          bl        physical 
          ssync     
          ssync     
.LDBG600:
.cc_bottom XUD_Endpoint1.function
.LDBG646:
          .section  .debug_frame, "",     @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
          .align    4
          .int      .LDBG648-.LDBG647
.LDBG647:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG601
          .int      .LDBG646-.LDBG601
          .byte     0x01
          .int      .LDBG603
          .byte     0x0e
          .uleb128  0x4c
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG604
          .byte     0x84
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG605
          .byte     0x85
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG606
          .byte     0x86
          .uleb128  0x2
          .byte     0x01
          .int      .LDBG607
          .byte     0x87
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG608
          .byte     0x88
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG609
          .byte     0x89
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG610
          .byte     0x8a
          .uleb128  0x1
          .byte     0x01
          .int      .LDBG634
          .byte     0x0a
          .byte     0x01
          .int      .LDBG635
          .byte     0xc4
          .byte     0x01
          .int      .LDBG636
          .byte     0xc5
          .byte     0x01
          .int      .LDBG637
          .byte     0xc6
          .byte     0x01
          .int      .LDBG638
          .byte     0xc7
          .byte     0x01
          .int      .LDBG639
          .byte     0xc8
          .byte     0x01
          .int      .LDBG640
          .byte     0xc9
          .byte     0x01
          .int      .LDBG641
          .byte     0xca
          .byte     0x01
          .int      .LDBG642
          .byte     0x0b
          .align    4, 0
.LDBG648:
.cc_bottom XUD_Endpoint1.function
.linkset .LLNK410, 2 + GetParam.maxchanends
.linkset .LLNK409, 2 $M .LLNK410
.linkset .LLNK411, 2 + configure_clock_src.maxchanends
.linkset .LLNK408, .LLNK409 $M .LLNK411
.linkset .LLNK412, 2 + configure_out_port_no_ready.maxchanends
.linkset .LLNK407, .LLNK408 $M .LLNK412
.linkset .LLNK413, 2 + configure_out_port_no_ready.maxchanends
.linkset .LLNK406, .LLNK407 $M .LLNK413
.linkset .LLNK414, 2 + configure_out_port_no_ready.maxchanends
.linkset .LLNK405, .LLNK406 $M .LLNK414
.linkset .LLNK415, 2 + configure_out_port_no_ready.maxchanends
.linkset .LLNK404, .LLNK405 $M .LLNK415
.linkset .LLNK416, 2 + configure_out_port_no_ready.maxchanends
.linkset .LLNK403, .LLNK404 $M .LLNK416
.linkset .LLNK418, readData.maxchanends + physical.maxchanends
.linkset .LLNK417, 2 + .LLNK418
.linkset .LLNK402, .LLNK403 $M .LLNK417
.linkset XUD_Endpoint1.maxchanends, .LLNK402
.linkset .LLNK427, 1 + GetParam.maxtimers
.linkset .LLNK426, 1 $M .LLNK427
.linkset .LLNK428, 1 + configure_clock_src.maxtimers
.linkset .LLNK425, .LLNK426 $M .LLNK428
.linkset .LLNK429, 1 + configure_out_port_no_ready.maxtimers
.linkset .LLNK424, .LLNK425 $M .LLNK429
.linkset .LLNK430, 1 + configure_out_port_no_ready.maxtimers
.linkset .LLNK423, .LLNK424 $M .LLNK430
.linkset .LLNK431, 1 + configure_out_port_no_ready.maxtimers
.linkset .LLNK422, .LLNK423 $M .LLNK431
.linkset .LLNK432, 1 + configure_out_port_no_ready.maxtimers
.linkset .LLNK421, .LLNK422 $M .LLNK432
.linkset .LLNK433, 1 + configure_out_port_no_ready.maxtimers
.linkset .LLNK420, .LLNK421 $M .LLNK433
.linkset .LLNK435, readData.maxtimers + physical.maxtimers
.linkset .LLNK434, 1 + .LLNK435
.linkset .LLNK419, .LLNK420 $M .LLNK434
.linkset XUD_Endpoint1.maxtimers, .LLNK419
.linkset .LLNK445, GetParam.maxthreads - 1
.linkset .LLNK444, 1 + .LLNK445
.linkset .LLNK443, 1 $M .LLNK444
.linkset .LLNK447, configure_clock_src.maxthreads - 1
.linkset .LLNK446, 1 + .LLNK447
.linkset .LLNK442, .LLNK443 $M .LLNK446
.linkset .LLNK449, configure_out_port_no_ready.maxthreads - 1
.linkset .LLNK448, 1 + .LLNK449
.linkset .LLNK441, .LLNK442 $M .LLNK448
.linkset .LLNK451, configure_out_port_no_ready.maxthreads - 1
.linkset .LLNK450, 1 + .LLNK451
.linkset .LLNK440, .LLNK441 $M .LLNK450
.linkset .LLNK453, configure_out_port_no_ready.maxthreads - 1
.linkset .LLNK452, 1 + .LLNK453
.linkset .LLNK439, .LLNK440 $M .LLNK452
.linkset .LLNK455, configure_out_port_no_ready.maxthreads - 1
.linkset .LLNK454, 1 + .LLNK455
.linkset .LLNK438, .LLNK439 $M .LLNK454
.linkset .LLNK457, configure_out_port_no_ready.maxthreads - 1
.linkset .LLNK456, 1 + .LLNK457
.linkset .LLNK437, .LLNK438 $M .LLNK456
.linkset .LLNK461, readData.maxthreads - 1
.linkset .LLNK460, 1 + .LLNK461
.linkset .LLNK459, 1 $M .LLNK460
.linkset .LLNK464, physical.maxthreads - 1
.linkset .LLNK463, 1 + .LLNK464
.linkset .LLNK462, 1 $M .LLNK463
.linkset .LLNK458, .LLNK459 + .LLNK462
.linkset .LLNK436, .LLNK437 $M .LLNK458
.linkset XUD_Endpoint1.maxthreads, .LLNK436
          .text
.LDBG649:
          .section .netinfo,       "", @netinfo
.globl p_but.info, "i:p"
p_but.info:
          .int      0x00040500
          .long stdcore
.globl p_usb_clk.info, "i:p"
p_usb_clk.info:
          .int      0x00010700
          .long stdcore + 4
.globl reg_write_port.info, "o:p"
reg_write_port.info:
          .int      0x00080200
          .long stdcore + 4
.globl reg_read_port.info, "i:p"
reg_read_port.info:
          .int      0x00080300
          .long stdcore + 4
.globl flag0_port.info, "i:p"
flag0_port.info:
          .int      0x00010d00
          .long stdcore + 4
.globl flag1_port.info, "i:p"
flag1_port.info:
          .int      0x00010e00
          .long stdcore + 4
.globl flag2_port.info, "i:p"
flag2_port.info:
          .int      0x00010f00
          .long stdcore + 4
.globl p_usb_txd.info, "o:p"
p_usb_txd.info:
          .int      0x00080000
          .long stdcore + 4
.globl p_usb_rxd.info, "p"
p_usb_rxd.info:
          .int      0x00080100
          .long stdcore + 4
.globl p_usb_rst.info, "o:p"
p_usb_rst.info:
          .int      0x00010000
          .long stdcore + 4
.globl p_test.info, "o:p"
p_test.info:
          .int      0x00010300
          .long stdcore + 4
.globl p_test2.info, "o:p"
p_test2.info:
          .int      0x00010100
          .long stdcore + 4
.globl clk.info, "ck"
clk.info:
          .int      0x00000306
          .long stdcore + 4
.globl p_aud_sclk.info, "p"
p_aud_sclk.info:
          .int      0x00010200
          .long stdcore + 8
.globl p_aud_lrclk.info, "i:p"
p_aud_lrclk.info:
          .int      0x00010000
          .long stdcore + 8
.globl p_aud_scl.info, "p"
p_aud_scl.info:
          .int      0x00040100
          .long stdcore + 8
.globl p_aud_sda.info, "p"
p_aud_sda.info:
          .int      0x00040000
          .long stdcore + 8
.globl p_aud_rst.info, "o:p"
p_aud_rst.info:
          .int      0x00040200
          .long stdcore + 8
.globl p_aud_clk.info, "o:p"
p_aud_clk.info:
          .int      0x00010d00
          .long stdcore + 8
.globl p_aud_dac1.info, "bo:p:32"
p_aud_dac1.info:
          .int      0x00010100
          .long stdcore + 8
.globl p_aud_dac2.info, "bo:p:32"
p_aud_dac2.info:
          .int      0x00010300
          .long stdcore + 8
.globl p_aud_dac3.info, "bo:p:32"
p_aud_dac3.info:
          .int      0x00010600
          .long stdcore + 8
.globl p_aud_dac4.info, "bo:p:32"
p_aud_dac4.info:
          .int      0x00010400
          .long stdcore + 8
.globl clk_aud_bit.info, "ck"
clk_aud_bit.info:
          .int      0x00000306
          .long stdcore + 8
.extern ep0_thread, "f{0}(chd,chd)"
.extern audio, "f{0}(chd,chd,chd,o:p,p,i:p,bo:p:32,bo:p:32,bo:p:32,p,p)"
main.parinfo.debugstring0:
.asciiz "# 535 \"./tests/audio/src/audio.xc\""
main.parinfo.debugstring1:
.asciiz "# 526 \"./tests/audio/src/audio.xc\""
.cc_top main.parinfo.cc, main.parinfo
.globl main.parinfo, "pi"
.type  main.parinfo, @object
main.parinfo:
          .int      0x00000003
          .long __main_default_network
          .long main.parinfo.debugstring0
          .long main.parinfo.debugstring1
          .int      0x00000005
          .int      $N __main_xm_0
          .long stdcore
          .int      0x00000001
          .int      0x00000000
          .int      $N __main_xm_1
          .long stdcore + 4
          .int      0x00000005
          .int      0x00000001
          .int      0x00000002
          .int      0x00000003
          .int      0x00000004
          .int      0x00000000
          .int      $N __main_xm_2
          .long stdcore + 8
          .int      0x00000002
          .int      0x00000005
          .int      0x00000003
          .int      $N __main_xm_3
          .long stdcore + 8
          .int      0x00000001
          .int      0x00000002
          .int      $N __main_xm_4
          .long stdcore + 8
          .int      0x00000001
          .int      0x00000004
.cc_bottom main.parinfo.cc
          .text
.LDBG650:
.call __main_xm_4, doClock
.globpassesref doClock, p_aud_clk, "./tests/audio/src/audio.xc:547: error: `__main_xm_4' makes alias of global 'p_aud_clk' in call to `doClock'"
.globwrite __main_xm_4, p_aud_clk, "./tests/audio/src/audio.xc:547: error: previously used here"
.globread __main_xm_4, p_aud_clk, "./tests/audio/src/audio.xc:547: error: previously used here"
.globl __main_xm_4, "f{0}(chd)"
.globl __main_xm_4.nstackwords
.globl __main_xm_4.maxthreads
.globl __main_xm_4.maxtimers
.globl __main_xm_4.maxchanends
.globl __main_xm_4.maxsync
.type  __main_xm_4, @function
.linkset __main_xm_4.locnoside, 1
.linkset __main_xm_4.locnochandec, 1
.linkset .LLNK466, doClock.nstackwords $M doClock.nstackwords
.linkset .LLNK465, .LLNK466 + 1
.linkset __main_xm_4.nstackwords, .LLNK465
.cc_top __main_xm_4.function,__main_xm_4
.LDBG653:
.LDBG654:                            # line (526)
          .align    4
__main_xm_4:
          entsp     0x1 
.LDBG655:
.L213:
.LDBG651:
.LDBG656:                            # line (526)
.LDBG657:                            # line (547)
          ldw       r1, dp[p_aud_clk] 
.LDBG658:                            # line (547)
          bl        doClock 
.LDBG659:                            # line (552)
.LDBG660:
          retsp     0x1 
.LDBG652:
.LDBG661:
.cc_bottom __main_xm_4.function
.LDBG662:
          .section  .debug_frame, "",     @progbits
.cc_top __main_xm_4.function,__main_xm_4
          .align    4
          .int      .LDBG664-.LDBG663
.LDBG663:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG653
          .int      .LDBG662-.LDBG653
          .byte     0x01
          .int      .LDBG655
          .byte     0x0e
          .uleb128  0x4
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG660
          .byte     0x0a
          .byte     0x01
          .int      .LDBG661
          .byte     0x0b
          .align    4, 0
.LDBG664:
.cc_bottom __main_xm_4.function
.linkset __main_xm_4.maxchanends, doClock.maxchanends
.linkset __main_xm_4.maxtimers, doClock.maxtimers
.linkset .LLNK469, doClock.maxthreads - 1
.linkset .LLNK468, 1 + .LLNK469
.linkset .LLNK467, 1 $M .LLNK468
.linkset __main_xm_4.maxthreads, .LLNK467
          .text
.LDBG665:
.LDBG666:
.call __main_xm_3, XUD_Endpoint1
.globpassesref XUD_Endpoint1, p_aud_rst, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_rst' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_rst, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_rst, "./tests/audio/src/audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_sclk, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_sclk' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_sclk, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_sclk, "./tests/audio/src/audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_scl, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_scl' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_scl, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_scl, "./tests/audio/src/audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_sda, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_sda' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_sda, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_sda, "./tests/audio/src/audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_lrclk, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_lrclk' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_lrclk, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_lrclk, "./tests/audio/src/audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac1, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac1' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac1, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac1, "./tests/audio/src/audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac2, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac2' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac2, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac2, "./tests/audio/src/audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac3, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac3' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac3, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac3, "./tests/audio/src/audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac4, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac4' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac4, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac4, "./tests/audio/src/audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, clk_aud_bit, "./tests/audio/src/audio.xc:544: error: `__main_xm_3' makes alias of global 'clk_aud_bit' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, clk_aud_bit, "./tests/audio/src/audio.xc:544: error: previously used here"
.globread __main_xm_3, clk_aud_bit, "./tests/audio/src/audio.xc:545: error: previously used here"
.globl __main_xm_3, "f{0}(chd)"
.globl __main_xm_3.nstackwords
.globl __main_xm_3.maxthreads
.globl __main_xm_3.maxtimers
.globl __main_xm_3.maxchanends
.globl __main_xm_3.maxsync
.type  __main_xm_3, @function
.linkset __main_xm_3.locnoside, 1
.linkset __main_xm_3.locnochandec, 1
.linkset .LLNK471, XUD_Endpoint1.nstackwords $M XUD_Endpoint1.nstackwords
.linkset .LLNK470, .LLNK471 + 8
.linkset __main_xm_3.nstackwords, .LLNK470
.cc_top __main_xm_3.function,__main_xm_3
.LDBG669:
.LDBG670:                            # line (526)
          .align    4
__main_xm_3:
          entsp     0x8 
.LDBG671:
.L214:
.LDBG667:
.LDBG672:                            # line (526)
.LDBG673:                            # line (545)
          ldw       r1, dp[p_aud_rst] 
          ldw       r2, dp[p_aud_sclk] 
          ldw       r3, dp[p_aud_scl] 
          ldw       r11, dp[p_aud_sda] 
          stw       r11, sp[0x1] 
          ldw       r11, dp[p_aud_lrclk] 
          stw       r11, sp[0x2] 
          ldw       r11, dp[p_aud_dac1] 
          stw       r11, sp[0x3] 
          ldw       r11, dp[p_aud_dac2] 
          stw       r11, sp[0x4] 
          ldw       r11, dp[p_aud_dac3] 
          stw       r11, sp[0x5] 
          ldw       r11, dp[p_aud_dac4] 
          stw       r11, sp[0x6] 
          ldw       r11, dp[clk_aud_bit] 
          stw       r11, sp[0x7] 
.LDBG674:                            # line (544)
          bl        XUD_Endpoint1 
.LDBG675:                            # line (552)
.LDBG676:
          retsp     0x8 
.LDBG668:
.LDBG677:
.cc_bottom __main_xm_3.function
.LDBG678:
          .section  .debug_frame, "",     @progbits
.cc_top __main_xm_3.function,__main_xm_3
          .align    4
          .int      .LDBG680-.LDBG679
.LDBG679:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG669
          .int      .LDBG678-.LDBG669
          .byte     0x01
          .int      .LDBG671
          .byte     0x0e
          .uleb128  0x20
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG676
          .byte     0x0a
          .byte     0x01
          .int      .LDBG677
          .byte     0x0b
          .align    4, 0
.LDBG680:
.cc_bottom __main_xm_3.function
.linkset __main_xm_3.maxchanends, XUD_Endpoint1.maxchanends
.linkset __main_xm_3.maxtimers, XUD_Endpoint1.maxtimers
.linkset .LLNK474, XUD_Endpoint1.maxthreads - 1
.linkset .LLNK473, 1 + .LLNK474
.linkset .LLNK472, 1 $M .LLNK473
.linkset __main_xm_3.maxthreads, .LLNK472
          .text
.LDBG681:
.LDBG682:
.call __main_xm_2, ep0_thread
.globl __main_xm_2, "f{0}(chd,chd)"
.globl __main_xm_2.nstackwords
.globl __main_xm_2.maxthreads
.globl __main_xm_2.maxtimers
.globl __main_xm_2.maxchanends
.globl __main_xm_2.maxsync
.type  __main_xm_2, @function
.linkset __main_xm_2.locnoside, 1
.linkset __main_xm_2.locnochandec, 1
.linkset .LLNK476, ep0_thread.nstackwords $M ep0_thread.nstackwords
.linkset .LLNK475, .LLNK476 + 1
.linkset __main_xm_2.nstackwords, .LLNK475
.cc_top __main_xm_2.function,__main_xm_2
.LDBG686:
.LDBG687:                            # line (526)
          .align    4
__main_xm_2:
          entsp     0x1 
.LDBG688:
.L215:
          mov       r2, r0
.LDBG683:
.LDBG684:
.LDBG689:                            # line (526)
.LDBG690:                            # line (541)
          mov       r0, r1
          mov       r1, r2
.LDBG691:                            # line (541)
          bl        ep0_thread 
.LDBG692:                            # line (552)
.LDBG693:
          retsp     0x1 
.LDBG685:
.LDBG694:
.cc_bottom __main_xm_2.function
.LDBG695:
          .section  .debug_frame, "",     @progbits
.cc_top __main_xm_2.function,__main_xm_2
          .align    4
          .int      .LDBG697-.LDBG696
.LDBG696:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG686
          .int      .LDBG695-.LDBG686
          .byte     0x01
          .int      .LDBG688
          .byte     0x0e
          .uleb128  0x4
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG693
          .byte     0x0a
          .byte     0x01
          .int      .LDBG694
          .byte     0x0b
          .align    4, 0
.LDBG697:
.cc_bottom __main_xm_2.function
.linkset __main_xm_2.maxchanends, ep0_thread.maxchanends
.linkset __main_xm_2.maxtimers, ep0_thread.maxtimers
.linkset .LLNK479, ep0_thread.maxthreads - 1
.linkset .LLNK478, 1 + .LLNK479
.linkset .LLNK477, 1 $M .LLNK478
.linkset __main_xm_2.maxthreads, .LLNK477
          .text
.LDBG698:
.LDBG699:
.call __main_xm_1, XUD_Manager
.globpassesref XUD_Manager, p_test, "./tests/audio/src/audio.xc:539: error: `__main_xm_1' makes alias of global 'p_test' in call to `XUD_Manager'"
.globwrite __main_xm_1, p_test, "./tests/audio/src/audio.xc:539: error: previously used here"
.globread __main_xm_1, p_test, "./tests/audio/src/audio.xc:539: error: previously used here"
.globpassesref XUD_Manager, p_test2, "./tests/audio/src/audio.xc:539: error: `__main_xm_1' makes alias of global 'p_test2' in call to `XUD_Manager'"
.globwrite __main_xm_1, p_test2, "./tests/audio/src/audio.xc:539: error: previously used here"
.globread __main_xm_1, p_test2, "./tests/audio/src/audio.xc:539: error: previously used here"
.globl __main_xm_1, "f{0}(chd,chd,chd,chd,chd)"
.globl __main_xm_1.nstackwords
.globl __main_xm_1.maxthreads
.globl __main_xm_1.maxtimers
.globl __main_xm_1.maxchanends
.globl __main_xm_1.maxsync
.type  __main_xm_1, @function
.linkset __main_xm_1.locnoside, 1
.linkset __main_xm_1.locnochandec, 1
.linkset .LLNK481, XUD_Manager.nstackwords $M XUD_Manager.nstackwords
.linkset .LLNK480, .LLNK481 + 5
.linkset __main_xm_1.nstackwords, .LLNK480
.cc_top __main_xm_1.function,__main_xm_1
.LDBG706:
.LDBG707:                            # line (526)
          .align    4
__main_xm_1:
          entsp     0x5 
.LDBG708:
.L216:
          stw       r4, sp[0x4] 
.LDBG709:
          mov       r11, r0
.LDBG700:
.LDBG701:
.LDBG702:
          mov       r4, r3
.LDBG703:
          ldw       r3, sp[0x6] 
.LDBG704:
.LDBG710:                            # line (526)
.LDBG711:                            # line (539)
          mov       r0, r2
          mov       r2, r3
          mov       r3, r11
          stw       r4, sp[0x1] 
          ldw       r4, dp[p_test] 
          stw       r4, sp[0x2] 
          ldw       r4, dp[p_test2] 
          stw       r4, sp[0x3] 
.LDBG712:                            # line (539)
          bl        XUD_Manager 
.LDBG713:                            # line (552)
.LDBG714:
          ldw       r4, sp[0x4] 
.LDBG715:
          retsp     0x5 
.LDBG705:
.LDBG716:
.cc_bottom __main_xm_1.function
.LDBG717:
          .section  .debug_frame, "",     @progbits
.cc_top __main_xm_1.function,__main_xm_1
          .align    4
          .int      .LDBG719-.LDBG718
.LDBG718:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG706
          .int      .LDBG717-.LDBG706
          .byte     0x01
          .int      .LDBG708
          .byte     0x0e
          .uleb128  0x14
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG709
          .byte     0x84
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG714
          .byte     0x0a
          .byte     0x01
          .int      .LDBG715
          .byte     0xc4
          .byte     0x01
          .int      .LDBG716
          .byte     0x0b
          .align    4, 0
.LDBG719:
.cc_bottom __main_xm_1.function
.linkset __main_xm_1.maxchanends, XUD_Manager.maxchanends
.linkset __main_xm_1.maxtimers, XUD_Manager.maxtimers
.linkset .LLNK484, XUD_Manager.maxthreads - 1
.linkset .LLNK483, 1 + .LLNK484
.linkset .LLNK482, 1 $M .LLNK483
.linkset __main_xm_1.maxthreads, .LLNK482
          .text
.LDBG720:
.LDBG721:
.call __main_xm_0, XUD_Endpoint3
.globpassesref XUD_Endpoint3, p_but, "./tests/audio/src/audio.xc:537: error: `__main_xm_0' makes alias of global 'p_but' in call to `XUD_Endpoint3'"
.globwrite __main_xm_0, p_but, "./tests/audio/src/audio.xc:537: error: previously used here"
.globread __main_xm_0, p_but, "./tests/audio/src/audio.xc:537: error: previously used here"
.globl __main_xm_0, "f{0}(chd)"
.globl __main_xm_0.nstackwords
.globl __main_xm_0.maxthreads
.globl __main_xm_0.maxtimers
.globl __main_xm_0.maxchanends
.globl __main_xm_0.maxsync
.type  __main_xm_0, @function
.linkset __main_xm_0.locnoside, 1
.linkset __main_xm_0.locnochandec, 1
.linkset .LLNK486, XUD_Endpoint3.nstackwords $M XUD_Endpoint3.nstackwords
.linkset .LLNK485, .LLNK486 + 1
.linkset __main_xm_0.nstackwords, .LLNK485
.cc_top __main_xm_0.function,__main_xm_0
.LDBG724:
.LDBG725:                            # line (526)
          .align    4
__main_xm_0:
          entsp     0x1 
.LDBG726:
.L217:
.LDBG722:
.LDBG727:                            # line (526)
.LDBG728:                            # line (537)
          ldw       r1, dp[p_but] 
.LDBG729:                            # line (537)
          bl        XUD_Endpoint3 
.LDBG730:                            # line (552)
.LDBG731:
          retsp     0x1 
.LDBG723:
.LDBG732:
.cc_bottom __main_xm_0.function
.LDBG733:
          .section  .debug_frame, "",     @progbits
.cc_top __main_xm_0.function,__main_xm_0
          .align    4
          .int      .LDBG735-.LDBG734
.LDBG734:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG724
          .int      .LDBG733-.LDBG724
          .byte     0x01
          .int      .LDBG726
          .byte     0x0e
          .uleb128  0x4
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG731
          .byte     0x0a
          .byte     0x01
          .int      .LDBG732
          .byte     0x0b
          .align    4, 0
.LDBG735:
.cc_bottom __main_xm_0.function
.linkset __main_xm_0.maxchanends, XUD_Endpoint3.maxchanends
.linkset __main_xm_0.maxtimers, XUD_Endpoint3.maxtimers
.linkset .LLNK489, XUD_Endpoint3.maxthreads - 1
.linkset .LLNK488, 1 + .LLNK489
.linkset .LLNK487, 1 $M .LLNK488
.linkset __main_xm_0.maxthreads, .LLNK487
          .text
.LDBG736:
.par GetInt, GetParam, "./tests/audio/src/audio.xc:186: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:188: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:190: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:192: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:202: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:204: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:206: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "./tests/audio/src/audio.xc:208: error: use of `%s' violates variable disjointness rules"
.par readData, physical, "./tests/audio/src/audio.xc:465: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, XUD_Manager, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, ep0_thread, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, XUD_Endpoint1, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, doClock, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, ep0_thread, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, XUD_Endpoint1, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, doClock, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par ep0_thread, doClock, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint1, doClock, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par ep0_thread, XUD_Endpoint1, "./tests/audio/src/audio.xc:535: error: use of `%s' violates variable disjointness rules"
# Thread names for recovering thread graph in linker
.set thread.anon.0, 0  #unreal
.set thread.anon.1, 0  #unreal
.set thread.anon.2, 0  #unreal
.set thread.anon.3, 0  #unreal
.set thread.anon.4, 0  #unreal
.set thread.anon.5, 0  #unreal
.set thread.anon.6, 0  #unreal
.set thread.anon.7, 0  #unreal
.set thread.anon.8, 0  #unreal
.set thread.anon.9, 0  #unreal
.set thread.anon.10, 0  #unreal
.set thread.anon.11, 0  #unreal
.set thread.anon.12, 0  #unreal
.set thread.anon.13, 0  #unreal
.set thread.anon.14, 0  #unreal
.set thread.anon.15, 0  #unreal
.LDBG737:
.extern audio_init, "f{0}(o:p,p,p,p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,i:p,ck)"
.globl bufferC, "a(768:si)"
.type  bufferC, @object
          .section .dp.bss,        "adw", @nobits
.cc_top bufferC.data, bufferC
          .align    4
bufferC:
.globl bufferC.globound
          .set      bufferC.globound,768
          .space    3072
.cc_bottom bufferC.data
          .align    4
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC7:
          .int      768
          .set      bufferC.locbound,s.LC7
.globl bufferB, "a(768:si)"
.type  bufferB, @object
          .section .dp.bss,        "adw", @nobits
.cc_top bufferB.data, bufferB
          .align    4
bufferB:
.globl bufferB.globound
          .set      bufferB.globound,768
          .space    3072
.cc_bottom bufferB.data
          .align    4
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC8:
          .int      768
          .set      bufferB.locbound,s.LC8
.globl bufferA, "a(768:si)"
.type  bufferA, @object
          .section .dp.bss,        "adw", @nobits
.cc_top bufferA.data, bufferA
          .align    4
bufferA:
.globl bufferA.globound
          .set      bufferA.globound,768
          .space    3072
.cc_bottom bufferA.data
          .align    4
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC9:
          .int      768
          .set      bufferA.locbound,s.LC9
.locl isLeft, "si"
          .section .dp.bss,        "adw", @nobits
.cc_top isLeft.data, isLeft
          .align    4
isLeft:
          .int      0x00000000
.cc_bottom isLeft.data
.extern stdcore, "a(*:cr)"
          .align    4
          .section  .debug_info,   "",    @progbits
.LDBG740:
          .int      .LDBG742-.LDBG741
.LDBG741:
          .short    0x0003
          .long     .LDBG738         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG737         # high address
          .asciiz   "./tests/audio/src/audio.xc"
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG739         # offset in .debug_lineprog
.LDBG743:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG744:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG745:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG746:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG747:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG748:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG749:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG750:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG751:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG752:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG753:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG754:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG755:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG756:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG757:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG758:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG759:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top ep3dead.function,ep3dead
.LDBG760:
          .uleb128  0x3
          .asciiz   "ep3dead"
          .byte     0x01
          .byte     0x1e
          .byte     0x01
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG18          # high address
          .byte     0x00
.cc_bottom ep3dead.function
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG761:
          .uleb128  0x4
          .asciiz   "XUD_Endpoint3"
          .byte     0x01
          .byte     0x26
          .byte     0x01
          .int      .LDBG745-.LDBG740
          .byte     0x01
          .long     .LDBG19          # low address
          .long     .LDBG54          # high address
          .uleb128  0x5
          .asciiz   "c_ep2_dat"
          .byte     0x01
          .short    0x0025
          .int      .LDBG751-.LDBG740
          .int      .LDBG762
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG762:
          .int      .LDBG31-.LDBG0
          .int      .LDBG32-.LDBG0
          .short    .LDBG764-.LDBG763
.LDBG763:
          .byte     0x54
.LDBG764:
          .int      .LDBG28-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG766-.LDBG765
.LDBG765:
          .byte     0x54
.LDBG766:
          .int      .LDBG26-.LDBG0
          .int      .LDBG27-.LDBG0
          .short    .LDBG768-.LDBG767
.LDBG767:
          .byte     0x54
.LDBG768:
          .int      .LDBG22-.LDBG0
          .int      .LDBG25-.LDBG0
          .short    .LDBG770-.LDBG769
.LDBG769:
          .byte     0x54
.LDBG770:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint3.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "p_but"
          .byte     0x01
          .short    0x0025
          .int      .LDBG754-.LDBG740
          .int      .LDBG771
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG771:
          .int      .LDBG26-.LDBG0
          .int      .LDBG27-.LDBG0
          .short    .LDBG773-.LDBG772
.LDBG772:
          .byte     0x55
.LDBG773:
          .int      .LDBG28-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG775-.LDBG774
.LDBG774:
          .byte     0x55
.LDBG775:
          .int      .LDBG23-.LDBG0
          .int      .LDBG25-.LDBG0
          .short    .LDBG777-.LDBG776
.LDBG776:
          .byte     0x55
.LDBG777:
          .int      .LDBG31-.LDBG0
          .int      .LDBG32-.LDBG0
          .short    .LDBG779-.LDBG778
.LDBG778:
          .byte     0x55
.LDBG779:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint3.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "loop"
          .byte     0x01
          .short    0x0027
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG780
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG780:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint3.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "butVal"
          .byte     0x01
          .short    0x0028
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG781
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG781:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG783-.LDBG782
.LDBG782:
          .byte     0x50
.LDBG783:
          .int      .LDBG24-.LDBG0
          .int      .LDBG25-.LDBG0
          .short    .LDBG785-.LDBG784
.LDBG784:
          .byte     0x50
.LDBG785:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint3.function
          .section  .debug_info,   "",    @progbits
.LDBG786:
          .uleb128  0x7
          .int      .LDBG750-.LDBG740
          .int      0x00000008
          .uleb128  0x8
          .byte     0x00
          .int      0x00000007
          .byte     0x00
          .uleb128  0x6
          .asciiz   "buffer"
          .byte     0x01
          .short    0x002c
          .short    0x0000
          .int      .LDBG786-.LDBG740
          .int      .LDBG787
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
.LDBG787:
          .int      .LDBG20-.LDBG0
          .int      .LDBG21-.LDBG0
          .short    .LDBG789-.LDBG788
.LDBG788:
          .byte     0x7e
          .sleb128  0x8
.LDBG789:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint3.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom XUD_Endpoint3.function
.cc_top dummyEP.function,dummyEP
.LDBG790:
          .uleb128  0x4
          .asciiz   "dummyEP"
          .byte     0x01
          .byte     0x45
          .byte     0x01
          .int      .LDBG745-.LDBG740
          .byte     0x01
          .long     .LDBG55          # low address
          .long     .LDBG78          # high address
          .uleb128  0x5
          .asciiz   "c_dat"
          .byte     0x01
          .short    0x0044
          .int      .LDBG751-.LDBG740
          .int      .LDBG791
          .section  .debug_loc,    "",    @progbits
.cc_top dummyEP.function,dummyEP
.LDBG791:
          .int      .LDBG59-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG793-.LDBG792
.LDBG792:
          .byte     0x50
.LDBG793:
          .int      .LDBG56-.LDBG0
          .int      .LDBG58-.LDBG0
          .short    .LDBG795-.LDBG794
.LDBG794:
          .byte     0x50
.LDBG795:
          .int      0x00000000
          .int      0x00000000
.cc_bottom dummyEP.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_ctl"
          .byte     0x01
          .short    0x0044
          .int      .LDBG751-.LDBG740
          .int      .LDBG796
          .section  .debug_loc,    "",    @progbits
.cc_top dummyEP.function,dummyEP
.LDBG796:
          .int      .LDBG57-.LDBG0
          .int      .LDBG58-.LDBG0
          .short    .LDBG798-.LDBG797
.LDBG797:
          .byte     0x51
.LDBG798:
          .int      .LDBG59-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG800-.LDBG799
.LDBG799:
          .byte     0x51
.LDBG800:
          .int      0x00000000
          .int      0x00000000
.cc_bottom dummyEP.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom dummyEP.function
.cc_top doClock.function,doClock
.LDBG801:
          .uleb128  0x3
          .asciiz   "doClock"
          .byte     0x01
          .byte     0x51
          .byte     0x01
          .byte     0x01
          .long     .LDBG79          # low address
          .long     .LDBG133         # high address
          .uleb128  0x5
          .asciiz   "c_sof"
          .byte     0x01
          .short    0x0050
          .int      .LDBG751-.LDBG740
          .int      .LDBG802
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG802:
          .int      .LDBG82-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG804-.LDBG803
.LDBG803:
          .byte     0x50
.LDBG804:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG806-.LDBG805
.LDBG805:
          .byte     0x50
.LDBG806:
          .int      .LDBG91-.LDBG0
          .int      .LDBG93-.LDBG0
          .short    .LDBG808-.LDBG807
.LDBG807:
          .byte     0x50
.LDBG808:
          .int      .LDBG96-.LDBG0
          .int      .LDBG97-.LDBG0
          .short    .LDBG810-.LDBG809
.LDBG809:
          .byte     0x50
.LDBG810:
          .int      .LDBG98-.LDBG0
          .int      .LDBG99-.LDBG0
          .short    .LDBG812-.LDBG811
.LDBG811:
          .byte     0x50
.LDBG812:
          .int      .LDBG100-.LDBG0
          .int      .LDBG101-.LDBG0
          .short    .LDBG814-.LDBG813
.LDBG813:
          .byte     0x50
.LDBG814:
          .int      .LDBG102-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG816-.LDBG815
.LDBG815:
          .byte     0x50
.LDBG816:
          .int      .LDBG104-.LDBG0
          .int      .LDBG106-.LDBG0
          .short    .LDBG818-.LDBG817
.LDBG817:
          .byte     0x50
.LDBG818:
          .int      .LDBG94-.LDBG0
          .int      .LDBG95-.LDBG0
          .short    .LDBG820-.LDBG819
.LDBG819:
          .byte     0x50
.LDBG820:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "p"
          .byte     0x01
          .short    0x0050
          .int      .LDBG754-.LDBG740
          .int      .LDBG821
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG821:
          .int      .LDBG83-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG823-.LDBG822
.LDBG822:
          .byte     0x51
.LDBG823:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG825-.LDBG824
.LDBG824:
          .byte     0x51
.LDBG825:
          .int      .LDBG96-.LDBG0
          .int      .LDBG97-.LDBG0
          .short    .LDBG827-.LDBG826
.LDBG826:
          .byte     0x51
.LDBG827:
          .int      .LDBG98-.LDBG0
          .int      .LDBG99-.LDBG0
          .short    .LDBG829-.LDBG828
.LDBG828:
          .byte     0x51
.LDBG829:
          .int      .LDBG100-.LDBG0
          .int      .LDBG101-.LDBG0
          .short    .LDBG831-.LDBG830
.LDBG830:
          .byte     0x51
.LDBG831:
          .int      .LDBG102-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG833-.LDBG832
.LDBG832:
          .byte     0x51
.LDBG833:
          .int      .LDBG104-.LDBG0
          .int      .LDBG106-.LDBG0
          .short    .LDBG835-.LDBG834
.LDBG834:
          .byte     0x51
.LDBG835:
          .int      .LDBG94-.LDBG0
          .int      .LDBG95-.LDBG0
          .short    .LDBG837-.LDBG836
.LDBG836:
          .byte     0x51
.LDBG837:
          .int      .LDBG91-.LDBG0
          .int      .LDBG93-.LDBG0
          .short    .LDBG839-.LDBG838
.LDBG838:
          .byte     0x51
.LDBG839:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "val"
          .byte     0x01
          .short    0x0052
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG840
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG840:
          .int      .LDBG84-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG842-.LDBG841
.LDBG841:
          .byte     0x54
.LDBG842:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG844-.LDBG843
.LDBG843:
          .byte     0x54
.LDBG844:
          .int      .LDBG94-.LDBG0
          .int      .LDBG95-.LDBG0
          .short    .LDBG846-.LDBG845
.LDBG845:
          .byte     0x54
.LDBG846:
          .int      .LDBG96-.LDBG0
          .int      .LDBG97-.LDBG0
          .short    .LDBG848-.LDBG847
.LDBG847:
          .byte     0x54
.LDBG848:
          .int      .LDBG98-.LDBG0
          .int      .LDBG99-.LDBG0
          .short    .LDBG850-.LDBG849
.LDBG849:
          .byte     0x54
.LDBG850:
          .int      .LDBG100-.LDBG0
          .int      .LDBG101-.LDBG0
          .short    .LDBG852-.LDBG851
.LDBG851:
          .byte     0x54
.LDBG852:
          .int      .LDBG102-.LDBG0
          .int      .LDBG103-.LDBG0
          .short    .LDBG854-.LDBG853
.LDBG853:
          .byte     0x54
.LDBG854:
          .int      .LDBG104-.LDBG0
          .int      .LDBG106-.LDBG0
          .short    .LDBG856-.LDBG855
.LDBG855:
          .byte     0x54
.LDBG856:
          .int      .LDBG91-.LDBG0
          .int      .LDBG93-.LDBG0
          .short    .LDBG858-.LDBG857
.LDBG857:
          .byte     0x54
.LDBG858:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "t"
          .byte     0x01
          .short    0x0053
          .short    0x0000
          .int      .LDBG752-.LDBG740
          .int      .LDBG859
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG859:
          .int      .LDBG85-.LDBG0
          .int      .LDBG87-.LDBG0
          .short    .LDBG861-.LDBG860
.LDBG860:
          .byte     0x53
.LDBG861:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x0054
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .int      .LDBG862
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG862:
          .int      .LDBG87-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG864-.LDBG863
.LDBG863:
          .byte     0x53
.LDBG864:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "count"
          .byte     0x01
          .short    0x0055
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG865
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG865:
          .int      .LDBG86-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG867-.LDBG866
.LDBG866:
          .byte     0x52
.LDBG867:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG869-.LDBG868
.LDBG868:
          .byte     0x52
.LDBG869:
          .int      .LDBG91-.LDBG0
          .int      .LDBG93-.LDBG0
          .short    .LDBG871-.LDBG870
.LDBG870:
          .byte     0x52
.LDBG871:
          .int      .LDBG94-.LDBG0
          .int      .LDBG95-.LDBG0
          .short    .LDBG873-.LDBG872
.LDBG872:
          .byte     0x52
.LDBG873:
          .int      .LDBG96-.LDBG0
          .int      .LDBG97-.LDBG0
          .short    .LDBG875-.LDBG874
.LDBG874:
          .byte     0x52
.LDBG875:
          .int      .LDBG98-.LDBG0
          .int      .LDBG99-.LDBG0
          .short    .LDBG877-.LDBG876
.LDBG876:
          .byte     0x52
.LDBG877:
          .int      .LDBG100-.LDBG0
          .int      .LDBG101-.LDBG0
          .short    .LDBG879-.LDBG878
.LDBG878:
          .byte     0x52
.LDBG879:
          .int      .LDBG105-.LDBG0
          .int      .LDBG106-.LDBG0
          .short    .LDBG881-.LDBG880
.LDBG880:
          .byte     0x52
.LDBG881:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "tmp"
          .byte     0x01
          .short    0x0056
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG882
          .section  .debug_loc,    "",    @progbits
.cc_top doClock.function,doClock
.LDBG882:
          .int      .LDBG92-.LDBG0
          .int      .LDBG93-.LDBG0
          .short    .LDBG884-.LDBG883
.LDBG883:
          .byte     0x53
.LDBG884:
          .int      0x00000000
          .int      0x00000000
.cc_bottom doClock.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom doClock.function
.cc_top data.data,data
          .uleb128  0x9
          .asciiz   "data"
          .byte     0x01
          .short    0x006f
          .int      .LDBG745-.LDBG740
          .short    .LDBG886-.LDBG885
.LDBG885:
          .byte     0x03
          .int      data
.LDBG886:
.cc_bottom data.data
.cc_top readData.function,readData
.LDBG887:
          .uleb128  0x4
          .asciiz   "readData"
          .byte     0x01
          .byte     0x74
          .byte     0x01
          .int      .LDBG745-.LDBG740
          .byte     0x01
          .long     .LDBG134         # low address
          .long     .LDBG207         # high address
          .uleb128  0x5
          .asciiz   "c"
          .byte     0x01
          .short    0x0073
          .int      .LDBG751-.LDBG740
          .int      .LDBG888
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG888:
          .int      .LDBG150-.LDBG0
          .int      .LDBG151-.LDBG0
          .short    .LDBG890-.LDBG889
.LDBG889:
          .byte     0x5a
.LDBG890:
          .int      .LDBG162-.LDBG0
          .int      .LDBG163-.LDBG0
          .short    .LDBG892-.LDBG891
.LDBG891:
          .byte     0x5a
.LDBG892:
          .int      .LDBG148-.LDBG0
          .int      .LDBG149-.LDBG0
          .short    .LDBG894-.LDBG893
.LDBG893:
          .byte     0x5a
.LDBG894:
          .int      .LDBG144-.LDBG0
          .int      .LDBG145-.LDBG0
          .short    .LDBG896-.LDBG895
.LDBG895:
          .byte     0x5a
.LDBG896:
          .int      .LDBG160-.LDBG0
          .int      .LDBG161-.LDBG0
          .short    .LDBG898-.LDBG897
.LDBG897:
          .byte     0x5a
.LDBG898:
          .int      .LDBG146-.LDBG0
          .int      .LDBG147-.LDBG0
          .short    .LDBG900-.LDBG899
.LDBG899:
          .byte     0x5a
.LDBG900:
          .int      .LDBG158-.LDBG0
          .int      .LDBG159-.LDBG0
          .short    .LDBG902-.LDBG901
.LDBG901:
          .byte     0x5a
.LDBG902:
          .int      .LDBG140-.LDBG0
          .int      .LDBG141-.LDBG0
          .short    .LDBG904-.LDBG903
.LDBG903:
          .byte     0x5a
.LDBG904:
          .int      .LDBG142-.LDBG0
          .int      .LDBG143-.LDBG0
          .short    .LDBG906-.LDBG905
.LDBG905:
          .byte     0x5a
.LDBG906:
          .int      .LDBG137-.LDBG0
          .int      .LDBG139-.LDBG0
          .short    .LDBG908-.LDBG907
.LDBG907:
          .byte     0x5a
.LDBG908:
          .int      .LDBG156-.LDBG0
          .int      .LDBG157-.LDBG0
          .short    .LDBG910-.LDBG909
.LDBG909:
          .byte     0x5a
.LDBG910:
          .int      .LDBG154-.LDBG0
          .int      .LDBG155-.LDBG0
          .short    .LDBG912-.LDBG911
.LDBG911:
          .byte     0x5a
.LDBG912:
          .int      .LDBG152-.LDBG0
          .int      .LDBG153-.LDBG0
          .short    .LDBG914-.LDBG913
.LDBG913:
          .byte     0x5a
.LDBG914:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
.LDBG915:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000000
          .byte     0x00
.LDBG916:
          .uleb128  0xa
          .int      .LDBG915-.LDBG740
          .uleb128  0x5
          .asciiz   "bufferA"
          .byte     0x01
          .short    0x0073
          .int      .LDBG916-.LDBG740
          .int      .LDBG917
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG917:
          .int      .LDBG134-.LDBG0
          .int      .LDBG207-.LDBG0
          .short    .LDBG919-.LDBG918
.LDBG918:
          .byte     0x7e
          .sleb128  0x4
.LDBG919:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
.LDBG920:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000000
          .byte     0x00
.LDBG921:
          .uleb128  0xa
          .int      .LDBG920-.LDBG740
          .uleb128  0x5
          .asciiz   "bufferB"
          .byte     0x01
          .short    0x0073
          .int      .LDBG921-.LDBG740
          .int      .LDBG922
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG922:
          .int      .LDBG134-.LDBG0
          .int      .LDBG207-.LDBG0
          .short    .LDBG924-.LDBG923
.LDBG923:
          .byte     0x7e
          .sleb128  0x8
.LDBG924:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
.LDBG925:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000000
          .byte     0x00
.LDBG926:
          .uleb128  0xa
          .int      .LDBG925-.LDBG740
          .uleb128  0x5
          .asciiz   "bufferC"
          .byte     0x01
          .short    0x0073
          .int      .LDBG926-.LDBG740
          .int      .LDBG927
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG927:
          .int      .LDBG134-.LDBG0
          .int      .LDBG207-.LDBG0
          .short    .LDBG929-.LDBG928
.LDBG928:
          .byte     0x7e
          .sleb128  0xc
.LDBG929:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "otherSide"
          .byte     0x01
          .short    0x0073
          .int      .LDBG751-.LDBG740
          .int      .LDBG930
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG930:
          .int      .LDBG150-.LDBG0
          .int      .LDBG151-.LDBG0
          .short    .LDBG932-.LDBG931
.LDBG931:
          .byte     0x59
.LDBG932:
          .int      .LDBG148-.LDBG0
          .int      .LDBG149-.LDBG0
          .short    .LDBG934-.LDBG933
.LDBG933:
          .byte     0x59
.LDBG934:
          .int      .LDBG162-.LDBG0
          .int      .LDBG163-.LDBG0
          .short    .LDBG936-.LDBG935
.LDBG935:
          .byte     0x59
.LDBG936:
          .int      .LDBG144-.LDBG0
          .int      .LDBG145-.LDBG0
          .short    .LDBG938-.LDBG937
.LDBG937:
          .byte     0x59
.LDBG938:
          .int      .LDBG146-.LDBG0
          .int      .LDBG147-.LDBG0
          .short    .LDBG940-.LDBG939
.LDBG939:
          .byte     0x59
.LDBG940:
          .int      .LDBG140-.LDBG0
          .int      .LDBG141-.LDBG0
          .short    .LDBG942-.LDBG941
.LDBG941:
          .byte     0x59
.LDBG942:
          .int      .LDBG160-.LDBG0
          .int      .LDBG161-.LDBG0
          .short    .LDBG944-.LDBG943
.LDBG943:
          .byte     0x59
.LDBG944:
          .int      .LDBG142-.LDBG0
          .int      .LDBG143-.LDBG0
          .short    .LDBG946-.LDBG945
.LDBG945:
          .byte     0x59
.LDBG946:
          .int      .LDBG158-.LDBG0
          .int      .LDBG159-.LDBG0
          .short    .LDBG948-.LDBG947
.LDBG947:
          .byte     0x59
.LDBG948:
          .int      .LDBG138-.LDBG0
          .int      .LDBG139-.LDBG0
          .short    .LDBG950-.LDBG949
.LDBG949:
          .byte     0x59
.LDBG950:
          .int      .LDBG156-.LDBG0
          .int      .LDBG157-.LDBG0
          .short    .LDBG952-.LDBG951
.LDBG951:
          .byte     0x59
.LDBG952:
          .int      .LDBG154-.LDBG0
          .int      .LDBG155-.LDBG0
          .short    .LDBG954-.LDBG953
.LDBG953:
          .byte     0x59
.LDBG954:
          .int      .LDBG152-.LDBG0
          .int      .LDBG153-.LDBG0
          .short    .LDBG956-.LDBG955
.LDBG955:
          .byte     0x59
.LDBG956:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "datalength"
          .byte     0x01
          .short    0x0075
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG957
          .section  .debug_loc,    "",    @progbits
.cc_top readData.function,readData
.LDBG957:
          .int      0x00000000
          .int      0x00000000
.cc_bottom readData.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom readData.function
.cc_top isLeft.data,isLeft
          .uleb128  0x9
          .asciiz   "isLeft"
          .byte     0x01
          .short    0x0091
          .int      .LDBG745-.LDBG740
          .short    .LDBG959-.LDBG958
.LDBG958:
          .byte     0x03
          .int      isLeft
.LDBG959:
.cc_bottom isLeft.data
.cc_top bufferA.data,bufferA
.LDBG960:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000c00
          .uleb128  0x8
          .byte     0x00
          .int      0x000002ff
          .byte     0x00
          .uleb128  0x9
          .asciiz   "bufferA"
          .byte     0x01
          .short    0x0098
          .int      .LDBG960-.LDBG740
          .short    .LDBG962-.LDBG961
.LDBG961:
          .byte     0x03
          .int      bufferA
.LDBG962:
.cc_bottom bufferA.data
.cc_top bufferB.data,bufferB
.LDBG963:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000c00
          .uleb128  0x8
          .byte     0x00
          .int      0x000002ff
          .byte     0x00
          .uleb128  0x9
          .asciiz   "bufferB"
          .byte     0x01
          .short    0x0099
          .int      .LDBG963-.LDBG740
          .short    .LDBG965-.LDBG964
.LDBG964:
          .byte     0x03
          .int      bufferB
.LDBG965:
.cc_bottom bufferB.data
.cc_top bufferC.data,bufferC
.LDBG966:
          .uleb128  0x7
          .int      .LDBG745-.LDBG740
          .int      0x00000c00
          .uleb128  0x8
          .byte     0x00
          .int      0x000002ff
          .byte     0x00
          .uleb128  0x9
          .asciiz   "bufferC"
          .byte     0x01
          .short    0x009a
          .int      .LDBG966-.LDBG740
          .short    .LDBG968-.LDBG967
.LDBG967:
          .byte     0x03
          .int      bufferC
.LDBG968:
.cc_bottom bufferC.data
.cc_top lrtime.data,lrtime
          .uleb128  0x9
          .asciiz   "lrtime"
          .byte     0x01
          .short    0x009c
          .int      .LDBG747-.LDBG740
          .short    .LDBG970-.LDBG969
.LDBG969:
          .byte     0x03
          .int      lrtime
.LDBG970:
.cc_bottom lrtime.data
.cc_top deliver.function,deliver
.LDBG971:
          .uleb128  0x4
          .asciiz   "deliver"
          .byte     0x01
          .byte     0xa3
          .byte     0x00
          .int      .LDBG745-.LDBG740
          .byte     0x01
          .long     .LDBG208         # low address
          .long     .LDBG375         # high address
          .uleb128  0x5
          .asciiz   "dataLength"
          .byte     0x01
          .short    0x00a0
          .int      .LDBG745-.LDBG740
          .int      .LDBG972
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG972:
          .int      .LDBG208-.LDBG0
          .int      .LDBG375-.LDBG0
          .short    .LDBG974-.LDBG973
.LDBG973:
          .byte     0x7e
          .sleb128  0x4
.LDBG974:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "buffer"
          .byte     0x01
          .short    0x00a0
          .int      .LDBG746-.LDBG740
          .int      .LDBG975
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG975:
          .int      .LDBG238-.LDBG0
          .int      .LDBG239-.LDBG0
          .short    .LDBG977-.LDBG976
.LDBG976:
          .byte     0x55
.LDBG977:
          .int      .LDBG240-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG979-.LDBG978
.LDBG978:
          .byte     0x55
.LDBG979:
          .int      .LDBG236-.LDBG0
          .int      .LDBG237-.LDBG0
          .short    .LDBG981-.LDBG980
.LDBG980:
          .byte     0x55
.LDBG981:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG983-.LDBG982
.LDBG982:
          .byte     0x55
.LDBG983:
          .int      .LDBG234-.LDBG0
          .int      .LDBG235-.LDBG0
          .short    .LDBG985-.LDBG984
.LDBG984:
          .byte     0x55
.LDBG985:
          .int      .LDBG228-.LDBG0
          .int      .LDBG229-.LDBG0
          .short    .LDBG987-.LDBG986
.LDBG986:
          .byte     0x55
.LDBG987:
          .int      .LDBG232-.LDBG0
          .int      .LDBG233-.LDBG0
          .short    .LDBG989-.LDBG988
.LDBG988:
          .byte     0x55
.LDBG989:
          .int      .LDBG222-.LDBG0
          .int      .LDBG223-.LDBG0
          .short    .LDBG991-.LDBG990
.LDBG990:
          .byte     0x55
.LDBG991:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG993-.LDBG992
.LDBG992:
          .byte     0x55
.LDBG993:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG995-.LDBG994
.LDBG994:
          .byte     0x55
.LDBG995:
          .int      .LDBG217-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG997-.LDBG996
.LDBG996:
          .byte     0x55
.LDBG997:
          .int      .LDBG211-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG999-.LDBG998
.LDBG998:
          .byte     0x55
.LDBG999:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_LRCLK"
          .byte     0x01
          .short    0x00a1
          .int      .LDBG754-.LDBG740
          .int      .LDBG1000
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1000:
          .int      .LDBG240-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG1002-.LDBG1001
.LDBG1001:
          .byte     0x59
.LDBG1002:
          .int      .LDBG228-.LDBG0
          .int      .LDBG229-.LDBG0
          .short    .LDBG1004-.LDBG1003
.LDBG1003:
          .byte     0x59
.LDBG1004:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG1006-.LDBG1005
.LDBG1005:
          .byte     0x59
.LDBG1006:
          .int      .LDBG236-.LDBG0
          .int      .LDBG237-.LDBG0
          .short    .LDBG1008-.LDBG1007
.LDBG1007:
          .byte     0x59
.LDBG1008:
          .int      .LDBG234-.LDBG0
          .int      .LDBG235-.LDBG0
          .short    .LDBG1010-.LDBG1009
.LDBG1009:
          .byte     0x59
.LDBG1010:
          .int      .LDBG222-.LDBG0
          .int      .LDBG223-.LDBG0
          .short    .LDBG1012-.LDBG1011
.LDBG1011:
          .byte     0x59
.LDBG1012:
          .int      .LDBG232-.LDBG0
          .int      .LDBG233-.LDBG0
          .short    .LDBG1014-.LDBG1013
.LDBG1013:
          .byte     0x59
.LDBG1014:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG1016-.LDBG1015
.LDBG1015:
          .byte     0x59
.LDBG1016:
          .int      .LDBG238-.LDBG0
          .int      .LDBG239-.LDBG0
          .short    .LDBG1018-.LDBG1017
.LDBG1017:
          .byte     0x59
.LDBG1018:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG1020-.LDBG1019
.LDBG1019:
          .byte     0x59
.LDBG1020:
          .int      .LDBG217-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG1022-.LDBG1021
.LDBG1021:
          .byte     0x59
.LDBG1022:
          .int      .LDBG212-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG1024-.LDBG1023
.LDBG1023:
          .byte     0x59
.LDBG1024:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT1"
          .byte     0x01
          .short    0x00a1
          .int      .LDBG759-.LDBG740
          .int      .LDBG1025
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1025:
          .int      .LDBG208-.LDBG0
          .int      .LDBG375-.LDBG0
          .short    .LDBG1027-.LDBG1026
.LDBG1026:
          .byte     0x7e
          .sleb128  0x8
.LDBG1027:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT2"
          .byte     0x01
          .short    0x00a1
          .int      .LDBG759-.LDBG740
          .int      .LDBG1028
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1028:
          .int      .LDBG208-.LDBG0
          .int      .LDBG375-.LDBG0
          .short    .LDBG1030-.LDBG1029
.LDBG1029:
          .byte     0x7e
          .sleb128  0x40
.LDBG1030:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT3"
          .byte     0x01
          .short    0x00a2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1031
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1031:
          .int      .LDBG240-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG1033-.LDBG1032
.LDBG1032:
          .byte     0x58
.LDBG1033:
          .int      .LDBG236-.LDBG0
          .int      .LDBG237-.LDBG0
          .short    .LDBG1035-.LDBG1034
.LDBG1034:
          .byte     0x58
.LDBG1035:
          .int      .LDBG228-.LDBG0
          .int      .LDBG229-.LDBG0
          .short    .LDBG1037-.LDBG1036
.LDBG1036:
          .byte     0x58
.LDBG1037:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG1039-.LDBG1038
.LDBG1038:
          .byte     0x58
.LDBG1039:
          .int      .LDBG234-.LDBG0
          .int      .LDBG235-.LDBG0
          .short    .LDBG1041-.LDBG1040
.LDBG1040:
          .byte     0x58
.LDBG1041:
          .int      .LDBG222-.LDBG0
          .int      .LDBG223-.LDBG0
          .short    .LDBG1043-.LDBG1042
.LDBG1042:
          .byte     0x58
.LDBG1043:
          .int      .LDBG232-.LDBG0
          .int      .LDBG233-.LDBG0
          .short    .LDBG1045-.LDBG1044
.LDBG1044:
          .byte     0x58
.LDBG1045:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG1047-.LDBG1046
.LDBG1046:
          .byte     0x58
.LDBG1047:
          .int      .LDBG238-.LDBG0
          .int      .LDBG239-.LDBG0
          .short    .LDBG1049-.LDBG1048
.LDBG1048:
          .byte     0x58
.LDBG1049:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG1051-.LDBG1050
.LDBG1050:
          .byte     0x58
.LDBG1051:
          .int      .LDBG217-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG1053-.LDBG1052
.LDBG1052:
          .byte     0x58
.LDBG1053:
          .int      .LDBG213-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG1055-.LDBG1054
.LDBG1054:
          .byte     0x58
.LDBG1055:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT4"
          .byte     0x01
          .short    0x00a2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1056
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1056:
          .int      .LDBG238-.LDBG0
          .int      .LDBG239-.LDBG0
          .short    .LDBG1058-.LDBG1057
.LDBG1057:
          .byte     0x57
.LDBG1058:
          .int      .LDBG240-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG1060-.LDBG1059
.LDBG1059:
          .byte     0x57
.LDBG1060:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG1062-.LDBG1061
.LDBG1061:
          .byte     0x57
.LDBG1062:
          .int      .LDBG236-.LDBG0
          .int      .LDBG237-.LDBG0
          .short    .LDBG1064-.LDBG1063
.LDBG1063:
          .byte     0x57
.LDBG1064:
          .int      .LDBG228-.LDBG0
          .int      .LDBG229-.LDBG0
          .short    .LDBG1066-.LDBG1065
.LDBG1065:
          .byte     0x57
.LDBG1066:
          .int      .LDBG234-.LDBG0
          .int      .LDBG235-.LDBG0
          .short    .LDBG1068-.LDBG1067
.LDBG1067:
          .byte     0x57
.LDBG1068:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG1070-.LDBG1069
.LDBG1069:
          .byte     0x57
.LDBG1070:
          .int      .LDBG222-.LDBG0
          .int      .LDBG223-.LDBG0
          .short    .LDBG1072-.LDBG1071
.LDBG1071:
          .byte     0x57
.LDBG1072:
          .int      .LDBG232-.LDBG0
          .int      .LDBG233-.LDBG0
          .short    .LDBG1074-.LDBG1073
.LDBG1073:
          .byte     0x57
.LDBG1074:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG1076-.LDBG1075
.LDBG1075:
          .byte     0x57
.LDBG1076:
          .int      .LDBG217-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG1078-.LDBG1077
.LDBG1077:
          .byte     0x57
.LDBG1078:
          .int      .LDBG214-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG1080-.LDBG1079
.LDBG1079:
          .byte     0x57
.LDBG1080:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "i_CurSampFreq"
          .byte     0x01
          .short    0x00a2
          .int      .LDBG745-.LDBG740
          .int      .LDBG1081
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1081:
          .int      .LDBG208-.LDBG0
          .int      .LDBG375-.LDBG0
          .short    .LDBG1083-.LDBG1082
.LDBG1082:
          .byte     0x7e
          .sleb128  0x4c
.LDBG1083:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0xb
          .asciiz   "value"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1085-.LDBG1084
.LDBG1084:
          .byte     0x03
          .int      value.stat.0
.LDBG1085:
          .uleb128  0xb
          .asciiz   "value2"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1087-.LDBG1086
.LDBG1086:
          .byte     0x03
          .int      value2.stat.1
.LDBG1087:
          .uleb128  0xb
          .asciiz   "value3"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1089-.LDBG1088
.LDBG1088:
          .byte     0x03
          .int      value3.stat.2
.LDBG1089:
          .uleb128  0xb
          .asciiz   "value4"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1091-.LDBG1090
.LDBG1090:
          .byte     0x03
          .int      value4.stat.3
.LDBG1091:
          .uleb128  0xb
          .asciiz   "value5"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1093-.LDBG1092
.LDBG1092:
          .byte     0x03
          .int      value5.stat.4
.LDBG1093:
          .uleb128  0xb
          .asciiz   "value6"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1095-.LDBG1094
.LDBG1094:
          .byte     0x03
          .int      value6.stat.5
.LDBG1095:
          .uleb128  0xb
          .asciiz   "value7"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1097-.LDBG1096
.LDBG1096:
          .byte     0x03
          .int      value7.stat.6
.LDBG1097:
          .uleb128  0xb
          .asciiz   "value8"
          .byte     0x01
          .short    0x00a4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .short    .LDBG1099-.LDBG1098
.LDBG1098:
          .byte     0x03
          .int      value8.stat.7
.LDBG1099:
          .uleb128  0x6
          .asciiz   "readIndex"
          .byte     0x01
          .short    0x00a5
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .int      .LDBG1100
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1100:
          .int      .LDBG241-.LDBG0
          .int      .LDBG242-.LDBG0
          .short    .LDBG1102-.LDBG1101
.LDBG1101:
          .byte     0x56
.LDBG1102:
          .int      .LDBG224-.LDBG0
          .int      .LDBG225-.LDBG0
          .short    .LDBG1104-.LDBG1103
.LDBG1103:
          .byte     0x56
.LDBG1104:
          .int      .LDBG236-.LDBG0
          .int      .LDBG237-.LDBG0
          .short    .LDBG1106-.LDBG1105
.LDBG1105:
          .byte     0x56
.LDBG1106:
          .int      .LDBG234-.LDBG0
          .int      .LDBG235-.LDBG0
          .short    .LDBG1108-.LDBG1107
.LDBG1107:
          .byte     0x56
.LDBG1108:
          .int      .LDBG228-.LDBG0
          .int      .LDBG229-.LDBG0
          .short    .LDBG1110-.LDBG1109
.LDBG1109:
          .byte     0x56
.LDBG1110:
          .int      .LDBG232-.LDBG0
          .int      .LDBG233-.LDBG0
          .short    .LDBG1112-.LDBG1111
.LDBG1111:
          .byte     0x56
.LDBG1112:
          .int      .LDBG222-.LDBG0
          .int      .LDBG223-.LDBG0
          .short    .LDBG1114-.LDBG1113
.LDBG1113:
          .byte     0x56
.LDBG1114:
          .int      .LDBG230-.LDBG0
          .int      .LDBG231-.LDBG0
          .short    .LDBG1116-.LDBG1115
.LDBG1115:
          .byte     0x56
.LDBG1116:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG1118-.LDBG1117
.LDBG1117:
          .byte     0x56
.LDBG1118:
          .int      .LDBG217-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG1120-.LDBG1119
.LDBG1119:
          .byte     0x56
.LDBG1120:
          .int      .LDBG215-.LDBG0
          .int      .LDBG216-.LDBG0
          .short    .LDBG1122-.LDBG1121
.LDBG1121:
          .byte     0x56
.LDBG1122:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "_"
          .byte     0x01
          .short    0x00a6
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1123
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1123:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x00a7
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1124
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1124:
          .int      .LDBG220-.LDBG0
          .int      .LDBG221-.LDBG0
          .short    .LDBG1126-.LDBG1125
.LDBG1125:
          .byte     0x51
.LDBG1126:
          .int      .LDBG218-.LDBG0
          .int      .LDBG219-.LDBG0
          .short    .LDBG1128-.LDBG1127
.LDBG1127:
          .byte     0x51
.LDBG1128:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "s_tmp"
          .byte     0x01
          .short    0x00a8
          .short    0x0000
          .int      .LDBG747-.LDBG740
          .int      .LDBG1129
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1129:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "oldData"
          .byte     0x01
          .short    0x00a9
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1130
          .section  .debug_loc,    "",    @progbits
.cc_top deliver.function,deliver
.LDBG1130:
          .int      0x00000000
          .int      0x00000000
.cc_bottom deliver.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom deliver.function
.cc_top physical.function,physical
.LDBG1131:
          .uleb128  0x3
          .asciiz   "physical"
          .byte     0x01
          .byte     0xfa
          .byte     0x01
          .byte     0x01
          .long     .LDBG376         # low address
          .long     .LDBG584         # high address
          .uleb128  0x5
          .asciiz   "otherSide"
          .byte     0x01
          .short    0x00f8
          .int      .LDBG751-.LDBG740
          .int      .LDBG1132
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1132:
          .int      .LDBG437-.LDBG0
          .int      .LDBG438-.LDBG0
          .short    .LDBG1134-.LDBG1133
.LDBG1133:
          .byte     0x5a
.LDBG1134:
          .int      .LDBG435-.LDBG0
          .int      .LDBG436-.LDBG0
          .short    .LDBG1136-.LDBG1135
.LDBG1135:
          .byte     0x5a
.LDBG1136:
          .int      .LDBG433-.LDBG0
          .int      .LDBG434-.LDBG0
          .short    .LDBG1138-.LDBG1137
.LDBG1137:
          .byte     0x5a
.LDBG1138:
          .int      .LDBG431-.LDBG0
          .int      .LDBG432-.LDBG0
          .short    .LDBG1140-.LDBG1139
.LDBG1139:
          .byte     0x5a
.LDBG1140:
          .int      .LDBG429-.LDBG0
          .int      .LDBG430-.LDBG0
          .short    .LDBG1142-.LDBG1141
.LDBG1141:
          .byte     0x5a
.LDBG1142:
          .int      .LDBG425-.LDBG0
          .int      .LDBG426-.LDBG0
          .short    .LDBG1144-.LDBG1143
.LDBG1143:
          .byte     0x5a
.LDBG1144:
          .int      .LDBG427-.LDBG0
          .int      .LDBG428-.LDBG0
          .short    .LDBG1146-.LDBG1145
.LDBG1145:
          .byte     0x5a
.LDBG1146:
          .int      .LDBG423-.LDBG0
          .int      .LDBG424-.LDBG0
          .short    .LDBG1148-.LDBG1147
.LDBG1147:
          .byte     0x5a
.LDBG1148:
          .int      .LDBG421-.LDBG0
          .int      .LDBG422-.LDBG0
          .short    .LDBG1150-.LDBG1149
.LDBG1149:
          .byte     0x5a
.LDBG1150:
          .int      .LDBG419-.LDBG0
          .int      .LDBG420-.LDBG0
          .short    .LDBG1152-.LDBG1151
.LDBG1151:
          .byte     0x5a
.LDBG1152:
          .int      .LDBG417-.LDBG0
          .int      .LDBG418-.LDBG0
          .short    .LDBG1154-.LDBG1153
.LDBG1153:
          .byte     0x5a
.LDBG1154:
          .int      .LDBG415-.LDBG0
          .int      .LDBG416-.LDBG0
          .short    .LDBG1156-.LDBG1155
.LDBG1155:
          .byte     0x5a
.LDBG1156:
          .int      .LDBG413-.LDBG0
          .int      .LDBG414-.LDBG0
          .short    .LDBG1158-.LDBG1157
.LDBG1157:
          .byte     0x5a
.LDBG1158:
          .int      .LDBG411-.LDBG0
          .int      .LDBG412-.LDBG0
          .short    .LDBG1160-.LDBG1159
.LDBG1159:
          .byte     0x5a
.LDBG1160:
          .int      .LDBG409-.LDBG0
          .int      .LDBG410-.LDBG0
          .short    .LDBG1162-.LDBG1161
.LDBG1161:
          .byte     0x5a
.LDBG1162:
          .int      .LDBG407-.LDBG0
          .int      .LDBG408-.LDBG0
          .short    .LDBG1164-.LDBG1163
.LDBG1163:
          .byte     0x5a
.LDBG1164:
          .int      .LDBG405-.LDBG0
          .int      .LDBG406-.LDBG0
          .short    .LDBG1166-.LDBG1165
.LDBG1165:
          .byte     0x5a
.LDBG1166:
          .int      .LDBG443-.LDBG0
          .int      .LDBG444-.LDBG0
          .short    .LDBG1168-.LDBG1167
.LDBG1167:
          .byte     0x5a
.LDBG1168:
          .int      .LDBG403-.LDBG0
          .int      .LDBG404-.LDBG0
          .short    .LDBG1170-.LDBG1169
.LDBG1169:
          .byte     0x5a
.LDBG1170:
          .int      .LDBG401-.LDBG0
          .int      .LDBG402-.LDBG0
          .short    .LDBG1172-.LDBG1171
.LDBG1171:
          .byte     0x5a
.LDBG1172:
          .int      .LDBG399-.LDBG0
          .int      .LDBG400-.LDBG0
          .short    .LDBG1174-.LDBG1173
.LDBG1173:
          .byte     0x5a
.LDBG1174:
          .int      .LDBG397-.LDBG0
          .int      .LDBG398-.LDBG0
          .short    .LDBG1176-.LDBG1175
.LDBG1175:
          .byte     0x5a
.LDBG1176:
          .int      .LDBG395-.LDBG0
          .int      .LDBG396-.LDBG0
          .short    .LDBG1178-.LDBG1177
.LDBG1177:
          .byte     0x5a
.LDBG1178:
          .int      .LDBG441-.LDBG0
          .int      .LDBG442-.LDBG0
          .short    .LDBG1180-.LDBG1179
.LDBG1179:
          .byte     0x5a
.LDBG1180:
          .int      .LDBG390-.LDBG0
          .int      .LDBG392-.LDBG0
          .short    .LDBG1182-.LDBG1181
.LDBG1181:
          .byte     0x5a
.LDBG1182:
          .int      .LDBG393-.LDBG0
          .int      .LDBG394-.LDBG0
          .short    .LDBG1184-.LDBG1183
.LDBG1183:
          .byte     0x5a
.LDBG1184:
          .int      .LDBG379-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1186-.LDBG1185
.LDBG1185:
          .byte     0x5a
.LDBG1186:
          .int      .LDBG439-.LDBG0
          .int      .LDBG440-.LDBG0
          .short    .LDBG1188-.LDBG1187
.LDBG1187:
          .byte     0x5a
.LDBG1188:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "p_aud_rst"
          .byte     0x01
          .short    0x00f8
          .int      .LDBG754-.LDBG740
          .int      .LDBG1189
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1189:
          .int      .LDBG380-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1191-.LDBG1190
.LDBG1190:
          .byte     0x56
.LDBG1191:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_BCLK"
          .byte     0x01
          .short    0x00f8
          .int      .LDBG754-.LDBG740
          .int      .LDBG1192
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1192:
          .int      .LDBG381-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1194-.LDBG1193
.LDBG1193:
          .byte     0x52
.LDBG1194:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SCLK"
          .byte     0x01
          .short    0x00f8
          .int      .LDBG754-.LDBG740
          .int      .LDBG1195
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1195:
          .int      .LDBG437-.LDBG0
          .int      .LDBG438-.LDBG0
          .short    .LDBG1197-.LDBG1196
.LDBG1196:
          .byte     0x59
.LDBG1197:
          .int      .LDBG435-.LDBG0
          .int      .LDBG436-.LDBG0
          .short    .LDBG1199-.LDBG1198
.LDBG1198:
          .byte     0x59
.LDBG1199:
          .int      .LDBG433-.LDBG0
          .int      .LDBG434-.LDBG0
          .short    .LDBG1201-.LDBG1200
.LDBG1200:
          .byte     0x59
.LDBG1201:
          .int      .LDBG425-.LDBG0
          .int      .LDBG426-.LDBG0
          .short    .LDBG1203-.LDBG1202
.LDBG1202:
          .byte     0x59
.LDBG1203:
          .int      .LDBG431-.LDBG0
          .int      .LDBG432-.LDBG0
          .short    .LDBG1205-.LDBG1204
.LDBG1204:
          .byte     0x59
.LDBG1205:
          .int      .LDBG429-.LDBG0
          .int      .LDBG430-.LDBG0
          .short    .LDBG1207-.LDBG1206
.LDBG1206:
          .byte     0x59
.LDBG1207:
          .int      .LDBG427-.LDBG0
          .int      .LDBG428-.LDBG0
          .short    .LDBG1209-.LDBG1208
.LDBG1208:
          .byte     0x59
.LDBG1209:
          .int      .LDBG423-.LDBG0
          .int      .LDBG424-.LDBG0
          .short    .LDBG1211-.LDBG1210
.LDBG1210:
          .byte     0x59
.LDBG1211:
          .int      .LDBG421-.LDBG0
          .int      .LDBG422-.LDBG0
          .short    .LDBG1213-.LDBG1212
.LDBG1212:
          .byte     0x59
.LDBG1213:
          .int      .LDBG419-.LDBG0
          .int      .LDBG420-.LDBG0
          .short    .LDBG1215-.LDBG1214
.LDBG1214:
          .byte     0x59
.LDBG1215:
          .int      .LDBG417-.LDBG0
          .int      .LDBG418-.LDBG0
          .short    .LDBG1217-.LDBG1216
.LDBG1216:
          .byte     0x59
.LDBG1217:
          .int      .LDBG415-.LDBG0
          .int      .LDBG416-.LDBG0
          .short    .LDBG1219-.LDBG1218
.LDBG1218:
          .byte     0x59
.LDBG1219:
          .int      .LDBG413-.LDBG0
          .int      .LDBG414-.LDBG0
          .short    .LDBG1221-.LDBG1220
.LDBG1220:
          .byte     0x59
.LDBG1221:
          .int      .LDBG411-.LDBG0
          .int      .LDBG412-.LDBG0
          .short    .LDBG1223-.LDBG1222
.LDBG1222:
          .byte     0x59
.LDBG1223:
          .int      .LDBG409-.LDBG0
          .int      .LDBG410-.LDBG0
          .short    .LDBG1225-.LDBG1224
.LDBG1224:
          .byte     0x59
.LDBG1225:
          .int      .LDBG407-.LDBG0
          .int      .LDBG408-.LDBG0
          .short    .LDBG1227-.LDBG1226
.LDBG1226:
          .byte     0x59
.LDBG1227:
          .int      .LDBG405-.LDBG0
          .int      .LDBG406-.LDBG0
          .short    .LDBG1229-.LDBG1228
.LDBG1228:
          .byte     0x59
.LDBG1229:
          .int      .LDBG403-.LDBG0
          .int      .LDBG404-.LDBG0
          .short    .LDBG1231-.LDBG1230
.LDBG1230:
          .byte     0x59
.LDBG1231:
          .int      .LDBG443-.LDBG0
          .int      .LDBG444-.LDBG0
          .short    .LDBG1233-.LDBG1232
.LDBG1232:
          .byte     0x59
.LDBG1233:
          .int      .LDBG401-.LDBG0
          .int      .LDBG402-.LDBG0
          .short    .LDBG1235-.LDBG1234
.LDBG1234:
          .byte     0x59
.LDBG1235:
          .int      .LDBG399-.LDBG0
          .int      .LDBG400-.LDBG0
          .short    .LDBG1237-.LDBG1236
.LDBG1236:
          .byte     0x59
.LDBG1237:
          .int      .LDBG397-.LDBG0
          .int      .LDBG398-.LDBG0
          .short    .LDBG1239-.LDBG1238
.LDBG1238:
          .byte     0x59
.LDBG1239:
          .int      .LDBG395-.LDBG0
          .int      .LDBG396-.LDBG0
          .short    .LDBG1241-.LDBG1240
.LDBG1240:
          .byte     0x59
.LDBG1241:
          .int      .LDBG441-.LDBG0
          .int      .LDBG442-.LDBG0
          .short    .LDBG1243-.LDBG1242
.LDBG1242:
          .byte     0x59
.LDBG1243:
          .int      .LDBG393-.LDBG0
          .int      .LDBG394-.LDBG0
          .short    .LDBG1245-.LDBG1244
.LDBG1244:
          .byte     0x59
.LDBG1245:
          .int      .LDBG390-.LDBG0
          .int      .LDBG392-.LDBG0
          .short    .LDBG1247-.LDBG1246
.LDBG1246:
          .byte     0x59
.LDBG1247:
          .int      .LDBG382-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1249-.LDBG1248
.LDBG1248:
          .byte     0x59
.LDBG1249:
          .int      .LDBG439-.LDBG0
          .int      .LDBG440-.LDBG0
          .short    .LDBG1251-.LDBG1250
.LDBG1250:
          .byte     0x59
.LDBG1251:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SDIN"
          .byte     0x01
          .short    0x00f8
          .int      .LDBG754-.LDBG740
          .int      .LDBG1252
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1252:
          .int      .LDBG437-.LDBG0
          .int      .LDBG438-.LDBG0
          .short    .LDBG1254-.LDBG1253
.LDBG1253:
          .byte     0x58
.LDBG1254:
          .int      .LDBG435-.LDBG0
          .int      .LDBG436-.LDBG0
          .short    .LDBG1256-.LDBG1255
.LDBG1255:
          .byte     0x58
.LDBG1256:
          .int      .LDBG433-.LDBG0
          .int      .LDBG434-.LDBG0
          .short    .LDBG1258-.LDBG1257
.LDBG1257:
          .byte     0x58
.LDBG1258:
          .int      .LDBG425-.LDBG0
          .int      .LDBG426-.LDBG0
          .short    .LDBG1260-.LDBG1259
.LDBG1259:
          .byte     0x58
.LDBG1260:
          .int      .LDBG431-.LDBG0
          .int      .LDBG432-.LDBG0
          .short    .LDBG1262-.LDBG1261
.LDBG1261:
          .byte     0x58
.LDBG1262:
          .int      .LDBG429-.LDBG0
          .int      .LDBG430-.LDBG0
          .short    .LDBG1264-.LDBG1263
.LDBG1263:
          .byte     0x58
.LDBG1264:
          .int      .LDBG427-.LDBG0
          .int      .LDBG428-.LDBG0
          .short    .LDBG1266-.LDBG1265
.LDBG1265:
          .byte     0x58
.LDBG1266:
          .int      .LDBG423-.LDBG0
          .int      .LDBG424-.LDBG0
          .short    .LDBG1268-.LDBG1267
.LDBG1267:
          .byte     0x58
.LDBG1268:
          .int      .LDBG421-.LDBG0
          .int      .LDBG422-.LDBG0
          .short    .LDBG1270-.LDBG1269
.LDBG1269:
          .byte     0x58
.LDBG1270:
          .int      .LDBG419-.LDBG0
          .int      .LDBG420-.LDBG0
          .short    .LDBG1272-.LDBG1271
.LDBG1271:
          .byte     0x58
.LDBG1272:
          .int      .LDBG417-.LDBG0
          .int      .LDBG418-.LDBG0
          .short    .LDBG1274-.LDBG1273
.LDBG1273:
          .byte     0x58
.LDBG1274:
          .int      .LDBG415-.LDBG0
          .int      .LDBG416-.LDBG0
          .short    .LDBG1276-.LDBG1275
.LDBG1275:
          .byte     0x58
.LDBG1276:
          .int      .LDBG413-.LDBG0
          .int      .LDBG414-.LDBG0
          .short    .LDBG1278-.LDBG1277
.LDBG1277:
          .byte     0x58
.LDBG1278:
          .int      .LDBG411-.LDBG0
          .int      .LDBG412-.LDBG0
          .short    .LDBG1280-.LDBG1279
.LDBG1279:
          .byte     0x58
.LDBG1280:
          .int      .LDBG409-.LDBG0
          .int      .LDBG410-.LDBG0
          .short    .LDBG1282-.LDBG1281
.LDBG1281:
          .byte     0x58
.LDBG1282:
          .int      .LDBG407-.LDBG0
          .int      .LDBG408-.LDBG0
          .short    .LDBG1284-.LDBG1283
.LDBG1283:
          .byte     0x58
.LDBG1284:
          .int      .LDBG405-.LDBG0
          .int      .LDBG406-.LDBG0
          .short    .LDBG1286-.LDBG1285
.LDBG1285:
          .byte     0x58
.LDBG1286:
          .int      .LDBG403-.LDBG0
          .int      .LDBG404-.LDBG0
          .short    .LDBG1288-.LDBG1287
.LDBG1287:
          .byte     0x58
.LDBG1288:
          .int      .LDBG401-.LDBG0
          .int      .LDBG402-.LDBG0
          .short    .LDBG1290-.LDBG1289
.LDBG1289:
          .byte     0x58
.LDBG1290:
          .int      .LDBG443-.LDBG0
          .int      .LDBG444-.LDBG0
          .short    .LDBG1292-.LDBG1291
.LDBG1291:
          .byte     0x58
.LDBG1292:
          .int      .LDBG399-.LDBG0
          .int      .LDBG400-.LDBG0
          .short    .LDBG1294-.LDBG1293
.LDBG1293:
          .byte     0x58
.LDBG1294:
          .int      .LDBG397-.LDBG0
          .int      .LDBG398-.LDBG0
          .short    .LDBG1296-.LDBG1295
.LDBG1295:
          .byte     0x58
.LDBG1296:
          .int      .LDBG395-.LDBG0
          .int      .LDBG396-.LDBG0
          .short    .LDBG1298-.LDBG1297
.LDBG1297:
          .byte     0x58
.LDBG1298:
          .int      .LDBG441-.LDBG0
          .int      .LDBG442-.LDBG0
          .short    .LDBG1300-.LDBG1299
.LDBG1299:
          .byte     0x58
.LDBG1300:
          .int      .LDBG393-.LDBG0
          .int      .LDBG394-.LDBG0
          .short    .LDBG1302-.LDBG1301
.LDBG1301:
          .byte     0x58
.LDBG1302:
          .int      .LDBG390-.LDBG0
          .int      .LDBG392-.LDBG0
          .short    .LDBG1304-.LDBG1303
.LDBG1303:
          .byte     0x58
.LDBG1304:
          .int      .LDBG383-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1306-.LDBG1305
.LDBG1305:
          .byte     0x58
.LDBG1306:
          .int      .LDBG439-.LDBG0
          .int      .LDBG440-.LDBG0
          .short    .LDBG1308-.LDBG1307
.LDBG1307:
          .byte     0x58
.LDBG1308:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_LRCLK"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG754-.LDBG740
          .int      .LDBG1309
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1309:
          .int      .LDBG439-.LDBG0
          .int      .LDBG440-.LDBG0
          .short    .LDBG1311-.LDBG1310
.LDBG1310:
          .byte     0x57
.LDBG1311:
          .int      .LDBG437-.LDBG0
          .int      .LDBG438-.LDBG0
          .short    .LDBG1313-.LDBG1312
.LDBG1312:
          .byte     0x57
.LDBG1313:
          .int      .LDBG435-.LDBG0
          .int      .LDBG436-.LDBG0
          .short    .LDBG1315-.LDBG1314
.LDBG1314:
          .byte     0x57
.LDBG1315:
          .int      .LDBG433-.LDBG0
          .int      .LDBG434-.LDBG0
          .short    .LDBG1317-.LDBG1316
.LDBG1316:
          .byte     0x57
.LDBG1317:
          .int      .LDBG431-.LDBG0
          .int      .LDBG432-.LDBG0
          .short    .LDBG1319-.LDBG1318
.LDBG1318:
          .byte     0x57
.LDBG1319:
          .int      .LDBG429-.LDBG0
          .int      .LDBG430-.LDBG0
          .short    .LDBG1321-.LDBG1320
.LDBG1320:
          .byte     0x57
.LDBG1321:
          .int      .LDBG427-.LDBG0
          .int      .LDBG428-.LDBG0
          .short    .LDBG1323-.LDBG1322
.LDBG1322:
          .byte     0x57
.LDBG1323:
          .int      .LDBG425-.LDBG0
          .int      .LDBG426-.LDBG0
          .short    .LDBG1325-.LDBG1324
.LDBG1324:
          .byte     0x57
.LDBG1325:
          .int      .LDBG423-.LDBG0
          .int      .LDBG424-.LDBG0
          .short    .LDBG1327-.LDBG1326
.LDBG1326:
          .byte     0x57
.LDBG1327:
          .int      .LDBG421-.LDBG0
          .int      .LDBG422-.LDBG0
          .short    .LDBG1329-.LDBG1328
.LDBG1328:
          .byte     0x57
.LDBG1329:
          .int      .LDBG419-.LDBG0
          .int      .LDBG420-.LDBG0
          .short    .LDBG1331-.LDBG1330
.LDBG1330:
          .byte     0x57
.LDBG1331:
          .int      .LDBG417-.LDBG0
          .int      .LDBG418-.LDBG0
          .short    .LDBG1333-.LDBG1332
.LDBG1332:
          .byte     0x57
.LDBG1333:
          .int      .LDBG415-.LDBG0
          .int      .LDBG416-.LDBG0
          .short    .LDBG1335-.LDBG1334
.LDBG1334:
          .byte     0x57
.LDBG1335:
          .int      .LDBG413-.LDBG0
          .int      .LDBG414-.LDBG0
          .short    .LDBG1337-.LDBG1336
.LDBG1336:
          .byte     0x57
.LDBG1337:
          .int      .LDBG411-.LDBG0
          .int      .LDBG412-.LDBG0
          .short    .LDBG1339-.LDBG1338
.LDBG1338:
          .byte     0x57
.LDBG1339:
          .int      .LDBG409-.LDBG0
          .int      .LDBG410-.LDBG0
          .short    .LDBG1341-.LDBG1340
.LDBG1340:
          .byte     0x57
.LDBG1341:
          .int      .LDBG407-.LDBG0
          .int      .LDBG408-.LDBG0
          .short    .LDBG1343-.LDBG1342
.LDBG1342:
          .byte     0x57
.LDBG1343:
          .int      .LDBG405-.LDBG0
          .int      .LDBG406-.LDBG0
          .short    .LDBG1345-.LDBG1344
.LDBG1344:
          .byte     0x57
.LDBG1345:
          .int      .LDBG403-.LDBG0
          .int      .LDBG404-.LDBG0
          .short    .LDBG1347-.LDBG1346
.LDBG1346:
          .byte     0x57
.LDBG1347:
          .int      .LDBG401-.LDBG0
          .int      .LDBG402-.LDBG0
          .short    .LDBG1349-.LDBG1348
.LDBG1348:
          .byte     0x57
.LDBG1349:
          .int      .LDBG399-.LDBG0
          .int      .LDBG400-.LDBG0
          .short    .LDBG1351-.LDBG1350
.LDBG1350:
          .byte     0x57
.LDBG1351:
          .int      .LDBG443-.LDBG0
          .int      .LDBG444-.LDBG0
          .short    .LDBG1353-.LDBG1352
.LDBG1352:
          .byte     0x57
.LDBG1353:
          .int      .LDBG397-.LDBG0
          .int      .LDBG398-.LDBG0
          .short    .LDBG1355-.LDBG1354
.LDBG1354:
          .byte     0x57
.LDBG1355:
          .int      .LDBG395-.LDBG0
          .int      .LDBG396-.LDBG0
          .short    .LDBG1357-.LDBG1356
.LDBG1356:
          .byte     0x57
.LDBG1357:
          .int      .LDBG393-.LDBG0
          .int      .LDBG394-.LDBG0
          .short    .LDBG1359-.LDBG1358
.LDBG1358:
          .byte     0x57
.LDBG1359:
          .int      .LDBG390-.LDBG0
          .int      .LDBG392-.LDBG0
          .short    .LDBG1361-.LDBG1360
.LDBG1360:
          .byte     0x57
.LDBG1361:
          .int      .LDBG384-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1363-.LDBG1362
.LDBG1362:
          .byte     0x57
.LDBG1363:
          .int      .LDBG441-.LDBG0
          .int      .LDBG442-.LDBG0
          .short    .LDBG1365-.LDBG1364
.LDBG1364:
          .byte     0x57
.LDBG1365:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT1"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG759-.LDBG740
          .int      .LDBG1366
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1366:
          .int      .LDBG376-.LDBG0
          .int      .LDBG584-.LDBG0
          .short    .LDBG1368-.LDBG1367
.LDBG1367:
          .byte     0x7e
          .sleb128  0x44
.LDBG1368:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT2"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG759-.LDBG740
          .int      .LDBG1369
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1369:
          .int      .LDBG376-.LDBG0
          .int      .LDBG584-.LDBG0
          .short    .LDBG1371-.LDBG1370
.LDBG1370:
          .byte     0x7e
          .sleb128  0x48
.LDBG1371:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT3"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG759-.LDBG740
          .int      .LDBG1372
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1372:
          .int      .LDBG376-.LDBG0
          .int      .LDBG584-.LDBG0
          .short    .LDBG1374-.LDBG1373
.LDBG1373:
          .byte     0x7e
          .sleb128  0x4c
.LDBG1374:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT4"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG759-.LDBG740
          .int      .LDBG1375
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1375:
          .int      .LDBG376-.LDBG0
          .int      .LDBG584-.LDBG0
          .short    .LDBG1377-.LDBG1376
.LDBG1376:
          .byte     0x7e
          .sleb128  0x50
.LDBG1377:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "clk_aud_bit"
          .byte     0x01
          .short    0x00f9
          .int      .LDBG753-.LDBG740
          .int      .LDBG1378
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1378:
          .int      .LDBG385-.LDBG0
          .int      .LDBG387-.LDBG0
          .short    .LDBG1380-.LDBG1379
.LDBG1379:
          .byte     0x50
.LDBG1380:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x00fb
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .int      .LDBG1381
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1381:
          .int      .LDBG387-.LDBG0
          .int      .LDBG388-.LDBG0
          .short    .LDBG1383-.LDBG1382
.LDBG1382:
          .byte     0x50
.LDBG1383:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "t"
          .byte     0x01
          .short    0x00fc
          .short    0x0000
          .int      .LDBG752-.LDBG740
          .int      .LDBG1384
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1384:
          .int      .LDBG386-.LDBG0
          .int      .LDBG389-.LDBG0
          .short    .LDBG1386-.LDBG1385
.LDBG1385:
          .byte     0x55
.LDBG1386:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "value"
          .byte     0x01
          .short    0x00fd
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1387
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1387:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "count"
          .byte     0x01
          .short    0x00fd
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1388
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1388:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "tmp"
          .byte     0x01
          .short    0x00fe
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1389
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1389:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i_CurSampFreq"
          .byte     0x01
          .short    0x00ff
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1390
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1390:
          .int      .LDBG397-.LDBG0
          .int      .LDBG398-.LDBG0
          .short    .LDBG1392-.LDBG1391
.LDBG1391:
          .byte     0x51
.LDBG1392:
          .int      .LDBG395-.LDBG0
          .int      .LDBG396-.LDBG0
          .short    .LDBG1394-.LDBG1393
.LDBG1393:
          .byte     0x51
.LDBG1394:
          .int      .LDBG391-.LDBG0
          .int      .LDBG392-.LDBG0
          .short    .LDBG1396-.LDBG1395
.LDBG1395:
          .byte     0x51
.LDBG1396:
          .int      .LDBG393-.LDBG0
          .int      .LDBG394-.LDBG0
          .short    .LDBG1398-.LDBG1397
.LDBG1397:
          .byte     0x51
.LDBG1398:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "dataLength"
          .byte     0x01
          .short    0x0100
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1399
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1399:
          .int      .LDBG377-.LDBG0
          .int      .LDBG378-.LDBG0
          .short    .LDBG1401-.LDBG1400
.LDBG1400:
          .byte     0x7e
          .sleb128  0x14
.LDBG1401:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i_tmp"
          .byte     0x01
          .short    0x0101
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1402
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1402:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "loop"
          .byte     0x01
          .short    0x0102
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1403
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1403:
          .int      .LDBG377-.LDBG0
          .int      .LDBG378-.LDBG0
          .short    .LDBG1405-.LDBG1404
.LDBG1404:
          .byte     0x7e
          .sleb128  0x18
.LDBG1405:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
.LDBG1406:
          .uleb128  0x7
          .int      .LDBG746-.LDBG740
          .int      0x0000000c
          .uleb128  0x8
          .byte     0x00
          .int      0x00000002
          .byte     0x00
          .uleb128  0x6
          .asciiz   "buffers"
          .byte     0x01
          .short    0x0103
          .short    0x0000
          .int      .LDBG1406-.LDBG740
          .int      .LDBG1407
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1407:
          .int      .LDBG377-.LDBG0
          .int      .LDBG378-.LDBG0
          .short    .LDBG1409-.LDBG1408
.LDBG1408:
          .byte     0x7e
          .sleb128  0x1c
.LDBG1409:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "bufferAdr"
          .byte     0x01
          .short    0x0104
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .int      .LDBG1410
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1410:
          .int      .LDBG377-.LDBG0
          .int      .LDBG378-.LDBG0
          .short    .LDBG1412-.LDBG1411
.LDBG1411:
          .byte     0x7e
          .sleb128  0x28
.LDBG1412:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "shift"
          .byte     0x01
          .short    0x0105
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1413
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1413:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "go"
          .byte     0x01
          .short    0x0106
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1414
          .section  .debug_loc,    "",    @progbits
.cc_top physical.function,physical
.LDBG1414:
          .int      .LDBG377-.LDBG0
          .int      .LDBG378-.LDBG0
          .short    .LDBG1416-.LDBG1415
.LDBG1415:
          .byte     0x7e
          .sleb128  0x2c
.LDBG1416:
          .int      0x00000000
          .int      0x00000000
.cc_bottom physical.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom physical.function
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1417:
          .uleb128  0x3
          .asciiz   "XUD_Endpoint1"
          .byte     0x01
          .byte     0xb3
          .byte     0x01
          .byte     0x01
          .long     .LDBG585         # low address
          .long     .LDBG649         # high address
          .uleb128  0x5
          .asciiz   "c"
          .byte     0x01
          .short    0x01b1
          .int      .LDBG751-.LDBG740
          .int      .LDBG1418
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1418:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1420-.LDBG1419
.LDBG1419:
          .byte     0x7e
          .sleb128  0x14
.LDBG1420:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "p_aud_rst"
          .byte     0x01
          .short    0x01b1
          .int      .LDBG754-.LDBG740
          .int      .LDBG1421
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1421:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1423-.LDBG1422
.LDBG1422:
          .byte     0x7e
          .sleb128  0x18
.LDBG1423:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1425-.LDBG1424
.LDBG1424:
          .byte     0x51
.LDBG1425:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1427-.LDBG1426
.LDBG1426:
          .byte     0x51
.LDBG1427:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_BCLK"
          .byte     0x01
          .short    0x01b1
          .int      .LDBG754-.LDBG740
          .int      .LDBG1428
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1428:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1430-.LDBG1429
.LDBG1429:
          .byte     0x7e
          .sleb128  0x1c
.LDBG1430:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1432-.LDBG1431
.LDBG1431:
          .byte     0x52
.LDBG1432:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1434-.LDBG1433
.LDBG1433:
          .byte     0x52
.LDBG1434:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SCLK"
          .byte     0x01
          .short    0x01b1
          .int      .LDBG754-.LDBG740
          .int      .LDBG1435
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1435:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1437-.LDBG1436
.LDBG1436:
          .byte     0x7e
          .sleb128  0x20
.LDBG1437:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1439-.LDBG1438
.LDBG1438:
          .byte     0x53
.LDBG1439:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1441-.LDBG1440
.LDBG1440:
          .byte     0x53
.LDBG1441:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_SDIN"
          .byte     0x01
          .short    0x01b1
          .int      .LDBG754-.LDBG740
          .int      .LDBG1442
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1442:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1444-.LDBG1443
.LDBG1443:
          .byte     0x7e
          .sleb128  0x50
.LDBG1444:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1446-.LDBG1445
.LDBG1445:
          .byte     0x5b
.LDBG1446:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1448-.LDBG1447
.LDBG1447:
          .byte     0x5b
.LDBG1448:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_LRCLK"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG754-.LDBG740
          .int      .LDBG1449
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1449:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1451-.LDBG1450
.LDBG1450:
          .byte     0x7e
          .sleb128  0x54
.LDBG1451:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1453-.LDBG1452
.LDBG1452:
          .byte     0x5a
.LDBG1453:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1455-.LDBG1454
.LDBG1454:
          .byte     0x5a
.LDBG1455:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT1"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1456
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1456:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1458-.LDBG1457
.LDBG1457:
          .byte     0x7e
          .sleb128  0x58
.LDBG1458:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1460-.LDBG1459
.LDBG1459:
          .byte     0x59
.LDBG1460:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1462-.LDBG1461
.LDBG1461:
          .byte     0x59
.LDBG1462:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT2"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1463
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1463:
          .int      .LDBG585-.LDBG0
          .int      .LDBG649-.LDBG0
          .short    .LDBG1465-.LDBG1464
.LDBG1464:
          .byte     0x7e
          .sleb128  0x5c
.LDBG1465:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1467-.LDBG1466
.LDBG1466:
          .byte     0x58
.LDBG1467:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1469-.LDBG1468
.LDBG1468:
          .byte     0x58
.LDBG1469:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT3"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1470
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1470:
          .int      .LDBG588-.LDBG0
          .int      .LDBG592-.LDBG0
          .short    .LDBG1472-.LDBG1471
.LDBG1471:
          .byte     0x59
.LDBG1472:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1474-.LDBG1473
.LDBG1473:
          .byte     0x57
.LDBG1474:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1476-.LDBG1475
.LDBG1475:
          .byte     0x57
.LDBG1476:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "AUD_DOUT4"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG759-.LDBG740
          .int      .LDBG1477
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1477:
          .int      .LDBG589-.LDBG0
          .int      .LDBG592-.LDBG0
          .short    .LDBG1479-.LDBG1478
.LDBG1478:
          .byte     0x58
.LDBG1479:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1481-.LDBG1480
.LDBG1480:
          .byte     0x56
.LDBG1481:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1483-.LDBG1482
.LDBG1482:
          .byte     0x56
.LDBG1483:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "clk_aud_bit"
          .byte     0x01
          .short    0x01b2
          .int      .LDBG753-.LDBG740
          .int      .LDBG1484
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1484:
          .int      .LDBG590-.LDBG0
          .int      .LDBG592-.LDBG0
          .short    .LDBG1486-.LDBG1485
.LDBG1485:
          .byte     0x57
.LDBG1486:
          .int      .LDBG599-.LDBG0
          .int      .LDBG600-.LDBG0
          .short    .LDBG1488-.LDBG1487
.LDBG1487:
          .byte     0x55
.LDBG1488:
          .int      .LDBG597-.LDBG0
          .int      .LDBG598-.LDBG0
          .short    .LDBG1490-.LDBG1489
.LDBG1489:
          .byte     0x55
.LDBG1490:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "time"
          .byte     0x01
          .short    0x01b4
          .short    0x0000
          .int      .LDBG746-.LDBG740
          .int      .LDBG1491
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1491:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "t"
          .byte     0x01
          .short    0x01b6
          .short    0x0000
          .int      .LDBG752-.LDBG740
          .int      .LDBG1492
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1492:
          .int      .LDBG593-.LDBG0
          .int      .LDBG594-.LDBG0
          .short    .LDBG1494-.LDBG1493
.LDBG1493:
          .byte     0x54
.LDBG1494:
          .int      .LDBG595-.LDBG0
          .int      .LDBG596-.LDBG0
          .short    .LDBG1496-.LDBG1495
.LDBG1495:
          .byte     0x54
.LDBG1496:
          .int      .LDBG591-.LDBG0
          .int      .LDBG592-.LDBG0
          .short    .LDBG1498-.LDBG1497
.LDBG1497:
          .byte     0x54
.LDBG1498:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "WrData"
          .byte     0x01
          .short    0x01b7
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1499
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1499:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "value"
          .byte     0x01
          .short    0x01b8
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1500
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1500:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "tmp"
          .byte     0x01
          .short    0x01b9
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1501
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1501:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i_CurSampFreq"
          .byte     0x01
          .short    0x01ba
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1502
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1502:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "dataLengthA"
          .byte     0x01
          .short    0x01bc
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1503
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1503:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "dataLengthB"
          .byte     0x01
          .short    0x01bc
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1504
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1504:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x6
          .asciiz   "i_tmp"
          .byte     0x01
          .short    0x01bd
          .short    0x0000
          .int      .LDBG745-.LDBG740
          .int      .LDBG1505
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
.LDBG1505:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_Endpoint1.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom XUD_Endpoint1.function
.cc_top p_but.data,p_but
          .uleb128  0x9
          .asciiz   "p_but"
          .byte     0x01
          .short    0x01de
          .int      .LDBG754-.LDBG740
          .short    .LDBG1507-.LDBG1506
.LDBG1506:
          .byte     0x03
          .int      p_but
.LDBG1507:
.cc_bottom p_but.data
.cc_top p_usb_clk.data,p_usb_clk
          .uleb128  0x9
          .asciiz   "p_usb_clk"
          .byte     0x01
          .short    0x01e1
          .int      .LDBG754-.LDBG740
          .short    .LDBG1509-.LDBG1508
.LDBG1508:
          .byte     0x03
          .int      p_usb_clk
.LDBG1509:
.cc_bottom p_usb_clk.data
.cc_top reg_write_port.data,reg_write_port
          .uleb128  0x9
          .asciiz   "reg_write_port"
          .byte     0x01
          .short    0x01e2
          .int      .LDBG754-.LDBG740
          .short    .LDBG1511-.LDBG1510
.LDBG1510:
          .byte     0x03
          .int      reg_write_port
.LDBG1511:
.cc_bottom reg_write_port.data
.cc_top reg_read_port.data,reg_read_port
          .uleb128  0x9
          .asciiz   "reg_read_port"
          .byte     0x01
          .short    0x01e3
          .int      .LDBG754-.LDBG740
          .short    .LDBG1513-.LDBG1512
.LDBG1512:
          .byte     0x03
          .int      reg_read_port
.LDBG1513:
.cc_bottom reg_read_port.data
.cc_top flag0_port.data,flag0_port
          .uleb128  0x9
          .asciiz   "flag0_port"
          .byte     0x01
          .short    0x01e4
          .int      .LDBG754-.LDBG740
          .short    .LDBG1515-.LDBG1514
.LDBG1514:
          .byte     0x03
          .int      flag0_port
.LDBG1515:
.cc_bottom flag0_port.data
.cc_top flag1_port.data,flag1_port
          .uleb128  0x9
          .asciiz   "flag1_port"
          .byte     0x01
          .short    0x01e5
          .int      .LDBG754-.LDBG740
          .short    .LDBG1517-.LDBG1516
.LDBG1516:
          .byte     0x03
          .int      flag1_port
.LDBG1517:
.cc_bottom flag1_port.data
.cc_top flag2_port.data,flag2_port
          .uleb128  0x9
          .asciiz   "flag2_port"
          .byte     0x01
          .short    0x01e6
          .int      .LDBG754-.LDBG740
          .short    .LDBG1519-.LDBG1518
.LDBG1518:
          .byte     0x03
          .int      flag2_port
.LDBG1519:
.cc_bottom flag2_port.data
.cc_top p_usb_txd.data,p_usb_txd
          .uleb128  0x9
          .asciiz   "p_usb_txd"
          .byte     0x01
          .short    0x01e7
          .int      .LDBG754-.LDBG740
          .short    .LDBG1521-.LDBG1520
.LDBG1520:
          .byte     0x03
          .int      p_usb_txd
.LDBG1521:
.cc_bottom p_usb_txd.data
.cc_top p_usb_rxd.data,p_usb_rxd
          .uleb128  0x9
          .asciiz   "p_usb_rxd"
          .byte     0x01
          .short    0x01e8
          .int      .LDBG754-.LDBG740
          .short    .LDBG1523-.LDBG1522
.LDBG1522:
          .byte     0x03
          .int      p_usb_rxd
.LDBG1523:
.cc_bottom p_usb_rxd.data
.cc_top p_usb_rst.data,p_usb_rst
          .uleb128  0x9
          .asciiz   "p_usb_rst"
          .byte     0x01
          .short    0x01e9
          .int      .LDBG754-.LDBG740
          .short    .LDBG1525-.LDBG1524
.LDBG1524:
          .byte     0x03
          .int      p_usb_rst
.LDBG1525:
.cc_bottom p_usb_rst.data
.cc_top p_test.data,p_test
          .uleb128  0x9
          .asciiz   "p_test"
          .byte     0x01
          .short    0x01ea
          .int      .LDBG754-.LDBG740
          .short    .LDBG1527-.LDBG1526
.LDBG1526:
          .byte     0x03
          .int      p_test
.LDBG1527:
.cc_bottom p_test.data
.cc_top p_test2.data,p_test2
          .uleb128  0x9
          .asciiz   "p_test2"
          .byte     0x01
          .short    0x01eb
          .int      .LDBG754-.LDBG740
          .short    .LDBG1529-.LDBG1528
.LDBG1528:
          .byte     0x03
          .int      p_test2
.LDBG1529:
.cc_bottom p_test2.data
.cc_top clk.data,clk
          .uleb128  0x9
          .asciiz   "clk"
          .byte     0x01
          .short    0x01ec
          .int      .LDBG753-.LDBG740
          .short    .LDBG1531-.LDBG1530
.LDBG1530:
          .byte     0x03
          .int      clk
.LDBG1531:
.cc_bottom clk.data
.cc_top p_aud_sclk.data,p_aud_sclk
          .uleb128  0x9
          .asciiz   "p_aud_sclk"
          .byte     0x01
          .short    0x01ef
          .int      .LDBG754-.LDBG740
          .short    .LDBG1533-.LDBG1532
.LDBG1532:
          .byte     0x03
          .int      p_aud_sclk
.LDBG1533:
.cc_bottom p_aud_sclk.data
.cc_top p_aud_lrclk.data,p_aud_lrclk
          .uleb128  0x9
          .asciiz   "p_aud_lrclk"
          .byte     0x01
          .short    0x01f0
          .int      .LDBG754-.LDBG740
          .short    .LDBG1535-.LDBG1534
.LDBG1534:
          .byte     0x03
          .int      p_aud_lrclk
.LDBG1535:
.cc_bottom p_aud_lrclk.data
.cc_top p_aud_scl.data,p_aud_scl
          .uleb128  0x9
          .asciiz   "p_aud_scl"
          .byte     0x01
          .short    0x01f1
          .int      .LDBG754-.LDBG740
          .short    .LDBG1537-.LDBG1536
.LDBG1536:
          .byte     0x03
          .int      p_aud_scl
.LDBG1537:
.cc_bottom p_aud_scl.data
.cc_top p_aud_sda.data,p_aud_sda
          .uleb128  0x9
          .asciiz   "p_aud_sda"
          .byte     0x01
          .short    0x01f2
          .int      .LDBG754-.LDBG740
          .short    .LDBG1539-.LDBG1538
.LDBG1538:
          .byte     0x03
          .int      p_aud_sda
.LDBG1539:
.cc_bottom p_aud_sda.data
.cc_top p_aud_rst.data,p_aud_rst
          .uleb128  0x9
          .asciiz   "p_aud_rst"
          .byte     0x01
          .short    0x01f3
          .int      .LDBG754-.LDBG740
          .short    .LDBG1541-.LDBG1540
.LDBG1540:
          .byte     0x03
          .int      p_aud_rst
.LDBG1541:
.cc_bottom p_aud_rst.data
.cc_top p_aud_clk.data,p_aud_clk
          .uleb128  0x9
          .asciiz   "p_aud_clk"
          .byte     0x01
          .short    0x01f4
          .int      .LDBG754-.LDBG740
          .short    .LDBG1543-.LDBG1542
.LDBG1542:
          .byte     0x03
          .int      p_aud_clk
.LDBG1543:
.cc_bottom p_aud_clk.data
.cc_top p_aud_dac1.data,p_aud_dac1
          .uleb128  0x9
          .asciiz   "p_aud_dac1"
          .byte     0x01
          .short    0x01f5
          .int      .LDBG759-.LDBG740
          .short    .LDBG1545-.LDBG1544
.LDBG1544:
          .byte     0x03
          .int      p_aud_dac1
.LDBG1545:
.cc_bottom p_aud_dac1.data
.cc_top p_aud_dac2.data,p_aud_dac2
          .uleb128  0x9
          .asciiz   "p_aud_dac2"
          .byte     0x01
          .short    0x01f6
          .int      .LDBG759-.LDBG740
          .short    .LDBG1547-.LDBG1546
.LDBG1546:
          .byte     0x03
          .int      p_aud_dac2
.LDBG1547:
.cc_bottom p_aud_dac2.data
.cc_top p_aud_dac3.data,p_aud_dac3
          .uleb128  0x9
          .asciiz   "p_aud_dac3"
          .byte     0x01
          .short    0x01f7
          .int      .LDBG759-.LDBG740
          .short    .LDBG1549-.LDBG1548
.LDBG1548:
          .byte     0x03
          .int      p_aud_dac3
.LDBG1549:
.cc_bottom p_aud_dac3.data
.cc_top p_aud_dac4.data,p_aud_dac4
          .uleb128  0x9
          .asciiz   "p_aud_dac4"
          .byte     0x01
          .short    0x01f8
          .int      .LDBG759-.LDBG740
          .short    .LDBG1551-.LDBG1550
.LDBG1550:
          .byte     0x03
          .int      p_aud_dac4
.LDBG1551:
.cc_bottom p_aud_dac4.data
.cc_top clk_aud_bit.data,clk_aud_bit
          .uleb128  0x9
          .asciiz   "clk_aud_bit"
          .byte     0x01
          .short    0x01f9
          .int      .LDBG753-.LDBG740
          .short    .LDBG1553-.LDBG1552
.LDBG1552:
          .byte     0x03
          .int      clk_aud_bit
.LDBG1553:
.cc_bottom clk_aud_bit.data
.cc_top __main_xm_4.function,__main_xm_4
.LDBG1554:
          .uleb128  0x3
          .asciiz   "__main_xm_4"
          .byte     0x01
          .byte     0x0e
          .byte     0x01
          .byte     0x01
          .long     .LDBG650         # low address
          .long     .LDBG665         # high address
          .uleb128  0x5
          .asciiz   "c_sof"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1555
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_4.function,__main_xm_4
.LDBG1555:
          .int      .LDBG651-.LDBG0
          .int      .LDBG652-.LDBG0
          .short    .LDBG1557-.LDBG1556
.LDBG1556:
          .byte     0x50
.LDBG1557:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_4.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom __main_xm_4.function
.cc_top __main_xm_3.function,__main_xm_3
.LDBG1558:
          .uleb128  0x3
          .asciiz   "__main_xm_3"
          .byte     0x01
          .byte     0x0e
          .byte     0x01
          .byte     0x01
          .long     .LDBG666         # low address
          .long     .LDBG681         # high address
          .uleb128  0x5
          .asciiz   "c_ep1_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1559
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_3.function,__main_xm_3
.LDBG1559:
          .int      .LDBG667-.LDBG0
          .int      .LDBG668-.LDBG0
          .short    .LDBG1561-.LDBG1560
.LDBG1560:
          .byte     0x50
.LDBG1561:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_3.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom __main_xm_3.function
.cc_top __main_xm_2.function,__main_xm_2
.LDBG1562:
          .uleb128  0x3
          .asciiz   "__main_xm_2"
          .byte     0x01
          .byte     0x0e
          .byte     0x01
          .byte     0x01
          .long     .LDBG682         # low address
          .long     .LDBG698         # high address
          .uleb128  0x5
          .asciiz   "c_ep0_ctl"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1563
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_2.function,__main_xm_2
.LDBG1563:
          .int      .LDBG683-.LDBG0
          .int      .LDBG685-.LDBG0
          .short    .LDBG1565-.LDBG1564
.LDBG1564:
          .byte     0x52
.LDBG1565:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_2.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_ep0_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1566
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_2.function,__main_xm_2
.LDBG1566:
          .int      .LDBG684-.LDBG0
          .int      .LDBG685-.LDBG0
          .short    .LDBG1568-.LDBG1567
.LDBG1567:
          .byte     0x51
.LDBG1568:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_2.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom __main_xm_2.function
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1569:
          .uleb128  0x3
          .asciiz   "__main_xm_1"
          .byte     0x01
          .byte     0x0e
          .byte     0x01
          .byte     0x01
          .long     .LDBG699         # low address
          .long     .LDBG720         # high address
          .uleb128  0x5
          .asciiz   "c_ep3_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1570
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1570:
          .int      .LDBG700-.LDBG0
          .int      .LDBG705-.LDBG0
          .short    .LDBG1572-.LDBG1571
.LDBG1571:
          .byte     0x5b
.LDBG1572:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_ep1_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1573
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1573:
          .int      .LDBG701-.LDBG0
          .int      .LDBG705-.LDBG0
          .short    .LDBG1575-.LDBG1574
.LDBG1574:
          .byte     0x51
.LDBG1575:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_ep0_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1576
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1576:
          .int      .LDBG702-.LDBG0
          .int      .LDBG705-.LDBG0
          .short    .LDBG1578-.LDBG1577
.LDBG1577:
          .byte     0x52
.LDBG1578:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_sof"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1579
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1579:
          .int      .LDBG703-.LDBG0
          .int      .LDBG705-.LDBG0
          .short    .LDBG1581-.LDBG1580
.LDBG1580:
          .byte     0x54
.LDBG1581:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_1.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "c_ep2_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1582
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_1.function,__main_xm_1
.LDBG1582:
          .int      .LDBG704-.LDBG0
          .int      .LDBG705-.LDBG0
          .short    .LDBG1584-.LDBG1583
.LDBG1583:
          .byte     0x53
.LDBG1584:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_1.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom __main_xm_1.function
.cc_top __main_xm_0.function,__main_xm_0
.LDBG1585:
          .uleb128  0x3
          .asciiz   "__main_xm_0"
          .byte     0x01
          .byte     0x0e
          .byte     0x01
          .byte     0x01
          .long     .LDBG721         # low address
          .long     .LDBG736         # high address
          .uleb128  0x5
          .asciiz   "c_ep2_dat"
          .byte     0x01
          .short    0x0217
          .int      .LDBG751-.LDBG740
          .int      .LDBG1586
          .section  .debug_loc,    "",    @progbits
.cc_top __main_xm_0.function,__main_xm_0
.LDBG1586:
          .int      .LDBG722-.LDBG0
          .int      .LDBG723-.LDBG0
          .short    .LDBG1588-.LDBG1587
.LDBG1587:
          .byte     0x50
.LDBG1588:
          .int      0x00000000
          .int      0x00000000
.cc_bottom __main_xm_0.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom __main_xm_0.function
          .byte     0x00
.LDBG742:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG1590-.LDBG1589
.LDBG1589:
          .short    0x0002
          .long     .LDBG740         # offset in .debug_info
          .int      .LDBG742-.LDBG740
.cc_top ep3dead.function,ep3dead
          .int      .LDBG760-.LDBG740
          .asciiz   "ep3dead"
.cc_bottom ep3dead.function
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
          .int      .LDBG761-.LDBG740
          .asciiz   "XUD_Endpoint3"
.cc_bottom XUD_Endpoint3.function
.cc_top dummyEP.function,dummyEP
          .int      .LDBG790-.LDBG740
          .asciiz   "dummyEP"
.cc_bottom dummyEP.function
.cc_top doClock.function,doClock
          .int      .LDBG801-.LDBG740
          .asciiz   "doClock"
.cc_bottom doClock.function
.cc_top readData.function,readData
          .int      .LDBG887-.LDBG740
          .asciiz   "readData"
.cc_bottom readData.function
.cc_top deliver.function,deliver
          .int      .LDBG971-.LDBG740
          .asciiz   "deliver"
.cc_bottom deliver.function
.cc_top physical.function,physical
          .int      .LDBG1131-.LDBG740
          .asciiz   "physical"
.cc_bottom physical.function
.cc_top XUD_Endpoint1.function,XUD_Endpoint1
          .int      .LDBG1417-.LDBG740
          .asciiz   "XUD_Endpoint1"
.cc_bottom XUD_Endpoint1.function
.cc_top __main_xm_4.function,__main_xm_4
          .int      .LDBG1554-.LDBG740
          .asciiz   "__main_xm_4"
.cc_bottom __main_xm_4.function
.cc_top __main_xm_3.function,__main_xm_3
          .int      .LDBG1558-.LDBG740
          .asciiz   "__main_xm_3"
.cc_bottom __main_xm_3.function
.cc_top __main_xm_2.function,__main_xm_2
          .int      .LDBG1562-.LDBG740
          .asciiz   "__main_xm_2"
.cc_bottom __main_xm_2.function
.cc_top __main_xm_1.function,__main_xm_1
          .int      .LDBG1569-.LDBG740
          .asciiz   "__main_xm_1"
.cc_bottom __main_xm_1.function
.cc_top __main_xm_0.function,__main_xm_0
          .int      .LDBG1585-.LDBG740
          .asciiz   "__main_xm_0"
.cc_bottom __main_xm_0.function
          .int      0x00000000
.LDBG1590:
          .section  .debug_line,   "",    @progbits
.LDBG739:
          .int      .LDBG1592-.LDBG1591
.LDBG1591:
          .short    0x0003
          .int      .LDBG1594-.LDBG1593
.LDBG1593:
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
          .asciiz   "./tests/audio/src/audio.xc"
          .uleb128  0x0
          .uleb128  0x49b92c47
          .uleb128  0x4809
          .byte     0x00
.LDBG1594:
.cc_top ep3dead.function, ep3dead
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG9
          .byte     0x03
          .sleb128  0x1d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG12
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG14
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom ep3dead.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG35
          .byte     0x03
          .sleb128  0x25
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG38
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG39
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG40
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG41
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG42
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG43
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG45
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG46
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG48
          .byte     0x03
          .sleb128  0xfffffffa
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG49
          .byte     0x03
          .sleb128  0xfffffffd
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG50
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom XUD_Endpoint3.function
.cc_top dummyEP.function, dummyEP
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG63
          .byte     0x03
          .sleb128  0x44
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG66
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG67
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG69
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG71
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG73
          .byte     0x03
          .sleb128  0xfffffffc
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG74
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom dummyEP.function
.cc_top doClock.function, doClock
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG109
          .byte     0x03
          .sleb128  0x50
          .byte     0x01
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
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG114
          .byte     0x03
          .sleb128  0x3
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
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG117
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG119
          .byte     0x03
          .sleb128  0x3
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
          .int      .LDBG123
          .byte     0x03
          .sleb128  0xfffffff9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG124
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG126
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG127
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG128
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG129
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom doClock.function
.cc_top readData.function, readData
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG166
          .byte     0x03
          .sleb128  0x73
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG169
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG170
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG171
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG173
          .byte     0x12             # special opcode (line + 1)
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
          .int      .LDBG180
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG182
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG184
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG185
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG187
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG189
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG191
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG193
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG194
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG196
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG198
          .byte     0x03
          .sleb128  0xffffffed
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG199
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG200
          .byte     0x03
          .sleb128  0xfffffffb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG201
          .byte     0x03
          .sleb128  0xb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG202
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG203
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom readData.function
.cc_top deliver.function, deliver
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG249
          .byte     0x03
          .sleb128  0xa2
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG259
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG260
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG261
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG262
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG264
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG266
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG267
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG268
          .byte     0x03
          .sleb128  0x34
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG269
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG270
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG271
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG272
          .byte     0x03
          .sleb128  0xffffffbf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG273
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG283
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG288
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG290
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG295
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG297
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG302
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG304
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG309
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG311
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG312
          .byte     0x12             # special opcode (line + 1)
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
          .int      .LDBG315
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG320
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG322
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG327
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG329
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG334
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG336
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG341
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG343
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG345
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG347
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG350
          .byte     0x03
          .sleb128  0xfffffff9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG351
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG361
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG371
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom deliver.function
.cc_top physical.function, physical
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG447
          .byte     0x03
          .sleb128  0xf9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG450
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG451
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG453
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG454
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG456
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG458
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG460
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
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
          .int      .LDBG464
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG465
          .byte     0x03
          .sleb128  0x9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG466
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG468
          .byte     0x03
          .sleb128  0xb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG469
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG471
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG473
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG475
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG477
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG479
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG480
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG482
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG483
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG484
          .byte     0x03
          .sleb128  0x30
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG486
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG487
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG488
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG489
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG490
          .byte     0x03
          .sleb128  0x9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG493
          .byte     0x03
          .sleb128  0xfffffff5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG494
          .byte     0x03
          .sleb128  0x15
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG495
          .byte     0x03
          .sleb128  0xffffffa8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG496
          .byte     0x03
          .sleb128  0xe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG498
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG500
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG502
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG504
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG505
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG507
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG509
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG511
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG513
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG515
          .byte     0x13             # special opcode (line + 2)
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
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG520
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG522
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG524
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG526
          .byte     0x13             # special opcode (line + 2)
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
          .sleb128  0x4
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
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG538
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG540
          .byte     0x03
          .sleb128  0x1b
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG541
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG542
          .byte     0x03
          .sleb128  0xfffffff6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG544
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG546
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG547
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG548
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG550
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG552
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG554
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG556
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG558
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG560
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG562
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG564
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG565
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG566
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG567
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG568
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG569
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG570
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG573
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG575
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG577
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG578
          .byte     0x03
          .sleb128  0xfffffff9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG579
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG580
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom physical.function
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG602
          .byte     0x03
          .sleb128  0x1b2
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG612
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG614
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG615
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG616
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG618
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG620
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG622
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG624
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG626
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG628
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG630
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG631
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG633
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG643
          .byte     0x03
          .sleb128  0xfffffffe
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG645
          .byte     0x10             # special opcode (line + -1)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom XUD_Endpoint1.function
.cc_top __main_xm_4.function, __main_xm_4
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG654
          .byte     0x03
          .sleb128  0x20d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG657
          .byte     0x03
          .sleb128  0x15
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG659
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom __main_xm_4.function
.cc_top __main_xm_3.function, __main_xm_3
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG670
          .byte     0x03
          .sleb128  0x20d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG673
          .byte     0x03
          .sleb128  0x13
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG674
          .byte     0x10             # special opcode (line + -1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG675
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom __main_xm_3.function
.cc_top __main_xm_2.function, __main_xm_2
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG687
          .byte     0x03
          .sleb128  0x20d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG690
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG692
          .byte     0x03
          .sleb128  0xb
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom __main_xm_2.function
.cc_top __main_xm_1.function, __main_xm_1
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG707
          .byte     0x03
          .sleb128  0x20d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG711
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG713
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom __main_xm_1.function
.cc_top __main_xm_0.function, __main_xm_0
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG725
          .byte     0x03
          .sleb128  0x20d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG728
          .byte     0x03
          .sleb128  0xb
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG730
          .byte     0x03
          .sleb128  0xf
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom __main_xm_0.function
.LDBG1592:
          .section  .debug_abbrev, "",    @progbits
.LDBG738:
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
          .uleb128  0x9
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
          .uleb128  0xb
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
          .byte     0x03
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
          .uleb128  0xa
          .byte     0x10
          .byte     0x00
          .byte     0x49
          .byte     0x13
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
.L218:
          .int      .L219-.L218
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001c1
          .long    .L212
.cc_bottom XUD_Endpoint1.function
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001c0
          .long    .L211
.cc_bottom XUD_Endpoint1.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001aa
          .long    .L203
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001a9
          .long    .L202
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000019e
          .long    .L201
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000019d
          .long    .L200
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000019c
          .long    .L199
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000019b
          .long    .L198
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000198
          .long    .L197
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000196
          .long    .L196
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000195
          .long    .L195
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000194
          .long    .L194
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000017d
          .long    .L193
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000017c
          .long    .L192
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000010f
          .long    .L191
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000010e
          .long    .L190
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000010d
          .long    .L189
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000010c
          .long    .L188
.cc_bottom physical.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000c8
          .long    .L132
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000c7
          .long    .L131
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000c6
          .long    .L130
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000c5
          .long    .L129
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000ee
          .long    .L128
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000ed
          .long    .L127
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000ec
          .long    .L126
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000eb
          .long    .L125
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000af
          .long    .L124
.cc_bottom deliver.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000008a
          .long    .L93
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000089
          .long    .L92
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000084
          .long    .L91
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000083
          .long    .L90
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000007e
          .long    .L89
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000007d
          .long    .L88
.cc_bottom readData.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000065
          .long    .L59
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000005e
          .long    .L58
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000060
          .long    .L57
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000059
          .long    .L56
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000058
          .long    .L55
.cc_bottom doClock.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000004a
          .long    .L37
.cc_bottom dummyEP.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000049
          .long    .L36
.cc_bottom dummyEP.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000048
          .long    .L35
.cc_bottom dummyEP.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000033
          .long    .L25
.cc_bottom XUD_Endpoint3.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000002e
          .long    .L24
.cc_bottom XUD_Endpoint3.function
.L219:
          .section .xtalabeltable,       "", @progbits
.L220:
          .int      .L221-.L220
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
.cc_top __main_xm_0.function, __main_xm_0
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L217
.cc_bottom __main_xm_0.function
.cc_top __main_xm_1.function, __main_xm_1
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L216
.cc_bottom __main_xm_1.function
.cc_top __main_xm_2.function, __main_xm_2
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L215
.cc_bottom __main_xm_2.function
.cc_top __main_xm_3.function, __main_xm_3
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L214
.cc_bottom __main_xm_3.function
.cc_top __main_xm_4.function, __main_xm_4
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L213
.cc_bottom __main_xm_4.function
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001d5
          .int      0x000001d4
          .long    .L210
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001d7
          .int      0x000001d7
          .long    .L209
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001d3
          .int      0x000001d3
          .long    .L208
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001b3
          .int      0x000001d1
          .long    .L207
.cc_bottom XUD_Endpoint1.function
.cc_top physical.function, physical
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001af
          .int      0x000001af
          .long    .L187
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000135
          .int      0x00000135
          .long    .L186
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001a4
          .int      0x000001a4
          .long    .L185
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001a9
          .int      0x000001ab
          .long    .L184
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001a7
          .int      0x000001a7
          .long    .L183
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000001a6
          .int      0x000001a6
          .long    .L182
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000018f
          .int      0x000001a4
          .long    .L181
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000018d
          .int      0x0000018d
          .long    .L180
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000178
          .int      0x00000178
          .long    .L179
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000017c
          .int      0x0000017f
          .long    .L178
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000185
          .int      0x00000186
          .long    .L177
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000183
          .int      0x00000183
          .long    .L176
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000017a
          .int      0x0000017a
          .long    .L175
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000174
          .int      0x00000178
          .long    .L174
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000016f
          .int      0x0000016f
          .long    .L173
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000163
          .int      0x0000016a
          .long    .L172
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000159
          .int      0x0000015f
          .long    .L171
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000014f
          .int      0x00000155
          .long    .L170
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000143
          .int      0x00000149
          .long    .L169
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000013d
          .int      0x0000013f
          .long    .L168
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000fa
          .int      0x00000135
          .long    .L167
.cc_bottom physical.function
.cc_top deliver.function, deliver
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000f2
          .int      0x000000f2
          .long    .L123
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000ad
          .int      0x000000ad
          .long    .L122
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000eb
          .int      0x000000ee
          .long    .L121
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000e3
          .int      0x000000e3
          .long    .L120
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000e2
          .int      0x000000e2
          .long    .L119
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000e1
          .int      0x000000e1
          .long    .L118
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000e0
          .int      0x000000e0
          .long    .L117
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000db
          .int      0x000000db
          .long    .L116
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000ba
          .int      0x000000d9
          .long    .L115
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000b5
          .int      0x000000b7
          .long    .L114
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000b3
          .int      0x000000b3
          .long    .L113
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000af
          .int      0x000000b1
          .long    .L112
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x000000a3
          .int      0x000000ad
          .long    .L111
.cc_bottom deliver.function
.cc_top readData.function, readData
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L87
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000076
          .int      0x00000076
          .long    .L86
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L85
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000088
          .int      0x0000008a
          .long    .L84
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L83
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000082
          .int      0x00000087
          .long    .L82
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000081
          .int      0x00000081
          .long    .L81
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000007c
          .int      0x00000081
          .long    .L80
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L79
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000007a
          .int      0x0000007b
          .long    .L78
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L77
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000074
          .int      0x00000076
          .long    .L76
.cc_bottom readData.function
.cc_top doClock.function, doClock
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000006b
          .int      0x0000006b
          .long    .L54
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000005b
          .int      0x0000005b
          .long    .L53
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000064
          .int      0x00000066
          .long    .L52
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000060
          .int      0x00000062
          .long    .L51
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000005e
          .int      0x0000005e
          .long    .L50
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000005d
          .int      0x0000005d
          .long    .L49
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000051
          .int      0x0000005b
          .long    .L48
.cc_bottom doClock.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000004c
          .int      0x0000004c
          .long    .L34
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000046
          .int      0x00000046
          .long    .L33
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000048
          .int      0x0000004a
          .long    .L32
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000045
          .int      0x00000046
          .long    .L31
.cc_bottom dummyEP.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000003d
          .int      0x0000003d
          .long    .L23
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L22
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000033
          .int      0x00000033
          .long    .L21
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000035
          .int      0x00000039
          .long    .L20
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000032
          .int      0x00000033
          .long    .L19
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000026
          .int      0x00000030
          .long    .L18
.cc_bottom XUD_Endpoint3.function
.cc_top ep3dead.function, ep3dead
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x00000020
          .int      0x00000020
          .long    .L7
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000001f
          .int      0x0000001f
          .long    .L6
          .asciiz  "./tests/audio/src/audio.xc"
          .int      0x0000001e
          .int      0x0000001f
          .long    .L5
.cc_bottom ep3dead.function
.L221:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
