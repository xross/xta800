/**
 * @file main.xc
 * @brief XMOS L1 USB 2,0 Audio Refence Design.  Top level.
 * @author Ross Owen, XMOS Semiconductor Ltd 
 * @version 0.1
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

#define USB_CORE 0

/* Endpoint type tables */
XUD_EpType epTypeTableOut[XUD_EP_COUNT] =   {XUD_EPTYPE_CTL, 
                                            XUD_EPTYPE_ISO, 
                                            XUD_EPTYPE_DIS, 
                                            XUD_EPTYPE_DIS, 
                                            XUD_EPTYPE_DIS, 
                                            XUD_EPTYPE_DIS};

XUD_EpType epTypeTableIn[XUD_EP_COUNT] = {   XUD_EPTYPE_CTL,
                                             XUD_EPTYPE_ISO, 
                                             XUD_EPTYPE_ISO,
                                             XUD_EPTYPE_DIS, 
                                             XUD_EPTYPE_DIS, 
                                             XUD_EPTYPE_DIS};

/* USB Port declarations */
on stdcore[USB_CORE] : in port  p_usb_clk       = UIFM_USB_CLK_PORT;
on stdcore[USB_CORE] : out port reg_write_port  = UIFM_REG_WRITE_PORT;
on stdcore[USB_CORE] : in  port reg_read_port   = UIFM_REG_READ_PORT;
on stdcore[USB_CORE] : in  port flag0_port      = UIFM_FLAG_0_PORT;
on stdcore[USB_CORE] : in  port flag1_port      = UIFM_FLAG_1_PORT;
on stdcore[USB_CORE] : in  port flag2_port      = UIFM_FLAG_2_PORT;
on stdcore[USB_CORE] : out port p_usb_txd       = UIFM_TX_DATA_PORT;
on stdcore[USB_CORE] :     port p_usb_rxd       = UIFM_RX_DATA_PORT;
#ifdef G1
on stdcore[USB_CORE] : out port p_usb_rst       = XS1_PORT_1I; // XDK PORT_1B, XTR: 1k, XVB: 1D, XTAG: 1I
#else
on stdcore[USB_CORE] : out port p_usb_rst       = XS1_PORT_1K; // XDK PORT_1B, XTR: 1k
#endif

on stdcore[USB_CORE] : clock    clk             = XS1_CLKBLK_3;  // USB clock

// audio IOs
on stdcore[0] : buffered out port:32 p_sdout = XS1_PORT_1D;  // also OSC_SEL
on stdcore[0] : buffered in port:32 p_sdin = XS1_PORT_1A;  
on stdcore[0] : buffered out port:32 p_lrclk = XS1_PORT_1C;
on stdcore[0] : buffered out port:32 p_bclk = XS1_PORT_1K;
on stdcore[0] : port p_mclk = XS1_PORT_1M;
on stdcore[0] : port p_codec_rst_n = XS1_PORT_1J;  // also CLK for OSC_SEL D-type
on stdcore[0] : buffered out port:32 p_spdif_tx = XS1_PORT_1L;


on stdcore[0] : port p_spi_flash_ss = XS1_PORT_1B;
	
on stdcore[USB_CORE] : clock    clk_audio_mclk             = XS1_CLKBLK_1;  
on stdcore[USB_CORE] : clock    clk_audio_bclk             = XS1_CLKBLK_2;  

	
int XUD_SetData(chanend c, unsigned char buffer[], unsigned datalength, unsigned pidToggle, unsigned startIndex);

void undangle(chanend a,chanend a1, chanend a2, chanend a3){}
void Endpoint0( chanend c_ep0_out, chanend c_ep0_in);


//
// Configure all the audio clocks
//
// bclk clocked off mclk
// all other buffered ports clocked off bclk
//
void configure_audio()
{
	timer t;
	unsigned int time;
		
	stop_clock(clk_audio_mclk);
	stop_clock(clk_audio_bclk);
	
  configure_clock_src(clk_audio_mclk, p_mclk);
  //configure_clock_ref(clk_audio_mclk, 4);
  
  configure_out_port_no_ready(p_bclk, clk_audio_mclk, 0);

  // Generate bit clock block from pin 
  configure_clock_src(clk_audio_bclk, p_bclk);
    
  // Clock ports from clock block 
  configure_out_port_no_ready(p_sdout, clk_audio_bclk, 0);	
  configure_in_port_no_ready(p_sdin, clk_audio_bclk);	
  configure_out_port_no_ready(p_lrclk, clk_audio_bclk, 0);	
 
  start_clock(clk_audio_mclk);
  start_clock(clk_audio_bclk);
    
  // reset the codec and select the desired clk freq 
  p_codec_rst_n <: 0;
  	
  p_sdout <: 0xFFFFFFFF;  	// high = use 24.576 Mhz osc.  low = use 11.2896 Mhz osc.
  p_bclk <: 0x1;            // toggle bclk to output on sdout
  	
  t :> time;
  time += 5;
  t when timerafter(time) :> int _;
  	
  p_codec_rst_n <: 1;     // D-type will sample sdout on the rising edge of codec_rst_n
  	
  	
  // now reset the codec
  p_codec_rst_n <: 0;  	
  	
  // hold in reset for 1ms
  time += 100000;
  t when timerafter(time) :> int _;
  	
  p_codec_rst_n <: 1;
  	
}


static void audio(chanend c_in, chanend c_out) 
{
	unsigned int value, lValue[64], rValue[64];
	unsigned int isLeft;
	unsigned int dac_wr = 0, dac_rd = 32;
	unsigned int adc_lValue[16], adc_rValue[16];
	unsigned int adc_sampleCount = 0;
	unsigned int req;
  
  int Sample[48];
  int x = 0; int y=0;

  	
  p_spi_flash_ss <: 1;

Sample[0] = 0;
Sample[1] = 3421;
Sample[2] = 6784;
Sample[3] = 10031;
Sample[4] = 13106;
Sample[5] = 15957;
Sample[6] = 18535;
Sample[7] = 20796;
Sample[8] = 22701;
Sample[9] = 24217;
Sample[10] = 25320;
Sample[11] = 25989;
Sample[12] = 26213;
Sample[13] = 25989;
Sample[14] = 25320;
Sample[15] = 24217;
Sample[16] = 22701;
Sample[17] = 20796;
Sample[18] = 18535;
Sample[19] = 15957;
Sample[20] = 13106;
Sample[21] = 10031;
Sample[22] = 6784;
Sample[23] = 3421;
Sample[24] = 0;
Sample[25] = -3421;
Sample[26] = -6784;
Sample[27] = -10031;
Sample[28] = -13106;
Sample[29] = -15957;
Sample[30] = -18535;
Sample[31] = -20796;
Sample[32] = -22701;
Sample[33] = -24217;
Sample[34] = -25320;
Sample[35] = -25989;
Sample[36] = -26213;
Sample[37] = -25989;
Sample[38] = -25320;
Sample[39] = -24217;
Sample[40] = -22701;
Sample[41] = -20796;
Sample[42] = -18535;
Sample[43] = -15957;
Sample[44] = -13106;
Sample[45] = -10031;
Sample[46] = -6784;
Sample[47] = -3421;


	
  /* Initialise buffers */	
	for(int i = 0 ;i < 64; i++) 
  {
	  lValue[i] = rValue[i] = 0;
  }
	for(int i = 0 ;i < 8; i++) 
  {
	  adc_lValue[i] = adc_rValue[i] = 0;
  }
 
  /* Configure ports and reset codec (also sets sample freq) */ 
	configure_audio();
	
	clearbuf(p_bclk);
	clearbuf(p_lrclk);
	clearbuf(p_sdout);
	clearbuf(p_sdin);
	
 
#if 0 
  p_lrclk <: 0xFFFFFFFF;
  p_sdout <: 0;
  
  p_bclk <: 0xAAAAAAAA;
  p_bclk <: 0xAAAAAAAA;
  p_bclk <: 0xFFFFFFFF;
  p_bclk <: 0xFFFFFFFF;		
  p_bclk <: 0xFFFFFFFF;
  p_bclk <: 0xFFFFFFFF;		
  p_bclk <: 0xFFFFFFFF;
  p_bclk <: 0xFFFFFFFF;		
  p_sdin :> int _;
#endif 
  	
	while (1)
	{	
    x = Sample[y] << 16; 

	/* Left output (MSB first0 */
    #pragma xta endpoint "left_output"
    p_sdout <: bitrev(lValue[dac_rd]);   
                               
    /* LR clock delayed by one clock, This is so MSB is output on the falling edge of BCLK after the falling edge on which LRCLK was toggled. (see I2S spec) */
    p_lrclk <: 0x80000000;

    /* Clock out data (and LR clock) */
    p_bclk <: 0xAAAAAAAA;
    p_bclk <: 0xAAAAAAAA;
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		

    /* Left input, store to buffer */
	p_sdin :> value;
	//adc_lValue[adc_sampleCount] = bitrev(value)<<1;
	adc_lValue[adc_sampleCount] = (x);
    
    /* Right output */
    #pragma xta endpoint "right_output"
    p_sdout <: bitrev(rValue[dac_rd]);  
           
    /* Increment DAC buffered read pointer */                    
    if(dac_rd != dac_wr) 
    {
      dac_rd++;
      dac_rd &= 0x3F;
    }
            
    p_lrclk <: 0x7FFFFFFF;

    p_bclk <: 0xAAAAAAAA;
    p_bclk <: 0xAAAAAAAA;
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		
    p_bclk <: 0xFFFFFFFF;
    p_bclk <: 0xFFFFFFFF;		
		
	  p_sdin :> value;
	  //adc_rValue[adc_sampleCount] = bitrev(value) << 1;
	  adc_rValue[adc_sampleCount] = (x);
    
    y ++;
    if(y == 48)
        y = 0;

	  adc_sampleCount++;
	  adc_sampleCount &= 0xF;
	  
    /* Check for new data from host */	
    select
    {
        case inuint_byref(c_in, lValue[dac_wr]):
        {
            rValue[dac_wr] = inuint(c_in);
      	    dac_wr++;
      	    dac_wr &= 0x3F;
      	
      	    /* If write pointer is overtaking read, move it back by half the buffer */
      	    if (dac_wr == dac_rd)
      	    {
      		      dac_wr = dac_wr ^ 0x20;
            }		
        
      	    break;
        }
      		
        default:
        {
            break;
        }
    } 
   
        /* Check if buffer thread is ready to accept data */ 
        select
        {
            case inuint_byref(c_out, req):
            {
      	        outuint(c_out, adc_sampleCount);
      	        for (int i=0; i<6; i++)
      	        {
      		          outuint(c_out, adc_lValue[i]);
      		          outuint(c_out, adc_rValue[(i)]);
                    adc_sampleCount = 0;
      	        }
      	
      	        break;
            }
            default:
            {
        	      break;    	
            }
        }   
	  }
}


/* Async Feedback EP (Isochronous) */
void Endpoint1_in(chanend c_ep1_in)
{
    unsigned char buffer[] = {0, 0x00, 0x06, 0};

    while(1)
    {
        XUD_SetData(c_ep1_in, buffer, 4, 0, 0); // no pid toggle, 0 start index

    }
}

/* Recieve audio from audio thread, write into buffer and forward */
void Endpoint2_in(chanend c_ep2_in, chanend c_aud_adc)
{
    unsigned char buffer[1024];
    unsigned int sampleCount, oldSampleCount;
    unsigned int newSamples;
    
    for ( int i = 0; i < 1024; i++ )
    {
        buffer[i] = 0;
    }

    while(1)
    { 
    	int i;
        XUD_SetData(c_ep2_in, buffer, 48, PIDn_DATA0, 0); // no pid toggle, 0 start index
     
        /* TODO SHOULD BE: XUD_SetBuffer(c_ep2_in, buffer, 48); // no pid toggle, 0 start index 
        requires ep type check in xud_lite */
        outuint(c_aud_adc, 0);  // get sample count from audio thread
        sampleCount = inuint(c_aud_adc);
        
        
        	
        /* Receive samples into buffer */
        for(i = 0; i < 6; i++)
        {
          (buffer,unsigned[])[(i*2)] = inuint(c_aud_adc);
          (buffer,unsigned[])[(i*2)+1] = inuint(c_aud_adc);
        }           
        
    }
}

/* Read audio data from buffers and forward to audio thread */
void Endpoint1_out(chanend c_ep1_out, chanend c_aud_dac)
{
    unsigned buffer[1024];
    unsigned datalength;
    unsigned time;
    timer t;

    while(1)
    {
        datalength = XUD_GetBuffer_u32(c_ep1_out, buffer); 

        for(int i = 0; i < 12; i+=2)
        {
          outuint(c_aud_dac, buffer[i]);
          outuint(c_aud_dac, buffer[i+1]);
          t :> time;
          t when timerafter(time+100) :> void;
        }            
    }
}


int main()
{
    chan c_sof;
    chan c_aud_dac, c_aud_adc;
     
    chan c_ep0_out, c_ep0_in; 
    chan c_ep1_out;
    chan c_ep2_out;
    chan c_ep3_out;
    chan c_ep1_in;
    chan c_ep2_in;
    chan c_ep3_in;

    par
    {

        /* USB Interface, must be on its own core */
        on stdcore[USB_CORE] : XUD_Manager( c_ep0_out, c_ep0_in, 
                                            c_ep1_out, c_ep1_in, 
                                            c_ep2_out, c_ep2_in,   
                                            c_ep3_out, c_ep3_in, 
                                            c_sof, epTypeTableOut, epTypeTableIn);  
        /* Endpoint 0 */
        on stdcore[USB_CORE] : Endpoint0( c_ep0_out, c_ep0_in);

        on stdcore[USB_CORE] : Endpoint2_in(c_ep2_in, c_aud_adc);
        on stdcore[USB_CORE] : Endpoint1_out(c_ep1_out, c_aud_dac);
        on stdcore[USB_CORE] : Endpoint1_in(c_ep1_in);
     
        //on stdcore[USB_CORE] : dacFast(c_aud) ;
        on stdcore[USB_CORE] : audio(c_aud_dac, c_aud_adc) ;
        
        
    }

    return 0;
}








