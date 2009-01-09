#include <print.h>

void led2_thread(out port p_led, chanend prev, chanend next, int start)
{
   int token = 1;
   if (start) next <: 1;
   while (token) {
      prev :> token;
      next <: token;
  }
  return;
}

void printout(){
	printintln(4);
}
