#ifndef _CAN_INCLUDES
#define _CAN_INCLUDES

#include <xs1.h>
#include <stdio.h>
#include <print.h>
#include <float.h>
#include <xclib.h>


#ifdef __XC__
# include <platform.h>
#endif

#define STRESS_LIMIT 1000

#define SYSTEM_CLOCK 100000000

#define QUANTA_SYNC_SEGS      1
#define QUANTA_PROP_SEGS      8
#define QUANTA_PHASE1_SEGS    8
#define QUANTA_PHASE2_SEGS    8
#define QUANTA_SJW_SEGS       4
#define QUANTA_TOTAL_SEGS    (QUANTA_SYNC_SEGS + QUANTA_PROP_SEGS + QUANTA_PHASE1_SEGS + QUANTA_PHASE2_SEGS) 

#define BAUD_RATE 1000000

#define CLOCK_DIV (SYSTEM_CLOCK/(BAUD_RATE * QUANTA_TOTAL_SEGS * 2))

/*
 * Assuming 25 time quanta then the following baud rates are avaliable.
 * If the time quanta are changed then you will need to ensure that the
 * CLOCK_DIV is still a integer
 *
 * BAUD_RATE  CLOCK_DIV
 * 1000000      2
 *  500000      4
 *  400000      5
 *  250000      8
 *  200000     10
 *  125000     16
 *  100000     20
 *  80000      25
 *  62500      32
 *  50000      40
 *  40000      50
 *  31250      64
 *  25000      80
 *  20000     100
 *  16000     125
 *  15625     128
 *  12500     160
 *  10000     200
 *  8000      250
 *  7813      256
 *  5000      400
 */

#define BIT_TIME (SYSTEM_CLOCK / BAUD_RATE )  //baud rate prescaler

#define QUANTA_SYNC     (QUANTA_SYNC_SEGS)
#define QUANTA_PROP    (QUANTA_PROP_SEGS)
#define QUANTA_PHASE1    (QUANTA_PHASE1_SEGS)
#define QUANTA_PHASE2    (QUANTA_PHASE2_SEGS)
#define QUANTA_SJW    (QUANTA_SJW_SEGS)
#define QUANTA_TOTAL    (QUANTA_TOTAL_SEGS) 

#define SAMPLE_TIME (BIT_TIME*(QUANTA_SYNC + QUANTA_PROP + QUANTA_PHASE1)/QUANTA_TOTAL)


typedef enum {
	STATE_SOF          =  0,
	STATE_ID           =  1,
	STATE_SRR          =  2,
	STATE_IEB          =  3,
	STATE_EID          =  4,
	STATE_RTR          =  5,
	STATE_RB1          =  6,
	STATE_RB0          =  7,
	STATE_DLC          =  8,
	STATE_DATA_BIT7    =  9,
	STATE_DATA_BIT6    = 10,
	STATE_DATA_BIT5    = 11,
	STATE_DATA_BIT4    = 12,
	STATE_DATA_BIT3    = 13,
	STATE_DATA_BIT2    = 14,
	STATE_DATA_BIT1    = 15,
	STATE_DATA_BIT0    = 16,
	STATE_CRC          = 17,
	STATE_CRC_DEL      = 18,
	STATE_ACK          = 19,
	STATE_ACK_DEL      = 20,
	STATE_EOF          = 21,
	STATE_INTERMISSION = 22,
	STATE_OVERLOAD     = 23,
	STATE_OVERLOAD_DEL = 24,
	STATE_BUS_IDLE     = 25,

	STATE_DATA

} STATE;

typedef enum {
	ERROR_NONE         = 0,
	ERROR_BIT_ERROR    = 1,
	ERROR_STUFF_ERROR  = 2,
	ERROR_FORM_ERROR   = 3,
	ERROR_CRC_ERROR    = 4,
} ERROR;

struct CanPacket {
	unsigned DATA[8]; // First in struct so that worst-case path is quicker

	unsigned SOF;
	unsigned ID;
	unsigned SRR;
	unsigned IEB;
	unsigned EID;
	unsigned RTR;
	unsigned RB1;
	unsigned RB0;
	unsigned DLC;
	unsigned CRC;
	unsigned CRC_DEL;
	unsigned ACK_DEL;
	unsigned _EOF; /* Uses _ because EOF is reserved */
};

struct CanPhyState {
	STATE        state;
	ERROR        error;
	unsigned int time;
	unsigned int packetCrc;

	int          sendStuffBit;
	unsigned int bitStuffingActive;

	unsigned int lastBit;

	int          txActive;
	int          txComplete;
};

#include "CanFunctions.h"

//todo:
//  receiver errors to handle:
//    glitches
//  do the timing properly according to specs
//  support various bitrates  (only 500K and 100K are supported for now)
//  bus arbitration
//  


#endif
