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
  }
  while (x < 9 + temp) {
    if (x == 11 + temp) {
      x = 13 ^ temp;
    } else {
      x = 16 ^ temp;
    }
    while (x < 18 + temp) {
      if (x == 20 + temp) {
        x = 22 ^ temp;
      } else {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 26 ^ temp;
      }
      while (x < 28 + temp) {
        if (x == 30 + temp) {
          x = 32 ^ temp;
        } else {
          x = 35 ^ temp;
        }
        while (x < 37 + temp) {
          if (x == 39 + temp) {
            x = 41 ^ temp;
          } else {
            x = 44 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
          x--;
        }
        x--;
      }
      x--;
    }
    x--;
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
