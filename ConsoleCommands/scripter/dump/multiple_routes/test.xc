#include <xs1.h>
#include <platform.h>

out port p = XS1_PORT_1A;

int foo(){
    int x = 0;
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    #pragma xta endpoint "x"
    p <: x;
    return x;
}

int bar(){
    int x;
    while(1){
        x = foo();
        x = foo();
        x = foo();
        x = foo();
    }
    #pragma xta label "the_great_outdoors"
    return x;
}

int main()
{
  return bar();
}

