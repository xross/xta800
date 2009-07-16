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
  }
  while (x < 9 + temp) {
    if (x == 11 + temp) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 14 ^ temp;
    } else {
      x = 17 ^ temp;
    }
    while (x < 19 + temp) {
      if (x == 21 + temp) {
        x = 23 ^ temp;
      } else {
        x = 26 ^ temp;
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
