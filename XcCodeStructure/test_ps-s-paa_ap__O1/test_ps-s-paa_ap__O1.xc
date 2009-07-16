#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  switch (x) {
  case 2:
    switch (x) {
    case 4:
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 7 ^ temp;
      break;
    case 5:
      x = 10 ^ temp;
      break;
    }
    break;
  case 3:
    x = 14 ^ temp;
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
