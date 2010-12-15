#include <xs1.h>
#include <platform.h>

on stdcore[0] : out port p_led0 = XS1_PORT_1F;
on stdcore[1] : out port p_led1 = XS1_PORT_1F;
on stdcore[2] : out port p_led2 = XS1_PORT_1F;
on stdcore[3] : out port p_led3 = XS1_PORT_1F;

void test(out port p_led);

int main()
{
  par
  {
    on stdcore[0] : test(p_led0);
    on stdcore[1] : test(p_led1);
    on stdcore[2] : test(p_led2);
    on stdcore[3] : test(p_led3);
  }
  return 0;
}

void test(out port p_led)
{
   p_led <: 0;
   p_led <: 1;
}

