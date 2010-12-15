// These coefficients are scaled to allow headroom in the result.
// They are in 4q28 format.

#define s2_NUM_COEFF 26
#define s2_OVERSAMPLING_RATE 5

int s2FirCoeff[s2_NUM_COEFF] = {
    0xfffbc923 ,
    0xffec1922 ,
    0xffc8289c ,
    0xff8f1298 ,
    0xff54446d ,
    0xff483800 ,
    0xffb385b6 ,
    0x00dee8d2 ,
    0x02ed846d ,
    0x05ba28e2 ,
    0x08cc8f2f ,
    0x0b735f47 ,
    0x0cfdde16 ,
    0x0cfdde16 ,
    0x0b735f47 ,
    0x08cc8f2f ,
    0x05ba28e2 ,
    0x02ed846d ,
    0x00dee8d2 ,
    0xffb385b6 ,
    0xff483800 ,
    0xff54446d ,
    0xff8f1298 ,
    0xffc8289c ,
    0xffec1922 ,
    0xfffbc923  
};


// With inputs in the range [-1,+1], the maximum output with these coefficients is:
//        5.6510068267947000e+00
//
// Note that this not scaled, so 1.0 corresponds to 1q31
// Note also that both the positive and negative values can occur

