#ifndef PWMDEFINES_H_
#define PWMDEFINES_H_

#define DEBUG S1

#define RAW_SAMPLE_FREQ    48000
#define REF_CLOCK_FREQ 100000000 
#define RAW_SAMPLE_PERIOD  (REF_CLOCK_FREQ / RAW_SAMPLE_FREQ) // Number of 100MHz cycles 


#endif /*PWMDEFINES_H_*/
