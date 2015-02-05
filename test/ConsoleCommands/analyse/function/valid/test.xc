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
    on tile[0] : test0();
    on tile[1] : test1();
    on tile[2] : test2();
    on tile[3] : test3();
  }
  return 0;
}
