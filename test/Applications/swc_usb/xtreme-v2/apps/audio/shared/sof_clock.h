#define CLOCK_MODE_LOCAL    0
#define CLOCK_MODE_EXTERNAL_SPDIF 1
#define CLOCK_MODE_EXTERNAL_ADAT 2

#define CLOCK_FREQ_LOCAL    3
#define CLOCK_FREQ_EXTERNAL_SPDIF 4
#define CLOCK_FREQ_EXTERNAL_ADAT 5

#define CLOCK_MODE_SELECTION 6


/* Generates 100Hz clock based on 500Hz SOF signal */
void clockRecovery(out port p_hundredHz,
                   chanend c_spDifRecord, chanend c_adatRecord, chanend c_digitalData,
                   chanend c_clockControl, chanend c_interruptControl, chanend clockQueryZero, chanend clockQueryInterrupt);

