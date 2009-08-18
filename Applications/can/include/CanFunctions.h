#ifndef _CAN_FUNCTIONS
#define _CAN_FUNCTIONS


void PrintPacket(struct canPacket p);

void PrintBits(unsigned times[], unsigned values[], unsigned count);

void printnum(char fmt[], unsigned num);

unsigned Progress(unsigned count);


#ifdef __XC__
//todo: add final functions prototype API
void CanPhyRx(chanend toData, buffered in port:32 canRX, port canTX);

//not used anymore
void CanSettw(buffered in port:32 canRX, unsigned value);
void CanClrpt(buffered in port:32 canRX);
unsigned CanGetts(port canTX);
void CanOut(chanend c, unsigned value);
unsigned CanIn(chanend c);
int CRC_15(int nxtBit, int bitCount,unsigned int crc_rg);
void PrintPacket(struct canPacket p);
#endif

#endif
