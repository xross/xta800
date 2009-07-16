#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  }
  do {
    if (x == 8 + temp) {
      x = 10 ^ temp;
    }
    do {
      #pragma xta endpoint "endpoint1"
      c <: x;
      if (( temp^ 15) == (x + 15)) {
      	return x;
      }
      if (x == 16 + temp) {
        x = 18 ^ temp;
      }
      do {
        if (x == 22 + temp) {
          x = 24 ^ temp;
        }
        do {
          if (x == 28 + temp) {
            if (( temp^ 30) == (x + 30)) {
            	return x;
            }
            x = 31 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
          x--;
        } while (x < 26);
        x--;
      } while (x < 20);
      x--;
    } while (x < 12);
    x--;
  } while (x < 6);
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
