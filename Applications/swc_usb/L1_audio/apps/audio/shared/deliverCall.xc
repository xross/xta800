#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "xud.h"
#include "interface.h"

typedef buffered  in port:32 inPort;
typedef buffered out port:32 outPort;
typedef buffered out port:32 clkPort;
 
// asm:
extern int deliverNew(outPort ADAT_TX, outPort LRCLK, outPort BCLK, outPort SPDIF_TX,
                      chanend c_adat, chanend c_spdif, chanend c_i2s_in, chanend c_i2s_out,
                      outPort DOUT1, outPort DOUT2, outPort DOUT3, outPort DOUT4, 
                      inPort DIN1, inPort DIN2, inPort DIN3, inPort DIN4,
                      int padding);


void deliver(outPort ADAT_TX, outPort LRCLK, outPort BCLK, outPort SPDIF_TX,
            chanend c_adat, chanend c_spdif, chanend c_i2s_in, chanend c_i2s_out,
            outPort DOUT1, outPort DOUT2, outPort DOUT3, outPort DOUT4, 
            inPort DIN1, inPort DIN2, inPort DIN3, inPort DIN4,
            int curSampFreq) {
    int clockPad = 0;

    switch(curSampFreq)
    {
    case 44100:
        clockPad = 12;
        break;
        
    case 48000:
        clockPad = 12;
        break;
        
    case 96000:
        clockPad = 4;
        break;
        
    case 192000: 
        clockPad = 0;
        break;
        
    default:
        printstr("Error: Unsupported sample freq\n");
        break;
    }        

    /* Output sample rate to S/PDIF thread(s) */
    
    clearbuf(BCLK);
    clearbuf(LRCLK);
    BCLK <: 0xffffffff;
    
    clearbuf(DOUT1);
    clearbuf(DOUT2);
    clearbuf(DOUT3);
    clearbuf(DOUT4);
    clearbuf(DIN1);
    clearbuf(DIN2);
    clearbuf(DIN3);
    clearbuf(DIN4);

    deliverNew(ADAT_TX, LRCLK, BCLK, SPDIF_TX, c_adat, c_spdif, c_i2s_in, c_i2s_out,
               DOUT1, DOUT2, DOUT3, DOUT4, DIN1, DIN2, DIN3, DIN4, clockPad);

    while (!testct(c_adat)) {
        inuchar(c_adat);
    }
    chkct(c_adat, 1);
    while (!testct(c_spdif)) {
        inuchar(c_spdif);
    }
    chkct(c_spdif, 1);
    chkct(c_i2s_in, 1);
}
