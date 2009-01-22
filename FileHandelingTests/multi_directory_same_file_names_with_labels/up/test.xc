#include <print.h>

void led2_thread(out port p_led, chanend prev, chanend next, int start)
{
   int token = 1;
   if (start) next <: 1;
   #pragma xta label "while_label"
   while (token) {
      prev :> token;
      next <: token;
  }
  return;
}

void printout(){
    #pragma xta label "printintln_label"
	printintln(4);
}
