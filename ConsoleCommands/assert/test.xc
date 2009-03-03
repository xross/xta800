#include <xs1.h>
#include <platform.h>

int test0()
{
  int b = 1;
  int a = 2;
  int c = 3;
  return a + b + c;
}

int test1()
{
  int b = 1;
  int a = 2;
  int c = 3;
  return a / b / c;
}

int main()
{
  chan c0;
  chan c1;
  par
  {
    on stdcore[0] : test0();
    on stdcore[1] : test1();
  }
  return 0;
}

