               
/**
 * @file audio.xc
 * @brief Implements a Audio class v2.0 in the form of a 8 channel soundcard. Also implements extra HID EP
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
#include "audio.h"

#include "xud.h"

static int GetParam(int);
inline unsigned GetBufferAddress(int b[]);

int Endpoint0Wrapper(chanend, chanend);

void ep1_thread();

void ep3dead()
{
  while(1);
}

/* HID Endpoint (IN Endpoint 3)
 * Currently does simple volume and mute control using buttons
 */
int XUD_Endpoint3(chanend c_ep2_dat, in port p_but)
{
  int loop = 1;
  int butVal;
  // [0] : vol inc
  // [1] : vol dec
  // [2] : master mute
  char buffer[] = {0, 0, 0, 0, 0, 0, 0, 0};

  p_but :> butVal;

  while(1)
  { 
    loop = 1;
    while(loop != -1)
    {
      p_but :> butVal;
        
      buffer[0] = (~butVal)&0x7;
      
      loop = XUD_SetBuffer_(c_ep2_dat, 2, buffer, 4);

     }
  }
  return 0;
}

long lrand48();
/* Dummy endpoint
 * just waits for kill on ctrl chanend then restarts
 */
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

/* Generates clock based on SOF */
void doClock(chanend c_sof, out port p) 
{
    int val = 0;
    timer t;
    unsigned time; 
    int count = 0; 
    int tmp;

    p <: val;
    t :> time;

    while(1)
    {
      if(testct(c_sof)) {
	inct(c_sof);
      } else {
        inuint_byref(c_sof, tmp);
            count++;
            if(count == 5)
        {
            val = !val;
            p <: val;
            count = 0;
        }

      }
    }
}

int GetInt(unsigned array, int index);

int data = 0;


// Returns datalength in WORDS
inline int readData(chanend c, int bufferA[], int bufferB[], int bufferC[], chanend otherSide) 
{
    int datalength;
    while(1) {
      while(1)
	{
	  
	  datalength = XUD_GetBuffer_int(c, 1, bufferA);
	  if (datalength == -1) break;
	  data = 0;
	  outuint(otherSide, datalength >> 2); /* Send datalength in words! */
	  outuint(otherSide, 0);
	  
	  datalength = XUD_GetBuffer_int(c, 1, bufferB);
	  if (datalength == -1) break;
	  data = 1;
	  outuint(otherSide, datalength >> 2); /* Send datalength in words! */
	  outuint(otherSide, 1);
	  
	  datalength = XUD_GetBuffer_int(c, 1, bufferC);
	  if (datalength == -1) break;
	  data = 2;
	  outuint(otherSide, datalength >> 2); /* Send datalength in words! */
	  outuint(otherSide, 2);
	}

    }       
}


static int isLeft;
#define MAGIC 84672000
#define ABL 768  // Buffer length


#define CODEC_DEV_ADR              (0x90)
#define SYNC_DEV_ADR              (0x9c) // Address of external clock sync chip 
int  bufferA[ABL];
    int  bufferB[ABL];
    int bufferC[ABL];
int swc_audio_codec_ctl_reg_wr(int deviceAdrs, int Adrs, int WrData, port AUD_SCLK, port AUD_SDIN);
static short lrtime = 0;

int GetData();

static inline int deliver(int dataLength, unsigned buffer, 
                   in port AUD_LRCLK, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  
                   buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, int i_CurSampFreq)
{
    static unsigned value, value2, value3, value4, value5, value6, value7, value8;
    unsigned readIndex = 0;
    int _;
    int time;
    short s_tmp;
    int oldData = GetData();
    

#pragma unsafe arrays
    while(1) 
    { 
        AUD_LRCLK when pinsneq(isLeft) :> isLeft @ time;

            if(porttimeafter(time, (short)(lrtime+64))) 
            {
                return -1;
            }
            lrtime = time;
        
        if (isLeft) 
        {
              
            {value, _}  = macs(  GetInt(buffer, readIndex), GetParam(19), 0, 0);
            value = bitrev( value );
            {value3, _}  = macs( GetInt( buffer, readIndex + 2 ), GetParam(21), 0, 0);
            value3 = bitrev(value3);
            {value5, _}  = macs( GetInt( buffer, readIndex + 4 ), GetParam(23), 0, 0);
            value5 = bitrev(value5);
            {value7, _}  = macs( GetInt( buffer, readIndex + 6 ), GetParam(25), 0, 0);
            value7 = bitrev(value7);
            
            //partout_timed(AUD_DOUT1,24,value,time);
            #pragma xta endpoint "deliverFinish"
            AUD_DOUT1 <: value;
            AUD_DOUT2 <: value3;
            AUD_DOUT3 <: value5;
            AUD_DOUT4 <: value7;   

            {value2, _}  = macs( GetInt(buffer,readIndex+1), GetParam(20), 0, 0);
            value2 = bitrev( value2);
            {value4, _}  = macs( GetInt( buffer, readIndex + 3 ), GetParam(22), 0, 0);
            value4 = bitrev(value4 );
            {value6, _}  = macs( GetInt( buffer, readIndex + 5 ), GetParam(24), 0, 0);
            value6 = bitrev(value6 );
            {value8, _}  = macs( GetInt ( buffer, readIndex + 7 ), GetParam(26), 0, 0);
            value8 = bitrev(value8 );

            //playSampleRate = GetParam(9);
            //playSampleRate = MAGIC / playSampleRate;

            //played += dacSampleRate;
            //while (played >= playSampleRate) {
               //played -= playSampleRate;
            if (readIndex + 8 < dataLength) 
            {
                readIndex += 8;
            }
            else
            {
                /* Replay if not new buffer */
                if(GetData() != oldData)
                  return 1;
                if(GetParam(9) != i_CurSampFreq) {
                   return -1;
                }
            }

        } 
        else 
        {
            //partout_timed(AUD_DOUT2,24,value2,time);
            AUD_DOUT1 <: value2;
            AUD_DOUT2 <: value4;
            AUD_DOUT3 <: value6;
            AUD_DOUT4 <: value8;
        }
    
    }
}



extern void audio_init(out port AUD_RESET_N, port AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, in port AUD_LRCLK, clock AUD_BIT_CLOCK);

void physical(chanend otherSide, out port p_aud_rst, port AUD_BCLK, port AUD_SCLK, port AUD_SDIN,
                   in port AUD_LRCLK, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, clock clk_aud_bit) 
{  
    unsigned time;
    timer t;
    int value, count = 0;
    int tmp;
    int i_CurSampFreq = GetParam(9);  /* Default sample freq: 96k */
    int dataLength;
    int i_tmp;
    int loop = 1;
    unsigned buffers[3];
    unsigned bufferAdr;
    int shift;
    int go;
    buffers[0] = GetBufferAddress(bufferA);
    buffers[1] = GetBufferAddress(bufferB);
    buffers[2] = GetBufferAddress(bufferC);
    
    /* Reset codec */
    p_aud_rst <: 0;
    t :> time;
    t when timerafter( time + 100000 ) :> int _;
    p_aud_rst <: 1;

    /******************* Configure codec *******************************/

    /* Interface Formats Register (Address 04h)             */
    /* 0    Freeze Controls     (FREEZE) = 0,               */
    /* 1    Auxiliary Digital Interface Format (AUX_DIF)  = 0, */
    /* 2:4  DAC Digital Interface Format       (DAC_DIF)  = 000 (Left justfied) */
    /* 5:7  ADC Digital Interface Format       (ADC_DIF)  = 1 (I2S) */
    tmp = 0b00010001;
    swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x4, tmp, AUD_SCLK, AUD_SDIN);
  
    /* ADC Control & DAC De-Emphasis (Address 05h) */ 
    /* 0   ADC1-2_HPF FREEZE = 0, */
    /* 1   ADC3_HPF FREEZE = 0, */ 
    /* 2   DAC_DEM = 0, */
    /* 3   ADC1_SINGLE = 1(single ended), */
    /* 4   ADC2_SINGLE = 1, */
    /* 5   ADC3_SINGLE = 1, */
    /* 6   AIN5_MUX = 0, */
    /* 7   AIN6_MUX = 0 */ 
    tmp = 0x1C;
    value = swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x5, tmp, AUD_SCLK, AUD_SDIN);

    /**********************Codec config end *****************************/  

 
    /*************** Initialise external clock sync chip ****************/
    /* Enable init */
    value = swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x03, 0x01, AUD_SCLK, AUD_SDIN);
    value = swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x05, 0x01, AUD_SCLK, AUD_SDIN);

    value = swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x16, 0x10, AUD_SCLK, AUD_SDIN);

    value = swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x17, 0x00, AUD_SCLK, AUD_SDIN);

    /************************ Init sync enc *****************************/

    while(1)
    {
        /* Set Code sample freq */
        /* Functional Mode Register (Address 03h)                */
        /* 0:1  DAC Functional Mode (DAC_FM) = 01 (double speed) */
        /* 2:3  ADC Functional Mode (ADC_FM) = 01 (double speed) */
        /* 4:6  MCLK Frequency      (MFREQ)  = 100:  MCLK/512    */
        /*   7  Reserved  */
        i_CurSampFreq = GetParam(9);  /* Default sample freq: 96k */
        shift = 64;
        switch(i_CurSampFreq)
        {
            case 44100:
                /* Multiplier: 100Hz -> 22.5792MHz */
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x6E, AUD_SCLK, AUD_SDIN); // 
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x40, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "

                i_tmp = 0b0001000;
                swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x3, i_tmp, AUD_SCLK, AUD_SDIN);

               break;

            case 48000:
                /* Multiplier: 100Hz -> 49.1520MHz */
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x78, AUD_SCLK, AUD_SDIN); // 100Hz -> 49.1520MHz
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "

                i_tmp = 0b00001000;
                swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x3, i_tmp, AUD_SCLK, AUD_SDIN);
                break;

            case 96000:
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x78, AUD_SCLK, AUD_SDIN); // 100Hz -> 49.1520MHz
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "

                i_tmp = 0b01011000;
                swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x3, i_tmp, AUD_SCLK, AUD_SDIN);
                break;

            case 192000:
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x78, AUD_SCLK, AUD_SDIN); // 100Hz -> 49.1520MHz
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, AUD_SCLK, AUD_SDIN); // "
                swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, AUD_SCLK, AUD_SDIN); // "
                i_tmp = 0b10101000;
                //i_tmp = 0b01011000;
                /* TODO CURRENTLY 192k running at 96 */
                swc_audio_codec_ctl_reg_wr(CODEC_DEV_ADR, 0x3, i_tmp, AUD_SCLK, AUD_SDIN);
                //shift = 62;
                break;

            default:
                XUD_Error("Unknown samp freq");
                break;
        
        }
        
        go = 1;
        loop = 1;

        /* Wait for first buffer or samp freq change */
        while(loop)
        {
            select
            {
                case inuint_byref(otherSide, dataLength):
                    bufferAdr = inuint(otherSide);
                    bufferAdr = buffers[bufferAdr];
                    loop = 0; 
                    break;

                default:
                    if(i_CurSampFreq != GetParam(9))
                    {
                        go = 0;
                        loop = 0;
                    }
                    break;
            }
        }

        /* Get if we got first buffer */
        if(go)
        {
            clearbuf(AUD_DOUT1);
            clearbuf(AUD_DOUT2);
            clearbuf(AUD_DOUT3);
            clearbuf(AUD_DOUT4);

            AUD_LRCLK  when pinsneq(1) :> isLeft;
            AUD_LRCLK  when pinsneq(0) :> isLeft;
            AUD_LRCLK  when pinsneq(1) :> isLeft;
            #pragma xta endpoint "physicalStart"
            AUD_LRCLK  when pinsneq(0) :> isLeft @ lrtime;
            
            lrtime += shift;
            AUD_DOUT1 @ lrtime <: 0;
            AUD_DOUT2 @ lrtime <: 0;
            AUD_DOUT3 @ lrtime <: 0;
            AUD_DOUT4 @ lrtime <: 0;

            /* Send first buffer */
            // deliver(dataLength, bufferAdr, AUD_LRCLK, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4, i_CurSampFreq);

            /* TODO: Use channel instead of memory poll */
            while(1)
            {
                if (deliver(dataLength, bufferAdr, AUD_LRCLK, AUD_DOUT1, AUD_DOUT2, AUD_DOUT3, AUD_DOUT4, i_CurSampFreq) == -1) {
                    break;
                }
                inuint_byref(otherSide, dataLength);
                bufferAdr = inuint(otherSide);
                bufferAdr = buffers[bufferAdr]; 
            }
        }
    }
}

void XUD_Endpoint1(chanend c, out port p_aud_rst, port AUD_BCLK, port AUD_SCLK, port AUD_SDIN,
                   in port AUD_LRCLK, buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, clock clk_aud_bit) 
{  
    unsigned time;
    chan otherSide; 
    timer t;
    int WrData;
    int value;
    int tmp;
    int i_CurSampFreq = GetParam(9);  /* Default sample freq: 96k */
   
    int dataLengthA, dataLengthB;
    int i_tmp;

    /* Initialise control ports */
    AUD_SCLK <: 1;
    AUD_SDIN <: 1;

    /* Generate bit clock block from pin */
    configure_clock_src(clk_aud_bit, AUD_BCLK);
  
   
    /* Clock ports from clock block */
    configure_out_port_no_ready(AUD_DOUT1, clk_aud_bit, 0);
    configure_out_port_no_ready(AUD_DOUT2, clk_aud_bit, 0);
    configure_out_port_no_ready(AUD_DOUT3, clk_aud_bit, 0);
    configure_out_port_no_ready(AUD_DOUT4, clk_aud_bit, 0);    
    configure_out_port_no_ready(AUD_LRCLK, clk_aud_bit, 0);
   
    /* Start clock */
    start_clock(clk_aud_bit);
          
    par 
    {
        readData(c, bufferA, bufferB, bufferC, otherSide);
        physical(otherSide, p_aud_rst, AUD_BCLK, AUD_SCLK, AUD_SDIN,
                    AUD_LRCLK, AUD_DOUT1,   AUD_DOUT2,  AUD_DOUT3,  AUD_DOUT4, clk_aud_bit);
    }  
}



#define USB_CORE 1
#define AUD_CORE 2

on stdcore[0] : in port p_but = XS1_PORT_4F;

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
on stdcore[1] : out port p_test         = XS1_PORT_1D;
on stdcore[1] : out port p_test2       = XS1_PORT_1C;
on stdcore[1] : clock clk = XS1_CLKBLK_3;

// Audio I/F codec ports
on stdcore[AUD_CORE] : port p_aud_sclk                  = XS1_PORT_1A;
on stdcore[AUD_CORE] : in port p_aud_lrclk              = XS1_PORT_1B;    
on stdcore[AUD_CORE] : port p_aud_scl                   = XS1_PORT_4B;    //  2-wire configuration interface.
on stdcore[AUD_CORE] : port p_aud_sda                   = XS1_PORT_4A;    
on stdcore[AUD_CORE] : out port p_aud_rst               = XS1_PORT_4C;    
on stdcore[AUD_CORE] : out port p_aud_clk 	            = XS1_PORT_1N;
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac1  = XS1_PORT_1C;
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac2  = XS1_PORT_1D; 
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac3  = XS1_PORT_1E;  
on stdcore[AUD_CORE] : buffered out port:32 p_aud_dac4  = XS1_PORT_1F; 
on stdcore [AUD_CORE] : clock clk_aud_bit               = XS1_CLKBLK_3; // TODO: move this out

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

void ep0_thread( chanend, chanend );

void audio(chanend leftOut, chanend rightOut, chanend middleOut, out port AUD_RESET_N, port AUD_BCLK, in port AUD_LRCLK, buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, port AUD_SCLK, port AUD_SDIN) ;

int main()
{
    chan c_sof;
    chan x;
     
    chan c_ep0_dat, c_ep0_ctl;   /* TODO we dont need this ctl chan */
    chan c_ep1_dat;
    chan c_ep2_dat;
    chan c_ep3_dat;

    par
    {
        on stdcore[0] : XUD_Endpoint3( c_ep2_dat, p_but );

        on stdcore[1] : XUD_Manager( c_ep0_dat, c_ep1_dat, c_ep2_dat, c_ep3_dat,  c_sof, p_test, p_test2);  
        
        on stdcore[2] : ep0_thread( c_ep0_dat, c_ep0_ctl );

          
        on stdcore[2] : XUD_Endpoint1(c_ep1_dat, p_aud_rst, p_aud_sclk, p_aud_scl, p_aud_sda, 
                        p_aud_lrclk,p_aud_dac1, p_aud_dac2, p_aud_dac3, p_aud_dac4, clk_aud_bit);

        on stdcore[2] : doClock(c_sof, p_aud_clk);

    }

    return 0;
}








