#ifndef _CAN_FUNCTIONS
#define _CAN_FUNCTIONS

#ifdef __XC__

void driveLeds(port led0, port led1, port led2, port ledGreen, port ledRed,
		unsigned int value, unsigned int green);

void canPhyRxTx(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx);
void canPhyRx(chanend rxChan, clock clk, buffered in port:32 canRx, port canTx);

void printPacket(struct CanPacket &p);
void initPacket(struct CanPacket &p);
void randomizePacket(struct CanPacket &p, int bitZero);
void sendPacket(chanend c, struct CanPacket &p);
void receivePacket(chanend c, struct CanPacket &p);

# pragma select handler
void rxReady(buffered in port:32 p, unsigned int &time);

#endif

#endif
