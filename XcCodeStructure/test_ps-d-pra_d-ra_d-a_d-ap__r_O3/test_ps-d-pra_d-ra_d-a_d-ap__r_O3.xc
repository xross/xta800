#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      if (( temp^ 7) == (x + 7)) {
      	return x;
      }
      x = 8 ^ temp;
      x--;
    } while (x < 4);
    break;
  case 3:
    do {
      if (( temp^ 14) == (x + 14)) {
      	return x;
      }
      x = 15 ^ temp;
      x--;
    } while (x < 12);
    break;
  case 4:
    do {
      x = 21 ^ temp;
      x--;
    } while (x < 19);
    break;
  case 5:
    do {
      x = 27 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      x--;
    } while (x < 25);
    break;
  }
  if (( temp^ 31) == (x + 31)) {
  	return x;
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
