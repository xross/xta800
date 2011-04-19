.type test, @function
.globl test
test:
source1:
.xtabranch target1
  bau r1
  
source2:
.xtabranch target1
  bau r1

target1:
  retsp 0

target2:
  retsp 0

target3:
  retsp 0

target4:
  retsp 0
