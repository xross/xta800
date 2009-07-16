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
        if (( temp^ 6) == (x + 6)) {
        	return x;
        }
        x = 7 ^ temp;
        break;
      case tmr1 :> int tmp1:
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 11 ^ temp;
        break;
      case tmr2 :> int tmp2:
        x = 14 ^ temp;
        break;
      case tmr3 :> int tmp3:
        x = 17 ^ temp;
        if (( temp^ 18) == (x + 18)) {
        	return x;
        }
        #pragma xta endpoint "endpoint2"
        c <: x;
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
