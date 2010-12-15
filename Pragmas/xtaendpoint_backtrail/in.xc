#include "xs1.h"

int read() {
  timer t;
  unsigned int x, y;

  #pragma xta endpoint "start"
  t :> x;

  #pragma xta endpoint "stop"
  t :> y;
  
  return y - x;
}

int func1() {
  #pragma xta call "read1"
  int x = read();
  
  return x;
}
  
int func2() {
  #pragma xta call "read2"
  int x = read();
  
  return x;
}

int main()
{
  #pragma xta call "func1"
  int x = func1();
  #pragma xta call "func2"
  x += func2();
  #pragma xta call "read3"
  x += read();
}
