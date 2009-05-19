#include <platform.h>

int a0()
{
  return 0;
}

int a1()
{
  return 0;
}

int main()
{
  par
  {
    on stdcore[0] : a0();
    on stdcore[1] : a1();
  }
  return 0;
}

