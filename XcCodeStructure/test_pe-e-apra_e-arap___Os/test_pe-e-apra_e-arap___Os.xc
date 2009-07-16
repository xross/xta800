#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  {
    timer tmr0;
    timer tmr1;
    select {
    case tmr0 :> int tmp0:
      {
        timer tmr0;
        timer tmr1;
        select {
        case tmr0 :> int tmp0:
          x = 6 ^ temp;
          break;
        case tmr1 :> int tmp1:
          #pragma xta endpoint "endpoint1"
          c <: x;
          if (( temp^ 10) == (x + 10)) {
          	return x;
          }
          x = 11 ^ temp;
          break;
        }
      }
      break;
    case tmr1 :> int tmp1:
      {
        timer tmr0;
        timer tmr1;
        select {
        case tmr0 :> int tmp0:
          x = 17 ^ temp;
          if (( temp^ 18) == (x + 18)) {
          	return x;
          }
          break;
        case tmr1 :> int tmp1:
          x = 21 ^ temp;
          #pragma xta endpoint "endpoint2"
          c <: x;
          break;
        }
      }
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
