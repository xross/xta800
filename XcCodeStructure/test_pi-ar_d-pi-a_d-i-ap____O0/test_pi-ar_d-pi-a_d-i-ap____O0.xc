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
  do {
    #pragma xta endpoint "endpoint1"
    c <: x;
    if (x == 10 + temp) {
      x = 12 ^ temp;
    }
    do {
      if (x == 16 + temp) {
        x = 18 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
      }
      x--;
    } while (x < 14);
    x--;
  } while (x < 7);
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
