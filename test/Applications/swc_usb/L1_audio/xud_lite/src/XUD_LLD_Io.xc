
#include <xs1.h>
#include <print.h>

#include "xud.h"

/* Location to store stack pointer (required for interupt handler) */
unsigned SavedSp;
unsigned suspendCount = 0;

int XUD_LLD_IoLoop(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
    in port, out port, int x,
    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], XUD_chan epChans[], int  epCount) ;










int XUD_LLD_Io(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
    int x, int y, int z,
    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], XUD_chan epChans[], int epCount, in port reg_read_port, out port reg_write_port) 
{

    /* Call main IO assembly loop */
    return  XUD_LLD_IoLoop(rxd_port, rxa_port,txd_port, rxe_port,  flag0_port, reg_read_port, reg_write_port,0, epTypeTableOut, epTypeTableIn, epChans, epCount) ;

}

