#include <xs1.h>
#include <platform.h>

on tile[0] : out port p_led0 = XS1_PORT_1F;
on tile[1] : out port p_led1 = XS1_PORT_1F;
on tile[2] : out port p_led2 = XS1_PORT_1F;
on tile[3] : out port p_led3 = XS1_PORT_1F;

void test(out port p_led);

int main()
{
  par
  {
    on tile[0] : test(p_led0);
    on tile[1] : test(p_led1);
    on tile[2] : test(p_led2);
    on tile[3] : test(p_led3);
  }
  return 0;
}

void test(out port p_led)
{
   p_led <: 0;
   p_led <: 1;
}

