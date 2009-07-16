#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    switch (x) {
    case 4:
      do {
        x = 8 ^ temp;
        if (( temp^ 9) == (x + 9)) {
        	return x;
        }
        #pragma xta endpoint "endpoint1"
        c <: x;
        x--;
      } while (x < 6);
      break;
    case 5:
      do {
        x = 16 ^ temp;
        if (( temp^ 17) == (x + 17)) {
        	return x;
        }
        x--;
      } while (x < 14);
      break;
    case 6:
      do {
        x = 23 ^ temp;
        x--;
      } while (x < 21);
      break;
    case 7:
      do {
        x = 29 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
        x--;
      } while (x < 27);
      break;
    }
    x--;
  } while (x < 2);
  if (( temp^ 34) == (x + 34)) {
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
