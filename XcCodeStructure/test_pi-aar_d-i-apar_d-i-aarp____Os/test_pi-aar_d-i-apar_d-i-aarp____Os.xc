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
  do {
    if (x == 12 + temp) {
      x = 14 ^ temp;
      #pragma xta endpoint "endpoint1"
      c <: x;
    } else {
      x = 18 ^ temp;
      if (( temp^ 19) == (x + 19)) {
      	return x;
      }
    }
    do {
      if (x == 23 + temp) {
        x = 25 ^ temp;
      } else {
        x = 28 ^ temp;
        if (( temp^ 29) == (x + 29)) {
        	return x;
        }
        #pragma xta endpoint "endpoint2"
        c <: x;
      }
      x--;
    } while (x < 21);
    x--;
  } while (x < 10);
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
