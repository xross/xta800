/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include <xclib.h>
#include "print.h"

#include <platform.h>
#define HALF_CLOCK_PERIOD 500   // 1MHz



#define BUILD_CTRL_BITS(cs,clk,data) (cs<<2) | (clk << 1) | (data&1) 

//
// Write bits and toggle clock
//
int swc_audio_codec_ctl_reg_wr_bits_gen(int data, int numBits, port p_CTRL, int cs_or_reset)
{
   int Result = 0;
   timer t;
   unsigned time;
   int i;

   for (i=0; i<numBits; i++)
   {
	   t :> time;      
	   time += HALF_CLOCK_PERIOD;
	   t when timerafter(time) :> int _;

	   p_CTRL <: BUILD_CTRL_BITS(cs_or_reset,0,(data>>numBits-1));
	   
	   time += HALF_CLOCK_PERIOD;
	   t when timerafter(time) :> int _;
	   
	   p_CTRL <: BUILD_CTRL_BITS(cs_or_reset,1,(data>>numBits-1));
     data = data << 1;

   }
   
   return(Result);
}

int swc_audio_codec_ctl_reg_wr_bits(int data, int numBits, port p_CTRL) {
    return swc_audio_codec_ctl_reg_wr_bits_gen(data, numBits, p_CTRL, 0);
}


/** This send simple write command to the DAC chip via 2wire interface.
 */
int swc_audio_codec_ctl_reg_wr_akm(int deviceAdrs, int Adrs, int WrData, port p_CTRL)
{
   int Result = 0;
   timer t;
   unsigned time;

   p_CTRL <: BUILD_CTRL_BITS(1,1,0);  // cs
   
   t :> time;
   time += HALF_CLOCK_PERIOD*5;
   t when timerafter(time) :> int _;

   p_CTRL <: BUILD_CTRL_BITS(0,1,0);  // cs
      
   t :> time;
   time += HALF_CLOCK_PERIOD;
   t when timerafter(time) :> int _;

   swc_audio_codec_ctl_reg_wr_bits(deviceAdrs, 2, p_CTRL);
   swc_audio_codec_ctl_reg_wr_bits(1, 1, p_CTRL);  // RW
   swc_audio_codec_ctl_reg_wr_bits(Adrs, 5, p_CTRL);
   swc_audio_codec_ctl_reg_wr_bits(WrData, 8, p_CTRL);

   t :> time;
   time += HALF_CLOCK_PERIOD;
   t when timerafter(time) :> int _;

   p_CTRL <: BUILD_CTRL_BITS(1,1,0);  // cs
   
   return(Result);
}


/** This send simple write command to the DAC chip via 2wire interface.
 */
int swc_audio_codec_ctl_reg_wr_akm_v2(int deviceAdrs, int Adrs, int WrData, port p_CTRL, out port p_CS)
{
   int Result = 0;
   timer t;
   unsigned time;

   p_CTRL <: BUILD_CTRL_BITS(1,1,0);  // Reset high, Clock high, data low

   p_CS <: 0xF; // BUILD_CTRL_BITS(1,1,0);  // cs
   
   t :> time;
   time += HALF_CLOCK_PERIOD*5;
   t when timerafter(time) :> int _;

   p_CS <: 0; //TRL <: BUILD_CTRL_BITS(0,1,0);  // cs
      
   t :> time;
   time += HALF_CLOCK_PERIOD;
   t when timerafter(time) :> int _;

   swc_audio_codec_ctl_reg_wr_bits_gen(deviceAdrs, 2, p_CTRL, 1);
   swc_audio_codec_ctl_reg_wr_bits_gen(1, 1, p_CTRL, 1);  // RW
   swc_audio_codec_ctl_reg_wr_bits_gen(Adrs, 5, p_CTRL, 1);
   swc_audio_codec_ctl_reg_wr_bits_gen(WrData, 8, p_CTRL, 1);

   t :> time;
   time += HALF_CLOCK_PERIOD;
   t when timerafter(time) :> int _;

   p_CS <: 0xF; // TRL <: BUILD_CTRL_BITS(1,1,0);  // cs
   
   return(Result);
}

//
// Perform hard reset and initialise configuration registers
//
/*int swc_audio_codec_init(port p_CTRL, port p_RESET_N, int sampleRate)
{
	int deviceAdrs = 2;
	timer t;
	unsigned int time;*/
/*
Addr - Data
0x00 - 0x27 - Sharp roll off filter, Zero detection disabled, All sections powered up.
0x01 - 0x0F - PCM Mode, Differential ADC input, ADC and DAC out of reset.
0x02 - Depends on sample rate:

44.1kHz - Normal Speed (DFS1 = 0, DFS0 = 0), MCLK = 1024fs (CMODE = 0, CKS1 = 1, CKS0 = 0), 24bit, MSB justified (DIF2 = 0, DIF1 = 1, DIF0 = 0).
48kHz   - Normal Speed (DFS1 = 0, DFS0 = 0), MCLK = 1024fs (CMODE = 0, CKS1 = 1, CKS0 = 0), 24bit, MSB justified (DIF2 = 0, DIF1 =
1, DIF0 = 0).
96kHz   - Double Speed (DFS1 = 0, DFS0 = 1), MCLK = 512fs  (CMODE = 0, CKS1 = 1, CKS0 = 0), 24bit, MSB justified (DIF2 = 0, DIF1 =
1, DIF0 = 0).
192kHz  -   Quad Speed (DFS1 = 1, DFS0 = 0), MCLK = 256fs  (CMODE = 0, CKS1 = 1, CKS0 = 0), 24bit, MSB justified (DIF2 = 0, DIF1 =
1, DIF0 = 0).

So for this register:

Sample Rate - Data
44.1kHz     - 0x48
48kHz       - 0x48
96kHz       - 0x49
192kHz      - 0x4A

0x03 - 0x19 (Default) - De-emphasis Off, Zero crossing timeout 1024fs, IPGA changes on zero crossing, High Pass Filter Enabled, No Mute.
0x04 - 0x80 (Default) - 0dB Input Gain
0x05 - 0x80 (Default) - 0dB Input Gain
0x06 - 0xFF (Default) - 0dB Output Attenuation
0x07 - 0xFF (Default) - 0dB Output Attenuation

 	
	printstr("Initialising...\n");
	
	// hard reset
	p_RESET_N <: 0;
	t :> time;
	t when timerafter(time + 100) :> int _;
	p_RESET_N <: 1;
	
	// initialise registers
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x0,0x27, p_CTRL);
	
	switch (sampleRate)
	{
	case 44100:
		swc_audio_codec_ctl_reg_wr(deviceAdrs,0x2,0x48, p_CTRL);
		break;
	case 48000:
		swc_audio_codec_ctl_reg_wr(deviceAdrs,0x2,0x48, p_CTRL);
		break;
	case 96000:
		swc_audio_codec_ctl_reg_wr(deviceAdrs,0x2,0x49, p_CTRL);
		break;
	case 192000:
		swc_audio_codec_ctl_reg_wr(deviceAdrs,0x2,0x4A, p_CTRL);
		break;
	default:
		printstr("Error. Unsupported sample rate");
		break;
	}
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x3,0x19, p_CTRL);
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x4,0x80, p_CTRL);
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x5,0x80, p_CTRL);
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x6,0xFF, p_CTRL);
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x7,0xFF, p_CTRL);
	
	// soft reset
	swc_audio_codec_ctl_reg_wr(deviceAdrs,0x1,0x0F, p_CTRL);
	
	printstr("done");
	return 0;
}

#define AUDIO_CORE 0

on stdcore[AUDIO_CORE] : port p_CODEC_CTRL = XS1_PORT_4F;  // cs, clk, cdi
on stdcore[AUDIO_CORE] : port p_CODEC_RESET_N = XS1_PORT_4C;  // reset

int main()
{
	par
	{
	    on stdcore[AUDIO_CORE] : swc_audio_codec_init(p_CODEC_CTRL, p_CODEC_RESET_N, 44100);
	}
	
	return 0;
}
*/
