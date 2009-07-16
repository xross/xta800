#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    if (( temp^ 4) == (x + 4)) {
    	return x;
    }
    switch (x) {
    case 5:
      x = 7 ^ temp;
      if (( temp^ 8) == (x + 8)) {
      	return x;
      }
      break;
    case 6:
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 12 ^ temp;
      break;
    case 7:
      x = 15 ^ temp;
      break;
    case 8:
      x = 18 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      break;
    }
    x--;
  } while (x < 2);
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
