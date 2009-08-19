/////////////////////////////////////////////////////////////////////////////////////////////////
//
// mic.xc
// PDM microphone
//
// Copyright (C) 2009 XMOS Ltd.
//
// Number of threads          1
// Decimation                 yes
// Mic clock frequency        3.125MHz
// Input pulse size           8 pulses at a time
// Low-pass filtering         biquad at 16-bit (upsampled from 3-bit)
// Output sample format       signed 8-bit
// Output sample rate         390625Hz (i.e. 32 x 12.207kHz)
// Biquad cutoff frequency    5kHz

#include <xs1.h>
#include <platform.h>
#include <xclib.h>
#include <stdlib.h>
#include <print.h>
#include "firAdcS1Lut.h"
#include "firAdcS2.h"

#define VOLUME_BIT_SHIFT 4

//#define PRINT_SAMPLES

clock               b_pdm  = on stdcore[0] : XS1_CLKBLK_3;
out         port    p_pdmc = on stdcore[0] : XS1_PORT_1J;
buffered in port:32 p_pdmd = on stdcore[0] : XS1_PORT_1O;  // get the 1bit samples 32 at a time

// Microphone clock = 100MHz / 86 = 1.163MHz
// Stage1 filter runs at 1.163MHz, but is decimated to have output samples at 290kHz
// Stage2 filter runs at   290kHz, but is decimated to have output samples at 48.45kHz

#define PDM_DIV (86/2)  // 100/86 = 1.163MHz
#define stage1_DECIMATION_RATE 4
#define stage2_DECIMATION_RATE 6

#pragma unsafe arrays // this code must ensure array bounds are not violated
void pdm(streaming chanend c_pdm)
{

  int mic_sample4=0;    // last 4 mic samples
  int s1_sample[stage2_NUM_COEFF]; // 2q30 format Output samples from the s1 filter.  Enough to compute all taps
  int s1_sample_no=0;   // samples are written into s1_sample[] at this location
#define BUF_SIZE 1024
int s2_buf[BUF_SIZE] = {
                       0x40000000, 0x40000000, 0x40000000, 0x40000000,
                       0, 0, 0, 0,
                       0xc0000000, 0xc0000000, 0xc0000000, 0xc0000000,
                       0, 0, 0, 0
                       };
int s2_buf_ptr=0;


  configure_clock_rate (b_pdm, 100, 86);  // set clockblock divider to run at mic sample rate
  set_port_clock(p_pdmc, b_pdm);          // p_pdmc port uses this clock
  set_port_mode_clock(p_pdmc);            // and drives the clock out
  set_port_clock(p_pdmd, b_pdm);          // p_pdmd port uses this clock
  set_port_no_sample_delay(p_pdmd);       // Samples from Left channel valid on rising edge
  start_clock(b_pdm);

  // Introduce a > 1us delay to allow the DAC to come up
  for (int i = 0; i < 1000; i++)
  {
    p_pdmd :> mic_sample4;
    mic_sample4 &= 0xf;
  }

  while (1)
  {
    // This loop runs with enough mic samples to obtain a single decimated sample
    int mic_samples;
    // filterSum[63:0] is split into Hi and Lo words.
    // filterSum is 5q59 = 3q29 (coeff) * 2q30 (sample)
    int           filterSumHi = 0; // [63:32] of the filter sum
    unsigned int  filterSumLo = 0; // [31: 0] of the filter sum
    int           s1_sample_index;

    // get enough 1 bit samples to produce 1 more 48kHz sample
    #pragma xta endpoint "mic_sample"
    mic_samples = partin ( p_pdmd, stage1_DECIMATION_RATE*stage2_DECIMATION_RATE);

//s2_buf [s2_buf_ptr++] = mic_samples;
//if (s2_buf_ptr == BUF_SIZE){
//  for (int i = 0; i < BUF_SIZE; i++)
//  printhexln(s2_buf[i]);
//  while(1);
//}

//printhex(mic_sample4);
//printstr(" ");
//printhex(mic_samples);
//printstr(" ");

    // There are 8 s1 samples generated for 32 mic samples
    for (int s1_sample_loop = 0; s1_sample_loop < stage2_DECIMATION_RATE; s1_sample_loop++)
    {
      #pragma xta label "loop1"
      int s1_sample_temp;

      /////////////////
      // Stage 1 Filter of the ADC's FIR Low pass filter
      /////////////////

      // increment and wrap the s1 sample pointer if necessary
      s1_sample_no++;
      if (s1_sample_no >= stage2_NUM_COEFF) {
        s1_sample_no = 0;
      }

      // apply filter coefficients to previous 4 sample values (may be from previous batch of 32 samples)
      s1_sample_temp = stage1FirLut1[mic_sample4];
      // select the oldest unprocessed 4 mic sample bits
      mic_sample4 = mic_samples & 0xf;
      // shift the other samples down
      mic_samples = mic_samples >> 4;
      // add in the fir taps for these 4 samples and store the s1 sample
      s1_sample[s1_sample_no] = s1_sample_temp + stage1FirLut0[mic_sample4];
//printhex(s1_sample[s1_sample_no]);
//printstr(" ");

    } // loop generating s1 decimated samples

    /////////////////
    // Stage 2 Filter of the ADC's FIR Low pass filter
    /////////////////

    // The first sample in the filter is the one just stored
    s1_sample_index = s1_sample_no;
    // Compute s2 sample
    for (int tap=0; tap < stage2_NUM_COEFF; tap++)
    {
      #pragma xta label "loop2"
      {filterSumHi, filterSumLo} = macs(stage2FirCoeff[tap], s1_sample[s1_sample_index], filterSumHi, filterSumLo);
      // Move onto next sample wrapping if necessary
      if (--s1_sample_index < 0) {
        s1_sample_index = stage2_NUM_COEFF-1;
      }

    }

#ifdef PRINT_SAMPLES
s2_buf [s2_buf_ptr++] = filterSumHi << VOLUME_BIT_SHIFT;
if (s2_buf_ptr == BUF_SIZE){
  for (int i = 0; i < BUF_SIZE; i++)
  printhexln(s2_buf[i]);
  while(1);
}
#else
//  printhexln(s2_buf_ptr);
    // output sample at low rate (5q27)
    c_pdm <: filterSumHi << VOLUME_BIT_SHIFT;
//    c_pdm <: s2_buf [s2_buf_ptr++];
//    if (s2_buf_ptr == BUF_SIZE)
//    {
//      s2_buf_ptr = 0;
//    }

#endif



//printhexln(filterSumHi);

//printstr("\n");
  } // while(1)
} // void pdm
