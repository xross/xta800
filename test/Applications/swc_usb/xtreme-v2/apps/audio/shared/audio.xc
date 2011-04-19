
/**
 * @file audio.xc
 * @brief Implements the main USB Audio buffering.  Also includes feedback endpoint.
 * @author Ross Owen, XMOS Semiconductor Ltd 
 * @version 0.5 
 **/                               

//#define SLAVECLOCK
                                       
#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"
#include "XUD_UIFM_Defines.h"

#include "xud.h"
#include "spdif.h"
#include "codec.h"
#include "hid.h"
#include "sof_clock.h"
#include "endpoint0.h"

#include "interface.h"

#include "midiuart.xh"
#include "midi2usbmidi.xh"

#define USB_RECORD_CHANNELS 18
#define USB_PLAY_CHANNELS 18

int data; // TODO: get rid of this.

/* Audio Buffer Length */
#define ABL 256  

extern inline unsigned GetBufferAddress(int b[]);

extern int GetInt(unsigned array, int index);
extern void SetInt(unsigned array, int index, int val);
extern int doVolumeUsbIn(int a, int x);

int bufferRecord0[ABL];
int bufferRecord1[ABL];
int bufferRecord2[ABL];


#ifdef ASYNC

/* Provides feedback for async OUT */
void feedbackEndpoint(unsigned epNum, chanend c, chanend c_time) {
    int feedBackBuffer[1] = { 0x060000 } ;

    while(1) {
        XUD_SetBuffer_ResetPid_int(c, 1, feedBackBuffer, 4, PIDn_DATA0); 
        outct(c_time, XS1_CT_END);
        feedBackBuffer[0] = inuint(c_time);
    }
}

#else // not async

/* Provides feedback for async OUT */
void feedbackEndpoint(unsigned epNum, chanend c, chanend c_time)
{
    int feedBackBuffer[1] = { 0x180000 } ;
    while(1) {
        XUD_SetBuffer_ResetPid_int(c, 1, feedBackBuffer, 4, PIDn_DATA0);
    }
}


#endif

extern int channelContainsControlToken(chanend);

#define HALF_BUFFER_ELEMENTS  (24)
#define BUFFER_ELEMENTS  (HALF_BUFFER_ELEMENTS * 2)
#define BUFFER_SIZE      (USB_PLAY_CHANNELS * BUFFER_ELEMENTS)
#define HALF_BUFFER      (BUFFER_SIZE >> 1)

void playDecoupler(chanend fromEndPoint, chanend playChannel, chanend c_time, chanend c_errorCounters) {
    int buffer[BUFFER_SIZE];
    int rd = 0, wr = 1;
    unsigned int len = 0;
    unsigned int tmp;
    unsigned char speed = 0, tmpCT;
    int integratedSamplesStillToBePlayed = 0;
    const unsigned expectedSpeeds[5] = {(44100 << 13) / 1000,
                                         (48000 << 13) / 1000,
                                         (96000 << 13) / 1000,
                                        0,                          // 3 has no meaning
                                         (192000 << 13) / 1000};
#pragma unsafe arrays
    for(int i = 0; i < BUFFER_SIZE; i++) {
        buffer[i] = 0;
    }

#pragma unsafe arrays
    while (1) {
#pragma ordered
        select {
        case inuchar_byref(playChannel, speed):      // signals that mixer needs data
            chkct(playChannel, 1);         // signals that mixer needs data
#pragma loop unroll(18)
            for(int i = USB_PLAY_CHANNELS-1; i >=0; i--) {
                outuint(playChannel, buffer[rd+i]);
            }
            outct(playChannel, XS1_CT_PAUSE);
            if (len >= 1) {
                len -= 1;
                rd += USB_PLAY_CHANNELS;
                if (rd >= BUFFER_SIZE) {
                    rd = 0;
                }
            }
            integratedSamplesStillToBePlayed --;
            if (integratedSamplesStillToBePlayed < -256) {
                integratedSamplesStillToBePlayed = -256;
            }
            break;
        case inuint_byref(fromEndPoint, tmp):      // signals that endpoint has data
            if (len != BUFFER_ELEMENTS-1) {                             // TODO: POP AND CLICK! - USB_PLAY_CHANN
                buffer[wr++] = tmp;
#pragma loop unroll(18)
                for(int i = 0; i < USB_PLAY_CHANNELS-1; i++) {
                    buffer[wr++] = inuint(fromEndPoint);
                }
                if (wr >= BUFFER_SIZE) {
                    wr = 0;
                }
                len++;
            } else {
#pragma loop unroll(18)
                for(int i = 0; i < USB_PLAY_CHANNELS-1; i++) {
                    inuint(fromEndPoint);
                }
                len -= HALF_BUFFER_ELEMENTS;
                if  (wr >= HALF_BUFFER) {
                    wr -= HALF_BUFFER;
                } else {
                    wr += HALF_BUFFER;
                }
            }
            integratedSamplesStillToBePlayed ++;
            if (integratedSamplesStillToBePlayed > 256) {
                integratedSamplesStillToBePlayed = 256;
            }
            break;
        case inct_byref(c_errorCounters, tmpCT):      // signals missing packet
#define N 6
            if (tmpCT == 4) {
                integratedSamplesStillToBePlayed += N;
                if (integratedSamplesStillToBePlayed > 256) {
                    integratedSamplesStillToBePlayed = 256;
                }
            } else {
                integratedSamplesStillToBePlayed -= N;
                if (integratedSamplesStillToBePlayed < -256) {
                    integratedSamplesStillToBePlayed = -256;
                }
            }
            break;
        case inct_byref(c_time, tmpCT):      // signals that feedback thread wants feedback info
            outuint(c_time,expectedSpeeds[speed]  - (integratedSamplesStillToBePlayed << 5));
            outct(c_time, XS1_CT_PAUSE);
            break;
        }
    }
}

/* Gets buffer from host and does volume control over this buffer */
void playEndpointHandler(chanend c, chanend toDecoupler) {
    int buffer[ABL];
    int datalength;
    
    while (1) {
        /* Get data from host */
        datalength = XUD_GetBuffer_int(c, 1, buffer);
    
        /* Check for reset */
        if (datalength != -1)  {
            /* Datalength in words */
            datalength = datalength >> 2;        
            for(int i = 0; i < datalength; i ++) {
                outuint(toDecoupler, buffer[i]);
            }
        }
    }
}

void playEndpoint(chanend c, chanend playChannel, chanend c_time, chanend c_errorCounters) 
{
    chan toDecoupler;
    par {
        playEndpointHandler(c, toDecoupler);
        playDecoupler(toDecoupler, playChannel, c_time, c_errorCounters);
    }
}

/* Runs volume over a buffer and sends buffer to host */
void doRecordBuffer(chanend c, int buffLength, int buffer[ABL]) {
    timer t;
    int t0;
#if 0
    t :> t0;
    t when timerafter(t0 + 12000) :> int _;
#endif

#if 1
    /* Do volume over buffer */
    for(int i = 0; i < buffLength; i+=USB_RECORD_CHANNELS) 
    {
        for(int j = 0; j < USB_RECORD_CHANNELS - 2; j++) 
        {
            buffer[i+j] = doVolumeUsbIn(buffer[i+j], j+1);
        }
    }
#endif

    /* Convert number of words to bytes */
    buffLength = buffLength << 2;   
    
    /* Send buffer to host */ 
    XUD_SetBuffer_ResetPid_int(c, 2, buffer, buffLength, PIDn_DATA0);
}

void recordEndpoint(chanend c, chanend deliverThread)
{ 
    int buffLength;
    unsigned char buffAddr;
    unsigned int tmp;

    while(1)
    {
        select
        {  
            case inuchar_byref(deliverThread, buffAddr):
                buffLength = inuchar(deliverThread);
                outct(deliverThread, 1);

                /* Convert number of samples to words */
                buffLength = buffLength + USB_RECORD_CHANNELS;

                switch(buffAddr) 
                {
                    case 0:
                        doRecordBuffer(c, buffLength, bufferRecord0);
                        break;
                    
                    case 1:
                        doRecordBuffer(c, buffLength, bufferRecord1);
                    break;
                    
                    case 2:
                        doRecordBuffer(c, buffLength, bufferRecord2);
                    break;
                    
                    default:
                        printstr("BUFFADDR unknown\n");
                        printintln(buffAddr);
                    break;
                }
                break;

            case inuint_byref(c, tmp):
                chkct(c, XS1_CT_END);
                outct(c, XUD_CT_ACK);
                outct(c, XS1_CT_END);
                break;
        }
    }
}

extern void spdif_tx(out buffered port:32 p, chanend c_tx);

#define SYNC_DEV_ADR                (0x9c) // Address of external clock sync chip 
int SetMclkFreq(int current, int required, port SCLK, port SDIN)
{
		if (current == required)
				return current;

		switch(required)
		{
				case 45:
						/* 100Hz -> 45.1584MHz */
						swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x6E, SCLK, SDIN); 
            swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x40, SCLK, SDIN); 
            swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, SCLK, SDIN); 
            swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, SCLK, SDIN); 
						return required;

				case 49:
						/* 100Hz -> 49.1520MHz */
						swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x78, SCLK, SDIN); 
						swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x00, SCLK, SDIN); 
						swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, SCLK, SDIN); 
						swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, SCLK, SDIN); 
						return required;						
                default:
                        return current;						
		}
}



void audioPhysical(
    out port p_aud_rst, buffered out port:32 AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered out port:32 AUD_LRCLK,
    in port p_master_clock,
    buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4,
    buffered in port:32 AUD_DIN1,  buffered in port:32 AUD_DIN2,  buffered in port:32 AUD_DIN3,  buffered in port:32 AUD_DIN4,
    out buffered port:32 p_adatTx, out buffered port:32 p_spdifTx, 
    clock bitClockBlock,  clock masterClockBlock,
    chanend c_adatPhysical, chanend c_spdifPhysical, chanend c_i2sIn, chanend c_i2sOut) {     
    /* Initialise codec control ports */
    AUD_SCLK <: 1;
    AUD_SDIN <: 1;

    configure_clock_src(masterClockBlock, p_master_clock);

    configure_out_port_no_ready(AUD_BCLK, masterClockBlock, 0xffffffff);
    configure_out_port_no_ready(p_adatTx, masterClockBlock, 0xffffffff);
    configure_out_port_no_ready(p_spdifTx, masterClockBlock, 0xffffffff);
    start_clock(masterClockBlock);

    /* Generate bit clock block from pin */
    configure_clock_src(bitClockBlock, AUD_BCLK);
  
    /* Clock ports from clock block */
    configure_out_port_no_ready(AUD_LRCLK, bitClockBlock, 0);
    configure_out_port_no_ready(AUD_DOUT1, bitClockBlock, 0);
    configure_out_port_no_ready(AUD_DOUT2, bitClockBlock, 0);
    configure_out_port_no_ready(AUD_DOUT3, bitClockBlock, 0);
    configure_out_port_no_ready(AUD_DOUT4, bitClockBlock, 0);
    configure_in_port_no_ready(AUD_DIN1, bitClockBlock);
    configure_in_port_no_ready(AUD_DIN2, bitClockBlock);
    configure_in_port_no_ready(AUD_DIN3, bitClockBlock);
    configure_in_port_no_ready(AUD_DIN4, bitClockBlock);

    set_pad_delay(AUD_DOUT1, 0);
    set_clock_fall_delay(bitClockBlock, 0);


    /* Start clock */
    start_clock(bitClockBlock);

          
    physical(p_adatTx, p_spdifTx,
             p_aud_rst, AUD_BCLK, AUD_SCLK, AUD_SDIN, AUD_LRCLK,
             AUD_DOUT1,   AUD_DOUT2,  AUD_DOUT3,  AUD_DOUT4,
             AUD_DIN1,    AUD_DIN2,   AUD_DIN3,   AUD_DIN4, 
             bitClockBlock, c_adatPhysical, c_spdifPhysical, c_i2sIn, c_i2sOut);
}

