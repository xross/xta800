#include <xs1.h>
#include <platform.h>

int test0(chanend c0, chanend c1) {
    int x = 0;
    
    c1 <: x;
    c0 :> x;

    return x;
}

int test1(chanend c0, chanend c1) {
    int x = 0;
    
    c1 <: x;
    c0 :> x;

    return x;
}

int main()
{
  chan c0;
  chan c1;
  par
  {
    on stdcore[0] : test0(c0, c1);
    on stdcore[1] : test1(c0, c1);
  }
  return 0;
}
