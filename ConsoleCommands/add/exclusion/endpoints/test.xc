#include <xs1.h>

in port p1a = XS1_PORT_1A;
in port p1b = XS1_PORT_1B;

int x;

int a() {
  #pragma xta endpoint "port_1a"
  p1a :> x;
  return x;
}

int b() {
  #pragma xta endpoint "port_1b"
  p1b :> x;
  return x;
}

int testA() {
  a();
}

int testB() {
  b();
}

int testAandB() {
  a();
  b();
}

int testAorB(int x) {
  if (x)
    a();
  else
    b();
}

int main()
{
  // Prevent functions from being stripped
  testA();
  testB();
  testAandB();
  testAorB(0);
  return 0;
}
