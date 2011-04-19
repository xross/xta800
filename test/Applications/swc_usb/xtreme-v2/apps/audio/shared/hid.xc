
#include "xud.h"
#include <xs1.h>
#include <print.h>


void interruptReport(chanend c_ep, unsigned epNum, int i) 
{  
    char hidReport[8];
    hidReport[0] = 0;    // Class-specific, caused by interface
    hidReport[1] = 1;    // attribute: CUR
    hidReport[2] = 0;    // CN/ MCN
    hidReport[3] = 0;    // CS
    hidReport[4] = 0;    // interface
    hidReport[5] = i;    // id of entity causing interrupt (clock selector)
    XUD_SetBuffer_(c_ep, epNum, hidReport, 6);
}


/* TODO This is no longer an interupt endpoint but a status ep */
int HIDEndpoint(chanend c_ep, unsigned epNum, chanend interruptControl, chanend queryClockValidity) 
{  
    int b = 0;
    unsigned tmp;

    outct(interruptControl, 1);
    
    while(1) 
    {
        unsigned char x;

        select
        {
            case inuint_byref(c_ep, tmp):
                chkct(c_ep, XS1_CT_END);
                outct(c_ep, XUD_CT_ACK);
                outct(c_ep, XS1_CT_END);
                break;
            
            case inuchar_byref(interruptControl, x):
                chkct(interruptControl, 1);
                outct(interruptControl, 1);
                b = ~(~b & ~x);
        
                if (b & 2) {interruptReport(c_ep, epNum, 1); b &= ~2;}
                if (b & 4) {interruptReport(c_ep, epNum, 2); b &= ~4;}
                if (b & 32) {interruptReport(c_ep, epNum, 5); b &= ~32;}
                if (b & 8) {interruptReport(c_ep, epNum, 3); b &= ~8;}
            break;
        }
    }

    /* Don't hit */
    return 0;
}


