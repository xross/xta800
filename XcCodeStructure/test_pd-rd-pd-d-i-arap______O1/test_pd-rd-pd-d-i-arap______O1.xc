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
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      do {
        do {
          if (x == 12 + temp) {
            x = 14 ^ temp;
          } else {
            if (( temp^ 17) == (x + 17)) {
            	return x;
            }
            x = 18 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
          x--;
        } while (x < 10);
        x--;
      } while (x < 8);
      x--;
    } while (x < 5);
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
