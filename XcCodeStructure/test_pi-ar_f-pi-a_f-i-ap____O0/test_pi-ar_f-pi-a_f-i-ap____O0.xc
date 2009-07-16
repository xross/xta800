#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
    if (( temp^ 5) == (x + 5)) {
    	return x;
    }
  }
  for (int i7 = 0; i7 < temp; i7++) {
    #pragma xta endpoint "endpoint1"
    c <: x;
    if (x == 10 + temp) {
      x = 12 ^ temp;
    }
    for (int i14 = 0; i14 < temp; i14++) {
      if (x == 16 + temp) {
        x = 18 ^ temp;
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
