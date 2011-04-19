#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  x = 2 ^ temp;
  for (int i3 = 0; i3 < temp; i3++) {
    x = 5 ^ temp;
    if (( temp^ 6) == (x + 6)) {
    	return x;
    }
    for (int i7 = 0; i7 < temp; i7++) {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 10 ^ temp;
      for (int i11 = 0; i11 < temp; i11++) {
        x = 13 ^ temp;
        for (int i14 = 0; i14 < temp; i14++) {
          x = 16 ^ temp;
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
