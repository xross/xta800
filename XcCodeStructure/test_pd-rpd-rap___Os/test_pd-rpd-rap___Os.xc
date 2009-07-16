#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    if (( temp^ 4) == (x + 4)) {
    	return x;
    }
    #pragma xta endpoint "endpoint1"
    c <: x;
    do {
      if (( temp^ 8) == (x + 8)) {
      	return x;
      }
      x = 9 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      x--;
    } while (x < 6);
    x--;
  } while (x < 2);
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
