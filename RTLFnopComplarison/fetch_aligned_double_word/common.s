.text

_exit:
_done:
  ldc r0, 7
  ldc r1, 0
  ldc r2, 0
  ldc r3, 0
  bl _DoSyscall
  clrsr 1
  waiteu

_DoSyscall:
  retsp 0

