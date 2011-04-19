// These coefficients are scaled to allow headroom in the result.
// They are in 2q30 format.

#define stage1_NUM_COEFF 8
#define stage1_OVERSAMPLING_RATE 1

/*
Original auto generated code
int stage1FirCoeff[stage1_NUM_COEFF] = {
    0x01b1785b ,
    0x05ec7b47 ,
    0x0bd87eed ,
    0x10497a31 ,
    0x10497a31 ,
    0x0bd87eed ,
    0x05ec7b47 ,
    0x01b1785b
};
*/
// Implemented as defines
#define stage1FirCoeff0 0x01b1785b
#define stage1FirCoeff1 0x05ec7b47
#define stage1FirCoeff2 0x0bd87eed
#define stage1FirCoeff3 0x10497a31
#define stage1FirCoeff4 0x10497a31
#define stage1FirCoeff5 0x0bd87eed
#define stage1FirCoeff6 0x05ec7b47
#define stage1FirCoeff7 0x01b1785b

#define DC_OFFSET ((stage1FirCoeff0 >> 2) + (stage1FirCoeff1 >> 2) + (stage1FirCoeff2 >> 2) + (stage1FirCoeff3 >> 2) + (stage1FirCoeff4 >> 2) + (stage1FirCoeff5 >> 2) + (stage1FirCoeff6 >> 2) + (stage1FirCoeff7 >> 2))

// With inputs in the range [-1,+1], the maximum output with these coefficients is:
//        1.1171783218965730e+00
//
// Note that this not scaled, so 1.0 corresponds to 1q31
// Note also that both the positive and negative values can occur

