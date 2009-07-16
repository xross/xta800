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
    if (x == 11 + temp) {
      x = 13 ^ temp;
    } else {
      x = 16 ^ temp;
    }
    for (int i18 = 0; i18 < temp; i18++) {
      if (x == 20 + temp) {
        #pragma xta endpoint "endpoint1"
        c <: x;
        x = 23 ^ temp;
      } else {
        x = 26 ^ temp;
      }
      for (int i28 = 0; i28 < temp; i28++) {
        if (x == 30 + temp) {
          x = 32 ^ temp;
        } else {
          x = 35 ^ temp;
        }
        for (int i37 = 0; i37 < temp; i37++) {
          if (x == 39 + temp) {
            x = 41 ^ temp;
          } else {
            x = 44 ^ temp;
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
