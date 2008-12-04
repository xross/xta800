/**
 * @ModuleName XDK Multicore Tutorial
 * @Date 06/10/2008
 * @Version 2.1
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
**/ 

#include <xs1.h>
#include <platform.h>

on stdcore[0] : out port p_led0 = XS1_PORT_1F;
on stdcore[1] : out port p_led1 = XS1_PORT_1F;
on stdcore[2] : out port p_led2 = XS1_PORT_1F;
on stdcore[3] : out port p_led3 = XS1_PORT_1F;
on stdcore[0] : in port p_button = XS1_PORT_4F;

void led1_thread(out port p_led, chanend prev, chanend next, int start);
void button_listener(in port p_button, chanend prev, chanend next);

int main()
{
  chan c_0to1;
  chan c_1to2;
  chan c_2to3;
  chan c_3to0;
  chan button_chan;

  par
  {
    on stdcore[0] : led1_thread(p_led0, c_0to1, c_1to2, 1);
    on stdcore[1] : led1_thread(p_led1, c_1to2, c_2to3, 0);
    on stdcore[2] : led1_thread(p_led2, c_2to3, c_3to0, 0);
    on stdcore[3] : led1_thread(p_led3, c_3to0, button_chan, 0);
    on stdcore[0] : button_listener(p_button, button_chan, c_0to1);
  }

  return 0;
}

#define PERIOD 10000000

void flashled(out port p_led)
{
   unsigned s;
   timer tmr;
   p_led <: 0;
   tmr :> s;
   tmr when timerafter(s + PERIOD) :> s ;
   p_led <: 1;
   return;
}

void button_listener(in port p_button, chanend prev, chanend next)
{
   int button_press;
   int token = 1;
   while (token) {
      prev:> token;
      p_button :> button_press;
      if (token){
         if( ((~button_press)&0xf) == 0x4 ){
           next <: 0;
         } else {
           next <: 1;
         }
      }
  }
  return;
}

void led1_thread(out port p_led, chanend prev, chanend next, int start)
{
   int token = 1;
   if (start) next <: 1;
   while (token) {
      prev :> token;
      flashled(p_led);
      next <: token;
  }
  return;
}