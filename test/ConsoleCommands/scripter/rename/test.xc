#include <xs1.h>
#include <platform.h>

out port p = XS1_PORT_1A;
out port q = XS1_PORT_1B;

int foo(){
    int x = 0;
  
    for (int i = 0; i < 10; ++i) {
        x += i;
    }

    #pragma xta endpoint "x"
    p <: x;
    return x;
}

int bar(){
    int x;
    while(1){
        q<:foo();
        q<:foo();
        q<:foo();
        q<:foo();
    }
    #pragma xta label "the_great_outdoors"
    return x;
}

int main()
{
  return bar();
}

