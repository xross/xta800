#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
    if (( temp^ 5) == (x + 5)) {
    	return x;
    }
  } else {
    x = 8 ^ temp;
  }
  do {
    if (x == 12 + temp) {
      x = 14 ^ temp;
    } else {
      #pragma xta endpoint "endpoint1"
      c <: x;
      if (( temp^ 18) == (x + 18)) {
      	return x;
      }
      x = 19 ^ temp;
    }
    do {
      if (x == 23 + temp) {
        x = 25 ^ temp;
      } else {
        x = 28 ^ temp;
      }
      do {
        if (x == 32 + temp) {
          x = 34 ^ temp;
        } else {
          x = 37 ^ temp;
          #pragma xta endpoint "endpoint2"
          c <: x;
          if (( temp^ 39) == (x + 39)) {
          	return x;
          }
        }
        x--;
      } while (x < 30);
      x--;
    } while (x < 21);
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
