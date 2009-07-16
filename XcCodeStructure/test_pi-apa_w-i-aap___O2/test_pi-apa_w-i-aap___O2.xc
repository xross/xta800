#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  } else {
    #pragma xta endpoint "endpoint1"
    c <: x;
    x = 8 ^ temp;
  }
  while (x < 10 + temp) {
    if (x == 12 + temp) {
      x = 14 ^ temp;
    } else {
      x = 17 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
    }
    x--;
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
