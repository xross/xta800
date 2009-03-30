#ifndef _uart_receive_h_
#define _uart_receive_h_

/* This function receives a number of bytes using the Uart protocol 
 * 
 * txr - the port to receive on
 * bytes - the array of bytes to put the received bytes in
 * numBytes - the number of bytes to receive
 * 
 */
void uart_receive(in port txr, char bytes[], int numBytes);

#endif
