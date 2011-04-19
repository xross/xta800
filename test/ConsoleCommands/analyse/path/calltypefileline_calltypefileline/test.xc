#include <xs1.h>
#include <platform.h>

int function_a() {
    return 0;
}

int function_b() {
    return 1;
}

int test0() {
    function_a();
    function_b();
    return 0;
}

int test1() {
    function_a();
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
