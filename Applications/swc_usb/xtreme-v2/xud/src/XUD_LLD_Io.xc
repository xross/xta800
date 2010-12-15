
#include <xs1.h>
#include <print.h>

#include "xud.h"

/* Location to store stack pointer (required for interupt handler) */
unsigned SavedSp;

void XUD_LLD_IoLoop(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
    chanend c_out, chanend c_in, chanend c_con_io, chanend c_ctl_buf,
    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[]) ;

void XUD_LLD_Io(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
    chanend c_out, chanend c_in, chanend c_con_io, chanend c_ctl_buf, 
    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[]) 
{

    /* Call main IO assembly loop */
    XUD_LLD_IoLoop(rxd_port, rxa_port,txd_port, rxe_port,  flag0_port, c_out,  c_in, c_con_io, c_ctl_buf, epTypeTableOut, epTypeTableIn) ;

}

