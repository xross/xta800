
#include <string.h>

#include "CanIncludes.h"
#include "CanFunctions.h"
#include "CanCFunctions.h"

/*
 * CanPhy function handles the physical layer signaling
 * It is also handling the destuffing of bits for simplicity
 * as well as signaling acknowledgements on the bus
 */
int alignTable[33];

void initialise(clock clk, buffered in port:32 canRx, port canTx, struct CanPhyState &phyState);
void packetStart(struct CanPhyState &phyState, unsigned int &bitStuffingCount);
void waitForBusIdle(struct CanPhyState &phyState, buffered in port:32 canRx);
inline void phyRxPacket(struct CanPhyState &phyState, struct CanPacket &rxPacket,
		buffered in port:32 canRx, port canTx, int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount);
inline void rxStateMachine(struct CanPhyState &phyState, struct CanPacket &rxPacket, unsigned int bit, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount, int &done);
inline void txStateMachine(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount);
void handleError(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx);
int crc15(int nxtBit, unsigned int crc_rg);

#pragma unsafe arrays
void canPhyRxTx(chanend rxChan, chanend txChan, clock clk, buffered in port:32 canRx, port canTx) {

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
		bitStuffingCount = 1;
		phyState.bitStuffingActive = 1;
		phyState.lastBit = 1;
		phyState.packetCrc = 0;
		phyState.state = STATE_SOF;

		if (phyState.txComplete) {
			unsigned int txPacketNum;
			// Wait for either TX or RX to start
			select {
				case inuint_byref(txChan, txPacketNum):
					receivePacket(txChan, txPacket);
					phyState.txComplete = 0;
					break;

				case canRx when pinseq(0) :> void @ phyState.time:
					phyState.time += QUANTA_TOTAL;
					break;
			}
		}

		if (!phyState.txComplete) {
			canTx <: 0 @ phyState.time;
			phyState.lastBit = 0;

			txStateMachine(phyState, rxPacket, txPacket, canRx, canTx, counter, rxBits, bitStuffingCount);

			if (!phyState.txComplete && (phyState.error == ERROR_NONE)) {
				// Lost arbitration - finish receiving the packet
				phyRxPacket(phyState, rxPacket, canRx, canTx, counter, rxBits, bitStuffingCount);
				outuint(rxChan, phyState.error);
			}

		} else {
			phyRxPacket(phyState, rxPacket, canRx, canTx, counter, rxBits, bitStuffingCount);
			outuint(rxChan, phyState.error);
		}

		if (phyState.error) {
			handleError(phyState, rxPacket, txPacket, canRx, canTx);
		}
	}
}

#pragma unsafe arrays
inline void phyRxPacket(struct CanPhyState &phyState, struct CanPacket &rxPacket,
		buffered in port:32 canRx, port canTx, int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount) {

	int done = 0;
	while (!done) {
		unsigned int bit;
		unsigned int zeros;
		unsigned int rxWord;

		#pragma xta endpoint "startRx"
		canRx @ phyState.time :> rxWord;
		bit = (rxWord << QUANTA_PHASE2) >> 31;
		zeros = clz(rxWord << QUANTA_SJW);
		rxStateMachine(phyState, rxPacket, bit, canTx, counter, rxBits, bitStuffingCount, done);
		phyState.time += alignTable[zeros];
	}
}

#pragma unsafe arrays
inline void rxStateMachine(struct CanPhyState &phyState, struct CanPacket &rxPacket, unsigned int bit, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount, int &done) {

	// Manage bit stuffing and error detection
	bitStuffingCount += 1;
	if (phyState.bitStuffingActive && bitStuffingCount == 6) {
		if (bit == phyState.lastBit) {
			phyState.error = ERROR_STUFF_ERROR;
			done = 1;
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
					done = 1;
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
					done = 1;
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
				done = 1;
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
				done = 1;
			} else if (counter == 0) {
				rxPacket._EOF = rxBits;
				phyState.state = STATE_INTERMISSION;
			}
			break;

		case STATE_INTERMISSION:
			counter++;
			if (bit == 0) {
				counter = 0;
				phyState.state = STATE_OVERLOAD;
			} else if (counter == 3) {
				phyState.state = STATE_BUS_IDLE;
				done = 1;
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
				done = 1;
			}
			break;
		}
	}

	if (bit != phyState.lastBit) {
		bitStuffingCount = 1;
	}
	phyState.lastBit = bit;
}

#pragma unsafe arrays
inline void txStateMachine(struct CanPhyState &phyState, struct CanPacket &rxPacket, struct CanPacket &txPacket,
		buffered in port:32 canRx, port canTx,
		int &counter, unsigned int &rxBits, unsigned int &bitStuffingCount) {

	#pragma xta label "excludeTx"
	int done = 0;

	unsigned int lastBitNoStuff = 0;
	int crcActive = 1;
	unsigned int currentCrc = 0;
	unsigned int nextCrc = 0;
	unsigned int nextBit = txPacket.ID >> 10;

	while (!done) {
		unsigned int rxBit = 0;
		unsigned int expectBit = phyState.lastBit;
		unsigned int txBit = nextBit;

		// Next bit time
		phyState.time += QUANTA_TOTAL;

		#pragma xta endpoint "txCheckBit"
		canRx @ (phyState.time - QUANTA_PHASE2) :> rxBit;

		// Check for losing arbitration and errors on last bit
		rxBit = rxBit >> 31;
		if (rxBit == expectBit) {
			int sendStuffBit = 0;
			int nextStuffingCount = bitStuffingCount + 1;

			if (phyState.bitStuffingActive && nextStuffingCount == 6) {
				sendStuffBit = 1;
				nextStuffingCount = 1;
				txBit = phyState.lastBit ^ 1;
			} else if (nextBit != phyState.lastBit) {
				nextStuffingCount = 1;
			}
			bitStuffingCount = nextStuffingCount;

			#pragma xta endpoint "sendBit"
			canTx @ phyState.time <: txBit;

			if (!sendStuffBit) {
				int lastBit = nextBit;

				phyState.packetCrc = currentCrc;
				currentCrc = nextCrc;

				switch (phyState.state) {
				case STATE_SOF:
					counter = 11;
					rxBits = 0;
					phyState.state = STATE_ID;
					nextBit = txPacket.ID >> 9;
					break;

				case STATE_ID:
					counter--;
					rxBits |= lastBit << counter;
					if (counter == 0) {
						rxPacket.ID = txPacket.ID;
						if (txPacket.IEB) {
							nextBit = txPacket.RB1;
							phyState.state = STATE_SRR;
						} else {
							nextBit = txPacket.IEB;
							phyState.state = STATE_RTR;
						}
					} else if (counter == 1) {
						if (txPacket.IEB) {
							nextBit = txPacket.SRR;
						} else {
							nextBit = txPacket.RTR;
						}
					} else {
						nextBit = txPacket.ID >> (counter - 2);
					}
					break;

				case STATE_SRR:
					rxPacket.SRR = lastBit;
					nextBit = txPacket.EID >> 17;
					phyState.state = STATE_IEB;
					break;

				case STATE_IEB:
					rxPacket.IEB = lastBit;
					counter = 18;
					rxBits = 0;
					nextBit = txPacket.EID >> 16;
					phyState.state = STATE_EID;
					break;

				case STATE_EID:
					counter--;
					rxBits |= lastBit << counter;
					if (counter == 0) {
						rxPacket.EID = rxBits;
						nextBit = txPacket.RB1;
						phyState.state = STATE_RTR;
					} else if (counter == 1) {
						nextBit = txPacket.RTR;
					} else {
						nextBit = txPacket.EID >> (counter - 2);
					}
					break;

				case STATE_RTR:
					nextBit = txPacket.RB0;
					phyState.state = STATE_RB1;
					break;

				case STATE_RB1:
					nextBit = txPacket.DLC >> 3;
					phyState.state = STATE_RB0;
					break;

				case STATE_RB0:
					counter = 4;
					nextBit = txPacket.DLC >> 2;
					phyState.state = STATE_DLC;
					break;

				case STATE_DLC: {
					counter--;
					if (counter == 0) {
						if (txPacket.DLC == 0) {
							counter = 15;
							nextBit = nextCrc >> 13;
							phyState.state = STATE_CRC;
						} else {
							nextBit = txPacket.DATA[0] >> 6;
							phyState.state = STATE_DATA_BIT7;
						}
					} else if (counter == 1) {
						if (txPacket.DLC == 0) {
							nextBit = nextCrc >> 14;
							crcActive = 0;
						} else {
							nextBit = txPacket.DATA[0] >> 7;
						}
					} else {
						nextBit = txPacket.DLC >> (counter - 2);
					}
					break;
				}

				case STATE_DATA_BIT7:
					nextBit = txPacket.DATA[counter] >> 5;
					phyState.state = STATE_DATA_BIT6;
					break;
				case STATE_DATA_BIT6:
					nextBit = txPacket.DATA[counter] >> 4;
					phyState.state = STATE_DATA_BIT5;
					break;
				case STATE_DATA_BIT5:
					nextBit = txPacket.DATA[counter] >> 3;
					phyState.state = STATE_DATA_BIT4;
					break;
				case STATE_DATA_BIT4:
					nextBit = txPacket.DATA[counter] >> 2;
					phyState.state = STATE_DATA_BIT3;
					break;
				case STATE_DATA_BIT3:
					nextBit = txPacket.DATA[counter] >> 1;
					phyState.state = STATE_DATA_BIT2;
					break;
				case STATE_DATA_BIT2:
					nextBit = txPacket.DATA[counter];
					phyState.state = STATE_DATA_BIT1;
					break;
				case STATE_DATA_BIT1:
					if (counter == txPacket.DLC) {
						crcActive = 0;
						nextBit = nextCrc >> 14;
					} else {
						nextBit = txPacket.DATA[counter + 1] >> 7;
					}
					phyState.state = STATE_DATA_BIT0;
					break;
				case STATE_DATA_BIT0:
					counter++;
					if (counter == txPacket.DLC) {
						counter = 15;
						nextBit = nextCrc >> 13;
						phyState.state = STATE_CRC;
					} else {
						nextBit = txPacket.DATA[counter] >> 6;
						phyState.state = STATE_DATA_BIT7;
					}
					break;

				case STATE_CRC:
					counter--;
					if (counter == 0) {
						txPacket.CRC = phyState.packetCrc;
						nextBit = 1;
						phyState.state = STATE_CRC_DEL;
					} else if (counter == 1) {
						nextBit = 1;
					} else {
						nextBit = nextCrc >> (counter - 2);
					}
					break;

				case STATE_CRC_DEL:
					phyState.bitStuffingActive = 0;
					nextBit = 1;
					phyState.state = STATE_ACK;
					break;

				case STATE_ACK:
					nextBit = 1;
					phyState.state = STATE_ACK_DEL;
					break;

				case STATE_ACK_DEL:
					counter = 7;
					nextBit = 1;
					phyState.state = STATE_EOF;
					break;

				case STATE_EOF:
					counter--;
					nextBit = 1;
					if (counter == 0) {
						phyState.state = STATE_INTERMISSION;
						phyState.txComplete = 1;
					}
					break;

				case STATE_INTERMISSION:
					counter++;
					nextBit = 1;
					if (counter == 3) {
						phyState.state = STATE_BUS_IDLE;
						done = 1;
					}
					break;
				}
			}
			nextBit = nextBit & 0x1;
			if (crcActive) {
				nextCrc = crc15(nextBit, currentCrc);
			}

			phyState.lastBit = nextBit;

		} else {
			#pragma xta endpoint "txTurnaround"
			canTx :> void;
			done = 1;

			if (phyState.state == STATE_ACK) {
				phyState.error = ERROR_NO_ACK;

			} else if (phyState.state > STATE_RTR) {
				phyState.error = ERROR_BIT_ERROR;

			} else {
				// Lost arbitration - ring state machine into line with where it should be
				rxStateMachine(phyState, rxPacket, 0, canTx, counter, rxBits, bitStuffingCount, done);

				// Move to next bit sample time
				phyState.time += QUANTA_TOTAL;
			}
		}
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
	printhexln(phyState.packetCrc & 0x7FFF);

	printPacket(rxPacket);
	while(1);

	phyState.state = STATE_BUS_IDLE;
}

void waitForBusIdle(struct CanPhyState &phyState, buffered in port:32 canRx) {
	// Ensure that the bus is in the BUS_IDLE state before starting the state machines
	timer t;
	unsigned time;
	int done = 0;

	while (!done) {
		// Wait for bus to be high
		canRx when pinseq(1) :> void;
		t :> time;

		select {
		case canRx when pinseq(0) :> void:
			// Bus not high for full 6 bit times
			break;
		case t when timerafter(time + (BIT_TIME * 6)) :> time:
			done = 1;
			break;
		}
	}
}

void initialise(clock clk, buffered in port:32 canRx, port canTx, struct CanPhyState &phyState) {
	initAlignTable();

	configure_clock_ref(clk, CLOCK_DIV);
	configure_in_port_no_ready(canRx, clk);
	set_port_clock(canTx, clk);

	set_port_pull_up(canTx);
	set_port_pull_up(canRx);

	start_clock(clk);

	phyState.state = STATE_BUS_IDLE;
	phyState.error = ERROR_NONE;
	phyState.txComplete = 1;
}

int crc15(int nxtBit, unsigned int crc_rg) {
	int crc_nxt = (nxtBit ^ (crc_rg >> 14)) & 0x1;
	crc_rg = crc_rg << 1;
	if (crc_nxt) {
		crc_rg = crc_rg ^ 0x4599;
	}
	return crc_rg;
}
