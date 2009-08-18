#include "CanIncludes.h"

#ifdef CAN_RX
on stdcore[0]: port tx1 = CAN_TXD1;			//this writes the packet then release the line
on stdcore[0]: buffered in  port:32 rx1 = CAN_RXD1;			//this only reads the actual value on the line
#endif

#ifdef CAN_TX
on stdcore[0]: port tx2 = CAN_TXD2;		//this writes the packet then release the line
on stdcore[0]: in port rx2 = CAN_RXD2;	//this only reads the actual value on the line
#endif

#ifdef CAN_TX
/*
	CanNetworkTx is the transmitter side of network layer 
	All it is doing for now is sending packets to the Data Link Layer 
	and also printing them on the consol (over RS232) for now.
*/
unsigned CanNetworkTx(chanend toData){

	struct canPacket p;
	unsigned tmp;
	unsigned count = 0;
	unsigned crc =0;
	p.SOF = 0;
	p.ID = 0x200;
	p.RTR = 0;
	p.IDE = 0;
	p.RB0 = 0;
	p.DLC = 4;
	p.DATA[0] = 0xFE;
	p.DATA[1] = 0xE1;
	p.DATA[2] = 0xDE;
	p.DATA[3] = 0xAD;
	p.CRCdel = 1;
	p.ACKdel = 1;
	p._EOF = 0x7F;
	

	//make crc
	crc = CRC_15(p.SOF, 1, crc);
	crc = CRC_15(p.ID, 11, crc);
	crc = CRC_15(p.RTR, 1, crc);
	crc = CRC_15(p.IDE, 1, crc);
	crc = CRC_15(p.RB0, 1, crc);
	crc = CRC_15(p.DLC, 4, crc);
	for(int i=0;i< p.DLC;i++){
	  crc = CRC_15(p.DATA[i], 8, crc);
	}
	//p.CRC = 0x47FF;
	p.CRC = crc;
#ifdef LOOP
	while(count < STRESS_LIMIT)
#endif
	{
		
		toData <: p;
		toData :> tmp;
#ifdef CHECK_ACK
		if(tmp != SUCCESS){
			printstr("NET:ACK_ERROR ");
			while(1);
		}
#endif

#ifdef CAN_DEBUG_TX_DATA
		printstr("Sent packet ");
		printintln(count);
#ifdef CAN_DEBUG_PRINT_WHOLE_PACKET
		PrintPacket(p);
#endif		
#endif

		count +=1;
	}
#ifdef CAN_DEBUG	
	printnum("TX NET finished %d frames.\n", count);
#endif	
	return SUCCESS;

}
#endif//CAN_TX


#ifdef CAN_RX
/*
	CanNetworkRx is receiver side of the network layer 
	All it is doing for now is recieving packets from the Data Link Layer 
	and printing them on the consol (over RS232) for now.
*/
void CanNetworkRx(chanend fromData){

	unsigned count;

	struct canPacket p;

#ifdef LOOP
	while(count < STRESS_LIMIT)
#endif
	{
		fromData :> count;
		fromData :> p;
#ifdef CAN_DEBUG_RX_DATA
		printstr("Received packet ");
		printintln(count);



#ifdef CAN_DEBUG_PRINT_WHOLE_PACKET
	PrintPacket(p);
#endif

#endif
		count += 1;
	}
#ifdef CAN_DEBUG	
	printnum("RX NET finished %d frames.\n", count);
#endif	
}
#endif//CAN_RX


#ifdef CAN_TX
/*
	CanDataTx function is handling the transmission of packets in the Data Link Layer 
	It is doing the serialisation, frame encapsulation
	The error handling is still to be implemented
	Also the bit stuffing is handled in the CanPhyTx function

*/
int CanDataTx(chanend fromNetwork, chanend toPhy){

	struct canPacket p;

	int i, j;

	unsigned tmp;
	unsigned count = 0;

#ifdef LOOP
	while(count < STRESS_LIMIT)
#endif
		{
		fromNetwork :> p;

		toPhy <: p.SOF;
		for(i = 10, tmp = p.ID; i >= 0; i--){
			toPhy <: ((tmp >> i) & 1);
		}
		//extended frame, write extra bits
		if(p.IDE == 1){
			toPhy <: p.SRR;
			toPhy <: p.IDE;
		
			for(i = 17, tmp = p.EID; i >= 0; i--){
				toPhy <: ((tmp >> i) & 1);
			}
			toPhy <: p.RTR;
			toPhy <: p.RB1;
		} 
		else{
			
			toPhy <: p.RTR;
			toPhy <: p.IDE;

		}
		toPhy <: p.RB0;

		//write Data Length Code
		for(i = 3, tmp = p.DLC; i >= 0; i--){
			toPhy <: (tmp >> i) & 1;
		}

		if(p.DLC > 8)
			p.DLC = 8;//error?

		//write data bytes 
		for(j = 0; j < p.DLC; j++){
			for(i = 7, tmp = p.DATA[j]; i >= 0; i--){
				toPhy <: (tmp >> i) & 1;
			}
		}

		//write CRC
		for(i = 14, tmp = p.CRC; i >= 0; i--){
			toPhy <: (tmp >> i) & 1;
		}
		toPhy <: p.CRCdel;

		//recieve an ACK
		toPhy <: RECEIVE_ACK;
		toPhy :> tmp;

#ifdef CHECK_ACK
		if(tmp != 0){
			toPhy <: ACK_ERROR;
			fromNetwork <: ACK_ERROR;
			while(1);
		}
#endif
		toPhy <: p.ACKdel;
		
		//write EOF? need to move that to the start of the loop
		for(i = 6, tmp = p._EOF; i >= 0; i--){
			toPhy <: ((tmp >> i) & 1);
		}
		fromNetwork <: SUCCESS;
		toPhy <: END_PACKET;

		count += 1;

	}
#ifdef CAN_DEBUG
	printnum("TX DAT finished %d frames.\n", count);
#endif	
	return SUCCESS;
}
#endif//CAN_TX

#ifdef CAN_RX
/*
	CanData function is handling the Data Link Layer 
	It is doing the deserialisation, frame decapsulation
	The error handling is still to be implemented
	Also the bit destuffing is handled in the CanPhy (physical layer) function

*/


#define GETBIT(); current = CanIn(fromPhy); \
		if(current == last){\
			counter+=1;\
			if(counter > 5){\
				return STUFF_ERROR;\
			}\
		}\
		else\
		{\
			last = current;\
			if(counter == 5){\
				current = CanIn(fromPhy);\
				if(current == last)\
					counter = 2;\
				else{\
					counter = 1;\
					last = current;\
				}\
			}\
			else\
				counter = 1;\
		}


struct canPacket pGlobal;

unsigned GetPacket(chanend fromPhy){

	int i;
	unsigned tmp;
	unsigned current, last, counter;
	unsigned int crc = 0;

	pGlobal.SOF = CanIn(fromPhy);
	last = pGlobal.SOF;
	counter = 1;
	
	for(i = 10, tmp = 0; i >= 0; i--){
	
		GETBIT();
		tmp |= (current << i);
	}
	pGlobal.ID = tmp;

	GETBIT();
	pGlobal.SRR = current;
	GETBIT();
	pGlobal.IDE = current;


	//extended frame, read extra bits
	if(pGlobal.IDE == 1){

		for(i = 17, tmp = 0; i >= 0; i--){
			GETBIT();
			tmp |= (current << i);
		}
		pGlobal.EID = tmp;
		GETBIT();
		pGlobal.RTR = current;
		GETBIT();
		pGlobal.RB1 = current;
	} 
	else{
		pGlobal.RTR = pGlobal.SRR;
	}
	GETBIT();
	pGlobal.RB0 = current;

	//read Data Length Code
	for(i = 3, tmp = 0; i >= 0; i--){	
		GETBIT();
		tmp |= (current << i);
	}
	pGlobal.DLC = tmp;
	if(pGlobal.DLC > 8){
		pGlobal.DLC = 8;//error?
	}

	//read data bytes 
	for(i = 0; i < pGlobal.DLC; i++){

		tmp = 0;
		//loop unrolled manually
		GETBIT();
		tmp = (current << 7);
		GETBIT();
		tmp |= (current << 6);
		GETBIT();
		tmp |= (current << 5);
		GETBIT();
		tmp |= (current << 4);
		GETBIT();
		tmp |= (current << 3);
		GETBIT();
		tmp |= (current << 2);
		GETBIT();
		tmp |= (current << 1);
		GETBIT();
		tmp |= current;

		pGlobal.DATA[i] = tmp;
	}


	//read CRC
	for(i = 14, tmp = 0; i >= 1; i--){
		GETBIT();
		tmp |= (current << i);
	}
	//last bit
	GETBIT();
	if(counter == 5)
		CanIn(fromPhy);//get the last stuffed bit?

	CanOut(fromPhy, SEND_ACK);

	tmp |= current;

	pGlobal.CRC = tmp;
	crc = CRC_15(0, 15, crc);
	crc = CRC_15(pGlobal.SOF, 1, crc);
	crc = CRC_15(pGlobal.ID, 11, crc);
	crc = CRC_15(pGlobal.RTR, 1, crc);
	crc = CRC_15(pGlobal.IDE, 1, crc);
	crc = CRC_15(pGlobal.RB0, 1, crc);
	
	crc = CRC_15(pGlobal.DLC, 4, crc);

	for(int i=0;i< pGlobal.DLC;i++){
	  crc = CRC_15(pGlobal.DATA[i], 8, crc);
	}
	
	if(crc != pGlobal.CRC){
	  return CRC_ERROR;
	}
	

	return SUCCESS;
}

int CRC_15(int inputStream, int bitCount,unsigned int crc_rg){
  int crc_nxt;
  int nxtBit;
  for(int i=0;i<bitCount;i++){
    nxtBit = (inputStream>>((bitCount-1)-i))&1;
    crc_nxt = nxtBit ^ ((crc_rg>>14)&1);
    crc_rg = crc_rg << 1;
    crc_rg = crc_rg & (0x7FFE);
    if(crc_nxt){
      crc_rg = crc_rg ^ 0x4599;
    }
  }
  return crc_rg;
}

void CanDataRx(chanend toNetwork, chanend fromPhy){

#ifdef CAN_DEBUG_RX_PHY
  /*
		just catch 500 bits and print them
		in this mode you can easily identify where the stuffed bits are
		since the times will be 2*BIT_TIME spaced
	*/
  unsigned values[500];
  unsigned times[500];
	unsigned i;


  for(i = 0; i < 500; i++){

    times[i] = CanIn(fromPhy);
    values[i] = CanIn(fromPhy);

  }

  PrintBits(times, values, 500);
  
#else


	unsigned count = 0, result;

	//will do destuffing here rather than in the physical layer

#ifdef LOOP
	while(count < STRESS_LIMIT)
#endif
		{
		
		result = GetPacket(fromPhy);
		switch(result){
			
			case SUCCESS:
				//send packet to network layer for processing
				toNetwork <: count;
				toNetwork <: pGlobal;
				count+=1;
				break;

			case STUFF_ERROR:
				printstrln("Stuff Error in CanDataRx");//need to handle more appropriatly
				CanOut(fromPhy, STUFF_ERROR);//let the PHY layer terminate this frame
				break;

			case CRC_ERROR:
				printstrln("CRC Error in CanDataRx");//need to handle more appropriatly
				CanOut(fromPhy, CRC_ERROR);//let the PHY layer terminate this frame
				break;

			default:
				printstrln("UNKNOWN result in CanDataRx");
				CanOut(fromPhy, UNKNOWN_ERROR);
				break;
		}
	}
#ifdef CAN_DEBUG
	printnum("RX DAT finished %d frames.\n", count);
#endif

#endif
}

#endif//CAN_RX


#ifdef CAN_TX

unsigned StartOfFram(chanend fromData, in port canRX, timer t){

		unsigned tmpTime, sof = 1, countIdle = 0, tmpValue, tmp;
		t :> tmpTime;

		canRX when pinseq(1) :> tmpValue;

		while(sof){
			select{
				case canRX when pinsneq(tmpValue) :> tmpValue:
					countIdle = 0;
					t :> tmpTime;
					break;
				case fromData :> tmpValue://SOF
					sof = tmpValue;//should be 0
					break;
				case t when timerafter(tmpTime + BIT_TIME) :> tmp:
					tmpTime += BIT_TIME;//to avoid drift
					if(tmpValue == 1)
						countIdle +=1;
					break;
			}
		}

		while(countIdle < BUS_IDLE_PERIOD){

			select{

				case canRX when pinsneq(tmpValue) :> tmpValue:
					countIdle = 0;
					t :> tmpTime;
					break;
				case t when timerafter(tmpTime + BIT_TIME) :> tmp:
					tmpTime += BIT_TIME;//to avoid drift
					if(tmpValue == 1)
						countIdle +=1;
					break;
			}
		}

		//reaching here would mean that the bus has been idle for more than BUS_IDLE_PERIOD of bit times,
		//and a start of fram signaled from the data layer...
		//tmpTime is the current time, on which the start bit should go
		return tmpTime;
}

on stdcore[0]: clock tx_cb = XS1_CLKBLK_2; 
/*
	CanPhy function handles the transmit side of the physical layer signaling 
	It receive bits and commands from the Data Link Layer, and uses them to control the physical bus
	It is also handling the stuffing of bits for simplicity
*/
void CanPhyTx(chanend fromData, in port canRX, port canTX){

	unsigned tmpTime, tmpValue, oldValue, count, done, tmp, tmp2, nostuffing;

	//need to keep track of the time using the timer and 
	//value changes on the port using conditional input on the port
	timer t;



#ifdef PROGRESS
	unsigned oldProgress = Progress(0);
#endif


	unsigned counter = 0;
	unsigned frameCounter = 0, bitCounter = 0, sof;

	stop_clock(tx_cb);
	configure_clock_ref(tx_cb, CLOCK_DIV);
	set_port_clock(canRX, tx_cb);
	set_port_clock(canTX, tx_cb);
	start_clock(tx_cb);

      printintln(BIT_TIME);

printintln(SAMPLE_TIME);
#ifdef LOOP
	while(frameCounter < STRESS_LIMIT)
#endif
		{

		tmpTime = StartOfFram(fromData, canRX, t);
		//this will halt untill the bus has became idle
		//and start of frame trigger has been recieved from the data layer 

		frameCounter += 1;
		bitCounter = 1;
		canTX <: 0;//sof
		t :> tmpTime;//it takes about 300 nano seconds from the end of the function StartOfFram to reach here, 
		//hence need to capture the time again
		sof = tmpTime;


		count = 1;
		nostuffing = 0;
		done = 0;
		oldValue = 0;
		
		while(!done){
		
			fromData :> tmpValue;

			switch(tmpValue){
				case(0):
				case(1):
					bitCounter += 1;

					tmpTime += BIT_TIME;
					t when timerafter(tmpTime) :> tmp;
					canTX <: tmpValue;

#ifdef CHECK_RX
					t when timerafter(tmpTime + SAMPLE_TIME) :> tmp;
					canRX :> actualValue;
					if(actualValue != tmpValue)
					{
						
						trigger <: 0;

						printstr("value on the line is different from transmitted value...\n\n\nframeCounter: ");
						printintln(frameCounter);
						printstr("bitCounter: ");
						printintln(bitCounter);
						printstrln("\n");

						while(1);

					}
#endif
				
					//need to stuff here...
					if(oldValue == tmpValue && !nostuffing){
						count+=1;
						if(count == 5){
							tmpValue = !oldValue;
							tmpTime += BIT_TIME;
							t when timerafter(tmpTime) :> tmp;
							canTX <: tmpValue;
#ifdef CHECK_RX
							t when timerafter(tmpTime + SAMPLE_TIME) :> tmp;
							canRX :> actualValue;
							if(actualValue != tmpValue)
							{
								printstrln("value on the line is different from transmitted value, halting...");
								while(1);
							}
#endif
							count = 1;
						}
					}
					else{
						count = 1;
					}
					oldValue = tmpValue;
					//need to check for bit errors here
					//i.e. what you drive is what is driven??!
					break;
				case(RECEIVE_ACK):
					canRX :> tmp;//sample
					tmpTime += BIT_TIME;
					t when timerafter(tmpTime) :> tmp;
					canTX <: 1;//drive, this will relase the line quicker
					canTX :> tmp;//release the line
					tmpTime += SAMPLE_TIME;

					t when timerafter(tmpTime) :> tmp;
					canRX :> tmp;//sample
					tmp2 = tmpTime;

					while(tmp != 0){
						canRX :> tmp;//sample
						t :> tmp2;
					}
#ifdef CHECK_ACK					
					if(tmp2 != tmpTime){
						printstrln("receive ACK ERROR");
						printintln(sof);
						printintln(tmpTime);
						printintln(tmp2);
						printintln(tmp2 - tmpTime);
						printintln(counter);
						while(1);
					}
#endif					
					counter += 1;

					tmpTime -= SAMPLE_TIME;
					//the start of the current bit, 
					//it will be incremented in the next iteration
					nostuffing = 1;//need to do this before the CRC del

					fromData <: tmp; 
					break;
#ifdef CHECK_ACK
				case(ACK_ERROR):
					printstr("PHY:ACK_ERROR ");
					while(1);
					break;
#endif
				case(END_PACKET):
					done = 1;
					break;
			}
		}// while(!done)


#ifdef CANUSB
		//need to load the bus at least at 10%
		//due to the speed limit of the CANUSB device
		t :> tmp;
		t when timerafter(tmp + (BIT_TIME * 140) * 1000) :> tmp;
		//delay about 1000 packet times to make sure the RS232 in the CANUSB bundle to finish transmitting
#endif

	
#ifdef PROGRESS
		tmp2 = Progress(counter);
		if(tmp2 != oldProgress){
			t :> tmp;
			//to allow for reception of the packet before the intrusive JTAG call which could otherwise mess the timing
			t when timerafter(tmp + (BIT_TIME * 140)) :> tmp;
			oldProgress = tmp2;
			printint(oldProgress);
			printstr("%");
			if(oldProgress == 99 || oldProgress == 100)
				printstr("\n");
		}
#endif



	}
#ifdef CAN_DEBUG
	printnum("TX PHY finished %d frames.\n", frameCounter);
#endif	
}
#endif//CAN_TX

int main(){

#ifdef CAN_RX
	chan canPhyDataRx, canDataNetworkRx;
#endif

#ifdef CAN_TX
	chan canPhyDataTx, canDataNetworkTx;
#endif


	par{
		/*
			Run the three layers handling functions
			The packets will only go as far as the Network Layer for now
			from where there are printed on the consol for now
		*/
#ifdef CAN_RX
		on stdcore[0]: CanPhyRx(canPhyDataRx, rx1, tx1);
		on stdcore[0]: CanDataRx(canDataNetworkRx, canPhyDataRx);
		on stdcore[0]: CanNetworkRx(canDataNetworkRx);
#endif
#ifdef CAN_TX
		on stdcore[0]: CanPhyTx(canPhyDataTx, rx2, tx2);
		on stdcore[0]: CanDataTx(canDataNetworkTx, canPhyDataTx);
		on stdcore[0]: CanNetworkTx(canDataNetworkTx);
#endif

	}

	return 0;
}




void PrintPacket(struct canPacket p){

	unsigned j;
	printstr("SOF: ");
	printhexln(p.SOF);
	printstr("ID: ");
	printhexln(p.ID);
	if(p.IDE == 1){
	  printstr("SRR: ");
	  printhexln(p.SRR);
	  printstr("IDE: ");
	  printhexln(p.IDE);
	  printstr("EID: ");
	  printhexln(p.EID);
	  printstr("RTR: ");
	  printhexln(p.RTR);
	  printstr("RB1: ");
	  printhexln(p.RB1);
	} else {
	  printstr("RTR: ");
	  printhexln(p.RTR);
	  printstr("IDE: ");
	  printhexln(p.IDE);
	}
	printstr("RB0: ");
	printhexln(p.RB0);
	printstr("DLC: ");
	printhexln(p.DLC);

	for(j = 0; j < p.DLC; j++){
	  printstr("DATA[");
	  printint(j);
	  printstr("]:");
	  printhexln(p.DATA[j]);
	}
	printstr("CRC: ");
	printhexln(p.CRC);
	printstr("CRCdel: ");
	printhexln(p.CRCdel);
	printstr("ACKdel: ");
	printhexln(p.ACKdel);
	printstr("EOF: ");
	printhexln(p._EOF);

}
