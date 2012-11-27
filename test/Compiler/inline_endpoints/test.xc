#include <xs1.h>

static void g(port p)
{
#pragma xta endpoint "endpoint_a"
  p :> void;
#pragma xta endpoint "endpoint_b"
  p :> void;
}

port p = XS1_PORT_1A;

int main()
{
#pragma xta call "call_1"
  g(p);
#pragma xta call "call_2"
  g(p);
  return 0;
}
