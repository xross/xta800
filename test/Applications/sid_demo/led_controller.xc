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

#include "led_controller.h"

on stdcore[0] : out port p_ledA = XS1_PORT_4A;
on stdcore[0] : out port p_ledB = XS1_PORT_4B;
on stdcore[0] : out port p_ledC = XS1_PORT_4C;
on stdcore[0] : out port p_ledG = XS1_PORT_1E;
on stdcore[0] : out port p_ledR = XS1_PORT_1F;

// The red LEDs are brighter so scale the values to match the green LEDs
#define LED_SCALE_R 96
#define LED_SCALE_G 256

// Map led values to more linear intensity
const char led_intensity_map[] = {
		0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 11,
		11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 15, 15, 15, 16, 16, 17, 17, 17, 18, 18, 19, 19, 19, 20, 20, 21, 21, 21,
		22, 22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 27, 27, 28, 28, 29, 29, 30, 30, 31, 31, 32, 32, 33, 33, 34, 34, 35,
		35, 36, 37, 37, 38, 38, 39, 39, 40, 41, 41, 42, 42, 43, 44, 44, 45, 45, 46, 47, 47, 48, 49, 49, 50, 51, 51, 52,
		53, 53, 54, 55, 56, 56, 57, 58, 59, 59, 60, 61, 62, 62, 63, 64, 65, 66, 66, 67, 68, 69, 70, 71, 71, 72, 73, 74,
		75, 76, 77, 78, 79, 80, 81, 82, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98, 99, 100, 101,
		102, 103, 105, 106, 107, 108, 109, 110, 112, 113, 114, 115, 117, 118, 119, 120, 122, 123, 124, 126, 127, 128,
		130, 131, 132, 134, 135, 136, 138, 139, 141, 142, 144, 145, 146, 148, 149, 151, 152, 154, 156, 157, 159, 160,
		162, 163, 165, 167, 168, 170, 172, 173, 175, 177, 178, 180, 182, 183, 185, 187, 189, 191, 192, 194, 196, 198,
		200, 202, 203, 205, 207, 209, 211, 213, 215, 217, 219, 221, 223, 225, 227, 229, 231, 233, 235
};

//
// Output a complete 8-bit PWM cycle for one color
//
void led_pwm_cycle(chanend c_cmd, timer t_pwm, unsigned int& time, int color, led_values& values, led_values& values_new, unsigned int scale)
{
	const unsigned int period = (XS1_TIMER_HZ / 500) / 256;
	unsigned int led;
	int counter = 0;

	while(counter<256)
	{
		for(int i=0; i<12; i++)
		{
			char dc;
			if(color)
				dc = led_intensity_map[(values.green[i]*scale)>>8];
			else
				dc = led_intensity_map[(values.red[i]*scale)>>8];

			if(dc==0 || counter>dc)
				led &= (0xffff^(1<<i));
			else
				led |= (1<<i);
		}
		
		p_ledR <: color==0;
		p_ledG <: color!=0;

		p_ledA <: led;
		p_ledB <: led>>4;
		p_ledC <: led>>8;
		
		#pragma ordered
		select
		{
			case t_pwm when timerafter(time+period) :> time:
				counter++;
				break;
			case c_cmd :> values_new:
				// Receive any updates to the led values but do not replace the current values.
				// We don't want to change the values during a PWM cycle
				break;
		}
	}

}

//
// Receives 8-bit LED values for the 12 green and 12 red LEDs on the XC-1 and modulates the LEDs using PWM
//
void led_controller(chanend c_cmd)
{
	unsigned int time;
	timer t_pwm;
	led_values leds;
	led_values leds_new;

	for(int i=0; i<12; i++)
	{
		leds.green[i] = 0;
		leds.red[i] = 0;
	}

	t_pwm :> time;
	while(1)
	{
		// Output one PWM cycle for each of the colors
		led_pwm_cycle(c_cmd, t_pwm, time, 0, leds, leds_new, LED_SCALE_R);
		led_pwm_cycle(c_cmd, t_pwm, time, 1, leds, leds_new, LED_SCALE_G);

		// Update values with any updates received during the PWM cycles
		leds = leds_new;
	}
}
