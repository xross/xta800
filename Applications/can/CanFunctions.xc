#include "CanIncludes.h"
#include "CanFunctions.h"
#include <print.h>
#include <stdlib.h>

void driveLeds(port led0, port led1, port led2, unsigned int value) {
	led0 <: value;
	led1 <: (value >> 4);
	led2 <: (value >> 8);
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

void randomizePacket(struct CanPacket &p, int bitZero) {
	// Fields which are fixed unless injecting errors
	p.SOF = 0;
	p.RB0 = 0;
	p.CRC_DEL = 1;
	p.ACK_DEL = 1;
	p._EOF = 0x7F;

	p.ID  = rand() & 0x7ff;
	if (rand() & 0x1) {
		// Create extended packet
		p.SRR = 0;
		p.IEB = 1;
		p.EID = rand() & 0x3ffff;
		p.RTR = 0;
		p.RB1 = 0;

		p.EID = (p.EID & ~1) | bitZero;
	} else {
		// Create normal packet
		p.SRR = 0;
		p.IEB = 0;
		p.EID = 0;
		p.RTR = 0;
		p.RB1 = 0;

		p.ID = (p.ID & ~1) | bitZero;
	}

	p.DLC = rand() % 9;
	for (int i = 0; i < p.DLC; i++) {
		p.DATA[i] = rand() & 0xff;
	}

	// CRC is calculated by transmitter
	p.CRC = 0;
}

#pragma unsafe arrays
void sendPacket(chanend c, struct CanPacket &p) {
	outuint(c, p.SOF);
	outuint(c, p.ID);
	outuint(c, p.SRR);
	outuint(c, p.IEB);
	outuint(c, p.EID);
	outuint(c, p.RTR);
	outuint(c, p.RB1);
	outuint(c, p.RB0);
	outuint(c, p.DLC);
	for (int i = 0; i < p.DLC; i++) {
		#pragma xta label "sendLoop"
		outuint(c, p.DATA[i]);
	}
	outuint(c, p.CRC);
	outuint(c, p.CRC_DEL);
	outuint(c, p.ACK_DEL);
	outuint(c, p._EOF);
}

#pragma unsafe arrays
void receivePacket(chanend c, struct CanPacket &p) {
	p.SOF = inuint(c);
	p.ID  = inuint(c);
	p.SRR = inuint(c);
	p.IEB = inuint(c);
	p.EID = inuint(c);
	p.RTR = inuint(c);
	p.RB1 = inuint(c);
	p.RB0 = inuint(c);
	p.DLC = inuint(c);
	for (int i = 0; i < p.DLC; i++) {
		#pragma xta label "receiveLoop"
		p.DATA[i] = inuint(c);
	}
	p.CRC = inuint(c);
	p.CRC_DEL = inuint(c);
	p.ACK_DEL = inuint(c);
	p._EOF = inuint(c);
}
