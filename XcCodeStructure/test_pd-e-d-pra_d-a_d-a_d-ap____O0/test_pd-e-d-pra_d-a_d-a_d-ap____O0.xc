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
      timer tmr2;
      timer tmr3;
      select {
      case tmr0 :> int tmp0:
        do {
          #pragma xta endpoint "endpoint1"
          c <: x;
          if (( temp^ 9) == (x + 9)) {
          	return x;
          }
          x = 10 ^ temp;
          x--;
        } while (x < 6);
        break;
      case tmr1 :> int tmp1:
        do {
          x = 16 ^ temp;
          x--;
        } while (x < 14);
        break;
      case tmr2 :> int tmp2:
        do {
          x = 22 ^ temp;
          x--;
        } while (x < 20);
        break;
      case tmr3 :> int tmp3:
        do {
          x = 28 ^ temp;
          #pragma xta endpoint "endpoint2"
          c <: x;
          x--;
        } while (x < 26);
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
