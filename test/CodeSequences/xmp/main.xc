#include <xs1.h>
#include <platform.h>

out port pled[16] = {
  on tile[0] : XS1_PORT_1E,
  on tile[4] : XS1_PORT_1E,
  on tile[8] : XS1_PORT_1E,
  on tile[12] : XS1_PORT_1E,
  on tile[16] : XS1_PORT_1E,
  on tile[20] : XS1_PORT_1E,
  on tile[24] : XS1_PORT_1E,
  on tile[28] : XS1_PORT_1E,
  on tile[32] : XS1_PORT_1E,
  on tile[36] : XS1_PORT_1E,
  on tile[40] : XS1_PORT_1E,
  on tile[44] : XS1_PORT_1E,
  on tile[48] : XS1_PORT_1E,
  on tile[52] : XS1_PORT_1E,
  on tile[56] : XS1_PORT_1E,
  on tile[60] : XS1_PORT_1E
};

void driver(chanend prev, chanend next);
void led_func(out port led, chanend prev, chanend next, int start);

int main()
{
  chan channels[17];
  
  par {
    on tile[0] : driver(channels[16], channels[0]);
  
    par (int i = 0; i < 16; i++)
    {
      on tile[i*4] : led_func(pled[i], channels[i], channels[i+1], i);
    }
  }

  return 0;
}

#define PERIOD 100000000


/*   Core to LED bit mask
 *   15 08 07 00
 *   14 09 06 01
 *   13 10 05 02
 *   12 11 04 03
 */

void driver(chanend prev, chanend next) {
  int i = 0;
  short result = 0;
  short xleds[4];
  unsigned s;
  timer tmr;
	            
  xleds[0] = 0b1001011001101001;   // X
  xleds[1] = 0b1111001001001111;   // M
  xleds[2] = 0b1111010110100111;   // P
	
  while (1) {
    for (i = 0; i < 3; i++) {
      next <: (short)xleds[i];
      prev :> result;
      tmr :> s;
      tmr when timerafter(s + PERIOD) :> s ;
    }
  }
}

void led_func(out port led, chanend prev, chanend next, int id) {
  unsigned s;
  timer tmr;
  short token = 0;
	
  while (1) {
    #pragma xta endpoint "loop"
    prev :> token;
    next <: (short)token;
    if (token & (1 << id)) {
      int i = 0;
      for (i = 0; i < 10; i++) {
        led <: 1;
        tmr :> s;
        tmr when timerafter(s + 5000000) :> s ;
        led <: 0;
        tmr :> s;
        tmr when timerafter(s + 5000000) :> s ;
      }
    } else {
      led <: 0;
    }
  }

  return;
}
