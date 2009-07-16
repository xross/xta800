#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  }
  for (int i6 = 0; i6 < temp; i6++) {
    #pragma xta endpoint "endpoint1"
    c <: x;
    if (x == 9 + temp) {
      x = 11 ^ temp;
    }
    for (int i13 = 0; i13 < temp; i13++) {
      if (x == 15 + temp) {
        x = 17 ^ temp;
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
