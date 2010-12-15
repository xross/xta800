
/* Functions for reading/writing codec registers via i2c */
#include <xs1.h>
#include <xclib.h>

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








