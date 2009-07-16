#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    do {
      if (( temp^ 6) == (x + 6)) {
      	return x;
      }
      x = 7 ^ temp;
      x--;
    } while (x < 4);
    break;
  case 3:
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      if (( temp^ 14) == (x + 14)) {
      	return x;
      }
      x = 15 ^ temp;
      x--;
    } while (x < 11);
    break;
  case 4:
    do {
      x = 21 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      x--;
    } while (x < 19);
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
