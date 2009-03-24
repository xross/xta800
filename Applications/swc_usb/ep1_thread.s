          .file     "./tests/audio/src/ep1_thread.xc"
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
          .text
.LDBG4:
.globl ep1_thread, "f{0}(0)"
.globl ep1_thread.nstackwords
.globl ep1_thread.maxthreads
.globl ep1_thread.maxtimers
.globl ep1_thread.maxchanends
.globl ep1_thread.maxsync
.type  ep1_thread, @function
.linkset ep1_thread.locnoside, 1
.linkset ep1_thread.locnochandec, 1
.linkset ep1_thread.nstackwords, 0
.cc_top ep1_thread.function,ep1_thread
.LDBG8:
.LDBG9:                              # line (3)
          .align    4
ep1_thread:
.LDBG10:
.L5:
.LDBG11:                             # line (3)
.LDBG12:                             # line (4)
.LDBG5:
.L4:
.L6:
.LDBG13:                             # line (4)
          bu        .L4 
.LDBG6:
.L7:
.LDBG14:                             # line (5)
.LDBG7:
.cc_bottom ep1_thread.function
.LDBG15:
          .section  .debug_frame, "",     @progbits
.cc_top ep1_thread.function,ep1_thread
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
.cc_bottom ep1_thread.function
.linkset ep1_thread.maxchanends, 0
.linkset ep1_thread.maxtimers, 0
.linkset ep1_thread.maxthreads, 1
          .text
.LDBG18:
# Thread names for recovering thread graph in linker
.LDBG19:
          .section  .debug_info,   "",    @progbits
.LDBG22:
          .int      .LDBG24-.LDBG23
.LDBG23:
          .short    0x0003
          .long     .LDBG20          # offset in .debug_abbrev
          .byte     0x04
          .uleb128  0x1
          .long     .LDBG0           # low address
          .long     .LDBG19          # high address
          .asciiz   "./tests/audio/src/ep1_thread.xc"
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
          .short    0xc000
          .asciiz   "XMOS Dwarf Symbolic Debug Generator"
          .long     .LDBG21          # offset in .debug_lineprog
.LDBG25:
          .uleb128  0x2
          .asciiz   "long"
          .byte     0x05
          .byte     0x04
.LDBG26:
          .uleb128  0x2
          .asciiz   "unsigned long"
          .byte     0x07
          .byte     0x04
.LDBG27:
          .uleb128  0x2
          .asciiz   "int"
          .byte     0x05
          .byte     0x04
.LDBG28:
          .uleb128  0x2
          .asciiz   "unsigned int"
          .byte     0x07
          .byte     0x04
.LDBG29:
          .uleb128  0x2
          .asciiz   "short"
          .byte     0x05
          .byte     0x02
.LDBG30:
          .uleb128  0x2
          .asciiz   "unsigned short"
          .byte     0x07
          .byte     0x02
.LDBG31:
          .uleb128  0x2
          .asciiz   "char"
          .byte     0x06
          .byte     0x01
.LDBG32:
          .uleb128  0x2
          .asciiz   "unsigned char"
          .byte     0x08
          .byte     0x01
.LDBG33:
          .uleb128  0x2
          .asciiz   "chanend"
          .byte     0x07
          .byte     0x04
.LDBG34:
          .uleb128  0x2
          .asciiz   "timer"
          .byte     0x07
          .byte     0x04
.LDBG35:
          .uleb128  0x2
          .asciiz   "clock"
          .byte     0x07
          .byte     0x04
.LDBG36:
          .uleb128  0x2
          .asciiz   "port"
          .byte     0x07
          .byte     0x04
.LDBG37:
          .uleb128  0x2
          .asciiz   "buffered port:1"
          .byte     0x07
          .byte     0x04
.LDBG38:
          .uleb128  0x2
          .asciiz   "buffered port:4"
          .byte     0x07
          .byte     0x04
.LDBG39:
          .uleb128  0x2
          .asciiz   "buffered port:8"
          .byte     0x07
          .byte     0x04
.LDBG40:
          .uleb128  0x2
          .asciiz   "buffered port:16"
          .byte     0x07
          .byte     0x04
.LDBG41:
          .uleb128  0x2
          .asciiz   "buffered port:32"
          .byte     0x07
          .byte     0x04
.cc_top ep1_thread.function,ep1_thread
.LDBG42:
          .uleb128  0x3
          .asciiz   "ep1_thread"
          .byte     0x01
          .byte     0x03
          .byte     0x01
          .byte     0x01
          .long     .LDBG4           # low address
          .long     .LDBG18          # high address
          .byte     0x00
.cc_bottom ep1_thread.function
          .byte     0x00
.LDBG24:
          .section  .debug_pubnames, "",  @progbits
          .int      .LDBG44-.LDBG43
.LDBG43:
          .short    0x0002
          .long     .LDBG22          # offset in .debug_info
          .int      .LDBG24-.LDBG22
.cc_top ep1_thread.function,ep1_thread
          .int      .LDBG42-.LDBG22
          .asciiz   "ep1_thread"
.cc_bottom ep1_thread.function
          .int      0x00000000
.LDBG44:
          .section  .debug_line,   "",    @progbits
.LDBG21:
          .int      .LDBG46-.LDBG45
.LDBG45:
          .short    0x0003
          .int      .LDBG48-.LDBG47
.LDBG47:
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
          .asciiz   "./tests/audio/src/ep1_thread.xc"
          .uleb128  0x0
          .uleb128  0x49b91aba
          .uleb128  0x23
          .byte     0x00
.LDBG48:
.cc_top ep1_thread.function, ep1_thread
          .byte     0x04
          .sleb128  0x1
          .byte     0x00
          .sleb128  0x5
          .byte     0x02
          .int      .LDBG9
          .byte     0x13             # special opcode (line + 2)
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
.cc_bottom ep1_thread.function
.LDBG46:
          .section  .debug_abbrev, "",    @progbits
.LDBG20:
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

          .byte     0x00
          .section .xtalabeltable,       "", @progbits
.L8:
          .int      .L9-.L8
          .int      0x00000000
          .asciiz   "/home/andrew-local/software/sandbox2/swc_usb"
.cc_top ep1_thread.function, ep1_thread
          .asciiz  "./tests/audio/src/ep1_thread.xc"
          .int      0x00000005
          .int      0x00000005
          .long    .L7
          .asciiz  "./tests/audio/src/ep1_thread.xc"
          .int      0x00000004
          .int      0x00000004
          .long    .L6
          .asciiz  "./tests/audio/src/ep1_thread.xc"
          .int      0x00000003
          .int      0x00000004
          .long    .L5
.cc_bottom ep1_thread.function
.L9:
          .section .dp.data,       "adw", @progbits
.align 4
          .align    4
          .section .dp.bss,        "adw", @nobits
.align 4
          .ident    "XMOS 32-bit XC Compiler This is an unreleased/unsupported version. (build xxxx)"
          .core     "XS1"
          .corerev  "REVB"
