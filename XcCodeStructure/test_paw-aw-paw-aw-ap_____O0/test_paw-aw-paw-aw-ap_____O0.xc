#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  while (x < 3 + temp) {
    x = 5 ^ temp;
    while (x < 6 + temp) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 9 ^ temp;
      while (x < 10 + temp) {
        x = 12 ^ temp;
        while (x < 13 + temp) {
          x = 15 ^ temp;
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
