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

int main()
{
  par
  {
    on stdcore[0] : test0();
    on stdcore[3] : test1();
  }
  return 0;
}
