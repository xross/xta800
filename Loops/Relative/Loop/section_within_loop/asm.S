.type f, @function
.globl f
f:
    entsp 0

.xtabranch target1, target2, target3, target4
    bau r0
    
target1:
    ldc r0, 1
    bu complete

target2:
    ldc r0, 1
    ldc r0, 1
    bu complete

target3:
    ldc r0, 1
    ldc r0, 1
    ldc r0, 1
    bu complete

target4:
    ldc r0, 1
    ldc r0, 1
    ldc r0, 1
    ldc r0, 1
    bu complete

complete:
    retsp 0

