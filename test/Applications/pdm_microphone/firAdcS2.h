// These coefficients are scaled to allow headroom in the result.
// They are in 3q29 format.

#define stage2_NUM_COEFF 15
#define stage2_OVERSAMPLING_RATE 1

int stage2FirCoeff[stage2_NUM_COEFF] = {
    0x000931be ,
    0x00516fd1 ,
    0x00db3e3b ,
    0x01bcfb14 ,
    0x02db20e3 ,
    0x03fc368a ,
    0x04d4d00a ,
    0x05255093 ,
    0x04d4d00a ,
    0x03fc368a ,
    0x02db20e3 ,
    0x01bcfb14 ,
    0x00db3e3b ,
    0x00516fd1 ,
    0x000931be  
};


// With inputs in the range [-1,+1], the maximum output with these coefficients is:
//        1.0746256023643594e+00
//
// Note that this not scaled, so 1.0 corresponds to 1q31
// Note also that both the positive and negative values can occur

