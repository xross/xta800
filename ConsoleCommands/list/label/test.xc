#include <platform.h>

int a0()
{
  #pragma xta label "l0"
  return 0;
}

int a1()
{
  #pragma xta label "l1"
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

