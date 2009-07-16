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
      x = 4 ^ temp;
      break;
    case tmr1 :> int tmp1:
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 8 ^ temp;
      break;
    case tmr2 :> int tmp2:
      if (( temp^ 11) == (x + 11)) {
      	return x;
      }
      x = 12 ^ temp;
      break;
    case tmr3 :> int tmp3:
      x = 15 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
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
