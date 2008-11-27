 /****************************************************************
 *
 * swc_uart_client.h
 *
 * The single thread uart software component
 * This is the client code header *
 */

#include "sw_comps_common.h"

XMOS_RTN_t uart_Clnt_txd_block(chanend server, uchar data[], uint &numBytes);
XMOS_RTN_t uart_Clnt_rxd_block(chanend server, uchar data[], uint &numBytes);
XMOS_RTN_t uart_Clnt_rts_cts_enable(chanend server) ;
XMOS_RTN_t uart_Clnt_rts_cts_disable(chanend server) ;
XMOS_RTN_t uart_Clnt_finish(chanend server) ;


