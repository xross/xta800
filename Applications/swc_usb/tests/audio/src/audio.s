          .file     "audio.xc"
          .section .netinfo,       "", @netinfo
          .int      0x1eaba15c
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
          .align    2
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
          .align    4
ep3dead:
.L3:
.L1:
.L4:
          ldc       r0, 0x1
          bt        r0, .L2 
          bu        .L0 
.L2:
          bu        .L1 
.L0:
.L5:
          retsp     0x0 
.cc_bottom ep3dead.function
.linkset ep3dead.maxchanends, 0
.linkset ep3dead.maxtimers, 0
.linkset ep3dead.maxthreads, 1
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
.linkset .LLNK0, .LLNK1 + 6
.linkset XUD_Endpoint3.nstackwords, .LLNK0
.cc_top XUD_Endpoint3.function,XUD_Endpoint3
          .align    4
XUD_Endpoint3:
          entsp     0x6 
.L12:
          stw       r4, sp[0x2] 
          stw       r5, sp[0x3] 
          mov       r5, r0
          mov       r4, r1
          ldc       r0, 0x1
          ldaw      r0, sp[0x4] 
          ldaw      r1, dp[s.LC0] 
          ldc       r2, 0x8
          bl        __crt_memcpy 
          setc      res[r4], 0x1
.L18:
          in        r0, res[r4] 
.L7:
.L13:
          ldc       r0, 0x1
          bt        r0, .L8 
          bu        .L6 
.L8:
.L14:
          ldc       r0, 0x1
.L10:
.L15:
          ldw       r11, cp[s.LC1]
          .section .cp.const4,     "acM", @progbits, 4
s.LC1:
          .align    4
          .int      0xffffffff
          .text
          eq        r0, r0, r11
          bf        r0, .L11 
          bu        .L9 
.L11:
.L16:
          setc      res[r4], 0x1
.L19:
          in        r0, res[r4] 
          not       r1, r0
          ldc       r0, 0x7
          and       r2, r1, r0
          ldaw      r1, sp[0x4] 
          ldc       r0, 0x0
          st8       r2, r1[r0] 
          ldc       r0, 0x0
          ld8u      r0, r1[r0] 
          ldc       r1, 0x2
          ldaw      r2, sp[0x4] 
          ldc       r3, 0x4
          mov       r0, r5
          ldc       r11, 0x8
          stw       r11, sp[0x1] 
          bl        XUD_SetBuffer_ 
          bu        .L10 
.L9:
          bu        .L7 
.L6:
.L17:
          ldc       r0, 0x0
          ldw       r4, sp[0x2] 
          ldw       r5, sp[0x3] 
          retsp     0x6 
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
          .section .cp.const4,     "acM", @progbits, 4
          .align    4
s.LC2:
          .int      8
          .set      buffer.locbound,s.LC2
.extern lrand48, "f{sl}(0)"
.call dummyEP, __builtin_inct
.call dummyEP, __builtin_stop_streaming_slave
.call dummyEP, __builtin_stop_streaming_slave
.set __builtin_inct, 0
.linkset __builtin_inct.locnoside, 0
.linkset __builtin_inct.locnochandec, 1
.set __builtin_stop_streaming_slave, 0
.linkset __builtin_stop_streaming_slave.locnoside, 0
.linkset __builtin_stop_streaming_slave.locnochandec, 1
          .text
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
          .align    4
dummyEP:
.L23:
.L21:
.L24:
          ldc       r2, 0x1
          bt        r2, .L22 
          bu        .L20 
.L22:
.L25:
.L27:
          inct      r2, res[r1] 
.L28:
          outct     res[r0], 0x1 
          chkct     res[r0], 0x1 
.L29:
          outct     res[r1], 0x1 
          chkct     res[r1], 0x1 
          bu        .L21 
.L20:
.L26:
          ldc       r0, 0x0
          retsp     0x0 
.cc_bottom dummyEP.function
.linkset dummyEP.maxchanends, 0
.linkset dummyEP.maxtimers, 0
.linkset dummyEP.maxthreads, 1
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
.linkset doClock.nstackwords, 1
.cc_top doClock.function,doClock
          .align    4
doClock:
          extsp     0x1 
.L38:
          stw       r4, sp[0x0] 
          ldc       r2, 0x0
          getr      r4, 0x1
          ldc       r3, 0x0
.L45:
          out       res[r1], r2 
          setc      res[r4], 0x1
.L46:
          in        r11, res[r4] 
.L31:
.L39:
          ldc       r11, 0x1
          bt        r11, .L32 
          bu        .L30 
.L32:
.L40:
          testct    r11, res[r0] 
          bt        r11, .L34 
          bu        .L35 
.L34:
.L41:
.L47:
          inct      r11, res[r0] 
          bu        .L33 
.L35:
.L42:
.L48:
          in        r11, res[r0] 
          add       r3, r3, 0x1
          eq        r11, r3, 0x5
          bt        r11, .L37 
          bu        .L36 
.L37:
.L43:
          eq        r2, r2, 0x0
.L49:
          out       res[r1], r2 
          ldc       r3, 0x0
.L36:
.L33:
          bu        .L31 
.L30:
.L44:
          freer     res[r4]
          ldw       r4, sp[0x0] 
          ldaw      sp, sp[0x1] 
          retsp     0x0 
.cc_bottom doClock.function
.linkset doClock.maxchanends, 0
.linkset doClock.maxtimers, 1
.linkset doClock.maxthreads, 1
.extern GetInt, "f{si}(ui,si)"
.globl data, "si"
.type  data, @object
          .section .dp.bss,        "adw", @nobits
.cc_top data.data, data
          .align    4
data:
          .int      0x00000000
.cc_bottom data.data
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "audio.xc:124: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "audio.xc:130: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.call readData, XUD_GetBuffer_int
.globwrite readData, data, "audio.xc:136: error: previously used here"
.call readData, __builtin_out_uint
.call readData, __builtin_out_uint
.set __builtin_out_uint, 0
.linkset __builtin_out_uint.locnoside, 0
.linkset __builtin_out_uint.locnochandec, 1
          .align    4
          .text
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
.linkset .LLNK11, .LLNK12 + 9
.linkset readData.nstackwords, .LLNK11
.cc_top readData.function,readData
          .align    4
readData:
          entsp     0x9 
.L62:
          stw       r4, sp[0x2] 
          stw       r5, sp[0x3] 
          stw       r6, sp[0x4] 
          stw       r7, sp[0x5] 
          stw       r8, sp[0x6] 
          stw       r9, sp[0x7] 
          stw       r10, sp[0x8] 
          mov       r10, r0
          stw       r1, sp[0x1] 
          mov       r9, r2
          mov       r8, r3
          ldw       r7, sp[0xa] 
          ldw       r6, sp[0xb] 
          ldw       r5, sp[0xc] 
          ldw       r4, sp[0xd] 
.L51:
.L63:
          ldc       r0, 0x1
          bt        r0, .L52 
          bu        .L50 
.L52:
.L54:
.L64:
          ldc       r0, 0x1
          bt        r0, .L55 
          bu        .L53 
.L55:
.L65:
          ldc       r1, 0x1
          ldw       r0, sp[0x1] 
          add       r2, r0, 0x0
          mov       r0, r10
          mov       r3, r6
          bl        XUD_GetBuffer_int 
          ldw       r11, cp[s.LC1]
          eq        r1, r0, r11
          bt        r1, .L57 
          bu        .L56 
.L57:
.L66:
          bu        .L53 
.L56:
.L67:
          ldc       r1, 0x0

          stw       r1, dp[data] 
          ashr      r0, r0, 0x2
.L73:
          out       res[r7], r0 
          ldc       r0, 0x0
.L74:
          out       res[r7], r0 
          ldc       r1, 0x1
          add       r2, r9, 0x0
          mov       r0, r10
          mov       r3, r5
          bl        XUD_GetBuffer_int 
          ldw       r11, cp[s.LC1]
          eq        r1, r0, r11
          bt        r1, .L59 
          bu        .L58 
.L59:
.L68:
          bu        .L53 
.L58:
.L69:
          ldc       r1, 0x1

          stw       r1, dp[data] 
          ashr      r0, r0, 0x2
.L75:
          out       res[r7], r0 
          ldc       r0, 0x1
.L76:
          out       res[r7], r0 
          ldc       r1, 0x1
          add       r2, r8, 0x0
          mov       r0, r10
          mov       r3, r4
          bl        XUD_GetBuffer_int 
          ldw       r11, cp[s.LC1]
          eq        r1, r0, r11
          bt        r1, .L61 
          bu        .L60 
.L61:
.L70:
          bu        .L53 
.L60:
.L71:
          ldc       r1, 0x2

          stw       r1, dp[data] 
          ashr      r0, r0, 0x2
.L77:
          out       res[r7], r0 
          ldc       r0, 0x2
.L78:
          out       res[r7], r0 
          bu        .L54 
.L53:
          bu        .L51 
.L50:
.L72:
          ldw       r4, sp[0x2] 
          ldw       r5, sp[0x3] 
          ldw       r6, sp[0x4] 
          ldw       r7, sp[0x5] 
          ldw       r8, sp[0x6] 
          ldw       r9, sp[0x7] 
          ldw       r10, sp[0x8] 
          retsp     0x9 
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
.extern swc_audio_codec_ctl_reg_wr, "f{si}(si,si,si,p,p)"
.locl lrtime, "ss"
          .section .dp.bss,        "adw", @nobits
.cc_top lrtime.data, lrtime
          .align    4
lrtime:
          .short    0x0000
.cc_bottom lrtime.data
.extern GetData, "f{si}(0)"
.locl value, "ui"
          .align    4
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
.globread deliver, isLeft, "audio.xc:175: error: previously used here"
.globread deliver, lrtime, "audio.xc:177: error: previously used here"
.globwrite deliver, lrtime, "audio.xc:181: error: previously used here"
.globread deliver, isLeft, "audio.xc:183: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value.stat.0, "audio.xc:187: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value3.stat.2, "audio.xc:189: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value5.stat.4, "audio.xc:191: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value7.stat.6, "audio.xc:193: error: previously used here"
.globread deliver, value.stat.0, "audio.xc:197: error: previously used here"
.globread deliver, value3.stat.2, "audio.xc:198: error: previously used here"
.globread deliver, value5.stat.4, "audio.xc:199: error: previously used here"
.globread deliver, value7.stat.6, "audio.xc:200: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value2.stat.1, "audio.xc:203: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value4.stat.3, "audio.xc:205: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value6.stat.5, "audio.xc:207: error: previously used here"
.call deliver, __builtin_macs
.call deliver, GetInt
.call deliver, GetParam
.call deliver, __builtin_bitrev
.globread deliver, value8.stat.7, "audio.xc:209: error: previously used here"
.call deliver, GetData
.call deliver, GetParam
.globread deliver, value2.stat.1, "audio.xc:235: error: previously used here"
.globread deliver, value4.stat.3, "audio.xc:236: error: previously used here"
.globread deliver, value6.stat.5, "audio.xc:237: error: previously used here"
.globread deliver, value8.stat.7, "audio.xc:238: error: previously used here"
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
.linkset .LLNK28, .LLNK29 + 11
.linkset deliver.nstackwords, .LLNK28
.cc_top deliver.function,deliver
          .align    4
deliver:
          entsp     0xb 
.L94:
          stw       r4, sp[0x3] 
          stw       r5, sp[0x4] 
          stw       r6, sp[0x5] 
          stw       r7, sp[0x6] 
          stw       r8, sp[0x7] 
          stw       r9, sp[0x8] 
          stw       r10, sp[0x9] 
          stw       r0, sp[0x1] 
          mov       r9, r1
          mov       r8, r2
          stw       r3, sp[0x2] 
          ldw       r7, sp[0xc] 
          ldw       r6, sp[0xd] 
          ldw       r5, sp[0xe] 
          ldc       r4, 0x0
          bl        GetData 
          stw       r0, sp[0xa] 
.L80:
.L95:
          ldc       r0, 0x1
          bt        r0, .L81 
          bu        .L79 
.L81:
.L96:
          ldw       r0, dp[isLeft] 
          setd      res[r8], r0
          setc      res[r8], 0x19
.L107:
          in        r0, res[r8] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          getts     r0, res[r8] 
          ldw       r2, dp[lrtime] 
          sext      r2, 0x10 
          ldc       r1, 0x40
          add       r1, r2, r1
          sext      r1, 0x10 
          sub       r1, r1, r0
          sext      r1, 0x10 
          ldc       r2, 0x0
          lss       r1, r1, r2
          bt        r1, .L83 
          bu        .L82 
.L83:
.L97:
          ldw       r11, cp[s.LC1]
          mov       r0, r11
          ldw       r4, sp[0x3] 
          ldw       r5, sp[0x4] 
          ldw       r6, sp[0x5] 
          ldw       r7, sp[0x6] 
          ldw       r8, sp[0x7] 
          ldw       r9, sp[0x8] 
          ldw       r10, sp[0x9] 
          retsp     0xb 
.L82:
.L98:
          sext      r0, 0x10 

          stw       r0, dp[lrtime] 
          ldw       r0, dp[isLeft] 
          bt        r0, .L85 
          bu        .L86 
.L85:
.L99:
          mov       r0, r9
          mov       r1, r4
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x13
          bl        GetParam 
          mov       r1, r0
          ldc       r2, 0x0
          ldc       r0, 0x0
          maccs     r2, r0, r10, r1

          stw       r2, dp[value.stat.0] 
          ldw       r0, dp[value.stat.0] 
          bitrev    r0, r0

          stw       r0, dp[value.stat.0] 
          mov       r0, r9
          add       r1, r4, 0x2
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x15
          bl        GetParam 
          mov       r1, r0
          ldc       r2, 0x0
          ldc       r0, 0x0
          maccs     r2, r0, r10, r1

          stw       r2, dp[value3.stat.2] 
          ldw       r0, dp[value3.stat.2] 
          bitrev    r0, r0

          stw       r0, dp[value3.stat.2] 
          mov       r0, r9
          add       r1, r4, 0x4
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x17
          bl        GetParam 
          mov       r1, r0
          ldc       r2, 0x0
          ldc       r0, 0x0
          maccs     r2, r0, r10, r1

          stw       r2, dp[value5.stat.4] 
          ldw       r0, dp[value5.stat.4] 
          bitrev    r0, r0

          stw       r0, dp[value5.stat.4] 
          mov       r0, r9
          add       r1, r4, 0x6
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x19
          bl        GetParam 
          mov       r1, r0
          ldc       r2, 0x0
          ldc       r0, 0x0
          maccs     r2, r0, r10, r1

          stw       r2, dp[value7.stat.6] 
          ldw       r0, dp[value7.stat.6] 
          bitrev    r0, r0

          stw       r0, dp[value7.stat.6] 
          ldw       r1, dp[value.stat.0] 
          ldw       r0, sp[0x2] 
.L108:
          out       res[r0], r1 
          ldw       r0, dp[value3.stat.2] 
.L109:
          out       res[r7], r0 
          ldw       r0, dp[value5.stat.4] 
.L110:
          out       res[r6], r0 
          ldw       r0, dp[value7.stat.6] 
.L111:
          out       res[r5], r0 
          mov       r0, r9
          add       r1, r4, 0x1
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x14
          bl        GetParam 
          ldc       r2, 0x0
          ldc       r1, 0x0
          maccs     r2, r1, r10, r0

          stw       r2, dp[value2.stat.1] 
          mov       r0, r1
          ldw       r0, dp[value2.stat.1] 
          bitrev    r0, r0

          stw       r0, dp[value2.stat.1] 
          mov       r0, r9
          add       r1, r4, 0x3
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x16
          bl        GetParam 
          ldc       r2, 0x0
          ldc       r1, 0x0
          maccs     r2, r1, r10, r0

          stw       r2, dp[value4.stat.3] 
          mov       r0, r1
          ldw       r0, dp[value4.stat.3] 
          bitrev    r0, r0

          stw       r0, dp[value4.stat.3] 
          mov       r0, r9
          add       r1, r4, 0x5
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x18
          bl        GetParam 
          ldc       r2, 0x0
          ldc       r1, 0x0
          maccs     r2, r1, r10, r0

          stw       r2, dp[value6.stat.5] 
          mov       r0, r1
          ldw       r0, dp[value6.stat.5] 
          bitrev    r0, r0

          stw       r0, dp[value6.stat.5] 
          mov       r0, r9
          add       r1, r4, 0x7
          bl        GetInt 
          mov       r10, r0
          ldc       r0, 0x1a
          bl        GetParam 
          mov       r1, r0
          ldc       r2, 0x0
          ldc       r0, 0x0
          maccs     r2, r0, r10, r1

          stw       r2, dp[value8.stat.7] 
          ldw       r0, dp[value8.stat.7] 
          bitrev    r0, r0

          stw       r0, dp[value8.stat.7] 
          add       r1, r4, 0x8
          ldw       r0, sp[0x1] 
          lsu       r0, r1, r0
          bt        r0, .L88 
          bu        .L89 
.L88:
.L100:
          add       r4, r4, 0x8
          bu        .L87 
.L89:
.L101:
          bl        GetData 
          ldw       r1, sp[0xa] 
          eq        r0, r0, r1
          bf        r0, .L91 
          bu        .L90 
.L91:
.L102:
          ldc       r0, 0x1
          ldw       r4, sp[0x3] 
          ldw       r5, sp[0x4] 
          ldw       r6, sp[0x5] 
          ldw       r7, sp[0x6] 
          ldw       r8, sp[0x7] 
          ldw       r9, sp[0x8] 
          ldw       r10, sp[0x9] 
          retsp     0xb 
.L90:
.L103:
          ldc       r0, 0x9
          bl        GetParam 
          ldw       r1, sp[0xf] 
          eq        r0, r0, r1
          bf        r0, .L93 
          bu        .L92 
.L93:
.L104:
          ldw       r11, cp[s.LC1]
          mov       r0, r11
          ldw       r4, sp[0x3] 
          ldw       r5, sp[0x4] 
          ldw       r6, sp[0x5] 
          ldw       r7, sp[0x6] 
          ldw       r8, sp[0x7] 
          ldw       r9, sp[0x8] 
          ldw       r10, sp[0x9] 
          retsp     0xb 
.L92:
.L87:
          bu        .L84 
.L86:
.L105:
          ldw       r1, dp[value2.stat.1] 
          ldw       r0, sp[0x2] 
.L112:
          out       res[r0], r1 
          ldw       r0, dp[value4.stat.3] 
.L113:
          out       res[r7], r0 
          ldw       r0, dp[value6.stat.5] 
.L114:
          out       res[r6], r0 
          ldw       r0, dp[value8.stat.7] 
.L115:
          out       res[r5], r0 
.L84:
          bu        .L80 
.L79:
.L106:
          ldw       r4, sp[0x3] 
          ldw       r5, sp[0x4] 
          ldw       r6, sp[0x5] 
          ldw       r7, sp[0x6] 
          ldw       r8, sp[0x7] 
          ldw       r9, sp[0x8] 
          ldw       r10, sp[0x9] 
          retsp     0xb 
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
.extern audio_init, "f{0}(o:p,p,p,p,bo:p:32,bo:p:32,bo:p:32,bo:p:32,i:p,ck)"
.call physical, GetParam
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferA, "audio.xc:263: error: `physical' makes alias of global 'bufferA' in call to `GetBufferAddress'"
.globwrite physical, bufferA, "audio.xc:263: error: previously used here"
.globwrite physical, bufferA, "audio.xc:263: error: previously used here"
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferB, "audio.xc:264: error: `physical' makes alias of global 'bufferB' in call to `GetBufferAddress'"
.globwrite physical, bufferB, "audio.xc:264: error: previously used here"
.globwrite physical, bufferB, "audio.xc:264: error: previously used here"
.call physical, GetBufferAddress
.globpassesref GetBufferAddress, bufferC, "audio.xc:265: error: `physical' makes alias of global 'bufferC' in call to `GetBufferAddress'"
.globwrite physical, bufferC, "audio.xc:265: error: previously used here"
.globwrite physical, bufferC, "audio.xc:265: error: previously used here"
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
.globwrite physical, lrtime, "audio.xc:156: error: previously used here"
.globwrite physical, lrtime, "audio.xc:410: error: previously used here"
.globread physical, lrtime, "audio.xc:411: error: previously used here"
.globread physical, lrtime, "audio.xc:412: error: previously used here"
.globread physical, lrtime, "audio.xc:413: error: previously used here"
.globread physical, lrtime, "audio.xc:414: error: previously used here"
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
.linkset .LLNK165, .LLNK166 $M swc_audio_codec_ctl_reg_wr.nstackwords
.linkset .LLNK164, .LLNK165 $M swc_audio_codec_ctl_reg_wr.nstackwords
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
.linkset .LLNK145, .LLNK146 $M XUD_Error.nstackwords
.linkset .LLNK144, .LLNK145 $M GetParam.nstackwords
.linkset .LLNK143, .LLNK144 $M deliver.nstackwords
.linkset .LLNK142, .LLNK143 $M .LLNK143
.linkset .LLNK141, .LLNK142 + 20
.linkset physical.nstackwords, .LLNK141
.cc_top physical.function,physical
          .align    4
physical:
          entsp     0x14 
.L144:
          stw       r4, sp[0x6] 
          stw       r5, sp[0x7] 
          stw       r6, sp[0x8] 
          stw       r7, sp[0x9] 
          stw       r8, sp[0xa] 
          stw       r9, sp[0xb] 
          stw       r10, sp[0xc] 
          mov       r9, r0
          stw       r1, sp[0x5] 
          mov       r8, r3
          ldw       r7, sp[0x15] 
          ldw       r6, sp[0x16] 
          ldw       r0, sp[0x1b] 
          getr      r5, 0x1
          ldc       r0, 0x0
          ldc       r0, 0x9
          bl        GetParam 
          stw       r0, sp[0xd] 
          ldc       r10, 0x1
          ldaw      r0, dp[bufferA] 
          ldc       r1, 0x300
          bl        GetBufferAddress 
          ldaw      r1, sp[0xf] 
          stw       r0, r1[0x0] 
          ldw       r0, r1[0x0] 
          ldaw      r0, dp[bufferB] 
          ldc       r1, 0x300
          bl        GetBufferAddress 
          ldaw      r1, sp[0xf] 
          stw       r0, r1[0x1] 
          ldw       r0, r1[0x1] 
          ldaw      r0, dp[bufferC] 
          ldc       r1, 0x300
          bl        GetBufferAddress 
          ldaw      r1, sp[0xf] 
          stw       r0, r1[0x2] 
          ldw       r0, r1[0x2] 
          ldc       r1, 0x0
          ldw       r0, sp[0x5] 
.L165:
          out       res[r0], r1 
          setc      res[r5], 0x1
.L166:
          in        r0, res[r5] 
          ldw       r11, cp[s.LC4]
          .section .cp.const4,     "acM", @progbits, 4
s.LC4:
          .align    4
          .int      0x000186a0
          .text
          add       r0, r0, r11
          setd      res[r5], r0
          setc      res[r5], 0x9
.L167:
          in        r0, res[r5] 
          ldc       r1, 0x1
          ldw       r0, sp[0x5] 
.L168:
          out       res[r0], r1 
          ldc       r2, 0x11
          ldc       r0, 0x90
          ldc       r1, 0x4
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r2, 0x1c
          ldc       r0, 0x90
          ldc       r1, 0x5
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x3
          ldc       r2, 0x1
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x5
          ldc       r2, 0x1
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x16
          ldc       r2, 0x10
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x17
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
.L117:
.L145:
          ldc       r0, 0x1
          bt        r0, .L118 
          bu        .L116 
.L118:
.L146:
          ldc       r0, 0x9
          bl        GetParam 
          stw       r0, sp[0xd] 
          ldc       r0, 0x40
          stw       r0, sp[0x12] 
          ldw       r1, sp[0xd] 
          bu        .L119 
.L121:
.L147:
          ldc       r0, 0x9c
          ldc       r1, 0x6
          ldc       r2, 0x6e
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x7
          ldc       r2, 0x40
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r2, 0x8
          ldc       r0, 0x90
          ldc       r1, 0x3
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L120 
.L122:
.L148:
          ldc       r0, 0x9c
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r2, 0x8
          ldc       r0, 0x90
          ldc       r1, 0x3
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L120 
.L123:
.L149:
          ldc       r0, 0x9c
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r2, 0x58
          ldc       r0, 0x90
          ldc       r1, 0x3
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L120 
.L124:
.L150:
          ldc       r0, 0x9c
          ldc       r1, 0x6
          ldc       r2, 0x78
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x7
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x8
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r0, 0x9c
          ldc       r1, 0x9
          ldc       r2, 0x0
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          ldc       r2, 0xa8
          ldc       r0, 0x90
          ldc       r1, 0x3
          mov       r3, r8
          stw       r7, sp[0x1] 
          bl        swc_audio_codec_ctl_reg_wr 
          bu        .L120 
.L125:
.L151:
          ldaw      r11, cp[s.LC3] 
          mov       r0, r11
          ldc       r1, 0x12
          bl        XUD_Error 
          bu        .L120 
.L119:
          ldc       r0, 0xac44
          eq        r0, r1, r0
          bt        r0, .L121 
          ldc       r0, 0xbb80
          eq        r0, r1, r0
          bt        r0, .L122 
          ldw       r11, cp[s.LC5]
          .section .cp.const4,     "acM", @progbits, 4
s.LC5:
          .align    4
          .int      0x00017700
          .text
          eq        r0, r1, r11
          bt        r0, .L123 
          ldw       r11, cp[s.LC6]
          .section .cp.const4,     "acM", @progbits, 4
s.LC6:
          .align    4
          .int      0x0002ee00
          .text
          eq        r0, r1, r11
          bt        r0, .L124 
          bu        .L125 
.L120:
.L152:
          ldc       r0, 0x1
          stw       r0, sp[0x13] 
          ldc       r10, 0x1
.L127:
.L153:
          bt        r10, .L128 
          bu        .L126 
.L128:
.L154:
          clre      
          ldap      r11, .L132
          setv      res[r9], r11
          eeu       res[r9]
.xtabranch physical, .L132
          setsr     0x1
          clrsr     0x1
.L155:
          ldc       r0, 0x9
          bl        GetParam 
          ldw       r1, sp[0xd] 
          eq        r0, r1, r0
          bf        r0, .L136 
          bu        .L135 
.L136:
.L156:
          ldc       r0, 0x0
          stw       r0, sp[0x13] 
          ldc       r10, 0x0
.L135:
          bu        .L131 
.L132:
.L157:
.L169:
          in        r0, res[r9] 
          stw       r0, sp[0xe] 
.L170:
          in        r4, res[r9] 
          ldaw      r1, sp[0xf] 
          ldc       r0, 0x3
          lsu       r0, r4, r0
          ecallf    r0
          ldw       r4, r1[r4] 
          ldc       r10, 0x0
          bu        .L131 
.L129:
.L131:
          bu        .L127 
.L126:
.L158:
          ldw       r0, sp[0x13] 
          bt        r0, .L138 
          bu        .L137 
.L138:
.L159:
          ldw       r0, sp[0x17] 
          setc      res[r0], 0x17
          ldw       r0, sp[0x18] 
          setc      res[r0], 0x17
          ldw       r0, sp[0x19] 
          setc      res[r0], 0x17
          ldw       r0, sp[0x1a] 
          setc      res[r0], 0x17
          ldc       r0, 0x1
          setd      res[r6], r0
          setc      res[r6], 0x19
.L171:
          in        r0, res[r6] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          ldc       r0, 0x0
          setd      res[r6], r0
          setc      res[r6], 0x19
.L172:
          in        r0, res[r6] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          ldc       r0, 0x1
          setd      res[r6], r0
          setc      res[r6], 0x19
.L173:
          in        r0, res[r6] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          ldc       r0, 0x0
          setd      res[r6], r0
          setc      res[r6], 0x19
.L174:
          in        r0, res[r6] 

          stw       r0, dp[isLeft] 
          ldw       r0, dp[isLeft] 

          stw       r0, dp[isLeft] 
          getts     r0, res[r6] 

          stw       r0, dp[lrtime] 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
          ldw       r0, sp[0x12] 
          add       r0, r1, r0
          sext      r0, 0x10 

          stw       r0, dp[lrtime] 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
          ldw       r0, sp[0x17] 
          setpt     res[r0], r1
          ldc       r1, 0x0
          ldw       r0, sp[0x17] 
.L175:
          out       res[r0], r1 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
          ldw       r0, sp[0x18] 
          setpt     res[r0], r1
          ldc       r1, 0x0
          ldw       r0, sp[0x18] 
.L176:
          out       res[r0], r1 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
          ldw       r0, sp[0x19] 
          setpt     res[r0], r1
          ldc       r1, 0x0
          ldw       r0, sp[0x19] 
.L177:
          out       res[r0], r1 
          ldw       r1, dp[lrtime] 
          sext      r1, 0x10 
          ldw       r0, sp[0x1a] 
          setpt     res[r0], r1
          ldc       r1, 0x0
          ldw       r0, sp[0x1a] 
.L178:
          out       res[r0], r1 
.L140:
.L160:
          ldc       r0, 0x1
          bt        r0, .L141 
          bu        .L139 
.L141:
.L161:
          ldw       r0, sp[0xe] 
          ldw       r10, sp[0xd] 
          mov       r1, r4
          mov       r2, r6
          ldw       r3, sp[0x17] 
          ldw       r11, sp[0x18] 
          stw       r11, sp[0x1] 
          ldw       r11, sp[0x19] 
          stw       r11, sp[0x2] 
          ldw       r11, sp[0x1a] 
          stw       r11, sp[0x3] 
          stw       r10, sp[0x4] 
          bl        deliver 
          ldw       r11, cp[s.LC1]
          eq        r0, r0, r11
          bt        r0, .L143 
          bu        .L142 
.L143:
.L162:
          bu        .L139 
.L142:
.L163:
.L179:
          in        r0, res[r9] 
          stw       r0, sp[0xe] 
.L180:
          in        r4, res[r9] 
          ldaw      r1, sp[0xf] 
          ldc       r0, 0x3
          lsu       r0, r4, r0
          ecallf    r0
          ldw       r4, r1[r4] 
          bu        .L140 
.L139:
.L137:
          bu        .L117 
.L116:
.L164:
          freer     res[r5]
          ldw       r4, sp[0x6] 
          ldw       r5, sp[0x7] 
          ldw       r6, sp[0x8] 
          ldw       r7, sp[0x9] 
          ldw       r8, sp[0xa] 
          ldw       r9, sp[0xb] 
          ldw       r10, sp[0xc] 
          retsp     0x14 
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
.linkset .LLNK198, .LLNK199 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK197, .LLNK198 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK196, .LLNK197 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK195, .LLNK196 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK194, .LLNK195 $M swc_audio_codec_ctl_reg_wr.maxchanends
.linkset .LLNK193, .LLNK194 $M deliver.maxchanends
.linkset .LLNK192, .LLNK193 $M GetParam.maxchanends
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
.linkset .LLNK176, .LLNK177 $M XUD_Error.maxchanends
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
.linkset .LLNK254, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK231, .LLNK232 $M .LLNK254
.linkset .LLNK255, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK230, .LLNK231 $M .LLNK255
.linkset .LLNK256, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK229, .LLNK230 $M .LLNK256
.linkset .LLNK257, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK228, .LLNK229 $M .LLNK257
.linkset .LLNK258, 1 + swc_audio_codec_ctl_reg_wr.maxtimers
.linkset .LLNK227, .LLNK228 $M .LLNK258
.linkset .LLNK259, 1 + deliver.maxtimers
.linkset .LLNK226, .LLNK227 $M .LLNK259
.linkset .LLNK260, 1 + GetParam.maxtimers
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
.linkset .LLNK276, 1 + XUD_Error.maxtimers
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
.linkset .LLNK334, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK333, 1 + .LLNK334
.linkset .LLNK299, .LLNK300 $M .LLNK333
.linkset .LLNK336, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK335, 1 + .LLNK336
.linkset .LLNK298, .LLNK299 $M .LLNK335
.linkset .LLNK338, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK337, 1 + .LLNK338
.linkset .LLNK297, .LLNK298 $M .LLNK337
.linkset .LLNK340, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK339, 1 + .LLNK340
.linkset .LLNK296, .LLNK297 $M .LLNK339
.linkset .LLNK342, swc_audio_codec_ctl_reg_wr.maxthreads - 1
.linkset .LLNK341, 1 + .LLNK342
.linkset .LLNK295, .LLNK296 $M .LLNK341
.linkset .LLNK344, deliver.maxthreads - 1
.linkset .LLNK343, 1 + .LLNK344
.linkset .LLNK294, .LLNK295 $M .LLNK343
.linkset .LLNK346, GetParam.maxthreads - 1
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
.linkset .LLNK378, XUD_Error.maxthreads - 1
.linkset .LLNK377, 1 + .LLNK378
.linkset .LLNK277, .LLNK278 $M .LLNK377
.linkset physical.maxthreads, .LLNK277
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
.globpassesref readData, bufferA, "audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferA' in call to `readData'"
.globwrite thread.anon.0, bufferA, "audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferA, "audio.xc:467: error: previously used here"
.globpassesref readData, bufferB, "audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferB' in call to `readData'"
.globwrite thread.anon.0, bufferB, "audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferB, "audio.xc:467: error: previously used here"
.globpassesref readData, bufferC, "audio.xc:467: error: `thread.anon.0' makes alias of global 'bufferC' in call to `readData'"
.globwrite thread.anon.0, bufferC, "audio.xc:467: error: previously used here"
.globwrite thread.anon.0, bufferC, "audio.xc:467: error: previously used here"
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
          .align    4
XUD_Endpoint1:
          entsp     0x13 
.L184:
          stw       r4, sp[0x9] 
          stw       r5, sp[0xa] 
          stw       r6, sp[0xb] 
          stw       r7, sp[0xc] 
          stw       r8, sp[0xd] 
          stw       r9, sp[0xe] 
          stw       r10, sp[0xf] 
          stw       r0, sp[0x5] 
          stw       r1, sp[0x6] 
          stw       r2, sp[0x7] 
          stw       r3, sp[0x8] 
          ldw       r10, sp[0x18] 
          ldw       r9, sp[0x19] 
          ldw       r8, sp[0x1a] 
          getr      r7, 0x2
          getr      r6, 0x2
          setd      res[r7], r6
          setd      res[r6], r7
          getr      r5, 0x1
          ldc       r0, 0x9
          bl        GetParam 
          ldc       r1, 0x1
          ldw       r0, sp[0x8] 
.L188:
          out       res[r0], r1 
          ldc       r1, 0x1
          ldw       r0, sp[0x14] 
.L189:
          out       res[r0], r1 
          mov       r0, r8
          ldw       r1, sp[0x7] 
          bl        configure_clock_src 
          ldc       r2, 0x0
          ldw       r0, sp[0x16] 
          mov       r1, r8
          bl        configure_out_port_no_ready 
          ldc       r2, 0x0
          ldw       r0, sp[0x17] 
          mov       r1, r8
          bl        configure_out_port_no_ready 
          ldc       r2, 0x0
          mov       r0, r10
          mov       r1, r8
          bl        configure_out_port_no_ready 
          ldc       r2, 0x0
          mov       r0, r9
          mov       r1, r8
          bl        configure_out_port_no_ready 
          ldc       r2, 0x0
          ldw       r0, sp[0x15] 
          mov       r1, r8
          bl        configure_out_port_no_ready 
          setc      res[r8], 0xf
          getr      r4, 0x3
          getst     r0, res[r4]
          ldap      r11, .L182
          ldaw      r11, sp[0x0] 
          init      t[r0]:sp, r11
          ldap      r11, .L182
          init      t[r0]:pc, r11
          set       t[r0]:r7, r10
          set       t[r0]:r6, r9
          set       t[r0]:r5, r8
          set       t[r0]:r4, r6
          msync     res[r4]
.L183:
.L185:
          ldaw      r1, dp[bufferA] 
          ldaw      r2, dp[bufferB] 
          ldaw      r3, dp[bufferC] 
          ldw       r0, sp[0x5] 
          stw       r7, sp[0x1] 
          ldc       r8, 0x300
          stw       r8, sp[0x2] 
          ldc       r8, 0x300
          stw       r8, sp[0x3] 
          ldc       r8, 0x300
          stw       r8, sp[0x4] 
          bl        readData 
          msync     res[r4]
          mjoin     res[r4]
.L181:
.L186:
          freer     res[r4]
          freer     res[r7]
          freer     res[r6]
          freer     res[r5]
          ldw       r4, sp[0x9] 
          ldw       r5, sp[0xa] 
          ldw       r6, sp[0xb] 
          ldw       r7, sp[0xc] 
          ldw       r8, sp[0xd] 
          ldw       r9, sp[0xe] 
          ldw       r10, sp[0xf] 
          retsp     0x13 
.L187:
.LDBG12:                             # line (469)
.L182:
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
          mov       r0, r4
          stw       r11, sp[0x1] 
          stw       r10, sp[0x2] 
          stw       r9, sp[0x3] 
          stw       r8, sp[0x4] 
          stw       r7, sp[0x5] 
          stw       r6, sp[0x6] 
          stw       r5, sp[0x7] 
          bl        physical 
          ssync     
          ssync     
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
.asciiz ""
main.parinfo.debugstring1:
.asciiz ""
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
.call __main_xm_4, doClock
.globpassesref doClock, p_aud_clk, "audio.xc:547: error: `__main_xm_4' makes alias of global 'p_aud_clk' in call to `doClock'"
.globwrite __main_xm_4, p_aud_clk, "audio.xc:547: error: previously used here"
.globread __main_xm_4, p_aud_clk, "audio.xc:547: error: previously used here"
          .text
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
          .align    4
__main_xm_4:
          entsp     0x1 
.L190:
          ldw       r1, dp[p_aud_clk] 
          bl        doClock 
          retsp     0x1 
.cc_bottom __main_xm_4.function
.linkset __main_xm_4.maxchanends, doClock.maxchanends
.linkset __main_xm_4.maxtimers, doClock.maxtimers
.linkset .LLNK469, doClock.maxthreads - 1
.linkset .LLNK468, 1 + .LLNK469
.linkset .LLNK467, 1 $M .LLNK468
.linkset __main_xm_4.maxthreads, .LLNK467
.call __main_xm_3, XUD_Endpoint1
.globpassesref XUD_Endpoint1, p_aud_rst, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_rst' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_rst, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_rst, "audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_sclk, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_sclk' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_sclk, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_sclk, "audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_scl, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_scl' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_scl, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_scl, "audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_sda, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_sda' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_sda, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_sda, "audio.xc:544: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_lrclk, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_lrclk' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_lrclk, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_lrclk, "audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac1, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac1' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac1, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac1, "audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac2, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac2' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac2, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac2, "audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac3, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac3' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac3, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac3, "audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, p_aud_dac4, "audio.xc:544: error: `__main_xm_3' makes alias of global 'p_aud_dac4' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, p_aud_dac4, "audio.xc:544: error: previously used here"
.globread __main_xm_3, p_aud_dac4, "audio.xc:545: error: previously used here"
.globpassesref XUD_Endpoint1, clk_aud_bit, "audio.xc:544: error: `__main_xm_3' makes alias of global 'clk_aud_bit' in call to `XUD_Endpoint1'"
.globwrite __main_xm_3, clk_aud_bit, "audio.xc:544: error: previously used here"
.globread __main_xm_3, clk_aud_bit, "audio.xc:545: error: previously used here"
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
          .align    4
__main_xm_3:
          entsp     0x8 
.L191:
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
          bl        XUD_Endpoint1 
          retsp     0x8 
.cc_bottom __main_xm_3.function
.linkset __main_xm_3.maxchanends, XUD_Endpoint1.maxchanends
.linkset __main_xm_3.maxtimers, XUD_Endpoint1.maxtimers
.linkset .LLNK474, XUD_Endpoint1.maxthreads - 1
.linkset .LLNK473, 1 + .LLNK474
.linkset .LLNK472, 1 $M .LLNK473
.linkset __main_xm_3.maxthreads, .LLNK472
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
          .align    4
__main_xm_2:
          entsp     0x1 
.L192:
          mov       r2, r0
          mov       r0, r1
          mov       r1, r2
          bl        ep0_thread 
          retsp     0x1 
.cc_bottom __main_xm_2.function
.linkset __main_xm_2.maxchanends, ep0_thread.maxchanends
.linkset __main_xm_2.maxtimers, ep0_thread.maxtimers
.linkset .LLNK479, ep0_thread.maxthreads - 1
.linkset .LLNK478, 1 + .LLNK479
.linkset .LLNK477, 1 $M .LLNK478
.linkset __main_xm_2.maxthreads, .LLNK477
.call __main_xm_1, XUD_Manager
.globpassesref XUD_Manager, p_test, "audio.xc:539: error: `__main_xm_1' makes alias of global 'p_test' in call to `XUD_Manager'"
.globwrite __main_xm_1, p_test, "audio.xc:539: error: previously used here"
.globread __main_xm_1, p_test, "audio.xc:539: error: previously used here"
.globpassesref XUD_Manager, p_test2, "audio.xc:539: error: `__main_xm_1' makes alias of global 'p_test2' in call to `XUD_Manager'"
.globwrite __main_xm_1, p_test2, "audio.xc:539: error: previously used here"
.globread __main_xm_1, p_test2, "audio.xc:539: error: previously used here"
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
          .align    4
__main_xm_1:
          entsp     0x5 
.L193:
          stw       r4, sp[0x4] 
          mov       r4, r0
          mov       r0, r2
          mov       r11, r3
          ldw       r2, sp[0x6] 
          mov       r3, r4
          stw       r11, sp[0x1] 
          ldw       r4, dp[p_test] 
          stw       r4, sp[0x2] 
          ldw       r4, dp[p_test2] 
          stw       r4, sp[0x3] 
          bl        XUD_Manager 
          ldw       r4, sp[0x4] 
          retsp     0x5 
.cc_bottom __main_xm_1.function
.linkset __main_xm_1.maxchanends, XUD_Manager.maxchanends
.linkset __main_xm_1.maxtimers, XUD_Manager.maxtimers
.linkset .LLNK484, XUD_Manager.maxthreads - 1
.linkset .LLNK483, 1 + .LLNK484
.linkset .LLNK482, 1 $M .LLNK483
.linkset __main_xm_1.maxthreads, .LLNK482
.call __main_xm_0, XUD_Endpoint3
.globpassesref XUD_Endpoint3, p_but, "audio.xc:537: error: `__main_xm_0' makes alias of global 'p_but' in call to `XUD_Endpoint3'"
.globwrite __main_xm_0, p_but, "audio.xc:537: error: previously used here"
.globread __main_xm_0, p_but, "audio.xc:537: error: previously used here"
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
          .align    4
__main_xm_0:
          entsp     0x1 
.L194:
          ldw       r1, dp[p_but] 
          bl        XUD_Endpoint3 
          retsp     0x1 
.cc_bottom __main_xm_0.function
.linkset __main_xm_0.maxchanends, XUD_Endpoint3.maxchanends
.linkset __main_xm_0.maxtimers, XUD_Endpoint3.maxtimers
.linkset .LLNK489, XUD_Endpoint3.maxthreads - 1
.linkset .LLNK488, 1 + .LLNK489
.linkset .LLNK487, 1 $M .LLNK488
.linkset __main_xm_0.maxthreads, .LLNK487
.par GetInt, GetParam, "audio.xc:186: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:188: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:190: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:192: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:202: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:204: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:206: error: use of `%s' violates variable disjointness rules"
.par GetInt, GetParam, "audio.xc:208: error: use of `%s' violates variable disjointness rules"
.par readData, physical, "audio.xc:465: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, XUD_Manager, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, ep0_thread, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, XUD_Endpoint1, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Manager, doClock, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, ep0_thread, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, XUD_Endpoint1, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint3, doClock, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par ep0_thread, doClock, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par XUD_Endpoint1, doClock, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
.par ep0_thread, XUD_Endpoint1, "audio.xc:535: error: use of `%s' violates variable disjointness rules"
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
          .section .xtaendpointtable,       "", @progbits
.L195:
          .int      .L196-.L195
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb/tests/audio/src"
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "audio.xc"
          .int      0x000001c1
          .long    .L189
.cc_bottom XUD_Endpoint1.function
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "audio.xc"
          .int      0x000001c0
          .long    .L188
.cc_bottom XUD_Endpoint1.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x000001aa
          .long    .L180
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x000001a9
          .long    .L179
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000019e
          .long    .L178
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000019d
          .long    .L177
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000019c
          .long    .L176
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000019b
          .long    .L175
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x00000198
          .long    .L174
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x00000196
          .long    .L173
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x00000195
          .long    .L172
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x00000194
          .long    .L171
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000017d
          .long    .L170
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000017c
          .long    .L169
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000010f
          .long    .L168
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000010e
          .long    .L167
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000010d
          .long    .L166
.cc_bottom physical.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x0000010c
          .long    .L165
.cc_bottom physical.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000ee
          .long    .L115
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000ed
          .long    .L114
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000ec
          .long    .L113
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000eb
          .long    .L112
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000c8
          .long    .L111
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000c7
          .long    .L110
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000c6
          .long    .L109
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000c5
          .long    .L108
.cc_bottom deliver.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000af
          .long    .L107
.cc_bottom deliver.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x0000008a
          .long    .L78
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x00000089
          .long    .L77
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x00000084
          .long    .L76
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x00000083
          .long    .L75
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x0000007e
          .long    .L74
.cc_bottom readData.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x0000007d
          .long    .L73
.cc_bottom readData.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x00000065
          .long    .L49
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x00000060
          .long    .L48
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x0000005e
          .long    .L47
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x00000059
          .long    .L46
.cc_bottom doClock.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x00000058
          .long    .L45
.cc_bottom doClock.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "audio.xc"
          .int      0x0000004a
          .long    .L29
.cc_bottom dummyEP.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "audio.xc"
          .int      0x00000049
          .long    .L28
.cc_bottom dummyEP.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "audio.xc"
          .int      0x00000048
          .long    .L27
.cc_bottom dummyEP.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "audio.xc"
          .int      0x00000035
          .long    .L19
.cc_bottom XUD_Endpoint3.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "audio.xc"
          .int      0x0000002e
          .long    .L18
.cc_bottom XUD_Endpoint3.function
.L196:
          .section .xtalabeltable,       "", @progbits
.L197:
          .int      .L198-.L197
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb/tests/audio/src"
.cc_top __main_xm_0.function, __main_xm_0
          .asciiz  "audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L194
.cc_bottom __main_xm_0.function
.cc_top __main_xm_1.function, __main_xm_1
          .asciiz  "audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L193
.cc_bottom __main_xm_1.function
.cc_top __main_xm_2.function, __main_xm_2
          .asciiz  "audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L192
.cc_bottom __main_xm_2.function
.cc_top __main_xm_3.function, __main_xm_3
          .asciiz  "audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L191
.cc_bottom __main_xm_3.function
.cc_top __main_xm_4.function, __main_xm_4
          .asciiz  "audio.xc"
          .int      0x0000020e
          .int      0x00000228
          .long    .L190
.cc_bottom __main_xm_4.function
.cc_top XUD_Endpoint1.function, XUD_Endpoint1
          .asciiz  "audio.xc"
          .int      0x000001d5
          .int      0x000001d4
          .long    .L187
          .asciiz  "audio.xc"
          .int      0x000001d7
          .int      0x000001d7
          .long    .L186
          .asciiz  "audio.xc"
          .int      0x000001d3
          .int      0x000001d3
          .long    .L185
          .asciiz  "audio.xc"
          .int      0x000001b3
          .int      0x000001d1
          .long    .L184
.cc_bottom XUD_Endpoint1.function
.cc_top physical.function, physical
          .asciiz  "audio.xc"
          .int      0x000001af
          .int      0x000001af
          .long    .L164
          .asciiz  "audio.xc"
          .int      0x000001a9
          .int      0x000001ab
          .long    .L163
          .asciiz  "audio.xc"
          .int      0x000001a7
          .int      0x000001a7
          .long    .L162
          .asciiz  "audio.xc"
          .int      0x000001a6
          .int      0x000001a6
          .long    .L161
          .asciiz  "audio.xc"
          .int      0x000001a4
          .int      0x000001a4
          .long    .L160
          .asciiz  "audio.xc"
          .int      0x0000018f
          .int      0x0000019e
          .long    .L159
          .asciiz  "audio.xc"
          .int      0x0000018d
          .int      0x0000018d
          .long    .L158
          .asciiz  "audio.xc"
          .int      0x0000017c
          .int      0x0000017f
          .long    .L157
          .asciiz  "audio.xc"
          .int      0x00000185
          .int      0x00000186
          .long    .L156
          .asciiz  "audio.xc"
          .int      0x00000183
          .int      0x00000183
          .long    .L155
          .asciiz  "audio.xc"
          .int      0x0000017a
          .int      0x0000017a
          .long    .L154
          .asciiz  "audio.xc"
          .int      0x00000178
          .int      0x00000178
          .long    .L153
          .asciiz  "audio.xc"
          .int      0x00000174
          .int      0x00000175
          .long    .L152
          .asciiz  "audio.xc"
          .int      0x0000016f
          .int      0x0000016f
          .long    .L151
          .asciiz  "audio.xc"
          .int      0x00000163
          .int      0x0000016a
          .long    .L150
          .asciiz  "audio.xc"
          .int      0x00000159
          .int      0x0000015f
          .long    .L149
          .asciiz  "audio.xc"
          .int      0x0000014f
          .int      0x00000155
          .long    .L148
          .asciiz  "audio.xc"
          .int      0x00000143
          .int      0x00000149
          .long    .L147
          .asciiz  "audio.xc"
          .int      0x0000013d
          .int      0x0000013f
          .long    .L146
          .asciiz  "audio.xc"
          .int      0x00000135
          .int      0x00000135
          .long    .L145
          .asciiz  "audio.xc"
          .int      0x000000fa
          .int      0x00000131
          .long    .L144
.cc_bottom physical.function
.cc_top deliver.function, deliver
          .asciiz  "audio.xc"
          .int      0x000000f2
          .int      0x000000f2
          .long    .L106
          .asciiz  "audio.xc"
          .int      0x000000eb
          .int      0x000000ee
          .long    .L105
          .asciiz  "audio.xc"
          .int      0x000000e3
          .int      0x000000e3
          .long    .L104
          .asciiz  "audio.xc"
          .int      0x000000e2
          .int      0x000000e2
          .long    .L103
          .asciiz  "audio.xc"
          .int      0x000000e1
          .int      0x000000e1
          .long    .L102
          .asciiz  "audio.xc"
          .int      0x000000e0
          .int      0x000000e0
          .long    .L101
          .asciiz  "audio.xc"
          .int      0x000000db
          .int      0x000000db
          .long    .L100
          .asciiz  "audio.xc"
          .int      0x000000ba
          .int      0x000000d9
          .long    .L99
          .asciiz  "audio.xc"
          .int      0x000000b5
          .int      0x000000b7
          .long    .L98
          .asciiz  "audio.xc"
          .int      0x000000b3
          .int      0x000000b3
          .long    .L97
          .asciiz  "audio.xc"
          .int      0x000000af
          .int      0x000000b1
          .long    .L96
          .asciiz  "audio.xc"
          .int      0x000000ad
          .int      0x000000ad
          .long    .L95
          .asciiz  "audio.xc"
          .int      0x000000a3
          .int      0x000000a9
          .long    .L94
.cc_bottom deliver.function
.cc_top readData.function, readData
          .asciiz  "audio.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L72
          .asciiz  "audio.xc"
          .int      0x00000088
          .int      0x0000008a
          .long    .L71
          .asciiz  "audio.xc"
          .int      0x00000087
          .int      0x00000087
          .long    .L70
          .asciiz  "audio.xc"
          .int      0x00000082
          .int      0x00000087
          .long    .L69
          .asciiz  "audio.xc"
          .int      0x00000081
          .int      0x00000081
          .long    .L68
          .asciiz  "audio.xc"
          .int      0x0000007c
          .int      0x00000081
          .long    .L67
          .asciiz  "audio.xc"
          .int      0x0000007b
          .int      0x0000007b
          .long    .L66
          .asciiz  "audio.xc"
          .int      0x0000007a
          .int      0x0000007b
          .long    .L65
          .asciiz  "audio.xc"
          .int      0x00000077
          .int      0x00000077
          .long    .L64
          .asciiz  "audio.xc"
          .int      0x00000076
          .int      0x00000076
          .long    .L63
          .asciiz  "audio.xc"
          .int      0x00000074
          .int      0x00000074
          .long    .L62
.cc_bottom readData.function
.cc_top doClock.function, doClock
          .asciiz  "audio.xc"
          .int      0x0000006b
          .int      0x0000006b
          .long    .L44
          .asciiz  "audio.xc"
          .int      0x00000064
          .int      0x00000066
          .long    .L43
          .asciiz  "audio.xc"
          .int      0x00000060
          .int      0x00000062
          .long    .L42
          .asciiz  "audio.xc"
          .int      0x0000005e
          .int      0x0000005e
          .long    .L41
          .asciiz  "audio.xc"
          .int      0x0000005d
          .int      0x0000005d
          .long    .L40
          .asciiz  "audio.xc"
          .int      0x0000005b
          .int      0x0000005b
          .long    .L39
          .asciiz  "audio.xc"
          .int      0x00000051
          .int      0x00000059
          .long    .L38
.cc_bottom doClock.function
.cc_top dummyEP.function, dummyEP
          .asciiz  "audio.xc"
          .int      0x0000004c
          .int      0x0000004c
          .long    .L26
          .asciiz  "audio.xc"
          .int      0x00000048
          .int      0x0000004a
          .long    .L25
          .asciiz  "audio.xc"
          .int      0x00000046
          .int      0x00000046
          .long    .L24
          .asciiz  "audio.xc"
          .int      0x00000045
          .int      0x00000045
          .long    .L23
.cc_bottom dummyEP.function
.cc_top XUD_Endpoint3.function, XUD_Endpoint3
          .asciiz  "audio.xc"
          .int      0x0000003d
          .int      0x0000003d
          .long    .L17
          .asciiz  "audio.xc"
          .int      0x00000035
          .int      0x00000039
          .long    .L16
          .asciiz  "audio.xc"
          .int      0x00000033
          .int      0x00000033
          .long    .L15
          .asciiz  "audio.xc"
          .int      0x00000032
          .int      0x00000032
          .long    .L14
          .asciiz  "audio.xc"
          .int      0x00000030
          .int      0x00000030
          .long    .L13
          .asciiz  "audio.xc"
          .int      0x00000026
          .int      0x0000002e
          .long    .L12
.cc_bottom XUD_Endpoint3.function
.cc_top ep3dead.function, ep3dead
          .asciiz  "audio.xc"
          .int      0x00000020
          .int      0x00000020
          .long    .L5
          .asciiz  "audio.xc"
          .int      0x0000001f
          .int      0x0000001f
          .long    .L4
          .asciiz  "audio.xc"
          .int      0x0000001e
          .int      0x0000001e
          .long    .L3
.cc_bottom ep3dead.function
.L198:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
