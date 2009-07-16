#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  while (x < 3 + temp) {
    x = 5 ^ temp;
    if (( temp^ 6) == (x + 6)) {
    	return x;
    }
    while (x < 7 + temp) {
      x = 9 ^ temp;
      #pragma xta endpoint "endpoint1"
      c <: x;
      while (x < 11 + temp) {
        if (( temp^ 13) == (x + 13)) {
        	return x;
        }
        x = 14 ^ temp;
        while (x < 15 + temp) {
          x = 17 ^ temp;
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
