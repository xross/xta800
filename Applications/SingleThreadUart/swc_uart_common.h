 /****************************************************************
 *
 * swc_uart_common.h
 *
 * The single thread uart software component
 * This header contains common definitions
 *
 */

#define BAUD_RATE  38400  // 38400 19200 9600 4800 2400 300 bits per second

// Channel Communications Protocol
// Request codes
#define REQUEST_TX_BLOCK 0x1
#define REQUEST_RX_ALL   0x2
#define REQUEST_CONFIG   0x3
#define REQUEST_STATUS   0x4

// Config codes
#define CONFIG_ENABLE_RTS_CTS  0x1
#define CONFIG_DISABLE_RTS_CTS 0x2
#define CONFIG_FINISH          0x3

// Error codes
#define RX_ERROR        0x80
#define NO_ERROR        0x00
#define UNDERFLOW_ERROR 0x01
#define FRAMING_ERROR   0x02
#define TIMING_ERROR    0x03               // Bit Rate is too high

// Uart signal levels
#define START_BIT 0
#define STOP_BIT  1

#define FIFO_DEPTH_BITS    4
#define FIFO_DEPTH        (1<< FIFO_DEPTH_BITS)
#define FIFO_DEPTH_MASK  (FIFO_DEPTH -1) // avoids the need for divide operations
#define FIFO_NEGATE_RTS_SPACE  2         // Negate rts (rtr) when there's this
                                         // number of spaces in the FIFO

// x8 sample counter uses this value when waiting for a stop bit
#define X8_WAITING_FOR_STOP 199 

// Number of consecutive missed client services that constitute an error
#define MISSED_CLIENT_THRESHOLD 10
