#include <stdio.h>

#pragma xta noload "puts"

int test()
{
  return 0;
}

int main()
{
  printf("Hello world\n");
  return test();
}
