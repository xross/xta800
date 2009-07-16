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
    select {
    case tmr0 :> int tmp0:
      do {
        x = 6 ^ temp;
        if (( temp^ 7) == (x + 7)) {
        	return x;
        }
        x--;
      } while (x < 4);
      break;
    case tmr1 :> int tmp1:
      do {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 14 ^ temp;
        x--;
      } while (x < 11);
      break;
    case tmr2 :> int tmp2:
      do {
        x = 20 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
        x--;
      } while (x < 18);
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
