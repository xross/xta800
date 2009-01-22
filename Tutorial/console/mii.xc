/*************************************************************************
 *
 * Ethernet Physical Layer Implementation
 * IEEE 802.3 Medium Independent Interface
 *
 *   File        : mii.xc (reduced)
 *
 *************************************************************************/
/*
 * Copyright (c) 2009 XMOS Ltd.
 *
 * Copyright Notice
 *
 *************************************************************************/

#include <xs1.h>
#include <syscall.h>
#include <xclib.h>

#define PORT_MII_RXD     XS1_PORT_4A
#define PORT_MII_RXDV    XS1_PORT_1I

buffered in port:32 p_mii_rxd = PORT_MII_RXD;
in port p_mii_rxdv = PORT_MII_RXDV;

void consumer(chanend);
void mii_rx_pins(chanend);

int mii_counter_rxgood = 0;
int mii_counter_rxeroverflow = 0;
int mii_counter_rxercrc = 0;
int mii_counter_rxerlen = 0;
int mii_counter_rxeralgn = 0;

enum mii_ctl {
  FRAME_DATA = 0,
  END_FRAME = 1,
} ;

int main()
{
    chan channel;
    par {
        consumer(channel);
        mii_rx_pins(channel);
    } 
}

void consumer(chanend input)
{
  int done = 0;
  while(!done) {
    int x;
    input:> x;

    if (x) {
      done = 1;
    }
  }
}


#pragma unsafe arrays
void mii_rx_pins(chanend c_mii_rx)
{
    const register unsigned poly = 0xEDB88320;
    timer tmr;

    p_mii_rxdv when pinseq(0) :> int lo;
    do
    {
        int eop = 0;
        int length = 0;
        register unsigned crc = 0x9226F562;
        unsigned time;
        unsigned word;
        
        #pragma xta label "packet_start"
        p_mii_rxdv when pinseq(1) :> int hi;
        p_mii_rxd when pinseq(0xD) :> int sof;

        tmr :> time;

        p_mii_rxd :> word;
        outuint(c_mii_rx, FRAME_DATA);
        outuint(c_mii_rx, word);
        length+=4;
        crc32(crc, word, poly);
        p_mii_rxd :> word;
        outuint(c_mii_rx, FRAME_DATA);
        outuint(c_mii_rx, word);
        length+=4;
        crc32(crc, word, poly);

        do
        {
            select
            {
                #pragma xta label "receive_loop"
                case p_mii_rxd :> word:
                {
                    outuint(c_mii_rx, FRAME_DATA);
                    outuint(c_mii_rx, word);
                    length+=4;
                    crc32(crc, word, poly);
                    break;
                }
                #pragma xta label "eop_detected"
                case p_mii_rxdv when pinseq(0) :> int lo:
                {
                    unsigned tail;
                    int taillen;
                    int endbytes;

                    eop = 1;
                    taillen = endin(p_mii_rxd);
                    p_mii_rxd :> tail;

                    tail = tail >> (32 - taillen);
                    endbytes =  (taillen >> 3);

                    outuint(c_mii_rx, 0);
                    outuint(c_mii_rx, tail);

                    outuint(c_mii_rx, FRAME_DATA);
                    outuint(c_mii_rx, time);

                    if (endbytes == 0) {
                        length += -4 + 0;
                    } else if (endbytes==1) {
                        length += -4 + 1;
                        tail = crc8shr(crc, tail, poly);
                    } else if (endbytes == 2) {
                        length += -4 + 2;
                        tail = crc8shr(crc, tail, poly);
                        tail = crc8shr(crc, tail, poly);
                    } else if (endbytes == 3) {
                        length += -4 + 3;
                        tail = crc8shr(crc, tail, poly);
                        tail = crc8shr(crc, tail, poly);
                        tail = crc8shr(crc, tail, poly);
                    } else {
                        #pragma xta label "error_overflow"
                        mii_counter_rxeroverflow++;
                        clearbuf(p_mii_rxd);
                    }

                    outuint(c_mii_rx, END_FRAME);
                    outuint(c_mii_rx, length);

                    if (~crc) {
                        #pragma xta label "error_crc"
                        mii_counter_rxercrc++;
                        clearbuf(p_mii_rxd);
                    } else if (length < 64) {
                        #pragma xta label "error_len"
                        mii_counter_rxerlen++;
                        clearbuf(p_mii_rxd);
                    } else if (taillen & 7) { 
                        #pragma xta label "error_nibble"
                        mii_counter_rxeralgn++;
                        clearbuf(p_mii_rxd);
                    } else {
                        mii_counter_rxgood++;
                    }

                    break;
                }
            }
        } while (!eop);
    } while (1);

}

