#include "CanIncludes.h"
#include "CanFunctions.h"

#if 0
#include "CanCFunctions.h"
#endif

on stdcore[0] : clock               canClk_0   = XS1_CLKBLK_1;
on stdcore[0] : buffered in port:32 canRx_0    = XS1_PORT_1A;
on stdcore[0] :             port    canTx_0    = XS1_PORT_1B;
on stdcore[0] :             port    led0_0     = XS1_PORT_4A;
on stdcore[0] :             port    led1_0     = XS1_PORT_4B;
on stdcore[0] :             port    led2_0     = XS1_PORT_4C;
on stdcore[0] :             port    ledRed_0   = XS1_PORT_1F;
on stdcore[0] :             port    ledGreen_0 = XS1_PORT_1E;

on stdcore[1] : clock               canClk_1   = XS1_CLKBLK_1;
on stdcore[1] : buffered in port:32 canRx_1    = XS1_PORT_1A;
on stdcore[1] :             port    canTx_1    = XS1_PORT_1B;
on stdcore[1] :             port    led0_1     = XS1_PORT_4A;
on stdcore[1] :             port    led1_1     = XS1_PORT_4B;
on stdcore[1] :             port    led2_1     = XS1_PORT_4C;
on stdcore[1] :             port    ledRed_1   = XS1_PORT_1F;
on stdcore[1] :             port    ledGreen_1 = XS1_PORT_1E;

/*
	CanNetworkTx is the transmitter side of network layer
	All it is doing for now is sending packets to the Data Link Layer
	and also printing them on the consol (over RS232) for now.
*/
void canNetworkTx(chanend txChan, port ledGreen, port ledRed, int bitZero) {
	struct CanPacket p;
	timer t;
	unsigned time;
	int green = 0;
	int packetCount = 0;

	while (1) {
		randomizePacket(p, bitZero);

		if (green) {
			ledRed :> void;
			ledGreen <: 1;
		} else {
			ledGreen :> void;
			ledRed <: 1;
		}
		green = ~green;

#ifdef SIM_TESTING
		outuint(txChan, packetCount);
		sendPacket(txChan, p);
#else
		for (int i = 0; i < 10; i++) {
			t when timerafter(time + 10000000) :> time;
		}
#endif
		packetCount++;
	}
}

/*
 * CanNetworkRx is receiver side of the network layer
 * All it is doing for now is recieving packets from the Data Link Layer
 * and printing them on the consol (over RS232) for now.
 */
void canNetworkRx(chanend rxChan, port led0, port led1, port led2) {
	unsigned count = 1;
	int direction = 0;
	timer t;
	unsigned int time;

	t :> time;
	driveLeds(led0, led1, led2, count);

	while(1) {
		unsigned int value = 0;

		select {
		case inuint_byref(rxChan, value):
			direction = ~direction;
			break;

		case t when timerafter(time + 1500000) :> time:
			driveLeds(led0, led1, led2, count);
			break;
		}

		if (value) {
			// Error detected - stop
			driveLeds(led0, led1, led2, (1 << value) - 1);
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


#ifdef SIM_TESTING
int main() {
	chan rxChan_0, txChan_0;
	chan rxChan_1, txChan_1;

	par {
		on stdcore[0]: canPhyRxTx(rxChan_0, txChan_0, canClk_0, canRx_0, canTx_0);
		on stdcore[0]: canNetworkRx(rxChan_0, led0_0, led1_0, led2_0);
//		on stdcore[0]: canNetworkTx(txChan_0, ledGreen_0, ledRed_0, 0);

/*		on stdcore[0]: canPhyRx(rxChan_0, canClk_0, canRx_0, canTx_0);
		on stdcore[0]: canNetworkRx(rxChan_0, led0_0, led1_0, led2_0);*/

		on stdcore[1]: canPhyRxTx(rxChan_1, txChan_1, canClk_1, canRx_1, canTx_1);
		on stdcore[1]: canNetworkRx(rxChan_1, led0_1, led1_1, led2_1);
		on stdcore[1]: canNetworkTx(txChan_1, ledGreen_1, ledRed_1, 1);
	}
	return 0;
}
#else

int main() {
	chan rxChan, txChan;

	par {
		on stdcore[0]: canPhy(rxChan, txChan, canClk_0, canRx_0, canTx_0);
		on stdcore[0]: canNetworkRx(rxChan, led0_0, led1_0, led2_0);
		on stdcore[0]: canNetworkTx(txChan, ledGreen_0, ledRed_0);
	}
	return 0;
}

#endif

#if 0
int main() {
	chan rxChan_0, txChan_0;
	chan rxChan_1, txChan_1;

	par {
		on stdcore[0]: {
			struct CanPacket rxPackets[2];
			struct CanPacket txPackets[2];

			canPhy_c(rxChan_0, txChan_0, canClk_0, canRx_0, canTx_0, rxPackets, txPackets);
			canNetworkRx_c(rxChan_0, led0_0, led1_0, led2_0, rxPackets);
			canNetworkTx_c(txChan_0, ledGreen_0, ledRed_0, 0, txPackets);
		}

		on stdcore[1]: {
			struct CanPacket rxPackets[2];
			struct CanPacket txPackets[2];

			canPhy_c(rxChan_1, txChan_1, canClk_1, canRx_1, canTx_1, rxPackets, txPackets);
			canNetworkRx_c(rxChan_1, led0_1, led1_1, led2_1, rxPackets);
			canNetworkTx_c(txChan_1, ledGreen_1, ledRed_1, 1, txPackets);
		}
	}
	return 0;
}
#endif
