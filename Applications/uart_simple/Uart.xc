#include <xs1_reva.h>
#include <print.h>
#include "UartTransmit.h"
#include "UartReceive.h"

out port txd = XS1_PORT_1A;
in  port txr = XS1_PORT_1B;

int main(void)
{
  char transmit[] = { 11 , 242, 57 };
  char receive[] = { 0, 0, 0 };

  /* Initialize the transmitter */
  uart_transmit_init(txd);
  
  /* Run the transmitter and receiver in parallel */
  par {
    uart_transmit(txd, transmit, 3);   
    uart_receive(txr, receive, 3);
  }

  /* Print out the received values */
  for (int i=0;i<3;i++) {
    printintln(receive[i]);
  }

  return 0;
}
