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
#include "pwm.h"
#include "led_controller.h"

on stdcore[0] : port p_speaker = XS1_PORT_1K;

int main(void)
{
	chan c_sid;
	chan c_pwm;
	chan c_led;
	chan c_tunes1;
	chan c_tunes2;
	chan c_tunes3;
	
	par
	{
		on stdcore[0]: sid_player(c_sid, c_led, c_tunes1, c_tunes2, c_tunes3);
		on stdcore[0]: buffer(c_sid, c_pwm);
		on stdcore[0]: pwm_server(c_pwm, p_speaker, SAMPLE_RATE);
		on stdcore[0]: led_controller(c_led);

		on stdcore[1]: sid_store_core_1(c_tunes1);
		on stdcore[2]: sid_store_core_2(c_tunes2);
		on stdcore[3]: sid_store_core_3(c_tunes3);
	}
	
	return 0;
}
