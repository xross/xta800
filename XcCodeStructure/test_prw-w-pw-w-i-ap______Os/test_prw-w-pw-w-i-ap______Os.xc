#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (( temp^ 2) == (x + 2)) {
  	return x;
  }
  while (x < 3 + temp) {
    while (x < 5 + temp) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      while (x < 8 + temp) {
        while (x < 10 + temp) {
          if (x == 12 + temp) {
            x = 14 ^ temp;
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
