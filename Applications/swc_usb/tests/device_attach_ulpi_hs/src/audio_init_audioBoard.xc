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

/*** Private Definitions ***/

#define CTL_SCLK_PERIOD_LOW_TICKS      (1000)
#define CTL_SCLK_PERIOD_HIGH_TICKS     (1000)

// Device address and R/W fixed to write only.
// 0011_0100
//#define DEVICE_ADRS                    (0x34)

// device addres 1001_000X.
#define DEVICE_ADRS                    (0x90)

// Valid register address.
#define LIVCTL_REG      (0x00)
#define RIVCTL_REG      (0x01)
#define LHPVCTL_REG     (0x02)
#define RHPVCTL_REG     (0x03)
#define AAPCTL_REG      (0x04)
#define DAPCTL_REG      (0x05)
#define PDCTL_REG       (0x06)
#define DAIF_REG        (0x07)
#define SRC_REG         (0x08)
#define DIA_REG         (0x09)
#define RESET_REG       (0x0F)

/** This send simple write command to the DAC chip via 2wire interface.
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
   
   // this build funny TI data format
   //CtlAdrsData = ((Adrs & 0x7F) << 9) | (WrData & 0x1FF);
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


/** This send simple write command to the DAC chip via 2wire interface.
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
   
   // this build funny TI data format
   //CtlAdrsData = ((Adrs & 0x7F) << 9) | (WrData & 0x1FF);
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


clock AUD_BIT_CLOCK = XS1_CLKBLK_3;


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
                in port AUD_LRCLK) 
{
   int WrData;
   timer t;
   unsigned int time;
   int value;
   
   // I2S based two channel Audio in/out.
   // Generate bit clock block from pin
   configure_clock_src(AUD_BIT_CLOCK, AUD_BCLK);
   
   // Configure ports with required clock source.
   configure_out_port_no_ready(AUD_DOUT0, AUD_BIT_CLOCK, 0);
   configure_out_port_no_ready(AUD_DOUT1, AUD_BIT_CLOCK, 0);
   configure_out_port_no_ready(AUD_DOUT2, AUD_BIT_CLOCK, 0);
   configure_out_port_no_ready(AUD_DOUT3, AUD_BIT_CLOCK, 0);   
    
   configure_out_port_no_ready(AUD_LRCLK, AUD_BIT_CLOCK, 0);
   
   // start the ports and clock ports.
   //start_port(AUD_BCLK);
   //start_port(AUD_LRCIN);
   //start_port(AUD_LRCOUT);
   //start_port(AUD_DIN0);
   //start_port(AUD_DIN1);
   //start_port(AUD_DOUT0);
   //start_port(AUD_DOUT1);
   
   // start clock.
   start_clock(AUD_BIT_CLOCK);

   AUD_RESET_N <: 0;
   t :> time;
   t when timerafter(time + 100000) :> time;
   AUD_RESET_N <: 1;
   
   t :> time;
   t when timerafter(time + 100000) :> time;
   
   // Initialise the ports.
   // control interface simple
   AUD_SCLK <: 1;
   AUD_SDIN <: 1;

   // DAC_FM = 0 (single speed), ADC_FM = 0 (single speed), MFREQ = 2:  MCLK/512 - 48Khz
   WrData = 0x54;
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
#if 1
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x06, 0x0c, AUD_SCLK, AUD_SDIN); // 4Khz -> 24.576
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "
#endif 
   value = swc_audio_codec_ctl_reg_wr(deviceAddr, 0x16, 0x10, AUD_SCLK, AUD_SDIN);
   }
}




