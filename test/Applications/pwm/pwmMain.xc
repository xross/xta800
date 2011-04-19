#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "pwm.h"
#include "pwmDefines.h"

int main(void) {
//  streaming chan   rawSampleChan;     // Send 32bit samples at  48000
//  streaming chan   s1SampleChan;      // Send 32bit samples at 192000
//  streaming chan   s2SampleChan;      // Send 32bit samples at 960000
  chan   rawSampleChan;     // Send 32bit samples at  48000
  chan   s1SampleChan;      // Send 32bit samples at 192000
  chan   s2SampleChan;      // Send 32bit samples at 960000

  // printstr ("Hello from the start of main\n");

    par {
      // thread provides samples down the channel
      rawSampleThread (rawSampleChan);
      // Interpolate samples
      interpolateStage1Thread (rawSampleChan, s1SampleChan);
      interpolateStage2Thread (s1SampleChan,  s2SampleChan);
      pwmThread (s2SampleChan);
    }
  // printstr ("Hello from the end   of main\n");

  return 0;
}
