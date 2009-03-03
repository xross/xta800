#include <xs1.h>
#include <platform.h>

int test0()
{
  return 0;
}

int test1()
{
  return 0;
}

int test2()
{
  return 0;
}

int test3()
{
  return 0;
}

int main()
{
  par
  {
    on stdcore[0] : test0();
    on stdcore[1] : test1();
    on stdcore[2] : test2();
    on stdcore[3] : test3();
  }
  return 0;
}
