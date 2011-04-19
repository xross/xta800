// This thread implements an interpolation filter, which
// upsamples by a factor of 5 from a rate of 192000Hz to 960000Hz

#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "pwm.h"
#include "pwmDefines.h"

#include "firFix_960000_-96.h" // defines coefficient values

// The number of samples required to interpolate using that filter
//#define s2_SAMPLE_TABLE_SIZE  ((int)(floor (s2_NUM_COEFF/s2_OVERSAMPLING_RATE + s2_OVERSAMPLING_RATE-1)))
#define s2_SAMPLE_TABLE_SIZE  6

void interpolateStage2Thread (chanend s1SampleChan, chanend s2SampleChan) {

  // Table of the input samples
  // This table should be initialized to 0 before using
  int s2InputSamples [s2_SAMPLE_TABLE_SIZE]; // 1q31 48000

  int s2SampleHeadPtr; // Points to the location which has the latest sample in it


  // Initialise sample table
  for (s2SampleHeadPtr = 0; s2SampleHeadPtr < s2_SAMPLE_TABLE_SIZE; s2SampleHeadPtr++) {
    s2InputSamples[s2SampleHeadPtr] = 0;
  }
  s2SampleHeadPtr = 0;

  // Main loop
  // 1. Get new 192000 sample
  // 2a. Loop around interpolation cycle 5 times
  //   2a.1 Get new 960000 sample
  //   2a.2 Store in sample table

  while (1) {
    int interpolationSampleNo;

    // Move onto the next space in the sample table

    if (s2SampleHeadPtr == (s2_SAMPLE_TABLE_SIZE-1)) {
      s2SampleHeadPtr = 0;
    } else {
      s2SampleHeadPtr ++;
    }


    // Get new 192000 sample
    #pragma xta endpoint "interS2ThreadInput"
    s1SampleChan :> s2InputSamples[s2SampleHeadPtr];

#if DEBUG == s2
//  printstr ("    S2 Input sample: ");
//  printhexln (s2InputSamples[s2SampleHeadPtr]);
#endif

    for (interpolationSampleNo = 0; interpolationSampleNo < s2_OVERSAMPLING_RATE ; interpolationSampleNo++) {
      #pragma xta label "interS2ThreadOuterLoop"       

      // First active (non-zero) sample
      int    sampleIndex = s2SampleHeadPtr;
      int    coeffNo;
      // filterSum[63:0] is split into Hi and Lo words.
      // filterSum is 9q55 = 4q28 (coeff) * 5q27 (sample)
      int           filterSumHi = 0; // [63:32] of the filter sum
      unsigned int  filterSumLo = 0; // [31: 0] of the filter sum

      int    interpolatedSample;     // 5q27 (scaled from interpolationSample)

      // Interpolating filter inserts extra 0s as extra samples.
      // This is the same as not adding those taps in.
      // So the 192000 taps are swept across the coefficients missing many of them
      for (coeffNo = interpolationSampleNo; coeffNo < s2_NUM_COEFF; coeffNo += s2_OVERSAMPLING_RATE) {
        #pragma xta label "interS2ThreadInnerLoop"

        // Perform MAC for the tap.
        {filterSumHi, filterSumLo} = macs(s2FirCoeff[coeffNo], s2InputSamples[sampleIndex], filterSumHi, filterSumLo);
        if (sampleIndex == 0 ) {
            sampleIndex = s2_SAMPLE_TABLE_SIZE-1;
        } else {
            sampleIndex --;
        }
      }

#if DEBUG == s2
//  printstr ("        S2 Output sample: ");
//  printhexln (filterSumHi);
#endif
      // filterSum is 9q55
      // lose some headroom & issue sample in 5q27 format
      interpolatedSample = (filterSumHi << 4) + (filterSumLo >> 28);
      
      #pragma xta endpoint "interS2ThreadOutput"
      s2SampleChan <: interpolatedSample;
    }

  }
}
