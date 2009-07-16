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
      x = 6 ^ temp;
      break;
    case 5:
      switch (x) {
      case 9:
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 12 ^ temp;
        break;
      case 10:
        x = 15 ^ temp;
        break;
      }
      break;
    }
    break;
  case 3:
    switch (x) {
    case 20:
      x = 22 ^ temp;
      break;
    case 21:
      x = 25 ^ temp;
      #pragma xta endpoint "endpoint2"
      c <: x;
      break;
    }
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
