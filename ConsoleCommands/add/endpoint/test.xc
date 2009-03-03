#include <xs1.h>
#include <platform.h>

on stdcore[0] : in port p0_1 = XS1_PORT_1A;
on stdcore[0] : in port p0_2 = XS1_PORT_1B;
on stdcore[1] : in port p1_1 = XS1_PORT_1C;
on stdcore[1] : in port p1_2 = XS1_PORT_1D;

int test(in port p1, in port p2) {
    int x = 0;
    
    p1 :> x;
    p2 :> x;

    return x;
}

int main()
{
  par
  {
    on stdcore[0] : test(p0_1, p0_2);
    on stdcore[1] : test(p1_1, p1_2);
  }
  return 0;
}
