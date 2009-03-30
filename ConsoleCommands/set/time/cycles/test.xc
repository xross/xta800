#include <xs1.h>

in port pIn = XS1_PORT_1A;

int main()
{
  #pragma xta endpoint "inst"
  pIn :> int x;
  return 0;
}
