#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  if (( temp^ 3) == (x + 3)) {
  	return x;
  }
  while (x < 4 + temp) {
    #pragma xta endpoint "endpoint1"
    c <: x;
    x = 7 ^ temp;
    while (x < 8 + temp) {
      x = 10 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      x--;
    }
    x--;
  }
  if (( temp^ 14) == (x + 14)) {
  	return x;
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
