#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  for (int i3 = 0; i3 < temp; i3++) {
    x = 5 ^ temp;
    for (int i6 = 0; i6 < temp; i6++) {
      x = 8 ^ temp;
      #pragma xta endpoint "endpoint1"
      c <: x;
      for (int i10 = 0; i10 < temp; i10++) {
        x = 12 ^ temp;
        for (int i13 = 0; i13 < temp; i13++) {
          x = 15 ^ temp;
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
