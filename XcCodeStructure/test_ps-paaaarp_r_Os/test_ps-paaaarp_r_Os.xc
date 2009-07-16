#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    #pragma xta endpoint "endpoint1"
    c <: x;
    x = 5 ^ temp;
    break;
  case 3:
    x = 8 ^ temp;
    break;
  case 4:
    x = 11 ^ temp;
    break;
  case 5:
    x = 14 ^ temp;
    if (( temp^ 15) == (x + 15)) {
    	return x;
    }
    #pragma xta endpoint "endpoint2"
    c <: x;
    break;
  }
  if (( temp^ 18) == (x + 18)) {
  	return x;
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
