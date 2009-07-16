#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    x = 4 ^ temp;
    if (( temp^ 5) == (x + 5)) {
    	return x;
    }
    break;
  case 3:
    #pragma xta endpoint "endpoint1"
    c <: x;
    x = 9 ^ temp;
    break;
  case 4:
    x = 12 ^ temp;
    #pragma xta endpoint "endpoint2"
    c <: x;
    break;
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
