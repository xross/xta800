#include "CanIncludes.h"

#ifdef PROGRESS
unsigned Progress(unsigned count){

	float prog = ((float)count/ (float) STRESS_LIMIT) * 100;

	
	return (unsigned)prog;

}
#endif


void printnum(char fmt[], unsigned num){
	printf(fmt, num);
}

/*
	Print Packet prints the packets with all their details
	on to the consol using the debug interface
*/
/*
void PrintPacket(struct canPacket p){

	unsigned j;

	if(p.IDE == 1){
		printf("SOF:%d\n",p.SOF);
		printf("ID:%X\n", p.ID);
		printf("SRR:%d\n", p.SRR);
		printf("IDE:%d\n", p.IDE);
		printf("EID:%X\n", p.EID);
		printf("RTR:%d\n", p.RTR);
		printf("RB1:%d\n", p.RB1);
	}
	else{
		printf("SOF:%d\n",p.SOF);
		printf("ID:%x\n", p.ID);
		printf("RTR:%d\n", p.RTR);
		printf("IDE:%d\n", p.IDE);
	}
	printf("RB0:%d\n", p.RB0);
	printf("DLC:%X\n", p.DLC);
	for(j = 0; j < p.DLC; j++){
		printf("DATA[%d]:%X\n",j, p.DATA[j]);
	}
	printf("CRC:%X\n", p.CRC);
	printf("CRCdel:%d\n", p.CRCdel);
	printf("ACKdel:%d\n", p.ACKdel);
	printf("EOF:%X\n", p._EOF);

}
*/
/*
	This is used for debugging
	It prints the values and times of bits
		Capturing is done online, then printing is done offline so as not to affect the timings 
*/

void PrintBits(unsigned times[], unsigned values[], unsigned count){

	unsigned i;

	for(i = 0; i < count; i++){
		
		printf("%d) time: %d, value: %08X, diff: %d\n", i, times[i], values[i], times[i+1] - times[i]);

	}

}

