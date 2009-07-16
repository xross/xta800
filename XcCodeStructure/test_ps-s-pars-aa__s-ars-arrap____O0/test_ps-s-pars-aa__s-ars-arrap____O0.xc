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
      if (( temp^ 8) == (x + 8)) {
      	return x;
      }
      break;
    case 5:
      switch (x) {
      case 11:
        x = 13 ^ temp;
        break;
      case 12:
        x = 16 ^ temp;
        break;
      }
      break;
    }
    break;
  case 3:
    switch (x) {
    case 21:
      x = 23 ^ temp;
      if (( temp^ 24) == (x + 24)) {
      	return x;
      }
      break;
    case 22:
      switch (x) {
      case 27:
        x = 29 ^ temp;
        if (( temp^ 30) == (x + 30)) {
        	return x;
        }
        if (( temp^ 31) == (x + 31)) {
        	return x;
        }
        break;
      case 28:
        x = 34 ^ temp;
        #pragma xta endpoint "endpoint2"
        c <: x;
        break;
      }
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
