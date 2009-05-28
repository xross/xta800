#include <xs1.h>
#include <platform.h>

int function() {
    return 0;
}

void test() {
    function();
    function();
}

int main()
{
  par
  {
    on stdcore[0] : test();
    on stdcore[1] : test();
  }
  return 0;
}
