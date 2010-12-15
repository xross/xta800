#include <xs1.h>

port p = XS1_PORT_1A;

int test(int z)
{
    int x = 1;
    int y = 1;
    while (x < z) {
        #pragma xta endpoint "a"
        p <: 1;
        ++x;
    } 
    #pragma xta endpoint "b"
    p <: 1;
    return 0;
}

int main(){
  return test(10);
}
