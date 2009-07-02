#include <platform.h>
#include <xs1.h>
#include <print.h>
#include "pwm.h"
#include "pwmDefines.h"

extern int sineWave[480];

void rawSampleThread (chanend rawSampleChan) {
  timer sampleTimer;
  int   sampleCount = 0;
  int   sampleTime;
  
  #pragma xta label "begin"
  sampleTimer :> sampleTime;
  while (1) {
    for (sampleCount = 0 ; sampleCount <480; sampleCount++){

      // Send sample
      #pragma xta endpoint "sendRawSample"
      rawSampleChan <: sineWave[sampleCount];

      // Wait for next sample time
      sampleTimer when timerafter (sampleTime + RAW_SAMPLE_PERIOD):> sampleTime;
      
      sampleCount++;
      if (sampleCount == 480) {
        sampleCount = 0;
      }
    }
  }

  #pragma xta label "end"
  return;
}
