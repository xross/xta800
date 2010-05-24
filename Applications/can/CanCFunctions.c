
#include "CanIncludes.h"
#include "CanFunctions.h"

extern int alignTable[];

void initAlignTable() {
	int aligned = QUANTA_TOTAL - QUANTA_SJW;

	for (int zeros = 0; zeros < 33; zeros++) {
		alignTable[zeros] = QUANTA_TOTAL;
	}

	for (int zeros = 1; zeros < 32; zeros++) {
		if (zeros < aligned) {
			// Edge is late, in the propagation delay segment need to extend the bit time
			int phaseError = aligned - zeros;
			if (phaseError <= QUANTA_SJW) {
				// Maximum compensation allowed by spec
				alignTable[zeros] = QUANTA_TOTAL + phaseError;
			}
		} else if (zeros > aligned) {
			// Edge is early, in the propagation delay segment need to reduce the bit time
			int phaseError = zeros - aligned;
			if (phaseError <= QUANTA_SJW) {
				// Maximum compensate allowed by spec
				alignTable[zeros] = QUANTA_TOTAL - phaseError;
			}
		}
	}
}

#if 0
void canNetworkTx_c(unsigned int txChan, unsigned int ledGreen, unsigned int ledRed, int bitZero,
		struct CanPacket txPackets[]) {
	canNetworkTx(txChan, ledGreen, ledRed, bitZero, txPackets);
}

void canNetworkRx_c(unsigned int rxChan, unsigned int led0, unsigned int led1, unsigned int led2,
		struct CanPacket rxPackets[]) {
	canNetworkRx(rxChan, led0, led1, led2, rxPackets);
}

void canPhy_c(unsigned int rxChan, unsigned int txChan, unsigned int clk, unsigned int canRx, unsigned int canTx,
		struct CanPacket rxPackets[], struct CanPacket txPackets[]) {
	canPhy(rxChan, txChan, clk, canRx, canTx, rxPackets, txPackets);
}
#endif
