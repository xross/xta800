#include "xs1.h"

int read();
int func1();
int func2();

int main()
{
  #pragma xta call "func1"
  int x = func1();
  #pragma xta call "func2"
  x += func2();
  #pragma xta call "read3"
  x += read();
}
