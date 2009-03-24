
#include <xs1.h>
#include <print.h>



void XUD_LLD_IoLoop(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, chanend c_out, chanend c_in, chanend c_con_io) ;

unsigned threadActive = 0xe;

void XUD_LLD_Io(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, chanend c_out, chanend c_in, chanend c_con_io) 
{

  

  // Call io assembly main loop
  XUD_LLD_IoLoop(rxd_port, rxa_port,txd_port, rxe_port,  flag0_port, c_out,  c_in, c_con_io) ;


  // Done when exiting loop...
  //outct(c_out, XS1_CT_END);
  //outct(c_in, XS1_CT_END);

  //printstr("out of io loop");
}

