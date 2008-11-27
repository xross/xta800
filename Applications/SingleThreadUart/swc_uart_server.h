 /****************************************************************
 *
 * swc_uart_server.h
 *
 * The single thread uart software component
 * This is the header for the code that is run on the single thread server
 *
 */

#include "sw_comps_common.h"

void swc_uart_server( out port txdPort,    // 1 bit output port
                      out port rtsPort,    // 1 bit output port
                      in  port rxdCtsPort, // 4 bit input port rxd = bit 0, cts = bit 1 
                      chanend  uartChan );


