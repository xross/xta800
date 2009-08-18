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


#define CAN_RX
#define CAN_TX

//#define CAN_DEBUG_TX_DATA

//#define CAN_DEBUG_RX_DATA
//#define CAN_DEBUG_RX_PHY

//#define PRINT_TIMES
#define CHECK_ACK
#define LOOP

//#define CHECK_RX

//#define CAN_DEBUG

#define CANUSB

#define STRESS_LIMIT 1000

#define PROGRESS

#define SYSTEM_CLOCK 100000000

#define QUANTA_SYNC_SEGS 		1
#define QUANTA_PROP_SEGS		8
#define QUANTA_PHASE1_SEGS	8
#define QUANTA_PHASE2_SEGS	8
#define QUANTA_SJW_SEGS			4
#define QUANTA_TOTAL_SEGS		(QUANTA_SYNC_SEGS + QUANTA_PROP_SEGS + QUANTA_PHASE1_SEGS + QUANTA_PHASE2_SEGS) 

#define BAUD_RATE 1000000

#define CLOCK_DIV (SYSTEM_CLOCK/(BAUD_RATE*QUANTA_TOTAL_SEGS*2))

/*
Assuming 25 time quanta then the following baud rates are avaliable.
If the time quanta are changed then you will need to ensure that the CLOCK_DIV
is still a integer

BAUD_RATE	CLOCK_DIV
1000000		2
500000		4
400000		5
250000		8
200000		10
125000		16
100000		20
80000		25
62500		32
50000		40
40000		50
31250		64
25000		80
20000		100
16000		125
15625		128
12500		160
10000		200
8000		250
7813		256
5000		400
*/

#define BIT_TIME (SYSTEM_CLOCK / BAUD_RATE )	//baud rate prescaler

#define QUANTA_SYNC 		(QUANTA_SYNC_SEGS)
#define QUANTA_PROP		(QUANTA_PROP_SEGS)
#define QUANTA_PHASE1		(QUANTA_PHASE1_SEGS)
#define QUANTA_PHASE2		(QUANTA_PHASE2_SEGS)
#define QUANTA_SJW		(QUANTA_SJW_SEGS)
#define QUANTA_TOTAL		(QUANTA_TOTAL_SEGS) 

#define SAMPLE_TIME (BIT_TIME*(QUANTA_SYNC + QUANTA_PROP + QUANTA_PHASE1)/QUANTA_TOTAL)


struct canPacket {

	unsigned SOF;
	unsigned ID;
	unsigned SRR;
	unsigned IDE;
	unsigned EID;
	unsigned RTR;
	unsigned RB1;
	unsigned RB0;
	unsigned DLC;
	unsigned DATA[8];
	unsigned CRC;
	unsigned CRCdel;
	unsigned ACKdel;
	unsigned _EOF;
};


//commands exchanged between the Data Link Layer and the Physical Layer:
#define SUCCESS 	(0x1)
#define FAIL 		(0x2)

#define SEND_ACK 	(0x4)
#define END_PACKET  	(0x8)
#define RECEIVE_ACK 	(0x10)

#define CRC_ERROR	(0x20)
#define FORM_ERROR	(0x40)
#define BIT_ERROR	(0x80)
#define ACK_ERROR	(0x100)
#define STUFF_ERROR	(0x200)

#define UNKNOWN_ERROR 	(0x400)

#include "CanConfig.h"
#include "CanFunctions.h"

#define STUFFING_PERIOD 		5
#define EOF_PERIOD 			7
#define INTERFRAME_PERIOD		3
#define SUSPEND_PERIOD			8
#define BUS_IDLE_PERIOD     		27 //(STUFFING_PERIOD - 1 + 3 /*CRC del, ACK, ACK del*/ + EOF_PERIOD + INTERFRAME_PERIOD + SUSPEND_PERIOD) /*25 in total, the CAN2USB is leaving 27 in total*/

//todo:
//	receiver errors to handle:
//		glitches
//		stuffing errors
//		crc errors
//	do the timing properly according to specs
//	support various bitrates	(only 500K and 100K are supported for now)
//	bus arbitration
//  
//	move destuffing to the data link layer?


#endif
