#include <print.h>

#pragma xta noload "printstrln"

int test()
{
  return 0;
}

int main()
{
  printstrln("Hello world");
  return test();
}
