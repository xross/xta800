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
#include "sid_demo.h"
#include <platform.h>

#define SAMPLE_PERIOD (XS1_TIMER_HZ / SAMPLE_RATE)

void buffer(chanend c_in, chanend c_out)
{
	timer t;
	unsigned int time;
	short buffer[BUFFER_SIZE];
	unsigned read = 0;
	unsigned int write = 0;
	unsigned int count = 0;
	short sample;

	t :> time;
	while(1)
	{
		if(count == BUFFER_SIZE) // Buffer full
		{
			t when timerafter(time) :> int _;
			c_out <: (short)buffer[read++];

			if(read >= BUFFER_SIZE)
				read = 0;
			count--;

			time += SAMPLE_PERIOD;
		}
		else
		{
			select
			{
			case t when timerafter(time) :> int _:
				if(count > 0)
				{
					c_out <: (short)buffer[read++];
	
					if(read >= BUFFER_SIZE)
						read = 0;
					count--;
				}
				time += SAMPLE_PERIOD;
				break;
			case c_in :> sample:
				buffer[write++] = sample;

				if(write >= BUFFER_SIZE)
					write = 0;
				count++;
				break;
			}
		}
	}
}
