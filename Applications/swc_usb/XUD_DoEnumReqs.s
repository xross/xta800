          .file     "./tests/audio/src/XUD_DoEnumReqs.xc"
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
.extern ParseSetupPacket, "f{0}(&(a(:uc)),&(s(setupPacket){m(bmRequestType){s(bmRequestType){m(Recipient){uc},m(Type){uc},m(Direction){uc}}},m(bRequest){uc},m(wValue){us},m(wIndex){us},m(wLength){us}}))"
          .text
.LDBG4:
.globl min, "f{si}(si,si)"
.globl min.nstackwords
.globl min.maxthreads
.globl min.maxtimers
.globl min.maxchanends
.globl min.maxsync
.type  min, @function
.linkset min.locnoside, 1
.linkset min.locnochandec, 1
.linkset min.nstackwords, 0
.cc_top min.function,min
.LDBG15:
.LDBG16:                             # line (11)
          .align    4
min:
.LDBG17:
.L3:
.LDBG5:
.LDBG6:
.LDBG18:                             # line (11)
.LDBG19:                             # line (12)
          lss       r2, r1, r0
          bt        r2, .L1 
.LDBG7:
.LDBG8:
.L5:
.LDBG20:                             # line (12)
.LDBG21:
          retsp     0x0 
.LDBG9:
.LDBG22:
.LDBG10:
.LDBG11:
.LDBG12:
.L1:
.L4:
.LDBG23:                             # line (12)
          mov       r0, r1
.LDBG24:
          retsp     0x0 
.LDBG13:
.LDBG25:
.LDBG14:
.cc_bottom min.function
.LDBG26:
          .section  .debug_frame, "",     @progbits
.cc_top min.function,min
          .align    4
          .int      .LDBG28-.LDBG27
.LDBG27:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG15
          .int      .LDBG26-.LDBG15
          .byte     0x01
          .int      .LDBG17
          .byte     0x01
          .int      .LDBG21
          .byte     0x0a
          .byte     0x01
          .int      .LDBG22
          .byte     0x0b
          .byte     0x01
          .int      .LDBG24
          .byte     0x0a
          .byte     0x01
          .int      .LDBG25
          .byte     0x0b
          .align    4, 0
.LDBG28:
.cc_bottom min.function
.linkset min.maxchanends, 0
.linkset min.maxtimers, 0
.linkset min.maxthreads, 1
          .text
.LDBG29:
.LDBG30:
.call XUD_DoEnumReqs, XUD_GetSetupBuffer_
.call XUD_DoEnumReqs, XUD_ParseSetupPacket
.call XUD_DoEnumReqs, XUD_SetBuffer_ResetPid_
.call XUD_DoEnumReqs, XUD_Sup_Delay
.call XUD_DoEnumReqs, XUD_SetDevAddr
.call XUD_DoEnumReqs, XUD_DoGetRequest
.call XUD_DoEnumReqs, XUD_DoGetRequest
.call XUD_DoEnumReqs, min
.call XUD_DoEnumReqs, safestrlen
.call XUD_DoEnumReqs, XUD_DoGetRequest
.globl XUD_DoEnumReqs, "f{si}(chd,&(a(:uc)),si,&(a(:uc)),si,&(a(:a(40:uc))),&(s(setupPacket){m(bmRequestType){s(bmRequestType){m(Recipient){uc},m(Type){uc},m(Direction){uc}}},m(bRequest){uc},m(wValue){us},m(wIndex){us},m(wLength){us}}))"
.globl XUD_DoEnumReqs.nstackwords
.globl XUD_DoEnumReqs.maxthreads
.globl XUD_DoEnumReqs.maxtimers
.globl XUD_DoEnumReqs.maxchanends
.globl XUD_DoEnumReqs.maxsync
.type  XUD_DoEnumReqs, @function
.linkset XUD_DoEnumReqs.locnoside, 1
.linkset XUD_DoEnumReqs.locnochandec, 1
.linkset .LLNK10, XUD_GetSetupBuffer_.nstackwords $M XUD_ParseSetupPacket.nstackwords
.linkset .LLNK9, .LLNK10 $M XUD_SetBuffer_ResetPid_.nstackwords
.linkset .LLNK8, .LLNK9 $M XUD_Sup_Delay.nstackwords
.linkset .LLNK7, .LLNK8 $M XUD_SetDevAddr.nstackwords
.linkset .LLNK6, .LLNK7 $M XUD_DoGetRequest.nstackwords
.linkset .LLNK5, .LLNK6 $M min.nstackwords
.linkset .LLNK4, .LLNK5 $M XUD_DoGetRequest.nstackwords
.linkset .LLNK3, .LLNK4 $M safestrlen.nstackwords
.linkset .LLNK2, .LLNK3 $M XUD_DoGetRequest.nstackwords
.linkset .LLNK1, .LLNK2 $M .LLNK2
.linkset .LLNK0, .LLNK1 + 42
.linkset XUD_DoEnumReqs.nstackwords, .LLNK0
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG98:
.LDBG99:                             # line (17)
          .align    4
XUD_DoEnumReqs:
          entsp     0x2a 
.LDBG100:
.L35:
          stw       r4, sp[0x5] 
.LDBG101:
          stw       r5, sp[0x6] 
.LDBG102:
          stw       r6, sp[0x7] 
.LDBG103:
          stw       r7, sp[0x8] 
.LDBG104:
          stw       r8, sp[0x9] 
.LDBG105:
          stw       r9, sp[0xa] 
.LDBG106:
          stw       r10, sp[0xb] 
.LDBG107:
          mov       r7, r0
.LDBG35:
          stw       r1, sp[0x3] 
          stw       r2, sp[0x4] 
          mov       r5, r3
.LDBG36:
          ldw       r9, sp[0x2b] 
.LDBG37:
          ldw       r10, sp[0x2e] 
          ldw       r8, sp[0x2f] 
.LDBG108:                            # line (17)
.LDBG33:
.LDBG109:                            # line (19)
          ldc       r4, 0x0
.LDBG38:
          ldc       r6, 0x0
.LDBG110:                            # line (26)
          mov       r1, r6
          ldaw      r2, sp[0xc] 
          mov       r0, r7
          ldc       r3, 0x78
.LDBG111:                            # line (26)
          bl        XUD_GetSetupBuffer_ 
.LDBG112:                            # line (26)
          ldw       r11, cp[s.LC0]
          .section .cp.const4,     "acM", @progbits, 4
s.LC0:
          .align    4
          .int      0xffffffff
          .text
          eq        r0, r0, r11
          bt        r0, .L7 
.LDBG39:
.LDBG40:
.L37:
.LDBG113:                            # line (31)
          ldaw      r0, sp[0xc] 
          ldw       r1, sp[0x2d] 
          ldc       r2, 0x78
.LDBG114:                            # line (31)
          bl        XUD_ParseSetupPacket 
.LDBG115:                            # line (34)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x0
          ld8u      r0, r1[r0] 
          bf        r0, .L10 
.LDBG41:
.LDBG42:
.LDBG43:
.LDBG44:
.L9:
.L50:
.LDBG116:                            # line (142)
          mov       r0, r4
.LDBG117:
          ldw       r4, sp[0x5] 
.LDBG118:
          ldw       r5, sp[0x6] 
.LDBG119:
          ldw       r6, sp[0x7] 
.LDBG120:
          ldw       r7, sp[0x8] 
.LDBG121:
          ldw       r8, sp[0x9] 
.LDBG122:
          ldw       r9, sp[0xa] 
.LDBG123:
          ldw       r10, sp[0xb] 
.LDBG124:
          retsp     0x2a 
.LDBG34:
.LDBG45:
.LDBG125:
.LDBG46:
.L7:
.L36:
.LDBG126:                            # line (27)
          ldw       r11, cp[s.LC0]
          mov       r0, r11
.LDBG127:
          ldw       r4, sp[0x5] 
.LDBG128:
          ldw       r5, sp[0x6] 
.LDBG129:
          ldw       r6, sp[0x7] 
.LDBG130:
          ldw       r7, sp[0x8] 
.LDBG131:
          ldw       r8, sp[0x9] 
.LDBG132:
          ldw       r9, sp[0xa] 
.LDBG133:
          ldw       r10, sp[0xb] 
.LDBG134:
          retsp     0x2a 
.LDBG47:
.LDBG135:
.LDBG48:
.L10:
.L38:
.LDBG136:                            # line (40)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x1
          ld8u      r0, r1[r0] 
          bf        r0, .L13 
.LDBG49:
.LDBG50:
          bu        .L9 
.LDBG51:
.LDBG52:
.L13:
.L39:
.LDBG137:                            # line (46)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x3
          ld8u      r0, r1[r0] 
          eq        r1, r0, 0x5
          bt        r1, .L16 
.LDBG53:
.LDBG54:
          eq        r0, r0, 0x6
          bt        r0, .L17 
.LDBG55:
.LDBG56:
          bu        .L9 
.LDBG57:
.LDBG58:
.L16:
.L40:
.LDBG138:                            # line (52)
          mov       r1, r6
          ldaw      r2, sp[0xc] 
          mov       r3, r6
          ldc       r4, 0x4b
          mov       r0, r7
          stw       r4, sp[0x1] 
          ldc       r4, 0x78
          stw       r4, sp[0x2] 
.LDBG139:                            # line (52)
          bl        XUD_SetBuffer_ResetPid_ 
.LDBG140:                            # line (55)
          ldc       r0, 0xc350
.LDBG141:                            # line (55)
          bl        XUD_Sup_Delay 
.LDBG142:                            # line (58)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x2
          ld16s     r1, r1[r0] 
          zext      r1, 0x10 
          mov       r0, r7
.LDBG143:                            # line (58)
          bl        XUD_SetDevAddr 
.LDBG144:                            # line (60)
          ldc       r4, 0x1
.LDBG59:
          bu        .L9 
.LDBG60:
.LDBG61:
.L17:
.L41:
.LDBG145:                            # line (68)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x2
          ld16s     r1, r1[r0] 
          zext      r1, 0x10 
          ldc       r0, 0xff00
          and       r1, r1, r0
          ldc       r0, 0x100
          eq        r0, r1, r0
          bt        r0, .L20 
.LDBG62:
.LDBG63:
          ldc       r0, 0x200
          eq        r0, r1, r0
          bt        r0, .L21 
.LDBG64:
.LDBG65:
          ldc       r0, 0x300
          eq        r0, r1, r0
          bt        r0, .L22 
.LDBG66:
.LDBG67:
          bu        .L9 
.LDBG68:
.LDBG69:
.L20:
.L42:
.LDBG146:                            # line (74)
          mov       r1, r6
          ldw       r2, sp[0x3] 
          ldw       r3, sp[0x4] 
          mov       r0, r7
          stw       r10, sp[0x1] 
.LDBG147:                            # line (74)
          bl        XUD_DoGetRequest 
.LDBG148:                            # line (76)
          ldc       r4, 0x1
.LDBG70:
          bu        .L9 
.LDBG71:
.LDBG72:
.L21:
.L43:
.LDBG149:                            # line (84)
          mov       r4, r6
.LDBG150:                            # line (84)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x4
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          mov       r1, r9
.LDBG151:                            # line (84)
          bl        min 
          mov       r3, r0
          mov       r0, r7
          mov       r1, r4
          mov       r2, r5
          stw       r8, sp[0x1] 
.LDBG152:                            # line (84)
          bl        XUD_DoGetRequest 
.LDBG153:                            # line (86)
          ldc       r4, 0x1
.LDBG73:
          bu        .L9 
.LDBG74:
.LDBG75:
.L22:
.L44:
.LDBG154:                            # line (95)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x2
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          ldc       r5, 0xff
          and       r0, r0, r5
          ldw       r1, sp[0x30] 
          lsu       r1, r0, r1
          ecallf    r1
          ldc       r4, 0x28
          mul       r1, r0, r4
          ldw       r0, sp[0x2c] 
          add       r0, r0, r1
          ldc       r1, 0x28
.LDBG155:                            # line (95)
          bl        safestrlen 
.LDBG156:                            # line (98)
          shl       r11, r0, 0x1
.LDBG76:
          add       r0, r11, 0x2
.LDBG157:                            # line (101)
          ldaw      r2, sp[0xc] 
          ldc       r1, 0x0
          st8       r0, r2[r1] 
.LDBG158:                            # line (103)
          ldw       r2, sp[0x2d] 
          ldc       r1, 0x4
          ld16s     r1, r2[r1] 
          zext      r1, 0x10 
.LDBG159:                            # line (103)
          lss       r0, r1, r0
          bt        r0, .L24 
.LDBG77:
.LDBG78:
.L23:
.L46:
.LDBG160:                            # line (107)
          ldaw      r2, sp[0xc] 
          ldc       r1, 0x1
          ldc       r0, 0x3
          st8       r0, r2[r1] 
.LDBG31:
.LDBG161:                            # line (110)
          ldc       r10, 0x0
.LDBG79:
          ldc       r1, 0x0
.LDBG162:                            # line (110)
          lss       r0, r1, r11
          bt        r0, .L51 
.LDBG80:
.LDBG81:
.L25:
.LDBG32:
.L49:
.LDBG163:                            # line (116)
          mov       r1, r6
          ldaw      r2, sp[0xc] 
          add       r0, r11, 0x2
          mov       r3, r0
          mov       r0, r7
          ldc       r4, 0x78
          stw       r4, sp[0x1] 
.LDBG164:                            # line (116)
          bl        XUD_DoGetRequest 
.LDBG165:                            # line (118)
          ldc       r4, 0x1
.LDBG82:
          bu        .L9 
.LDBG83:
.LDBG84:
.LDBG85:
.LDBG86:
.L24:
.L45:
.LDBG166:                            # line (104)
          ldw       r1, sp[0x2d] 
          ldc       r0, 0x4
          ld16s     r0, r1[r0] 
          zext      r0, 0x10 
          sub       r11, r0, 0x2
.LDBG87:
          bu        .L23 
.LDBG88:
.LDBG89:
.LDBG90:
.LDBG91:
.L51:
          ldaw      r8, sp[0xc] 
          ldc       r2, 0x78
.LDBG167:                            # line (113)
.LDBG92:
.LDBG93:
.L28:
.L47:
.LDBG168:                            # line (112)
          ldw       r3, sp[0x2d] 
          ldc       r0, 0x2
          ld16s     r0, r3[r0] 
          zext      r0, 0x10 
          and       r3, r0, r5
          ldw       r0, sp[0x30] 
          lsu       r0, r3, r0
          ecallf    r0
          mul       r3, r3, r4
          ldw       r0, sp[0x2c] 
          add       r9, r0, r3
          ashr      r3, r10, 0x1
          mov       r0, r3
          lsu       r0, r0, r4
          ecallf    r0
          ld8u      r9, r9[r3] 
          add       r0, r10, 0x2
          lsu       r3, r0, r2
          ecallf    r3
          st8       r9, r8[r0] 
          add       r3, r10, 0x3
          lsu       r9, r3, r2
          ecallf    r9
          st8       r1, r8[r3] 
.LDBG169:                            # line (110)
          mov       r10, r0
.L48:
.LDBG170:                            # line (110)
          lss       r0, r0, r11
          bt        r0, .L28 
.LDBG94:
.LDBG95:
          bu        .L25 
.LDBG96:
.LDBG97:
.cc_bottom XUD_DoEnumReqs.function
.LDBG171:
          .section  .debug_frame, "",     @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
          .align    4
          .int      .LDBG173-.LDBG172
.LDBG172:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG98
          .int      .LDBG171-.LDBG98
          .byte     0x01
          .int      .LDBG100
          .byte     0x0e
          .uleb128  0xa8
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG101
          .byte     0x84
          .uleb128  0x9
          .byte     0x01
          .int      .LDBG102
          .byte     0x85
          .uleb128  0x9
          .byte     0x01
          .int      .LDBG103
          .byte     0x86
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG104
          .byte     0x87
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG105
          .byte     0x88
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG106
          .byte     0x89
          .uleb128  0x8
          .byte     0x01
          .int      .LDBG107
          .byte     0x8a
          .uleb128  0x7
          .byte     0x01
          .int      .LDBG117
          .byte     0x0a
          .byte     0x01
          .int      .LDBG118
          .byte     0xc4
          .byte     0x01
          .int      .LDBG119
          .byte     0xc5
          .byte     0x01
          .int      .LDBG120
          .byte     0xc6
          .byte     0x01
          .int      .LDBG121
          .byte     0xc7
          .byte     0x01
          .int      .LDBG122
          .byte     0xc8
          .byte     0x01
          .int      .LDBG123
          .byte     0xc9
          .byte     0x01
          .int      .LDBG124
          .byte     0xca
          .byte     0x01
          .int      .LDBG125
          .byte     0x0b
          .byte     0x01
          .int      .LDBG127
          .byte     0x0a
          .byte     0x01
          .int      .LDBG128
          .byte     0xc4
          .byte     0x01
          .int      .LDBG129
          .byte     0xc5
          .byte     0x01
          .int      .LDBG130
          .byte     0xc6
          .byte     0x01
          .int      .LDBG131
          .byte     0xc7
          .byte     0x01
          .int      .LDBG132
          .byte     0xc8
          .byte     0x01
          .int      .LDBG133
          .byte     0xc9
          .byte     0x01
          .int      .LDBG134
          .byte     0xca
          .byte     0x01
          .int      .LDBG135
          .byte     0x0b
          .align    4, 0
.LDBG173:
.cc_bottom XUD_DoEnumReqs.function
.linkset .LLNK19, XUD_GetSetupBuffer_.maxchanends $M XUD_ParseSetupPacket.maxchanends
.linkset .LLNK18, .LLNK19 $M safestrlen.maxchanends
.linkset .LLNK17, .LLNK18 $M XUD_DoGetRequest.maxchanends
.linkset .LLNK16, .LLNK17 $M min.maxchanends
.linkset .LLNK15, .LLNK16 $M XUD_DoGetRequest.maxchanends
.linkset .LLNK14, .LLNK15 $M XUD_DoGetRequest.maxchanends
.linkset .LLNK13, .LLNK14 $M XUD_SetBuffer_ResetPid_.maxchanends
.linkset .LLNK12, .LLNK13 $M XUD_Sup_Delay.maxchanends
.linkset .LLNK11, .LLNK12 $M XUD_SetDevAddr.maxchanends
.linkset XUD_DoEnumReqs.maxchanends, .LLNK11
.linkset .LLNK28, XUD_GetSetupBuffer_.maxtimers $M XUD_ParseSetupPacket.maxtimers
.linkset .LLNK27, .LLNK28 $M safestrlen.maxtimers
.linkset .LLNK26, .LLNK27 $M XUD_DoGetRequest.maxtimers
.linkset .LLNK25, .LLNK26 $M min.maxtimers
.linkset .LLNK24, .LLNK25 $M XUD_DoGetRequest.maxtimers
.linkset .LLNK23, .LLNK24 $M XUD_DoGetRequest.maxtimers
.linkset .LLNK22, .LLNK23 $M XUD_SetBuffer_ResetPid_.maxtimers
.linkset .LLNK21, .LLNK22 $M XUD_Sup_Delay.maxtimers
.linkset .LLNK20, .LLNK21 $M XUD_SetDevAddr.maxtimers
.linkset XUD_DoEnumReqs.maxtimers, .LLNK20
.linkset .LLNK40, XUD_GetSetupBuffer_.maxthreads - 1
.linkset .LLNK39, 1 + .LLNK40
.linkset .LLNK38, 1 $M .LLNK39
.linkset .LLNK42, XUD_ParseSetupPacket.maxthreads - 1
.linkset .LLNK41, 1 + .LLNK42
.linkset .LLNK37, .LLNK38 $M .LLNK41
.linkset .LLNK44, safestrlen.maxthreads - 1
.linkset .LLNK43, 1 + .LLNK44
.linkset .LLNK36, .LLNK37 $M .LLNK43
.linkset .LLNK46, XUD_DoGetRequest.maxthreads - 1
.linkset .LLNK45, 1 + .LLNK46
.linkset .LLNK35, .LLNK36 $M .LLNK45
.linkset .LLNK48, min.maxthreads - 1
.linkset .LLNK47, 1 + .LLNK48
.linkset .LLNK34, .LLNK35 $M .LLNK47
.linkset .LLNK50, XUD_DoGetRequest.maxthreads - 1
.linkset .LLNK49, 1 + .LLNK50
.linkset .LLNK33, .LLNK34 $M .LLNK49
.linkset .LLNK52, XUD_DoGetRequest.maxthreads - 1
.linkset .LLNK51, 1 + .LLNK52
.linkset .LLNK32, .LLNK33 $M .LLNK51
.linkset .LLNK54, XUD_SetBuffer_ResetPid_.maxthreads - 1
.linkset .LLNK53, 1 + .LLNK54
.linkset .LLNK31, .LLNK32 $M .LLNK53
.linkset .LLNK56, XUD_Sup_Delay.maxthreads - 1
.linkset .LLNK55, 1 + .LLNK56
.linkset .LLNK30, .LLNK31 $M .LLNK55
.linkset .LLNK58, XUD_SetDevAddr.maxthreads - 1
.linkset .LLNK57, 1 + .LLNK58
.linkset .LLNK29, .LLNK30 $M .LLNK57
.linkset XUD_DoEnumReqs.maxthreads, .LLNK29
          .text
.LDBG174:
# Thread names for recovering thread graph in linker
.LDBG175:
          .section  .debug_info,   "",    @progbits
.LDBG178:
          .int      .LDBG180-.LDBG179
.LDBG179:
          .short    0x0003
          .long     .LDBG176         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG175         # high address
          .asciiz   "./tests/audio/src/XUD_DoEnumReqs.xc"
          .asciiz   "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Applications/swc_usb"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG177         # offset in .debug_lineprog
.LDBG181:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG182:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG183:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG184:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG185:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG186:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG187:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG188:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG189:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG190:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG191:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG192:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG193:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG194:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG195:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG196:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG197:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top min.function,min
.LDBG198:
          .uleb128  0x3
          .asciiz   "min"
          .byte     0x01
          .byte     0x0b
          .byte     0x01
          .int      .LDBG183-.LDBG178
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG29          # high address
          .uleb128  0x4
          .asciiz   "a"
          .byte     0x01
          .short    0x000b
          .int      .LDBG183-.LDBG178
          .int      .LDBG199
          .section  .debug_loc,    "",    @progbits
.cc_top min.function,min
.LDBG199:
          .int      .LDBG8-.LDBG0
          .int      .LDBG9-.LDBG0
          .short    .LDBG201-.LDBG200
.LDBG200:
          .byte     0x50
.LDBG201:
          .int      .LDBG5-.LDBG0
          .int      .LDBG7-.LDBG0
          .short    .LDBG203-.LDBG202
.LDBG202:
          .byte     0x50
.LDBG203:
          .int      0x00000000
          .int      0x00000000
.cc_bottom min.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x4
          .asciiz   "b"
          .byte     0x01
          .short    0x000b
          .int      .LDBG183-.LDBG178
          .int      .LDBG204
          .section  .debug_loc,    "",    @progbits
.cc_top min.function,min
.LDBG204:
          .int      .LDBG12-.LDBG0
          .int      .LDBG13-.LDBG0
          .short    .LDBG206-.LDBG205
.LDBG205:
          .byte     0x51
.LDBG206:
          .int      .LDBG10-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG208-.LDBG207
.LDBG207:
          .byte     0x51
.LDBG208:
          .int      .LDBG6-.LDBG0
          .int      .LDBG7-.LDBG0
          .short    .LDBG210-.LDBG209
.LDBG209:
          .byte     0x51
.LDBG210:
          .int      0x00000000
          .int      0x00000000
.cc_bottom min.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom min.function
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG211:
          .uleb128  0x3
          .asciiz   "XUD_DoEnumReqs"
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .int      .LDBG183-.LDBG178
          .byte     0x01
          .long     .LDBG30          # low address
          .long     .LDBG174         # high address
          .uleb128  0x4
          .asciiz   "c"
          .byte     0x01
          .short    0x000f
          .int      .LDBG189-.LDBG178
          .int      .LDBG212
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG212:
          .int      .LDBG95-.LDBG0
          .int      .LDBG96-.LDBG0
          .short    .LDBG214-.LDBG213
.LDBG213:
          .byte     0x57
.LDBG214:
          .int      .LDBG93-.LDBG0
          .int      .LDBG94-.LDBG0
          .short    .LDBG216-.LDBG215
.LDBG215:
          .byte     0x57
.LDBG216:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG218-.LDBG217
.LDBG217:
          .byte     0x57
.LDBG218:
          .int      .LDBG91-.LDBG0
          .int      .LDBG92-.LDBG0
          .short    .LDBG220-.LDBG219
.LDBG219:
          .byte     0x57
.LDBG220:
          .int      .LDBG86-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG222-.LDBG221
.LDBG221:
          .byte     0x57
.LDBG222:
          .int      .LDBG84-.LDBG0
          .int      .LDBG85-.LDBG0
          .short    .LDBG224-.LDBG223
.LDBG223:
          .byte     0x57
.LDBG224:
          .int      .LDBG81-.LDBG0
          .int      .LDBG83-.LDBG0
          .short    .LDBG226-.LDBG225
.LDBG225:
          .byte     0x57
.LDBG226:
          .int      .LDBG78-.LDBG0
          .int      .LDBG80-.LDBG0
          .short    .LDBG228-.LDBG227
.LDBG227:
          .byte     0x57
.LDBG228:
          .int      .LDBG72-.LDBG0
          .int      .LDBG74-.LDBG0
          .short    .LDBG230-.LDBG229
.LDBG229:
          .byte     0x57
.LDBG230:
          .int      .LDBG75-.LDBG0
          .int      .LDBG77-.LDBG0
          .short    .LDBG232-.LDBG231
.LDBG231:
          .byte     0x57
.LDBG232:
          .int      .LDBG69-.LDBG0
          .int      .LDBG71-.LDBG0
          .short    .LDBG234-.LDBG233
.LDBG233:
          .byte     0x57
.LDBG234:
          .int      .LDBG65-.LDBG0
          .int      .LDBG66-.LDBG0
          .short    .LDBG236-.LDBG235
.LDBG235:
          .byte     0x57
.LDBG236:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG238-.LDBG237
.LDBG237:
          .byte     0x57
.LDBG238:
          .int      .LDBG58-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG240-.LDBG239
.LDBG239:
          .byte     0x57
.LDBG240:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG242-.LDBG241
.LDBG241:
          .byte     0x57
.LDBG242:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG244-.LDBG243
.LDBG243:
          .byte     0x57
.LDBG244:
          .int      .LDBG52-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG246-.LDBG245
.LDBG245:
          .byte     0x57
.LDBG246:
          .int      .LDBG48-.LDBG0
          .int      .LDBG49-.LDBG0
          .short    .LDBG248-.LDBG247
.LDBG247:
          .byte     0x57
.LDBG248:
          .int      .LDBG40-.LDBG0
          .int      .LDBG41-.LDBG0
          .short    .LDBG250-.LDBG249
.LDBG249:
          .byte     0x57
.LDBG250:
          .int      .LDBG35-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG252-.LDBG251
.LDBG251:
          .byte     0x57
.LDBG252:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG253:
          .uleb128  0x5
          .int      .LDBG188-.LDBG178
          .int      0x00000000
          .byte     0x00
.LDBG254:
          .uleb128  0x6
          .int      .LDBG253-.LDBG178
          .uleb128  0x4
          .asciiz   "devDesc"
          .byte     0x01
          .short    0x000f
          .int      .LDBG254-.LDBG178
          .int      .LDBG255
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG255:
          .int      .LDBG30-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG257-.LDBG256
.LDBG256:
          .byte     0x7e
          .sleb128  0xc
.LDBG257:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x4
          .asciiz   "devDescLength"
          .byte     0x01
          .short    0x000f
          .int      .LDBG183-.LDBG178
          .int      .LDBG258
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG258:
          .int      .LDBG30-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG260-.LDBG259
.LDBG259:
          .byte     0x7e
          .sleb128  0x10
.LDBG260:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG261:
          .uleb128  0x5
          .int      .LDBG188-.LDBG178
          .int      0x00000000
          .byte     0x00
.LDBG262:
          .uleb128  0x6
          .int      .LDBG261-.LDBG178
          .uleb128  0x4
          .asciiz   "cfgDesc"
          .byte     0x01
          .short    0x000f
          .int      .LDBG262-.LDBG178
          .int      .LDBG263
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG263:
          .int      .LDBG72-.LDBG0
          .int      .LDBG74-.LDBG0
          .short    .LDBG265-.LDBG264
.LDBG264:
          .byte     0x55
.LDBG265:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG267-.LDBG266
.LDBG266:
          .byte     0x55
.LDBG267:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG269-.LDBG268
.LDBG268:
          .byte     0x55
.LDBG269:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG271-.LDBG270
.LDBG270:
          .byte     0x55
.LDBG271:
          .int      .LDBG52-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG273-.LDBG272
.LDBG272:
          .byte     0x55
.LDBG273:
          .int      .LDBG48-.LDBG0
          .int      .LDBG49-.LDBG0
          .short    .LDBG275-.LDBG274
.LDBG274:
          .byte     0x55
.LDBG275:
          .int      .LDBG40-.LDBG0
          .int      .LDBG41-.LDBG0
          .short    .LDBG277-.LDBG276
.LDBG276:
          .byte     0x55
.LDBG277:
          .int      .LDBG36-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG279-.LDBG278
.LDBG278:
          .byte     0x55
.LDBG279:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x4
          .asciiz   "cfgDescLength"
          .byte     0x01
          .short    0x000f
          .int      .LDBG183-.LDBG178
          .int      .LDBG280
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG280:
          .int      .LDBG72-.LDBG0
          .int      .LDBG74-.LDBG0
          .short    .LDBG282-.LDBG281
.LDBG281:
          .byte     0x59
.LDBG282:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG284-.LDBG283
.LDBG283:
          .byte     0x59
.LDBG284:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG286-.LDBG285
.LDBG285:
          .byte     0x59
.LDBG286:
          .int      .LDBG52-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG288-.LDBG287
.LDBG287:
          .byte     0x59
.LDBG288:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG290-.LDBG289
.LDBG289:
          .byte     0x59
.LDBG290:
          .int      .LDBG48-.LDBG0
          .int      .LDBG49-.LDBG0
          .short    .LDBG292-.LDBG291
.LDBG291:
          .byte     0x59
.LDBG292:
          .int      .LDBG37-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG294-.LDBG293
.LDBG293:
          .byte     0x59
.LDBG294:
          .int      .LDBG40-.LDBG0
          .int      .LDBG41-.LDBG0
          .short    .LDBG296-.LDBG295
.LDBG295:
          .byte     0x59
.LDBG296:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG297:
          .uleb128  0x5
          .int      .LDBG188-.LDBG178
          .int      0x00000028
          .byte     0x00
.LDBG298:
          .uleb128  0x5
          .int      .LDBG297-.LDBG178
          .int      0x00000000
          .byte     0x00
.LDBG299:
          .uleb128  0x6
          .int      .LDBG298-.LDBG178
          .uleb128  0x4
          .asciiz   "strDescs"
          .byte     0x01
          .short    0x0010
          .int      .LDBG299-.LDBG178
          .int      .LDBG300
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG300:
          .int      .LDBG30-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG302-.LDBG301
.LDBG301:
          .byte     0x7e
          .sleb128  0xb0
.LDBG302:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG303:
          .uleb128  0x7
          .asciiz   "setupPacket"
          .int      0x0000000a
.LDBG304:
          .uleb128  0x7
          .asciiz   "bmRequestType"
          .int      0x00000003
          .uleb128  0x8
          .asciiz   "Recipient"
          .byte     0x00
          .short    0x0018
          .int      .LDBG188-.LDBG178
          .uleb128  0x0
          .uleb128  0x8
          .asciiz   "Type"
          .byte     0x00
          .short    0x001d
          .int      .LDBG188-.LDBG178
          .uleb128  0x1
          .uleb128  0x8
          .asciiz   "Direction"
          .byte     0x00
          .short    0x0020
          .int      .LDBG188-.LDBG178
          .uleb128  0x2
          .byte     0x00
          .uleb128  0x8
          .asciiz   "bmRequestType"
          .byte     0x00
          .short    0x002a
          .int      .LDBG304-.LDBG178
          .uleb128  0x0
          .uleb128  0x8
          .asciiz   "bRequest"
          .byte     0x00
          .short    0x002b
          .int      .LDBG188-.LDBG178
          .uleb128  0x3
          .uleb128  0x8
          .asciiz   "wValue"
          .byte     0x00
          .short    0x002c
          .int      .LDBG186-.LDBG178
          .uleb128  0x4
          .uleb128  0x8
          .asciiz   "wIndex"
          .byte     0x00
          .short    0x002d
          .int      .LDBG186-.LDBG178
          .uleb128  0x6
          .uleb128  0x8
          .asciiz   "wLength"
          .byte     0x00
          .short    0x002e
          .int      .LDBG186-.LDBG178
          .uleb128  0x8
          .byte     0x00
.LDBG305:
          .uleb128  0x6
          .int      .LDBG303-.LDBG178
          .uleb128  0x4
          .asciiz   "sp"
          .byte     0x01
          .short    0x0010
          .int      .LDBG305-.LDBG178
          .int      .LDBG306
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG306:
          .int      .LDBG30-.LDBG0
          .int      .LDBG174-.LDBG0
          .short    .LDBG308-.LDBG307
.LDBG307:
          .byte     0x7e
          .sleb128  0xb4
.LDBG308:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "reqDone"
          .byte     0x01
          .short    0x0013
          .short    0x0000
          .int      .LDBG183-.LDBG178
          .int      .LDBG309
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG309:
          .int      .LDBG82-.LDBG0
          .int      .LDBG83-.LDBG0
          .short    .LDBG311-.LDBG310
.LDBG310:
          .byte     0x54
.LDBG311:
          .int      .LDBG73-.LDBG0
          .int      .LDBG74-.LDBG0
          .short    .LDBG313-.LDBG312
.LDBG312:
          .byte     0x54
.LDBG313:
          .int      .LDBG70-.LDBG0
          .int      .LDBG71-.LDBG0
          .short    .LDBG315-.LDBG314
.LDBG314:
          .byte     0x54
.LDBG315:
          .int      .LDBG67-.LDBG0
          .int      .LDBG68-.LDBG0
          .short    .LDBG317-.LDBG316
.LDBG316:
          .byte     0x54
.LDBG317:
          .int      .LDBG65-.LDBG0
          .int      .LDBG66-.LDBG0
          .short    .LDBG319-.LDBG318
.LDBG318:
          .byte     0x54
.LDBG319:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG321-.LDBG320
.LDBG320:
          .byte     0x54
.LDBG321:
          .int      .LDBG59-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG323-.LDBG322
.LDBG322:
          .byte     0x54
.LDBG323:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG325-.LDBG324
.LDBG324:
          .byte     0x54
.LDBG325:
          .int      .LDBG56-.LDBG0
          .int      .LDBG57-.LDBG0
          .short    .LDBG327-.LDBG326
.LDBG326:
          .byte     0x54
.LDBG327:
          .int      .LDBG54-.LDBG0
          .int      .LDBG55-.LDBG0
          .short    .LDBG329-.LDBG328
.LDBG328:
          .byte     0x54
.LDBG329:
          .int      .LDBG52-.LDBG0
          .int      .LDBG53-.LDBG0
          .short    .LDBG331-.LDBG330
.LDBG330:
          .byte     0x54
.LDBG331:
          .int      .LDBG50-.LDBG0
          .int      .LDBG51-.LDBG0
          .short    .LDBG333-.LDBG332
.LDBG332:
          .byte     0x54
.LDBG333:
          .int      .LDBG44-.LDBG0
          .int      .LDBG45-.LDBG0
          .short    .LDBG335-.LDBG334
.LDBG334:
          .byte     0x54
.LDBG335:
          .int      .LDBG48-.LDBG0
          .int      .LDBG49-.LDBG0
          .short    .LDBG337-.LDBG336
.LDBG336:
          .byte     0x54
.LDBG337:
          .int      .LDBG42-.LDBG0
          .int      .LDBG43-.LDBG0
          .short    .LDBG339-.LDBG338
.LDBG338:
          .byte     0x54
.LDBG339:
          .int      .LDBG40-.LDBG0
          .int      .LDBG41-.LDBG0
          .short    .LDBG341-.LDBG340
.LDBG340:
          .byte     0x54
.LDBG341:
          .int      .LDBG38-.LDBG0
          .int      .LDBG39-.LDBG0
          .short    .LDBG343-.LDBG342
.LDBG342:
          .byte     0x54
.LDBG343:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x9
          .asciiz   "datalength"
          .byte     0x01
          .short    0x0014
          .short    0x0000
          .int      .LDBG183-.LDBG178
          .int      .LDBG344
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG344:
          .int      .LDBG93-.LDBG0
          .int      .LDBG94-.LDBG0
          .short    .LDBG346-.LDBG345
.LDBG345:
          .byte     0x5b
.LDBG346:
          .int      .LDBG95-.LDBG0
          .int      .LDBG96-.LDBG0
          .short    .LDBG348-.LDBG347
.LDBG347:
          .byte     0x5b
.LDBG348:
          .int      .LDBG91-.LDBG0
          .int      .LDBG92-.LDBG0
          .short    .LDBG350-.LDBG349
.LDBG349:
          .byte     0x5b
.LDBG350:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG352-.LDBG351
.LDBG351:
          .byte     0x5b
.LDBG352:
          .int      .LDBG87-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG354-.LDBG353
.LDBG353:
          .byte     0x5b
.LDBG354:
          .int      .LDBG81-.LDBG0
          .int      .LDBG83-.LDBG0
          .short    .LDBG356-.LDBG355
.LDBG355:
          .byte     0x5b
.LDBG356:
          .int      .LDBG78-.LDBG0
          .int      .LDBG80-.LDBG0
          .short    .LDBG358-.LDBG357
.LDBG357:
          .byte     0x5b
.LDBG358:
          .int      .LDBG76-.LDBG0
          .int      .LDBG77-.LDBG0
          .short    .LDBG360-.LDBG359
.LDBG359:
          .byte     0x5b
.LDBG360:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG361:
          .uleb128  0x5
          .int      .LDBG188-.LDBG178
          .int      0x00000078
          .uleb128  0xa
          .byte     0x00
          .int      0x00000077
          .byte     0x00
          .uleb128  0x9
          .asciiz   "buffer"
          .byte     0x01
          .short    0x0017
          .short    0x0000
          .int      .LDBG361-.LDBG178
          .int      .LDBG362
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG362:
          .int      .LDBG33-.LDBG0
          .int      .LDBG34-.LDBG0
          .short    .LDBG364-.LDBG363
.LDBG363:
          .byte     0x7e
          .sleb128  0x30
.LDBG364:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG365:
          .uleb128  0xb
          .long     .LDBG31          # low address
          .long     .LDBG32          # high address
          .uleb128  0x9
          .asciiz   "i"
          .byte     0x01
          .short    0x006e
          .short    .LDBG366-.LDBG365
          .int      .LDBG183-.LDBG178
          .int      .LDBG367
          .section  .debug_loc,    "",    @progbits
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
.LDBG367:
          .int      .LDBG93-.LDBG0
          .int      .LDBG94-.LDBG0
          .short    .LDBG369-.LDBG368
.LDBG368:
          .byte     0x5a
.LDBG369:
          .int      .LDBG91-.LDBG0
          .int      .LDBG92-.LDBG0
          .short    .LDBG371-.LDBG370
.LDBG370:
          .byte     0x5a
.LDBG371:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG373-.LDBG372
.LDBG372:
          .byte     0x5a
.LDBG373:
          .int      .LDBG79-.LDBG0
          .int      .LDBG80-.LDBG0
          .short    .LDBG375-.LDBG374
.LDBG374:
          .byte     0x5a
.LDBG375:
          .int      0x00000000
          .int      0x00000000
.cc_bottom XUD_DoEnumReqs.function
          .section  .debug_info,   "",    @progbits
.LDBG366:
          .byte     0x00
          .byte     0x00
.cc_bottom XUD_DoEnumReqs.function
          .byte     0x00
.LDBG180:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG377-.LDBG376
.LDBG376:
          .short    0x0002
          .long     .LDBG178         # offset in .debug_info
          .int      .LDBG180-.LDBG178
.cc_top min.function,min
          .int      .LDBG198-.LDBG178
          .asciiz   "min"
.cc_bottom min.function
.cc_top XUD_DoEnumReqs.function,XUD_DoEnumReqs
          .int      .LDBG211-.LDBG178
          .asciiz   "XUD_DoEnumReqs"
.cc_bottom XUD_DoEnumReqs.function
          .int      0x00000000
.LDBG377:
          .section  .debug_line,   "",    @progbits
.LDBG177:
          .int      .LDBG379-.LDBG378
.LDBG378:
          .short    0x0003
          .int      .LDBG381-.LDBG380
.LDBG380:
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
          .asciiz   "./tests/audio/src/XUD_DoEnumReqs.xc"
          .uleb128  0x0
          .uleb128  0x49c8db41
          .uleb128  0x1454
          .byte     0x00
.LDBG381:
.cc_top min.function, min
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG16
          .byte     0x03
          .sleb128  0xa
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG19
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom min.function
.cc_top XUD_DoEnumReqs.function, XUD_DoEnumReqs
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG99
          .byte     0x03
          .sleb128  0x10
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG109
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG110
          .byte     0x03
          .sleb128  0x7
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG113
          .byte     0x03
          .sleb128  0x5
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG115
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG116
          .byte     0x03
          .sleb128  0x6c
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG126
          .byte     0x03
          .sleb128  0xffffff8d
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG136
          .byte     0x03
          .sleb128  0xd
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG137
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG138
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG140
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG142
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG144
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG145
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG146
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG148
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG149
          .byte     0x03
          .sleb128  0x8
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG153
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG154
          .byte     0x03
          .sleb128  0x9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG156
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG157
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG158
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG160
          .byte     0x03
          .sleb128  0x4
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG161
          .byte     0x03
          .sleb128  0x3
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG163
          .byte     0x03
          .sleb128  0x6
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG165
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG166
          .byte     0x03
          .sleb128  0xfffffff2
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG167
          .byte     0x03
          .sleb128  0x9
          .byte     0x01
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG168
          .byte     0x10             # special opcode (line + -1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG169
          .byte     0x03
          .sleb128  0xfffffffe
          .byte     0x01
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.cc_bottom XUD_DoEnumReqs.function
.LDBG379:
          .section  .debug_abbrev, "",    @progbits
.LDBG176:
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
          .uleb128  0x9
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
          .uleb128  0xb
          .byte     0x0b
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .byte     0x12
          .byte     0x01
          .byte     0x00
          .byte     0x00
          .uleb128  0x6
          .byte     0x10
          .byte     0x00
          .byte     0x49
          .byte     0x13
          .byte     0x00
          .byte     0x00
          .uleb128  0x5
          .byte     0x01
          .byte     0x01
          .byte     0x49
          .byte     0x13
          .byte     0x0b
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0xa
          .byte     0x21
          .byte     0x00
          .byte     0x22
          .byte     0x0b
          .byte     0x2f
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x7
          .byte     0x13
          .byte     0x01
          .byte     0x03
          .byte     0x08
          .byte     0x0b
          .byte     0x06
          .byte     0x00
          .byte     0x00
          .uleb128  0x8
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
.L55:
          .int      .L56-.L55
          .int      0x00000000
          .asciiz   "/home/kris/Views/Tools/verif_tests_sw/Tools/xta/Applications/swc_usb"
.cc_top XUD_DoEnumReqs.function, XUD_DoEnumReqs
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000008e
          .int      0x0000008e
          .long    .L50
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000074
          .int      0x00000076
          .long    .L49
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000006e
          .int      0x0000006e
          .long    .L48
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000070
          .int      0x0000006e
          .long    .L47
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000006b
          .int      0x0000006e
          .long    .L46
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000068
          .int      0x00000068
          .long    .L45
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000005f
          .int      0x00000067
          .long    .L44
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000054
          .int      0x00000056
          .long    .L43
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000004a
          .int      0x0000004c
          .long    .L42
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000044
          .int      0x00000044
          .long    .L41
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000034
          .int      0x0000003c
          .long    .L40
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000002e
          .int      0x0000002e
          .long    .L39
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000028
          .int      0x00000028
          .long    .L38
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000001f
          .int      0x00000022
          .long    .L37
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000001b
          .int      0x0000001b
          .long    .L36
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x00000011
          .int      0x0000001a
          .long    .L35
.cc_bottom XUD_DoEnumReqs.function
.cc_top min.function, min
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000000c
          .int      0x0000000c
          .long    .L5
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000000c
          .int      0x0000000c
          .long    .L4
          .asciiz  "./tests/audio/src/XUD_DoEnumReqs.xc"
          .int      0x0000000b
          .int      0x0000000c
          .long    .L3
.cc_bottom min.function
.L56:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
