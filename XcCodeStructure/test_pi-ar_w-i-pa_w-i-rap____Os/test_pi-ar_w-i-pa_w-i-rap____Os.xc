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
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 12 ^ temp;
    }
    while (x < 14 + temp) {
      if (x == 16 + temp) {
        if (( temp^ 18) == (x + 18)) {
        	return x;
        }
        x = 19 ^ temp;
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
