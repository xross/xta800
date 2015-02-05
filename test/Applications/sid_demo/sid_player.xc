// Copyright 2009 Fredrik Petrini
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

#include <xs1.h>
#include <platform.h>

#include "sid_demo.h"
#include "led_controller.h"

on tile[0] : in port p_buttons = XS1_PORT_4D;

unsigned short load_addr;
unsigned short init_addr;
unsigned short play_addr;
unsigned char subsongs;
unsigned char startsong;
unsigned char subsong;
unsigned char speed;
unsigned char sid_count[3];
unsigned int current_tune = 0;
unsigned int current_core = 0;
unsigned int core_count = 3;

// Run emulation and fill the buffer
void fill_buffer(short buffer[], int len)
{
	int newLength = len;
	unsigned int nSamplesToRender = 0;
	unsigned int nSamplesPerCall = 882;  /* This is PAL SID single speed (44100/50Hz) */
	int nSamplesRendered = 0;
	unsigned int nRefreshCIA;

	nSamplesRendered = 0;
	while (nSamplesRendered < newLength)
	{
		if (nSamplesToRender == 0)
		{
			cpuJSR(play_addr, 0);

			/* Find out if cia timing is used and how many samples
			have to be calculated for each cpujsr */
			nRefreshCIA = 0; // (unsigned int)(20000*(memory[0xdc04]|(memory[0xdc05]<<8))/0x4c00);	
			if ((nRefreshCIA==0) || (speed == 0)) 
				nRefreshCIA = 20000;
			nSamplesPerCall = SAMPLE_RATE*nRefreshCIA/1000000;
      
			nSamplesToRender = nSamplesPerCall;
		}

		if (nSamplesRendered + nSamplesToRender > newLength)
		{
			synth_render(buffer[nSamplesRendered], newLength-nSamplesRendered);
			nSamplesToRender -= newLength-nSamplesRendered;
			nSamplesRendered = newLength;
		}
		else
		{
			synth_render(buffer[nSamplesRendered], nSamplesToRender);
			nSamplesRendered += nSamplesToRender;
			nSamplesToRender = 0;
		}
	}	
}

// Get selected tune and setup emulator for playback
unsigned char load_sid_from_core(chanend c, unsigned short tune)
{
    unsigned int data_file_offset;
    unsigned short size;
    unsigned char sid_count;
    char buffer[0x7c+2];

	// Select tune
	c <: (unsigned short)tune;

	// Get size of SID
	c :> size;

	// Get PSID header + first word of data
	for(int i=0; i<0x7c+2; i++)
		c :> buffer[i];

    data_file_offset  = buffer[6]<<8;
    data_file_offset |= buffer[7];

    load_addr  = buffer[8]<<8;
    load_addr |= buffer[9];

    if(load_addr==0)
    {
    	load_addr  = buffer[0x7c];
    	load_addr |= buffer[0x7c+1]<<8;
    }

    init_addr  = buffer[10]<<8;
    init_addr |= buffer[11];

    play_addr  = buffer[12]<<8;
    play_addr |= buffer[13];

    subsongs = buffer[0xf]-1;
    startsong = buffer[0x11]-1;

    speed = buffer[0x15];

	// Get rest of the tune and load into emulator memory
    for(int i=0; i<size-0x7c-2; i++)
    {
    	char data;

    	c :> data;
    	mm_set(load_addr+i, data);
    }

    if (play_addr == 0)
    {
        cpuJSR(init_addr, 0);
        play_addr = (mm_get(0x0315)<<8)+mm_get(0x0314);
    }

    return sid_count;
}

// Select which core to get tune from
void load_sid(chanend c_tunes1, chanend c_tunes2, chanend c_tunes3, int core_number, unsigned short tune)
{
    switch(core_number)
    {
    	case 0:
    		load_sid_from_core(c_tunes1, tune);
    		break;
    	case 1:
    		load_sid_from_core(c_tunes2, tune);
    		break;
    	case 2:
    		load_sid_from_core(c_tunes3, tune);
    		break;
    }

    // Hardcoded fix for incorrect startsong
    if(current_tune==2 && current_core==0)
    	startsong = 1;
}

void sid_player(chanend c_audio, chanend c_led, chanend c_tunes1, chanend c_tunes2, chanend c_tunes3)
{
	short buffer[BUFFER_SIZE];
	unsigned int fftbuffer[32];
	unsigned int buttons_value = 0;
	unsigned int buttons_value_old = 0;
	led_values leds;

	for(int i=0; i<3; i++)
	{
		sid_count[i] = 0;
	}
	
	for(int i=0; i<12; i++)
	{
		leds.green[i] = 0;
		leds.red[i] = 0;
	}

	// Get number of SIDs stored in each core
	c_tunes1 :> sid_count[0];
	c_tunes2 :> sid_count[1];
	c_tunes3 :> sid_count[2];
	
	c64Init(SAMPLE_RATE);
	load_sid(c_tunes1, c_tunes2, c_tunes3, current_core, current_tune);
	subsong = startsong;
	cpuJSR(init_addr, subsong);

	while(1)
	{
		p_buttons :> buttons_value;
		if(buttons_value != buttons_value_old)
		{
			if(buttons_value==0x0b) // Button B, next tune
			{
				current_tune++;
				if(current_tune >= sid_count[current_core])
				{
					current_tune = 0;
					current_core++;
					if(current_core >= core_count)
					{
						current_core = 0;
					}
				}

				c64Init(SAMPLE_RATE);
				load_sid(c_tunes1, c_tunes2, c_tunes3, current_core, current_tune);
				subsong = startsong;
				cpuJSR(init_addr, subsong);
			}
			else if(buttons_value==0x0d) // Button C, previous tune
			{
				if(current_tune == 0)
				{
					if(current_core == 0)
					{
						current_core = core_count-1;
					}
					else
						current_core--;
					
					current_tune = sid_count[current_core]-1;
				}
				else
					current_tune--;

				c64Init(SAMPLE_RATE);
				load_sid(c_tunes1, c_tunes2, c_tunes3, current_core, current_tune);
				subsong = startsong;
				cpuJSR(init_addr, subsong);
			}
			else if(buttons_value==0x0e) // Button A, next subsong
			{
				subsong++;
				if(subsong>=subsongs)
					subsong = 0;

				c64Init(SAMPLE_RATE);
				load_sid(c_tunes1, c_tunes2, c_tunes3, current_core, current_tune);
				cpuJSR(init_addr, subsong);
			}
			else if(buttons_value==0x07) // Button B, restart current subsong
			{
			    if(current_tune==2 && current_core==0)
					cpuJSR(init_addr, subsong);
			    else
			    {
				c64Init(SAMPLE_RATE);
				load_sid(c_tunes1, c_tunes2, c_tunes3, current_core, current_tune);
				cpuJSR(init_addr, subsong);
			    }
			}

			buttons_value_old = buttons_value;
		}

		fill_buffer(buffer, BUFFER_SIZE);

		// Send audio to buffer thread
		for(int i=0; i<BUFFER_SIZE; i++)
		{
			c_audio <: (short)buffer[i];
		}

		// Transform audio buffer into frequency domain
		// I don't think I use it correctly but it looks nice ;)
		ffter(buffer, fftbuffer);
		
		// Display frequency spectrum on LEDs
		for(int i=0; i<12; i++)
		{
			int cg = fftbuffer[i];
			int cr = fftbuffer[i];

			cr -= 110;
			if(cr<0) cr = 0;
			cr *= 2;
			if(cr>255) cr = 255;

			if(cg<0) cg = 0;
			if(cg>127) cg = 127;
			cg *= 2;
			
			cg -= cr;
			if(cg<0) cg = 0;

			leds.green[i] = cg;
			leds.red[i] = cr;
		}

		c_led <: leds;
	}
}
