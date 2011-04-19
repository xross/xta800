// These are the look up tables for implementing the stage1 filter.
//
// Note that since every sample is either 1 or 0 - DC_OFFSET, a LUT can be used rather than multiplying each tap/sample
#include "firAdcS1.h"

// The latest sample use Lut0 - DC_OFFSET, the previous samples use Lut1
int stage1FirLut0[16] = {
    0               + 0               + 0               + 0               - DC_OFFSET,
    0               + 0               + 0               + stage1FirCoeff0 - DC_OFFSET,
    0               + 0               + stage1FirCoeff1 + 0               - DC_OFFSET,
    0               + 0               + stage1FirCoeff1 + stage1FirCoeff0 - DC_OFFSET,

    0               + stage1FirCoeff2 + 0               + 0               - DC_OFFSET,
    0               + stage1FirCoeff2 + 0               + stage1FirCoeff0 - DC_OFFSET,
    0               + stage1FirCoeff2 + stage1FirCoeff1 + 0               - DC_OFFSET,
    0               + stage1FirCoeff2 + stage1FirCoeff1 + stage1FirCoeff0 - DC_OFFSET,

    stage1FirCoeff3 + 0               + 0               + 0               - DC_OFFSET,
    stage1FirCoeff3 + 0               + 0               + stage1FirCoeff0 - DC_OFFSET,
    stage1FirCoeff3 + 0               + stage1FirCoeff1 + 0               - DC_OFFSET,
    stage1FirCoeff3 + 0               + stage1FirCoeff1 + stage1FirCoeff0 - DC_OFFSET,

    stage1FirCoeff3 + stage1FirCoeff2 + 0               + 0               - DC_OFFSET,
    stage1FirCoeff3 + stage1FirCoeff2 + 0               + stage1FirCoeff0 - DC_OFFSET,
    stage1FirCoeff3 + stage1FirCoeff2 + stage1FirCoeff1 + 0               - DC_OFFSET,
    stage1FirCoeff3 + stage1FirCoeff2 + stage1FirCoeff1 + stage1FirCoeff0 - DC_OFFSET
};

int stage1FirLut1[16] = {
    0               + 0               + 0               + 0               - DC_OFFSET,
    0               + 0               + 0               + stage1FirCoeff4 - DC_OFFSET,
    0               + 0               + stage1FirCoeff5 + 0               - DC_OFFSET,
    0               + 0               + stage1FirCoeff5 + stage1FirCoeff4 - DC_OFFSET,

    0               + stage1FirCoeff6 + 0               + 0               - DC_OFFSET,
    0               + stage1FirCoeff6 + 0               + stage1FirCoeff4 - DC_OFFSET,
    0               + stage1FirCoeff6 + stage1FirCoeff5 + 0               - DC_OFFSET,
    0               + stage1FirCoeff6 + stage1FirCoeff5 + stage1FirCoeff4 - DC_OFFSET,

    stage1FirCoeff7 + 0               + 0               + 0               - DC_OFFSET,
    stage1FirCoeff7 + 0               + 0               + stage1FirCoeff4 - DC_OFFSET,
    stage1FirCoeff7 + 0               + stage1FirCoeff5 + 0               - DC_OFFSET,
    stage1FirCoeff7 + 0               + stage1FirCoeff5 + stage1FirCoeff4 - DC_OFFSET,

    stage1FirCoeff7 + stage1FirCoeff6 + 0               + 0               - DC_OFFSET,
    stage1FirCoeff7 + stage1FirCoeff6 + 0               + stage1FirCoeff4 - DC_OFFSET,
    stage1FirCoeff7 + stage1FirCoeff6 + stage1FirCoeff5 + 0               - DC_OFFSET,
    stage1FirCoeff7 + stage1FirCoeff6 + stage1FirCoeff5 + stage1FirCoeff4 - DC_OFFSET
};

