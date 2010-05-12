#include "CanIncludes.h"
#include "CanFunctions.h"
#include <print.h>

on stdcore[0] : port led0 = XS1_PORT_4A;
on stdcore[0] : port led1 = XS1_PORT_4B;
on stdcore[0] : port led2 = XS1_PORT_4C;

on stdcore[0] : port ledRed   = XS1_PORT_1F;
on stdcore[0] : port ledGreen = XS1_PORT_1E;

void driveLeds(unsigned int value) {
	led0 <: value;
	led1 <: (value >> 4);
	led2 <: (value >> 8);
}

/*
	CanNetworkTx is the transmitter side of network layer 
	All it is doing for now is sending packets to the Data Link Layer 
	and also printing them on the consol (over RS232) for now.
*/
void canNetworkTx(chanend txChan) {
	struct CanPacket p;
	timer t;
	unsigned time;
	int green = 0;

	p.SOF = 0;
	p.ID  = 0x200;
	p.RTR = 0;
	p.IEB = 0;
	p.RB0 = 0;
	p.DLC = 4;
	p.DATA[0] = 0xFE;
	p.DATA[1] = 0xE1;
	p.DATA[2] = 0xDE;
	p.DATA[3] = 0xAD;
	p.CRC = 0;
	p.CRC_DEL = 1;
	p.ACK_DEL = 1;
	p._EOF = 0x7F;
	
	while (1) {
		if (green) {
			ledRed :> void;
			ledGreen <: 1;
		} else {
			ledGreen :> void;
			ledRed <: 1;
		}
		green = ~green;

		for (int i = 0; i < 10; i++) {
			t when timerafter(time + 10000000) :> time;
		}

//		txChan <: p;
	}
}

/*
 * CanNetworkRx is receiver side of the network layer
 * All it is doing for now is recieving packets from the Data Link Layer
 * and printing them on the consol (over RS232) for now.
 */
void canNetworkRx(chanend rxChan) {
	unsigned count = 1;
	int direction = 0;
	timer t;
	unsigned int time;

	t :> time;
	driveLeds(count);

	while(1) {
		unsigned int value = 0;

		select {
		case inuint_byref(rxChan, value):
			direction = ~direction;
			break;

		case t when timerafter(time + 1500000) :> time:
			driveLeds(count);
			break;
		}

		if (value) {
			// Error detected - stop
			driveLeds((1 << value) - 1);
			while(1);
		}

		if (direction) {
			count = count << 1;
			if (count > (1 << 12)) {
				count = 1;
			}
		} else {
			count = count >> 1;
			if (count == 0) {
				count = (1 << 12);
			}
		}
	}
}

void printPacket(struct CanPacket &p) {
	printstr("SOF: ");
	printhexln(p.SOF);
	printstr("ID: ");
	printhexln(p.ID);
	if (p.IEB == 1) {
		printstr("SRR: ");
		printhexln(p.SRR);
		printstr("IEB: ");
		printhexln(p.IEB);
		printstr("EID: ");
		printhexln(p.EID);
		printstr("RTR: ");
		printhexln(p.RTR);
		printstr("RB1: ");
		printhexln(p.RB1);
	} else {
		printstr("RTR: ");
		printhexln(p.RTR);
		printstr("IEB: ");
		printhexln(p.IEB);
	}
	printstr("RB0: ");
	printhexln(p.RB0);
	printstr("DLC: ");
	printhexln(p.DLC);

	for (int i = 0; i < 8; i++) {
		printstr("DATA[");
		printint(i);
		printstr("]:");
		printhexln(p.DATA[i]);
	}
	printstr("CRC: ");
	printhexln(p.CRC);
	printstr("CRCdel: ");
	printhexln(p.CRC_DEL);
	printstr("ACKdel: ");
	printhexln(p.ACK_DEL);
	printstr("EOF: ");
	printhexln(p._EOF);
}

void initPacket(struct CanPacket &p) {
	p.SOF = 0xffffffff;
	p.ID  = 0xffffffff;
	p.SRR = 0xffffffff;
	p.IEB = 0xffffffff;
	p.EID = 0xffffffff;
	p.RTR = 0xffffffff;
	p.RB1 = 0xffffffff;
	p.RB0 = 0xffffffff;
	p.DLC = 0xffffffff;
	for (int i = 0; i < 8; i++) {
		p.DATA[i] = 0xffffffff;
	}
	p.CRC = 0xffffffff;
	p.CRC_DEL = 0xffffffff;
	p.ACK_DEL = 0xffffffff;
	p._EOF = 0xffffffff;
}
