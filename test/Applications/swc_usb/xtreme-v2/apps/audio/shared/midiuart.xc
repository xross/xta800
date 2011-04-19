#include <xs1.h>
#include <print.h>
#include <platform.h>
#define TRUE 1
#define FALSE 0

// Functions
{unsigned char, int} uart_getch(in port rx);
void uart_putch(out port tx, unsigned char buffer);

int GetParam(int);
int GetX();

// Global state
static unsigned bit_time =  XS1_TIMER_MHZ * 1000000 / (unsigned) 31250;
static unsigned bit_time_2 =  (XS1_TIMER_MHZ * 1000000 / (unsigned) 31250) / 2;

unsigned makeSymbol(unsigned data) {
    // Start and stop bits to the data packet
    return (data << 1) | 0x200;
}

timer t;
timer t2;

extern void mixerEnable(int portNumber, int enable);

void buttonChanged(unsigned x) {
    static int pressedTime;
    int time;

    if (x & 8) { // button released
        t :> time;
        if (time - pressedTime > 10000000) {
        }
    } else {
        t :> pressedTime;
    }
}

void repeater(in port p_midi_in, chanend c_midi_in, out port p_midi_out, chanend c_midi_out) {
    // repeater thread just reports input pin change times and writes symbols out of the output
    unsigned symbol = 0x0;
    unsigned outputting = 0;
    unsigned time;
    unsigned inputPortState, newInputPortState;

    // Receiver
    unsigned rxByte;
    int rxI;
    int rxT;
    int isRX = FALSE;

    t :> time;
    t2 :> rxT;
    p_midi_out <: 1; // Start with high bit.

    p_midi_in :> newInputPortState;
    inputPortState = newInputPortState;

    //mixerEnable(2, 1);

    while(1) 
    {
      
#define WORKAROUND
#ifdef WORKAROUND
// This works around bug #5071
#pragma ordered
#endif
        select 
        {
            // Input to read the start bit
            case !isRX => p_midi_in when pinsneq(inputPortState) :> newInputPortState :
                if((newInputPortState ^ inputPortState) & 1) {
                // Inspect for MIDI bit [0] 
                    if((newInputPortState & 0x1) == 0)
                    {
                        isRX = 1;
                    t2 :> rxT;
                        rxT += (bit_time + bit_time_2); // absorb start bit and set to halfway through the next bit
                        rxI = 0;
                    }
                } else {
                    buttonChanged(newInputPortState);
                }
                inputPortState = newInputPortState;
                break;
            
            // Input to read the remaining bits
            case isRX => t2 when timerafter(rxT) :> int _ :
                if (rxI++ < 8) 
                {
                    unsigned bit;
                    rxByte >>= 1;
                    p_midi_in :> bit;
                    bit <<= 31;
                    rxByte |= bit;
                    rxT += bit_time;
                } 
                else 
                { 
                    unsigned bit;
                    // rcv and check stop bit
                    p_midi_in :> bit;
                    if ((bit & 0x1) == 1)
                    {
                        c_midi_in <: (rxByte >> 24);
                    }
                    isRX = 0;
                }
                break;
        
            // Output
            // If outputting then feed the bits out one at a time until symbol is zero expect pattern like 10'b1dddddddd0
            // Should leave the output high afterwards
            case outputting => t when timerafter(time) :> int _:
                if (symbol == 0) 
                {
                    outputting = 0;
                } 
                else 
                {
                    p_midi_out <: (symbol & 1);
                    symbol >>= 1;
                    time += bit_time;
                }
                break;
            
            case !outputting => c_midi_out :> symbol:
                outputting = 1;
                symbol = makeSymbol(symbol);
                // printstr("Received symbol: ");
                // printhexln(symbol);
                t :> time; // reset the time stamp
                time += bit_time;
                p_midi_out <: (symbol & 1);
                symbol >>= 1;
                break;
        }
    }
}

void uart_getmidi(in port rx, chanend midi) {
    while(1) {
        unsigned char c;
        {c, void} = uart_getch(rx);
        midi <: (unsigned)c;
    }
}

/** UART receive a character
  **/
{unsigned char, int} uart_getch(in port rx)
{
   unsigned data = 0;
   unsigned time;
   int i;
   unsigned char buffer;
   
   // Trigger off low start bit
   rx when pinseq( 0) :> int _ @ time;

   // sample each bit in the middle.
   time += bit_time / 2;
   
   for (i = 0; i < 8; i += 1)
   {
      unsigned bit;
      time += bit_time;

      data >>= 1; // shift existing data right, 0 will be put in high bit
      rx @ time :> bit; // Will go in low bit
      bit <<= 31; // presuming unsigned is 32 bits!
      data |= bit;
   }

   // reshuffle the data.
   buffer = (unsigned char) (data >> 24);

   // Discard stop bit
   time += bit_time;
   rx @ time :> int _;

   return {buffer,0};
}

void uart_putmidi(out port tx, chanend midi) {
   tx <: 1;  
    while (1) {
        unsigned int c = 0;
        midi :> c;
        uart_putch(tx, (unsigned char)c);
    }
}

/** UART transmit a character.
 *  This is blocking call for now.
 */
void uart_putch(out port tx, unsigned char buffer)
{
   unsigned time;
   unsigned data;
   
   //p_kled <: 0x00;
   data = buffer;
   
   // get current time from port with force out.
   tx <: 1 @ time;
   // Start bit.
   tx <: 0;
   // Data bits.
   for (int i = 0; i < 8; i += 1)
   {
      time += bit_time;
      tx @ time <: (data & 0x1); // output a single bit at a time
      data >>= 1;
   }
   // Stop bit
   time += bit_time;
   tx @ time <: 1;
   time += bit_time;
   tx @ time <: 1;
   time += bit_time;
   tx @ time <: 1;
   //p_kled <: 0xf0;
}

