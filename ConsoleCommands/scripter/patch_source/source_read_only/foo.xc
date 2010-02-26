#include <xs1.h>
#include <platform.h>

out port p = XS1_PORT_1A;

int bar();

int foo(){
    int x = 0;
  
    for (int i = 0; i < 10; ++i) {
        x += i;
    }

    p <: x;
    return x;
}

int main()
{
  return bar();
}
  
