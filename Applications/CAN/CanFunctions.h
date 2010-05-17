#ifndef _CAN_FUNCTIONS
#define _CAN_FUNCTIONS

#ifdef __XC__

void driveLeds(port led0, port led1, port led2, unsigned int value);

void canNetworkTx(chanend txChan, port ledGreen, port ledRed, int bitZero);
void canNetworkRx(chanend rxChan, port led0, port led1, port led2);
void canPhyRxTx(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx);
void canPhyRx(chanend rxChan, clock clk, buffered in port:32 canRx, port canTx);

void printPacket(struct CanPacket &p);
void initPacket(struct CanPacket &p);
void randomizePacket(struct CanPacket &p, int bitZero);
void sendPacket(chanend c, struct CanPacket &p);
void receivePacket(chanend c, struct CanPacket &p);

#endif

#if 0

void canNetworkTx(unsigned int txChan, unsigned int ledGreen, unsigned int ledRed,
		int bitZero, struct CanPacket txPackets[]);
void canNetworkRx(unsigned int rxChan, unsigned int led0, unsigned int led1, unsigned int led2,
		struct CanPacket rxPackets[]);
void canPhy(unsigned int rxChan, unsigned int txChan, unsigned int clk, unsigned int canRx, unsigned int canTx,
		struct CanPacket rxPackets[], struct CanPacket txPackets[]);

#pragma unsafe arrays
inline transaction sendStruct(chanend c, struct CanPacket &s) {
   for (int i = 0; i < sizeof(s) / 4; i++) {
     c <: (s,unsigned int[])[i];
   }
}

#pragma unsafe arrays
inline transaction receiveStruct(chanend c, struct CanPacket &s) {
   for (int i = 0; i < sizeof(s) / 4; i++) {
   c :> (s,unsigned int[])[i];
   }
}

#endif


#endif
