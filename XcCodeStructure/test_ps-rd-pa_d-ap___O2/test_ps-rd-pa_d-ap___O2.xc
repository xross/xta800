#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    if (( temp^ 4) == (x + 4)) {
    	return x;
    }
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 8 ^ temp;
      x--;
    } while (x < 5);
    break;
  case 3:
    do {
      x = 14 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      x--;
    } while (x < 12);
    break;
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
