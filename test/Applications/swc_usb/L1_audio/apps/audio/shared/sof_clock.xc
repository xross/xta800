
/**
 * @file sof_clock.xc
 * @brief Generates 100Hz clock to clock sync based on 500Hz SOF signal from channel
 * @author Ross Owen, XMOS Semiconductor
 * @version 0.1
 */

#include <xs1.h>
#include <xclib.h>
#include <print.h>
#include "sof_clock.h"

/**
 * @brief Generates 100Hz clock to external sync chip based on 500Hz SOF signal from channel
 * @warning Only expects tick per frame, not microframe (i.e. change in frame number)
 * @param[in] c_sof SOF channel from XUD
 * @param[in] p     Out put port on which clock is generated
 *
 * @return    void
 */

#define TWO_HUNDRED_HZ     (500000)
#define TWO_HUNDRED_MARGIN   5000

#define M 48000

static int clockMode = CLOCK_MODE_LOCAL;
static int clockFrequency = M;

static int clockSettings[7] = { 1, 0, 0, 48000, 48000, 48000, 1 };

#include <platform.h>

int lval = 0;
on tile[2] : out port theLeds                  = XS1_PORT_8D;

void reportErrors(chanend c) {
    timer t;
    int lastIn, lastOut, now;
    unsigned char ct;
    int lval = 0;
t :> now;
    while (1) {
        select {
        case inct_byref(c, ct):
            t :> now;
            if (ct == 4) {
                if ((now - lastOut) > 18000) {
                    lval ^= 0x10;
                }
                lastOut = now;
            } else {
                if ((now - lastIn) > 18000) {
                    lval ^= 0x40;
                }
                lastIn = now;
            }
            theLeds <: lval;
            break;
        case t when timerafter(now + 18000) :> now:
            if ((now - lastOut) > 18000) {
                lval ^= 0x10;
                lastOut = now;
            }
            if ((now - lastIn) > 18000) {
                lval ^= 0x40;
                lastIn = now;
            }
            theLeds <: lval;
            break;
        }
    }
}



int channelContainsControlToken(chanend x);

void outInterrupt(chanend c_interruptControl, int value) 
{
    /* Non-blocking check for control token */
    if (channelContainsControlToken(c_interruptControl)) 
    {
        outuchar(c_interruptControl, value);
        outct(c_interruptControl, 1);
    }
}

void setClockValidity(chanend c_interruptControl, int mode, int valid) 
{
    if (clockSettings[mode] != valid) {
        clockSettings[mode] = valid;
        outInterrupt(c_interruptControl, 2 | 4 | 8 | 32);
    }
}

#define MAXSAMPLES      64                     // must be power of 2
#define MAXSPDIFSAMPLES (2 * MAXSAMPLES)       // must be power of 2
#define MAXADATSAMPLES  (8 * MAXSAMPLES)       // must be power of 2

#define   recordSpdif(spdifSamples, offset, value, index)     spdifSamples[index + offset] = value<<4;
#define   recordAdat(adatSamples, offset, value, index)     adatSamples[index + offset] = value;

int abs(int x) {
    if (x < 0) return -x;
    return x;
}


void clearBuffers(int spdifSamples[], int adatSamples[]) {
    for(int i = 0; i < MAXSPDIFSAMPLES; i++) {
        spdifSamples[i] = 0;
    }
    for(int i = 0; i < MAXADATSAMPLES; i++) {
        adatSamples[i] = 0;
    }
}

typedef struct {
    int receivedSamples;
    int samples;
    int savedSamples;
    int lastDiff;
    unsigned identicaldiffs;
    int samplesPerTwoHundredHz;
} Counter;

void clearSamples(Counter &counter) {
    counter.lastDiff = -1;
    counter.identicaldiffs = 0;
}

int validSamples(Counter &counter, int index) {
    int diff = counter.samples - counter.savedSamples;

    counter.savedSamples = counter.samples;
    
    if (diff != 0 && abs( diff - counter.lastDiff ) < 3 ) {
        counter.identicaldiffs++;           /* Check for stable sample rate from S/PDIF*/
        if (counter.identicaldiffs > 200) {
            /* Detect correct sample rate (round to nearest) */
            int s = -1;
            
            if (diff > 430 && diff < 450) {
                s = 441;
            } else if (diff > 470 && diff < 490) {
                s = 480;
            } else if (diff > 940 && diff < 980) {
                s = 960;
            } else if (diff > 1880 && diff < 1960) {
                s = 1920;
            }
                    
            if (s != -1) { // Hit a valid frequency
                counter.samplesPerTwoHundredHz = s;
                
                /* Update record of external clock source sample frequency */
                clockSettings[index] = s * 100;
                
                /* In internal clock mode, switch to external clock */
//                counter.receivedSamples = 0;                                        // Case 2: we just had a clock.
                
                /* Set current clock as invalid, external clock as valid */
                return 1;
            } else { // Not a valid frequency - find another run of 200 identical samples
                counter.identicaldiffs = 0;
            }
        }
    } else {
        counter.identicaldiffs = 0;
        counter.lastDiff = diff;
    }
    return 0;
}


/*
 * State changes:
 * Host -> External (device initiated)    1
 * Local -> External (device initiated)   2
 * External -> Local (device initiated)   3
 * Local -> Host (host initiated)         4
 * Host -> Local (host initiated)         5
 */

void clockRecovery(out port p_hundredHz,
                   chanend c_adatRecord, chanend c_spdifRecord, chanend c_digitalData,
                   chanend c_clockControl, chanend c_interruptControl, chanend clockQueryZero, chanend clockQueryInterrupt) {
    int val = 0;
    unsigned int tmp, tmp2;
    timer t, clockDetection;
    int timeNextEdge, spdifReceivedTime, adatReceivedTime, timeNextClockDetection;
    int spdifLedCnt = 0, adatLedCnt = 0, localLedCnt = 0;
    char tmpCT;
    int spdifWriteIndex = 0;
    int spdifReadIndex = 0;
    int spdifSamples[MAXSPDIFSAMPLES];
    int adatChannel = 0;
    int adatWriteIndex = 0;
    int adatReadIndex = 0;
    int adatSamples[MAXADATSAMPLES];
    int clockChanged = 0;
    Counter adatCounters, spdifCounters;

    for(int i = 0; i<MAXSPDIFSAMPLES; i++) {
        spdifSamples[i] = 0;
    }
    for(int i = 0; i<MAXADATSAMPLES; i++) {
        adatSamples[i] = 0;
    }
    /* Initial port val */
    t :> timeNextEdge;
    timeNextEdge += TWO_HUNDRED_HZ;
    timeNextClockDetection = timeNextEdge + TWO_HUNDRED_HZ / 2;
    p_hundredHz <: val;

    clearBuffers(spdifSamples, adatSamples);
    outuint(c_digitalData, M);
    while(1) 
    {
        select 
        {
            /* Command from Endpoint 0 to change clock mode */
        case clockQueryZero :> tmp:
            clockQueryZero <: clockSettings[tmp];
            break;

        case c_clockControl :> tmp:
            
                switch (tmp) 
                {
                    case 0:
                        c_clockControl :> tmp;

                        switch (tmp) 
                        {
                            case 1:
                                if (clockSettings[CLOCK_MODE_LOCAL] && clockMode != CLOCK_MODE_LOCAL) 
                                {                      /* 4 TODO: smooth clock */
                                    clockSettings[CLOCK_MODE_SELECTION] = tmp;
                                    clockMode = CLOCK_MODE_LOCAL;
                                    timeNextEdge -= TWO_HUNDRED_MARGIN;
                                }
                                break;
                        
                            case 2: 
                                if (clockSettings[CLOCK_MODE_EXTERNAL_ADAT]) 
                                {                       /* 1/2 TODO: smooth clock */
                                    clockSettings[CLOCK_MODE_SELECTION] = tmp;

                                    t :> tmp;
                                    tmp = TWO_HUNDRED_HZ - (timeNextEdge - tmp);
                                    timeNextEdge += TWO_HUNDRED_MARGIN;
                                    adatCounters.receivedSamples = tmp * adatCounters.samplesPerTwoHundredHz / TWO_HUNDRED_HZ;
                                    
                                    clockMode = CLOCK_MODE_EXTERNAL_ADAT;
                                    lval = 0;
                                }
                                break;
                        
                            case 3:
                                if (clockSettings[CLOCK_MODE_EXTERNAL_SPDIF]) 
                                {
                                    clockSettings[CLOCK_MODE_SELECTION] = tmp;
                                    
                                    t :> tmp;
                                    tmp = TWO_HUNDRED_HZ - (timeNextEdge - tmp);
                                    timeNextEdge += TWO_HUNDRED_MARGIN;
                                    spdifCounters.receivedSamples = tmp * spdifCounters.samplesPerTwoHundredHz / TWO_HUNDRED_HZ;

                                    clockMode = CLOCK_MODE_EXTERNAL_SPDIF;
                                    lval = 0;
                                }
                                break;
                            default:
                                //printintln(tmp);
                                break;
                        }
                        spdifReadIndex = (spdifWriteIndex ^ (MAXSPDIFSAMPLES >> 1)) & ~1;// Read from middle
                        adatReadIndex = (adatWriteIndex ^ (MAXADATSAMPLES >> 1)) & ~7;   // sample onwards
                        break;
                    
                    case 1:
                        c_clockControl :> clockFrequency;
//                        clockFrequency = 48000;
                        clockSettings[CLOCK_FREQ_LOCAL] = clockFrequency;
                        clockChanged = 1;
                        break;
                default:
                    printstr("Unknown clock request ");
                    printintln(tmp);
                        break;
                }
                break;


        /* Our own 200Hz toggle (to generate 100 Hz) */
        case t when timerafter(timeNextEdge) :> int _:

            /* At this stage we are LOCAL clocked - guaranteed not EXTERNAL */
            /* generate clock based on our own reference */

            val = !val;
            p_hundredHz <: val; 

            timeNextClockDetection = timeNextEdge + TWO_HUNDRED_HZ / 2;  // Keep this out of phase!
            timeNextEdge += TWO_HUNDRED_HZ;

            if (localLedCnt++ >= 200) {
                lval ^= 0x40;
//                theLeds <: lval;
                localLedCnt = 0;
            }
            break;

        case clockDetection when timerafter(timeNextClockDetection) :> int _:
            timeNextClockDetection += TWO_HUNDRED_HZ;
          
            if (validSamples(adatCounters, CLOCK_FREQ_EXTERNAL_ADAT)) {
                if (!clockSettings[CLOCK_MODE_EXTERNAL_ADAT]) {
                    setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_ADAT, 1);
                    spdifReadIndex = (spdifWriteIndex ^ (MAXSPDIFSAMPLES >> 1)) & ~1;// Read from middle
                    adatReadIndex = (adatWriteIndex ^ (MAXADATSAMPLES >> 1)) & ~7;   // sample onwards
                }
            } else {
                setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_ADAT, 0);                
                lval &= ~0x10;
                if (clockMode == CLOCK_MODE_EXTERNAL_ADAT) {
                    clockMode = CLOCK_MODE_LOCAL;
                }
            }

            if (validSamples(spdifCounters, CLOCK_FREQ_EXTERNAL_SPDIF)) {
                if (!clockSettings[CLOCK_MODE_EXTERNAL_SPDIF]) {
                    setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_SPDIF, 1);
                    spdifReadIndex = (spdifWriteIndex ^ (MAXSPDIFSAMPLES >> 1)) & ~1;// Read from middle
                    adatReadIndex = (adatWriteIndex ^ (MAXADATSAMPLES >> 1)) & ~7;   // sample onwards
                }
            } else {
                setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_SPDIF, 0);                
                lval &= ~0x20;
                if (clockMode == CLOCK_MODE_EXTERNAL_SPDIF) {
                    clockMode = CLOCK_MODE_LOCAL;
                }
            }

            break;

        /* S/PDIF sample received */
        case inuint_byref(c_spdifRecord, tmp):
            t :> spdifReceivedTime;

            tmp2 = tmp & 0xf;
            if (tmp2 == 9 || tmp2 == 0xc || tmp2 == 0xa) {
                recordSpdif(spdifSamples, 0, tmp, spdifWriteIndex);
                spdifWriteIndex = spdifWriteIndex + 1;
                if (spdifWriteIndex >= MAXSPDIFSAMPLES) {
                    spdifWriteIndex = 0;
                }
                if (tmp2 == 0xc) {
                    spdifWriteIndex &= ~1;
                }
                spdifCounters.samples++;
                if (clockMode == CLOCK_MODE_EXTERNAL_SPDIF) {
                    spdifCounters.receivedSamples ++;
                    if (spdifCounters.receivedSamples >= spdifCounters.samplesPerTwoHundredHz) {
                   //     if (timeafter(spdifReceivedTime + 2 * TWO_HUNDRED_MARGIN, timeNextEdge)) {
                            timeNextEdge = spdifReceivedTime + TWO_HUNDRED_HZ + TWO_HUNDRED_MARGIN;
                            val = !val;
                            p_hundredHz <: val; 
                            spdifCounters.receivedSamples = 0;
                  //      } else {
          /*                  clockMode = CLOCK_MODE_LOCAL;
                            setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_SPDIF, 0);
                            spdifCounters.lastDiff = -1; */
                   //     }
/*                        if (clockSettings[CLOCK_MODE_EXTERNAL_ADAT] &&
                            timeafter(spdifReceivedTime, adatReceivedTime + TWO_HUNDRED_HZ)) {
                            setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_ADAT, 0);                            
                        }*/
                    }
                }
                if (spdifLedCnt++ > 96000) {
                    lval ^= 0x20;
//                    theLeds <: lval;
                    if (((lval & 0x20) != 0) == (clockMode == CLOCK_MODE_EXTERNAL_SPDIF)) {
                        spdifLedCnt = -75000;
                    } else {
                        spdifLedCnt = 75000;
                    }
                }
            }
            break;


        /* ADAT sample received */
        case inuint_byref(c_adatRecord, tmp):
            t :> adatReceivedTime;

            if ((tmp & 0xff000000) == 0xff000000) {
                recordAdat(adatSamples, 0, tmp << 8, adatWriteIndex + adatChannel); // bug 5835 sext
                adatChannel = (adatChannel + 1) & 7;
            } else if (tmp & 0x80000000) {
                adatChannel = 0;
                adatCounters.samples+=2;
                adatWriteIndex = adatWriteIndex + 8;
                if (adatWriteIndex >= MAXADATSAMPLES) {
                    adatWriteIndex = 0;
                }
                if (clockMode == CLOCK_MODE_EXTERNAL_ADAT) 
                {   
                    adatCounters.receivedSamples += 2;
                    if (adatCounters.receivedSamples >= adatCounters.samplesPerTwoHundredHz) 
                    {
                        if (timeafter(adatReceivedTime + 2 * TWO_HUNDRED_MARGIN, timeNextEdge)) {
                            timeNextEdge = adatReceivedTime + TWO_HUNDRED_HZ + TWO_HUNDRED_MARGIN;
                            val = !val;
                            p_hundredHz <: val; 
                            adatCounters.receivedSamples -= adatCounters.samplesPerTwoHundredHz;
//                            adatCounters.receivedSamples = 0;
                        } else {
/*                            clockMode = CLOCK_MODE_LOCAL;
                            setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_ADAT, 0);
                            adatCounters.lastDiff = -1; */
                        }
/*                        if (clockSettings[CLOCK_MODE_EXTERNAL_SPDIF] &&
                            timeafter(adatReceivedTime, spdifReceivedTime + TWO_HUNDRED_HZ)) {
                            setClockValidity(c_interruptControl, CLOCK_MODE_EXTERNAL_SPDIF, 0);                            
                        }*/
                    }
                }
                if ((adatLedCnt+=2) > 96000) {
                    lval ^= 0x10;
//                    theLeds <: lval;
                    if (((lval & 0x10) != 0) == (clockMode == CLOCK_MODE_EXTERNAL_ADAT)) {
                        adatLedCnt = -75000;
                    } else {
                        adatLedCnt = 75000;
                    }
                }
            }
            break;

        /* Mixer requests ADAT and SPDIT sample */
        case inct_byref(c_digitalData, tmpCT):
            if (clockChanged && ( adatReadIndex & 7) == 0) {
                outct(c_digitalData, 1);
                outuint(c_digitalData, clockFrequency);
                outct(c_digitalData, XS1_CT_PAUSE);
                clockChanged = 0;
            } else {
                switch (clockFrequency) {
                case 48000:
                case 44100:
                    if (clockSettings[CLOCK_MODE_EXTERNAL_ADAT]) {
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                    } else {
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                    }
                    break;
                case 96000:
                    if (clockSettings[CLOCK_MODE_EXTERNAL_ADAT]) {
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                    } else {
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                    }
                    break;
                case 192000:
                    if (clockSettings[CLOCK_MODE_EXTERNAL_ADAT]) {
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                        outuint(c_digitalData, adatSamples[adatReadIndex++]);
                    } else {
                        outuint(c_digitalData, 0);
                        outuint(c_digitalData, 0);
                    }
                    break;
                default:
                    printstr("Unknown frequency\n");
                    printintln(clockFrequency);
                    break;
                }
                if (clockSettings[CLOCK_MODE_EXTERNAL_SPDIF]) {
                    outuint(c_digitalData, spdifSamples[spdifReadIndex++]);
                    outuint(c_digitalData, spdifSamples[spdifReadIndex++]);
                } else {
                    outuint(c_digitalData, 0);
                    outuint(c_digitalData, 0);
                }
                outct(c_digitalData, XS1_CT_PAUSE);

                spdifReadIndex &= MAXSPDIFSAMPLES-1;
                adatReadIndex &= MAXADATSAMPLES-1;
            }
            break;
        }
    }
}


