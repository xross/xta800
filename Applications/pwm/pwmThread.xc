// This thread implements the PWM
// It re

#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "pwm.h"
#include "pwmDefines.h"

//out port speaker = XS1_PORT_1K; // Real Speaker
out port speaker = XS1_PORT_8D; // LED [7:4]

////////////////////////////////////

// Adjust the modulation depth
#define MODULATION_DEPTH_PC         50 // Percentage
#define MODULATION_DEPTH_MUL_ALIGNMENT_BITS   24
#define MODULATION_DEPTH_MULTIPLIER             ((1<<MODULATION_DEPTH_MUL_ALIGNMENT_BITS)*MODULATION_DEPTH_PC/100)
#define UNSIGNED_HALF_RAIL  (1<<31)

#define PWM_TIME_SAMPLE_TIME_RATIO  5
#define S2_SAMPLE_PERIOD_SAMPTIME   RAW_SAMPLE_PERIOD*20/20  // Raw_sample_period(10ns cycles)*20(to get to 0.5ns cycles)/20(oversampling ratio)

#define MIN_FET_ON_NS       600
#define MIN_FET_ON_STEPS    ((MIN_FET_ON_NS*2)/5) // pwm code works in 2.5ns steps
#define MIN_FET_ON_PT       (MIN_FET_ON_NS/10)    // port time works in 10ns steps



void pwmThread (chanend s2SampleChan){

  int pwmTime = 0;      // 2.5ns steps.  Time at current point of analysis
  int s2Sample;         // 5q27
  // SampleTime has 0.5ns granularity.  This allows an integer relationship (5) between SampleTime and pwmTime
  int s2SampleTime = 0; // 0.5ns steps.  The time that s2Sample corresponds to
  int portTime;         // 10ns steps.  Used to schedule times to toggle the pwm signal
  int portTimeInc;      // 10ns steps.  Time to increment to port time.
  unsigned downFracError = 0;

  // Get initial sample
  s2SampleChan :> s2Sample;
  s2SampleTime = 0;

  // Drive a 1 on the port, and get the port time
  // This is the expected state at the start of the pwm loop
//%  speaker <: 1 @ portTime;
  speaker <: 0xf0 @ portTime;

  while (1) {

    // Need to offset the sample to be unsigned
    int      offsetHi = (1 << 19); // The signed mac output is in 13q19 format
    unsigned offsetLo = 0;
    int      s2SampleConditionedHi;
    unsigned s2SampleConditionedLo;
    unsigned s2SampleConditioned;

    unsigned upTimeStep;            // 2.5ns.       Duration that the PWM was up for
    unsigned downTimeStepWhole;     // 2.5ns.       Duration that the PWM was up for
    unsigned cycleDuration;         // This is the total pwm cycle duration (upStepTime+downStepTime)
                                    // 2.5ns time steps. [0, 1<<12-1] 2 bits more than minStep for maximum modulation depth
    unsigned cycleDurationZ;        // upStep left shifted to the MSB
    unsigned cycleDurationD ;       // Scale to give exact whole num for crossing time

    unsigned cycleDurationFrac ;    // The Fractional part of the cycle
    unsigned cycleDurationFracZ;
    unsigned cycleDurationFracD;

    unsigned maxFrac=0;             // Just to keep track incase scaling assumptions are invalid
    unsigned downStepWhole;


    ////////////////////////////
    // Move time on
    ////////////////////////////
    // pwm must be '1' for a minimum time. so use the sample from that time
    pwmTime += MIN_FET_ON_STEPS;

    // See if we need to move onto the next sample
    while (pwmTime*PWM_TIME_SAMPLE_TIME_RATIO >= s2SampleTime + S2_SAMPLE_PERIOD_SAMPTIME) {
      #pragma xta label "pwmThreadLoop1"
      // Get next sample
      s2SampleChan :> s2Sample;
      // Increment sample time & sample number
      s2SampleTime += S2_SAMPLE_PERIOD_SAMPTIME;
    }

    ////////////////////////////
    // Determine pwm Hi duration
    ////////////////////////////
    if (s2Sample < 0){
      #pragma xta label "pwmThreadHiDuration1"

      // Hi for minumum duration
      upTimeStep = MIN_FET_ON_STEPS;
      portTime += MIN_FET_ON_PT;
      // Schedule low time

      #pragma xta endpoint "pwmThreadLow1"
      speaker @ portTime <: 0;

    } else {
      #pragma xta label "pwmThreadHiDuration2"

      // Hi for longer than minumum (work out how long)
      // Modify sample by modulation depth & unsigned offset
      unsigned upStepDividend ;
      unsigned upStepDivisor  ;

      // s2Sample is 5q27, but s2SampleConditioned should be unsigned with a FS signal using 22bits
      // s2Sample 07ff_ffff maps to 003F_FFFF (for 100% modulation depth)
      // s2Sample 0400_0000 maps to 0030_0000 (for 100% modulation depth)
      // s2Sample         0 maps to 0020_0000 (for 100% modulation depth)
      // s2Sample fc00_0000 maps to 0010_0000 (for 100% modulation depth)
      // s2Sample f800_0000 maps to 0000_0000 (for 100% modulation depth)
      {s2SampleConditionedHi, s2SampleConditionedLo} = macs (s2Sample, MODULATION_DEPTH_MULTIPLIER, offsetHi, offsetLo);
      s2SampleConditioned = ((unsigned) s2SampleConditionedHi << 2)
                           + (unsigned)(s2SampleConditionedLo >> (32-2));

      // MIN_FET_ON_STEPS must be less than 10 bits
      upStepDividend = MIN_FET_ON_STEPS * s2SampleConditioned;
      upStepDivisor = (1<<22)- s2SampleConditioned;
      upTimeStep = upStepDividend / upStepDivisor;

      // Now we know how long the up time will be, schecdule the start of the low time
      portTimeInc = upTimeStep >> 2;
      portTime += portTimeInc;

      #pragma xta endpoint "pwmThreadLow2"
      speaker @ portTime <: 0;

      /////////////////
      // We need to take a new sample, valid at the end of the Hi cycle
      // Add HiTime to pwmTime (*4 because of conversion from 10ns to 2.5ns steps; -MIN_FET_ON_STEPS because that was previously added)
      pwmTime += upTimeStep - MIN_FET_ON_STEPS;
      // See if we need to move onto the next sample
      while (pwmTime*PWM_TIME_SAMPLE_TIME_RATIO >= s2SampleTime + S2_SAMPLE_PERIOD_SAMPTIME) {
        #pragma xta label "pwmThreadLoop2"
        // Get next sample
        s2SampleChan :> s2Sample;
        // Increment sample time & sample number
        s2SampleTime += S2_SAMPLE_PERIOD_SAMPTIME;
      }
    }

    ////////////////////
    // Now calculate the full cycle time (possibly with a new sample)
    //
    // First condition the latest sample
//    s2Sample= 0x07ffffff;
//    s2Sample= 0xf8000000;

    {s2SampleConditionedHi, s2SampleConditionedLo} = macs (s2Sample, MODULATION_DEPTH_MULTIPLIER, offsetHi, offsetLo);
    s2SampleConditioned = ((unsigned) s2SampleConditionedHi << 2)
                           + (unsigned)(s2SampleConditionedLo >> (32-2));

    cycleDurationZ = (upTimeStep << 20); // upStep left shifted to the MSB
    cycleDurationD  = s2SampleConditioned >> 2; //11; // Scale to give exact whole num for crossing time
    cycleDuration = cycleDurationZ/cycleDurationD;

    cycleDurationFracZ = (cycleDurationZ - (cycleDuration*cycleDurationD));
    cycleDurationFracD = s2SampleConditioned >> 7; //16; // More accurte this time,
    cycleDurationFrac  = (cycleDurationFracZ << 11)/cycleDurationFracD; // [15:0]

    // Find maximum error
    if (cycleDurationFrac > maxFrac)
        maxFrac = cycleDurationFrac;

    downTimeStepWhole = cycleDuration - upTimeStep;
    downFracError += cycleDurationFrac;

    // If the error gets significant, add another cycle
    if (downFracError >= 0x10000) {
//      printstr ("* ");
        downFracError -= 0x10000;
        downTimeStepWhole++;
    }

    // Move time on (but don't update the sample yet.
    // We'll do that at the start of the next cycle
    pwmTime += downTimeStepWhole;

    portTimeInc = downTimeStepWhole >> 2;
    portTime += portTimeInc;
//%    speaker @ portTime <: 1;
      
    #pragma xta endpoint "pwmThreadHigh"
    speaker @ portTime <: 0xf0;

/*
    printhex (pwmTime);
    printstr (" ");
    printhex (s2Sample);
    printstr (" ");
      printhex (s2SampleConditioned);
      printstr (" ");
      printhex (upTimeStep);
      printstr (" ");
      printhex (downTimeStepWhole);
      printstr (" ");
      printhex (downFracError);
      printstr (" ");
      printhex (cycleDuration);
      printstr (" ");
      printstrln (" ");
*/

  }


/*


*/

}
