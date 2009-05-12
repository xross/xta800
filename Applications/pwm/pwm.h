#ifndef PWM_H_
#define PWM_H_

void rawSampleThread (chanend rawSampleChan);
void interpolateStage1Thread (chanend rawSampleChan, chanend s1SampleChan);
void interpolateStage2Thread (chanend s1SampleChan,  chanend s2SampleChan);
void pwmThread (chanend s2SampleChan);

#endif /*PWM_H_*/
