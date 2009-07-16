#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    {
      timer tmr0;
      timer tmr1;
      select {
      case tmr0 :> int tmp0:
        do {
          #pragma xta endpoint "endpoint1"
          c <: x;
          x = 9 ^ temp;
          x--;
        } while (x < 6);
        break;
      case tmr1 :> int tmp1:
        do {
          x = 15 ^ temp;
          if (( temp^ 16) == (x + 16)) {
          	return x;
          }
          #pragma xta endpoint "endpoint2"
          c <: x;
          x--;
        } while (x < 13);
        break;
      }
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
