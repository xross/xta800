#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  do {
    x = 5 ^ temp;
    #pragma xta endpoint "endpoint1"
    c <: x;
    if (( temp^ 7) == (x + 7)) {
    	return x;
    }
    do {
      x = 10 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      if (( temp^ 12) == (x + 12)) {
      	return x;
      }
      x--;
    } while (x < 8);
    x--;
  } while (x < 3);
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
