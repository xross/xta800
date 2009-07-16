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
      if (( temp^ 7) == (x + 7)) {
      	return x;
      }
      break;
    case 5:
      switch (x) {
      case 10:
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 13 ^ temp;
        break;
      case 11:
        x = 16 ^ temp;
        if (( temp^ 17) == (x + 17)) {
        	return x;
        }
        break;
      }
      break;
    }
    break;
  case 3:
    switch (x) {
    case 22:
      x = 24 ^ temp;
      break;
    case 23:
      x = 27 ^ temp;
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
