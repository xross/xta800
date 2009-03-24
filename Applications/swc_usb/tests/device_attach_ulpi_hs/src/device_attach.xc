               
/**
 * @file device_attach.xc
 * @brief Tests HS device attach via UIFM
 * @author Ross Owen, XMOS Semiconductor Ltd 
 * @version 1.00
 **/                               
                                       
#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"
#include "XUD_UIFM_Defines.h"

#include "xud.h"

//#define HID 1

static int GetParam(int);
int XUD_Manager(chanend, chanend, chanend, chanend, chanend, chanend, chanend, chanend, chanend);
int XUD_Endpoints0(chanend, chanend);
void ep1_thread();
int XUD_GetBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char
buffer[]);
int XUD_SetBuffer_ResetPid(chanend c, chanend c_con, unsigned epNum, unsigned
char buffer[], unsigned datalength, unsigned char pid);
int XUD_SetBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char
buffer[], unsigned datalength);
void XUD_Sup_Delay(unsigned);

#ifdef HID
int XUD_Endpoints1(chanend c_ep1_dat, chanend c_ep1_ctl)
{
  int loop = 1;
  char buffer[] = {0x0, 8, 0, 0x0, 4, 5, 66, 77, 88, 99};
  int counter = 0, counter2 = 0;
  unsigned char pid = PIDn_DATA0;
  
  int state = 0;

  while(1)
  {
    loop = 1;
    counter = 0;
    //buffer[0] = 0;
    while(loop)
    {
      counter++;
      counter2++;
      if(counter2 == 3)
      {
        //buffer[0] = !buffer[0];
        counter2 = 0;
      }
      if(counter == 50)
      {
               state++; 
        if(state == 4)
          state = 0;

        if(state == 0)
        {
          buffer[1] = 4;
          buffer[2] = 0; 
        }
        else if(state == 1)
        {
          buffer[1] = 0;
          buffer[2] = 4; 
        }
         else if(state == 2)
        {
          buffer[1] = -4;
          buffer[2] = 0; 
        }
        else if(state == 3)
        {
          buffer[1] = 0;
          buffer[2] = -4; 
        }

        counter = 0;
      }
      //loop = XUD_SetBuffer_ResetPid(c_ep1_dat, c_ep1_ctl, 1, buffer, 4, pid);
      loop = XUD_SetBuffer(c_ep1_dat, c_ep1_ctl, 1, buffer, 4);
      pid = pid ^ 0x88;
      
    }
    
    stop_streaming_slave(c_ep1_dat); 
    stop_streaming_slave(c_ep1_ctl);
  }
}
#else

int XUD_Endpoint3(chanend c_ep3_dat, chanend c_ep3_ctl)
{
  int loop = 1;
  char buffer[] = {0x0, 0, 0, 0x0, 4, 5, 66, 77, 88, 99};
  unsigned char pid; 

  while(1)
  {
    pid = PIDn_DATA1;
    loop = 1;
    while(loop)
    {
      //loop = XUD_SetBuffer_ResetPid(c_ep1_dat, c_ep1_ctl, 1, buffer, 4, pid);
      loop = XUD_SetBuffer(c_ep3_dat, c_ep3_ctl, 3, buffer, 4);
      pid = pid ^ 0x88;
      
    }
    
    stop_streaming_slave(c_ep3_dat); 
    stop_streaming_slave(c_ep3_ctl);
  }
  return 0;
}

#endif

int dummyEP(chanend c_dat, chanend c_ctl)
{
  while(1)
  {
    inct(c_ctl);
    stop_streaming_slave(c_dat); 
    stop_streaming_slave(c_ctl);
  }
  return 0;
}

/*
void dac(chanend c_dac, out port dacport)
{ 
  int value = 512;
  timer t;
  int off = 0;
  int offTime = 0;
  int correct;
  int err = 0, lastValue = 0, lastPeriod = 0, lastPort = 0, last = 0;
 
  set_thread_fast_mode_on();


  //t :> nextSample;
  while (1)
  {
      inuint_byref(c_dac, value);
      //t :> correct;
      //lastPeriod = ((int)(correct - last));
      //last = correct;
      //err += (lastPort - lastValue) * lastPeriod ;
      err += value;
      if (err > 65536) 
      {
        err -= 65536;
        dacport <: 1;
      } 
      else 
      {
        //lastPort = 0;
        dacport <: 0;
      }
      //lastValue = value;
  }

}
*/
void dac(chanend c_dac, out port dacport)
{ 
  unsigned value = 512;
  timer t;
  int nextSample;
  int separation = 1024;
  int off = 0;
  int offTime = 0;
  int correct = 0, correct0 = 0;

  t :> nextSample;
    
  inuint(c_dac);
  
  while (1)
  {
    select {
      case  inuint_byref(c_dac, value):
        value >>= 22;
        break;
      case t when timerafter(nextSample) :> correct: 
        dacport <: off; 
        correct -= nextSample;
        correct = 0;
        if (off) {
          off = 0;
          offTime = separation - (value) -correct;
          nextSample += (value) - correct0;
        } else {
          off = 1;
          nextSample += offTime;
          correct0 = correct; 
        }
       break;
    }
  }
}

void doClock(chanend c_sof, out port p) 
{
  int sofcount = 0;
  int frameNo;
  int val = 0;
  timer t;
  int time;

  p <: val;

  t :> time;
  time += 500000;

  while(1)
  {
    //val = !val;

    select
    {
      case c_sof :> frameNo:
       // break;

      //case t when timerafter(time) :> int _:
      //time += 100000;

      val = !val;
      p <: val;
      break;

   }
  }

}

void clockGen(out port p)
{
   int val = 0;
   unsigned int time;
   timer t;

   t :> time;
   time += 50000;

   while (1)
   {
     select
     {
       case t when timerafter(time) :> int tmp:
         time += 50000;  // 0.5ms for 1ms period, 1kz clock freq.
         val = ~val;
         p <: val;
	 break;
     }
   }
}


inline int readData(chanend c, chanend c_ctl, int buffer[], chanend otherSide) {
  int datalength;
  int cmd;
  select 
  {
    case inct_byref(c_ctl, cmd):
      return -1;
    case inuint_byref(c, datalength):
      datalength >>= 2;
#pragma unsafe arrays
      for(int i = 0; i < datalength; i+=1)
      { 
      buffer[i] = inuint(c);
      } 
     
      // Crc 
      inuint(c);       

      outuint(c, XUD_CMD_OUT);
      outuint(c, 1);
      outct(c, XS1_CT_PAUSE);
      outct(otherSide, 1);
      return datalength;
  }
  return -1; // Not reached.
}

static int isLeft;
#define MAGIC 84672000

inline void deliver(int dataLength, int buffer[], chanend otherSide,
                   in port AUD_LRCLK, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4)
{
   static unsigned value, value2, value3, value4, value5, value6, value7, value8;
  unsigned readIndex = 0;
   static int played = 0;
   const int dacSampleRate = MAGIC/96000;
   int playSampleRate = dacSampleRate;
   int _;
   int done = 0;
#pragma unsafe arrays
   while(!done) {
     select {
       case inct_byref(otherSide, done):
         break;
       case AUD_LRCLK when pinsneq(isLeft) :> isLeft:
         if (isLeft) {
             {value, _}  = macs(buffer[readIndex], GetParam(19), 0, 0);
             value = bitrev(value);
             {value3, _}  = macs(buffer[readIndex+2], GetParam(21), 0, 0);
             value3 = bitrev(value3);
             {value5, _}  = macs(buffer[readIndex+4], GetParam(23), 0, 0);
             value5 = bitrev(value5);
             {value7, _}  = macs(buffer[readIndex+6], GetParam(25), 0, 0);
             value7 = bitrev(value7);
            
             AUD_DOUT1 <: value;
             AUD_DOUT2 <: value3;
             AUD_DOUT3 <: value5;
             AUD_DOUT4 <: value7;             

             {value2, _}  = macs(buffer[readIndex+1], GetParam(20), 0, 0);
             value2 = bitrev(value2);
             {value4, _}  = macs(buffer[readIndex+3], GetParam(22), 0, 0);
             value4 = bitrev(value4);
             {value6, _}  = macs(buffer[readIndex+5], GetParam(24), 0, 0);
             value6 = bitrev(value6);
             {value8, _}  = macs(buffer[readIndex+7], GetParam(26), 0, 0);
             value8 = bitrev(value8);

             playSampleRate = GetParam(9);
             playSampleRate = MAGIC / playSampleRate;

             played += dacSampleRate;
             while (played >= playSampleRate) {
               played -= playSampleRate;
               if (readIndex + 8 < dataLength) {
                 readIndex += 8;
               }
             }

         } else {
             AUD_DOUT1 <: value2;
             AUD_DOUT2 <: value4;
             AUD_DOUT3 <: value6;
             AUD_DOUT4 <: value8;
                       }
          break;
      }
   }
}

extern void audio_init(out port AUD_RESET_N, port AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, in port AUD_LRCLK);

void XUD_Endpoint1(chanend c, chanend c_ctl, out port AUD_RESET_N, port AUD_BCLK, port AUD_SCLK, port AUD_SDIN,
                   in port AUD_LRCLK, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4) 
{  unsigned time;
  chan otherSide;
#define ABL 768  // Buffer length
  int  bufferA[ABL];
  int  bufferB[ABL];
  int dataLengthA, dataLengthB;

  audio_init(AUD_RESET_N, AUD_BCLK, AUD_SCLK, AUD_SDIN, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4, AUD_LRCLK); 

  while(1)
  {
    outuint(c, XUD_CMD_OUT);
    outuint(c, 1);
    outct(c, XS1_CT_PAUSE);

    AUD_LRCLK :> int _; 
    AUD_LRCLK  when pinsneq(1) :> isLeft;
    AUD_LRCLK  when pinsneq(0) :> isLeft@time;
    time+=64;
    AUD_DOUT1 @ time <: 0;
    AUD_DOUT2 @ time <: 0;
    AUD_DOUT3 @ time <: 0;
    AUD_DOUT4 @ time <: 0;

    while(1) {      
      par {
        dataLengthA = readData(c, c_ctl, bufferA, otherSide);
        deliver(dataLengthB, bufferB, otherSide, AUD_LRCLK, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4);
      }
      if (dataLengthA == -1) break;
      par {
        dataLengthB = readData(c, c_ctl, bufferB, otherSide);
        deliver(dataLengthA, bufferA, otherSide, AUD_LRCLK, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4);
      }
      if (dataLengthB == -1) break;
    }
    
    //stop_streaming_slave(c); 
    //stop_streaming_slave(c_ctl);
  }
}





int codec_init(int);
void codec_clock(void);
void codec_dac(chanend , chanend);


#define USB_CORE 1
#define AUD_CORE 2

// USB Port declarations
on stdcore[1] : in port  p_usb_clk      = UIFM_USB_CLK_PORT;
on stdcore[1] : out port reg_write_port = UIFM_REG_WRITE_PORT;
on stdcore[1] : in  port reg_read_port  = UIFM_REG_READ_PORT;
on stdcore[1] : in  port flag0_port     = UIFM_FLAG_0_PORT;
on stdcore[1] : in  port flag1_port     = UIFM_FLAG_1_PORT;
on stdcore[1] : in  port flag2_port     = UIFM_FLAG_2_PORT;
on stdcore[1] : out port p_usb_txd      = UIFM_TX_DATA_PORT;
on stdcore[1] :     port p_usb_rxd      = UIFM_RX_DATA_PORT;
on stdcore[1] : out port p_usb_rst      = XS1_PORT_1B; // was PORT_1A, 1D for XVB

// Audio breakout board codec ports
on stdcore[AUD_CORE] : port p_aud_sclk                 = XS1_PORT_1A;
on stdcore[AUD_CORE] : in port p_aud_lrclk  	       = XS1_PORT_1B;    
on stdcore[AUD_CORE] : port p_aud_scl                  = XS1_PORT_4B;    //  2-wire configuration interface.
on stdcore[AUD_CORE] : port p_aud_sda                  = XS1_PORT_4A;    
on stdcore[AUD_CORE] : out port p_aud_rst              = XS1_PORT_4C;    
on stdcore[AUD_CORE] : out port p_aud_clk 	       = XS1_PORT_1N;
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac1 = XS1_PORT_1C;
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac2 = XS1_PORT_1D; 
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac3 = XS1_PORT_1E;  
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac4 = XS1_PORT_1F;  

/*
// XDK onbaord codec ports
on stdcore[AUD_CORE] : port p_aud_lrcin      = XS1_PORT_1D;    // X3D11
on stdcore[AUD_CORE] : buffered in port p_aud_lrcout     = XS1_PORT_1H;    // X3D23
on stdcore[AUD_CORE] : buffered in  port:32 p_aud_din  = XS1_PORT_1J;    // X3D25
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dout = XS1_PORT_1I;    // X3D24
on stdcore[AUD_CORE] : port p_aud_sclk       = XS1_PORT_1K;    // X3D34  2-wire configuration interface.
on stdcore[AUD_CORE] : port p_aud_sdin       = XS1_PORT_1L;    // X3D35
on stdcore[AUD_CORE] : port out p_led_dac    = XS1_PORT_1E;
on stdcore[AUD_CORE] : port out p_led_adc    = XS1_PORT_1G;
on stdcore[AUD_CORE] : port p_aud_bclk       = XS1_PORT_1C;    // X3D10
on stdcore[AUD_CORE] : port p_aud_rst    = XS1_PORT_4C;
*/

void audio(chanend leftOut, chanend rightOut, chanend middleOut, out port AUD_RESET_N, port AUD_BCLK, in port AUD_LRCLK, buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, port AUD_SCLK, port AUD_SDIN) ;

int main()
{
  chan c_ep0_dat, c_ep0_ctl;
  chan c_ep1_dat, c_ep1_ctl;
  chan c_ep2_dat, c_ep2_ctl;
  chan c_ep3_dat, c_ep3_ctl;

  chan c_sof;

  par
  {
    on stdcore[1] : XUD_Manager(c_ep0_dat, c_ep0_ctl, c_ep1_dat, c_ep1_ctl, c_ep2_dat, c_ep2_ctl, c_ep3_dat, c_ep3_ctl, c_sof);  
    on stdcore[2] : XUD_Endpoints0(c_ep0_dat, c_ep0_ctl);

// TODO Seperate HID demo
#ifdef HID 
    on stdcore[1] : XUD_Endpoints1(c_ep1_dat, c_ep1_ctl);
    on stdcore[1] : dummyEP(c_ep2_dat, c_ep2_ctl);
    on stdcore[1] : dummyEP(c_ep3_dat, c_ep3_ctl);
#else



    on stdcore[0] : XUD_Endpoint3(c_ep3_dat, c_ep3_ctl);
   
    on stdcore[2] : XUD_Endpoint1(c_ep1_dat, c_ep1_ctl,
                                  p_aud_rst, p_aud_sclk, p_aud_scl, p_aud_sda, p_aud_lrclk,
                                  p_aud_dac1, p_aud_dac2, p_aud_dac3, p_aud_dac4);

    on stdcore[1] : dummyEP(c_ep2_dat, c_ep2_ctl);
    on stdcore[2] : doClock(c_sof, p_aud_clk);
    //on stdcore[2] : clockGen(p_aud_clk);
#endif

  }

  return 0;
}








