/*
* TO test:
 * Make hashtables large (especially the second one).
 * Make sure all entries that are not used are '0'
 * Changed adat.S adat44100.S so that error does return. // should do anyway!!!
 */



#include <xs1.h>
#include <xclib.h>
#include <print.h>
#ifndef FAKE
#include <platform.h>
#endif

#ifdef FAKE
typedef chanend PTYPE;
{int,int, int} parseAdat44100(chanend adatRx, chanend dataOut);
#else
typedef buffered in port:32 PTYPE;
on stdcore[2]: buffered in port:32 aPort = XS1_PORT_1P;
on stdcore[2]: clock clk = XS1_CLKBLK_2;
on stdcore[2] : out port theLeds                  = XS1_PORT_8D;
{int,int, int, int} parseAdat44100(PTYPE x, chanend dataOut);
#endif

unsigned int idata[] = 
{
  0xC00FFC00,
  0xFFE007FE,
  0x007FE003,
  0xFF003FF0,
  0x03FF0011,
  0xF9E66666,
  0x6007FE03,
  0xC0333333,
  0x000001FF,
  0x801FF801,
  0xFF800FFC,
  0x00FFC00F,
  0xFC007FE0,
  0x07FE607F,
  0xE003FF00,
  0x3FF03CFC,
  0x7FE007FE,
  0x007FE003,
  0xFF003FF0,
  0x03FF001F,
  0xF801FF80,
  0x18666333,
  0x333003FF,
  0x03CFF999,
  0x99800001,
  0xFFC00FFC,
  0x00FFC00F,
  0xFE007FE0,
  0x07FE007F,
  0xF003FF3C,
  0xC00FFC00,
  0x7FE007E6,
  0x07801FFC,
  0x00FFC00F,
  0xFC00FFE0,
  0x07FE007F,
  0xE003FF00,
  0x3FF3C333,
  0x33199801,
  0xFF819FE6,
  0x63333FFF,
  0xFF003FF0,
  0x01FF801F,
  0xF801FF80,
  0x0FFC00FF,
  0xC00FFC00,
  0x60661999,
  0x99800FFC,
  0x3F0FC00F,
  0xFC007FE0,
  0x07FE007F,
  0xE003FF00,
  0x3FF003FF,
  0x001FF9F9,
  0x9E666678,
  0x00FFC3F0,
  0x3333331F,
};

source(chanend x) {
    for(int i = 0; i < sizeof(idata)/sizeof(int); i++) {
//        int br = data[i] | data[i+1] << 4 | data[i+2] << 8 |data[i+3] << 12 |
//            data[i+4] << 16 | data[i+5] << 20 | data[i+6] << 24 |data[i+7] << 28;
        unsigned int br = bitrev(idata[i]);
        outuint(x, br & 0xffff);
        outuint(x, br >> 16);
    }
    outct(x, 1);
}

sink(chanend x, chanend query) {
    int k[100*8];
    int cnt = 0, total = 0;
    unsigned z;
    while(1){
        select {
        case inuint_byref(x, z):
            total++;
            theLeds <: ~(total >> 16);
//            printintln(z);
//                    printhexln(z);
/*
//    x :> z;
*/
            if (0 && (z&0xff000000) == 0xff000000) {
                k[cnt++] = z;
                if (cnt == 1) {
                    for(int i = 0; i < cnt; i++) {
                        printint(k[i] << 8);
                        if (k[i] != 0x80000000) {
                            printstr(" ");
                        } else {
                            printstr("\n");
                        }
                    }
                    cnt = 0;
                }
            } else if (z == 0x80000000) {
                cnt &= ~7;
            }
            break;
        case query :> int _:
            query <: total;
            total = 0;
            break;
        }
    }
}

pAdat(PTYPE p, chanend fromAdat, chanend query) {
    int t = 0, n = 0;
    while(1) {
        int recent, failed, old, oldest, q;
        {failed, recent, old, oldest} = parseAdat44100(p, fromAdat);
        query <: 1;
        query :> q;
        if (q > 5) {
            printint(q);
            printstr(" ");
            t += q;
            n++;
            printint(t/n);
            printstr(" R11: ");
            printhex(oldest);
            printstr(" Oldest: ");
            printhex(old);
            printstr(" Old: ");
            printhex(recent);
            printstr(" Failed: ");
            printhexln(failed);
        }
    }
}


int mymain(void) {
    chan toAdat, fromAdat, query;
    int z[100];
    while(0){
        for(int i=0; i<10000000;i++);
        printstr("Hello\n");
        theLeds <: (z[0] >> 16);
        z[0] = ~z[0];
    }
#ifndef FAKE
    set_clock_div(clk, 1); 
    configure_in_port_no_ready(aPort, clk); 
    start_clock(clk); 
#endif
    par {
#ifdef FAKE
        pAdat(toAdat, fromAdat, query);
        sink(fromAdat, query);
        source(toAdat);
#else
        pAdat(aPort, fromAdat, query);
        sink(fromAdat, query);
#endif
    }
}

main() {
#ifdef FAKE
    mymain();
#else
    par {
        on stdcore[2]: mymain();
    }
#endif
}
