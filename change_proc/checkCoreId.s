  .globl  getCoreId, "f{ui}(0)"

  .globl getCoreId.maxthreads
  .linkset getCoreId.maxthreads, 1

  .globl getCoreId.maxtimers
  .linkset getCoreId.maxtimers, 0

  .globl getCoreId.maxchanends
  .linkset getCoreId.maxchanends, 0

  .globl getCoreId.nstackwords
  .linkset getCoreId.nstackwords, 0

.text
  .align  2
getCoreId:
  ldc   r1, 0x30b
  get   r0, ps[r1]
  shr   r0, r0, 16
  ldc   r1, 0xff
  and   r0, r0, r1
  retsp 0

