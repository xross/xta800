/**
 * @file audio_xdk.xc
 * @brief Implements a Audio class v2.0 in the form of a 8 in/out soundcard on XDK. Also implements extra HID EP
 * @warning Requires XMOS Audio Interface v2
 * @author Ross Owen, XMOS Semiconductor Ltd 
 * @version 0.2 
 **/                               

//#define SLAVECLOCK
//#define SPDIF 
                                       
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

// TODO Tidy
#include "midiuart.xh"
#include "midi2usbmidi.xh"


/* Endpoint type tables */
XUD_EpType epTypeTableOut[XUD_EP_COUNT] = {
                                            XUD_EPTYPE_CTL, 
                                            XUD_EPTYPE_ISO, /* ISO out linked to feedback pipe */
                                            XUD_EPTYPE_DIS, 
                                            XUD_EPTYPE_DIS, 
                                            XUD_EPTYPE_BUL, 
                                            XUD_EPTYPE_DIS};

XUD_EpType epTypeTableIn[XUD_EP_COUNT] = {   XUD_EPTYPE_CTL,
                                             XUD_EPTYPE_ISO, /* Feedback pipe for OUT */ 
                                             XUD_EPTYPE_ISO, /* ISO in pipe */
                                             XUD_EPTYPE_INT, 
                                             XUD_EPTYPE_DIS, 
                                             XUD_EPTYPE_BUL};

#define USB_CORE 0
#define MIXER_CORE 1
#define AUD_CORE 2
#define REST_CORE 3

on stdcore[1]: in port p_but = XS1_PORT_4F;

// USB Port declarations
on stdcore[USB_CORE]: in port  p_usb_clk      = UIFM_USB_CLK_PORT;
on stdcore[USB_CORE]: out port reg_write_port = UIFM_REG_WRITE_PORT;
on stdcore[USB_CORE]: in  port reg_read_port  = UIFM_REG_READ_PORT;
on stdcore[USB_CORE]: in  port flag0_port     = UIFM_FLAG_0_PORT;
on stdcore[USB_CORE]: in  port flag1_port     = UIFM_FLAG_1_PORT;
on stdcore[USB_CORE]: in  port flag2_port     = UIFM_FLAG_2_PORT;
on stdcore[USB_CORE]: out port p_usb_txd      = UIFM_TX_DATA_PORT;
on stdcore[USB_CORE]:     port p_usb_rxd      = UIFM_RX_DATA_PORT;
on stdcore[USB_CORE]: out port p_usb_rst      = XS1_PORT_1K; // XDK PORT_1B
on stdcore[USB_CORE]: out port p_test         = XS1_PORT_1D;
on stdcore[USB_CORE]: out port p_test2       = XS1_PORT_1C;
on stdcore[USB_CORE]: clock clk = XS1_CLKBLK_3;

// Audio I/F codec ports
on stdcore[AUD_CORE]: buffered out port:32 p_aud_sclk  = XS1_PORT_1A;
on stdcore[AUD_CORE]: buffered out port:32 p_aud_lrclk  = XS1_PORT_1B;    
on stdcore[AUD_CORE]: port p_aud_scl                    = XS1_PORT_4D;    // XDK: 4B//  2-wire configuration interface.
on stdcore[AUD_CORE]: port p_aud_sda                    = XS1_PORT_4B;    // XDK: 4A
on stdcore[AUD_CORE]: out port p_aud_rst                = XS1_PORT_4C;    
on stdcore[AUD_CORE]: out port p_aud_clk 	             = XS1_PORT_1N;    
on stdcore[AUD_CORE]: buffered out port:32 p_aud_dac1   = XS1_PORT_1C;
on stdcore[AUD_CORE]: buffered out port:32 p_aud_dac2   = XS1_PORT_1D; 
on stdcore[AUD_CORE]: buffered out port:32 p_aud_dac3   = XS1_PORT_1E;  
on stdcore[AUD_CORE]: buffered out port:32 p_aud_dac4   = XS1_PORT_1F;
on stdcore[AUD_CORE]: buffered in port:32 p_aud_adc1    = XS1_PORT_1G;
on stdcore[AUD_CORE]: buffered in port:32 p_aud_adc2    = XS1_PORT_1H; 
on stdcore[AUD_CORE]: buffered in port:32 p_aud_adc3    = XS1_PORT_1I; 
on stdcore[AUD_CORE]: buffered in port:32 p_aud_adc4    = XS1_PORT_1J;
on stdcore[AUD_CORE]: in port p_aud_dummy               = XS1_PORT_16A;

/* S/PDIF Ports */
on stdcore[AUD_CORE]: buffered in port:4 p_spdifRx        = XS1_PORT_1K;    //XDK: 1L
on stdcore[AUD_CORE]: buffered out port:32 p_spdifTx      = XS1_PORT_1L;    //XDK: 1K
on stdcore[AUD_CORE]: buffered out port:32 p_adatTx       = XS1_PORT_1O;
on stdcore[AUD_CORE]: buffered in port:32 p_adatRx        = XS1_PORT_1P;

on stdcore[AUD_CORE]: in port p_master_clock               = XS1_PORT_1M;    

/* MIDI Ports */
on stdcore[AUD_CORE]: out port p_midi_out                = XS1_PORT_4E;
on stdcore[AUD_CORE]: in port p_midi_in                  = XS1_PORT_4F;

on stdcore[AUD_CORE]: clock adat_in_clk                 = XS1_CLKBLK_5;
on stdcore[AUD_CORE]: clock spdif_in_clk                = XS1_CLKBLK_2;
on stdcore[AUD_CORE]: clock bitClockBlock               = XS1_CLKBLK_3; 
on stdcore[AUD_CORE]: clock masterClockBlock            = XS1_CLKBLK_4;
//on stdcore[AUD_CORE]: port p4f = XS1_PORT_8D;

extern void reportErrors(chanend);

void undangle(chanend a,chanend a1,chanend a2,chanend a3,chanend a4) {
}


void doHundred(port p);

int main()
{
    chan c_time, c_clockControl, c_interruptControl, c_rate_report;
     
    chan c_ep0, c_ep0_ctl;   /* TODO we dont need this ctl chan */
    chan c_ep1_out;
    chan c_ep2_out;
    chan c_ep3_out;
    chan c_ep4_out;
    chan c_ep5_out;
    chan c_ep1_in;
    chan c_ep2_in;
    chan c_ep3_in;
    chan c_ep4_in;
    chan c_ep5_in;

    chan c_midi_in;
    chan c_midi_out;
    
    chan c_usbRecordData, c_usbPlayData, c_digitalData;
    chan c_adatPhysical, c_adatRecord;
    chan c_spdifPhysical, c_spdifRecord;
    chan c_adatOut, c_spdifOut, c_i2sToMixer, c_i2sToHW;
    chan clockQueryZero, clockQueryInterrupt;
    par
    {
        /* USB Interface, must be on its own core */
        on stdcore[USB_CORE]: XUD_Manager( c_ep0, 
                                            c_ep1_out, c_ep2_out, c_ep3_out, c_ep4_out, c_ep5_out,  
                                            c_ep1_in, c_ep2_in, c_ep3_in, c_ep4_in, c_ep5_in, 
                                           p_test, p_test2, epTypeTableOut, epTypeTableIn, c_rate_report);  
        on stdcore[USB_CORE]: undangle(c_ep2_out, c_ep3_out, c_ep5_out, c_ep4_in, c_ep0_ctl);

        /* Mixer requires 300 MIPS...*/

        on stdcore[MIXER_CORE]: Endpoint0(c_ep0, c_ep0_ctl, c_clockControl, clockQueryZero);

        on stdcore[MIXER_CORE]: recordEndpoint(c_ep2_in, c_usbRecordData);

        on stdcore[MIXER_CORE]: mixer(c_usbPlayData, c_usbRecordData, c_digitalData,
                                      c_adatOut, c_spdifOut, c_i2sToMixer, c_i2sToHW);



        /* Audio, pars 3 threads (includes S/PDIF) */ 

//        on stdcore[AUD_CORE]: reportErrors(c_rate_report);
        on stdcore[AUD_CORE]: audioPhysical(p_aud_rst, p_aud_sclk, p_aud_scl, p_aud_sda, p_aud_lrclk,
                                            p_master_clock,
                                            p_aud_dac1, p_aud_dac2, p_aud_dac3, p_aud_dac4, 
                                            p_aud_adc1, p_aud_adc2, p_aud_adc3, p_aud_adc4,
                                            p_adatTx, p_spdifTx,
                                            bitClockBlock, masterClockBlock,
                                            c_adatPhysical, c_spdifPhysical, c_i2sToHW, c_i2sToMixer);

        on stdcore[AUD_CORE]: clockRecovery(p_aud_clk,
                                            c_adatRecord, c_spdifRecord, c_digitalData,
                                            c_clockControl, c_interruptControl, clockQueryZero, clockQueryInterrupt);

        on stdcore[AUD_CORE]: adat_receiver(adat_in_clk, p_adatRx, c_adatRecord);        
        on stdcore[AUD_CORE]: spdif_receiver(spdif_in_clk, p_spdifRx, c_spdifRecord);        

        on stdcore[AUD_CORE]: repeater(p_midi_in, c_midi_in, p_midi_out, c_midi_out); // MIDI physical

        on stdcore[REST_CORE]: midi_in(3, 5, c_ep5_in, c_midi_in);
        on stdcore[REST_CORE]: midi_out(4, c_ep4_out, c_midi_out);

        on stdcore[REST_CORE]: playEndpoint(c_ep1_out, c_usbPlayData, c_time, c_rate_report);
        on stdcore[REST_CORE]: HIDEndpoint(c_ep3_in, 3, c_interruptControl, clockQueryInterrupt); 
        on stdcore[REST_CORE]: feedbackEndpoint(1, c_ep1_in, c_time);

        on stdcore[REST_CORE]: adat_transmitter(c_adatOut, c_adatPhysical);        
        on stdcore[REST_CORE]: spdif_transmitter(c_spdifOut, c_spdifPhysical);     


//        on stdcore[AUD_CORE] : doHundred(p4f);
    }

    return 0;
}








