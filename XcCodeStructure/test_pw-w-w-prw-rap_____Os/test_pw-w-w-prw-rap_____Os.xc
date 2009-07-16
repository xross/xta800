#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  while (x < 2 + temp) {
    while (x < 4 + temp) {
      while (x < 6 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        if (( temp^ 9) == (x + 9)) {
        	return x;
        }
        while (x < 10 + temp) {
          if (( temp^ 12) == (x + 12)) {
          	return x;
          }
          x = 13 ^ temp;
          #pragma xta endpoint "endpoint2"
          c <: x;
          x--;
        }
        x--;
      }
      x--;
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
