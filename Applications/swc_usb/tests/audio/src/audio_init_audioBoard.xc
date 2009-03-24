
#include <xs1.h>
#include <xclib.h>
#include "print.h"

/*** Private Definitions ***/
#define CTL_SCLK_PERIOD_LOW_TICKS      (1000)
#define CTL_SCLK_PERIOD_HIGH_TICKS     (1000)

#define DEVICE_ADRS                    (0x90)

unsigned samp;

/** Simple write command to the DAC chip via 2wire interface.
 */
int swc_audio_codec_ctl_reg_wr(int deviceAdrs, int Adrs, int WrData, port AUD_SCLK, port AUD_SDIN)
{
    int Result;
   timer gt;
   unsigned time;
   int Temp, CtlAdrsData, i;
   // three device ACK
   int DeviceACK[3];
 
   // initial values.
   AUD_SCLK <: 1;
   AUD_SDIN  <: 1;
   sync(AUD_SDIN);

   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;

   // start bit on SDI
   AUD_SCLK <: 1;
   AUD_SDIN  <: 0;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;

   // shift 7bits of address and 1bit R/W (fixed to write).
   // WARNING: Assume MSB first.
   for (i = 0; i < 8; i += 1)
   {
      Temp = (deviceAdrs >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }

   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;

   // sample first ACK.
   AUD_SDIN :> DeviceACK[0];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   
   CtlAdrsData = (Adrs & 0xFF);
   
   // shift first 8 bits.
   for (i = 0; i < 8; i += 1)
   {
      Temp = (CtlAdrsData >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample second ACK.
   AUD_SDIN :> DeviceACK[1];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;

   CtlAdrsData = (WrData & 0xFF);
   // shift second 8 bits.
   for (i = 0; i < 8; i += 1)
   {
      Temp = (CtlAdrsData >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample second ACK.
   AUD_SDIN :> DeviceACK[2];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // put the data to a good value for next round.
   AUD_SDIN  <: 1;
   // validate all items are ACK properly.
   Result = 0;
   for (i = 0; i < 3; i += 1)
   {
      if ((DeviceACK[i]&1) != 0)
      {
         Result = Result | (1 << i);
      }
   }

   return(Result);
}


/** Simple write command to the DAC chip via 2wire interface.
 */
int swc_audio_codec_ctl_reg_rd(int deviceAdrs, int Adrs, port AUD_SCLK, port AUD_SDIN)
{
    int Result;
   timer gt;
   unsigned time;
   int Temp, CtlAdrsData, i;
   // three device ACK
   int DeviceACK[3];
   int rdData;

   // initial values.
   AUD_SCLK <: 1;
   AUD_SDIN  <: 1;
   sync(AUD_SDIN);
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;
   // start bit on SDI
   AUD_SCLK <: 1;
   AUD_SDIN  <: 0;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   // shift 7bits of address and 1bit R/W (fixed to write).
   // WARNING: Assume MSB first.
   for (i = 0; i < 8; i += 1)
   {
      Temp = (deviceAdrs >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample first ACK.
   AUD_SDIN :> DeviceACK[0];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   
   CtlAdrsData = (Adrs & 0xFF);
   // shift first 8 bits.
   for (i = 0; i < 8; i += 1)
   {
      Temp = (CtlAdrsData >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample second ACK.
   AUD_SDIN :> DeviceACK[1];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;


   // stop bit
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;
   // start bit on SDI
   AUD_SCLK <: 1;
   AUD_SDIN  <: 1;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;
   
   
   // send address and read
   AUD_SCLK <: 1;
   AUD_SDIN  <: 1;
   sync(AUD_SDIN);
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS + CTL_SCLK_PERIOD_LOW_TICKS;
   gt when timerafter(time) :> int _;
   // start bit on SDI
   AUD_SCLK <: 1;
   AUD_SDIN  <: 0;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   // shift 7bits of address and 1bit R/W (fixed to write).
   // WARNING: Assume MSB first.
   for (i = 0; i < 8; i += 1)
   {
      int deviceAddr = deviceAdrs | 1;
      Temp = (deviceAddr >> (7 - i)) & 0x1;
      AUD_SDIN <: Temp;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample first ACK.
   AUD_SDIN :> DeviceACK[0];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   
   
   rdData = 0;
   // shift second 8 bits.
   for (i = 0; i < 8; i += 1)
   {
      
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 1;

      AUD_SDIN :> Temp;
      rdData = (rdData << 1) | (Temp & 1);
      
      gt :> time;
      time += CTL_SCLK_PERIOD_HIGH_TICKS;
      gt when timerafter(time) :> int _;
      AUD_SCLK <: 0;
   }
   
   // turn the data to input
   AUD_SDIN :> Temp;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // sample second ACK.
   AUD_SDIN :> DeviceACK[2];
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 0;
   gt :> time;
   time += CTL_SCLK_PERIOD_HIGH_TICKS;
   gt when timerafter(time) :> int _;
   AUD_SCLK <: 1;
   // put the data to a good value for next round.
   AUD_SDIN  <: 1;
   // validate all items are ACK properly.
   Result = 0;
   for (i = 0; i < 3; i += 1)
   {
      if ((DeviceACK[i]&1) != 0)
      {
         Result = Result | (1 << i);
      }
   }

   return(Result);
}


//clock AUD_BIT_CLOCK = XS1_CLKBLK_3; // TODO: move this out


#define CODEC_FUNCTIONAL_MODE          (0x3) 
#define CODEC_INTERFACE_FORMATS        (0x4)
#define CODEC_ADC_CONTROL              (0x5)

void audio_init(out port AUD_RESET_N, 
                port AUD_BCLK, 
                port AUD_SCLK, 
                port AUD_SDIN, 
                buffered out port:32 AUD_DOUT0, 
                buffered out port:32 AUD_DOUT1, 
                buffered out port:32 AUD_DOUT2, 
                buffered out port:32 AUD_DOUT3,
                in port AUD_LRCLK,
                clock AUD_BIT_CLOCK) 
{
   int WrData;
   timer t;
   unsigned int time;
   int value;
   
 

   // DAC_FM = 0 (single speed), ADC_FM = 0 (single speed), MFREQ = 2:  MCLK/512 - 48Khz
   //WrData = 0x54; 0b01010100
   //value = swc_audio_codec_ctl_reg_wr(DEVICE_ADRS, CODEC_FUNCTIONAL_MODE, WrData, AUD_SCLK, AUD_SDIN);
   
   // DAC_FM = 01 (double speed), ADC_FM = 01 (double speed), MFREQ = 100:  MCLK/512 
   WrData = 0b01011000;
   value = swc_audio_codec_ctl_reg_wr(DEVICE_ADRS, CODEC_FUNCTIONAL_MODE, WrData, AUD_SCLK, AUD_SDIN);

   // FREEZE = 0, AUX_DIF = 0, DAC_DIF=1 (I2S), ADC_DIF = 1 (I2S)
   WrData = 0x11;
   value = swc_audio_codec_ctl_reg_wr(DEVICE_ADRS, CODEC_INTERFACE_FORMATS, WrData, AUD_SCLK, AUD_SDIN);
   
   // ADC1-2_HPF FREEZE = 0, ADC3_HPF FREEZE = 0, DAC_DEM = 0, 
   // ADC1_SINGLE = 1(single ended), ADC2_SINGLE = 1, ADC3_SINGLE = 1, AIN5_MUX = 0, AIN6_MUX = 0  
   WrData = 0x1C;
   value = swc_audio_codec_ctl_reg_wr(DEVICE_ADRS, CODEC_ADC_CONTROL, WrData, AUD_SCLK, AUD_SDIN);
   
   
   // Initialise the clock
   //I2C Chip Address = 0x9C; // 1001110X where X is R/nW bit.
   {
	   int deviceAddr = 0x9C;
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x03, 0x01, AUD_SCLK, AUD_SDIN);
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x05, 0x01, AUD_SCLK, AUD_SDIN);
#if 0 
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x06, 0x06, AUD_SCLK, AUD_SDIN);
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x07, 0x00, AUD_SCLK, AUD_SDIN);
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x08, 0x00, AUD_SCLK, AUD_SDIN);
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x09, 0x00, AUD_SCLK, AUD_SDIN);
#endif
#if 0
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x06, 0x0c, AUD_SCLK, AUD_SDIN); // 500Hz -> 24.5760Mhz
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "
#endif 
#if 1
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x06, 0x18, AUD_SCLK, AUD_SDIN); // 500Hz -> 49.1520MHz
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "
#endif 




   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x16, 0x10, AUD_SCLK, AUD_SDIN);
   }
}




