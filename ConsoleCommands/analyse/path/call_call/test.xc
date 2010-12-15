#include <xs1.h>
#include <platform.h>

int function_a() {
    return 0;
}

int function_b() {
    return 1;
}

int test0() {
    #pragma xta call "call_function_a0" 
    function_a();
    #pragma xta call "call_function_b0" 
    function_b();
    return 0;
}

int test1() {
    #pragma xta call "call_function_a1" 
    function_a();
    #pragma xta call "call_function_b1" 
    function_b();
    return 1;
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
