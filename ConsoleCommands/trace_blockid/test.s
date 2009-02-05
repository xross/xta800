.globl a, "si"
.type  a, @object
          .section .dp.data,       "adw", @progbits
          .align    4
a:
          .int      0x00000002
.globl main, "f{si}(0)"
.type  main, @function
          .align    4
main:
          ldc       r0, 0x0
          ldw       r1, dp[a] 
          eq        r1, r1, 0x2
          bt        r1, .L1 
          bu        .L0 
.L1:
          ldc       r0, 0x2
.L0:
          retsp     0x0 
