#include <xs1.h>
#include <platform.h>

int x;
int y;

int test0(chanend c0, chanend c1) {
    #pragma xta endpoint "output0" 
    c1 <: x;
    #pragma xta endpoint "input0" 
    c0 :> x;
    return x;
}

int test1(chanend c0, chanend c1) {
    #pragma xta endpoint "output1" 
    c1 <: y;
    #pragma xta endpoint "input1" 
    c0 :> y;
    return y;
}

int main()
{
  chan c0;
  chan c1;
  par {
    on stdcore[0] : test0(c0, c1);
    on stdcore[1] : test1(c0, c1);
  }
  return 0;
}
