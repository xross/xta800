#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
    if (( temp^ 5) == (x + 5)) {
    	return x;
    }
  }
  while (x < 7 + temp) {
    if (x == 9 + temp) {
      if (( temp^ 11) == (x + 11)) {
      	return x;
      }
      x = 12 ^ temp;
    }
    while (x < 14 + temp) {
      if (x == 16 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 19 ^ temp;
      }
      while (x < 21 + temp) {
        if (x == 23 + temp) {
          if (( temp^ 25) == (x + 25)) {
          	return x;
          }
          x = 26 ^ temp;
        }
        while (x < 28 + temp) {
          if (x == 30 + temp) {
            x = 32 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
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
