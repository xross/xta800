          .file     "test.xc"
          .text
          .align    2

.LDBG0:
# Thread names for recovering thread graph in linker
.globl a, "si"
.type  a, @object
          .section .dp.data,       "adw", @progbits
          .align    4
a:
          .int      0x00000002
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
          .text
.LDBG4:
.globread main, a, "test.xc:5: error: previously used here"
.globl main, "f{si}(0)"
.globl main.nstackwords
.globl main.maxthreads
.globl main.maxtimers
.globl main.maxchanends
.globl main.maxsync
.type  main, @function
.linkset main.locnoside, 1
.linkset main.locnochandec, 1
.linkset main.nstackwords, 0
.cc_top main.function,main
.LDBG14:
          .align    4
main:
.LDBG15:
.LDBG16:                             # line (3)
.LDBG5:
.LDBG17:                             # line (4)
          ldc       r0, 0x0
.LDBG7:
.LDBG18:                             # line (5)
          ldw       r1, dp[a] 
          eq        r1, r1, 0x2
          bt        r1, .L1 
.LDBG8:
.LDBG9:
          bu        .L0 
.LDBG10:
.L1:
.LDBG19:                             # line (7)
          ldc       r0, 0x2
.LDBG11:
.LDBG12:
.L0:
.LDBG20:                             # line (9)
.LDBG21:
          retsp     0x0 
.LDBG6:
.LDBG13:
.LDBG22:
.LDBG23:
          .section  .debug_frame, "",     @progbits
          .align    4
          .int      .LDBG25-.LDBG24
.LDBG24:
          .long     .LDBG1           # offset in .debug_frame
          .int      .LDBG14
          .int      .LDBG23-.LDBG14
          .byte     0x01
          .int      .LDBG15
          .byte     0x01
          .int      .LDBG21
          .byte     0x0a
          .byte     0x01
          .int      .LDBG22
          .byte     0x0b
          .align    4, 0
.LDBG25:
.cc_bottom main.function
.linkset main.maxchanends, 0
.linkset main.maxtimers, 0
.linkset main.maxthreads, 1
          .text
.LDBG26:
.LDBG27:
          .section  .debug_info,   "",    @progbits
.LDBG30:
          .int      .LDBG32-.LDBG31
.LDBG31:
          .short    0x0003
          .long     .LDBG28          # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG27          # high address
          .asciiz   "test.xc"
          .asciiz   "ace.xmos.local:/home/kris/Views/Tools/verif_tests_sw/Tools/xta/ConsoleCommands/asm"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG29          # offset in .debug_lineprog
.LDBG33:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG34:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG35:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG36:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG37:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG38:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG39:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x05
          .byte     0x01
.LDBG40:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x07
          .byte     0x01
          .uleb128  0x3
          .asciiz   "a"
          .byte     0x01
          .short    0x0001
          .int      .LDBG35-.LDBG30
          .short    .LDBG42-.LDBG41
.LDBG41:
          .byte     0x03
          .int      a
.LDBG42:
.LDBG43:
          .uleb128  0x4
          .asciiz   "main"
          .byte     0x01
          .byte     0x03
          .byte     0x01
          .int      .LDBG35-.LDBG30
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG26          # high address
          .uleb128  0x5
          .asciiz   "b"
          .byte     0x01
          .short    0x0004
          .short    0x0000
          .int      .LDBG35-.LDBG30
          .int      .LDBG44
          .section  .debug_loc,    "",    @progbits
.LDBG44:
          .int      .LDBG11-.LDBG0
          .int      .LDBG11-.LDBG0
          .short    .LDBG46-.LDBG45
.LDBG45:
          .byte     0x50
.LDBG46:
          .int      .LDBG12-.LDBG0
          .int      .LDBG13-.LDBG0
          .short    .LDBG48-.LDBG47
.LDBG47:
          .byte     0x50
.LDBG48:
          .int      .LDBG9-.LDBG0
          .int      .LDBG10-.LDBG0
          .short    .LDBG50-.LDBG49
.LDBG49:
          .byte     0x50
.LDBG50:
          .int      .LDBG7-.LDBG0
          .int      .LDBG8-.LDBG0
          .short    .LDBG52-.LDBG51
.LDBG51:
          .byte     0x50
.LDBG52:
          .int      0x00000000
          .int      0x00000000
          .section  .debug_info,   "",    @progbits
          .byte     0x00
          .byte     0x00
.LDBG32:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG54-.LDBG53
.LDBG53:
          .short    0x0002
          .long     .LDBG30          # offset in .debug_info
          .int      .LDBG32-.LDBG30
          .int      .LDBG43-.LDBG30
          .asciiz   "main"
          .int      0x00000000
.LDBG54:
          .section  .debug_line,   "",    @progbits
.LDBG29:
          .int      .LDBG56-.LDBG55
.LDBG55:
          .short    0x0003
          .int      .LDBG58-.LDBG57
.LDBG57:
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
          .asciiz   "test.xc"
          .uleb128  0x0
          .uleb128  0x48c78ed9
          .uleb128  0x55
          .byte     0x00
.LDBG58:
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG16
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG17
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG18
          .byte     0x12             # special opcode (line + 1)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG19
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG20
          .byte     0x13             # special opcode (line + 2)
          .byte     0x00
          .uleb128  0x1
          .byte     0x01
.LDBG56:
          .section  .debug_abbrev, "",    @progbits
.LDBG28:
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
          .section .dp.data,       "adw", @progbits
.align 4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVA"
