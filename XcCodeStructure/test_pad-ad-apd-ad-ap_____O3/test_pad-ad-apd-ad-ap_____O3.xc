#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  do {
    x = 5 ^ temp;
    do {
      x = 8 ^ temp;
      #pragma xta endpoint "endpoint1"
      c <: x;
      do {
        x = 12 ^ temp;
        do {
          x = 15 ^ temp;
          #pragma xta endpoint "endpoint2"
          c <: x;
          x--;
        } while (x < 13);
        x--;
      } while (x < 10);
      x--;
    } while (x < 6);
    x--;
  } while (x < 3);
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
