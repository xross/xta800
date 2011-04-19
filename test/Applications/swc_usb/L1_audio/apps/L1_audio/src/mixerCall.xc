#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "interface.h"
#include "mixer.h"

// Maps a DB value onto a multiplier. First line is 0db, penultimate line is -128 db. Final line is -inf db.
static unsigned dbTable[129] = {
    4294967295, 3827893631, 3411613790, 3040603991, 2709941159, 2415237600,
    2152582777, 1918491420, 1709857277, 1523911903, 1358187913, 1210486251,
    1078847007, 961523407, 856958639, 763765191, 680706443, 606680256,
     540704347, 481903257, 429496729, 382789363, 341161379, 304060399,
     270994116, 241523760, 215258278, 191849142, 170985728, 152391190,
     135818791, 121048625, 107884701, 96152341, 85695864, 76376519,
      68070644, 60668026, 54070435, 48190326, 42949673, 38278936,
      34116138, 30406040, 27099412, 24152376, 21525828, 19184914,
      17098573, 15239119, 13581879, 12104863, 10788470, 9615234,
       8569586, 7637652, 6807064, 6066803, 5407043, 4819033,
       4294967, 3827894, 3411614, 3040604, 2709941, 2415238,
       2152583, 1918491, 1709857, 1523912, 1358188, 1210486,
       1078847, 961523, 856959, 763765, 680706, 606680,
        540704, 481903, 429497, 382789, 341161, 304060,
        270994, 241524, 215258, 191849, 170986, 152391,
        135819, 121049, 107885, 96152, 85696, 76377,
         68071, 60668, 54070, 48190, 42950, 38279,
         34116, 30406, 27099, 24152, 21526, 19185,
         17099, 15239, 13582, 12105, 10788, 9615,
          8570, 7638, 6807, 6067, 5407, 4819,
          4295, 3828, 3412, 3041, 2710, 2415,
          2153, 1918, 0 // -0x8000
}; 

#define MAX 0x7fffffff

// Maps a DB to a multiplier using the above table. It maps a short in the range 0..-32767 (0 .. -0x7fff) to
// DB values 0db .. -127.99 db. -0x8000 is mapped to -inf db (mute). The multiplier value is an unsigned number
// it needs to be shifted right by one bit to make it a signed number; the format is 0.32

static unsigned dbToMultiplier(short db) {
    db = - (db >> 8);  // 0x00 .. 0x80
    if (db >= 0) 
    {
        return dbTable[db];
    }
    return MAX;
}

// multiplies two unsigned multipliers into a signed multiplier (0.32 * 0.32 -> 1.31)
static unsigned longMul(unsigned a, unsigned b) 
{
    unsigned x,y;
    {x, y} = lmul(a, b, 0, 0);
    return x >> 1;
}


/*
 * Mixer control sction, 3 arrays (...Controls) store the values as seen by the host.
 * These are encoded db values (using the Audio-2.0 standard of 0x0000 - -0x7fff for 0..-127.99 db
 * One big array stores the multiplier-values the used by the mixer.
 * Access functions allow to set and get Volume, Mute, and Mixer.
 * The mixer itslef directly accesses the array using shared memory
 */
int mixerMultipliers[18*19];

static int volumeControls[19], muteControls[19], mixerControls[18*18];

extern void setMixerMultiplier(int i, int x);

static void recomputeVolume(int channel) {
    if (channel != 0) {
        int x = longMul(dbToMultiplier(volumeControls[0]), dbToMultiplier(volumeControls[channel])) * !muteControls[0] * !muteControls[channel];
        //setMixerMultiplier((channel - 1)*19 + 18, x);
        return;
    }
    for(int i = 0; i < 18; i++) {
        int x = longMul(dbToMultiplier(volumeControls[0]), dbToMultiplier(volumeControls[i+1])) * !muteControls[0] * !muteControls[i+1];
        //setMixerMultiplier(i*19 + 18, x);
    }
}

static void recomputeMixerControl(int i) {
    int x = dbToMultiplier(mixerControls[i]) >> 1;
    //setMixerMultiplier(i, x);
}


void setVolumeControl(int outChannel, int volume) {
    volumeControls[outChannel] = volume;
    recomputeVolume(outChannel);
}

void setMuteControl(int outChannel, int mute) {
    muteControls[outChannel] = mute;
    recomputeVolume(outChannel);
}

void setMixerControl(int outChannel, int inChannel, int volume) {
    mixerControls[outChannel*19 + inChannel] = volume;
    recomputeMixerControl(outChannel*19 + inChannel);
}

int getVolumeControl(int outChannel) {
    return volumeControls[outChannel];
}

int getMuteControl(int outChannel) {
    return muteControls[outChannel];
}

int getMixerControl(int outChannel, int inChannel) {
    return mixerControls[outChannel*19 + inChannel];
}

#pragma unsafe arrays
void performMix(int i2sIn[8], int clockIn[10], int usbPlay[3], int mixed[], int base) {

    for(int i = 0, offset = base*19+ 38; i < 3; i++, offset -= 19) {
        int h = 0, l = 0;
#pragma loop unroll(8)
        for(int j = 0; j < 8; j++) {
            {h, l} = macs(mixerMultipliers[offset+j], i2sIn[j], h, l);
        }
#pragma loop unroll(10)
        for(int j = 0; j < 10; j++) {
            {h, l} = macs(mixerMultipliers[offset+j+8], clockIn[j], h, l);
        }
        {h, l} = macs(mixerMultipliers[offset+18], usbPlay[i], h, l);
        mixed[i] = h;
    }
    return;
}




extern int usbRecord(int, int, int, int, chanend, int[], int[]);

int channelSpace = 2;
int recordBufferNumber = 0;
int recordPointer = 0;
int recordState = 0;




#define N 1000
void doMix1(chanend c_in, chanend c_out, int base) {
    int clockIn[10], usbPlay[3], i2sIn[8], mixed[3];
    timer t;
    int t0, t1, t2, t3, t4, diff, times[N];
    int cnt = 0, cnt2 = 0;
    int recording = 0;
set_thread_fast_mode_on();
#pragma unsafe arrays
    while(1) {

#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            int x = inuint(c_in);
            outuint(c_out, x);
            i2sIn[i] = x;
        }
    t :> t2;
        if (testct(c_in)) {
            chkct(c_in, 1);
            outct(c_out, 1);
            return;
        }
    t :> t3;
#pragma loop unroll(10)    
        for(int i = 0; i < 10; i++) {
            int x = inuint(c_in);
            outuint(c_out, x);
            clockIn[i] = x;
        }

    t :> t4;
        diff = t4 - t0;
        if (0 && cnt2 > 100000) {
            if (diff > 1000 && base == 3) {
                recording = 1;
            }
            if (recording) {
                recording = 0;
                times[cnt++] = cnt2;
                times[cnt++] = diff;
                times[cnt++] = t4-t1;
                times[cnt++] = t4-t2;
                times[cnt++] = t4-t3;
                if (cnt >= N-5) {
                    for(int i=0; i < N; i+=5) {
                        printint(times[i]);
                        printstr(" ");
                        printint(times[i+1]);
                        printstr(" ");
                        printint(times[i+2]);
                        printstr(" ");
                        printint(times[i+3]);
                        printstr(" ");
                        printintln(times[i+4]);
                    }
                    cnt = 0;
                }
            }
            cnt2++;
        } else {
            cnt2++;
        }
        performMix(i2sIn, clockIn, usbPlay, mixed, base);

        if (base == 3) {
#pragma loop unroll(12)
            for(int i = 0; i < 12; i++) {
                int x = inuint(c_in);
                if (i == 0) {
                    t:> t0;
                }
                outuint(c_out, x);
            }
#pragma loop unroll(3)
            for(int i = 0; i < 3; i++) {
                int x = inuint(c_in);
                outuint(c_out, mixed[i]);
                usbPlay[i] = x;
            }
#pragma loop unroll(3)
            for(int i = 0; i < 3; i++) {
                int x = inuint(c_in);
                outuint(c_out, x);
            }
        } else if (base == 9) {
#pragma loop unroll(6)
            for(int i = 0; i < 6; i++) {
                int x = inuint(c_in);
                if (i == 0) {
                    t:> t0;
                }
                outuint(c_out, x);
            }
#pragma loop unroll(3)
            for(int i = 0; i < 3; i++) {
                int x = inuint(c_in);
                outuint(c_out, mixed[i]);
                usbPlay[i] = x;
            }
#pragma loop unroll(1)
            for(int i = 0; i < 1; i++) {
                int x = inuint(c_in);
                outuint(c_out, x);
            }
        } else if (base == 12) {
#pragma loop unroll(3)
            for(int i = 0; i < 3; i++) {
                int x = inuint(c_in);
                if (i == 0) {
                    t:> t0;
                }
                outuint(c_out, x);
            }
#pragma loop unroll(3)
            for(int i = 0; i < 3; i++) {
                int x = inuint(c_in);
                outuint(c_out, mixed[i]);
                usbPlay[i] = x;
            }
#pragma loop unroll(4)
            for(int i = 0; i < 4; i++) {
                int x = inuint(c_in);
                outuint(c_out, x);
            }
        }
        t :> t1;
    }
}

void doMix0(chanend c_clock_in, chanend c_usbPlayData, chanend c_i2s_in, chanend c_out) {
    int clockIn[10], usbPlay[3], i2sIn[8], mixed[3];
//    int cnt = 0;
//    int sineWave[16] = {0, 5000, 7071, 8500, 10000, 8500, 7071, 5000, 0,-5000, -7071, -8500 -10000, -8500, -7071, -5000};
set_thread_fast_mode_on();

#pragma unsafe arrays
    while(1) {

#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            int x = inuint(c_i2s_in);
            outuint(c_out, x);
            i2sIn[i] = x;
        }
        
        outct(c_clock_in, 1);
        if (testct(c_clock_in)) {
//            chkct(c_clock_in, 1); Done in main call - should be moved here.
            outct(c_out, 1);
            return;
        }

#pragma loop unroll(10)    
        for(int i = 0; i < 10; i++) {
            int x = inuint(c_clock_in);
            outuint(c_out, x);
            clockIn[i] = x;
        }

        performMix(i2sIn, clockIn, usbPlay, mixed, 0);

        outct(c_usbPlayData, 1);
#pragma loop unroll(15)
        for(int i = 0; i < 15; i++) {
            int x = inuint(c_usbPlayData);
//            int x = sineWave[cnt] * 200000;
            outuint(c_out, x);
        }
#pragma loop unroll(3)
        for(int i = 0; i < 3; i++) {
            int x = inuint(c_usbPlayData);
//            int x = sineWave[cnt] * 200000;
            outuint(c_out, mixed[i]);
            usbPlay[i] = x;
        }
//        cnt = (cnt+1) & 15;
    }
}

void doMix2(chanend c_in, chanend c_out, chanend c_i2s_out) {
    int clockIn[10], usbPlay[3], i2sIn[8], mixed[8];
set_thread_fast_mode_on();
#pragma unsafe arrays
    while(1) {

#pragma loop unroll(8)
        for(int i = 7; i >= 0; i--) {
            outuint(c_i2s_out, mixed[i]);
        }
//        outct(c_i2s_out, XS1_CT_PAUSE);

#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            int x = inuint(c_in);
            outuint(c_out, x);
            i2sIn[i] = x;
        }
        
        if (testct(c_in)) {
            chkct(c_in, 1);
            outct(c_out, 1);
            return;
        }

#pragma loop unroll(10)    
        for(int i = 0; i < 10; i++) {
            int x = inuint(c_in);
            outuint(c_out, x);
            clockIn[i] = x;
        }

        performMix(i2sIn, clockIn, usbPlay, mixed, 6);

#pragma loop unroll(9)
        for(int i = 0; i < 9; i++) {
            int x = inuint(c_in);
            outuint(c_out, x);
        }
        usbPlay[2] = inuint(c_in);
        outuint(c_out, mixed[2]);
#pragma loop unroll(2)
        for(int i = 0; i < 2; i++) {
            int x = inuint(c_in);
            usbPlay[i] = x;
        }
#pragma loop unroll(6)
        for(int i = 0; i < 6; i++) {
            int x = inuint(c_in);
            mixed[i+2] = x;
        }
    }
}

void doMix5(chanend c_in, chanend c_adat_out, chanend c_spdif_out, chanend c_usbRecordData) {
    int clockIn[10], usbPlay[3], i2sIn[8], index = 0, mixed[10];
    set_thread_fast_mode_on();
#pragma unsafe arrays
    while(1) {

#pragma loop unroll(8)
        for(int i = 7; i >= 0; i--) {
            outuint(c_adat_out, mixed[i]);
        }
        outct(c_adat_out, XS1_CT_PAUSE);
        outuint(c_spdif_out, mixed[9]);
        outuint(c_spdif_out, mixed[8]);
        outct(c_spdif_out, XS1_CT_PAUSE);

#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            int x = inuint(c_in);
            i2sIn[i] = x;
        }
        
        if (testct(c_in)) {
            chkct(c_in, 1);
            return;
        }

#pragma loop unroll(10)    
        for(int i = 0; i < 10; i++) {
            int x = inuint(c_in);
            clockIn[i] = x;
        }

        performMix(i2sIn, clockIn, usbPlay, mixed, 15);

#pragma loop unroll(3)
        for(int i = 0; i < 3; i++) {
            int x = inuint(c_in);
            usbPlay[i] = x;
        }
#pragma loop unroll(7)
        for(int i = 0; i < 7; i++) {
            mixed[i+3] = inuint(c_in);
        }
       // index = usbRecord(index, 4*18, 6*18, 18, c_usbRecordData, i2sIn, clockIn);
    }
}

/*
 * example mixer.
 * Passes data through form ADAT to I2S and I2S to ADAT.
 */
static void doMix48(chanend c_clock_in, chanend c_usbPlayData, chanend c_usbRecordData,
                    chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out) {
    chan pass0, pass1, pass2, pass3, pass4;
    par {
        doMix0(c_clock_in, c_usbPlayData, c_i2s_in, pass0);
        doMix1(pass0, pass1, 3);
        doMix2(pass1, pass2, c_i2s_out);
        doMix1(pass2, pass3, 9);
        doMix1(pass3, pass4, 12);
        doMix5(pass4, c_adat_out, c_spdif_out, c_usbRecordData);
    }
}

/*
static void doMix48_OLD(chanend c_clock_in, chanend c_usbPlayData, chanend c_usbRecordData,
                    chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out) {
    int clockIn[10], i2sIn[8], usbPlay[10], index = 0;
    outct(c_clock_in, 1);
#pragma unsafe arrays
    while(!testct(c_clock_in)) {
#pragma loop unroll(10)    
        for(int i = 0; i < 10; i++) {
            clockIn[i] = inuint(c_clock_in);
        }
//        chkct(c_clock_in, 1);
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            outuint(c_adat_out, i2sIn[i]);
        }
        outuint(c_spdif_out, clockIn[8] + usbPlay[8]);
        outuint(c_spdif_out, clockIn[9] + usbPlay[9]);
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            outuint(c_i2s_in, clockIn[i]  + usbPlay[i]);
        }
#pragma loop unroll(8)
        outct(c_usbPlayData, 1);
        for(int i = 0; i < 8; i++) {
            i2sIn[i] = inuint(c_i2s_in);
        }

#pragma loop unroll(10)
        for(int i = 0; i < 10; i++) {
            usbPlay[i] = inuint(c_usbPlayData);
        }

        index = usbRecord(index, 40, 60, 10, c_usbRecordData, clockIn);

        outct(c_clock_in, 1);
    }
}*/


/*
 * example mixer.
 * Passes data through form ADAT to I2S and I2S to ADAT.
 */
static void doMix96(chanend c_clock_in, chanend c_usbPlayData, chanend c_usbRecordData,
                    chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out) {
    int clockIn[10], i2sIn[8];
    int signal = 0;
    outct(c_clock_in, 1);
#pragma unsafe arrays
    while(!testct(c_clock_in)) {
        signal += 1<<21;
#pragma loop unroll(6)    
        for(int i = 0; i < 6; i++) {
            clockIn[i] = inuint(c_clock_in);
        }
//        chkct(c_clock_in, 1);
#pragma loop unroll(8)
        for(int i = 0; i < 4; i++) {
            outuint(c_adat_out, i2sIn[i]);
        }
        outuint(c_spdif_out, clockIn[8]);
        outuint(c_spdif_out, clockIn[9]);
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            outuint(c_i2s_in, clockIn[i] + (signal >> 8));
        }
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            i2sIn[i] = inuint(c_i2s_in);
        }
        outct(c_clock_in, 1);
    }
}

/*
 * example mixer.
 * Passes data through form ADAT to I2S and I2S to ADAT.
 */
static void doMix192(chanend c_clock_in, chanend c_usbPlayData, chanend c_usbRecordData,
                     chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out) {
    int clockIn[10], i2sIn[8];
    int signal = 0;
    outct(c_clock_in, 1);
#pragma unsafe arrays
    while(!testct(c_clock_in)) {
        signal += 1<<17;
#pragma loop unroll(4)    
        for(int i = 0; i < 4; i++) {
            clockIn[i] = inuint(c_clock_in);
        }
//        chkct(c_clock_in, 1);
#pragma loop unroll(2)
        for(int i = 0; i < 2; i++) {
            outuint(c_adat_out, i2sIn[i]);
        }
        outuint(c_spdif_out, clockIn[8]);
        outuint(c_spdif_out, clockIn[9]);
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            outuint(c_i2s_in, clockIn[i] + (signal>> 8));
        }
#pragma loop unroll(8)
        for(int i = 0; i < 8; i++) {
            i2sIn[i] = inuint(c_i2s_in);
        }
        outct(c_clock_in, 1);
    }
}


/*
 * The mixer.
 * Has input channels from the clock (ADAT + SPDIF), USB (from USB OUT).
 * Has output channels for ADAT, SPDIF, I2S (bi-directional), and USB buffering (for USB IN)
 * First token from clock carries the frequency.
 * On change of frequency, an end token comes in on the clock channel, which has to be propagated.
 */

void mixer(chanend c_usbPlayData, chanend c_usbRecordData, chanend c_clock_in,
           chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out) {

    for(int i = 0; i < 19*18; i++) {
        mixerMultipliers[i] = 0x7fffffff;
    }
    while (1) {
        int curSampFreq = inuint(c_clock_in);
        outuint(c_adat_out, curSampFreq);
        outuint(c_spdif_out, curSampFreq);
        outuint(c_i2s_out, curSampFreq);

        switch(curSampFreq) {
        case 44100:
        case 48000:
            doMix48(c_clock_in, c_usbPlayData, c_usbRecordData, c_adat_out, c_spdif_out, c_i2s_in, c_i2s_out);
            break;
        case 96000:
            doMix96(c_clock_in, c_usbPlayData, c_usbRecordData, c_adat_out, c_spdif_out, c_i2s_in, c_i2s_out);
            break;
        case 192000:
            doMix192(c_clock_in, c_usbPlayData, c_usbRecordData, c_adat_out, c_spdif_out, c_i2s_in, c_i2s_out);
            break;
        default:
            printstr("Error, deliver cannot run at freq ");
            printintln(curSampFreq);
            break;
        }

        chkct(c_clock_in, 1);  // gobble up end token that signifies new frequency

        outct(c_i2s_out, 1);
        outct(c_adat_out, 1);
        outct(c_spdif_out, 1);
    }
}
