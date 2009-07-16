#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  for (int i2 = 0; i2 < temp; i2++) {
    for (int i4 = 0; i4 < temp; i4++) {
      if (x == 6 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 9 ^ temp;
      } else {
        x = 12 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
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
