.text
.align 2

.cc_top func.function,func
.type func, @function
.globl func
func:

INPUT_1:
  in r0, res[r0]

  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0
  ldc r0, 0x0

INPUT_2:
  in r0, res[r0]

  retsp 0

.cc_bottom func.function

