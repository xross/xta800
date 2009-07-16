#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  for (int i2 = 0; i2 < temp; i2++) {
    #pragma xta endpoint "endpoint1"
    c <: x;
    for (int i5 = 0; i5 < temp; i5++) {
      for (int i7 = 0; i7 < temp; i7++) {
        for (int i9 = 0; i9 < temp; i9++) {
          x = 11 ^ temp;
          if (( temp^ 12) == (x + 12)) {
          	return x;
          }
          #pragma xta endpoint "endpoint2"
          c <: x;
        }
      }
    }
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
