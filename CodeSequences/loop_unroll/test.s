          .file     "test.xc"
          .text
          .align    2

.LDBG0:
          .section  .debug_line,   "",    @progbits
.LDBG1:
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
          .text
.cc_top foo.function,foo
          .align    4
.LDBG5:
.globl foo, "f{si}(si)"
.globl foo.nstackwords
.globl foo.maxthreads
.globl foo.maxtimers
.globl foo.maxchanends
.globl foo.maxsync
.type  foo, @function
.linkset foo.locnoside, 1
.linkset foo.locnochandec, 1
.linkset foo.nstackwords, 0
.LDBG173:
.LDBG8:
foo:
.LDBG174:
.LDBG9:
          .file     1 "test.xc"
          .loc      1 1 0

.LDBG6:
          .loc      1 4 0

.L11:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 4 0

          ldc       r11, 0x345
          mkmsk     r3, 0x1
          ldc       r1, 0x2c
.L25:
.xtaloop 20
          .loc      1 5 0

.L46:
          .loc      1 5 0

          xor       r2, r0, r11
.L44:
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

.L61:
          and       r2, r0, r3
.L59:
          .loc      1 6 0

          bf        r2, .L76 
.LDBG10:
.LDBG11:
          .loc      1 11 0

.L89:
          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L84:
          add       r0, r0, r1
          .loc      1 5 0

.LDBG12:
.LDBG13:
.L117:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L135 
.LDBG14:
.LDBG15:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L142:
          add       r2, r1, 0x1
          add       r0, r0, r2
          .loc      1 5 0

.LDBG16:
.LDBG17:
.L157:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L175 
.LDBG18:
.LDBG19:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L182:
          add       r2, r1, 0x2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG20:
.LDBG21:
.L198:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L216 
.LDBG22:
.LDBG23:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L223:
          add       r2, r1, 0x3
          add       r0, r0, r2
          .loc      1 5 0

.LDBG24:
.LDBG25:
.L239:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L257 
.LDBG26:
.LDBG27:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L264:
          add       r2, r1, 0x4
          add       r0, r0, r2
          .loc      1 5 0

.LDBG28:
.LDBG29:
.L280:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L298 
.LDBG30:
.LDBG31:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L305:
          add       r2, r1, 0x5
          add       r0, r0, r2
          .loc      1 5 0

.LDBG32:
.LDBG33:
.L321:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L339 
.LDBG34:
.LDBG35:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L346:
          add       r2, r1, 0x6
          add       r0, r0, r2
          .loc      1 5 0

.LDBG36:
.LDBG37:
.L362:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L380 
.LDBG38:
.LDBG39:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L387:
          add       r2, r1, 0x7
          add       r0, r0, r2
          .loc      1 5 0

.LDBG40:
.LDBG41:
.L403:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L421 
.LDBG42:
.LDBG43:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L428:
          add       r2, r1, 0x8
          add       r0, r0, r2
          .loc      1 5 0

.LDBG44:
.LDBG45:
.L444:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L462 
.LDBG46:
.LDBG47:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L469:
          add       r2, r1, 0x9
          add       r0, r0, r2
          .loc      1 5 0

.LDBG48:
.LDBG49:
.L485:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L503 
.LDBG50:
.LDBG51:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L510:
          add       r2, r1, 0xa
          add       r0, r0, r2
          .loc      1 5 0

.LDBG52:
.LDBG53:
.L526:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L544 
.LDBG54:
.LDBG55:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L551:
          add       r2, r1, 0xb
          add       r0, r0, r2
          .loc      1 5 0

.LDBG56:
.LDBG57:
.L567:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L585 
.LDBG58:
.LDBG59:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L592:
          ldc       r2, 0xc
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG60:
.LDBG61:
.L608:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L626 
.LDBG62:
.LDBG63:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L633:
          ldc       r2, 0xd
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG64:
.LDBG65:
.L649:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L667 
.LDBG66:
.LDBG67:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L674:
          ldc       r2, 0xe
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG68:
.LDBG69:
.L690:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L708 
.LDBG70:
.LDBG71:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L715:
          mkmsk     r2, 0x4
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG72:
.LDBG73:
.L731:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L749 
.LDBG74:
.LDBG75:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L756:
          ldc       r2, 0x10
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG76:
.LDBG77:
.L772:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L790 
.LDBG78:
.LDBG79:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L797:
          ldc       r2, 0x11
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG80:
.LDBG81:
.L813:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L831 
.LDBG82:
.LDBG83:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L838:
          ldc       r2, 0x12
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 5 0

.LDBG84:
.LDBG85:
.L854:
          .loc      1 5 0

          xor       r2, r0, r11
          mul       r2, r2, r0
          add       r0, r0, r2
          .loc      1 6 0

          and       r2, r0, r3
          .loc      1 6 0

          bf        r2, .L872 
.LDBG86:
.LDBG87:
          .loc      1 11 0

          .loc      1 11 0

.xtalabel "retain_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 10
.L879:
          ldc       r2, 0x13
          add       r2, r1, r2
          add       r0, r0, r2
          .loc      1 14 0

.L109:
.LDBG88:
.LDBG89:
.L111:
.LDBG175:
.L894:
          retsp     0x0 
.LDBG90:
.LDBG176:
.LDBG91:
          .loc      1 8 0

.LDBG92:
.LDBG93:
.L76:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L70:
          ldc       r2, 0x37
          or        r0, r0, r2
          bu        .L117 
.LDBG94:
.LDBG95:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG96:
.LDBG97:
.L135:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L136:
          ldc       r2, 0x36
          or        r0, r0, r2
          bu        .L157 
.LDBG98:
.LDBG99:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG100:
.LDBG101:
.L175:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L176:
          ldc       r2, 0x35
          or        r0, r0, r2
          bu        .L198 
.LDBG102:
.LDBG103:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG104:
.LDBG105:
.L216:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L217:
          ldc       r2, 0x34
          or        r0, r0, r2
          bu        .L239 
.LDBG106:
.LDBG107:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG108:
.LDBG109:
.L257:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L258:
          ldc       r2, 0x33
          or        r0, r0, r2
          bu        .L280 
.LDBG110:
.LDBG111:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG112:
.LDBG113:
.L298:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L299:
          ldc       r2, 0x32
          or        r0, r0, r2
          bu        .L321 
.LDBG114:
.LDBG115:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG116:
.LDBG117:
.L339:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L340:
          ldc       r2, 0x31
          or        r0, r0, r2
          bu        .L362 
.LDBG118:
.LDBG119:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG120:
.LDBG121:
.L380:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L381:
          ldc       r2, 0x30
          or        r0, r0, r2
          bu        .L403 
.LDBG122:
.LDBG123:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG124:
.LDBG125:
.L421:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L422:
          mkmsk     r2, 0x6
          or        r0, r0, r2
          bu        .L444 
.LDBG126:
.LDBG127:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG128:
.LDBG129:
.L462:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L463:
          ldc       r2, 0x3e
          or        r0, r0, r2
          bu        .L485 
.LDBG130:
.LDBG131:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG132:
.LDBG133:
.L503:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L504:
          ldc       r2, 0x3d
          or        r0, r0, r2
          bu        .L526 
.LDBG134:
.LDBG135:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG136:
.LDBG137:
.L544:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L545:
          ldc       r2, 0x3c
          or        r0, r0, r2
          bu        .L567 
.LDBG138:
.LDBG139:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG140:
.LDBG141:
.L585:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L586:
          ldc       r2, 0x3b
          or        r0, r0, r2
          bu        .L608 
.LDBG142:
.LDBG143:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG144:
.LDBG145:
.L626:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L627:
          ldc       r2, 0x3a
          or        r0, r0, r2
          bu        .L649 
.LDBG146:
.LDBG147:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG148:
.LDBG149:
.L667:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L668:
          ldc       r2, 0x39
          or        r0, r0, r2
          bu        .L690 
.LDBG150:
.LDBG151:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG152:
.LDBG153:
.L708:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L709:
          ldc       r2, 0x38
          or        r0, r0, r2
          bu        .L731 
.LDBG154:
.LDBG155:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG156:
.LDBG157:
.L749:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L750:
          ldc       r2, 0x27
          or        r0, r0, r2
          bu        .L772 
.LDBG158:
.LDBG159:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG160:
.LDBG161:
.L790:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L791:
          ldc       r2, 0x26
          or        r0, r0, r2
          bu        .L813 
.LDBG162:
.LDBG163:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG164:
.LDBG165:
.L831:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L832:
          ldc       r2, 0x25
          or        r0, r0, r2
          bu        .L854 
.LDBG166:
.LDBG167:
          .loc      1 4 0

          .loc      1 4 0

          .loc      1 8 0

.LDBG168:
.LDBG169:
.L872:
          .loc      1 8 0

.xtalabel "exclusion_path", "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll", "test.xc", 7
.L873:
          ldc       r2, 0x24
          or        r0, r0, r2
          bu        .L111 
.LDBG170:
.LDBG171:
          .loc      1 4 0

          .loc      1 4 0

.LDBG7:
.LDBG172:
.LDBG177:
.cc_bottom foo.function
          .section  .debug_frame, "",     @progbits
.cc_top foo.function,foo
          .align    4
          .int      .LDBG179-.LDBG178
.LDBG178:
          .long     .LDBG2           # offset in .debug_frame
          .int      .LDBG173
          .int      .LDBG177-.LDBG173
          .byte     0x01
          .int      .LDBG174
          .byte     0x01
          .int      .LDBG175
          .byte     0x0a
          .byte     0x01
          .int      .LDBG176
          .byte     0x0b
          .align    4, 0
.LDBG179:
.cc_bottom foo.function
.linkset foo.maxchanends, 0
.linkset foo.maxtimers, 0
.linkset foo.maxthreads, 1
          .text
.LDBG180:
.cc_top main.function,main
          .align    4
.LDBG181:
.call main, foo
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 1
.linkset .LLNK1, foo.nstackwords $M foo.nstackwords
.linkset .LLNK0, .LLNK1 + 1
.linkset main.nstackwords, .LLNK0
.LDBG184:
.LDBG182:
main:
          entsp     0x1 
.LDBG185:
          .loc      1 17 0

          .loc      1 18 0

.L897:
          .loc      1 18 0

          ldc       r0, 0x67
          .loc      1 18 0

.L904:
          bl        foo 
.LDBG186:
.L903:
          retsp     0x1 
.LDBG183:
.LDBG187:
.LDBG188:
.cc_bottom main.function
          .section  .debug_frame, "",     @progbits
.cc_top main.function,main
          .align    4
          .int      .LDBG190-.LDBG189
.LDBG189:
          .long     .LDBG2           # offset in .debug_frame
          .int      .LDBG184
          .int      .LDBG188-.LDBG184
          .byte     0x01
          .int      .LDBG185
          .byte     0x0e
          .uleb128  0x4
          .byte     0x14
          .uleb128  0xe
          .uleb128  0x0
          .byte     0x8f
          .uleb128  0x0
          .byte     0x01
          .int      .LDBG186
          .byte     0x0a
          .byte     0x01
          .int      .LDBG187
          .byte     0x0b
          .align    4, 0
.LDBG190:
.cc_bottom main.function
.linkset main.maxchanends, foo.maxchanends
.linkset main.maxtimers, foo.maxtimers
.linkset .LLNK4, foo.maxthreads - 1
.linkset .LLNK3, 1 + .LLNK4
.linkset .LLNK2, 1 $M .LLNK3
.linkset main.maxthreads, .LLNK2
          .text
.LDBG191:
# Thread names for recovering thread graph in linker
.LDBG192:
          .section  .debug_info,   "",    @progbits
.LDBG194:
          .int      .LDBG196-.LDBG195
.LDBG195:
          .short    0x0003
          .long     .LDBG193         # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG192         # high address
          .asciiz   "test.xc"
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG1           # offset in .debug_lineprog
.LDBG197:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG198:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG199:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG200:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG201:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG202:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG203:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG204:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG205:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG206:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG207:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG208:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG209:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG210:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG211:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG212:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG213:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top foo.function,foo
.LDBG214:
          .uleb128  0x3
          .asciiz   "foo"
          .byte     0x01
          .byte     0x01
          .byte     0x01
          .int      .LDBG199-.LDBG194
          .byte     0x01
          .long     .LDBG5           # low address
          .long     .LDBG180         # high address
          .uleb128  0x4
          .asciiz   "j"
          .byte     0x01
          .short    0x0001
          .int      .LDBG199-.LDBG194
          .int      .LDBG215
          .section  .debug_loc,    "",    @progbits
.cc_top foo.function,foo
.LDBG215:
          .int      .LDBG85-.LDBG0
          .int      .LDBG86-.LDBG0
          .short    .LDBG217-.LDBG216
.LDBG216:
          .byte     0x50
.LDBG217:
          .int      .LDBG83-.LDBG0
          .int      .LDBG84-.LDBG0
          .short    .LDBG219-.LDBG218
.LDBG218:
          .byte     0x50
.LDBG219:
          .int      .LDBG81-.LDBG0
          .int      .LDBG82-.LDBG0
          .short    .LDBG221-.LDBG220
.LDBG220:
          .byte     0x50
.LDBG221:
          .int      .LDBG79-.LDBG0
          .int      .LDBG80-.LDBG0
          .short    .LDBG223-.LDBG222
.LDBG222:
          .byte     0x50
.LDBG223:
          .int      .LDBG77-.LDBG0
          .int      .LDBG78-.LDBG0
          .short    .LDBG225-.LDBG224
.LDBG224:
          .byte     0x50
.LDBG225:
          .int      .LDBG135-.LDBG0
          .int      .LDBG136-.LDBG0
          .short    .LDBG227-.LDBG226
.LDBG226:
          .byte     0x50
.LDBG227:
          .int      .LDBG75-.LDBG0
          .int      .LDBG76-.LDBG0
          .short    .LDBG229-.LDBG228
.LDBG228:
          .byte     0x50
.LDBG229:
          .int      .LDBG133-.LDBG0
          .int      .LDBG134-.LDBG0
          .short    .LDBG231-.LDBG230
.LDBG230:
          .byte     0x50
.LDBG231:
          .int      .LDBG73-.LDBG0
          .int      .LDBG74-.LDBG0
          .short    .LDBG233-.LDBG232
.LDBG232:
          .byte     0x50
.LDBG233:
          .int      .LDBG71-.LDBG0
          .int      .LDBG72-.LDBG0
          .short    .LDBG235-.LDBG234
.LDBG234:
          .byte     0x50
.LDBG235:
          .int      .LDBG131-.LDBG0
          .int      .LDBG132-.LDBG0
          .short    .LDBG237-.LDBG236
.LDBG236:
          .byte     0x50
.LDBG237:
          .int      .LDBG69-.LDBG0
          .int      .LDBG70-.LDBG0
          .short    .LDBG239-.LDBG238
.LDBG238:
          .byte     0x50
.LDBG239:
          .int      .LDBG129-.LDBG0
          .int      .LDBG130-.LDBG0
          .short    .LDBG241-.LDBG240
.LDBG240:
          .byte     0x50
.LDBG241:
          .int      .LDBG67-.LDBG0
          .int      .LDBG68-.LDBG0
          .short    .LDBG243-.LDBG242
.LDBG242:
          .byte     0x50
.LDBG243:
          .int      .LDBG127-.LDBG0
          .int      .LDBG128-.LDBG0
          .short    .LDBG245-.LDBG244
.LDBG244:
          .byte     0x50
.LDBG245:
          .int      .LDBG65-.LDBG0
          .int      .LDBG66-.LDBG0
          .short    .LDBG247-.LDBG246
.LDBG246:
          .byte     0x50
.LDBG247:
          .int      .LDBG125-.LDBG0
          .int      .LDBG126-.LDBG0
          .short    .LDBG249-.LDBG248
.LDBG248:
          .byte     0x50
.LDBG249:
          .int      .LDBG63-.LDBG0
          .int      .LDBG64-.LDBG0
          .short    .LDBG251-.LDBG250
.LDBG250:
          .byte     0x50
.LDBG251:
          .int      .LDBG123-.LDBG0
          .int      .LDBG124-.LDBG0
          .short    .LDBG253-.LDBG252
.LDBG252:
          .byte     0x50
.LDBG253:
          .int      .LDBG61-.LDBG0
          .int      .LDBG62-.LDBG0
          .short    .LDBG255-.LDBG254
.LDBG254:
          .byte     0x50
.LDBG255:
          .int      .LDBG59-.LDBG0
          .int      .LDBG60-.LDBG0
          .short    .LDBG257-.LDBG256
.LDBG256:
          .byte     0x50
.LDBG257:
          .int      .LDBG121-.LDBG0
          .int      .LDBG122-.LDBG0
          .short    .LDBG259-.LDBG258
.LDBG258:
          .byte     0x50
.LDBG259:
          .int      .LDBG57-.LDBG0
          .int      .LDBG58-.LDBG0
          .short    .LDBG261-.LDBG260
.LDBG260:
          .byte     0x50
.LDBG261:
          .int      .LDBG119-.LDBG0
          .int      .LDBG120-.LDBG0
          .short    .LDBG263-.LDBG262
.LDBG262:
          .byte     0x50
.LDBG263:
          .int      .LDBG55-.LDBG0
          .int      .LDBG56-.LDBG0
          .short    .LDBG265-.LDBG264
.LDBG264:
          .byte     0x50
.LDBG265:
          .int      .LDBG117-.LDBG0
          .int      .LDBG118-.LDBG0
          .short    .LDBG267-.LDBG266
.LDBG266:
          .byte     0x50
.LDBG267:
          .int      .LDBG53-.LDBG0
          .int      .LDBG54-.LDBG0
          .short    .LDBG269-.LDBG268
.LDBG268:
          .byte     0x50
.LDBG269:
          .int      .LDBG115-.LDBG0
          .int      .LDBG116-.LDBG0
          .short    .LDBG271-.LDBG270
.LDBG270:
          .byte     0x50
.LDBG271:
          .int      .LDBG51-.LDBG0
          .int      .LDBG52-.LDBG0
          .short    .LDBG273-.LDBG272
.LDBG272:
          .byte     0x50
.LDBG273:
          .int      .LDBG113-.LDBG0
          .int      .LDBG114-.LDBG0
          .short    .LDBG275-.LDBG274
.LDBG274:
          .byte     0x50
.LDBG275:
          .int      .LDBG49-.LDBG0
          .int      .LDBG50-.LDBG0
          .short    .LDBG277-.LDBG276
.LDBG276:
          .byte     0x50
.LDBG277:
          .int      .LDBG47-.LDBG0
          .int      .LDBG48-.LDBG0
          .short    .LDBG279-.LDBG278
.LDBG278:
          .byte     0x50
.LDBG279:
          .int      .LDBG111-.LDBG0
          .int      .LDBG112-.LDBG0
          .short    .LDBG281-.LDBG280
.LDBG280:
          .byte     0x50
.LDBG281:
          .int      .LDBG45-.LDBG0
          .int      .LDBG46-.LDBG0
          .short    .LDBG283-.LDBG282
.LDBG282:
          .byte     0x50
.LDBG283:
          .int      .LDBG109-.LDBG0
          .int      .LDBG110-.LDBG0
          .short    .LDBG285-.LDBG284
.LDBG284:
          .byte     0x50
.LDBG285:
          .int      .LDBG43-.LDBG0
          .int      .LDBG44-.LDBG0
          .short    .LDBG287-.LDBG286
.LDBG286:
          .byte     0x50
.LDBG287:
          .int      .LDBG41-.LDBG0
          .int      .LDBG42-.LDBG0
          .short    .LDBG289-.LDBG288
.LDBG288:
          .byte     0x50
.LDBG289:
          .int      .LDBG107-.LDBG0
          .int      .LDBG108-.LDBG0
          .short    .LDBG291-.LDBG290
.LDBG290:
          .byte     0x50
.LDBG291:
          .int      .LDBG39-.LDBG0
          .int      .LDBG40-.LDBG0
          .short    .LDBG293-.LDBG292
.LDBG292:
          .byte     0x50
.LDBG293:
          .int      .LDBG105-.LDBG0
          .int      .LDBG106-.LDBG0
          .short    .LDBG295-.LDBG294
.LDBG294:
          .byte     0x50
.LDBG295:
          .int      .LDBG37-.LDBG0
          .int      .LDBG38-.LDBG0
          .short    .LDBG297-.LDBG296
.LDBG296:
          .byte     0x50
.LDBG297:
          .int      .LDBG103-.LDBG0
          .int      .LDBG104-.LDBG0
          .short    .LDBG299-.LDBG298
.LDBG298:
          .byte     0x50
.LDBG299:
          .int      .LDBG35-.LDBG0
          .int      .LDBG36-.LDBG0
          .short    .LDBG301-.LDBG300
.LDBG300:
          .byte     0x50
.LDBG301:
          .int      .LDBG33-.LDBG0
          .int      .LDBG34-.LDBG0
          .short    .LDBG303-.LDBG302
.LDBG302:
          .byte     0x50
.LDBG303:
          .int      .LDBG101-.LDBG0
          .int      .LDBG102-.LDBG0
          .short    .LDBG305-.LDBG304
.LDBG304:
          .byte     0x50
.LDBG305:
          .int      .LDBG31-.LDBG0
          .int      .LDBG32-.LDBG0
          .short    .LDBG307-.LDBG306
.LDBG306:
          .byte     0x50
.LDBG307:
          .int      .LDBG99-.LDBG0
          .int      .LDBG100-.LDBG0
          .short    .LDBG309-.LDBG308
.LDBG308:
          .byte     0x50
.LDBG309:
          .int      .LDBG29-.LDBG0
          .int      .LDBG30-.LDBG0
          .short    .LDBG311-.LDBG310
.LDBG310:
          .byte     0x50
.LDBG311:
          .int      .LDBG27-.LDBG0
          .int      .LDBG28-.LDBG0
          .short    .LDBG313-.LDBG312
.LDBG312:
          .byte     0x50
.LDBG313:
          .int      .LDBG97-.LDBG0
          .int      .LDBG98-.LDBG0
          .short    .LDBG315-.LDBG314
.LDBG314:
          .byte     0x50
.LDBG315:
          .int      .LDBG25-.LDBG0
          .int      .LDBG26-.LDBG0
          .short    .LDBG317-.LDBG316
.LDBG316:
          .byte     0x50
.LDBG317:
          .int      .LDBG95-.LDBG0
          .int      .LDBG96-.LDBG0
          .short    .LDBG319-.LDBG318
.LDBG318:
          .byte     0x50
.LDBG319:
          .int      .LDBG23-.LDBG0
          .int      .LDBG24-.LDBG0
          .short    .LDBG321-.LDBG320
.LDBG320:
          .byte     0x50
.LDBG321:
          .int      .LDBG21-.LDBG0
          .int      .LDBG22-.LDBG0
          .short    .LDBG323-.LDBG322
.LDBG322:
          .byte     0x50
.LDBG323:
          .int      .LDBG93-.LDBG0
          .int      .LDBG94-.LDBG0
          .short    .LDBG325-.LDBG324
.LDBG324:
          .byte     0x50
.LDBG325:
          .int      .LDBG19-.LDBG0
          .int      .LDBG20-.LDBG0
          .short    .LDBG327-.LDBG326
.LDBG326:
          .byte     0x50
.LDBG327:
          .int      .LDBG17-.LDBG0
          .int      .LDBG18-.LDBG0
          .short    .LDBG329-.LDBG328
.LDBG328:
          .byte     0x50
.LDBG329:
          .int      .LDBG91-.LDBG0
          .int      .LDBG92-.LDBG0
          .short    .LDBG331-.LDBG330
.LDBG330:
          .byte     0x50
.LDBG331:
          .int      .LDBG15-.LDBG0
          .int      .LDBG16-.LDBG0
          .short    .LDBG333-.LDBG332
.LDBG332:
          .byte     0x50
.LDBG333:
          .int      .LDBG13-.LDBG0
          .int      .LDBG14-.LDBG0
          .short    .LDBG335-.LDBG334
.LDBG334:
          .byte     0x50
.LDBG335:
          .int      .LDBG89-.LDBG0
          .int      .LDBG90-.LDBG0
          .short    .LDBG337-.LDBG336
.LDBG336:
          .byte     0x50
.LDBG337:
          .int      .LDBG11-.LDBG0
          .int      .LDBG12-.LDBG0
          .short    .LDBG339-.LDBG338
.LDBG338:
          .byte     0x50
.LDBG339:
          .int      .LDBG9-.LDBG0
          .int      .LDBG10-.LDBG0
          .short    .LDBG341-.LDBG340
.LDBG340:
          .byte     0x50
.LDBG341:
          .int      .LDBG87-.LDBG0
          .int      .LDBG88-.LDBG0
          .short    .LDBG343-.LDBG342
.LDBG342:
          .byte     0x50
.LDBG343:
          .int      .LDBG169-.LDBG0
          .int      .LDBG170-.LDBG0
          .short    .LDBG345-.LDBG344
.LDBG344:
          .byte     0x50
.LDBG345:
          .int      .LDBG167-.LDBG0
          .int      .LDBG168-.LDBG0
          .short    .LDBG347-.LDBG346
.LDBG346:
          .byte     0x50
.LDBG347:
          .int      .LDBG165-.LDBG0
          .int      .LDBG166-.LDBG0
          .short    .LDBG349-.LDBG348
.LDBG348:
          .byte     0x50
.LDBG349:
          .int      .LDBG163-.LDBG0
          .int      .LDBG164-.LDBG0
          .short    .LDBG351-.LDBG350
.LDBG350:
          .byte     0x50
.LDBG351:
          .int      .LDBG161-.LDBG0
          .int      .LDBG162-.LDBG0
          .short    .LDBG353-.LDBG352
.LDBG352:
          .byte     0x50
.LDBG353:
          .int      .LDBG159-.LDBG0
          .int      .LDBG160-.LDBG0
          .short    .LDBG355-.LDBG354
.LDBG354:
          .byte     0x50
.LDBG355:
          .int      .LDBG157-.LDBG0
          .int      .LDBG158-.LDBG0
          .short    .LDBG357-.LDBG356
.LDBG356:
          .byte     0x50
.LDBG357:
          .int      .LDBG155-.LDBG0
          .int      .LDBG156-.LDBG0
          .short    .LDBG359-.LDBG358
.LDBG358:
          .byte     0x50
.LDBG359:
          .int      .LDBG153-.LDBG0
          .int      .LDBG154-.LDBG0
          .short    .LDBG361-.LDBG360
.LDBG360:
          .byte     0x50
.LDBG361:
          .int      .LDBG151-.LDBG0
          .int      .LDBG152-.LDBG0
          .short    .LDBG363-.LDBG362
.LDBG362:
          .byte     0x50
.LDBG363:
          .int      .LDBG149-.LDBG0
          .int      .LDBG150-.LDBG0
          .short    .LDBG365-.LDBG364
.LDBG364:
          .byte     0x50
.LDBG365:
          .int      .LDBG147-.LDBG0
          .int      .LDBG148-.LDBG0
          .short    .LDBG367-.LDBG366
.LDBG366:
          .byte     0x50
.LDBG367:
          .int      .LDBG145-.LDBG0
          .int      .LDBG146-.LDBG0
          .short    .LDBG369-.LDBG368
.LDBG368:
          .byte     0x50
.LDBG369:
          .int      .LDBG143-.LDBG0
          .int      .LDBG144-.LDBG0
          .short    .LDBG371-.LDBG370
.LDBG370:
          .byte     0x50
.LDBG371:
          .int      .LDBG141-.LDBG0
          .int      .LDBG142-.LDBG0
          .short    .LDBG373-.LDBG372
.LDBG372:
          .byte     0x50
.LDBG373:
          .int      .LDBG139-.LDBG0
          .int      .LDBG140-.LDBG0
          .short    .LDBG375-.LDBG374
.LDBG374:
          .byte     0x50
.LDBG375:
          .int      .LDBG137-.LDBG0
          .int      .LDBG138-.LDBG0
          .short    .LDBG377-.LDBG376
.LDBG376:
          .byte     0x50
.LDBG377:
          .int      0x00000000
          .int      0x00000000
.cc_bottom foo.function
          .section  .debug_info,   "",    @progbits
          .uleb128  0x5
          .asciiz   "i"
          .byte     0x01
          .short    0x0002
          .short    0x0000
          .int      .LDBG199-.LDBG194
          .int      .LDBG378
          .section  .debug_loc,    "",    @progbits
.cc_top foo.function,foo
.LDBG378:
          .int      0x00000000
          .int      0x00000000
.cc_bottom foo.function
          .section  .debug_info,   "",    @progbits
          .byte     0x00
.cc_bottom foo.function
.cc_top main.function,main
.LDBG379:
          .uleb128  0x3
          .asciiz   "main"
          .byte     0x01
          .byte     0x11
          .byte     0x01
          .int      .LDBG199-.LDBG194
          .byte     0x01
          .long     .LDBG181         # low address
          .long     .LDBG191         # high address
          .byte     0x00
.cc_bottom main.function
          .byte     0x00
.LDBG196:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG381-.LDBG380
.LDBG380:
          .short    0x0002
          .long     .LDBG194         # offset in .debug_info
          .int      .LDBG196-.LDBG194
.cc_top foo.function,foo
          .int      .LDBG214-.LDBG194
          .asciiz   "foo"
.cc_bottom foo.function
.cc_top main.function,main
          .int      .LDBG379-.LDBG194
          .asciiz   "main"
.cc_bottom main.function
          .int      0x00000000
.LDBG381:
          .section  .debug_abbrev, "",    @progbits
.LDBG193:
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
          .uleb128  0x5
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

          .byte     0x00
          .section .xtacalltable,       "", @progbits
.L905:
          .int      .L906-.L905
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll"
.cc_top main.function, main
          .asciiz  "test.xc"
          .int      0x00000012
          .long    .L904
.cc_bottom main.function
.L906:
          .section .xtalabeltable,       "", @progbits
.L907:
          .int      .L908-.L907
          .int      0x00000000
          .asciiz   "/home/andrew/sandbox/software/verif_tests_sw/Tools/xta/CodeSequences/loop_unroll"
.cc_top main.function, main
          .asciiz  "test.xc"
          .int      0x00000013
          .int      0x00000013
# line info for line 19 
          .long    .L903
          .asciiz  "test.xc"
          .int      0x00000012
          .int      0x00000012
# line info for line 18 
          .long    .L897
.cc_bottom main.function
.cc_top foo.function, foo
          .asciiz  "test.xc"
          .int      0x0000000f
          .int      0x0000000f
# line info for line 15 
          .long    .L894
          .asciiz  "test.xc"
          .int      0x0000000e
          .int      0x0000000e
# line info for line 14 
          .long    .L109
          .asciiz  "test.xc"
          .int      0x0000000b
          .int      0x0000000b
# line info for line 11 
          .long    .L89
          .asciiz  "test.xc"
          .int      0x00000006
          .int      0x00000006
# line info for line 6 
          .long    .L59
          .asciiz  "test.xc"
          .int      0x00000006
          .int      0x00000006
# line info for line 6 
          .long    .L61
          .asciiz  "test.xc"
          .int      0x00000005
          .int      0x00000005
# line info for line 5 
          .long    .L44
          .asciiz  "test.xc"
          .int      0x00000005
          .int      0x00000005
# line info for line 5 
          .long    .L46
          .asciiz  "test.xc"
          .int      0x00000004
          .int      0x00000004
# line info for line 4 
          .long    .L11
.cc_bottom foo.function
.L908:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
