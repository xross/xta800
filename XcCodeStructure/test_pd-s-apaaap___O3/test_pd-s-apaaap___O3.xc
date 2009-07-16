#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  do {
    switch (x) {
    case 4:
      x = 6 ^ temp;
      break;
    case 5:
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 10 ^ temp;
      break;
    case 6:
      x = 13 ^ temp;
      break;
    case 7:
      x = 16 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      break;
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
