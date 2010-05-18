#ifndef _CAN_C_FUNCTIONS
#define _CAN_C_FUNCTIONS

void initAlignTable();

#if 0
#ifdef __XC__

void canNetworkTx_c(chanend txChan, port ledGreen, port ledRed, int bitZero, struct CanPacket txPackets[]);
void canNetworkRx_c(chanend rxChan, port led0, port led1, port led2, struct CanPacket rxPackets[]);
void canPhy_c(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx,
		struct CanPacket rxPackets[], struct CanPacket txPackets[]);

#else
void canNetworkTx_c(unsigned int txChan, unsigned int ledGreen, unsigned int ledRed, int bitZero,
		struct CanPacket txPackets[]);

void canNetworkRx_c(unsigned int rxChan, unsigned int led0, unsigned int led1, unsigned int led2,
		struct CanPacket rxPackets[]);

void canPhy_c(unsigned int rxChan, unsigned int txChan, unsigned int clk, unsigned int canRx, unsigned int canTx,
		struct CanPacket rxPackets[], struct CanPacket txPackets[]);
#endif
#endif

#endif
