// This thread implements an interpolation filter, which
// upsamples by a factor of 4 from a rate of 48000Hz to 192000Hz

#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "pwm.h"
#include "pwmDefines.h"

#include "firFix_192000_-96.h" // defines coefficient values

// The number of samples required to interpolate using that filter
//#define s1_SAMPLE_TABLE_SIZE  ((int)(floor (s1_NUM_COEFF/s1_OVERSAMPLING_RATE + s1_OVERSAMPLING_RATE-1)))
#define s1_SAMPLE_TABLE_SIZE  24

void interpolateStage1Thread (chanend rawSampleChan, chanend s1SampleChan) {

  // Table of the input samples
  // This table should be initialized to 0 before using
  int s1InputSamples [s1_SAMPLE_TABLE_SIZE]; // 1q31 48000

  int s1SampleHeadPtr; // Points to the location which has the latest sample in it


  // Initialise sample table
  for (s1SampleHeadPtr = 0; s1SampleHeadPtr < s1_SAMPLE_TABLE_SIZE; s1SampleHeadPtr++) {
    s1InputSamples[s1SampleHeadPtr] = 0;
  }
  s1SampleHeadPtr = 0;

  // Main loop
  // 1. Get new 48000 sample
  // 2. Loop around interpolation cycle 4 times
  //   2.1 Get new 192000 sample
  //   2.2 Issue to the stage 2 filter

  while (1) {
    int interpolationSampleNo;

    // Move onto the next space in the sample table
    
    if (s1SampleHeadPtr == (s1_SAMPLE_TABLE_SIZE-1)) {
      s1SampleHeadPtr = 0;
    } else {
      s1SampleHeadPtr ++;
    }
    
    // Get new 48000 sample
    #pragma xta endpoint "interS1ThreadInput"
    rawSampleChan :> s1InputSamples[s1SampleHeadPtr];

#if DEBUG == s1
//  printstr ("S1 Input sample: ");
//  printhexln (s1InputSamples[s1SampleHeadPtr]);
#endif

    for (interpolationSampleNo = 0; interpolationSampleNo < s1_OVERSAMPLING_RATE ; interpolationSampleNo++) {
      //#pragma xta label "interS1ThreadOuterLoop"       

      // First active (non-zero) sample
      int    sampleIndex = s1SampleHeadPtr;
      int    coeffNo;
      // filterSum[63:0] is split into Hi and Lo words.
      // filterSum is 5q59 = 4q28 (coeff) * 1q31 (sample)
      int           filterSumHi = 0; // [63:32] of the filter sum
      unsigned int  filterSumLo = 0; // [31: 0] of the filter sum

      int    interpolatedSample;     // 5q27 (scaled from interpolationSample)

      // Interpolating filter inserts extra 0s as extra samples.
      // This is the same as not adding those taps in.
      // So the 48000 taps are swept across the coefficients missing many of them
      #pragma unsafe arrays
      for (coeffNo = interpolationSampleNo; coeffNo < s1_NUM_COEFF; coeffNo += s1_OVERSAMPLING_RATE) {
        #pragma xta label "interS1ThreadInnerLoop"       
 
        // Perform MAC for the tap.
        {filterSumHi, filterSumLo} = macs(s1FirCoeff[coeffNo], s1InputSamples[sampleIndex], filterSumHi, filterSumLo);
        if (sampleIndex == 0 ) {
            sampleIndex = s1_SAMPLE_TABLE_SIZE-1;
        } else {
            sampleIndex --;
        }
      }

#if DEBUG == s1
//  printstr ("    S1 Output sample: ");
//  printhexln (filterSumHi);
#endif

      #pragma xta endpoint "interS1ThreadOutput"
      s1SampleChan <: filterSumHi;
    }

  }
}
