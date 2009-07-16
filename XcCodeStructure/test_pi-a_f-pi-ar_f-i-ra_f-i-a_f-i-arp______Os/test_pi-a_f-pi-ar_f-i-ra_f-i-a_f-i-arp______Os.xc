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
      if (( temp^ 12) == (x + 12)) {
      	return x;
      }
    }
    for (int i14 = 0; i14 < temp; i14++) {
      if (x == 16 + temp) {
        if (( temp^ 18) == (x + 18)) {
        	return x;
        }
        x = 19 ^ temp;
      }
      for (int i21 = 0; i21 < temp; i21++) {
        if (x == 23 + temp) {
          x = 25 ^ temp;
        }
        for (int i27 = 0; i27 < temp; i27++) {
          if (x == 29 + temp) {
            x = 31 ^ temp;
            if (( temp^ 32) == (x + 32)) {
            	return x;
            }
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
