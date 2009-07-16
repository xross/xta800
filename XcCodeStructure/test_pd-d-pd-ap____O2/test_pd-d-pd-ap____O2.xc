#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      do {
        x = 9 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
        x--;
      } while (x < 7);
      x--;
    } while (x < 4);
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
