#include "CanIncludes.h"



#ifdef RXPHY
/*
	CanPhy function handles the physical layer signaling 
	It detects bits and edges on the line, and signals them to the Data Link Layer
	It is also handling the destuffing of bits for simplicity
	as well as signaling acknowledgements on the bus
*/
clock rx_cb = XS1_CLKBLK_1; 

void CanPhyRx(chanend toData, buffered in port:32 canRX, port canTX){

	unsigned tmpValue, done, bitFinishTime, tmpTime, zeros, phaseError, i, command, ackTime, currentTime;

#ifdef LOOP
	unsigned frameCounter = 0;
#endif

	stop_clock(rx_cb);
	configure_clock_ref(rx_cb, CLOCK_DIV);
	set_port_clock(canRX, rx_cb);
	set_port_clock(canTX, rx_cb);
	start_clock(rx_cb);
	
	bitFinishTime = 0;

#ifdef LOOP
	while(frameCounter < STRESS_LIMIT)
	{
		frameCounter += 1;
#endif		

		canRX when pinseq(0) :> tmpValue @ tmpTime;//trigger start of frame
		
		done = 0;

		tmpTime += QUANTA_TOTAL + QUANTA_PROP;//end of propdelay of next bit

		while(!done)
		{
			select {
				case toData :> command:
				  #pragma xta label commandReceived
					if(command == SEND_ACK){
						//after sent ack, will go and recieve the end of packet and in a different part
						//this will simplify the control here, and hence shorten the critical path
						bitFinishTime = tmpTime - QUANTA_PROP - QUANTA_SYNC;
						//there is a pause time which need be cleared here
						currentTime = CanGetts(canTX);
						//canRX :> tmpValue @ currentTime2;//this would wait 32 cycles?
						canRX @ bitFinishTime :> tmpValue;
						//this is the end of the CRC DEL bit
						//the ack bit should be at QUANTA_PHASE2 distance from the end
						canTX <: 0 @ ackTime;

						//need to send the ack in assembly
						//this is not working since the port is enabled for events
						//and is not responding untill that time has elapsed

						printstr("currentTime:");
						printhexln(currentTime);
						printstr("bitFinishTime:");
						printhexln(bitFinishTime);
						printstr("ackTime:");
						printhexln(ackTime);
						printstr("tmpTime:");
						printhexln(tmpTime);

						bitFinishTime+=QUANTA_TOTAL;
						canTX @ bitFinishTime <: 1;
						//make sure the bus is stable has charged at 1 before releasing it
						canTX @ bitFinishTime + QUANTA_PROP :> int x;
						//Now need to check CRC DEL, clock in and check ACK DEL + 7 bits EOF
						if ((tmpValue << QUANTA_PHASE2) >> 31 != 1 ){
							printstrln("CRC DEL ERROR");//TODO: handle this appropriatly
						}

						for(i = 0; i < 7; i+=1){
	
							bitFinishTime+=QUANTA_TOTAL;
							canRX @ bitFinishTime :> tmpValue;
							if ((tmpValue << QUANTA_PHASE2) >> 31 != 1 ){
								printstrln("EOF DEL ERROR");//TODO: handle this appropriatly
							}
						}
					}
					done = 1;
					break;

  		  #pragma xta pragma dataReceiveLoop
				case	canRX @ tmpTime :> tmpValue:
					
#ifdef CAN_DEBUG_RX_PHY
					CanOut(toData, tmpTime);
					CanOut(toData, (tmpValue << 17) >> 31);
#else
					CanOut(toData, (tmpValue << 17) >> 31);
#endif
					zeros = clz(tmpValue);

					if(zeros == QUANTA_PROP || zeros >= 18 || zeros == 0){
						tmpTime += QUANTA_TOTAL;
					}
					else if(zeros < QUANTA_PROP){
						//edge is late, in the propagation delay segment
						//need to extend the bit time
						phaseError = QUANTA_PROP - zeros;
						if(phaseError < QUANTA_SJW)
							tmpTime += QUANTA_TOTAL + phaseError;
						else
							tmpTime += QUANTA_TOTAL + QUANTA_SJW;//can't compensate for the error totally

					}
					else if(zeros < 18){
						phaseError = zeros - QUANTA_PROP - QUANTA_SYNC;
						if(phaseError < QUANTA_SJW)
							tmpTime += QUANTA_TOTAL - phaseError;
						else
							tmpTime += QUANTA_TOTAL + QUANTA_SJW;
					}
					break;

			}//select

		}//while(!done)
#ifdef LOOP
	}//#ifdef LOOP while(frameCounter < STRESS_LIMIT)
	printnum("RX PHY finished %d frames.\n", frameCounter);
#endif

}
#endif


