#include <xs1.h>
port c = XS1_PORT_1A;
int temp = 1;
int test(int x, port c) {
  #pragma xta endpoint "endpoint0"
  c <: x;
  if (x == 2 + temp) {
    x = 4 ^ temp;
  } else {
    x = 7 ^ temp;
  }
  for (int i9 = 0; i9 < temp; i9++) {
    if (( temp^ 11) == (x + 11)) {
    	return x;
    }
    if (x == 12 + temp) {
      x = 14 ^ temp;
    } else {
      x = 17 ^ temp;
    }
    for (int i19 = 0; i19 < temp; i19++) {
      if (( temp^ 21) == (x + 21)) {
      	return x;
      }
      if (x == 22 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 25 ^ temp;
      } else {
        x = 28 ^ temp;
      }
      for (int i30 = 0; i30 < temp; i30++) {
        if (x == 32 + temp) {
          x = 34 ^ temp;
        } else {
          x = 37 ^ temp;
        }
        for (int i39 = 0; i39 < temp; i39++) {
          if (( temp^ 41) == (x + 41)) {
          	return x;
          }
          if (x == 42 + temp) {
            x = 44 ^ temp;
          } else {
            x = 47 ^ temp;
            #pragma xta endpoint "endpoint2"
            c <: x;
          }
        }
      }
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
