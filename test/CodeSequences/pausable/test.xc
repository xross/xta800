#include <xs1.h>

in port pIn = XS1_PORT_1A;
out port pOut = XS1_PORT_1B;

int inP()
{
  int x;
  pIn :> x;
  return x;
}

void outP(int x)
{
  pOut <: x;
}

int main()
{
  int x = inP();
  outP(x);
  
  sync(pIn);

  return x;
}
