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
  for (int i9 = 0; i9 < temp; i9++) {
    if (( temp^ 11) == (x + 11)) {
    	return x;
    }
    if (x == 12 + temp) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 15 ^ temp;
    } else {
      x = 18 ^ temp;
    }
    for (int i20 = 0; i20 < temp; i20++) {
      if (( temp^ 22) == (x + 22)) {
      	return x;
      }
      if (x == 23 + temp) {
        x = 25 ^ temp;
      } else {
        x = 28 ^ temp;
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
