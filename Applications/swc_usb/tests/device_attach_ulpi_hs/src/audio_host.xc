/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

/*
 * @ModuleName Audio A to D converter interface via I2S slave.
 * @Author Ayewin Oung
 * @Date 07/04/2008
 * @Version 1.0
 * @Description: Audio A to D converter interface via I2S slave.
 *
 *
 * Copyright XMOS Ltd 2008
 */

//
// NOTES:
// This assume 16bits per audio sample and there are two, left/right channels.
// Also assume AUD_DIN is 1 bit buffered port of widith 8bits, since there is no
// 16bits buffered mode port.
//

#include <xs1.h>
#include <xclib.h>

#include "print.h"

#define NUM_BYTES_IN_SAMPLE (2)

// Internal private defines.
#define AUDIO_ADC_BUF_CTL_IDLE      (0x0)
#define AUDIO_ADC_BUF_CTL_REQ       (0x1)
#define AUDIO_ADC_BUF_CTL_RESP      (0x2)

// Size of data items to move per *transaction*, 1/4 of actual buffer size.
#define AUDIO_ADC_DATA_MOVE_SIZE    (AUDIO_ADC_BUF_SIZE >> 2)


/** This reshuffle incoming bit stream from I2S to internal 32bit Audio data structure.
 */


static unsigned int I2SToAudio(unsigned int I2SData)
{
   // NOTE: No bit shifting is required just bitresv, since XCore inShiftRight
   // matches with I2S, MSBit first.
   // reverse the bits.
   I2SData = bitrev(I2SData);

   return (I2SData);
}






//extern void audio_init(port AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered in port:32 AUD_DIN, buffered out port:32 AUD_DOUT, buffered in port:1 AUD_LRCOUT, port AUD_LRCIN);




/** Audio analogue to digital interface via simple I2S.
 *  Receive Left/Right samples via I2S.
 *  NOTE: I2S input sample interface ONLY contain valid pin on *second*
 *  rising edge of LRCIN change.
 *  LRCIN pin should be clocked off from BCLK.
 */
void audio_from_host(chanend poison, chanend before, chanend left, chanend right, port AUD_LRCIN, buffered in port:8 AUD_DIN, out port led_adc)
{
    int isLeft;
    unsigned int SampleData = 0;
    int odd = 1;

    led_adc <: 0;
    isLeft = 0;
    // loop to get continuous samples.
    while (1) {
        select {
        case AUD_LRCIN when pinsneq (isLeft) :> isLeft:
            break;
        case poison :> int x:
            before <: 0x80000000;
            outuint(left, 0x80000000);
            outct(left, XS1_CT_END);
            outuint(right, 0x80000000);
            outct(right, XS1_CT_END);
            inct(left);
            inct(right);
            led_adc <: 1;
            return;
        }

        clearbuf(AUD_DIN);
        SampleData = I2SToAudio(SampleData);
        if (isLeft) {
            if (odd) {
                before <: (int)(short)SampleData;
            }
            odd = !odd;
            outuint(left, (int)(short)SampleData);
        } else {
            outuint(right, (int)(short)SampleData);
        }
        SampleData = 0;
        AUD_DIN :> >> SampleData;
        AUD_DIN :> >> SampleData;
    }
}

/** This covert the buffer data into sample data required by I2S interface.
 */
static unsigned int AudioToI2S(unsigned int AudioData)
{
    AudioData = AudioData << 16;
    AudioData = bitrev(AudioData);

    return (AudioData);
}

int GetSamp();
int SetSamp(int);
unsigned samp;

void receive(chanend left)
{
  while(1)
  {
    samp = inuint(left);
    inuint(left);
  }
}

/** Audio Digital to Analogue interface control via I2S
 *  NOTE: To provide max. head room, LRCOUT is clocked from 100MHz, software ref. clock.
 */
void audio_to_host(chanend left, chanend right, chanend middle, in port AUD_LRCOUT, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3) {
    unsigned int sampleData1L = 0, sampleData1R = 0, sampleData2L = 0, sampleData2R=0, sampleData3L = 0, sampleData3R = 0;
    register int isLeft;
    int time;
   
    set_thread_fast_mode_on();
    AUD_LRCOUT :> int _; 
  
    inuint(left);  // Sync
    inuint(right);
    inuint(middle);
    AUD_LRCOUT  when pinsneq(1) :> isLeft;
    AUD_LRCOUT  when pinsneq(0) :> isLeft;
    AUD_LRCOUT  when pinsneq(1) :> isLeft;
    AUD_LRCOUT  when pinsneq(0) :> isLeft@time;
    time+=64;
    AUD_DOUT1 @ time <: 0;
    AUD_DOUT2 @ time <: 0;
    AUD_DOUT3 @ time <: 0;


    while (1) 
    {
#pragma priority
        select
        {
         
          case AUD_LRCOUT when pinsneq(isLeft) :> isLeft:
          {
            if (isLeft)
            { 
              AUD_DOUT1 <: sampleData1L;
	      AUD_DOUT2 <: sampleData2L;
	      AUD_DOUT3 <: sampleData3L;
            }
            else
            {
              AUD_DOUT1<: sampleData1R;
 	      AUD_DOUT2<: sampleData2R;
	      AUD_DOUT3<: sampleData3R;
            }
            break;
          } 
          case inuint_byref(left, sampleData1L):
            inuint_byref(right, sampleData1R);
	    //inuint_byref(middle, sampleData2L);
	    //inuint_byref(left, sampleData2R);
            //inuint_byref(right, sampleData3L);
	    //inuint_byref(middle, sampleData3R);
            break;
        }
        
    }
}

#define CODEC_DIGITAL_CTL_REG          (0x5)
#define CODEC_ANALOGE_CTL_REG          (0x4)
/*extern int swc_audio_codec_ctl_reg_wr(int Adrs, int WrData, port AUD_SCLK, port AUD_SDIN);


void audio_selector(chanend x, port AUD_SCLK, port AUD_SDIN) {
    while (1) {
        int cmd;
        x :> cmd;
        if (cmd == 0x80000000) {
            return;
        } else if (cmd < 0) {
            swc_audio_codec_ctl_reg_wr(CODEC_DIGITAL_CTL_REG, 0, AUD_SCLK, AUD_SDIN);
            swc_audio_codec_ctl_reg_wr(CODEC_ANALOGE_CTL_REG, 0x12, AUD_SCLK, AUD_SDIN);
        } else if (cmd > 0) {
            swc_audio_codec_ctl_reg_wr(CODEC_DIGITAL_CTL_REG, 8, AUD_SCLK, AUD_SDIN);
            swc_audio_codec_ctl_reg_wr(CODEC_ANALOGE_CTL_REG, 0x1A, AUD_SCLK, AUD_SDIN);
        } else {
            swc_audio_codec_ctl_reg_wr(CODEC_DIGITAL_CTL_REG, 8, AUD_SCLK, AUD_SDIN);
            swc_audio_codec_ctl_reg_wr(CODEC_ANALOGE_CTL_REG, 0x12, AUD_SCLK, AUD_SDIN);
        }
    }
}*/

void audio_init(out port AUD_RESET_N, 
                port AUD_BCLK, 
                port AUD_SCLK, 
                port AUD_SDIN, 
                buffered out port:32 AUD_DOUT1, 
                buffered out port:32 AUD_DOUT2, 
                buffered out port:32 AUD_DOUT3, 
                in port AUD_LRCLK);

void audio(chanend leftOut,  chanend rightOut, chanend middleOut, out port AUD_RESET_N, port AUD_BCLK, in port AUD_LRCLK, buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, port AUD_SCLK, port AUD_SDIN) 
{
      //audio_init(AUD_RESET_N, AUD_BCLK, AUD_SCLK, AUD_SDIN, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4,/ AUD_LRCLK); 
       audio_to_host(leftOut, rightOut, middleOut, AUD_LRCLK, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3); 
}
