#ifndef _CAN_FUNCTIONS
#define _CAN_FUNCTIONS

#ifdef __XC__

void driveLeds(unsigned int value);

void canNetworkTx(chanend txChan);
void canNetworkRx(chanend rxChan);
void printPacket(struct CanPacket &p);
void initPacket(struct CanPacket &p);

void canPhy(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx);

#endif

#endif
