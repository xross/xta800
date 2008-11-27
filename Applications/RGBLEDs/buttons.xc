/*
 * @Button Sender
 * @Author Sam Garcia / Ross Owen
 * @Date June 2008
 * @Version 1.1
 * @Description Runs on core with buttons on. Allows polling for button state.
 *
 * Copyright XMOS Ltd 2008
 */
 
#include <print.h>
#include <platform.h>
#include "xdk.h"

on stdcore[0] : in port p_button03 = PORT_KEY_0_3;
on stdcore[0] : in port p_button4  = PORT_KEY_4;

// Gets button data
void getButtonData(chanend ch)
{
	unsigned data1, data2, data;
  int done;

	while (1)
	{  
		// Receive state request
		ch :> done;
    if (done)
      break;

		// Read state
		p_button03 :> data1;
		p_button4 :> data2;

		data = ~((data1 & 0xF) | ((data2 & 0x1) << 4));

		// Send back state over channel
    ch <: data;
  	
	}

}
