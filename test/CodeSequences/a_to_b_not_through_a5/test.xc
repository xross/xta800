#include <xs1.h>

port p = XS1_PORT_1A;
int x ;
int test()
{
    
    while (1) {
        #pragma xta endpoint "a"
        p <: 1;        
        if (x == 10) {
            break;
        }
        ++x;
    } 
    #pragma xta endpoint "b"
    p :> x;
    return x;
}

int main(){
  return test();
}

