#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "interface.h"

#include "xud.h"
#include "spdif.h"
#include "codec.h"
#include "codec_akm.h"

#define SYNC_DEV_ADR                (0x9c) // Address of external clock sync chip 
#define AKM_CODEC_DEV_ADRS          (2)

int GetParam(int);
typedef buffered  in port:32 inPort;
typedef buffered out port:32 outPort;
typedef buffered out port:32 clkPort;

on stdcore[2] : port p_CTRL = XS1_PORT_4A;




void fakedata(chanend c_adat, chanend c_spdif, chanend c_i2s) {
    while (1) {
        outuint(c_adat,  0xF0F0F0F0);
        outuint(c_spdif, 0xFF00FF00);
        for(int i = 0; i < 8; i++) {
            outuint(c_i2s, 0xF0000000 | (1<<(2*i+4)));
        }
        outuint(c_adat,  0xF0F0F0F0);
        outuint(c_spdif, 0xFF00FF00);
        for(int i = 0; i < 8; i++) {
            inuint(c_i2s);
        }
        for(int i = 0; i < 6; i++) {
            outuint(c_adat,  0xF0F0F0F0);
            outuint(c_spdif, 0xFF00FF00);
        }
    }
}

extern int SetMclkFreq(int current, int required, port SCLK, port SDIN);


void physical(outPort ADAT_TX, outPort SPDIF_TX, out port p_aud_rst,
              buffered out port:32 BCLK, port SCLK, port SDIN, buffered out port:32 LRCLK,
              buffered out port:32 DOUT1,  buffered out port:32 DOUT2,  buffered out port:32 DOUT3, buffered out port:32 DOUT4,  buffered in port:32 DIN1,  buffered in port:32 DIN2,  buffered in port:32 DIN3,  buffered in port:32 DIN4, clock clk_aud_bit, chanend c_adat, chanend c_spdif, chanend c_i2s_in, chanend c_i2s_out) 
{  
    unsigned time;
    timer t;
    int curSampFreq = 192000; //GetParam(0);  /* Default sample freq: 96k */
    int curMclkFreq;                /* Current master clock frequency */
    int low, high;

    p_CTRL <: 0xf;
    p_aud_rst <: 0;

    /*************** Initialise external clock sync chip ****************/
    
    /* Enable init */
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x03, 0x01, SCLK, SDIN);
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x05, 0x01, SCLK, SDIN);
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x16, 0x10, SCLK, SDIN);
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x17, 0x00, SCLK, SDIN);
    
    /* 100Hz -> 49.1520MHz */   
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x06, 0x78, SCLK, SDIN); 
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x07, 0x00, SCLK, SDIN); 
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x08, 0x00, SCLK, SDIN); 
    swc_audio_codec_ctl_reg_wr(SYNC_DEV_ADR, 0x09, 0x00, SCLK, SDIN); 

    /*************** Sync chip config end ******************************/
    
    /* TODO Wait for MCLK, Codec requred Mclk for config */
    t :> time;
    t when timerafter( time + 100000000 ) :> int _;

    /* Reset codec */
    p_aud_rst <: 0;
    t :> time;
    t when timerafter( time + 100000 ) :> int _;
    p_aud_rst <: 1;

    /******************* Configure codec ********************************/
   
    /* Power Down Control (Address 00h)                                 */
    /* 0  PWDA: DAC Power Down                                          */
    /* 1  PWAD: ADC Power Down                                          */
    /* 2  PWVR: Vref Power Down                                         */
    /* 3  Reserved                                        : 0           */
    /* 4  ZOS : Zero/Overflow detect :  Overflow for ADC  : 0           */
    /*                                  Zero for DAC      : 1           */
    /* 5  ZOE : Zero/Over detect disable                                */
    /* 6  DZFB: Inverting Enable of DZF                                 */
    /* 7  SLOW: DAC Slow roll-off filter enable                         */ 
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x0,0b00100111, p_CTRL, p_aud_rst);

    /* Deem and Volume Control */
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x3,0x19, p_CTRL, p_aud_rst);
    
    /* Lch IPGA control */
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x4,0x80, p_CTRL, p_aud_rst);
    
    /* Rch IPGA Control */
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x5,0x80, p_CTRL, p_aud_rst);
    
    /* Lch OATT Control */
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x6,0xFF, p_CTRL, p_aud_rst);
    
    /* Rch OATT Control */
    swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x7,0xFF, p_CTRL, p_aud_rst);
    
    /******************* Codec config end ******************************/  

    while(1)
    {
        curSampFreq = inuint(c_i2s_in);
        /* Set reset bits in codec */
        /* TODO Power down bits */
        /* TODO Wait for Mclk */
        swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x1, 0x0C, p_CTRL, p_aud_rst);

        switch(curSampFreq)
        {
            case 44100:
                curMclkFreq = SetMclkFreq(curMclkFreq, 45, SCLK, SDIN);
                swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x2,0x48, p_CTRL, p_aud_rst); // Normal Speed 
                low = 4;
                high = 5;
                break;

            case 48000:
                curMclkFreq = SetMclkFreq(curMclkFreq, 49, SCLK, SDIN);
                swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x2,0x48, p_CTRL, p_aud_rst); // Normal Speed
                low = 4;
                high = 6;
                break;

            case 96000:
                curMclkFreq = SetMclkFreq(curMclkFreq, 49, SCLK, SDIN);
                swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x2,0x49, p_CTRL, p_aud_rst); // Double Speed
                low = 10;
                high = 12;
                break;

            case 192000: 
                curMclkFreq = SetMclkFreq(curMclkFreq, 49, SCLK, SDIN);
                swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x2,0x4A, p_CTRL, p_aud_rst); // Quad Speed
                low = 22;
                high = 24;
                break;

            default:
                printstr("Error: Unsupported sample freq ");
                printintln(curSampFreq);
                break;
        }        

        /* Codec out of (soft) reset */
        swc_audio_codec_ctl_reg_wr_akm_v2(AKM_CODEC_DEV_ADRS, 0x1, 0x0F, p_CTRL, p_aud_rst);

        /* Output sample rate to S/PDIF thread(s) */

        deliver(ADAT_TX, LRCLK, BCLK, SPDIF_TX, c_adat, c_spdif, c_i2s_in, c_i2s_out,
                DOUT1, DOUT2, DOUT3, DOUT4, DIN1, DIN2, DIN3, DIN4, curSampFreq);
    }
}

