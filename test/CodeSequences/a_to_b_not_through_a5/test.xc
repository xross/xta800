#include <xs1.h>

port p = XS1_PORT_1A;

int test()
{
    int x = 0;
    while (1) {
        #pragma xta endpoint "a"
        p <: 1;        
        if (x == 10) {
            break;
        }
        ++x;
    } 
    #pragma xta endpoint "b"
    p <: 1;
    return 0;
}

int main(){
  return test();
}

