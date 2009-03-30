#ifndef _uart_transmit_h_
#define _uart_transmit_h_


/* This function initializes the uart transmitted on a port */
void uart_transmit_init(out port txd);

/* This function transmits a number of bytes using the Uart protocol 
 * 
 * txd - the port to transmit on
 * bytes - the array of bytes to transmit
 * numBytes - the number of bytes to transmit 
 * 
 */
void uart_transmit(out port txd, char bytes[], int numBytes);

#endif
