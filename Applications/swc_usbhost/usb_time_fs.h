/* Defines for USB FS timing
   Assumes 100MHz default clock for timers 
   ie 10ns tick width
*/

// ref time tick is 10ns
#define REF_TIME_TICK (1000 / XS1_TIMER_MHZ)

#define T_WFTFS (500000 / REF_TIME_TICK)

#define PACKET_TIMEOUT (100000 / REF_TIME_TICK)
