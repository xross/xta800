#include "CanIncludes.h"
#include <string.h>

/*
 * CanPhy function handles the physical layer signaling
 * It is also handling the destuffing of bits for simplicity
 * as well as signaling acknowledgements on the bus
 */

/*
 * Table for performing synchronization
 */
int alignTable[33];

void initialise(clock clk, buffered in port:32 canRx, port canTx, struct CanPhyState &phyState);
void packetStart(struct CanPhyState &phyState, unsigned int &bitStuffingCount);
void waitForBusIdle(struct CanPhyState &phyState, buffered in port:32 canRx);
inline void canPhyRx(struct CanPhyState &phyState, struct CanPacket &rxPacket,
		chanend rxChan, buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount);
inline void canPhyTx(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount);
void handleError(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx);
int crc15(int nxtBit, unsigned int crc_rg);

#pragma unsafe arrays
void canPhy(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx) {

	struct CanPhyState phyState;
	struct CanPacket rxPacket;
	struct CanPacket txPacket;

	int          counter = 0;
	unsigned int rxBits = 0;
	unsigned int bitStuffingCount;

	initialise(clk, canRx, canTx, phyState);
	printstrln("CanPhy starting");

	// Ensure the state machines don't start mid-packet
	waitForBusIdle(phyState, canRx);

	while(1) {
		#pragma xta label "phyLoop"
		packetStart(phyState, bitStuffingCount);

		// Wait for either TX or RX to start
		select {
			case txChan :> txPacket:
				phyState.txComplete = 0;
				break;

			case canRx when pinseq(0) :> void @ phyState.time:
				phyState.time += QUANTA_TOTAL;
				break;
		}

		phyState.state = STATE_SOF;
		if (!phyState.txComplete) {
			phyState.txActive = 1; // Keep attempting to send till complete
		}

		while (phyState.state != STATE_BUS_IDLE) {
			if (phyState.txActive) {
				canPhyTx(phyState, rxPacket, txPacket, canRx, canTx,
						counter, rxBits, bitStuffingCount);
			} else {
				canPhyRx(phyState, rxPacket, rxChan, canRx, canTx,
						counter, rxBits, bitStuffingCount);
			}

			if (phyState.error) {
				outuint(rxChan, phyState.error);
				handleError(phyState, rxPacket, txPacket, canRx, canTx);
			}
		}
	}
}

#pragma unsafe arrays
inline void canPhyRx(struct CanPhyState &phyState, struct CanPacket &rxPacket,
		chanend rxChan, buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount) {

	unsigned int bit;
	unsigned int zeros;
	unsigned int rxWord;

	#pragma xta endpoint "startRx"
	canRx @ phyState.time :> rxWord;
	bit = (rxWord << QUANTA_PHASE2) >> 31;
	zeros = clz(rxWord << QUANTA_SJW);

	// Manage bit stuffing and error detection
	bitStuffingCount += 1;
	if (phyState.bitStuffingActive && bitStuffingCount == 5) {
		if (bit == phyState.lastBit) {
			phyState.error = ERROR_STUFF_ERROR;
		} else {
			// Ignore stuffing bit
		}

	} else {
		switch (phyState.state) {
		case STATE_SOF:
			rxPacket.SOF = bit;
			counter = 11;
			rxBits = 0;
			phyState.state = STATE_ID;

			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_ID:
			counter--;
			rxBits |= bit << counter;
			if (counter == 0) {
				rxPacket.ID = rxBits;
				phyState.state = STATE_SRR;
			}
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_SRR:
			rxPacket.SRR = bit;
			phyState.state = STATE_IEB;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_IEB:
			rxPacket.IEB = bit;

			if (rxPacket.IEB == 1) {
				counter = 18;
				rxBits = 0;
				phyState.state = STATE_EID;
			} else {
				phyState.state = STATE_RB0;
				rxPacket.RTR = rxPacket.SRR;
				rxPacket.RB1 = rxPacket.IEB;
			}
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_EID:
			counter--;
			rxBits |= bit << counter;
			if (counter == 0) {
				rxPacket.EID = rxBits;
				phyState.state = STATE_RTR;
			}
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_RTR:
			rxPacket.RTR = bit;
			phyState.state = STATE_RB1;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_RB1:
			rxPacket.RB1 = bit;
			phyState.state = STATE_RB0;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_RB0:
			rxPacket.RB0 = bit;
			counter = 4;
			rxBits = 0;
			phyState.state = STATE_DLC;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_DLC:
			counter--;
			rxBits |= bit << counter;
			if (counter == 0) {
				rxPacket.DLC = rxBits;

				rxBits = 0;
				if (rxPacket.DLC == 0) {
					counter = 15;
					phyState.state = STATE_CRC;
				} else if (rxPacket.DLC > 8) {
					phyState.error = ERROR_FORM_ERROR;
				} else {
					phyState.state = STATE_DATA_BIT7;
				}
			}
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_DATA_BIT7:
			rxBits = bit;
			phyState.state = STATE_DATA_BIT6;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT6:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT5;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT5:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT4;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT4:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT3;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT3:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT2;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT2:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT1;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT1:
			rxBits = (rxBits << 1) | bit;
			phyState.state = STATE_DATA_BIT0;
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;
		case STATE_DATA_BIT0:
			rxPacket.DATA[counter] = (rxBits << 1) | bit;
			counter++;
			if (counter == rxPacket.DLC) {
				rxBits = 0;
				counter = 15;
				phyState.state = STATE_CRC;
			} else {
				phyState.state = STATE_DATA_BIT7;
			}
			phyState.packetCrc = crc15(bit, phyState.packetCrc);
			break;

		case STATE_CRC:
			counter--;
			rxBits |= bit << counter;
			if (counter == 0) {
				rxPacket.CRC = rxBits;

				if (rxPacket.CRC != (phyState.packetCrc & 0x7FFF)) {
					phyState.error = ERROR_CRC_ERROR;
				} else {
					phyState.state = STATE_CRC_DEL;

					// Drive the ACK at start of the right bit
					canTx @ phyState.time + (QUANTA_TOTAL) <: 0;
				}
			}
			break;

		case STATE_CRC_DEL:
			rxPacket.CRC_DEL = bit;
			phyState.bitStuffingActive = 0;
			if (rxPacket.CRC_DEL == 0) {
				phyState.error = ERROR_BIT_ERROR;
			} else {
				phyState.state = STATE_ACK;
			}
			break;

		case STATE_ACK:
			// Stop driving the ACK bit
			canTx :> void;

			phyState.state = STATE_ACK_DEL;
			break;

		case STATE_ACK_DEL:
			rxPacket.ACK_DEL = bit;
			if (rxPacket.CRC_DEL == 0) {
				phyState.error = ERROR_BIT_ERROR;
			} else {
				counter = 7;
				rxBits = 0;
				phyState.state = STATE_EOF;
			}
			break;

		case STATE_EOF:
			counter--;
			rxBits |= bit << counter;

			if (bit == 0) {
				phyState.error = ERROR_BIT_ERROR;
			} else if (counter == 0) {
				rxPacket._EOF = rxBits;
				phyState.state = STATE_INTERMISSION;

				outuint(rxChan, ERROR_NONE); // Signal packet received ok
			}
			break;

		case STATE_INTERMISSION:
			counter++;
			if (bit == 0) {
				counter = 0;
				phyState.state = STATE_OVERLOAD;

			} else if (counter == 3) {
				phyState.state = STATE_BUS_IDLE;
			}
			break;

		case STATE_OVERLOAD:
			counter++;
			if (bit == 1) {
				counter = 0;
				phyState.state = STATE_OVERLOAD_DEL;
			}
			break;

		case STATE_OVERLOAD_DEL:
			counter++;
			if (counter == 8) {
				phyState.state = STATE_BUS_IDLE;
			}
			break;
		}
	}

	if (bit != phyState.lastBit) {
		bitStuffingCount = 0;
	}
	phyState.lastBit = bit;
	phyState.time += alignTable[zeros];
}

#pragma unsafe arrays
inline void canPhyTx(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount) {

	int rxBitExpectedModifier = 0;
	int rxBit = 0;
	int txBit = 0;

	if (!phyState.sendStuffBit) {

		switch (phyState.state) {
		case STATE_SOF:
			txBit = txPacket.SOF;
			counter = 11;
			phyState.state = STATE_ID;

			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_ID:
			counter--;
			txBit = txPacket.ID >> counter;
			if (counter == 0) {
				if (txPacket.IEB) {
					phyState.state = STATE_SRR;
				} else {
					phyState.state = STATE_RTR;
				}
			}
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_SRR:
			txBit = txPacket.SRR;
			phyState.state = STATE_IEB;
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_IEB:
			txBit = txPacket.IEB;
			counter = 18;
			phyState.state = STATE_EID;
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_EID:
			counter--;
			txBit = txPacket.EID >> counter;
			if (counter == 0) {
				phyState.state = STATE_RTR;
			}
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_RTR:
			txBit = txPacket.RTR;
			phyState.state = STATE_RB1;
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_RB1:
			txBit = txPacket.RB1;
			phyState.state = STATE_RB0;
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_RB0:
			txBit = txPacket.RB0;
			counter = 4;
			phyState.state = STATE_DLC;
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_DLC:
			counter--;
			txBit = txPacket.DLC >> counter;
			if (counter == 0) {
				if (txPacket.DLC == 0) {
					counter = 15;
					phyState.state = STATE_CRC;
				} else if (txPacket.DLC > 8) {
					phyState.error = ERROR_FORM_ERROR;
				} else {
					phyState.state = STATE_DATA;
				}
			}
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;

		case STATE_DATA: {
			int byteNumber = counter >> 3;
			int bitNumber = 7 - (counter & 0x7);
			txBit = txPacket.DATA[byteNumber] >> bitNumber;
			counter++;
			if (bitNumber == 0) {
				if (byteNumber + 1 == txPacket.DLC) {
					counter = 15;
					phyState.state = STATE_CRC;
				}
			}
			phyState.packetCrc = crc15(txBit, phyState.packetCrc);
			break;
		}

		case STATE_CRC:
			counter--;
			txBit = phyState.packetCrc >> counter;
			if (counter == 0) {
				txPacket.CRC = phyState.packetCrc;
				phyState.state = STATE_CRC_DEL;
			}
			break;

		case STATE_CRC_DEL:
			txBit = txPacket.CRC_DEL;
			phyState.bitStuffingActive = 0;
			phyState.state = STATE_ACK;
			break;

		case STATE_ACK:
			txBit = txPacket.CRC_DEL;
			phyState.state = STATE_ACK_DEL;
			break;

		case STATE_ACK_DEL:
			txBit = txPacket.ACK_DEL;
			counter = 7;
			phyState.state = STATE_EOF;

			// ACK should be driven low by a receiver
			rxBitExpectedModifier = 1;
			break;

		case STATE_EOF:
			counter--;
			txBit = txPacket._EOF >> counter;
			if (counter == 0) {
				phyState.state = STATE_INTERMISSION;
				phyState.txComplete = 1;
			}
			break;

		case STATE_INTERMISSION:
			counter++;
			txBit = 1;
			if (counter == 3) {
				phyState.state = STATE_BUS_IDLE;
			}
			break;
		}

		txBit = txBit & 0x1;

		// Manage bit stuffing
		if (txBit != phyState.lastBit) {
			bitStuffingCount = 0;

		} else {
			bitStuffingCount += 1;
			if (phyState.bitStuffingActive && bitStuffingCount == 5) {
				phyState.sendStuffBit = 1;
			}
		}
		phyState.lastBit = txBit;

	} else {
		phyState.sendStuffBit = 0;
		txBit = ~phyState.lastBit;
	}

	phyState.time += QUANTA_TOTAL;

	// Check for losing arbitration and errors on last bit
	#pragma xta endpoint "startTx"
	canRx @ (phyState.time - QUANTA_PHASE2_SEGS) :> rxBit;

	if (rxBit == (phyState.lastBit ^ rxBitExpectedModifier)) {
		// Send next bit
		canTx @ phyState.time <: txBit;

	} else {
		// Message failed to send
		phyState.txActive = 0;
	}
}

#pragma unsafe arrays
inline void handleError(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx) {

	#pragma xta label "handleErrorLabel"
	int lastBit = 0;
	int rxWord = 0;

/*	switch (phyState.error) {
	case ERROR_STUFF_ERROR:
	case ERROR_FORM_ERROR:
	case ERROR_BIT_ERROR:
		// Send active error flag - 6 dominant bits
		for (int i = 0; i < 6; i++) {
			canTx @ phyState.time <: 0;
			phyState.time += QUANTA_TOTAL;
		}

		// Send error delimiter
		canTx @ phyState.time <: 1;

		// Wait till recessive bit received
		do {
			canRx @ phyState.time :> rxWord;
			lastBit = (rxWord << QUANTA_PHASE2) >> 31;
			phyState.time += QUANTA_TOTAL;
		} while (lastBit != 1);

		// Send remaining recessive bits
		for (int i = 0; i < 7; i++) {
			canTx @ phyState.time <: 1;
			phyState.time += QUANTA_TOTAL;
		}
		break;

	case ERROR_CRC_ERROR:
		// Send error after ACK_DEL
		break;
	}*/

	printstr("Error ");
	printint(phyState.error);
	printstr(" in state ");
	printint(phyState.state);
	printstr(" phyState.CRC ");
	printhex(phyState.packetCrc & 0x7FFF);
	printstr(" phyState.txActive ");
	printintln(phyState.txActive);

	printPacket(rxPacket);
	while(1);

	phyState.state = STATE_BUS_IDLE;
}

void waitForBusIdle(struct CanPhyState &phyState, buffered in port:32 canRx) {
	// Ensure that the bus is in the BUS_IDLE state before starting the state machines

/*	while (1) {
		// Wait for
		canRx when pinseq(1) :> void;
		for ()
	}*/

}

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

/*	printstr("Align: ");
	for (int i = 0; i < 33; i++) {
		printint(alignTable[i]);
		printstr(", ");
	}
	printstrln("");*/
}

void initialise(clock clk, buffered in port:32 canRx, port canTx, struct CanPhyState &phyState) {
	initAlignTable();

	configure_clock_ref(clk, CLOCK_DIV);
	configure_in_port_no_ready(canRx, clk);
	set_port_clock(canTx, clk);
	start_clock(clk);

	phyState.state = STATE_BUS_IDLE;
	phyState.error = ERROR_NONE;
	phyState.txActive = 0;
	phyState.txComplete = 1;
}

void packetStart(struct CanPhyState &phyState, unsigned int &bitStuffingCount) {
	bitStuffingCount = 0;
	phyState.bitStuffingActive = 1;
	phyState.lastBit = 1;
	phyState.sendStuffBit = 0;
	phyState.packetCrc = 0;
}

int crc15(int nxtBit, unsigned int crc_rg) {
	int crc_nxt = (nxtBit ^ (crc_rg >> 14)) & 0x1;
	crc_rg = crc_rg << 1;
	if (crc_nxt) {
		crc_rg = crc_rg ^ 0x4599;
	}
	return crc_rg;
}
