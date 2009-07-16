#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  for (int i3 = 0; i3 < temp; i3++) {
    #pragma xta endpoint "endpoint1"
    c <: x;
    x = 6 ^ temp;
    #pragma xta endpoint "endpoint2"
    c <: x;
  }
}

int main() {
  int returnVal = 0;
  #pragma xta endpoint "start"
  c<: returnVal;
  returnVal = test(1, c);
  #pragma xta endpoint "end"
  c<: returnVal;
  return returnVal;
}
