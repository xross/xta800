#include <xs1.h>
#include <platform.h>

///////////////////////////////////////////////////////////////////////////////
//
// G4 SPDIF techonology demo
// Copyright (C) 2009 XMOS Ltd
//
// stream.xc
// Stream generation
//

#include <xs1.h>

static int inline parity32(unsigned x)
{
  crc32(x, 0, 1);
  return (x & 1)^1;
}

void spdif_stream(chanend c_tx0, chanend c_tx)
{
    unsigned sample, control, preamble, parity, word;
#ifdef FASTMODE
    set_thread_fast_mode_on();
#endif
    outuint(c_tx, inuint(c_tx0));
    while (1) {
        int controlLeft  = 0x1000204;
        int controlRight = 0x2000204;
        int newblock = 2;
        for (int i = 0 ; i<192; i++) {
            if (testct(c_tx0)) {              // new frequency
                outct(c_tx, 1);               // tell physical thread to change frequency
                inct(c_tx0);
                outuint(c_tx, inuint(c_tx0)); // pass on new frequency
            }
            sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
            chkct(c_tx0, 1);
            control = (controlLeft & 1)<<30;
            preamble = newblock ;
            parity = parity32(sample | control) << 31;
            word = preamble | sample | control | parity | (1<<28);
            newblock = 0;
            controlLeft >>=1;

            outuint(c_tx, word);
            
            if (testct(c_tx0)) {
                outct(c_tx, 1);
                inct(c_tx0);
                outuint(c_tx, inuint(c_tx0));
                continue;
            }
            sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
            chkct(c_tx0, 1);
            control = (controlRight & 1)<<30;
            preamble = (1);
            parity = parity32(sample | control) << 31;
            word = preamble | sample | control | parity | (1<<28);

            outuint(c_tx, word);

            controlRight >>=1;
        }
    }
}

extern void spdif_tx(out buffered port:32 p, chanend c_tx);

void spdif(out buffered port:32 p, chanend internal) {
//    spdif_tx(p, internal); 
}

extern int parseSpDif(in buffered port:4 i, chanend output, int unused, clock clk);
extern void parseAdat48000(in buffered port:32 adatRx, chanend dataOut);
extern void parseAdat44100(in buffered port:32 adatRx, chanend dataOut);

//on stdcore[2] : out port p_leds                  = XS1_PORT_8D;

void adat_receiver(clock clk, in buffered port:32 p, chanend adatReceivedData) {
    set_clock_div(clk, 1); 
    configure_in_port_no_ready(p, clk); 
    start_clock(clk); 
    while (1) {                                   // Try decoders in sequence. One will succeed.
        parseAdat44100(p, adatReceivedData);
        parseAdat48000(p, adatReceivedData);
    }
}

void spdif_receiver(clock clk, in buffered port:4 p, chanend spDifReceivedData) {
    set_clock_div(clk, 2); 
    configure_in_port_no_ready(p, clk); 
    start_clock(clk); 
    parseSpDif(p, spDifReceivedData, 0x12345, clk);
}

void doHundred(port p) {
    timer t;
    unsigned i;
t :> i;
    while(1) {
        t when timerafter(i+50000) :> i;
        p <: 0;
        t when timerafter(i+50000) :> i;
        p <: -1;
    }
}
