#include <xs1.h>

in port pIn = XS1_PORT_1A;

int main()
{
  sync(pIn);
  return 0;
}
