#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  } else {
    x = 7 ^ temp;
    if (( temp^ 8) == (x + 8)) {
    	return x;
    }
  }
  while (x < 10 + temp) {
    if (x == 12 + temp) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 15 ^ temp;
      if (( temp^ 16) == (x + 16)) {
      	return x;
      }
    } else {
      x = 19 ^ temp;
    }
    while (x < 21 + temp) {
      if (x == 23 + temp) {
        if (( temp^ 25) == (x + 25)) {
        	return x;
        }
        x = 26 ^ temp;
      } else {
        x = 29 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
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
