.text

.type function, @function
.globl function
function:
  ldc r0, 0xabcd
  ldc r1, 0xbdca
  andnot r0, r1
  ldc r1, 0x205
  sub r0, r0, r1
  bf r0, true
  ldc r0, 0
  bu done
  true:
  ldc r0, 1
done:
  retsp 0
