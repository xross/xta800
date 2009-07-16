#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  for (int i2 = 0; i2 < temp; i2++) {
    for (int i4 = 0; i4 < temp; i4++) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      if (( temp^ 7) == (x + 7)) {
      	return x;
      }
      for (int i8 = 0; i8 < temp; i8++) {
        for (int i10 = 0; i10 < temp; i10++) {
          if (( temp^ 12) == (x + 12)) {
          	return x;
          }
          if (x == 13 + temp) {
            x = 15 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
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
