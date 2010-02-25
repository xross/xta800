#include <xs1.h>
#include <platform.h>

out port p = XS1_PORT_1A;

int foo() {
    int x = 0;
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    #pragma xta endpoint "x1"
    p <: x;
  
    x += 10;

    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    #pragma xta endpoint "x2"
    p <: x;
    return x;
}

int main() {
  return foo();
}

