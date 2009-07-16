#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
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
        x = 7 ^ temp;
        if (( temp^ 8) == (x + 8)) {
        	return x;
        }
        x--;
      } while (x < 4);
      break;
    case tmr1 :> int tmp1:
      do {
        x = 14 ^ temp;
        x--;
      } while (x < 12);
      break;
    case tmr2 :> int tmp2:
      do {
        if (( temp^ 20) == (x + 20)) {
        	return x;
        }
        if (( temp^ 21) == (x + 21)) {
        	return x;
        }
        x = 22 ^ temp;
        x--;
      } while (x < 18);
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
