#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  } else {
    x = 7 ^ temp;
    if (( temp^ 8) == (x + 8)) {
    	return x;
    }
  }
  do {
    if (x == 12 + temp) {
      x = 14 ^ temp;
    } else {
      x = 17 ^ temp;
    }
    do {
      if (x == 21 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 24 ^ temp;
      } else {
        x = 27 ^ temp;
      }
      do {
        if (x == 31 + temp) {
          x = 33 ^ temp;
        } else {
          x = 36 ^ temp;
        }
        do {
          if (( temp^ 40) == (x + 40)) {
          	return x;
          }
          if (x == 41 + temp) {
            x = 43 ^ temp;
          } else {
            x = 46 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
          x--;
        } while (x < 38);
        x--;
      } while (x < 29);
      x--;
    } while (x < 19);
    x--;
  } while (x < 10);
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
