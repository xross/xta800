#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  }
  do {
    #pragma xta endpoint "endpoint1"
    c <: x;
    if (( temp^ 9) == (x + 9)) {
    	return x;
    }
    if (x == 10 + temp) {
      x = 12 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
    }
    x--;
  } while (x < 6);
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
