#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (( temp^ 2) == (x + 2)) {
  	return x;
  }
  switch (x) {
  case 3:
    switch (x) {
    case 5:
      x = 7 ^ temp;
      break;
    case 6:
      #pragma xta endpoint "endpoint1"
      c <: x;
      x = 11 ^ temp;
      break;
    }
    break;
  case 4:
    switch (x) {
    case 15:
      x = 17 ^ temp;
      break;
    case 16:
      x = 20 ^ temp;
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
